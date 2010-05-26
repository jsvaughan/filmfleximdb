package com.monksvaughan.filmflex;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {
	@Test
	public void testGetId() throws Exception {
		Assert
				.assertEquals(
						"tt0133093",
						Utils
								.getId("archResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://www.imdb.com/title/tt0133093/\",\"url\":\"http://www.imdb.com/title/tt0133093/\""));
	}
	
	@Test
	public void testGetRating() throws Exception {
		Assert.assertEquals("4.1", Utils.getRating("<b>4.1/10</b>")); 
	}
}
