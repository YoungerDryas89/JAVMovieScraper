package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.text.WordUtils;
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

import javax.annotation.Nonnull;

public class CaribbeancomParsingProfile extends SiteParsingProfile implements SpecificProfile {

	Document japaneseDocument;
	String id;

	boolean useTranslationOfJapanesePageForEnglishMetadata = true;
	private static final SimpleDateFormat caribbeanReleaseDateFormat = new SimpleDateFormat("yyyy/mm/dd", Locale.ENGLISH);

	Map<String, String> japaneseDetailEquivalent = Map.of(
			"Starring:", "出演",
			"Release date:", "配信日",
			"Runtime:", "再生時間",
			"Tags:", "タグ",
			"User Rating:", "ユーザー評価"
	);

	Map<Language, String> detailTable = new HashMap<>();

	@Override
	public String getParserName() {
		return "Caribbeancom";
	}

	/**
	 * loads up the japanese version of this page into japaneseDocument
	 */
	private void initializeJapaneseDocument() {
		if (document != null && japaneseDocument == null) {
			String url = "http://www.caribbeancom.com/moviepages/" + id + "/index.html";
			japaneseDocument = SiteParsingProfile.downloadDocumentFromURLString(url);
		}
	}

	@Nonnull
    @Override
	public Title scrapeTitle() {
		try {
			Element titleElement = document.select(".movie-info .heading [itemprop=name]").first();
			return new Title(titleElement.text());
		} catch (Exception e) {
			return new Title("");
		}
	}

	@Nonnull
    @Override
	public OriginalTitle scrapeOriginalTitle() {
		return OriginalTitle.BLANK_ORIGINALTITLE;
	}

	@Nonnull
    @Override
	public SortTitle scrapeSortTitle() {
		return SortTitle.BLANK_SORTTITLE;
	}

	@Nonnull
    @Override
	public Set scrapeSet() {
		return Set.BLANK_SET;
	}

	@Nonnull
    @Override
	public Rating scrapeRating() {
		return Rating.BLANK_RATING;
	}

