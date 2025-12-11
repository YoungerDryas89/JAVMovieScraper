package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.github.youngerdryas89.moviescraper.scraper.UserAgent;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfile;
import com.github.youngerdryas89.moviescraper.model.Movie;
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

public class ExcaliburFilmsParsingProfile extends SiteParsingProfile implements SpecificProfile {

    String movieId;
    Connection session = newSession()
            .userAgent(UserAgent.getRandomUserAgent())
            .ignoreHttpErrors(true)
            .timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE)
            .followRedirects(true)
            .cookie("ITEMPERPAGE", "75")
            .cookie("MYPERPAGE", "75")
            .cookie("SELECTEDFORMAT", "AdultDVDMovies");
    Map<String, String[]> studioNames;

    public ExcaliburFilmsParsingProfile(){
        try {
            studioNames = new HashMap<>();

            CSVFormat csvf = CSVFormat.DEFAULT.builder()
                    .setHeader(new String[]{
                            "Original Studio Name", "Common Filename Variations", "Acronym"
                    })
                    .build();

            var in = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("excalibur-studios.csv"));

            Iterable<CSVRecord> records = csvf.parse(in);
            for(var record : records){
                var name = record.get("Original Studio Name");
                var aliases = record.get("Common Filename Variations");
                studioNames.put(name, aliases.split(","));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

	@Override
	public List<ScraperGroupName> getScraperGroupNames() {
		if (groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP);
		return groupNames;
	}

	@Nonnull
    @Override
	public Title scrapeTitle() {
		Element titleElement = document.select("title").first();
		if (titleElement != null) {
			String titleText = titleElement.text();
			titleText = titleText.replaceFirst("Adult DVD", "");
			titleText = titleText.replaceFirst("Blu-Ray", "");
			return new Title(titleText);
		}
		return new Title("");
	}

	@Nonnull
    @Override
	public OriginalTitle scrapeOriginalTitle() {
		return new OriginalTitle(scrapeTitle().getTitle());
	}

	@Nonnull
    @Override
	public SortTitle scrapeSortTitle() {
		return SortTitle.BLANK_SORTTITLE;
	}

	@Nonnull
    @Override
	public Set scrapeSet() {
		//Excalibur doesn't have set info
		return Set.BLANK_SET;
	}

	@Nonnull
    @Override
	public Rating scrapeRating() {
		//Excalibur doesn't have rating info
		return Rating.BLANK_RATING;
	}

	@Nonnull
    @Override
	public ReleaseDate scrapeReleaseDate() {
		Element releaseDateElement = document.select("font:containsOwn(Released:) + font").first();
		if (releaseDateElement != null) {
			ReleaseDate releaseDate = new ReleaseDate(releaseDateElement.text(), new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH));
			return releaseDate;
		}
		return ReleaseDate.BLANK_RELEASEDATE;
	}

	@Nonnull
    @Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}

	@Nonnull
    @Override
	public Top250 scrapeTop250() {
		//Excalibur doesn't have this info
		return Top250.BLANK_TOP250;
	}

	@Nonnull
    @Override
	public Votes scrapeVotes() {
		//Excalibur doesn't have this info
		return Votes.BLANK_VOTES;
	}

	@Nonnull
    @Override
	public Outline scrapeOutline() {
		//Excalibur doesn't have this info
		return Outline.BLANK_OUTLINE;
	}

	@Nonnull
    @Override
	public Plot scrapePlot() {
		Element plotElement = document.select("a:has(font b:containsOwn(Description:)) + font").first();
		if (plotElement != null) {
			String plotElementText = plotElement.text().trim();
			//They like to include their plot descriptions within quotes, so we can remove those quotes
			if (plotElementText.startsWith("\"") && plotElementText.endsWith("\"") && plotElementText.length() > 2) {
				plotElementText = plotElementText.substring(1, plotElementText.length() - 1);
			}
			return new Plot(plotElementText);
		}
		return Plot.BLANK_PLOT;
	}

	@Nonnull
    @Override
	public Tagline scrapeTagline() {
		//Excalibur doesn't have this information
		return Tagline.BLANK_TAGLINE;
	}

	@Nonnull
    @Override
	public Runtime scrapeRuntime() {
		Element runtimeElement = document.select("font:containsOwn(Run Time:) + font").first();
		if (runtimeElement != null) {
			String runtimeText = runtimeElement.text().replace(" min.", "");
			return new Runtime(runtimeText);
		}
		return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters(boolean cropPosters) {
        try {
            List<Thumb> posters = new ArrayList<>();
            if(fileExistsAtURL(getPosterPathFromIDString(movieId))) {
                var thumb = new Thumb(getPosterPathFromIDString(movieId));
                posters.add(thumb);
                if(fileExistsAtURL(getBackPosterPathFromIDString(movieId))){
                    var back = new Thumb(getBackPosterPathFromIDString(movieId));
                    posters.add(back);
                }

                return posters.toArray(new Thumb[posters.size()]);
            } else {

                var imageUrl = document.select("center > img").first();
                var thumb = new Thumb(imageUrl.attr("src"), false);
                posters.add(thumb);

                return posters.toArray(new Thumb[posters.size()]);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return new Thumb[0];
	}

    private String getPosterPathFromIDString(String movieID) {
        if (movieID == null)
            throw new RuntimeException("getPosterPathFromIDString: `movieID` is null");

        return "http://images.excaliburfilms.com/DVD/reviews/imagesBB020609/largemoviepic/dvd_" + movieID + ".jpg";
    }


    private String getBackPosterPathFromIDString(String movieID) {
        if (movieID == null)
            throw new RuntimeException("getPosterPathFromIDString: `movieID` is null");

        return "http://images.excaliburfilms.com/DVD/reviews/imagesBB020609/largemoviepic/dvd_" + movieID + "-b.jpg";
    }

	private String getPosterPreviewPathFromIDString(String movieID) {
		if (movieID == null)
			return null;
		return "http://images.excaliburfilms.com/dvd/dvdicon2/dvd_" + movieID + ".jpg";
	}

	@Override
	public Thumb[] scrapeFanart() {
		//No Fanart on this site
		return new Thumb[0];
	}

	/**
	 * We return the back cover as the extrafanart for Excalibur Films
	 */
	@Override
	public Thumb[] scrapeExtraFanart() {
		String movieID = scrapeID().getId();
		String thumbPath = "http://images.excaliburfilms.com/DVD/reviews/imagesBB020609/largemoviepic/dvd_" + movieID + "-b.jpg";
		try {
			Thumb posterThumb = new Thumb(thumbPath);
			Thumb[] thumbsToReturn = { posterThumb };
			return thumbsToReturn;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return new Thumb[0];
		}
	}

	@Nonnull
    @Override
	public MPAARating scrapeMPAA() {
		Element mpaaRatingElement = document.select("font:containsOwn(Rated:) + font a").first();
		if (mpaaRatingElement != null) {
			String mpaaRatingText = mpaaRatingElement.text();
			return new MPAARating(mpaaRatingText);
		}
		return MPAARating.BLANK_RATING;
	}

	@Nonnull
    @Override
	public ID scrapeID() {
		String id = getIDStringFromDocumentLocation(document);
		if (id != null) {
            this.movieId = id;
			return new ID(id);
		}
		return ID.BLANK_ID;
	}

	private String getIDStringFromDocumentLocation(Document doc) {
		if (doc != null) {
			String id = doc.location();
			if (id.contains("/") && id.contains("_") && id.contains(".htm")) {
				id = id.substring(id.lastIndexOf('/') + 1, Math.min(id.indexOf('_'), id.length()));
				return id;
			}
		}
		return null;
	}

	@Nonnull
    @Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<>();
		Element genreElement = document.select("font:containsOwn(Fetish:) + a").first();
		if (genreElement != null) {
			String genreText = genreElement.text();
			if (genreText.length() > 0 && !genreText.equals("BluRay")) {
				genreList.add(new Genre(genreText));
			}
		}
		return genreList;
	}

	@Nonnull
    @Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<>();
		Element firstActorList = document.select("font:containsOwn(Starring:) + font").first();
		Elements actorListElements = firstActorList.select("a");
		for (Element currentActor : actorListElements) {
			String actorName = currentActor.text();
			String pageName = currentActor.attr("href");
			Thumb actorThumb = null;
			if (pageName.contains("/starpgs/") || pageName.contains("/malepgs/")) {
				actorThumb = getThumbForPersonPageUrl(pageName);
			}
			if (actorThumb != null) {
				Actor currentActorToAdd = new Actor(actorName, "", actorThumb);
				actorList.add(currentActorToAdd);
			} else {
				Actor currentActorToAdd = new Actor(actorName, "", null);
				if (actorName.trim().length() > 0)
					actorList.add(currentActorToAdd);
			}
		}
		//get no image actors
		String firstActorListText = firstActorList.ownText();
		if (firstActorListText.length() > 0) {
			String currentActorTextSplitByComma[] = firstActorListText.trim().split(",");
			for (String currentNoThumbActor : currentActorTextSplitByComma) {
				String actorName = currentNoThumbActor.trim();
				//last actor in the list has a period since the list is in sentence form, so we want to get rid of that
				if (actorName.endsWith("."))
					actorName = actorName.substring(0, actorName.length() - 1);
				//we already have some of the actors if they were added in the thumb version, so check before adding them again
				boolean hadThisActorAlready = false;
				for (Actor existingActor : actorList) {
					if (existingActor.getName().equals(actorName))
						hadThisActorAlready = true;
				}
				if (!hadThisActorAlready && actorName.trim().length() > 0)
					actorList.add(new Actor(actorName, "", null));
			}
		}
		return actorList;
	}

	private Thumb getThumbForPersonPageUrl(String personPageUrl) {
		String actorFromPageName = personPageUrl.substring(personPageUrl.lastIndexOf("/"), personPageUrl.length()).replace(".htm", "");
		String actorThumbURL = "http://Images.ExcaliburFilms.com/pornlist/starpicsAA020309" + actorFromPageName + ".jpg";
		Thumb actorThumb = null;
		try {
			actorThumb = new Thumb(actorThumbURL);
		} catch (MalformedURLException e) {
			return null;
		}
		return actorThumb;
	}

	@Nonnull
    @Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directorList = new ArrayList<>();
		Element directorElement = document.select("font:containsOwn(Director:) + a").first();
		if (directorElement != null) {
			String directorName = directorElement.text();
			String directorPageURL = directorElement.attr("href");
			Thumb directorThumb = null;
			if (directorPageURL != null) {
				directorThumb = getThumbForPersonPageUrl(directorPageURL);
			}
			Director directorToAdd = new Director(directorName, directorThumb);
			directorList.add(directorToAdd);
		}
		return directorList;
	}

	@Nonnull
    @Override
	public Studio scrapeStudio() {
		Element studioElement = document.select("font:containsOwn(By:) + a").first();
		if (studioElement != null) {
			String studioText = studioElement.text();
			return new Studio(studioText);
		}
		return Studio.BLANK_STUDIO;
	}

	@Nonnull
    @Override
	public String createSearchString(File file) {
		String fileBaseName = cleanseFilename(file);
        return fileBaseName;
	}

	@Override
	public String createSearchStringFromId(String id) {
		return null;
	}

    @NotNull
    public Connection createRequestWithPageN(@NotNull  String searchString, @Nullable String referer, int page){
        try {
            URLCodec codec = new URLCodec();
            var request =
                    session.newRequest()
                            .url("https://www.excaliburfilms.com/search/adultSearch.htm")
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                            .header("Accept-Language", "en-US,en;q=0.5")
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .header("Origin", "https://www.excaliburfilms.com")
                            .header("Connection", "keep-alive")
                            .header("Upgrade-Insecure-Requests", "1")
                            .header("Sec-Fetch-Dest", "document")
                            .header("Sec-Fetch-Mode", "navigate")
                            .header("Sec-Fetch-Site", "same-origin")
                            .header("Sec-Fetch-User", "?1")
                            .data("sortBy", "title")
                            .data("searchCT", "ALL")
                            .data("searchSN", "")
                            .data("searchST", "")
                            .data("searchKW", "")
                            .data("searchStar", "")
                            .data("searchWord", searchString)
                            .data("Year_In", "")
                            .data("searchFor", "Title.x")
                            .data("LetterIn", "")
                            .method(Connection.Method.POST);

            if(page > 0){
                request = request.data("PaginationPage", Integer.toString(page));
            }

            if(referer != null)
                request = request.referrer(referer);
            else
                request = request.referrer("http://www.excaliburfilms.com/search/adultSearch.htm?searchString=" + codec.encode(searchString) + "&Case=ExcalMovies&Search=AdultDVDMovies&SearchFor=Title.x");
            return request;
        } catch (EncoderException e) {
            throw new RuntimeException(e);
        }
    }

    void randomWait(){
        try {
            var waitTime = (long) (Math.random() * (3 - 7)) + 1;
            TimeUnit.SECONDS.sleep(waitTime);
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    List<SearchResult> extractSearchResults(Document doc){
        List<SearchResult> results = new ArrayList<>();
        try {
            boolean onSearchResultsPage = doc.location().contains("adultSearch.htm");
            //found the movie without a search results page
            if (doc.location() != null && !onSearchResultsPage) {
                String idOfPage = getIDStringFromDocumentLocation(doc);
                String posterPath = getPosterPreviewPathFromIDString(idOfPage);
                String label = doc.select("title").first().text();
                Thumb previewImage = new Thumb(posterPath);
                //SearchResult directResult = new SearchResult(doc.location());
                SearchResult result = null;
                if (posterPath != null)
                    result = new SearchResult(doc.location(), label, previewImage);
                else
                    result = new SearchResult(doc.location(), label, null);

                results.add(result);
                return results;
            }

            //This selector in particular tends to break when they update their site.
            //Unfortunately, they don't use things like ids or classes much which makes it hard to get the right element without resorting to
            //hackery like width=600 stuff
            Elements foundMovies = doc.select(".searchTitle18");

            for (Element movie : foundMovies) {
                Element parent = movie.parent().parent().parent();
                String urlPath = movie.select("a").first().attr("href");
                String thumb = parent.select("img").first().attr("src");
                String label = parent.select("img").first().attr("alt");
                SearchResult searchResult = new SearchResult(urlPath, label, new Thumb(thumb));
                if (!results.contains(searchResult))
                    results.add(searchResult);
            }
            return results;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
        var request = createRequestWithPageN(searchString, null, 0);
        var response = request.execute();

        if (response.statusCode() == 200) {
            var doc = response.parse();
            var results = extractSearchResults(doc);
            if(!doc.select("div.pagination").isEmpty()){
                var totalPages = Integer.valueOf(doc.select("div.pagination > a")
                        .stream()
                        .filter(e -> {
                            return !e.hasAttr("class");
                        }).toList().getLast().text());

                for(int i = 2; i < totalPages; i++){
                    var referrer = (i > 2? searchString : searchString + "&PaginationPage=" + i);
                    var newRequest = createRequestWithPageN(searchString, referrer, i);
                    randomWait();
                    var newResponse = newRequest.execute();
                    if(newResponse.statusCode() == 200){
                        var doc_ = newResponse.parse();
                        var results_ = extractSearchResults(doc_);
                        if(results_.isEmpty())
                            System.err.println("WARNING: " + newResponse.url() + " returned no results.");
                        else
                            results.addAll(results_);

                    }
                }

            }
            return results.toArray(new SearchResult[results.size()]);
        } else {
            System.err.println(response.statusCode() + " " + response.statusMessage());
        }

        return new SearchResult[0];
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new ExcaliburFilmsParsingProfile();
	}

	@Override
	public String getParserName() {
		return "Excalibur Films";
	}

    @Override
    public String cleanseFilename(File file){
        String fileBaseName;
        if (file.isFile())
            fileBaseName = FilenameUtils.getBaseName(Movie.getUnstackedMovieName(file));
        else
            fileBaseName = file.getName();
        fileBaseName = fileBaseName.replaceAll("(19\\d\\d|20\\d\\d)", "");
        fileBaseName = fileBaseName.replaceAll("[()\\[\\]\\.\\-_#]", "");
        fileBaseName = fileBaseName.replaceAll("(1080p?|720p?|480p?|360p?|240p?|144p?|4k|4K|2160p?)", "");

        for(var name : studioNames.keySet()){
            fileBaseName = fileBaseName.replaceAll(name, "");
            for(var alias : studioNames.get(name)){
                fileBaseName = fileBaseName.replaceAll(alias, "");
            }
        }
        return fileBaseName;
    }

}

