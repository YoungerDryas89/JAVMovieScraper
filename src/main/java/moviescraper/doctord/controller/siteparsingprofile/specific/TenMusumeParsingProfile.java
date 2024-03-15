package moviescraper.doctord.controller.siteparsingprofile.specific;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfileJSON;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.*;
import moviescraper.doctord.model.dataitem.Runtime;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class TenMusumeParsingProfile extends OnePondoParsingProfile implements SpecificProfile {
    @Override
    public Thumb[] scrapeFanart() {
        // TODO: Implement getting fan art
        return new Thumb[0];
    }

    @Override
    public String createSearchString(File file) {
        return createSearchStringFromId(findIDTagFromFile(file, true));
    }

    @Override
    public String createSearchStringFromId(String id) {
        String url = "";
        if(id != null || !id.isEmpty()) {
            url = "https://www.10musume.com/dyn/phpauto/movie_details/movie_id/" + id + ".json";
        }
        return url;
    }

    @Override
    public SearchResult[] getSearchResults(String searchString) throws IOException {
        SearchResult result = new SearchResult(searchString);
        result.setJSONSearchResult(true);
        return new SearchResult[]{result};
    }

    @Override
    public SiteParsingProfile newInstance() {
        return new TenMusumeParsingProfile();
    }

    @Override
    public String getParserName() {
        return "10musume";
    }

    @Override
    public String getDomain(){
        return "www.10musume.com";
    }

    @Override
    public String getTrailerDomain(){
        return "smovie.10musume.com";
    }
}
