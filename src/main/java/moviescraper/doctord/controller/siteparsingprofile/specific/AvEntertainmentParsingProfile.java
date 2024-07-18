package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

public class AvEntertainmentParsingProfile extends SiteParsingProfile implements SpecificProfile {

	private static final SimpleDateFormat avEntertainmentReleaseDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
	final static String getResultsByDiv = "//div[contains(concat(' ', normalize-space(@class), ' '), \" shop-product-wrap \")]/div";
	final static String getThumbnail = ".//div[@class=\"single-slider-product__image\"]/a/img[@class=\"img-fluid\"]";
	final static String getTitlePageUrl = ".//div[@class=\"single-slider-product__image\"]/a";
	final static String getTitleFromSearchPage = ".//div[@class=\"single-slider-product__content\"]//a";

	final static String getStars = ".//h4[text()=\"Starring\"]//following-sibling::span/*";
	//final static String getActressImage = "//div[contains(concat(' ', normalize-space(@class), ' '), \"list-view-product\")]//img/@src";
	final static String getActressImage = "//div[contains(concat(' ', normalize-space(@class), ' '), ' single-slider-product--list-view__image ')]/img";
	final static String getTitle = "//div[contains(concat(' ', normalize-space(@class), ' '), ' col-sm-12 ')]/div[@class='section-title']/h3";
	final static String getReleaseDate = "//div[contains(concat(' ', normalize-space(@class), ' '), ' product-info-block-rev ')]/div/span[text()='Date']/following-sibling::span";
	final static String getRuntime = "//div[contains(concat(' ', normalize-space(@class), ' '), ' product-info-block-rev ')]/div/span[text()='Play Time']/following-sibling::span";
	final static String getPoster = "//div[@id='PlayerCover']//img";
	final static String getProductDetailsArea = "//div[contains(concat(' ', normalize-space(@class), ' '), ' product-details-area ')]/div/div[2]/div[1]/div[3]";

	final static String getFanArtTable = ".gallery-block > div:nth-child(1) > div:nth-child(2)";

	@Nonnull
    @Override
	public Title scrapeTitle() {
		Elements elements = document.selectXpath(getTitle);
		return new Title(elements.text());
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
		String set = getMovieData("Series", "シリーズ").get(0).text();
		return new Set(set);
	}

	@Nonnull
    @Override
	public Rating scrapeRating() {
		return new Rating(0, "");
	}

