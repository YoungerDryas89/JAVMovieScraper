package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moviescraper.doctord.controller.languagetranslation.Language;
import org.apache.commons.io.FilenameUtils;
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

public class Kin8tengokuParsingProfile extends SiteParsingProfile implements SpecificProfile {

	private String id;

	@Nonnull
    @Override
	public Title scrapeTitle() {
		var scrapedTitle = document.select(".sub_title_vip");
		if(scrapedTitle.isEmpty()){
			scrapedTitle = document.select(".sub_title");
		}
		if(!scrapedTitle.isEmpty()){
			return new Title(scrapedTitle.first().text());
		}
		return new Title();
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
		return new Set("Kin8tengoku");
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
		Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
		Elements elements = document.select("td[class^=movie_table] ");
		for (Element element : elements) {
			String time = element.childNode(0).toString();
			Matcher matcher = pattern.matcher(time);
			if (matcher.find()) {
				String timeString = matcher.group();
				return new ReleaseDate(timeString);
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
		Pattern pattern = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
		Elements elements = document.select("td[class^=movie_table] ");
		for (Element element : elements) {
			String time = element.childNode(0).toString();
			Matcher matcher = pattern.matcher(time);
			if (matcher.find()) {
				String timeString = matcher.group();
				String[] split = timeString.split(":");
				Integer minutes = Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
				return new Runtime(minutes.toString());
			}
		}
		return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters(boolean cropPosters) {
		try {
			String Id = findID(scrapedMovieFile.getName());
			Thumb[] thumbs = new Thumb[1];
			thumbs[0] = new Thumb(getThumbURL(Id, 1), cropPosters);
			return thumbs;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeFanart() {
		String thumbURL = getThumbURL(id, 1);
		try {
			Thumb thumb = new Thumb(thumbURL);
			Thumb[] thumbs = { thumb };
			return thumbs;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		ArrayList<Thumb> extraFanart = new ArrayList<>();
		return extraFanart.toArray(new Thumb[extraFanart.size()]);
	}

	@Nonnull
    @Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Nonnull
    @Override
	public ID scrapeID() {
		return new ID("KIN8-" + findID(scrapedMovieFile.getName()));
	}

	@Nonnull
    @Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> list = new ArrayList<>();
		Elements elements = document.select("div[class=icon] a[href~=/listpages/[0-9]]");
		for (Element element : elements) {
			String genre = element.childNode(0).toString();
			list.add(new Genre(genre));
		}
		return list;
	}

	@Nonnull
    @Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> list = new ArrayList<>();
		Elements elements = document.select("a[href^=/listpages/actor_]");
		for (Element element : elements) {
			String name = element.childNode(0).toString();
			list.add(new Actor(name, null, null));
		}
		return list;
	}

	@Nonnull
    @Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> list = new ArrayList<>();
		list.add(new Director("Kin8tengoku", null));
		return list;
	}

	@Nonnull
    @Override
	public Studio scrapeStudio() {
		return new Studio("Kin8tengoku");
	}

	@Nonnull
    @Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		return createSearchStringFromId(findID(FilenameUtils.getName(file.getName())));
	}

	@Override
	public String createSearchStringFromId(String Id) {
		if (Id != null && !Id.isEmpty())
			return getDomain() + Id + "/pht/shosai.htm";
		return Id;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		String thumb = getThumbURL(findID(searchString));
		SearchResult searchResult = new SearchResult(searchString, "ID :" + findID(searchString), new Thumb(thumb));
		return new SearchResult[]{searchResult};
	}

	public static String findID(String searchString) {
		Pattern pattern = Pattern.compile("[0-9]{4}");
		Matcher matcher = pattern.matcher(searchString);
		if (matcher.find()) {
			String id = matcher.group();
			return id;
		}
		return "";
	}

	public String getThumbURL(String id) {
		return getThumbURL(id, 8);
	}

	public String getThumbURL(String id, int number) {
		//return getDomain() + id + "/pht/" + number + ".jpg";
		if(scrapingLanguage == Language.ENGLISH){
			return getDomain() + id + "/pht/" + "main_en.jpg";
		} else {
			return getDomain() + id + "/pht/" + number + ".jpg";
		}
	}

	private String getDomain(){
		if(scrapingLanguage == Language.ENGLISH){
			return "https://en.kin8tengoku.com/";
		} else {
			return "https://kin8tengoku.com/";
		}
	}

	@Override
	public String getParserName() {
		return "Kin8tengoku";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new Kin8tengokuParsingProfile();
	}

}
