package com.github.youngerdryas89.moviescraper.controller.amalgamation;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfile;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class AmalgamationOrderingLegacyHandler {
    XStream xstream;
    AmalgamationOrderingLegacyHandler() {

    xstream = new XStream(new DomDriver());
        xstream.allowTypesByWildcard(
                new String[]{
                        "com.github.youngerdryas89.moviescraper.controller.amalgamation.*",
                        "com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.*",
                        "preferences.model.com.github.youngerdryas89.moviescraper.Settings.*",
                        "com.github.youngerdryas89.moviescraper.model.dataitem.*",
                        "com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.*",
                        "com.github.youngerdryas89.moviescraper.scraper.*"
                }
        );
        xstream.alias("ScraperGroupAmalgamationPreferences", ScraperGroupAmalgamationPreference.class);
        xstream.alias("ScraperGroupName", SiteParsingProfile.ScraperGroupName.class);
        xstream.alias("OrderingSettings", AllAmalgamationOrderingPreferences.class);
    }

    @Nullable
    public Map<SiteParsingProfile.ScraperGroupName, ScraperGroupAmalgamationPreference> loadData() {
        try (FileInputStream inputFromFile = new FileInputStream("AmalgamationSettings.xml");) {
            var data = IOUtils.toString(inputFromFile, "UTF-8");
            if (!data.isEmpty())
                return (Map<SiteParsingProfile.ScraperGroupName, ScraperGroupAmalgamationPreference>) xstream.fromXML(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CannotResolveClassException | IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
