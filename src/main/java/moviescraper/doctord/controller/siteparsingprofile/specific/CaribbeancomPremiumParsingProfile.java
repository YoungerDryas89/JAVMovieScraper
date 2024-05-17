package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.controller.languagetranslation.Language;
import moviescraper.doctord.controller.languagetranslation.TranslateString;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Director;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.ID;
import moviescraper.doctord.model.dataitem.MPAARating;
import moviescraper.doctord.model.dataitem.OriginalTitle;
import moviescraper.doctord.model.dataitem.Outline;
import moviescraper.doctord.model.dataitem.Plot;
import moviescraper.doctord.model.dataitem.Rating;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Runtime;
import moviescraper.doctord.model.dataitem.Set;
import moviescraper.doctord.model.dataitem.SortTitle;
import moviescraper.doctord.model.dataitem.Studio;
import moviescraper.doctord.model.dataitem.Tagline;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Top250;
import moviescraper.doctord.model.dataitem.Trailer;
import moviescraper.doctord.model.dataitem.Votes;
import moviescraper.doctord.model.dataitem.Year;

public class CaribbeancomPremiumParsingProfile extends SiteParsingProfile implements SpecificProfile {

	// TODO: Implement also getting the japanese translation
	// TODO: Implement scrapeOriginalTitle
	// TODO: Implement getting ratings from japanese page
	// TODO: Get proper studio, thumbnail. The English movie pages are missing attributes such as studio
	private Document japaneseDocument;
	private Thumb[] scrapedPosters;
	private static final SimpleDateFormat caribbeanReleaseDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	private static final Pattern videojsPoster = Pattern.compile("vgsPlayer\\.poster\\('([^']+)'");
	private static final Pattern TRAILER_RE = Pattern.compile("(https:\\\\/\\\\/smovie.caribbeancompr.com\\\\/sample\\\\/movies\\\\/[0-9_]+\\\\/[0-9ip]+.mp4)");
	private static final Pattern DOC_ID_RE = Pattern.compile("moviepages/([0-9_]+)/");

	final String title_path = ".movie-info .section .heading h1";
	final String release_date_path = "#moviepages > div > div.inner-container > div.movie-info > div > ul > li:nth-child(2) > span.spec-content";
	final String actor_path = "#moviepages > div > div.inner-container > div.movie-info > div > ul > li:nth-child(1) > span.spec-content";
	final String genre_path = "#moviepages > div > div.inner-container > div.movie-info > div > ul > li:nth-child(4) > span.spec-content";
	final String duration_path = "#moviepages > div > div.inner-container > div.movie-info > div > ul > li:nth-child(3) > span.spec-content";

	@Override
	public Title scrapeTitle() {
		// html body div#page div#main div#moviepages div.container.page-margin div.inner-container div.movie-info div.section div.heading h1
		Element title_element = document.select(title_path).first();

		if(title_element.text().isEmpty()){
			// Since no title was provided in the html document, it means we must load the japanese one
			System.out.println("English page for movie " + getIdFromUrl() + " is missing critical details; the Japanese document will fill those missing details.");
			initializeJapaneseDocument();
			title_element = japaneseDocument.selectFirst(title_path);
		}
		return new Title(title_element.text());
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		// Carribean.com has no more title
		return new OriginalTitle("");
	}

	@Override
	public SortTitle scrapeSortTitle() {
		return SortTitle.BLANK_SORTTITLE;
	}

	@Override
	public Set scrapeSet() {
		return Set.BLANK_SET;
	}

	@Override
	public Rating scrapeRating() {
		// this site does not have ratings, so just return some default values
		if(japaneseDocument != null){
			var rating_elements = japaneseDocument.select("#userreview_average > span.spec-content.rating");
			if(!rating_elements.isEmpty()){
				return new Rating(5, String.valueOf(rating_elements.first().text().length()));
			}

		}
		return new Rating(0, "0");
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}

	@Override
	public ReleaseDate scrapeReleaseDate() {
		Element date_element = document.select(release_date_path).first();
        assert date_element != null;
        return new ReleaseDate(date_element.text(), caribbeanReleaseDateFormat);
	}

	@Override
	public Top250 scrapeTop250() {
		// This type of info doesn't exist on this site
		return Top250.BLANK_TOP250;
	}

	@Override
	public Votes scrapeVotes() {
		// This type of info doesn't exist on this site
		return Votes.BLANK_VOTES;
	}

	@Override
	public Outline scrapeOutline() {
		// This type of info doesn't exist on this site
		return Outline.BLANK_OUTLINE;
	}

	@Override
	public Plot scrapePlot() {
		if(japaneseDocument == null && scrapingLanguage == Language.ENGLISH) {
			return Plot.BLANK_PLOT;
		} else {
			var plot = japaneseDocument.selectFirst("#moviepages > div > div.inner-container > div.movie-info > div > p").text();
			return new Plot(plot);
		}
	}

