package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific;

import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfileJSON;
import com.github.youngerdryas89.moviescraper.model.SearchResult;
import com.github.youngerdryas89.moviescraper.model.dataitem.*;
import com.github.youngerdryas89.moviescraper.model.dataitem.Runtime;
import org.jetbrains.annotations.NotNull;

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
