package moviescraper.doctord.controller.amalgamation.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import moviescraper.doctord.controller.amalgamation.DataItemSourceAmalgamationPreference;
import moviescraper.doctord.controller.amalgamation.MovieScrapeResultGroup;
import moviescraper.doctord.controller.amalgamation.ScraperGroupAmalgamationPreference;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile.ScraperGroupName;
import moviescraper.doctord.controller.siteparsingprofile.specific.DmmParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.JavLibraryParsingProfile;
import moviescraper.doctord.model.Movie;

public class TestAmalgamation {

	static Movie dmmSourcedMovie;
	static Movie javLibrarySourcedMovie;
	static Movie amalgamatedMovie;

	@BeforeClass
	public static void initialize() throws URISyntaxException, NoSuchFieldException, SecurityException {
		System.out.println("Testing amalgamation");
		var overallOrdering = DataItemSourceAmalgamationPreference.createPreferenceOrdering(new JavLibraryParsingProfile(), new DmmParsingProfile());
		var actorOdering = DataItemSourceAmalgamationPreference.createPreferenceOrdering(new DmmParsingProfile(), new JavLibraryParsingProfile());
		var posterOrdering = DataItemSourceAmalgamationPreference.createPreferenceOrdering(new DmmParsingProfile(), new JavLibraryParsingProfile());
		var titleOrdering = DataItemSourceAmalgamationPreference.createPreferenceOrdering(new JavLibraryParsingProfile(), new DmmParsingProfile());

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
