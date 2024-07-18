package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
import moviescraper.doctord.model.dataitem.Votes;
import moviescraper.doctord.model.dataitem.Year;

import javax.annotation.Nonnull;

public class ActionJavParsingProfile extends SiteParsingProfile implements SpecificProfile {

	private static final SimpleDateFormat actionJavReleaseDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

	@Nonnull
    @Override
	public Title scrapeTitle() {

		Element titleElement = document.select("table.p-2 > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2) > span:nth-child(1)").first();
		return new Title(titleElement.text());
	}

	@Nonnull
    @Override
	public OriginalTitle scrapeOriginalTitle() {
		// ActionJav doesn't have the Japanese title, so we don't want to return
		// anything but a blank text element
		return OriginalTitle.BLANK_ORIGINALTITLE;
	}

	@Nonnull
    @Override
	public SortTitle scrapeSortTitle() {
		// we don't need any special sort title - that's usually something the
		// user provides
		return SortTitle.BLANK_SORTTITLE;
	}

	@Nonnull
    @Override
	public Set scrapeSet() {
		// ActionJav doesn't have any set information
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
		Element releaseDateElement = document.select("table.p-2 > tbody:nth-child(1) > tr:nth-child(7) > td:nth-child(2) > span:nth-child(1)").first();
		if (releaseDateElement != null && releaseDateElement.text().length() > 4) {
			String releaseDateText = releaseDateElement.text().trim();
			if (!Character.isAlphabetic(releaseDateText.charAt(0))) //fix for weird white space trim() is not getting rid of
				releaseDateText = releaseDateText.substring(1);
			if (releaseDateText.length() > 4)
				return new ReleaseDate(releaseDateText.trim(), actionJavReleaseDateFormat);
		}
		return ReleaseDate.BLANK_RELEASEDATE;
	}

	@Nonnull
    @Override
	public Top250 scrapeTop250() {
		// This type of info doesn't exist on ActionJav
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
		// Find text elements that contain the word "min"
		// We might get some duplicates here if the movie is offered in multiple
		// codecs
		// but we can do some filtering later on to fix things by using a
		// HashTable to take care of the duplicate format problems
		Elements movieDownloadParts = document
		        .select("html body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr td table tbody tr td p:has(font:containsOwn(min, ))");
		ArrayList<String> movieFileName = new ArrayList<>(movieDownloadParts.size());
		Hashtable<String, Integer> runtimesByPart = new Hashtable<>(movieDownloadParts.size());
		// we got to do some processing to get the unique runtime per part,
		// ignoring file extension
		for (Element movieElement : movieDownloadParts) {
			// get the filename without extension
			// System.out.println("movieElement: " + movieElement);
			Element movieElementLink = movieElement.select("a").first();
			if (movieElementLink != null) {
				String filePath = movieElementLink.attr("href");
				String[] splitBySlash = filePath.split("/");
				//get just the file
				String fileNameNoExtension = splitBySlash[splitBySlash.length - 1];

				fileNameNoExtension = fileNameNoExtension.substring(0, fileNameNoExtension.length() - 4); // strip the extension

				movieFileName.add(filePath);

				// get the runtime
				String runtimeText = movieElement.select("font").last().text();

				//get whole text element
				Integer runtimeAmt = Integer.valueOf(runtimeText.substring(1, runtimeText.indexOf('m') - 1));
				//narrow it down to just the numeric part since we want to ignore the other garbage in the string
				runtimesByPart.put(fileNameNoExtension, runtimeAmt);
			}
		}
		int totalRuntime = 0;
		// Our hastable has automatically taken care of the duplicate format
		// problem with listing each runtime part twice
		for (Integer uniqueRuntime : runtimesByPart.values()) {
			totalRuntime += uniqueRuntime.intValue();
		}

		if (totalRuntime != 0) {
			return new Runtime(Integer.toString(totalRuntime));
		} else
			return Runtime.BLANK_RUNTIME;
	}
	@Override
	public Thumb[] scrapePosters() {
		// TODO: Find a way around 403 forbidden
		try {
			var posterElement = document.select("div.bg-white:nth-child(2) > div:nth-child(1) > center:nth-child(1) > a:nth-child(1) > img:nth-child(1)");
			var thumb = new Thumb(posterElement.first().attr("src"));
			return new Thumb[]{thumb};
		}catch (MalformedURLException e){
			System.err.println(e.getMessage());
		}
		return new Thumb[]{};
	}

	@Override
	public Thumb[] scrapeFanart() {
		return new Thumb[]{};
	}

	@Nonnull
    @Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Nonnull
    @Override
	public ID scrapeID() {
		Element idElement = document.select("table.p-2 > tbody:nth-child(1) > tr:nth-child(4) > td:nth-child(2) > span:nth-child(1)").first();

		if (idElement != null) {
			return new ID(idElement.text().split(" ")[0]);
		}
		return ID.BLANK_ID;
	}

	@Nonnull
    @Override
	public ArrayList<Genre> scrapeGenres() {
		Element genreElements = document.select("table.p-2 > tbody:nth-child(1) > tr:nth-child(9) > td:nth-child(2) > span:nth-child(1)").first();
		var genres = new ArrayList<Genre>();
        assert genreElements != null;
        for(var genre : genreElements.text().split(" ")){
			Genre gen = new Genre(genre);
			genres.add(gen);
		}
		return genres;
	}

	@Nonnull
    @Override
	public ArrayList<Actor> scrapeActors() {
		Elements actorElements = document.select("table.p-2 > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(2) > span:nth-child(1) > a:nth-child(1)");
		if (actorElements != null) {
			ArrayList<Actor> actorList = new ArrayList<>(actorElements.size());
			try {

				for (Element actorElement : actorElements) {
					Actor currentActor = new Actor(actorElement.text(), "", null);
					actorList.add(currentActor);
				}
				return actorList;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<>();
	}

	@Nonnull
    @Override
	public ArrayList<Director> scrapeDirectors() {
		//ActionJav doesn't have director information, so just return an empty list
		return new ArrayList<>();
	}

	@Nonnull
    @Override
	public Studio scrapeStudio() {
		Element studioElement = document.select("table.p-2 > tbody:nth-child(1) > tr:nth-child(3) > td:nth-child(2) > span:nth-child(1)").first();
		return new Studio(studioElement.text());
	}

	@Nonnull
    @Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		return createSearchStringFromId(findIDTagFromFile(file, isFirstWordOfFileIsID()));
	}

	@Override
	public String createSearchStringFromId(String Id) {
		return "https://www.actionjav.com/?view=search&item=" + Id;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		if (searchString == null)
			return new SearchResult[0];

		var searchItems = new ArrayList<SearchResult>();

		var searchDoc = Jsoup.connect(searchString).userAgent(getRandomUserAgent()).get();
		var elems = searchDoc.select("div.card");

		for(var elem : elems){
			var url_element = elem.getElementsByTag("a").first();
			var title = elem.getElementsByClass("movie-list-title").first().text();
			SearchResult result = new SearchResult(url_element.attr("href"), title, new Thumb(url_element.childNode(1).attr("src")));
			searchItems.add(result);
		}
		return searchItems.toArray(new SearchResult[searchItems.size()]);
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		return new Thumb[]{};
	}

	@Override
	public String toString() {
		return "ActionJav";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new ActionJavParsingProfile();
	}

	@Override
	public String getParserName() {
		return "ActionJav";
	}

}
