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

	private Document japaneseDocument;
	private Thumb[] scrapedPosters;
	private static final SimpleDateFormat caribbeanReleaseDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	private static final Pattern videojsPoster = Pattern.compile("vgsPlayer\\.poster\\('([^']+)'");
	private static final Pattern TRAILER_RE = Pattern.compile("(https:\\\\/\\\\/smovie.caribbeancompr.com\\\\/sample\\\\/movies\\\\/[0-9_]+\\\\/[0-9ip]+.mp4)");
	private static final Pattern DOC_ID_RE = Pattern.compile("moviepages/([0-9_]+)/");

	final String title_path = ".movie-info .section .heading h1";
	final String release_date_path = "#moviepages > div > div.inner-container > div.movie-info > div > ul > li:nth-child(2) > span.spec-content";
	final String actor_path = "html body div#page div#main div#moviepages div.container.page-margin div.inner-container div.movie-info div.section ul li.movie-spec span.spec-content";
	final String genre_path = actor_path;
	final String duration_path = "#moviepages > div > div.inner-container > div.movie-info > div > ul > li:nth-child(3) > span.spec-content";
	//#moviepages > div > div.inner-container > div.movie-info > div > ul > li:nth-child(3) > span.spec-content

	@Override
	public Title scrapeTitle() {
		// html body div#page div#main div#moviepages div.container.page-margin div.inner-container div.movie-info div.section div.heading h1
		var title_element = document.select(title_path).first();
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
		if(scrapingLanguage == Language.ENGLISH) {
			return Plot.BLANK_PLOT;
		} else {
			// TODO: Japanese plot
			return Plot.BLANK_PLOT;
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
		return new ID("");
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genresReturned = new ArrayList<>();
		var genre_elements = document.select(genre_path).last();
		for (Element genreElement : genre_elements.children()) {
			genresReturned.add(new Genre(genreElement.text().trim()));
		}
		return genresReturned;
	}

	private String convertGenreCodeToDescription(String currentGenreCode) {
		switch (currentGenreCode) {
			case "1_1":
				return "Pornstar";
			case "2_1":
				return "School Girls";
			case "3_1":
				return "Amateur";
			case "4_1":
				return "Sister";
			case "5_1":
				return "Lolita";
			case "6_1":
				return "MILF / Housewife";
			case "8_1":
				return "Slut";
			case "9_1":
				return "Big Tits";
			case "10_1":
				return "Gonzo";
			case "11_1":
				return "Creampie";
			case "12_1":
				return "Squirting";
			case "13_1":
				return "Orgy";
			case "14_1":
				return "Cosplay";
			case "15_1":
				return "Teen";
			case "16_1":
				return "Gal";
			case "17_1":
				return "Idol";
			case "18_1":
				return "Teacher";
			case "20_1":
				return "Big Tits";
			case "21_1":
				return "Swimsuit";
			case "22_1":
				return "Bondage";
			case "24_1":
				return "Outdoor Exposure";
			case "26_1":
				return "Documentary";
			case "27_1":
				return "Seduction";
			case "28_1":
				return "S&M";
			case "29_1":
				return "Shaved Pussy";
			case "30_1":
				return "Restraints";
			case "31_1":
				return "Masturbation";
			case "32_1":
				return "Vibrator";
			case "33_1":
				return "Fucking";
			case "34_1":
				return "Blowjob";
			case "35_1":
				return "Semen";
			case "36_1":
				return "Cum Swallow";
			case "37_1":
				return "Golden Shower";
			case "38_1":
				return "Handjob";
			case "39_1":
				return "69";
			case "40_1":
				return "Anal";
			case "42_1":
				return "Cunnilingus";
			case "43_1":
				return "Best / VA";
			case "44_1":
				return "Bareback Fucking";
			case "45_1":
				return "Nurse";
			case "46_1":
				return "Bloomers";
			case "47_1":
				return "Molester";
			case "49_1":
				return "White Girl";
			case "51_1":
				return "Anime";
			case "52_1":
				return "Insult";
			case "53_1":
				return "First Time Porn";
			case "56_1":
				return "Uniforms";
			case "55_1":
				return "Pornstar";
			case "62_1":
				return "Ass";
			case "64_1":
				return "Legs";
			case "65_1":
				return "Bukkake";
			case "67_1":
				return "Deep Throating";
			case "69_1":
				return "Transsexual";
			case "70_1":
				return "Teen";
			case "71_1":
				return "Look-alike";
			case "72_1":
				return "Small Tits";
			case "73_1":
				return "Slender";
			case "74_1":
				return "Car Sex";
			case "75_1":
				return "Shaving";
			case "77_1":
				return "Dirty Words";
			case "78_1":
				return "Cumshot";
			case "79_1":
				return "Facial";
			case "80_1":
				return "Apron";
			case "81_1":
				return "Glasses";
			case "82_1":
				return "OL";
			case "83_1":
				return "Maid";
			case "84_1":
				return "Yukata / Kimono";
			default:
				break;
		}
		//System.out.println("No genre match for " + currentGenreCode);
		return null;
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
		return new Studio("Caribbeancom Premium");
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String fileNameNoExtension = findIDTagFromFile(file, isFirstWordOfFileIsID());
		return fileNameNoExtension;
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
		if (japaneseDocument == null) {
			String urlOfCurrentPage = document.location();
			if (urlOfCurrentPage != null && urlOfCurrentPage.contains("moviepages")) {
				//the genres are only available on the japanese version of the page
				urlOfCurrentPage = urlOfCurrentPage.replaceFirst(Pattern.quote("http://en.caribbeancompr.com/eng/"), "http://www.caribbeancompr.com/");
				if (urlOfCurrentPage.length() > 1) {
					try {
						japaneseDocument = Jsoup.connect(urlOfCurrentPage).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
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
