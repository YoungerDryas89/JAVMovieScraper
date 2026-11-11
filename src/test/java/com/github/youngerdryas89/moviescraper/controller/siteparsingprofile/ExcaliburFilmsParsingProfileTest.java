package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile;

import java.io.File;
import java.io.IOException;

import com.github.youngerdryas89.moviescraper.model.movie.MovieFactory;
import org.junit.BeforeClass;

import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.ExcaliburFilmsParsingProfile;

public class ExcaliburFilmsParsingProfileTest extends GenericParsingProfileTest {

	@BeforeClass
	public static void initialize() {
		System.out.println("Testing Excalibur Films Parsing Profile");
		overloadedScraper = new ExcaliburFilmsParsingProfile();
		expectedValueFile = new File("C:/Temp/Pirates 2/");

		try {
			actualMovie = MovieFactory.scrapeMovie(expectedValueFile, overloadedScraper, "", false, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		expectedMovie = GenericParsingProfileTest.createMovieFromFileName("ExcaliburFilmsSiteParsingProfileTestMovie.nfo");
	}

}
