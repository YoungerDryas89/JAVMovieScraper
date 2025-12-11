package com.github.youngerdryas89.moviescraper.controller.amalgamation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfileItem;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileFactory;
import com.github.youngerdryas89.moviescraper.model.Movie;
import com.github.youngerdryas89.moviescraper.model.dataitem.DataItemSource;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.youngerdryas89.moviescraper.controller.amalgamation.ScraperGroupAmalgamationPreference.getMoviefieldNames;

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

        try (FileOutputStream os = new FileOutputStream(settingsFileName)){
            ObjectMapper mapper = createConfiguredObjectMapper();

            var root = mapper.createObjectNode();

            root.put("version", version);

            var preferences = root.putObject("OrderingPreferences");
            var american = writeAmalgamationDataForGroup("American", preferences);
            var japanese = writeAmalgamationDataForGroup("Japanese", preferences);

            var json = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(root);
            os.write(json);
        }
        System.out.println("Saved amalgamation preferences to " + settingsFileName);
    }

    ObjectNode writeAmalgamationDataForGroup(String groupName, ObjectNode root){

        assert root != null;

        ScraperGroupAmalgamationPreference items;

        ObjectNode groupNode;
        if (groupName.equals("American")) {
            groupNode = root.putObject(groupName);
            items = allAmalgamationOrderingPreferences.get(SiteParsingProfile.ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP);
        } else {
            groupNode = root.putObject("Japanese");
            items = allAmalgamationOrderingPreferences.get(SiteParsingProfile.ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP);
        }

        var overall = groupNode.putArray("default");
        var custom = groupNode.putObject("custom");


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
