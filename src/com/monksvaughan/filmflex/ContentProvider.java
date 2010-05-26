package com.monksvaughan.filmflex;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.google.inject.Singleton;

@Singleton
public class ContentProvider {
	private static final Logger logger = Logger.getLogger(ContentProvider.class
			.getName());

	private final static String CONTENT_KEY = "contentKey";

	Cache cache = null;

	public ContentProvider() {
		try {
			CacheFactory cacheFactory = CacheManager.getInstance()
					.getCacheFactory();
			cache = cacheFactory.createCache(Collections.emptyMap());
		} catch (CacheException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public String getContent() throws IOException {
		if (cache.containsKey(CONTENT_KEY)) {
			return (String) cache.get(CONTENT_KEY);
		}

		String content = createContent();
		cache.put(CONTENT_KEY, content);
		return content;
	}

	public void updateContentInCache() throws IOException {
		cache.put(CONTENT_KEY, createContent());
	}

	@SuppressWarnings("unchecked")
	private String createContent() throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			String queryTxt = "select from " + Film.class.getName()
					+ " ORDER BY rating DESC";
			List<Film> data = (List<Film>) pm.newQuery(queryTxt).execute();
			StringBuilder b = new StringBuilder();
			Iterator<Film> fit = data.iterator();
			while (fit.hasNext()) {
				b.append(fit.next().html());
			}
			return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 "
					+ "Transitional//EN\">\n"
					+ "<HTML>\n"
					+ "<HEAD><TITLE>Virgin Media Filmflex Films Rated By IMDB</TITLE></HEAD>\n"
					+ "<BODY>\n"
					+ getAnalytics()
					+ "<P>Filmflex Films Rated By IMDB (Updated weekly)</P><TABLE>\n"
					+ b.toString() + "</TABLE></HTML>";
		} catch (Exception e) {
			throw new IOException("Coded while drinking, apologies: "
					+ e.getMessage(), e);
		} finally {
			pm.close();
		}
	}

	private String getAnalytics() {
		return "<script type=\"text/javascript\">\n"
				+ "\n"
				+ "  var _gaq = _gaq || [];\n"
				+ "  _gaq.push(['_setAccount', 'UA-54959-8']);\n"
				+ "  _gaq.push(['_trackPageview']);\n"
				+ "\n"
				+ "  (function() {\n"
				+ "    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n"
				+ "    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n"
				+ "    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n"
				+ "  })();\n" + "\n" + "</script>";
	}
}