	@Nonnull
    @Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}

	@Nonnull
    @Override
	public ReleaseDate scrapeReleaseDate() {
		try {
			Element releaseDate = document.select(".movie-info [itemprop=uploadDate]").first();
			return new ReleaseDate(releaseDate.text(), caribbeanReleaseDateFormat);
		} catch (NullPointerException e) {
			return ReleaseDate.BLANK_RELEASEDATE;
		}
	}

	@Nonnull
    @Override
	public Top250 scrapeTop250() {
		// TODO Auto-generated method stub
		return Top250.BLANK_TOP250;
	}

	@Nonnull
    @Override
	public Votes scrapeVotes() {
		return Votes.BLANK_VOTES;
	}

	@Nonnull
    @Override
	public Outline scrapeOutline() {
		return Outline.BLANK_OUTLINE;
	}

	@Nonnull
    @Override
	public Plot scrapePlot() {
		return Plot.BLANK_PLOT;
	}

	@Nonnull
    @Override
	public Tagline scrapeTagline() {
		return Tagline.BLANK_TAGLINE;
	}

	@Nonnull
    @Override
	public Runtime scrapeRuntime() {
		try {
			Element durationElement = document.select(".movie-info span[itemprop=duration]").first();
			if (durationElement.text().trim().length() == 0) {
				throw new IllegalArgumentException("Duration text is empty");
			}
			String[] durationSplitByTimeUnit = durationElement.text().split(":");
			if (durationSplitByTimeUnit.length != 3) {
				throw new IllegalArgumentException("Invalid number of parts");
			}
			int hours = Integer.parseInt(durationSplitByTimeUnit[0]);
			int minutes = Integer.parseInt(durationSplitByTimeUnit[1]);
			// we don't care about seconds

			int totalMinutes = (hours * 60) + minutes;
			return new Runtime(Integer.toString(totalMinutes));
		} catch (Exception e) {
			return Runtime.BLANK_RUNTIME;
		}
	}

	@Override
	public Thumb[] scrapePosters(boolean cropPosters) {
		ID id = scrapeID();
		ArrayList<Thumb> posters = new ArrayList<>();
		// I tried getting the URL through extracting it directly, but what I would get in the scraper would not be
		// consistent with my web browser; dunno why yey, but I chose to do this instead
		String[] image_url_suffix = {"poster_en.jpg", "l.jpg", "l_l.jpg"};
		String img_url = "https://en.caribbeancom.com/moviepages/" + id.getId();
		try {
			for(var suffix : image_url_suffix){
				if(fileExistsAtURL(img_url + "/images/" + suffix)){
					Thumb additionalThumb = new Thumb("https://en.caribbeancom.com/moviepages/" + id.getId() + "/images/" + suffix, cropPosters);
					posters.add(additionalThumb);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return posters.toArray(new Thumb[posters.size()]);
	}

	@Override
	public Thumb[] scrapeFanart() {
		ID id = scrapeID();
		ArrayList<Thumb> posters = new ArrayList<>();
		for (Element anchor : document.select("a.fancy-gallery")) {
			if (anchor.attr("data-is_sample").compareTo("1") == 0) {
				try {
					Thumb additionalThumb = new Thumb("https://en.caribbeancom.com" + anchor.attr("href"));
					posters.add(additionalThumb);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		return posters.toArray(new Thumb[posters.size()]);
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		return new Thumb[0];
	}

	@Nonnull
    @Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Nonnull
    @Override
	public ID scrapeID() {
		initializeJapaneseDocument();
		// Just get the ID from the page URL by doing some string manipulation
		String baseUri = japaneseDocument.baseUri();
		if (baseUri.length() > 0 && baseUri.contains("caribbeancom.com")) {
			baseUri = baseUri.replaceFirst("/index.html", "");
			String idFromBaseUri = baseUri.substring(baseUri.lastIndexOf('/') + 1);
			return new ID(idFromBaseUri);
		}
		return new ID("");
	}

	@Nonnull
    @Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<>();
		try {
			// Elements genres = document.select(".movie-info
			// [itemtype=http://data-vocabulary.org/Breadcrumb][itemprop=url]");
			Elements genres = document.select(".movie-info [itemprop=genre]");

			for (Element currentGenre : genres) {
				if (currentGenre.text().trim().length() > 0) {
					String genreText = currentGenre.text();
					genreList.add(new Genre(genreText));
				}
			}
		} catch (Exception e) {
		}
		return genreList;
	}

	@Nonnull
    @Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<>();
		Elements actorElements = document.select(".movie-info [itemprop=actor]");
		try {
			for (Element actorElement : actorElements) {
				String actorName = actorElement.text();
				actorList.add(new Actor(actorName, "", null));
			}
		} catch (Exception e) {
			// Do nothing. Just skip this actor
		}
		return actorList;
	}

	@Nonnull
    @Override
	public ArrayList<Director> scrapeDirectors() {
		//No Director information on the site
		return new ArrayList<>();
	}

	@Nonnull
    @Override
	public Studio scrapeStudio() {
		return new Studio("Caribbeancom");
	}

	@Nonnull
    @Override
	public Trailer scrapeTrailer() {
		ID id = scrapeID();
		if (id != null && id.getId().length() > 0) {
			String trailerPath = "http://smovie.caribbeancom.com/sample/movies/" + id.getId() + "/sample_m.mp4";
			if (SiteParsingProfile.fileExistsAtURL(trailerPath))
				return new Trailer(trailerPath);
		}

		return Trailer.BLANK_TRAILER;
	}

	@Nonnull
    @Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		this.id = findIDTagFromFile(file);
		return createSearchStringFromId(this.id);
	}

	@Override
	public String createSearchStringFromId(String id) {
		return "http://en.caribbeancom.com/eng/moviepages/" + id + "/index.html";

	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		SearchResult englishPage = new SearchResult(searchString);
		SearchResult[] results = { englishPage };
		initializeJapaneseDocument();
		return results;
	}

	public static String findIDTagFromFile(File file) {
		return findIDTag(FilenameUtils.getName(file.getName()));
	}

	public static String findIDTag(String fileName) {
		Pattern pattern = Pattern.compile("[0-9]{6}-[0-9]{3}");
		Matcher matcher = pattern.matcher(fileName);
		if (matcher.find()) {
			String searchString = matcher.group();
			return searchString;
		}
		return null;
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new CaribbeancomParsingProfile();
	}

}
