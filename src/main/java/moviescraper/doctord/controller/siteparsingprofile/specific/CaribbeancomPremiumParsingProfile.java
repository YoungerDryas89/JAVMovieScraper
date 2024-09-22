package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import moviescraper.doctord.model.dataitem.Trailer;
import moviescraper.doctord.model.dataitem.Votes;
import moviescraper.doctord.model.dataitem.Year;

import javax.annotation.Nonnull;

public class CaribbeancomPremiumParsingProfile extends SiteParsingProfile implements SpecificProfile {

	// TODO: Implement also getting the japanese translation
	private Document japaneseDocument;
	private static final SimpleDateFormat caribbeanReleaseDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

	final String title_path = ".movie-info .section .heading h1";

    final Map<String, String> japaneseDetailEquivalent = Map.of(
            "Starring:", "出演",
            "Release Date", "販売日",
            "Duration", "再生時間",
            "Studio", "スタジオ",
            "Tags", "タグ",
            "Ratings", "ユーザー評価",
            "Series", "シリーズ"

    );

    Map<Language, Elements> detailTable = null;


    @Override
    public void prepareData(){
        detailTable = new HashMap<>();
        try {
            detailTable.put(Language.ENGLISH, document.select(".movie-spec"));
            initializeJapaneseDocument();
            detailTable.put(Language.JAPANESE, japaneseDocument.select(".movie-spec"));
        }catch (NullPointerException e){
            System.err.println(e.getMessage());
            throw e;
        }
    }

    private Element getItemByNameWithFallback(String name){
        if(detailTable != null){
            Elements targetLang = detailTable.get(scrapingLanguage).select(".spec-title");
            for(var elem : targetLang){
                if(elem.text().equals(name))
                    return elem.nextElementSibling();
            }
            return getItemFromOriginalPageByName(name);
        }
        return null;
    }

    private Element getItemFromOriginalPageByName(String name){
        Elements japanese = detailTable.get(Language.JAPANESE).select(".spec-title");
        name = japaneseDetailEquivalent.get(name);
        for(var jelem : japanese){
            if(jelem.text().equals(name))
                return jelem.nextElementSibling();
        }
        return null;
    }

	@Nonnull
    @Override
	public Title scrapeTitle() {
		Element title_element = document.select(title_path).first();
		return new Title(title_element.text());
	}


	@Nonnull
    @Override
	public OriginalTitle scrapeOriginalTitle() {
        if(scrapingLanguage == Language.ENGLISH && japaneseDocument == null){
            initializeJapaneseDocument();
        }
        if(scrapingLanguage == Language.ENGLISH){
            var title_element = japaneseDocument.select(title_path).first();
            return new OriginalTitle(title_element.text());
        }
		return new OriginalTitle("");
	}

	@Nonnull
    @Override
	public SortTitle scrapeSortTitle() {
		return SortTitle.BLANK_SORTTITLE;
	}

	@Nonnull
    @Override
	public Set scrapeSet() {
        if(japaneseDocument != null){
            Element series = getItemFromOriginalPageByName("Series");
            if(series != null){
                return new Set(series.text());
            }
        }
		return Set.BLANK_SET;
	}

	@Nonnull
    @Override
	public Rating scrapeRating() {
		// this site does not have ratings, so just return some default values
		if(japaneseDocument != null){
			Element rating_elements = getItemFromOriginalPageByName("Ratings");
			if(rating_elements != null){
				return new Rating(5, String.valueOf(rating_elements.text().length()));
			}

		}
		return new Rating(0, "0");
	}

