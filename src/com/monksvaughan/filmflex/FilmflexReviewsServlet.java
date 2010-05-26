package com.monksvaughan.filmflex;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class FilmflexReviewsServlet extends HttpServlet {
	private final ContentProvider contentProvider;

	@Inject
	public FilmflexReviewsServlet(ContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println(contentProvider.getContent());
	}
}