	@Nonnull
    @Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}

	@Nonnull
    @Override
	public ReleaseDate scrapeReleaseDate() {

		Elements elements = document.selectXpath(getReleaseDate);
		if (elements != null) {
			return new ReleaseDate(elements.text().split(" ")[0], avEntertainmentReleaseDateFormat);
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
		String runtime = "";
		Elements elements = document.selectXpath(getRuntime);
		runtime = elements.text().split(" ")[1];
		return new Runtime(runtime);
	}

	@Override
	public Thumb[] scrapePosters() {
		//List<Thumb> thumbs = new ArrayList<>();
		//Thumb[] fanart = scrapeFanart();
		// TODO: Fix AvEntertainment's scrapePosters()
		/*if (fanart.length > 0) {
			try {
				BufferedImage read = ImageIO.read(fanart[0].getThumbURL());
				if (read != null) {
					//int newWidth = (int) ((1.0 - 0.526666) * read.getWidth());
					thumbs.add(new Thumb(fanart[0].getThumbURL().toString(), true));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		var poster_elem_string = "span.grid-gallery > a:nth-child(1)";
		var elem = document.select(poster_elem_string).first();
		List<Thumb> returnList = new ArrayList<Thumb>();
		try{
			Thumb thumb = new Thumb(elem.attr("href"));
			returnList.add(thumb);
		} catch (MalformedURLException e){
			System.err.println(e.getMessage());
		}
		return returnList.toArray(new Thumb[returnList.size()]);
	}

	@Override
	public Thumb[] scrapeFanart() {
		List<Thumb> thumbs = new ArrayList<>();
		Elements elements = document.select(getFanArtTable).first().children();
		for(var image_element : elements){
			var url = image_element.firstElementChild().attr("href");
			try {
				thumbs.add(new Thumb(url));
			}catch (MalformedURLException e){
				System.err.println(e.getMessage());
			}
		}

		return thumbs.toArray(new Thumb[thumbs.size()]);
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
		Elements select = document.select("div[class=top-title]");
		String id = "";
		if (select.size() > 0) {
			Element element = select.get(0);
			if (element.childNodeSize() > 0) {
				id = element.childNode(0).toString();
				id = getLastWord(id);
			}
		}
		return new ID(id);
	}

	private static String getLastWord(String input) {
		String wordSeparator = " ";
		boolean inputIsOnlyOneWord = !StringUtils.contains(input, wordSeparator);
		if (inputIsOnlyOneWord) {
			return input;
		}
		return StringUtils.substringAfterLast(input, wordSeparator);
	}

	@Nonnull
    @Override
	public ArrayList<Genre> scrapeGenres() {
		// TODO: Add Japanese word for category
		var categories = getMovieData("Category", "");
		ArrayList<Genre> genres = new ArrayList<>();
		for (Element element : categories) {
			if(element != null)
				genres.add(new Genre(element.text()));
		}
		return genres;
	}

	@Nonnull
    @Override
	public ArrayList<Actor> scrapeActors() {
		Elements elements = document.selectXpath(getStars);
		ArrayList<Actor> list = new ArrayList<>();
		if (elements != null) {
			for (Element element : elements) {
				String href = element.attr("href");
				String name = WordUtils.capitalize(element.text());
				Thumb thumb = null;
				try {
					Document actorDoc = Jsoup.connect(href).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
					var url = actorDoc.selectXpath(getActressImage).attr("src");
					if (!url.isEmpty()) {
						thumb = new Thumb(url);
					}
					list.add(new Actor(name, null, thumb));
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		return list;
	}

	@Nonnull
    @Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> list = new ArrayList<>();
		return list;
	}

	@Nonnull
    @Override
	public Studio scrapeStudio() {
		String studio = getMovieData("Studio", "スタジオ").get(0).text();
		return new Studio(studio);
	}

	private List<Element> getMovieData(String category, String japaneseWordForCategory) {
		var elements = document.selectXpath(getProductDetailsArea).first();
		List<Element> returnElements = new ArrayList<Element>();
		if(elements != null) {
			for (Element element : elements.children()) {
				Elements span = element.select("span");
				if (span != null || !span.isEmpty()) {
					String cat = span.first().childNode(0).toString();
					if (cat.startsWith(category) || (cat.startsWith(japaneseWordForCategory) && !japaneseWordForCategory.isEmpty())) {
						returnElements.addAll(span.last().children().stream().collect(Collectors.toList()));
					}
				}
			}
		}
		return returnElements;
	}

	@Nonnull
    @Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		return createSearchStringFromId(findIDTagFromFile(file, isFirstWordOfFileIsID()));
	}


	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		Document doc = Jsoup.connect(searchString).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
		List<SearchResult> list = new ArrayList<>();
		Elements elements = doc.selectXpath(getResultsByDiv);

		for (Element e : elements) {
			String href = e.selectXpath(getTitlePageUrl).attr("href");
			String label = e.selectXpath(getTitleFromSearchPage).text();
			String thumnailUrl = e.selectXpath(getThumbnail).attr("href");
			Thumb thumb = null;
			if(thumnailUrl.startsWith("https://imgs02.aventertainments.com")){
				thumb = new Thumb(thumnailUrl);
			}
			list.add(new SearchResult(href, label, thumb));
		}
		return list.toArray(new SearchResult[list.size()]);
	}

	@Override
	public String createSearchStringFromId(String Id) {
		String languageID = "1";
		if (getScrapingLanguage() == Language.JAPANESE)
			languageID = "2";
		return "http://www.aventertainments.com/search_Products.aspx?languageID=" + languageID + "&dept_id=29&keyword=" + Id + "&searchby=item_no";
	}

	@Override
	public String getParserName() {
		return "AV Entertainment";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new AvEntertainmentParsingProfile();
	}

}
