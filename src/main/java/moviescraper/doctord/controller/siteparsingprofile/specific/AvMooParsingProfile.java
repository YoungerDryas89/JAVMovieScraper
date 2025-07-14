package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.controller.languagetranslation.JapaneseCharacter;
import moviescraper.doctord.controller.languagetranslation.Language;
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

public class AvMooParsingProfile extends SiteParsingProfile implements SpecificProfile {

	public static final String urlLanguageEnglish = "en";
	public static final String urlLanguageJapanese = "ja";

	@Override
	public List<ScraperGroupName> getScraperGroupNames() {
		if (groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP);
		return groupNames;
	}

	public AvMooParsingProfile(Document doc) {
		super(doc);
	}

	public AvMooParsingProfile() {
		// TODO Auto-generated constructor stub
	}

	@Nonnull
    @Override
	public Title scrapeTitle() {
		Element titleElement = document.select("div.container h3").first();
		if (titleElement != null) {
			//remove the ID number off beginning of the title, if it exists (and it usually always does on JavLibrary)
			String titleElementText = titleElement.text().trim();
			titleElementText = titleElementText.substring(StringUtils.indexOf(titleElementText, " ")).trim();
			//sometimes this still leaves "- " at the start of the title, so we'll want to get rid of that too
			if (titleElementText.startsWith("- ")) {
				titleElementText = titleElementText.replaceFirst(Pattern.quote("- "), "");
			}

			// TODO: Add Japanese to Romaji or English translation code
			return new Title(titleElementText);
		} else
			return new Title("");
	}

