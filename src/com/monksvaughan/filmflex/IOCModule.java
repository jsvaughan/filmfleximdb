package com.monksvaughan.filmflex;

import com.google.inject.servlet.ServletModule;

public class IOCModule extends ServletModule {
	@Override
	protected void configureServlets() {
		serve("/").with(FilmflexReviewsServlet.class);
		serve("/list").with(LaunchLoadListingServlet.class);
		serve("/rate").with(LaunchLoadRatingServlet.class);
	}
}
