package moviescraper.doctord.controller.siteparsingprofile.specific;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.*;
import moviescraper.doctord.model.dataitem.Runtime;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NJavParsingProfile extends SiteParsingProfile implements SpecificProfile {

    final String titlePath = "html body div#app div#body div#page-video.container div.row div.col div.d-flex.justify-content-between.align-items-start div.mr-3 h1";
    final String posterPath = "html body div#app div#body div#page-video.container div.row div.col div#player";
    final String movieDetailsPath = ".content .detail-item";
    final String plotPath = "html body div#app div#body div#page-video.container div.row div.col div#details div.content div.description p";
    Map<String, Element> movie_data = new HashMap<>();

    @Override
    public void prepareData(){
        Element message = document.select(".message").first();
        if(message != null){
            System.err.println(message.text());
        } else {
            Element table = document.select(movieDetailsPath).first();
            if (table != null) {
                for (Element data : table.children()) {
                    movie_data.put(data.firstElementChild().text(), data.lastElementChild());
                }
            }
        }
    }
    @Override
    public Title scrapeTitle() {
        return new Title(document.select(titlePath).text());
    }

    @Override
    public OriginalTitle scrapeOriginalTitle() {
        return OriginalTitle.BLANK_ORIGINALTITLE;
    }

    @Override
    public SortTitle scrapeSortTitle() {
        return SortTitle.BLANK_SORTTITLE;
    }

    @Override
    public Set scrapeSet() {
        if(movie_data.containsKey("Series:")){
            return new Set(movie_data.get("Series:").text());
        }
        return Set.BLANK_SET;
    }

    @Override
    public Rating scrapeRating() {
        return Rating.BLANK_RATING;
    }

    @Override
    public ReleaseDate scrapeReleaseDate() {
        if(movie_data.containsKey("Release date:")){
            return new ReleaseDate(movie_data.get("Release date:").text());
        }
        return ReleaseDate.BLANK_RELEASEDATE;
    }

    @Override
    public Year scrapeYear() {
        return scrapeReleaseDate().getYear();
    }

    @Override
    public Top250 scrapeTop250() {
        return Top250.BLANK_TOP250;
    }

    @Override
    public Votes scrapeVotes() {
        return Votes.BLANK_VOTES;
    }

    @Override
    public Outline scrapeOutline() {
        return Outline.BLANK_OUTLINE;
    }

    @Override
    public Plot scrapePlot() {
        Element plotElement = document.select(plotPath).first();
        if(plotElement != null){
            return new Plot(plotElement.text());
        }
        return Plot.BLANK_PLOT;
    }

    @Override
    public Tagline scrapeTagline() {
        return Tagline.BLANK_TAGLINE;
    }

    @Override
    public Runtime scrapeRuntime() {
        if(movie_data.containsKey("Runtime:")){
            return new Runtime(movie_data.get("Runtime:").text());
        }
        return Runtime.BLANK_RUNTIME;
    }

    @Override
    public Thumb[] scrapePosters() {
        List<Thumb> posters = new ArrayList<>();
        try {
            Element poster = document.select(posterPath).first();
            if (poster != null) {
                posters.add(new Thumb(poster.attr("data-poster"), true));
            }
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
        return posters.toArray(new Thumb[0]);
    }

    @Override
    public Thumb[] scrapeFanart() {
        List<Thumb> posters = new ArrayList<>();
        try {
            Element poster = document.select(posterPath).first();
            if (poster != null) {
                posters.add(new Thumb(poster.attr("data-poster")));
            }
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
        return posters.toArray(new Thumb[0]);
    }

    @Override
    public Thumb[] scrapeExtraFanart() {
        return new Thumb[0];
    }

    @Override
    public MPAARating scrapeMPAA() {
        return MPAARating.RATING_XXX;
    }

    @Override
    public ID scrapeID() {
        if(movie_data.containsKey("Code:")){
            return new ID(movie_data.get("Code:").text());
        }
        return ID.BLANK_ID;
    }

    @Override
    public ArrayList<Genre> scrapeGenres() {
        ArrayList<Genre> genres = new ArrayList<>();
        if(movie_data.containsKey("Genres:")){
            for(Element genre : movie_data.get("Genres:").children()){
                genres.add(new Genre(genre.text()));
            }
        }
        return genres;
    }

    @Override
    public ArrayList<Actor> scrapeActors() {
        ArrayList<Actor> actresses = new ArrayList<>();
        if(movie_data.containsKey("Actresses:")){
            for(Element actress : movie_data.get("Actresses:").children()){
               actresses.add(new Actor(actress.text(), null, null));
            }
        }
        return actresses;
    }

    @Override
    public ArrayList<Director> scrapeDirectors() {
        return new ArrayList<>();
    }

    @Override
    public Studio scrapeStudio() {
        if(movie_data.containsKey("Maker:")){
            return new Studio(movie_data.get("Maker:").text());
        } else {
            if (movie_data.containsKey("Label:")) {
                return new Studio(movie_data.get("Label:").text());
            }
        }
        return Studio.BLANK_STUDIO;
    }

    @Override
    public String createSearchString(File file) {
        scrapedMovieFile = file;
        return createSearchStringFromId(findIDTagFromFile(file, false));
    }

    @Override
    public String createSearchStringFromId(String id) {
        return "https://njav.tv/en/v/" + id;
    }

    @Override
    public SearchResult[] getSearchResults(String searchString) throws IOException {
        var result = new SearchResult(searchString);
        return new SearchResult[]{result};
    }

    @Override
    public SiteParsingProfile newInstance() {
        return new NJavParsingProfile();
    }

    @Override
    public String getParserName() {
        return "NJav";
    }

    @Override
    public ArrayList<Tag> scrapeTags(){
        ArrayList<Tag> tags = new ArrayList<>();
        if(movie_data.containsKey("Tags:")){
            for(var tag : movie_data.get("Tags:").children()){
                tags.add(new Tag(tag.text()));
            }
        }
        return tags;
    }
}
