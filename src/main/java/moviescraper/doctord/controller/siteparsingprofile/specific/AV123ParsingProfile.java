package moviescraper.doctord.controller.siteparsingprofile.specific;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.*;
import moviescraper.doctord.model.dataitem.Runtime;
import moviescraper.doctord.scraper.UserAgent;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AV123ParsingProfile extends SiteParsingProfile implements SpecificProfile {

    final String titlePath = "html body div#app div#body div#page-video.container div.row div.col div.d-flex.justify-content-between.align-items-start div.mr-3 h1";
    final String posterPath = "html body div#app div#body div#page-video.container div.row div.col div#player";
    final String movieDetailsPath = ".content .detail-item";
    final String plotPath = "html body div#app div#body div#page-video.container div.row div.col div#details div.content div.description p";
    Map<String, Element> movie_data = new HashMap<>();
    String id, url;
    Document japaneseDocument;

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

    private void initializeJapaneseDocument(){
        try {
            // TODO: FIX this hack
            // Replace the original url since just a simple string concatenation of "https://123av.com/ja/v" might just redirect to the english page
            var response = downloadDocumentFromUrl(url.replace("https://123av.com/en", "https://123av.com/ja")).bufferUp();
            if (response.statusCode() == 200) {
                japaneseDocument = response.parse();
            }
        }catch (IOException e){
            System.err.println(e.getMessage());
        }

    }

    @Override
    public Connection.Response downloadDocumentFromUrl(String url){
        try {
            var response = Jsoup.connect(url).userAgent(UserAgent.getRandomUserAgent()).followRedirects(true).ignoreHttpErrors(true).timeout(CONNECTION_TIMEOUT_VALUE).execute();
            if(response.statusCode() == 200){

                response = response.bufferUp();
                var continueButton = response.parse().selectFirst("a.btn-primary");
                if(continueButton != null && continueButton.text().equals("Click here to continue")) {
                    String redirectUrl = continueButton.attr("href");

                    System.out.println("AV123: Redirecting to: " + redirectUrl);
                    this.url = redirectUrl;
                    return Jsoup.connect(redirectUrl).userAgent(UserAgent.getRandomUserAgent()).followRedirects(true).ignoreHttpErrors(true).timeout(CONNECTION_TIMEOUT_VALUE).execute();
                }
            }
            this.url = response.url().toString();
            return response;
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Nonnull
    @Override
    public Title scrapeTitle() {
        return new Title(document.select(titlePath).text());
    }

    @Nonnull
    @Override
    public OriginalTitle scrapeOriginalTitle() {
        initializeJapaneseDocument();
        if(japaneseDocument != null){
            return new OriginalTitle(japaneseDocument.select(titlePath).text());
        }
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
        if(movie_data.containsKey("Series:")){
            return new Set(movie_data.get("Series:").text());
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
    public ReleaseDate scrapeReleaseDate() {
        if(movie_data.containsKey("Release date:")){
            return new ReleaseDate(movie_data.get("Release date:").text());
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
        Element plotElement = document.select(plotPath).first();
        if(plotElement != null){
            return new Plot(plotElement.text());
        }
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
        if(movie_data.containsKey("Runtime:")){
            try {
                Element durationElement = movie_data.get("Runtime:");
                if (durationElement != null) {
                    String[] durationSplitByTimeUnit = durationElement.text().split(":");
                    if (durationSplitByTimeUnit.length != 3) {
                        throw new IllegalArgumentException("Invalid number of parts");
                    }
                    int hours = Integer.parseInt(durationSplitByTimeUnit[0]);
                    int minutes = Integer.parseInt(durationSplitByTimeUnit[1]);
                    // we don't care about seconds

                    int totalMinutes = (hours * 60) + minutes;
                    return new Runtime(Integer.toString(totalMinutes));
                }
            }catch (Exception e){
                System.err.println(e.getMessage());
            }
        }
        return Runtime.BLANK_RUNTIME;
    }

    @Override
    public Thumb[] scrapePosters(boolean cropPosters) {
        List<Thumb> posters = new ArrayList<>();
        try {
            Element poster = document.select(posterPath).first();
            if (poster != null) {
                posters.add(new Thumb(poster.attr("data-poster"), cropPosters));
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

    @Nonnull
    @Override
    public MPAARating scrapeMPAA() {
        return MPAARating.RATING_XXX;
    }

    @Nonnull
    @Override
    public ID scrapeID() {
        if(movie_data.containsKey("Code:")){
            return new ID(movie_data.get("Code:").text());
        }
        return ID.BLANK_ID;
    }

    @Nonnull
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

    @Nonnull
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

    @Nonnull
    @Override
    public ArrayList<Director> scrapeDirectors() {
        return new ArrayList<>();
    }

    @Nonnull
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

    @Nonnull
    @Override
    public String createSearchString(File file) {
        scrapedMovieFile = file;
        return createSearchStringFromId(findIDTagFromFile(file, false));
    }

    @Override
    public String createSearchStringFromId(String id) {
        this.id = id;
        return "https://123av.com/en/v/" + id;
    }

    @Override
    public SearchResult[] getSearchResults(String searchString) throws IOException {
        var result = new SearchResult(searchString);
        return new SearchResult[]{result};
    }

    @Override
    public SiteParsingProfile newInstance() {
        return new AV123ParsingProfile();
    }

    @Override
    public String getParserName() {
        return "123AV";
    }

    @Nonnull
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