	@Nonnull
    @Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}

	@Nonnull
    @Override
	public ReleaseDate scrapeReleaseDate() {
		Element date_element = getItemByNameWithFallback("Release Date");
        if(date_element != null){
           return new ReleaseDate(date_element.text(), caribbeanReleaseDateFormat);
        }
        return ReleaseDate.BLANK_RELEASEDATE;
	}

	@Nonnull
    @Override
	public Top250 scrapeTop250() {
		// This type of info doesn't exist on this site
		return Top250.BLANK_TOP250;
	}

	@Nonnull
    @Override
	public Votes scrapeVotes() {
		// This type of info doesn't exist on this site
		return Votes.BLANK_VOTES;
	}

	@Nonnull
    @Override
	public Outline scrapeOutline() {
		// This type of info doesn't exist on this site
		return Outline.BLANK_OUTLINE;
	}

	@Nonnull
    @Override
	public Plot scrapePlot() {
		if(japaneseDocument == null && scrapingLanguage == Language.ENGLISH) {
			return Plot.BLANK_PLOT;
		} else {
			var plot = japaneseDocument.selectFirst("#moviepages > div > div.inner-container > div.movie-info > div > p").text();
			return new Plot(plot);
		}
	}

	@Nonnull
    @Override
	public Tagline scrapeTagline() {
		// This type of info doesn't exist on this site
		return Tagline.BLANK_TAGLINE;
	}

	@Nonnull
    @Override
	public Runtime scrapeRuntime() {
		Element duration_element = getItemByNameWithFallback("Duration");
        if(duration_element != null) {
            String[] durationSplitByTimeUnit = duration_element.text().split(":");
            if (durationSplitByTimeUnit.length == 3) {
                int hours = Integer.parseInt(durationSplitByTimeUnit[0]);
                int minutes = Integer.parseInt(durationSplitByTimeUnit[1]);
                return new Runtime(Integer.toString((hours * 60) + minutes));
            }
        }
		return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters(boolean cropPosters) {
		String id = getIdFromUrl();
		List<Thumb> posters = new LinkedList<>();
		String[] posterUrls = {
				"/moviepages/"+ id +"/images/l_l.jpg",
				"/moviepages/"+ id +"/images/poster_en.jpg"
		};


		try {
			for(var paths : posterUrls){
				if(fileExistsAtURL("https://www.caribbeancompr.com" + paths)){
					posters.add(new Thumb("https://www.caribbeancompr.com" + paths, cropPosters));
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(CaribbeancomPremiumParsingProfile.class.getName()).log(Level.SEVERE, null, ex);
		}
		return posters.toArray(new Thumb[posters.size()]);
	}

	@Override
	public Thumb[] scrapeFanart() {
		return scrapePosters(false);
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
        List<Thumb> extrafanart = new ArrayList<>();
        try {
			Element gallery_elements = document.select(".is-gallery").first();
			if(gallery_elements != null && gallery_elements.childrenSize() > 0) {
				for (var image_elem_trees : gallery_elements.children()) {
					var image_elem = image_elem_trees.select(".fancy-gallery").first();
					if (image_elem.attr("data-is_sample").equals("1")) {
						Thumb img = new Thumb(image_elem.attr("href"));
						extrafanart.add(img);
					}
				}
			}
        }catch (MalformedURLException|NullPointerException e){
            System.err.println(e.getMessage());
        }
		return extrafanart.toArray(new Thumb[0]);
	}

	@Nonnull
    @Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Nonnull
    @Override
	public ID scrapeID() {
		return new ID(getIdFromUrl());
	}

	@Nonnull
    @Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genresReturned = new ArrayList<>();
		var genre_elements = getItemByNameWithFallback("Tags");
		for (Element genreElement : genre_elements.children()) {
			genresReturned.add(new Genre(genreElement.text().trim()));
		}
		return genresReturned;
	}

	@Nonnull
    @Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<>();
		var actor_elements = getItemByNameWithFallback("Starring:");
        if(actor_elements != null) {

            // In some cases the English page's actors element will be present but the contents will be empty, hence we will get them from the
            // Japanese translation of the page
            var first_child = actor_elements.firstElementChild();
            if(first_child.text() == null || first_child.text().isEmpty()){
                actor_elements = getItemFromOriginalPageByName("Starring");
            }

            for (var actor_element : actor_elements.children()) {
                var actor_name = actor_element.text();
                Actor actor = new Actor(actor_element.text(), null, null);
                actorList.add(actor);
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
	public Trailer scrapeTrailer() {
		int id_index = 5;
		if(scrapingLanguage != Language.ENGLISH){
			id_index = 4;
		}
		return new Trailer("https://smovie.caribbeancompr.com/sample/movies/"+ document.baseUri().split("/")[id_index] +"/480p.mp4");
	}

	@Nonnull
    @Override
	public Studio scrapeStudio() {
		// Studio attribute is only available on the Japanese version of the site
		if(japaneseDocument != null) {
            var studio_element = getItemFromOriginalPageByName("Studio");
            if(studio_element != null){
                return new Studio(studio_element.text());
            }
		}
		return Studio.BLANK_STUDIO;
	}

	@Nonnull
    @Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String fileNameNoExtension = findIDTagFromFile(file, isFirstWordOfFileIsID());
		return fileNameNoExtension.split("-")[1];
	}

	@Override
	public String createSearchStringFromId(String id) {
		return null;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		return new SearchResult[]{ new SearchResult("https://en.caribbeancompr.com/eng/moviepages/" + searchString + "/index.html")};
	}

	private void initializeJapaneseDocument() {
		var japanese_url = "https://www.caribbeancompr.com/moviepages/" + getIdFromUrl() + "/index.html";
		try {
			japaneseDocument = Jsoup.connect(japanese_url).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getParserName() {
		return "Caribbeancom Premium";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new CaribbeancomPremiumParsingProfile();
	}

	public String getIdFromUrl(){
		// TODO: Implement something better than this and more broader
		int id_index = 5;
		if(scrapingLanguage != Language.ENGLISH){
			id_index = 4;
		}
		return document.baseUri().split("/")[id_index];
	}

}
