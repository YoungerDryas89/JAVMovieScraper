package moviescraper.doctord.controller.amalgamation;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.dataitem.DataItemSource;
import org.json.JSONPropertyName;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@JsonRootName(value = "OrderingPreferences")
public class AmalgamationOrderingPreferencesWrapper {

    public Long version = 1L;

    public Map<SiteParsingProfile.ScraperGroupName, ScraperGroupAmalgamationPreference> allAmalgamationOrderingPreferences;

    public AmalgamationOrderingPreferencesWrapper(AllAmalgamationOrderingPreferences other) {
        allAmalgamationOrderingPreferences = other.getAllAmalgamationOrderingPreferences().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new ScraperGroupAmalgamationPreference(e.getValue())));
    }

    public AmalgamationOrderingPreferencesWrapper() {
    }

    public void saveData(String settingsFileName) throws IOException {

        ObjectMapper MAPPER = createConfiguredObjectMapper();
        File out = new File(settingsFileName);
        MAPPER.writeValue(out, this);
        System.out.println("Saved amalgamation preferences to " + settingsFileName);
    }

    public ObjectMapper createConfiguredObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        SimpleModule module = new SimpleModule();
        module.addSerializer(DataItemSource.class, new DataItemSourceJsonSerializer());
        module.addDeserializer(DataItemSource.class, new DataItemSourceJsonDeserializer());
        mapper.registerModule(module);

        return mapper;
    }

    public Map<SiteParsingProfile.ScraperGroupName, ScraperGroupAmalgamationPreference> loadData(String settingsFilename) throws IOException {
        ObjectMapper mapper = createConfiguredObjectMapper();
        File settingsFile = new File(settingsFilename);
        return mapper.readValue(settingsFile, new TypeReference<>() {
        });
    }

}
