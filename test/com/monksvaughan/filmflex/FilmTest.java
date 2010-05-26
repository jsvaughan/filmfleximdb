package com.monksvaughan.filmflex;

import org.junit.Assert;
import org.junit.Test;

public class FilmTest {
	@Test
	public void testLinkText() throws Exception {
		Film a = new Film("title", 0.0, null);
		Assert.assertEquals("Title", a.getLinkTitle());
		Film b = new Film("title", 0.0, "1234");
		Assert.assertEquals(
				"<a href=\"http://www.imdb.com/title/1234/\">Title</a>", b
						.getLinkTitle());

	}
}
