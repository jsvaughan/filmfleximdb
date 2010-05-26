package com.monksvaughan.filmflex;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.commons.lang.WordUtils;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Film {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String title;
	@Persistent
	private Double rating;

	@Persistent
	private String imdbId;

	public Film(String title, Double rating, String imdbId) {
		super();
		this.title = title;
		this.rating = rating;
		this.imdbId = imdbId;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public String html() {
		return "<tr><td>" + getLinkTitle() + "</td><td>" + rating
				+ "</td></tr>";
	}

	String getLinkTitle() {
		if (imdbId == null) {
			return WordUtils.capitalize(getTitle());
		}
		return "<a href=\"http://www.imdb.com/title/" + imdbId + "/\">"
				+ WordUtils.capitalize(getTitle()) + "</a>";
	}

	public String getImdbId() {
		return imdbId;
	}

	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
	}
}
