package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.github.youngerdryas89.moviescraper.scraper.UserAgent;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.youngerdryas89.moviescraper.controller.languagetranslation.JapaneseCharacter;
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language;
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.TranslateString;
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

import static org.jsoup.Jsoup.newSession;

public class JavBusParsingProfile extends SiteParsingProfile implements SpecificProfile {

	public static final String urlLanguageEnglish = "en";
	public static final String urlLanguageJapanese = "ja";
	//JavBus divides movies into two categories - censored and uncensored.
	//All censored movies need cropping of their poster
	private boolean isCensoredSearch = true;
	private Document japaneseDocument;

    Connection session = newSession()
            .userAgent(UserAgent.getRandomUserAgent())
            .ignoreHttpErrors(true)
            .timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE)
            .followRedirects(true)
            .cookie("dv", "1")
            .cookie("age", "verified")
            .cookie("existmag", "mag");


	@Override
	public List<ScraperGroupName> getScraperGroupNames() {
		if (groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP);
		return groupNames;
	}

	private void initializeJapaneseDocument() {
		if (japaneseDocument == null) {
			String urlOfCurrentPage = document.location();
			if (urlOfCurrentPage != null && urlOfCurrentPage.contains("/en/")) {
				//the genres are only available on the japanese version of the page
				urlOfCurrentPage = urlOfCurrentPage.replaceFirst(Pattern.quote("http://www.javbus.com/en/"), "http://www.javbus.com/ja/");
				if (urlOfCurrentPage.length() > 1) {
					try {
						japaneseDocument = Jsoup.connect(urlOfCurrentPage).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (document != null)
				japaneseDocument = document;
		}
	}

	@Nonnull
    @Override
	public Title scrapeTitle() {
		Element titleElement = document.select("title").first();
		if (titleElement != null) {
			String titleText = titleElement.text();
			titleText = titleText.replace("- JavBus", "");
			//Remove the ID from the front of the title
			if (titleText.contains(" "))
				titleText = titleText.substring(titleText.indexOf(" "), titleText.length());
			//Translate the element using google translate if needed
			// FIXME: Broken
			/*if (scrapingLanguage == Language.ENGLISH && JapaneseCharacter.containsJapaneseLetter(titleText))
				titleText = TranslateString.translateStringJapaneseToEnglish(titleText);*/
			return new Title(titleText);
		} else
			return new Title("");
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
		String seriesWord = (scrapingLanguage == Language.ENGLISH) ? "Series:" : "シリーズ:";
		Element setElement = document.select("span.header:containsOwn(" + seriesWord + ") ~ a").first();
		if (setElement != null && setElement.text().length() > 0) {
			String setText = setElement.text();
			// FIXME: Broken
			/*if (scrapingLanguage == Language.ENGLISH && JapaneseCharacter.containsJapaneseLetter(setText)) {
				setText = TranslateString.translateStringJapaneseToEnglish(setText);
			}*/
			return new Set(setText);
		}
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
		String releaseDateWord = (scrapingLanguage == Language.ENGLISH) ? "Release Date:" : "発売日:";
		Element releaseDateElement = document.select("p:contains(" + releaseDateWord + ")").first();
		if (releaseDateElement != null && releaseDateElement.ownText().trim().length() > 4) {
			String releaseDateText = releaseDateElement.ownText().trim();
			return new ReleaseDate(releaseDateText);
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
		String lengthWord = (scrapingLanguage == Language.ENGLISH) ? "Length:" : "収録時間:";
		Element lengthElement = document.select("p:contains(" + lengthWord + ")").first();
		if (lengthElement != null && lengthElement.ownText().trim().length() >= 0) {
			//Getting rid of the word "min" in both Japanese and English
			String runtimeText = lengthElement.ownText().trim().replace("min", "");
			runtimeText = runtimeText.replace("分", "");
			return new Runtime(runtimeText);
		}
		return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters(boolean cropPosters) {
		// TODO: crop posters for this scraper
		return scrapePostersAndFanart(true);
	}

	@Override
	public Thumb[] scrapeFanart() {
		return scrapePostersAndFanart(false);
	}

	private Thumb[] scrapePostersAndFanart(boolean isPosterScrape) {
		Element posterElement = document.select("a.bigImage").first();
		if (posterElement != null) {
			try {
                var imgResponse = downloadDocumentFromUrl("https://www.javbus.com" + posterElement.attr("href"));
                if(imgResponse.statusCode() != 200){
                    System.err.println("Error failed to download image: " + imgResponse.url() + "; " + imgResponse.statusCode() + " " + imgResponse.statusMessage());
                    return new Thumb[0];
                }


				Thumb posterImage = new Thumb(imgResponse.url().toString(), imgResponse.bodyAsBytes(), (isCensoredSearch && isPosterScrape));
				Thumb[] posterArray = { posterImage };
				return posterArray;
			} catch (IOException e) {
				e.printStackTrace();
				return new Thumb[0];
			}
		} else
			return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		Elements extraFanartElements = document.select("div.sample-box ul li a");
		if (extraFanartElements != null && extraFanartElements.size() > 0) {
			Thumb[] extraFanart = new Thumb[extraFanartElements.size()];
			int i = 0;
			for (Element extraFanartElement : extraFanartElements) {
				String href = extraFanartElement.attr("href");
				try {
					extraFanart[i] = new Thumb(href);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
			return extraFanart;
		}
		return new Thumb[0];
	}

	@Nonnull
    @Override
	public MPAARating scrapeMPAA() {
		return new MPAARating("XXX");
	}

	@Nonnull
    @Override
	public ID scrapeID() {
		Element idElement = document.select(".movie .info span + span").first();
		if (idElement != null)
			return new ID(idElement.text());
		else
			return ID.BLANK_ID;
	}

	@Nonnull
    @Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<>();
		Elements genreElements = document.select("span.genre");
		if (genreElements != null) {
			for (Element genreElement : genreElements) {
				String genreText = genreElement.text();
				if (genreElement.text().length() > 0) {
					//some genre elements are untranslated, even on the english site, so we need to do it ourselves
					// FIXME: Broken
					/*if (scrapingLanguage == Language.ENGLISH && JapaneseCharacter.containsJapaneseLetter(genreText)) {
						genreText = TranslateString.translateStringJapaneseToEnglish(genreText);
					}*/
					genreList.add(new Genre(WordUtils.capitalize(genreText)));
				}
			}
		}
		return genreList;
	}

	@Nonnull
    @Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<>();
		Elements actorElements = document.select("div.star-box li a img");
		if (actorElements != null) {
			for (Element currentActor : actorElements) {
				Thumb thumbnail = null;
				String actorName = currentActor.attr("title");
				//Sometimes for whatever reason the english page still has the name in japanaese, so I will translate it myself
				// FIXME: Broken
				/*if (scrapingLanguage == Language.ENGLISH && JapaneseCharacter.containsJapaneseLetter(actorName))
					actorName = TranslateString.translateJapanesePersonNameToRomaji(actorName);*/
				String actorImage = currentActor.attr("src");
				if (actorImage != null && !actorImage.contains("printing.gif") && fileExistsAtURL(actorImage)) {

					try {
						thumbnail = new Thumb(new URL(actorImage));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				actorList.add(new Actor(actorName, null, thumbnail));
			}
		}
		return actorList;
	}

	@Nonnull
    @Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directorList = new ArrayList<>();
		String directorWord = (scrapingLanguage == Language.ENGLISH) ? "Director:" : "監督:";
		Element directorElement = document.select("span.header:containsOwn(" + directorWord + ") ~ a").first();
		if (directorElement != null && directorElement.text().length() > 0) {
			directorList.add(new Director(directorElement.text(), null));
		}
		return directorList;
	}

	@Nonnull
    @Override
	public Studio scrapeStudio() {
		String studioWord = (scrapingLanguage == Language.ENGLISH) ? "Studio:" : "メーカー:";
		Element studioElement = document.select("span.header:containsOwn(" + studioWord + ") ~ a").first();
		if (studioElement != null && studioElement.text().length() > 0) {
			return new Studio(studioElement.text());
		}
		return Studio.BLANK_STUDIO;
	}

	@Nonnull
    @Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		return createSearchStringFromId(findIDTagFromFile(file, isFirstWordOfFileIsID()));
	}
        
        @Override
        public String createSearchStringFromId(String Id){
            URLCodec codec = new URLCodec();
		try {
			String fileNameURLEncoded = codec.encode(Id);
			String searchTerm = "http://www.javbus.com/" + getUrlLanguageToUse() + "/search/" + fileNameURLEncoded;
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
		ArrayList<SearchResult> linksList = new ArrayList<>();
		try {
            var doc = downloadDocumentFromUrl(searchString).parse();
			Elements videoLinksElements = doc.select("div.item");
			if (videoLinksElements.isEmpty()) {
				doc = downloadDocumentFromUrl(searchString.replace("/search/", "/uncensored/search/")).parse();
				isCensoredSearch = false;
			}
			videoLinksElements = doc.select("div.item");
            for (Element videoLink : videoLinksElements) {
                String currentLink = videoLink.select("a").attr("href");
                String currentLinkLabel = videoLink.select("a").text().trim();
                String currentLinkImage = "https://www.javbus.com" + videoLink.select("img").attr("src");
                if (currentLink.length() > 1) {
                    linksList.add(new SearchResult(currentLink, currentLinkLabel, new Thumb(currentLinkImage)));
                }
            }
            return linksList.toArray(new SearchResult[linksList.size()]);
		}

		catch (IOException e) {
			e.printStackTrace();
			return new SearchResult[0];
		}
	}

    @Override
    public Connection.Response downloadDocumentFromUrl(String url) throws IOException {
        try {
            return session.newRequest().url(url).ignoreHttpErrors(true).execute();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
	public SiteParsingProfile newInstance() {
		return new JavBusParsingProfile();
	}

	@Override
	public String getParserName() {
		return "JavBus";
	}

}
