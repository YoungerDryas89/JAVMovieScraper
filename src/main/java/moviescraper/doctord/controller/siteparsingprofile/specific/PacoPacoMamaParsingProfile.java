package moviescraper.doctord.controller.siteparsingprofile.specific;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfileJSON;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.*;
import moviescraper.doctord.model.dataitem.Runtime;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PacoPacoMamaParsingProfile extends SiteParsingProfileJSON implements SpecificProfile {
    @Override
    public @NotNull Title scrapeTitle() {
        return null;
    }

    @Override
    public @NotNull OriginalTitle scrapeOriginalTitle() {
        return null;
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
        return null;
    }

    @Override
    public @NotNull ReleaseDate scrapeReleaseDate() {
        return null;
    }

    @Override
    public @NotNull Year scrapeYear() {
        return null;
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
        return null;
    }

    @Override
    public @NotNull Tagline scrapeTagline() {
        return Tagline.BLANK_TAGLINE;
    }

    @Override
    public @NotNull Runtime scrapeRuntime() {
        return null;
    }

    @Override
    public @NotNull Thumb[] scrapePosters(boolean cropPosters) {
        return new Thumb[0];
    }

    @Override
    public @NotNull Thumb[] scrapeFanart() {
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
        return null;
    }

    @Override
    public @NotNull ArrayList<Genre> scrapeGenres() {
        return null;
    }

    @Override
    public @NotNull ArrayList<Actor> scrapeActors() {
        return null;
    }

    @Override
    public @NotNull ArrayList<Director> scrapeDirectors() {
        return new ArrayList<>();
    }

    @Override
    public @NotNull Studio scrapeStudio() {
        return null;
    }

    @Override
    public @NotNull String createSearchString(File file) {
        return "";
    }

    @Override
    public String createSearchStringFromId(String id) {
        return "https://www.pacopacomama.com/dyn/phpauto/movie_details/movie_id/" + id + ".json";
    }

    @Override
    public @NotNull SearchResult[] getSearchResults(String searchString) throws IOException {
        return new SearchResult[0];
    }

    @Override
    public SiteParsingProfile newInstance() {
        return new PacoPacoMamaParsingProfile();
    }

    @Override
    public String getParserName() {
        return "PacoPacoMama";
    }
}
