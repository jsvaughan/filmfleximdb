package com.monksvaughan.filmflex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.commons.lang.math.RandomUtils;

import com.google.inject.Inject;

/**
 * Job Runs regularly (but not so often that it is throttled by google Finds the
 * first film in the db that is missing a rating, gets the rating and updates it
 */
public class LoadRating {
	private static final Logger logger = Logger.getLogger(LoadRating.class
			.getName());

	private ContentProvider contentProvider = null;

	@Inject
	public LoadRating(ContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	@SuppressWarnings("unchecked")
	public void execute() {
		// Films that need to be processed have a rating of -1.0
		// Find the first one and process it
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery(Film.class);
			query.setFilter("rating == ratingParam");
			query.declareParameters("Double ratingParam");
			List<Film> results = (List<Film>) query.execute(new Double(-1.0));
			if (results.size() > 0) {
				Film film = results.get(0);
				logger.info("Finding rating for: " + film.getTitle());
				String imdbIdForFilm = getImdbIdForFilm(film.getTitle());
				film.setImdbId(imdbIdForFilm);
				String imdbRatingForId = getImdbRatingForId(imdbIdForFilm);
				if (imdbRatingForId == null) {
					logger.info("Cant get a rating for: " + film.getTitle());
					film.setRating(new Double(-997.0));
				} else {
					film.setRating(new Double(imdbRatingForId));
				}
			} else {
				// Pick a random film to update
				// Prefer films that have no filmflex id
				String queryTxt = "select from " + Film.class.getName();
				List<Film> existingFilms = (List<Film>) pm.newQuery(queryTxt)
						.execute();

				Film picked = null;
				for (Film f : existingFilms) {
					if (f.getImdbId() == null) {
						picked = f;
						break;
					}
				}

				if (picked == null) {
					// Then update a random one
					int ix = RandomUtils.nextInt(existingFilms.size());
					picked = existingFilms.get(ix);
				}

				logger.info("Updating rating for: " + picked.getTitle());
				String imdbIdForFilm = getImdbIdForFilm(picked.getTitle());
				if (picked.getImdbId() == null) {
					picked.setImdbId(imdbIdForFilm);
				}
				String imdbRatingForId = getImdbRatingForId(imdbIdForFilm);
				if (imdbRatingForId == null) {
					logger.info("Cant get an updated rating for: "
							+ picked.getTitle());
				} else {
					picked.setRating(new Double(imdbRatingForId));
				}
			}
			contentProvider.updateContentInCache();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			pm.close();
		}
	}

	private String getImdbRatingForId(String id) {
		String result = null;
		if (id == null) {
			return "-999.0";
		}
		String url = "http://www.imdb.com/title/" + id + "/";
		try {
			URL filmdata = new URL(url);
			URLConnection conn = filmdata.openConnection();
			conn
					.setRequestProperty(
							"User-agent",
							"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.0 (KHTML, like Gecko) Chrome/6.0.408.1 Safari/534.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			String inputLine = null;
			boolean nextLineIsRating = false;
			while ((!nextLineIsRating) && ((inputLine = in.readLine()) != null)) {
				nextLineIsRating = inputLine.contains("starbar-meta");
			}
			if (nextLineIsRating) {
				inputLine = in.readLine();
				if (inputLine != null) {
					result = Utils.getRating(inputLine);
				}
			}
			in.close();
		} catch (Throwable t) {
			System.out.println("Can't fetch: " + id);
			result = "-998.0";
		}
		return result;
	}

	private String getImdbIdForFilm(String filmName) throws IOException {
		String url = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&&q=site:imdb.com+";
		url += URLEncoder.encode(filmName, "UTF-8");
		url += "&key=ABQIAAAAjXg1Laq9ly9D0zFyu2SrahRaqlgjJj-YXJhojB3E4YJ9dzrK-BTPjanoOck0RrAOcWNFVBnCVpXHSA";
		URL filmlist = new URL(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(filmlist
				.openStream()));
		String inputLine = null;
		boolean foundSomething = false;
		while ((!foundSomething) && ((inputLine = in.readLine()) != null)) {
			foundSomething = inputLine.contains("http://www.imdb.com/title/");
		}
		in.close();
		if (!foundSomething) {
			return null;
		}
		try {
			return Utils.getId(inputLine);
		} catch (Throwable e) {
			logger.log(Level.INFO, "Can't get id for film: " + filmName);
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
}
