package moviescraper.doctord.controller.siteparsingprofile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import moviescraper.doctord.scraper.UserAgent;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.controller.AbstractMovieScraper;
import moviescraper.doctord.controller.GenericMovieScraper;
import moviescraper.doctord.controller.languagetranslation.Language;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.DataItemSource;
import moviescraper.doctord.model.dataitem.Director;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.ID;
import moviescraper.doctord.model.dataitem.MPAARating;
import moviescraper.doctord.model.dataitem.OriginalTitle;
import moviescraper.doctord.model.dataitem.Outline;
import moviescraper.doctord.model.dataitem.Plot;
import moviescraper.doctord.model.dataitem.Rating;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Set;
import moviescraper.doctord.model.dataitem.SortTitle;
import moviescraper.doctord.model.dataitem.Studio;
import moviescraper.doctord.model.dataitem.Tag;
import moviescraper.doctord.model.dataitem.Tagline;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Top250;
import moviescraper.doctord.model.dataitem.Trailer;
import moviescraper.doctord.model.dataitem.Votes;
import moviescraper.doctord.model.dataitem.Year;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;
import moviescraper.doctord.view.GUIMain;

public abstract class SiteParsingProfile implements DataItemSource {

	/*
	 * Any group of SiteParsingProfiles which return the same type of information for a given file and which
	 * will be compatible for amalgamation should return the same ScraperGroupName by implementing getScraperGroupName()
	 */
	public enum ScraperGroupName {
		JAV_CENSORED_SCRAPER_GROUP {
			@Override
			public String toString() {
				return "JAV Censored Group";
			}
		},
		AMERICAN_ADULT_DVD_SCRAPER_GROUP {
			@Override
			public String toString() {
				return "American Adult DVD";
			}
		},
		DEFAULT_SCRAPER_GROUP {
			@Override
			public String toString() {
				return "Default Group";
			}
		}
	}

	public List<ScraperGroupName> getScraperGroupNames() {
		if (groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.DEFAULT_SCRAPER_GROUP);
		return groupNames;
	}

	protected List<ScraperGroupName> groupNames;

	protected Language scrapingLanguage;

	public Document document; // the base page to start parsing from

	@Deprecated
	public String overrideURLDMM; //TODO: no longer used variable - will be removed later

	private boolean extraFanartScrapingEnabled = false;

	MoviescraperPreferences scrapingPreferences;

	private boolean isDisabled = false;

	private boolean firstWordOfFileIsID = false;

	public static final int CONNECTION_TIMEOUT_VALUE = 13000;

	protected File scrapedMovieFile;

	private ImageIcon profileIcon;
	protected static DetermineMovie dproperties = new DetermineMovie();

	/**
	 * If this has a value when scraping, will use overridenSearchResult
	 * from a user provided URL without looking at file name
	 */
	private SearchResult overridenSearchResult;

	final static Pattern FC2Pattern = Pattern.compile("(?i)(:?FC2-PPV)[-_\\s](?<id>(\\d{7}))");
	final static Pattern OnePondoPattern = Pattern.compile("(?i)(:?1Pondo[-_\\s]?)?(?<id>\\d{6}[_-]\\d{1,3}?(:?-1PON)?)");
	final static Pattern TokyoHotPattern = Pattern.compile("(?i)(:?Tokyo-?Hot)?[-_\\s\\S]?(?<productId>[nk]\\d{4})");
	final static Pattern CaribbeancomPattern = Pattern.compile("(?i)(?<id>(?<series>carib|caribbeancom(pr)?|caribbeancom premium)\\s?[-_\\s]\\s?(?<number>\\d{6}[_-]\\d{3}))");
	final static Pattern CaribbeancomPatternReverse = Pattern.compile("(?i)(?<id>(?<number>\\d{6}[_-]\\d{3})\\s?[-_\\s]\\s?(?<series>carib(pr)?|caribbeancom(pr)?|caribbeancom premium))");
	final static Pattern TenMusumePattern = Pattern.compile("(?i)(?<id>\\d{6}_\\d{2,3})(\\-|_)?10MU");

	final static Pattern avGeneralIdextract = Pattern.compile("(?i)(?<tag>(?:\\d{3,4})?[a-z]+|[a-z]{1,2}\\d+)[^a-z0-9_](?<num>\\d+)");
	final static Pattern kinten8gokuPattern = Pattern.compile("(?i)(?:Kin8tengoku|KIN8)[-_\\s](?<num>\\d+)");
	/**
	 * do we want to ignore scraping from this scraper. typically done when the user has hit cancel from a dialog box because none of the seen results were valid
	 */
	private boolean discardResults;

