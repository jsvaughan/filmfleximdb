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
public class LaunchLoadListingServlet extends HttpServlet {
	private static final Logger logger = Logger
			.getLogger(LaunchLoadListingServlet.class.getName());
	LoadListing job = null;

	@Inject
	public LaunchLoadListingServlet(LoadListing job) {
		this.job = job;
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		logger.info("Launching load listings");
		job.execute();
	}
}
