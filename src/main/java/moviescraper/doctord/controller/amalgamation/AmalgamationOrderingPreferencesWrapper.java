package moviescraper.doctord.controller.amalgamation;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfileItem;
import moviescraper.doctord.controller.siteparsingprofile.SpecificProfileFactory;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.dataitem.DataItemSource;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONPropertyName;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static moviescraper.doctord.controller.amalgamation.ScraperGroupAmalgamationPreference.getMoviefieldNames;

@JsonRootName(value = "OrderingPreferences")
public class AmalgamationOrderingPreferencesWrapper {

    public Long version = 1L;

    public Map<SiteParsingProfile.ScraperGroupName, ScraperGroupAmalgamationPreference> allAmalgamationOrderingPreferences;
     public Collection<SiteParsingProfileItem> allScrapers = SpecificProfileFactory.getAll();

    public AmalgamationOrderingPreferencesWrapper(AllAmalgamationOrderingPreferences other) {
        allAmalgamationOrderingPreferences = other.getAllAmalgamationOrderingPreferences().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new ScraperGroupAmalgamationPreference(e.getValue())));
    }

    public AmalgamationOrderingPreferencesWrapper() {
    }

    public void saveData(String settingsFileName) throws IOException {

        ObjectMapper MAPPER = createConfiguredObjectMapper();
//        File out = new File(settingsFileName);
//        MAPPER.writeValue(out, this);
        try (FileOutputStream os = new FileOutputStream(settingsFileName)){
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            var root = mapper.createObjectNode();

            root.put("version", version);

            var preferences = root.putObject("OrderingPreferences");
            var american = writeAmalgamationDataForGroup(SiteParsingProfile.ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP, preferences);
            var japanese = writeAmalgamationDataForGroup(SiteParsingProfile.ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP, preferences);

            var json = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(root);
            os.write(json);
        }
        System.out.println("Saved amalgamation preferences to " + settingsFileName);
    }

    ObjectNode writeAmalgamationDataForGroup(SiteParsingProfile.@NotNull ScraperGroupName groupName, ObjectNode root){

        assert root != null;

        ObjectNode groupNode;
        if (groupName.equals(SiteParsingProfile.ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP)) {
            groupNode = root.putObject("American");
        } else {
            groupNode = root.putObject("Japanese");
        }

        var overall = groupNode.putArray("default");
        var custom = groupNode.putObject("custom");

        var items = allAmalgamationOrderingPreferences.get(groupName);

        items.overallOrdering.forEach(e -> {
            var entry = overall.addObject();
            entry.put("name", e.getDataItemSourceName());
            entry.put("enabled", e.isDisabled());
        });

        getMoviefieldNames().forEach(e -> {
            var field = custom.putArray(e.getName());

            var fieldItems = items.customAmalgamationOrderPerField.get(e.getName());
            if(fieldItems != null && !fieldItems.isEmpty()){
                fieldItems.forEach(item -> {
                    var entry = field.addObject();
                    entry.put("name", item.getDataItemSourceName());
                    entry.put("enabled", item.isDisabled());
                });
            }
        });

        return groupNode;
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
        var returnMap = new HashMap<SiteParsingProfile.ScraperGroupName, ScraperGroupAmalgamationPreference>();

        try (FileInputStream fis = new FileInputStream(settingsFilename)){
            ObjectMapper mapper = new ObjectMapper();
            var root = mapper.readTree(fis);

            if(!root.has("version"))
                throw new RuntimeException("Configuration missing node: 'version'");

            var preferencesNode = root.get("OrderingPreferences");

            var american = readOrdering(preferencesNode.get("American"), "American");
            var japanese = readOrdering(preferencesNode.get("Japanese"), "Japanese");

            returnMap.put(SiteParsingProfile.ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP, american);
            returnMap.put(SiteParsingProfile.ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP, japanese);
        }
        return returnMap;
    }

    ScraperGroupAmalgamationPreference readOrdering(JsonNode root, String group){
        SiteParsingProfile.ScraperGroupName groupName;
        if(group.equals("American")){
            groupName = SiteParsingProfile.ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP;
        } else if (group.equals("Japanese")){
            groupName = SiteParsingProfile.ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP;
        } else {
            throw new RuntimeException("Unknown scraper group name: " + group);
        }

        var overallOrdering = readListOfScrapers(root.get("default"));

        ScraperGroupAmalgamationPreference preference = new ScraperGroupAmalgamationPreference(groupName, overallOrdering);

        var custom = root.get("custom");
        var fieldNames = getMoviefieldNames().stream().map(Field::getName).toList();
        for(var field : fieldNames){
            try {
                if (custom.has(field)) {
                    var elems = readListOfScrapers(custom.get(field));
                    if(elems != null)
                        preference.setCustomOrderingForField(field, elems);
                } else {
                    System.err.println("WARNING: " + field + " is not recognized as a legitimate field within Movie class. Ignoring!");
                }
            } catch (NoSuchFieldException e) {
                System.err.println("WARNING: " + field + " is not recognized as a legitimate field within Movie class. Ignoring!");
            }
        }

        return preference;
    }

    @Nullable
    List<DataItemSource> readListOfScrapers(JsonNode root){
       assert root != null;

       if(root.size() > 0 && !root.isNull()) {

           List<DataItemSource> items = new ArrayList<>();

           root.forEach(node -> {

               var name = node.get("name").asText();


               var enabled = node.get("enabled").asBoolean();

               if (!name.equals("Default Data Item Source")) {
                   var profile = allScrapers.stream().filter(spp -> {
                       return spp.getParser().getDataItemSourceName().equals(name);
                   }).findFirst();

                   if (profile.isPresent()) {
                       var i = profile.get().getParser().createInstanceOfSameType();
                       i.setDisabled(enabled);
                       items.add(i);
                   } else {
                       System.err.println("WARNING: " + name + " is not recognized as a legitimate scraper. Ignoring!");
                   }
               }
           });

           return items;
       }

       return null;
    }

}
