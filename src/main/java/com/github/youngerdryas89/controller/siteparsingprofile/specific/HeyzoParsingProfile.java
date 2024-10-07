package com.github.youngerdryas89.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.youngerdryas89.controller.languagetranslation.Language;
import com.github.youngerdryas89.controller.siteparsingprofile.SiteParsingProfile;
import com.github.youngerdryas89.model.SearchResult;
import com.github.youngerdryas89.model.dataitem.Actor;
import com.github.youngerdryas89.model.dataitem.Director;
import com.github.youngerdryas89.model.dataitem.Genre;
import com.github.youngerdryas89.model.dataitem.ID;
import com.github.youngerdryas89.model.dataitem.MPAARating;
import com.github.youngerdryas89.model.dataitem.OriginalTitle;
import com.github.youngerdryas89.model.dataitem.Outline;
import com.github.youngerdryas89.model.dataitem.Plot;
import com.github.youngerdryas89.model.dataitem.Rating;
import com.github.youngerdryas89.model.dataitem.ReleaseDate;
import com.github.youngerdryas89.model.dataitem.Runtime;
import com.github.youngerdryas89.model.dataitem.Set;
import com.github.youngerdryas89.model.dataitem.SortTitle;
import com.github.youngerdryas89.model.dataitem.Studio;
import com.github.youngerdryas89.model.dataitem.Tagline;
import com.github.youngerdryas89.model.dataitem.Thumb;
import com.github.youngerdryas89.model.dataitem.Title;
import com.github.youngerdryas89.model.dataitem.Top250;
import com.github.youngerdryas89.model.dataitem.Trailer;
import com.github.youngerdryas89.model.dataitem.Votes;
import com.github.youngerdryas89.model.dataitem.Year;

import javax.annotation.Nonnull;

public class HeyzoParsingProfile extends SiteParsingProfile implements SpecificProfile {

	private String englishPage;
	private String japanesePage;

	Document japaneseDocument;

	@Override
	public String getParserName() {
		return "HEYZO";
	}

	public HeyzoParsingProfile() {
		super();
	}

	@Nonnull
    @Override
	public Title scrapeTitle() {
		Element titleElement = document.select("div#movie h1").first();
		if (titleElement != null) {
			String titleElementText = titleElement.text().trim().replaceAll("[ ]+", " ");
			return new Title(titleElementText);
		}
		return new Title("");
	}

	@Nonnull
    @Override
	public OriginalTitle scrapeOriginalTitle() {
		if (scrapingLanguage == Language.JAPANESE)
			return new OriginalTitle(scrapeTitle().getTitle());
		else {
			Document originalDocument = document;
			document = japaneseDocument;
			OriginalTitle originalTitle = new OriginalTitle(scrapeTitle().getTitle());
			document = originalDocument;
			return originalTitle;
		}
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
		//This used to be scrapable, but this now requires javascript to parse the page to get the rating
		//TODO: If I ever replace jsoup with a javascript enabled parser, rewrite this function
		return Rating.BLANK_RATING;
		/* this was the code that should work if I had javascript enabled parser */
		/*
		 * Element ratingValueElement = japaneseDocument.select("#review-value").first();
		 * if(ratingValueElement != null)
		 * {
		 * return new Rating(5.0, ratingValueElement.text().trim());
		 * }
		 * else return Rating.BLANK_RATING;
		 */
	}

