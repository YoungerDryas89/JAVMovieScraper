package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.Kin8tengokuParsingProfile;
import com.github.youngerdryas89.moviescraper.model.dataitem.Actor;
import com.github.youngerdryas89.moviescraper.model.dataitem.Genre;
import com.github.youngerdryas89.moviescraper.model.dataitem.ReleaseDate;
import com.github.youngerdryas89.moviescraper.model.dataitem.Runtime;
import com.github.youngerdryas89.moviescraper.model.dataitem.Thumb;
import com.github.youngerdryas89.moviescraper.model.dataitem.Year;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

public class Kin8tengokuParsingProfileTest {

	static File file = new File("C:/Temp/kin8tengoku-1000.wmv");
	static Kin8tengokuParsingProfile profile;

	@BeforeClass
	public static void initialize() {
		profile = new Kin8tengokuParsingProfile();
		String url = profile.createSearchString(file);
		Document document = SiteParsingProfile.downloadDocumentFromURLString(url);
		profile.setDocument(document);
	}

	@Test
	public void testSearchString() {
		String searchString = profile.createSearchString(file);
		assertEquals("Wrong searchString.", "https://en.kin8tengoku.com/1000/pht/shosai.htm", searchString);
	}

	@Test
	public void testTitle(){
		var title = profile.scrapeTitle();
		assertEquals("Expected: Beautiful Gina Gerson VOL1 / Gina Gerson", title.getTitle(), "Beautiful Gina Gerson VOL1 / Gina Gerson");
	}

	@Test
	public void findID() {
		String id = Kin8tengokuParsingProfile.findID(file.getName());
		assertEquals("Found Wrong ID", "1000", id);
	}

	@Test
	public void testRuntime() {
		Runtime runtime = profile.scrapeRuntime();
		assertEquals("Wrong Runtime", "26", runtime.getRuntime());
	}

	@Test
	public void testYear() {
		Year year = profile.scrapeYear();
		assertEquals("Wrong Year", "2014", year.getYear());
	}

	@Test
	public void testReleaseDate() {
		ReleaseDate scrapeReleaseDate = profile.scrapeReleaseDate();
		assertEquals("Found wrong releaseDate", "2014-03-05", scrapeReleaseDate.getReleaseDate());
	}

	@Test
	public void testActor() {
		ArrayList<Actor> actors = profile.scrapeActors();
		assertTrue("There should be 1 Actor.", actors.size() == 1);
		assertEquals("Wrong Actor.", "Gina Gerson", actors.get(0).getName());
	}

	@Test
	public void testGenre() {
		ArrayList<Genre> genres = profile.scrapeGenres();
		assertTrue("There should be 9 genres.", genres.size() == 11);

		assertEquals("First Genre not found", true, genres.contains(new Genre("Costume Play")));
		assertEquals("Second Genre not found", true, genres.contains(new Genre("Shower Room")));
	}

	@Test
	public void testFanart() throws IOException {
		Thumb[] fanart = profile.scrapeFanart();
		assertTrue("There should be 1 Fanart.", fanart.length == 1);

		//TestingHelper.showImage("Fanart", fanart[0].getThumbImage());
	}

}
