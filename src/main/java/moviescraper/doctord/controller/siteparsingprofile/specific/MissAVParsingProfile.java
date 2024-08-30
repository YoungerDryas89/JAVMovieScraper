package moviescraper.doctord.controller.siteparsingprofile.specific;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.*;
import moviescraper.doctord.model.dataitem.Runtime;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MissAVParsingProfile extends SiteParsingProfile implements SpecificProfile {
    final String titleTablePath = "div.space-y-2";
    final String titlePath = "h1.text-base";
    final String notFoundpath = "p.text-4xl.font-extrabold.text-primary";
    final String plotTextPath = "div.mb-1";
    final String posterImg = "video.player";
    final String durtationPath = "/html/body/div[2]/div[3]/div/div[2]/div[1]/div[1]/div/div/div[1]/div[3]";
    Map<String, Element> movie_data = new HashMap<>();

    @Override
    public void prepareData(){
        Element notFoundnum = document.select(notFoundpath).first();
        if(notFoundnum != null && notFoundnum.text().equals("404")){
            System.err.println("Title not found");
        } else {
            Element table = document.select(titleTablePath).first();
            if(table != null){
                for(Element data : table.children()){
                    switch (data.firstElementChild().text()){
                        case "Genre:":
                        case "Actors:":
                        case "Actress:":
                        case "Tags:": movie_data.put(data.firstElementChild().text(), data); break;
                        default: movie_data.put(data.firstElementChild().text(), data.lastElementChild()); break;
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public Title scrapeTitle() {
        Element title_element = document.select(titlePath).first();
        if(title_element != null){
            return new Title(title_element.text());
        }
        return new Title("");
    }

    @Nonnull
    @Override
    public OriginalTitle scrapeOriginalTitle() {
        // TODO: Fix for chinese media titles
        if(movie_data.containsKey("Title:")){
            Element origTitle = movie_data.get("Title:");
            if(origTitle != null){
                return new OriginalTitle(origTitle.text());
            }
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
            Element series = movie_data.get("Series:");
            if(series != null){
                return new Set(series.text());
            }
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
            Element date = movie_data.get("Release date:");
            if(date != null){
                return new ReleaseDate(date.text());
            }
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
        Element plot = document.select(plotTextPath).first();
        if(plot != null && !plot.text().isEmpty()){
            return new Plot(plot.text());
        }
        return Plot.BLANK_PLOT;
    }

    @Nonnull
    @Override
    public Tagline scrapeTagline() {
        // TODO: Implement this once I find an example title to test against
        return Tagline.BLANK_TAGLINE;
    }

    @Nonnull
    @Override
    public Runtime scrapeRuntime() {
        // TODO: Broken
        /*try {
            Element durationElement = document.getElementsByAttributeStarting()
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
        }*/
        return Runtime.BLANK_RUNTIME;
    }

    @Override
    public Thumb[] scrapePosters(boolean cropPosters) {
        // TODO: Fix for chinese media titles; Need a way to discern Japanese titles from Chinese ones
        try {
            Thumb[] posters = new Thumb[1];
            Element posterElement = document.select(posterImg).first();
            if (posterElement != null) {
                posters[0] = new Thumb(posterElement.attr("data-poster"), cropPosters);
                return posters;
            }
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
        return new Thumb[0];
    }

    @Override
    public Thumb[] scrapeFanart() {
        try {
            Thumb[] posters = new Thumb[1];
            Element posterElement = document.select(posterImg).first();
            if (posterElement != null) {
                posters[0] = new Thumb(posterElement.attr("data-poster"), false);
                return posters;
            }
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
        return new Thumb[0];
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
            Element code = movie_data.get("Code:");
            if(code != null){
                return new ID(code.text());
            }
        }
        return ID.BLANK_ID;
    }

    @Nonnull
    @Override
    public ArrayList<Genre> scrapeGenres() {
        ArrayList<Genre> genres = new ArrayList<>();
        if(movie_data.containsKey("Genre:")){
            for(Element genre : movie_data.get("Genre:").getElementsByTag("a")){
                genres.add(new Genre(genre.text()));
            }
        }
        return genres;
    }

    @Nonnull
    @Override
    public ArrayList<Actor> scrapeActors() {
        ArrayList<Actor> actors = new ArrayList<>();
        if(movie_data.containsKey("Actress:")){
            for(Element actress : movie_data.get("Actress:").getElementsByTag("a")){
                actors.add(new Actor(actress.text(), "", null));
            }
        }

        if(movie_data.containsKey("Actor:")){
            for(Element actor : movie_data.get("Actor:").getElementsByTag("a")){
                actors.add(new Actor(actor.text(), "", null));
            }
        }
        return actors;
    }

    @Nonnull
    @Override
    public ArrayList<Director> scrapeDirectors() {
        ArrayList<Director> directors = new ArrayList<>();
        if(movie_data.containsKey("Director:")){
            Element directorElement = movie_data.get("Director:");
            if(directorElement != null){
                directors.add(new Director(directorElement.text(), null));
            }
        }
        return directors;
    }

    @Nonnull
    @Override
    public Studio scrapeStudio() {
        if(movie_data.containsKey("Maker:")){
            Element maker = movie_data.get("Maker:");
            if(maker != null){
                return new Studio(maker.text());
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
        return "https://missav.com/en/" + id;
    }

    @Override
    public SearchResult[] getSearchResults(String searchString) throws IOException {
        var result = new SearchResult(searchString);
        return new SearchResult[]{result};
    }

    @Override
    public SiteParsingProfile newInstance() {
        return new MissAVParsingProfile();
    }

    @Override
    public String getParserName() {
        return "MissAV";
    }
}