	public boolean isExtraFanartScrapingEnabled() {
		return extraFanartScrapingEnabled;
	}

	public void setExtraFanartScrapingEnabled(boolean extraFanartScrapingEnabled) {
		this.extraFanartScrapingEnabled = extraFanartScrapingEnabled;
	}

	public String getOverrideURLDMM() {
		return overrideURLDMM;
	}

	public void setOverrideURLDMM(String overrideURL) {
		this.overrideURLDMM = overrideURL;
	}

	public SiteParsingProfile(Document document) {
		this.document = document;
		overrideURLDMM = null;
		scrapingLanguage = Language.ENGLISH;
		scrapingPreferences = MoviescraperPreferences.getInstance();
		setScrapingLanguage(scrapingPreferences);
		this.firstWordOfFileIsID = scrapingPreferences.getIsFirstWordOfFileID();
		this.isDisabled = false;
	}

	public SiteParsingProfile() {
		scrapingLanguage = Language.ENGLISH;
		scrapingPreferences = MoviescraperPreferences.getInstance();
		setScrapingLanguage(scrapingPreferences);
		this.firstWordOfFileIsID = scrapingPreferences.getIsFirstWordOfFileID();
		this.isDisabled = false;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	/**
	 * Sets the {@link SiteParsingProfile#overridenSearchResult} to the URL defined by @param urlPath
	 * This will cause the scraper to ignore the file name of the file when scraping
	 * 
	 * @param urlPath
	 */
	public void setOverridenSearchResult(String urlPath) {
		overridenSearchResult = new SearchResult(urlPath);
		if (SiteParsingProfileJSON.class.isAssignableFrom(this.getClass())) {
			overridenSearchResult.setJSONSearchResult(true);
		}
	}

	/**
	 * @return {@link SiteParsingProfile#overridenSearchResult}
	 */
	public SearchResult getOverridenSearchResult() {
		return overridenSearchResult;
	}

	/**
	 * Gets the ID number from the file and considers stripped out multipart file identifiers like CD1, CD2, etc
	 * The ID number needs to be the last word in the filename or the next to the last word in the file name if the file name
	 * ends with something like CD1 or Disc 1
	 * So this filename "My Movie ABC-123 CD1" would return the id as ABC-123
	 * This filename "My Movie ABC-123" would return the id as ABC-123
	 * 
	 * @param file - file to find the ID tag from
	 * @param firstWordOfFileIsID - if true, just uses the first word in the file (seperated by space) as the ID number
	 * otherwise use the method described above
	 * @return
	 */
	public static String findIDTagFromFile(File file, boolean firstWordOfFileIsID) {
		// TODO: Need something better and more specific than this function
		String fileNameNoExtension;
		if (file.isDirectory()) {
			fileNameNoExtension = file.getName();
		} else
			fileNameNoExtension = FilenameUtils.removeExtension(file.getName());
		if (file.getPath().endsWith(".nfo")) {
			try {
				Movie movie = Movie.createMovieFromNfo(file);
				return movie.getId().getId();
			} catch (IOException ex) {
				System.out.println("Cannot load this file as nfo. Try from filename");
			}

		}

		String id = null;
		Matcher match = CaribbeancomPattern.matcher(fileNameNoExtension);
		if(match.find()){
			assert (match.group("id") != null);
			assert (match.group("series") != null);
			assert (match.group("number") != null);
			if(match.group("series").contains("pr")){
				return "caribbeancompr-" + match.group("number");
			} else {
				return "caribbeancom-" + match.group("number");
			}
		}

		match = CaribbeancomPatternReverse.matcher(fileNameNoExtension);
		if(match.find()){
			assert (match.group("id") != null);
			assert (match.group("series") != null);
			assert (match.group("number") != null);
			if(match.group("series").contains("pr")){
				return "caribbeancompr" + match.group("number");
			} else {
				return "caribbeancom-" + match.group("number");
			}
		}


		match = FC2Pattern.matcher(fileNameNoExtension);
		if(match.find()){
			assert (match.group("id") != null);
			id = match.group("id");
			return "FC2-PPV-" + id;
		}

		match = kinten8gokuPattern.matcher(fileNameNoExtension);
		if(match.find()){
			id = "KIN8-" + match.group("num");
			return id;
		}

		match = OnePondoPattern.matcher(fileNameNoExtension);
		if(match.find()){
			assert (match.group("id") != null);
			id = match.group("id");
			return id;
		}

		match = TenMusumePattern.matcher(fileNameNoExtension);
		if(match.find()){
			assert (match.group("id") != null);
			id = match.group("id");
			return id;
		}

		match = TokyoHotPattern.matcher(fileNameNoExtension);
		if(match.find()){
			assert(match.group("productId") != null);
			id = match.group("productId");
			return id;
		}

        var result = dproperties.determineIdFromTitle(fileNameNoExtension);
        if(result != null){
            return result.getKey() + "-" + result.getValue();
        }

		match = avGeneralIdextract.matcher(fileNameNoExtension);
		if(match.find()){
			assert(match.group("tag") != null);
			assert(match.group("num") != null);

			id = match.group("tag") + "-" + match.group("num");
		}
		return id;
	}

	public static String stripDiscNumber(String fileNameNoExtension) {
		//replace <cd/dvd/part/pt/disk/disc/d> <0-N>  (case insensitive) with empty
		String discNumberStripped = fileNameNoExtension.replaceAll("(?i)[ _.]+(?:cd|dvd|p(?:ar)?t|dis[ck]|d)[ _.]*[0-9]+$", "");
		//replace <cd/dvd/part/pt/disk/disc/d> <a-d> (case insensitive) with empty
		discNumberStripped = discNumberStripped.replaceAll("(?i)[ _.]+(?:cd|dvd|p(?:ar)?t|dis[ck]|d)[ _.]*[a-d]$", "");
		return discNumberStripped.trim();
	}

	public abstract @NotNull Title scrapeTitle();

	public abstract @NotNull  OriginalTitle scrapeOriginalTitle();

	public abstract @NotNull SortTitle scrapeSortTitle();

	public abstract @NotNull Set scrapeSet();

	public abstract @NotNull Rating scrapeRating();

	public abstract @NotNull ReleaseDate scrapeReleaseDate();

	public abstract @NotNull Year scrapeYear();

	public abstract @NotNull Top250 scrapeTop250();

	public abstract @NotNull Votes scrapeVotes();

	public abstract @NotNull Outline scrapeOutline();

	public abstract @NotNull Plot scrapePlot();

	public abstract @NotNull Tagline scrapeTagline();

	public abstract @NotNull moviescraper.doctord.model.dataitem.Runtime scrapeRuntime();

	public abstract @NotNull Thumb[] scrapePosters(boolean cropPosters);

	public abstract @NotNull Thumb[] scrapeFanart();

	public abstract @NotNull Thumb[] scrapeExtraFanart();

	public abstract @NotNull MPAARating scrapeMPAA();

	public abstract @NotNull ID scrapeID();

	public abstract @NotNull ArrayList<Genre> scrapeGenres();

	public abstract @NotNull ArrayList<Actor> scrapeActors();

	public abstract @NotNull ArrayList<Director> scrapeDirectors();

	public abstract @NotNull Studio scrapeStudio();

	public abstract @NotNull String createSearchString(File file);
        
    public abstract String createSearchStringFromId(String id);

	public @NotNull Trailer scrapeTrailer() {
		return Trailer.BLANK_TRAILER;
	}

	public @NotNull ArrayList<Tag> scrapeTags() {
		return Tag.BLANK_TAGS;
	}

	public abstract @NotNull SearchResult[] getSearchResults(String searchString) throws IOException;

	public SearchResult[] getLinksFromGoogle(String searchQuery, String site) {
		//System.out.println("calling get links from google with searchQuery = " + searchQuery);
		ArrayList<SearchResult> linksToReturn = new ArrayList<>();
		try {
			String encodingScheme = "UTF-8";
			String queryToEncode = "site:" + site + " " + searchQuery;
			String encodedSearchQuery = URLEncoder.encode(queryToEncode, encodingScheme);
			Document doc = Jsoup.connect("https://www.google.com/search?q=" + encodedSearchQuery).userAgent(UserAgent.getRandomUserAgent()).referrer("http://www.google.com").ignoreHttpErrors(true)
			        .timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
			Elements sorryLink = doc.select("form[action=CaptchaRedirect] input");
			Map<String, String> captchaData = new HashMap<>();
			for (Element element : sorryLink) {
				String key = element.attr("name");
				String value = element.attr("value");
				captchaData.put(key, value);
			}
			if (captchaData.size() > 0) {
				System.out.println("Found Captchadata : " + captchaData);
				System.out.println("Google has temporarily blocked us. Trying on bing instead.");
				return getLinksFromBing(searchQuery, site);
			}

			Elements links = doc.select("div.g");
			for (Element link : links) {
				Elements hrefs = link.select(".r a");
				String href = hrefs.attr("href");
				href = URLDecoder.decode(href, encodingScheme);
				href = href.replaceFirst(Pattern.quote("/url?url="), "");
				href = href.replaceFirst(Pattern.quote("/url?q="), "");
				href = href.replaceFirst(Pattern.quote("http://www.google.com/url?url="), "");
				//remove some junk referrer stuff
				int startIndexToRemove = href.indexOf("&rct=");
				if (startIndexToRemove > -1)
					href = href.substring(0, startIndexToRemove);
				linksToReturn.add(new SearchResult(href, hrefs.text()));
			}
			if (linksToReturn.size() == 0) {
				//maybe we will have better luck with bing since we found nothing on google
				return getLinksFromBing(encodedSearchQuery, site);
			}
			return linksToReturn.toArray(new SearchResult[linksToReturn.size()]);
		} catch (IOException e) {
			e.printStackTrace();
			return linksToReturn.toArray(new SearchResult[linksToReturn.size()]);
		}
	}

	/**
	 * A backup search provider in case google search fails. This method is marked private and is called from getLinksFromGoogle. It should not be called in any other class.
	 */
	private SearchResult[] getLinksFromBing(String searchQuery, String site) {
		ArrayList<SearchResult> linksToReturn = new ArrayList<>();
		String encodingScheme = "UTF-8";
		String queryToEncode = "site:" + site + " " + searchQuery;
		String encodedSearchQuery;
		try {
			encodedSearchQuery = URLEncoder.encode(queryToEncode, encodingScheme);
			Document bingResultDocument = Jsoup.connect("https://www.bing.com/search?q=" + encodedSearchQuery).userAgent(UserAgent.getRandomUserAgent()).referrer("http://www.bing.com").ignoreHttpErrors(true)
			        .timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
			Elements links = bingResultDocument.select("a[href*=" + site);
			for (Element link : links) {
				linksToReturn.add(new SearchResult(link.attr("href")));
			}
		} catch (IOException e) {
			e.printStackTrace();
			return linksToReturn.toArray(new SearchResult[linksToReturn.size()]);
		}
		return linksToReturn.toArray(new SearchResult[linksToReturn.size()]);
	}

	protected static boolean fileExistsAtURL(String URLName) {
		return fileExistsAtURL(URLName, true);
	}

	protected static boolean fileExistsAtURL(String URLName, Boolean allow_redirects) {
		try {
			HttpURLConnection.setFollowRedirects(allow_redirects);
			// note : you may also need
			//        HttpURLConnection.setInstanceFollowRedirects(false)
			HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
			con.setInstanceFollowRedirects(allow_redirects);
			con.setRequestMethod("HEAD");
			con.setConnectTimeout(CONNECTION_TIMEOUT_VALUE);
			con.setReadTimeout(CONNECTION_TIMEOUT_VALUE);
			con.setRequestProperty("User-Agent", UserAgent.getRandomUserAgent());
			if (!allow_redirects) {
				return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
			} else {
				return (con.getResponseCode() == HttpURLConnection.HTTP_OK || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM);
			}
		} catch (SocketTimeoutException e) {
			// Non-existing DMM trailers usually time out
			System.err.println("Connection timed out: " + URLName);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public AbstractMovieScraper getMovieScraper() {
		return new GenericMovieScraper(this);
	}

	/**
	 * @return a new copy of the parser by calling the parser's constructor.
	 * used to instantiate a parser when the type of the object is not known
	 */
	public abstract SiteParsingProfile newInstance();

	public Language getScrapingLanguage() {
		return scrapingLanguage;
	}

	public void setScrapingLanguage(Language scrapingLanguage) {
		this.scrapingLanguage = scrapingLanguage;
	}

	public void setScrapingLanguage(MoviescraperPreferences preferences) {
		if (preferences.getScrapeInJapanese())
			scrapingLanguage = Language.JAPANESE;
		else
			scrapingLanguage = Language.ENGLISH;
	}

	/**
	 * If your file is called "Movie Name Here (2001)" this method returns "Movie Name Here"
	 * 
	 * @param file the file to process
	 * @return The movie name without the year in parenthesis next to it
	 */
	public static String getMovieNameFromFileWithYear(File file) {
		String movieName = FilenameUtils.removeExtension(FilenameUtils.getName(file.getName()));
		movieName = movieName.replaceFirst("\\(\\d{4}\\)$", "").trim();
		return movieName;
	}

	/**
	 * @return - null if no file has been scraped yet or the file name of the scraped movie used in {@link #createSearchString(File)} method
	 */
	public String getFileNameOfScrapedMovie() {
		if (scrapedMovieFile == null)
			return null;
		return FilenameUtils.removeExtension(FilenameUtils.getName(scrapedMovieFile.getName()));
	}

	/**
	 * If your file is called "Movie Name Here (2001)" this method returns "2001"
	 * 
	 * @param file the file to process
	 * @return A length 4 string representing the year, if it exists. Otherwise an empty String
	 */
	public static String getYearFromFileWithYear(File file) {
		String movieName = FilenameUtils.removeExtension(FilenameUtils.getName(file.getName()));
		String patternString = "\\(\\d{4}\\)$";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(movieName);
		if (matcher.find()) {
			return matcher.group().replace("(", "").replace(")", "").trim();
		}
		return "";
	}

	/**
	 * @return The name of the parser used when displaying the parser in drop down menus or console output.
	 * For example if the parser parses a site called, "MySite.com"
	 * this function may return "My Site".
	 */
	public abstract String getParserName();

	@Override
	public String toString() {
		return getParserName();
	}

	public boolean isFirstWordOfFileIsID() {
		return firstWordOfFileIsID;
	}

	public void setFirstWordOfFileIsID(boolean firstWordOfFileIsID) {
		this.firstWordOfFileIsID = firstWordOfFileIsID;
	}

	@Override
	public String getDataItemSourceName() {
		return getParserName();
	}

	@Override
	public DataItemSource createInstanceOfSameType() {
		DataItemSource newInstance = newInstance();
		newInstance.setDisabled(isDisabled());
		return newInstance;
	}

	@Override
	public boolean isDisabled() {
		return isDisabled;
	}

	@Override
	public void setDisabled(boolean value) {
		isDisabled = value;
	}

	public static Document downloadDocumentFromURLString(String url) {
		try {
			return Jsoup.connect(url).userAgent("Mozilla").ignoreHttpErrors(true).timeout(CONNECTION_TIMEOUT_VALUE).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Connection.Response getDocument(SearchResult searchResult) {
		try {
			if(searchResult.isJSONSearchResult())
				Thread.sleep(Duration.ofSeconds((int) (Math.random() * (10 - 5) + 5)));
			return Jsoup.connect(searchResult.getUrlPath()).userAgent(UserAgent.getRandomUserAgent()).ignoreContentType(true).ignoreHttpErrors(true).timeout(CONNECTION_TIMEOUT_VALUE).execute();
		}catch (InterruptedException | IOException e){
			System.err.println(e.getMessage());
		}
		return null;
	}

	public Connection.Response downloadDocument(SearchResult searchResult) {
		try {
			if(searchResult.isJSONSearchResult())
				Thread.sleep(Duration.ofSeconds((int) (Math.random() * (10 - 5) + 5)));
			return downloadDocumentFromUrl(searchResult.getUrlPath());
		}catch (InterruptedException | IOException e){
			System.err.println(e.getMessage());
		}
		return null;
	}

	// TODO: Need to fix this whole situation with all these http request functions
	public Connection.Response downloadDocumentFromUrl(String url) throws IOException {
		return Jsoup.connect(url).userAgent(UserAgent.getRandomUserAgent()).ignoreContentType(true).ignoreHttpErrors(true).timeout(CONNECTION_TIMEOUT_VALUE).execute();
	}

	@Override
	public ImageIcon getProfileIcon() {
		if (profileIcon != null)
			return profileIcon;
		else {
			String profileName = this.getClass().getSimpleName();
			String siteName = profileName.replace("ParsingProfile", "");
			return initializeResourceIcon("/res/sites/" + siteName + ".png", 16, 16);
		}
	}

	private ImageIcon initializeResourceIcon(String resourceName, int iconSizeX, int iconSizeY) {
		try {
			URL url = GUIMain.class.getResource(resourceName);
			if (url != null) {
				BufferedImage iconBufferedImage = ImageIO.read(url);
				if (iconBufferedImage != null) {
					iconBufferedImage = Scalr.resize(iconBufferedImage, Method.QUALITY, iconSizeX, iconSizeY, Scalr.OP_ANTIALIAS);
					return new ImageIcon(iconBufferedImage);
				} else
					return new ImageIcon();
			}
			return new ImageIcon();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	public boolean getDiscardResults() {
		return discardResults;
	}

	public void setDiscardResults(boolean value) {
		discardResults = value;
	}

    // Intended for classes that need to do extra preparation after this.document is assigned with a new page
    public void prepareData(){
    }
}
