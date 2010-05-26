package com.monksvaughan.filmflex;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {
	private static final String HTTP_WWW_IMDB_COM_TITLE = "http://www.imdb.com/title/";

	private static final Logger logger = Logger
			.getLogger(Utils.class.getName());

	public static String getId(String inputLine) {
		int i = inputLine.indexOf(HTTP_WWW_IMDB_COM_TITLE)
				+ HTTP_WWW_IMDB_COM_TITLE.length();
		int j = inputLine.indexOf("/", i);
		String result = inputLine.substring(i, j);
		if (result.length() > 25) {
			logger.log(Level.SEVERE, "Issues with imdb parsing, got this: "
					+ result.substring(0, 24));
			return null;
		}
		return result;
	}

	public static String getRating(String ratingLine) {
		int i = ratingLine.indexOf("<b>") + 3;
		int j = ratingLine.indexOf("/10</b>", i);
		return ratingLine.substring(i, j);
	}
}