	@Override
	public Tagline scrapeTagline() {
		// This type of info doesn't exist on this site
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public Runtime scrapeRuntime() {
		Element duration_element = document.selectFirst(duration_path);
		String[] durationSplitByTimeUnit = duration_element.text().split(":");
		if (durationSplitByTimeUnit.length == 3) {
			int hours = Integer.parseInt(durationSplitByTimeUnit[0]);
			int minutes = Integer.parseInt(durationSplitByTimeUnit[1]);
			// we don't care about seconds

			int totalMinutes = (hours * 60) + minutes;
			return new Runtime(Integer.toString(totalMinutes));
		}
		return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters() {
		String id = getIdFromUrl();
		List<Thumb> posters = new LinkedList<>();
		String[] posterUrls = {
				"/moviepages/"+ id +"/images/l_l.jpg",
				"/moviepages/"+ id +"/images/poster_en.jpg"
		};


		try {
			for(var paths : posterUrls){
				if(fileExistsAtURL("https://www.caribbeancompr.com" + paths)){
					posters.add(new Thumb("https://www.caribbeancompr.com" + paths));
				}
			}
		} catch (MalformedURLException ex) {
			Logger.getLogger(CaribbeancomPremiumParsingProfile.class.getName()).log(Level.SEVERE, null, ex);
		}
		return posters.toArray(new Thumb[posters.size()]);
	}

	@Override
	public Thumb[] scrapeFanart() {
		// TODO: Look at this sometime
		//Believe it or not, the fanart (dvd cover) exists, but is normally only set as the preview of the trailer
		//it follows a predictable URL structure though, so we can grab it anyways :)

		//start by grabbing the ID part of the current page
		String urlOfCurrentPage = document.location();
		if (urlOfCurrentPage != null && urlOfCurrentPage.contains("moviepages")) {
			urlOfCurrentPage = urlOfCurrentPage.replaceFirst(Pattern.quote("http://en.caribbeancompr.com/eng/moviepages/"), "");
			String movieID = urlOfCurrentPage.replaceFirst(Pattern.quote("/index.html"), "");
			if (urlOfCurrentPage.length() > 1) {
				String imageURL = "http://www.caribbeancompr.com/moviepages/" + movieID + "/images/l_l.jpg";
				try {
					Thumb fanartThumbs[] = new Thumb[1];
					Thumb fanartThumb = new Thumb(imageURL);
					//also allow the user to use posters as the fanart
					Thumb[] additionalPosterThumbs;
					fanartThumbs[0] = fanartThumb;
					additionalPosterThumbs = (scrapedPosters == null) ? scrapePosters() : scrapedPosters;
					Thumb[] allCombinedFanart = ArrayUtils.addAll(fanartThumbs, additionalPosterThumbs);
					return allCombinedFanart;
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return new Thumb[0];
				}

			}
		}
		return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		// TODO: Look at this sometime
		String urlOfCurrentPage = document.location();
		if (urlOfCurrentPage != null && urlOfCurrentPage.contains("moviepages")) {
			urlOfCurrentPage = urlOfCurrentPage.replaceFirst(Pattern.quote("http://en.caribbeancompr.com/eng/moviepages/"), "");
			String movieID = urlOfCurrentPage.replaceFirst(Pattern.quote("/index.html"), "");
			if (urlOfCurrentPage.length() > 1) {
				Thumb extraFanartThumbs[] = new Thumb[3];
				for (int i = 1; i < 4; i++) {
					String extraThumbURL = "http://en.caribbeancompr.com/moviepages/" + movieID + "/images/l/00" + i + ".jpg";
					try {
						Thumb extraFanartThumb = new Thumb(extraThumbURL);
						extraFanartThumbs[i - 1] = extraFanartThumb;
					} catch (MalformedURLException e) {
						e.printStackTrace();
						return new Thumb[0];
					}
				}
				return extraFanartThumbs;
			}
		}
		return new Thumb[0];
	}

	@Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {
		return new ID(getIdFromUrl());
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genresReturned = new ArrayList<>();
		var genre_elements = document.selectFirst(genre_path);
		for (Element genreElement : genre_elements.children()) {
			genresReturned.add(new Genre(genreElement.text().trim()));
		}
		return genresReturned;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<>();
		var actor_elements = document.select(actor_path).first();
		for(var actor_element : actor_elements.children()){
			Actor actor = new Actor(actor_element.text(), null, null);
			actorList.add(actor);
		}
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		return new ArrayList<>();
	}

	@Override
	public Trailer scrapeTrailer() {
		int id_index = 5;
		if(scrapingLanguage != Language.ENGLISH){
			id_index = 4;
		}
		return new Trailer("https://smovie.caribbeancompr.com/sample/movies/"+ document.baseUri().split("/")[id_index] +"/480p.mp4");
	}

	@Override
	public Studio scrapeStudio() {
		// Studio attribute is only available on the Japanese version of the site
		if(japaneseDocument != null) {
			return new Studio(japaneseDocument.selectFirst("#moviepages > div > div.inner-container > div.movie-info > div > ul > li:nth-child(3) > span.spec-content > a").text());
		}
		return new Studio("Caribbeancom");
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String fileNameNoExtension = findIDTagFromFile(file, isFirstWordOfFileIsID());
		return fileNameNoExtension.split("-")[1];
	}

	@Override
	public String createSearchStringFromId(String id) {
		return null;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		return new SearchResult[]{ new SearchResult("https://en.caribbeancompr.com/eng/moviepages/" + searchString + "/index.html")};
	}

	private void initializeJapaneseDocument() {
		var japanese_url = "https://www.caribbeancompr.com/moviepages/" + getIdFromUrl() + "/index.html";
		try {
			japaneseDocument = Jsoup.connect(japanese_url).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getParserName() {
		return "Caribbeancom Premium";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new CaribbeancomPremiumParsingProfile();
	}

	public String getIdFromUrl(){
		// TODO: Implement something better than this and more broader
		int id_index = 5;
		if(scrapingLanguage != Language.ENGLISH){
			id_index = 4;
		}
		return document.baseUri().split("/")[id_index];
	}

}
