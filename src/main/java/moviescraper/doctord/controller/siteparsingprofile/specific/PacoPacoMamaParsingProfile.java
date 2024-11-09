package moviescraper.doctord.controller.siteparsingprofile.specific;

import moviescraper.doctord.controller.languagetranslation.Language;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfileJSON;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.*;
import moviescraper.doctord.model.dataitem.Runtime;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PacoPacoMamaParsingProfile extends TenMusumeParsingProfile {
    @Override
    public String createSearchStringFromId(String id) {
        return "https://www.pacopacomama.com/dyn/phpauto/movie_details/movie_id/" + id + ".json";
    }


    @Override
    public @NotNull Studio scrapeStudio(){
        return new Studio("PacoPacoMama");
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
