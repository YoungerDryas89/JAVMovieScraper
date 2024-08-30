package moviescraper.doctord.controller;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.Movie;

public class GenericMovieScraper extends AbstractMovieScraper {

	protected SiteParsingProfile profile;

	public GenericMovieScraper(SiteParsingProfile spp) {
		this.profile = spp;
	}

	@Override
	public Movie createMovie() {
		// Null isn't it here, but this class is never used anyway and will probably get removed
		return new Movie(profile, null);
	}

}