	@Nonnull
    @Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}

	@Nonnull
    @Override
	public ReleaseDate scrapeReleaseDate() {
		Elements elements = japaneseDocument.select("table.movieInfo tr.table-release-day");
		Element releaseDateElement = elements.first().children().last();
		if (releaseDateElement != null) {
			//System.out.println("year = " + yearElement.text());
			String yearText = releaseDateElement.text().trim();
			if (yearText.length() > 4) {
				return new ReleaseDate(yearText);
			}

		}
		return ReleaseDate.BLANK_RELEASEDATE;
	}

	@Nonnull
    @Override
	public Top250 scrapeTop250() {
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
		Element runtimeElement = document.select("tbody:contains(Whole Movie File Download) tr:contains(:) td").first();
		if (runtimeElement != null) {
			String[] runtimeTextSplit = runtimeElement.text().trim().split((":"));
			if (runtimeTextSplit.length == 3) {
				int hours = Integer.parseInt(runtimeTextSplit[0]);
				int minutes = Integer.parseInt(runtimeTextSplit[1]);
				int totalMinutes = (hours * 60) + minutes;
				if (totalMinutes > 0)
					return new Runtime(Integer.valueOf(totalMinutes).toString());
			}
		}
		return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters(boolean cropPosters) {
		ArrayList<Thumb> thumbList = new ArrayList<>();
		String scrapedId = scrapeID().getId();
		try {
			if(SiteParsingProfile.fileExistsAtURL("https://en.heyzo.com/contents/3000/" + scrapedId + "/images/player_thumbnail_en.jpg")) {
				Thumb cover = new Thumb("https://en.heyzo.com/contents/3000/" + scrapedId +"/images/player_thumbnail_en.jpg", cropPosters);
				thumbList.add(cover);
			} else {
				//If for whatever reason the above^ image is not available use some fan art as a thumbnail
				if(SiteParsingProfile.fileExistsAtURL("https://https://en.heyzo.com/contents/3000/" + scrapedId + "/gallery/001.jpg")){
					Thumb fanartSubstitute = new Thumb("https://https://en.heyzo.com/contents/3000/" + scrapedId + "/gallery/001.jpg", cropPosters);
					thumbList.add(fanartSubstitute);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return thumbList.toArray(new Thumb[thumbList.size()]);
		}
		// TODO Auto-generated method stub
		return thumbList.toArray(new Thumb[thumbList.size()]);
	}

	@Nonnull
    @Override
	public Trailer scrapeTrailer() {
		String scrapedId = scrapeID().getId();
		String trailerURL = "https://sample.heyzo.com/contents/3000/" + scrapedId + "/heyzo_hd_0194_sample.mp4";
		if (SiteParsingProfile.fileExistsAtURL(trailerURL))
			return new Trailer(trailerURL);
		return Trailer.BLANK_TRAILER;
	}

	@Override
	public Thumb[] scrapeFanart()  {
		ArrayList<Thumb> thumbList = new ArrayList<Thumb>();
		try {
			var scrapedId = scrapeID().getId();
			for (int i = 1; i <= 21; i++) {
				String potentialGalleryImageURL = "https://en.heyzo.com/contents/3000/" + scrapedId + "/gallery/0" + String.format("%02d", i) + ".jpg";
				String potentialGalleryPreviewImageURL = "https://en.heyzo.com/contents/3000/" + scrapedId + "/gallery/thumbnail_0" + String.format("%02d", i) + ".jpg";
				if (SiteParsingProfile.fileExistsAtURL(potentialGalleryImageURL)) {
					Thumb thumbToAdd = new Thumb(potentialGalleryImageURL);
					thumbToAdd.setPreviewURL(new URL(potentialGalleryPreviewImageURL));
					thumbList.add(thumbToAdd);

				}
			}
		} catch (MalformedURLException e){
			System.err.println(e.getMessage());
		}
		return thumbList.toArray(new Thumb[thumbList.size()]);
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		return scrapePosters(false);
	}

	@Nonnull
    @Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Nonnull
    @Override
	public ID scrapeID() {
		//Just get the ID from the page URL by doing some string manipulation
		String baseUri = document.baseUri();
		if (baseUri.length() > 0 && baseUri.contains("heyzo.com")) {
			baseUri = baseUri.replaceFirst("/index.html", "");
			String idFromBaseUri = baseUri.substring(baseUri.lastIndexOf('/') + 1);
			return new ID(idFromBaseUri);
		}
		return ID.BLANK_ID;
	}

	@Nonnull
    @Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<>();
		Elements genreElements = document.select(".movieInfo a[href*=/listpages/category]");
		if (genreElements != null) {
			for (Element currentGenre : genreElements) {
				if (currentGenre.text().trim().length() > 0)
					genreList.add(new Genre(currentGenre.text().trim()));
			}
		}
		return genreList;
	}

	@Nonnull
    @Override
	public ArrayList<Actor> scrapeActors() {
		Elements actorElements = document.selectXpath("//td[text()='Actress(es)']/following-sibling::td/a");
		ArrayList<Actor> actorList = new ArrayList<>();
		for (Element currentActor : actorElements) {
			String actorName = currentActor.text().trim();
			String actorHref = currentActor.attr("href");
			String actorNumber = null;
			String actorThumbUrl = null;
			if (actorHref != null && actorHref.length() > 0) {
				String[] splitHrefByUnderScore = actorHref.split("_");
				if (splitHrefByUnderScore.length > 0) {
					actorNumber = splitHrefByUnderScore[1];
					actorThumbUrl = "http://en.heyzo.com/actorprofile/3000/" + String.format("%04d", Integer.parseInt(actorNumber)) + "/profile.jpg";
				}
			}
			//we found a thumbnail image for this actor
			if (actorThumbUrl != null && SiteParsingProfile.fileExistsAtURL(actorThumbUrl)) {
				try {
					actorList.add(new Actor(actorName, "", new Thumb(actorThumbUrl)));
				} catch (MalformedURLException e) {
					e.printStackTrace();
					actorList.add(new Actor(actorName, "", null));
				}
			}
			//we didn't find a thumbnail image for this actor
			else {
				actorList.add(new Actor(actorName, "", null));
			}
		}
		return actorList;
	}

	@Nonnull
    @Override
	public ArrayList<Director> scrapeDirectors() {
		return new ArrayList<>();
	}

	@Nonnull
    @Override
	public Studio scrapeStudio() {
		// TODO Auto-generated method stub
		return new Studio("HEYZO");
	}

	@Nonnull
    @Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String fileID = findIDTagFromFile(file).toLowerCase();

		if (fileID != null && !fileID.isEmpty()) {
			return createSearchStringFromId(fileID);
		}

		return null;
	}

	@Override
	public String createSearchStringFromId(String Id) {
		englishPage = "https://en.heyzo.com/moviepages/" + Id + "/index.html";
		japanesePage = "https://www.heyzo.com/moviepages/" + Id + "/index.html";
		try {
			japaneseDocument = Jsoup.connect(japanesePage).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (scrapingLanguage == Language.ENGLISH) {
			return englishPage;
		} else {
			return japanesePage;
		}
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		SearchResult searchResult = new SearchResult(searchString);
		SearchResult[] searchResultArray = { searchResult };
		return searchResultArray;
	}

	public static String findIDTagFromFile(File file) {
		return findIDTag(FilenameUtils.getName(file.getName()));
	}

	public static String findIDTag(String fileName) {
		Pattern pattern = Pattern.compile("[0-9]{4}");
		Matcher matcher = pattern.matcher(fileName);
		if (matcher.find()) {
			String searchString = matcher.group();
			return searchString;
		}
		return null;
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new HeyzoParsingProfile();
	}

}
