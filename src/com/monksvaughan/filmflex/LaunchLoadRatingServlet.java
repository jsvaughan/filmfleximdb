package com.monksvaughan.filmflex;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class LaunchLoadRatingServlet extends HttpServlet {
	private static final Logger logger = Logger
			.getLogger(LaunchLoadRatingServlet.class.getName());
	LoadRating job = null;

	@Inject
	public LaunchLoadRatingServlet(LoadRating job) {
		this.job = job;
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		logger.info("Launching load ratings");
		job.execute();
	}
}
