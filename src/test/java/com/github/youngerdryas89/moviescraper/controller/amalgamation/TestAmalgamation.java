package com.github.youngerdryas89.moviescraper.controller.amalgamation;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.youngerdryas89.moviescraper.controller.amalgamation.DataItemSourceAmalgamationPreference;
import com.github.youngerdryas89.moviescraper.controller.amalgamation.MovieScrapeResultGroup;
import com.github.youngerdryas89.moviescraper.controller.amalgamation.ScraperGroupAmalgamationPreference;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfile.ScraperGroupName;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.DmmParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.JavLibraryParsingProfile;
import com.github.youngerdryas89.moviescraper.model.Movie;

public class TestAmalgamation {

	static Movie dmmSourcedMovie;
	static Movie javLibrarySourcedMovie;
	static Movie amalgamatedMovie;

	@BeforeClass
	public static void initialize() throws URISyntaxException, NoSuchFieldException, SecurityException {
		System.out.println("Testing amalgamation");
		DataItemSourceAmalgamationPreference overallOrdering = new DataItemSourceAmalgamationPreference(new JavLibraryParsingProfile(), new DmmParsingProfile());
		DataItemSourceAmalgamationPreference actorOdering = new DataItemSourceAmalgamationPreference(new DmmParsingProfile(), new JavLibraryParsingProfile());
		DataItemSourceAmalgamationPreference posterOrdering = new DataItemSourceAmalgamationPreference(new DmmParsingProfile(), new JavLibraryParsingProfile());
		DataItemSourceAmalgamationPreference titleOrdering = new DataItemSourceAmalgamationPreference(new JavLibraryParsingProfile(), new DmmParsingProfile());

		ScraperGroupAmalgamationPreference orderingPreference = new ScraperGroupAmalgamationPreference(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP, overallOrdering);

		//test the field based version of this method
		orderingPreference.setCustomOrderingForField(Movie.class.getDeclaredField("actors"), actorOdering);
		orderingPreference.setCustomOrderingForField(Movie.class.getDeclaredField("posters"), posterOrdering);
		//also test the one where you just pass in the name of the field by a string value
		orderingPreference.setCustomOrderingForField("title", titleOrdering);

		try {

			URI movieOneURI =  TestAmalgamation.class.getResource("/testdata/Movie1.nfo").toURI();
			URI movieTwoURI = TestAmalgamation.class.getResource("/testdata/Movie2.nfo").toURI();
			URI movieThreeURI = TestAmalgamation.class.getResource("/testdata/Movie3.nfo").toURI();

			//3rd, except actors and posters will be first

			System.out.println(movieOneURI);
			dmmSourcedMovie = Movie.createMovieFromNfo(new File(movieOneURI));
			dmmSourcedMovie.getTitle().setDataItemSource(new DmmParsingProfile());
			dmmSourcedMovie.getActors().get(0).setDataItemSource(new DmmParsingProfile());
			dmmSourcedMovie.getPosters()[0].setDataItemSource(new DmmParsingProfile());


			//2nd, except Title which should be first
			javLibrarySourcedMovie = Movie.createMovieFromNfo(new File(movieThreeURI));
			javLibrarySourcedMovie.getTitle().setDataItemSource(new JavLibraryParsingProfile());
			javLibrarySourcedMovie.getActors().get(0).setDataItemSource(new JavLibraryParsingProfile());
			javLibrarySourcedMovie.getPosters()[0].setDataItemSource(new JavLibraryParsingProfile());

			List<Movie> movieList = Arrays.asList(dmmSourcedMovie, javLibrarySourcedMovie);
			MovieScrapeResultGroup movieScrapeResultGroup = new MovieScrapeResultGroup(movieList, orderingPreference);
			amalgamatedMovie = movieScrapeResultGroup.amalgamateMovie();
			System.out.println("amalgamated movie is " + amalgamatedMovie);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testAmalgamatedTitleCustomDataItemSort() {
		assertEquals("Wrong amalgamated title using a custom data item sort", amalgamatedMovie.getTitle(), javLibrarySourcedMovie.getTitle());
	}

	@Test
	public void testAmalgamatedPosterCustomDataItemSort() {
		assertEquals("Wrong amalgamated posters using a custom data item sort", amalgamatedMovie.getPosters()[0], dmmSourcedMovie.getPosters()[0]);
	}

	@Test
	public void testActorsCustomDataItemSort() {
		assertEquals("Wrong amalgamated actors using a custom data item sort", amalgamatedMovie.getActors(), dmmSourcedMovie.getActors());
	}

	@Test
	public void testPlotGlobalSort() {
		// FIXME: Replaced r18.com sourced movie with one from jav library, dunno if it will work
		assertEquals("Wrong plot using a standard amalgamation", amalgamatedMovie.getPlot(), javLibrarySourcedMovie.getPlot());
	}

}
