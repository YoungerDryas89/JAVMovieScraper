package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific;

import com.github.youngerdryas89.moviescraper.controller.ScraperGroupName;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfile;
import com.github.youngerdryas89.moviescraper.model.dataitem.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

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

    @Override
    public List<ScraperGroupName> getScraperGroupNames() {
        return Arrays.asList(com.github.youngerdryas89.moviescraper.controller.ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP);
    }

}
