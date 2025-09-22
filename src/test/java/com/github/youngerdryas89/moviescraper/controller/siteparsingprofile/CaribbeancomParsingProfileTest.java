package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language;

import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.CaribbeancomParsingProfile;
import com.github.youngerdryas89.moviescraper.model.SearchResult;
import com.github.youngerdryas89.moviescraper.model.dataitem.Actor;
import com.github.youngerdryas89.moviescraper.model.dataitem.Genre;
import com.github.youngerdryas89.moviescraper.model.dataitem.ID;
import com.github.youngerdryas89.moviescraper.model.dataitem.OriginalTitle;
import com.github.youngerdryas89.moviescraper.model.dataitem.Plot;
import com.github.youngerdryas89.moviescraper.model.dataitem.Rating;
import com.github.youngerdryas89.moviescraper.model.dataitem.ReleaseDate;
import com.github.youngerdryas89.moviescraper.model.dataitem.Thumb;
import com.github.youngerdryas89.moviescraper.model.dataitem.Title;
import com.github.youngerdryas89.moviescraper.model.dataitem.Trailer;
import com.github.youngerdryas89.moviescraper.model.dataitem.Year;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

public class CaribbeancomParsingProfileTest {
	static File file = new File("C:/Temp/Caribbeancom 070514-637 abc.avi");
	static CaribbeancomParsingProfile parser = new CaribbeancomParsingProfile();

	@BeforeClass
	public static void initialize() {
		parser = new CaribbeancomParsingProfile();
		parser.setScrapingLanguage(Language.ENGLISH);
		String searchString = parser.createSearchString(file);
		try {
			SearchResult[] searchResults = parser.getSearchResults(searchString);
			var document = SiteParsingProfile.downloadDocumentFromURLString(searchResults[0].getUrlPath());
			parser.setDocument(document);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("static-access")
	@Test
	public void testFindID() {
		String findIDTagFromFile = parser.findIDTagFromFile(file);
		assertEquals(findIDTagFromFile, "070514-637");
	}

	@Test
	public void testScrapeTitle() {
		Title title = parser.scrapeTitle();
		assertEquals("Wrong title", "Ruka Ichinose", title.getTitle());
	}

	@Test
	public void testScrapeOriginalTitle() {
		OriginalTitle originalTitle = parser.scrapeOriginalTitle();
		assertEquals("Wrong original title", OriginalTitle.BLANK_ORIGINALTITLE, originalTitle);
	}

	@Test
	public void testScrapeRating() {
		Rating rating = parser.scrapeRating();
		assertEquals("Wrong rating", Rating.BLANK_RATING, rating);
	}

	@Test
	public void testScrapeYear() {
		Year year = parser.scrapeYear();
		assertEquals("Wrong year", "2014", year.getYear());
	}

	@Test
	public void testScrapeReleaseDate() {
		ReleaseDate releaseDate = parser.scrapeReleaseDate();
		assertEquals("Wrong release date", "2014-01-05", releaseDate.getReleaseDate());
	}

	@Test
	public void testScrapeRuntime() {
		com.github.youngerdryas89.moviescraper.model.dataitem.Runtime movieRuntime = parser.scrapeRuntime();
		assertEquals("Wrong runtime", "80", movieRuntime.getRuntime());
	}

	@SuppressWarnings("static-access")
	@Test
	public void testScrapeID() {
		ID id = parser.scrapeID();
		assertEquals("Wrong ID", "070514-637", id.getId());
		assertEquals("ID did not match from filename", parser.findIDTagFromFile(file), id.getId());
	}

	@Test
	public void testScrapeActors() {
		ArrayList<Actor> actorList = parser.scrapeActors();
		assertEquals("Wrong actor name", "Ruka Ichinose", actorList.get(0).getName());
	}

	@Test
	public void testScrapeGenre() {
		ArrayList<Genre> genreList = parser.scrapeGenres();
		assertEquals("Wrong genre", "creampie", genreList.get(0).getGenre());
	}

	@Test
	public void testTrailer() {
		Trailer trailer = parser.scrapeTrailer();
		assertEquals("Wrong trailer", "http://smovie.caribbeancom.com/sample/movies/070514-637/sample_m.mp4", trailer.getTrailer());
	}

	@Test
	public void testScrapePoster() {
		Thumb[] posters = parser.scrapePosters(false);
		assertEquals("Poster size not right", true, posters.length > 0);
		assertEquals("Wrong poster url", "https://en.caribbeancom.com/moviepages/070514-637/images/l.jpg", posters[0].getThumbURL().toString());
	}

	@Test
	public void testScrapeFanarts() {
		Thumb[] posters = parser.scrapeFanart();
		assertEquals("Poster size not right", true, posters.length == 5);
		assertEquals("Wrong fanart url", "https://en.caribbeancom.com/moviepages/070514-637/images/l/001.jpg", posters[0].getThumbURL().toString());
	}
}
