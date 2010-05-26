package com.monksvaughan.filmflex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.inject.Inject;

/**
 * Job runs nightly loads listing adds films not already present removes films
 * not found in list
 * 
 */
public class LoadListing {
	private static final Logger logger = Logger.getLogger(LoadListing.class
			.getName());
	private ContentProvider contentProvider = null;

	@Inject
	public LoadListing(ContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	@SuppressWarnings("unchecked")
	public void execute() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Set<String> allFilms = getFilmListFromFF();
			logger.info("Found listing containing " + allFilms.size()
					+ " films");
			// Load all the existing films and if they are not named in all
			// films, delete from db
			String queryTxt = "select from " + Film.class.getName();
			List<Film> storedFilms = (List<Film>) pm.newQuery(queryTxt)
					.execute();
			for (Film storedFilm : storedFilms) {
				if (!allFilms.contains(storedFilm.getTitle())) {
					logger.info("Filmflex no longer showing "
							+ storedFilm.getTitle() + " so deleting.");
					pm.deletePersistent(storedFilm);
				}
			}

			// Insert unrated entries for all films that are not currently
			// present in the db
			for (String title : allFilms) {
				Query query = pm.newQuery(Film.class);
				query.setFilter("title == titleParam");
				query.declareParameters("String titleParam");
				List<Film> results = (List<Film>) query.execute(title);
				if (results.size() == 0) {
					logger.info("Adding newly found film " + title);
					pm.makePersistent(new Film(title, -1.0, null));
				}
			}

			contentProvider.updateContentInCache();
		} catch (Throwable e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			pm.close();
		}
	}

	private Set<String> getFilmListFromFF() throws MalformedURLException,
			IOException {
		URL filmlist = new URL(
				"http://xml.filmflexmovies.com/printing/filmlist.aspx");
		BufferedReader in = new BufferedReader(new InputStreamReader(filmlist
				.openStream()));

		String inputLine;
		boolean careAboutThis = false;
		Set<String> allFilms = new TreeSet<String>();
		while ((inputLine = in.readLine()) != null) {
			if (careAboutThis) {
				String result = inputLine.trim();
				if (result.length() > 0) {
					allFilms.add(result);
				}
			}
			careAboutThis = inputLine.contains("<td class=\"nicecase\"><b>");
		}
		in.close();
		return allFilms;
	}
}
