package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import com.github.youngerdryas89.moviescraper.model.preferences.MoviescraperPreferences;
import com.github.youngerdryas89.moviescraper.scraper.UserAgent;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language;
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
    MoviescraperPreferences prefs = MoviescraperPreferences.getInstance();

    Connection session = newSession()
            .userAgent(UserAgent.getRandomUserAgent())
            .ignoreHttpErrors(true)
            .timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE)
            .followRedirects(true)
            .cookie("dv", "1")
            .cookie("age", "verified");


	@Override
	public List<ScraperGroupName> getScraperGroupNames() {
		if (groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP);
		return groupNames;
	}

	@Nonnull
    @Override
	public Title scrapeTitle() {
		Element titleElement = document.select("h3").first();
		if (titleElement != null) {
			String titleText = titleElement.text();

			//Remove the ID from the front of the title
			if (!prefs.appendIDToStartOfTitle && titleText.contains(" "))
				titleText = titleText.substring(titleText.indexOf(" ")).trim();

			return new Title(titleText);
		}
        return Title.BLANK_TITLE;
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
		Element setElement = document.select("span.header:matchesOwn((?i)Series:|シリーズ:|시리즈:|系列:) + a").first();
		if (setElement != null && !setElement.text().isEmpty())
			return new Set(setElement.text());
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
		Element releaseDateElement = document.select("p:matches(Release Date:|発売日:|출시일:|發行日期:)").first();
		if (releaseDateElement != null && releaseDateElement.ownText().trim().length() > 4)
			return new ReleaseDate(releaseDateElement.ownText().trim());
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
		Element lengthElement = document.select("p:contains(Length:|収録時間:|길이:|長度:)").first();
		if (lengthElement != null && !lengthElement.ownText().trim().isEmpty()) {
			//Getting rid of the word "min" in both Japanese and English
			String runtimeText = lengthElement.ownText().trim().replace("min", "");
			runtimeText = runtimeText.replace("分", "");
			return new Runtime(runtimeText);
		}
		return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters(boolean cropPosters) {
        var images = document.select("a.bigImage").stream()
                .map(posterElem -> posterElem.attr("href"))
                .map(this::downloadImage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(response -> {
                    if(response.statusCode() != 200)
                        System.err.println("JavBus scraper error: Couldn't fetch image: " + response.url().toString() + "; " + response.statusCode() + " " + response.statusMessage());
                })
                .filter(res -> res.statusCode() == 200)
                .map(resp -> new Thumb(resp.url().toString(), resp.bodyAsBytes(), (isCensoredSearch && cropPosters)))
                .toList();
        return images.toArray(new Thumb[images.size()]);
	}

	@Override
	public Thumb[] scrapeFanart() {

        var images = document.select("a.sample-box").stream()
                .map((urlElem) -> urlElem.attr("href"))
                .map(this::downloadImage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(response -> {
                    if(response.statusCode() != 200)
                        System.err.println("JavBus scraper error: Couldn't fetch image: " + response.url().toString() + "; " + response.statusCode() + " " + response.statusMessage());
                })
                .filter(res -> res.statusCode() == 200)
                .map(resp -> new Thumb(resp.url().toString(), resp.bodyAsBytes(), false))
                .toList();
        return images.toArray(new Thumb[images.size()]);
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
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

        genreElements.stream().map(Element::text).map(WordUtils::capitalize).map(Genre::new).toList();
		if (!genreElements.isEmpty()) {
			for (Element genreElement : genreElements) {
				String genreText = genreElement.text();
				if (!genreElement.text().isEmpty()) {
					genreList.add(new Genre(WordUtils.capitalize(genreText)));
				}
			}
		}
		return genreList;
	}

	@Nonnull
    @Override
	public ArrayList<Actor> scrapeActors() {
		var actresses = document.select("div.star-box li a img").stream()
                .map(elem -> {
                    String name = elem.attr("title");
                    String imageURL = elem.attr("src");
                    Thumb thumb;
                    if(!imageURL.contains("printing.gif") && !imageURL.isEmpty()) {
                        var image = downloadImage(imageURL);
                        if(image.isPresent()) {
                            thumb = new Thumb(image.get().url().toString(), image.get().bodyAsBytes(), false);
                            return new Actor(name, null, thumb);
                        }
                    }
                    return new Actor(name, null, null);
                }).toList();
		return new ArrayList<Actor>(actresses);
	}

	@Nonnull
    @Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directorList = new ArrayList<>();
		Element directorElement = document.select("span.header:containsOwn(Director:|監督:|관리자:|導演:) ~ a").first();
		if (directorElement != null && !directorElement.text().isEmpty())
			directorList.add(new Director(directorElement.text(), null));
		return directorList;
	}

	@Nonnull
    @Override
	public Studio scrapeStudio() {
		Element studioElement = document.select("span.header:matchesOwn(Studio:|メーカー:|메이커:|製作商:) ~ a").first();
		if (studioElement != null && !studioElement.text().isEmpty())
			return new Studio(studioElement.text());
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
            return "http://www.javbus.com/" + getUrlLanguageToUse() + "/search/" + fileNameURLEncoded;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
        }

	private String getUrlLanguageToUse() {
        return (scrapingLanguage == Language.ENGLISH) ? urlLanguageEnglish : urlLanguageJapanese;
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
            return session.newRequest().url(url).ignoreHttpErrors(true).ignoreContentType(true).execute();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
        return null;
    }

    Optional<Connection.Response> downloadImage(String url){
        try {
            var url_ = new URL(url);
            var response = session.newRequest()
                    .header("Accept", "image/avif,image/webp,image/png,image/svg+xml,image/*;q=0.8,*/*;q=0.5")
                    .header("Accept-Language", "en-US,en;q=0.5")
                    .header("Host", url_.getHost())
                    .header("referer", document.location())
                    .header("Sec-Fetch-Dest", "image")
                    .header("Sec-Fetch-Mode", "no-cors")
                    .method(Connection.Method.GET)
                    .url(url_)
                    .ignoreContentType(true)
                    .followRedirects(true);
            if(document.location().contains(url_.getHost()) ){
                response = response.header("Sec-Fetch-Site", "same-origin");
            } else {
                response = response.header("Sec-Fetch-Site", "cross-site");
            }
            return Optional.of(response.execute());
        } catch (MalformedURLException e){
           return downloadImage("https://www.javbus.com" + url);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
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
