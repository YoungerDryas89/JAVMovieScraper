package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;

import com.github.youngerdryas89.moviescraper.scraper.UserAgent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfile;
import com.github.youngerdryas89.moviescraper.model.SearchResult;
import com.github.youngerdryas89.moviescraper.model.dataitem.Actor;
import com.github.youngerdryas89.moviescraper.model.dataitem.Director;
import com.github.youngerdryas89.moviescraper.model.dataitem.Genre;
import com.github.youngerdryas89.moviescraper.model.dataitem.ID;
import com.github.youngerdryas89.moviescraper.model.dataitem.MPAARating;
import com.github.youngerdryas89.moviescraper.model.dataitem.OriginalTitle;
import com.github.youngerdryas89.moviescraper.model.dataitem.Outline;
import com.github.youngerdryas89.moviescraper.model.dataitem.Plot;
import com.github.youngerdryas89.moviescraper.model.dataitem.Rating;
import com.github.youngerdryas89.moviescraper.model.dataitem.ReleaseDate;
import com.github.youngerdryas89.moviescraper.model.dataitem.Runtime;
import com.github.youngerdryas89.moviescraper.model.dataitem.Set;
import com.github.youngerdryas89.moviescraper.model.dataitem.SortTitle;
import com.github.youngerdryas89.moviescraper.model.dataitem.Studio;
import com.github.youngerdryas89.moviescraper.model.dataitem.Tagline;
import com.github.youngerdryas89.moviescraper.model.dataitem.Thumb;
import com.github.youngerdryas89.moviescraper.model.dataitem.Title;
import com.github.youngerdryas89.moviescraper.model.dataitem.Top250;
import com.github.youngerdryas89.moviescraper.model.dataitem.Votes;
import com.github.youngerdryas89.moviescraper.model.dataitem.Year;

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
		Elements runtime = document
		        .select("td:containsOwn(Runtime) + td");

        if(runtime != null && !runtime.isEmpty()){
            return new Runtime(runtime.first().text().split(" ")[0]);
        }
        return Runtime.BLANK_RUNTIME;
	}
	@Override
	public Thumb[] scrapePosters(boolean cropPosters) {
		// TODO: Find a way around 403 forbidden
		try {
			var posterElement = document.select("div.bg-white:nth-child(2) > div:nth-child(1) > center:nth-child(1) > a:nth-child(1) > img:nth-child(1)");
			var thumb = new Thumb(posterElement.first().attr("src"), cropPosters);
			return new Thumb[]{thumb};
		}catch (IOException e){
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
        return new ArrayList<>();
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

		var searchDoc = Jsoup.connect(searchString).userAgent(UserAgent.getRandomUserAgent()).get();
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