	@Nonnull
    @Override
	public OriginalTitle scrapeOriginalTitle() {
		try {
			Element titleElement = document.select("div.container h3").first();
			if (titleElement != null) {
				//remove the ID number off beginning of the title, if it exists (and it usually always does on JavLibrary)
				String titleElementText = titleElement.text().trim();
				titleElementText = titleElementText.substring(StringUtils.indexOf(titleElementText, " ")).trim();
				//sometimes this still leaves "- " at the start of the title, so we'll want to get rid of that too
				if (titleElementText.startsWith("- ")) {
					titleElementText = titleElementText.replaceFirst(Pattern.quote("- "), "");
				}

				//sometimes title is not translated on the english site
				if (JapaneseCharacter.containsJapaneseLetter(titleElementText))
					return new OriginalTitle(titleElementText);

				// scrape japanese site for original text
				String japaneseUrl = document.location().replaceFirst(Pattern.quote("/en/"), "/ja/");
				if (japaneseUrl.equals(document.location()))
					return new OriginalTitle(titleElementText);

				Document japaneseDoc = Jsoup.connect(japaneseUrl).timeout(CONNECTION_TIMEOUT_VALUE).get();
				AvMooParsingProfile spp = new AvMooParsingProfile(japaneseDoc);
				return spp.scrapeOriginalTitle();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		Element setElement = document.select("div.container p:contains(Series:) ~ p a").first();
		if (setElement != null) {
			return new Set(setElement.text().trim());
		} else
			return Set.BLANK_SET;
	}

	@Nonnull
    @Override
	public Rating scrapeRating() {
		// this site does not have ratings, so just return some default values
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
		Element releaseDateElement = document.select(
			"div.container p:contains(Release Date:), div.container p:contains(發行日期:), div.container p:contains(発売日:)").first();
		if (releaseDateElement != null) {
			String releaseDateText = releaseDateElement.text().trim();
			releaseDateText = releaseDateText.replace("Release Date:", "");
			releaseDateText = releaseDateText.replace("發行日期:", "");
			releaseDateText = releaseDateText.replace("発売日:", "");
			if (releaseDateText != null && releaseDateText.length() > 4)
				return new ReleaseDate(releaseDateText.trim());
		}
		return ReleaseDate.BLANK_RELEASEDATE;
	}

	@Nonnull
    @Override
	public Top250 scrapeTop250() {
		// This type of info doesn't exist on AvMoo
		return Top250.BLANK_TOP250;
	}

	@Nonnull
    @Override
	public Votes scrapeVotes() {
		//This type of info doesn't exist on AvMoo
		return Votes.BLANK_VOTES;
	}

	@Nonnull
    @Override
	public Outline scrapeOutline() {
		//This type of info doesn't exist on AvMoo
		return Outline.BLANK_OUTLINE;
	}

	@Nonnull
    @Override
	public Plot scrapePlot() {
		//This type of info doesn't exist on AvMoo
		return Plot.BLANK_PLOT;
	}

	@Nonnull
    @Override
	public Tagline scrapeTagline() {
		//This type of info doesn't exist on AvMoo
		return Tagline.BLANK_TAGLINE;
	}

	@Nonnull
    @Override
	public Runtime scrapeRuntime() {
		Element runtimeElement = document.select(
			"div.container p:contains(Length:), div.container p:contains(収録時間:)").first();
		if (runtimeElement != null) {
			String lengthText = runtimeElement.text().trim();
			lengthText = lengthText.replaceFirst(Pattern.quote("Length: "), "");
			lengthText = lengthText.replaceFirst(Pattern.quote("min"), "");
			lengthText = lengthText.replaceFirst(Pattern.quote("収録時間: "), "");
			lengthText = lengthText.replaceFirst(Pattern.quote("分"), "");
			if (lengthText.length() > 0) {
				return new Runtime(lengthText);
			}
		}
		return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters(boolean cropPosters) {
		return scrapePostersAndFanart(cropPosters);
	}

	@Override
	public Thumb[] scrapeFanart() {
		return scrapePostersAndFanart(false);
	}

	private Thumb[] scrapePostersAndFanart(boolean doCrop) {
		Element posterElement = document.select("a.bigImage img").first();
		Thumb[] posterThumbs = new Thumb[1];
		if (posterElement != null) {
			String posterLink = posterElement.attr("src").trim();
			try {
				if (doCrop)
					//posterThumbs[0] = new Thumb(posterLink, 52.7, 0, 0, 0);
					posterThumbs[0] = new Thumb(posterLink, true);
				else
					posterThumbs[0] = new Thumb(posterLink);
				return posterThumbs;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new Thumb[0];
			}
		} else
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
		Element idElement = document.select("div.container p:contains(ID:), div.container p:contains(品番:)").first();
		if (idElement != null) {
			String idText = idElement.text().trim();
			idText = idText.replaceFirst(Pattern.quote("ID: "), "");
			idText = idText.replaceFirst(Pattern.quote("品番: "), "");
			return new ID(idText);
		} else
			return ID.BLANK_ID;
	}

	@Nonnull
    @Override
	public ArrayList<Genre> scrapeGenres() {
		Elements genreElements = document.select(".genre");
		if (genreElements != null) {
			ArrayList<Genre> genreList = new ArrayList<>(genreElements.size());
			for (Element currentGenre : genreElements) {
				genreList.add(new Genre(currentGenre.text().trim()));
			}
			return genreList;
		}
		return new ArrayList<>();
	}

	@Nonnull
    @Override
	public ArrayList<Actor> scrapeActors() {
		Elements actorElements = document.select("div#avatar-waterfall a.avatar-box");
		if (actorElements != null) {
			ArrayList<Actor> actorList = new ArrayList<>(actorElements.size());
			for (Element currentActor : actorElements) {
				String actorName = currentActor.select("span").first().text().trim();
				String actorThumbURL = currentActor.select("img").first().attr("src");
				//we want the full resolution thumbnail, so replace the "medium" from the URL to get it
				//actorThumbURL = actorThumbURL.replaceFirst(Pattern.quote("/medium/"), "/");
				try {
					//we can add the actor with their thumbnail so long as we aren't using a placeholder image
					if (!actorThumbURL.contains("nowprinting.gif")) {
						actorList.add(new Actor(actorName, "", new Thumb(actorThumbURL)));
					} else //otherwise add the actor without an image
					{
						actorList.add(new Actor(actorName, "", null));
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return actorList;
		}
		return new ArrayList<>();
	}

	@Nonnull
    @Override
	public ArrayList<Director> scrapeDirectors() {
		Element directorElement = document.select("div.row.movie p:contains(Director:)").first();
		if (directorElement != null) {
			ArrayList<Director> directorList = new ArrayList<>(1);
			String directorNameText = directorElement.text().trim();
			directorNameText = directorNameText.replaceFirst(Pattern.quote("Director: "), "");
			directorList.add(new Director(directorNameText, null));
			return directorList;
		} else
			return new ArrayList<>();
	}

	@Nonnull
    @Override
	public Studio scrapeStudio() {
		Element studioElement = document.select(
			"div.row.movie p:contains(Studio:) ~ p a, div.row.movie p:contains(メーカー:) ~ p a").first();
		if (studioElement != null) {
			String studioText = studioElement.text().trim();
			studioText = studioText.replaceFirst(Pattern.quote("Studio: "), "");
			studioText = studioText.replaceFirst(Pattern.quote("メーカー: "), "");
			return new Studio(studioText);
		} else
			return Studio.BLANK_STUDIO;
	}

	@Nonnull
    @Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		return createSearchStringFromId(findIDTagFromFile(file, isFirstWordOfFileIsID()));
	}
        
	@Override
	public String createSearchStringFromId(String Id) {
		URLCodec codec = new URLCodec();
		try {
			String fileNameURLEncoded = codec.encode(Id);
			// String searchTerm = "http://www.javdog.com/" + siteLanguageToScrape +
			// "/search/" + fileNameURLEncoded;
			String searchTerm = String.format(
				"http://avmoo.website/%s/search/%s", getUrlLanguageToUse(), fileNameURLEncoded);

			return searchTerm;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String getUrlLanguageToUse() {
		String urlLanguageToUse = (scrapingLanguage == Language.ENGLISH) ? urlLanguageEnglish : urlLanguageJapanese;
		return urlLanguageToUse;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		LinkedList<SearchResult> linksList = new LinkedList<>();
		try {
			Document doc = Jsoup.connect(searchString).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
			{
				Elements divVideoLinksElements = doc.select("div.item:has(a[href*=/movie/])");

				for (Element currentDivVideoLink : divVideoLinksElements) {
					Element videoLinksElements = currentDivVideoLink.select("a[href*=/movie/]").last();
					String idFromSearchResult = currentDivVideoLink.select("span").first().text();
					String currentLink = videoLinksElements.attr("href");

					String linkPrefixPath = currentLink.substring(0, currentLink.lastIndexOf('/'));
					String linkToken = currentLink.substring(currentLink.lastIndexOf('/') + 1);
					
					URLCodec codec = new URLCodec();
					if(!currentLink.startsWith("http")){
						try {
							currentLink = "https:" + linkPrefixPath + "/" + codec.encode(linkToken);
						} catch (EncoderException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					String currentLabel = idFromSearchResult + " " + videoLinksElements.text();
					String currentThumb = currentDivVideoLink.select("img").first().attr("src");

					if (currentLink.length() > 1) {
						SearchResult searchResult = new SearchResult(currentLink, currentLabel, new Thumb(currentThumb));

						//maybe we can improve search accuracy by putting our suspected best match at the front of the array
						//we do this by examining the ID from the search result and seeing if it was in our initial search string
						if (searchString.contains(idFromSearchResult) || searchString.contains(idFromSearchResult.replaceAll(Pattern.quote("-"), "")))
							linksList.addFirst(searchResult);
						else
							linksList.addLast(searchResult);
					}
				}

				return linksList.toArray(new SearchResult[linksList.size()]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new SearchResult[0];
		}
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		ArrayList<Thumb> imageList = new ArrayList<>();

		Elements sampleBoxImageLinks = document.select("div.sample-box li a[href]");
		if (sampleBoxImageLinks != null) {
			for (Element link : sampleBoxImageLinks)
				try {
					imageList.add(new Thumb(link.attr("href")));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
		}

		return imageList.toArray(new Thumb[imageList.size()]);
	}

	@Override
	public String toString() {
		return "AvMoo";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new AvMooParsingProfile();
	}

	@Override
	public String getParserName() {
		return "AvMoo";
	}

}
