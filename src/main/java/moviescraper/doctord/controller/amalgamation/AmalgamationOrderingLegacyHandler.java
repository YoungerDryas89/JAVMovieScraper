package moviescraper.doctord.controller.amalgamation;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONPropertyName;

import java.io.File;
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
                        "moviescraper.doctord.controller.amalgamation.*",
                        "moviescraper.doctord.controller.siteparsingprofile.*",
                        "moviescraper.doctord.model.preferences.Settings.*",
                        "moviescraper.doctord.model.dataitem.*",
                        "moviescraper.doctord.controller.siteparsingprofile.specific.*",
                        "moviescraper.doctord.scraper.*"
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
