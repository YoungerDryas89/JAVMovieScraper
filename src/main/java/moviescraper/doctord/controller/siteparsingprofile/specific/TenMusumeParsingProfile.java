package moviescraper.doctord.controller.siteparsingprofile.specific;

import moviescraper.doctord.controller.languagetranslation.Language;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfileJSON;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.*;
import moviescraper.doctord.model.dataitem.Runtime;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TenMusumeParsingProfile extends SiteParsingProfileJSON implements SpecificProfile {
    @Override
    public @NotNull Title scrapeTitle() {
        JSONObject pageJSON = getMovieJSON();
        String title;
        if (scrapingLanguage == Language.ENGLISH) {
            if(!pageJSON.isNull("TitleEn")) {
                title = pageJSON.getString("TitleEn");
            } else {
                title = pageJSON.getString("Title");
            }

        } else {
            title = pageJSON.getString("Title");
        }

        return new Title(title);
    }

    @Override
    public @NotNull OriginalTitle scrapeOriginalTitle() {
        JSONObject pageJSON = getMovieJSON();
        return new OriginalTitle(pageJSON.getString("Title"));
    }

    @Override
    public @NotNull SortTitle scrapeSortTitle() {
        return SortTitle.BLANK_SORTTITLE;
    }

    @Override
    public @NotNull Set scrapeSet() {
        return Set.BLANK_SET;
    }

    @Override
    public @NotNull Rating scrapeRating() {
        return Rating.BLANK_RATING;
    }

    @Override
    public @NotNull ReleaseDate scrapeReleaseDate() {
        JSONObject pageJSON = getMovieJSON();
        String releaseDate = pageJSON.getString("Release");
        return new ReleaseDate(releaseDate);
    }

    @Override
    public @NotNull Year scrapeYear() {
        JSONObject pageJSON = getMovieJSON();
        String releaseYear = pageJSON.getString("Year");
        return new Year(releaseYear);
    }

    @Override
    public @NotNull Top250 scrapeTop250() {
        return Top250.BLANK_TOP250;
    }

    @Override
    public @NotNull Votes scrapeVotes() {
        return Votes.BLANK_VOTES;
    }

    @Override
    public @NotNull Outline scrapeOutline() {
        return Outline.BLANK_OUTLINE;
    }

    @Override
    public @NotNull Plot scrapePlot() {
        var json = getMovieJSON();
        String plot;
        if(scrapingLanguage == Language.ENGLISH){
            if(!json.isNull("DescEn")) {
                plot = json.getString("DescEn");
            } else {
                plot = json.getString("Desc");
            }
        } else {
            plot = json.getString("Desc");
        }

        return new Plot(plot);
    }

    @Override
    public @NotNull Tagline scrapeTagline() {
        return Tagline.BLANK_TAGLINE;
    }

    @Override
    public @NotNull Runtime scrapeRuntime() {
        JSONObject pageJSON = getMovieJSON();
        String duration = String.valueOf(pageJSON.getInt("Duration"));
        return new Runtime(duration);
    }

    @Override
    public @NotNull Thumb[] scrapePosters(boolean cropPosters) {
        ArrayList<Thumb> thumbList = new ArrayList<>();
        JSONObject pageJSON = getMovieJSON();
        try {

            // Some movies have a special poster "jacket". Use it as the primary poster instead of anything else.
            String[] thumbnailJsonNodes = {
                    "ThumbUltra",
                    "ThumbHigh",
                    "ThumbMed",
                    "ThumbLow"
            };
            // Iterate and make sure no duplicates get added
            for (var elem : thumbnailJsonNodes) {
                var url = pageJSON.getString(elem);
                if(!thumbList.isEmpty()) {
                    Thumb newThumb = new Thumb(url);
                    if(!thumbList.contains(newThumb))
                        thumbList.add(newThumb);
                } else {
                    thumbList.add(new Thumb(url));
                }
            }
            if(fileExistsAtURL(pageJSON.getString("MovieThumb"))){
                thumbList.add(new Thumb(pageJSON.getString("MovieThumb")));
            }

            return thumbList.toArray(new Thumb[thumbList.size()]);

        } catch (IOException ex) {
            Logger.getLogger(OnePondoParsingProfile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Thumb[0];
    }

    @Override
    public @NotNull Thumb[] scrapeFanart() {
        // TODO: Fanart available on HTML page
        return new Thumb[0];
    }

    @Override
    public @NotNull Thumb[] scrapeExtraFanart() {
        return new Thumb[0];
    }

    @Override
    public @NotNull MPAARating scrapeMPAA() {
        return MPAARating.RATING_XXX;
    }

    @Override
    public @NotNull ID scrapeID() {
        JSONObject pageJSON = getMovieJSON();
        String movieID = pageJSON.getString("MovieID");
        return new ID(movieID);
    }

    @Override
    public @NotNull ArrayList<Genre> scrapeGenres() {
        ArrayList<Genre> genreList = new ArrayList<>();
        return genreList;
    }

    @Override
    public @NotNull ArrayList<Actor> scrapeActors() {
        ArrayList<Actor> actorList = new ArrayList<>(1);
        JSONObject pageJSON = getMovieJSON();
        JSONArray actors;
        if(scrapingLanguage == Language.ENGLISH) {
            if(!pageJSON.isNull("AcressesEn")) {
                actors = pageJSON.getJSONArray("ActressesEn");
            } else {
                actors = pageJSON.getJSONArray("ActressesJa");
            }
        } else {
            actors = pageJSON.getJSONArray("ActressesJa");
        }
        for (Object actor : actors) {
            actorList.add(new Actor((String) actor, "", null));
        }

        return actorList;
    }

    @Override
    public @NotNull ArrayList<Director> scrapeDirectors() {
        ArrayList<Director> directorList = new ArrayList<>();
        return directorList;
    }

    @Override
    public @NotNull Studio scrapeStudio() {
        return new Studio("10musume");
    }

    @Override
    public @NotNull Trailer scrapeTrailer(){
        // TODO: Getting the trailer requires the HTML page
        return Trailer.BLANK_TRAILER;
    }

    @Override
    public @NotNull String createSearchString(File file) {
        return createSearchStringFromId(findIDTagFromFile(file));
    }

    @Override
    public String createSearchStringFromId(String id) {

        if(id.isEmpty() || id == null)
            return "";
        return "https://www.10musume.com/dyn/phpauto/movie_details/movie_id/" + id + ".json";
    }

    @Override
    public @NotNull SearchResult[] getSearchResults(String searchString) throws IOException {
        SearchResult searchResult = new SearchResult(searchString);
        searchResult.setJSONSearchResult(true);
        SearchResult[] searchResultArray = { searchResult };
        return searchResultArray;
    }

    @Override
    public SiteParsingProfile newInstance() {
        return new TenMusumeParsingProfile();
    }

    @Override
    public String getParserName() {
        return "10Musume";
    }

    @Override
    public Language[] getSupportedLanguages() {
        return new Language[] {
                Language.JAPANESE,
                Language.ENGLISH
        };
    }

    public static String findIDTagFromFile(File file) {
        return findIDTag(FilenameUtils.getName(file.getName()));
    }

    public static String findIDTag(String fileName) {
        Pattern pattern = Pattern.compile("\\d{6}_\\d{2,3}");
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            String searchString = matcher.group();
            return searchString;
        }
        return null;
    }
}
