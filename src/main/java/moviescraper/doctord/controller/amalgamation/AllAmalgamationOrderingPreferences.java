package moviescraper.doctord.controller.amalgamation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile.ScraperGroupName;
import moviescraper.doctord.controller.siteparsingprofile.specific.*;
import moviescraper.doctord.model.dataitem.DataItemSource;

public class AllAmalgamationOrderingPreferences {

	private Map<ScraperGroupName, ScraperGroupAmalgamationPreference> allAmalgamationOrderingPreferences;


	private static final String settingsFileName = "AmalgamationSettings.json";

    boolean loaded = false;



	public AllAmalgamationOrderingPreferences() {
		allAmalgamationOrderingPreferences = new Hashtable<>();
	}

	public AllAmalgamationOrderingPreferences(AllAmalgamationOrderingPreferences other){
		allAmalgamationOrderingPreferences = other.allAmalgamationOrderingPreferences.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> new ScraperGroupAmalgamationPreference(e.getValue())));
	}

	@Override
	public String toString() {
		return allAmalgamationOrderingPreferences.toString();
	}

    public boolean isLoaded(){
        return loaded;
    }

	public ScraperGroupAmalgamationPreference getScraperGroupAmalgamationPreference(ScraperGroupName scraperGroupName) {
		//make an attempt to reinitialize things if we added a new type of scraping group 
		//and our existing preferences didn't contain that type
		if (!allAmalgamationOrderingPreferences.containsKey(scraperGroupName)) {
			initializeDefaultPreference(scraperGroupName);
		}
		return allAmalgamationOrderingPreferences.get(scraperGroupName);
	}

	//TODO: Good candidate to do this in a more object oriented way
	private void initializeDefaultPreference(ScraperGroupName scraperGroupName) {
		if (scraperGroupName == ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP)
			initializeAmericanAdultDVDScraperGroupDefaultPreferences();
		if (scraperGroupName == ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP)
			initializeJAVCensoredGroupDefaultPreferences();
	}

	public void putScraperGroupAmalgamationPreference(ScraperGroupName scraperGroupName, ScraperGroupAmalgamationPreference pref) {
		allAmalgamationOrderingPreferences.put(scraperGroupName, pref);
	}

	public void initializeValuesFromPreferenceFile() {
        if (!Files.exists(Path.of(settingsFileName))) {
            if(Files.exists(Path.of("AmalgamationSettings.xml"))) {
                AmalgamationOrderingLegacyHandler handler = new AmalgamationOrderingLegacyHandler();
                var data = handler.loadData();
                if (data != null) {
                    allAmalgamationOrderingPreferences = data;
                    loaded = true;
                    return;
                }
            }

            initializeDefaultPreferences(true);
        } else {
            try {
               AmalgamationOrderingPreferencesWrapper wrapper = new AmalgamationOrderingPreferencesWrapper();
               var returnMap = wrapper.loadData(settingsFileName);

               if(returnMap.isEmpty())
                   initializeDefaultPreferences(true);
               else
                   loaded = true;
            } catch (IOException e) {
                System.err.println("Could not read amalgamation settings file, loading defaults. Error: " + e.getMessage());
                // If file is corrupt or structure changed, load defaults and overwrite.
                initializeDefaultPreferences(true);
            }
        }
	}

    public List<String> preferenceOrderingToString(List<DataItemSource> pref){
        return pref.stream().map(DataItemSource::getDataItemSourceName).toList();
    }

	public void saveToPreferencesFile() {
		try {
            AmalgamationOrderingPreferencesWrapper wrapper = new AmalgamationOrderingPreferencesWrapper(this);
            wrapper.saveData(settingsFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initializeDefaultPreferences(boolean saveToDisk) {
		if (!allAmalgamationOrderingPreferences.containsKey(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP)) {
			initializeJAVCensoredGroupDefaultPreferences();
		}
		if (!allAmalgamationOrderingPreferences.containsKey(ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP)) {
			initializeAmericanAdultDVDScraperGroupDefaultPreferences();
		}
		//TODO: define a default ordering for all other scraper types

		if (saveToDisk) {
			saveToPreferencesFile();
		}

        loaded = true;
	}

	private void initializeAmericanAdultDVDScraperGroupDefaultPreferences() {
		System.out.println("Initializing default american adult dvd preferences");
		var overallOrdering = DataItemSourceAmalgamationPreference.createPreferenceOrdering(new TheMovieDatabaseParsingProfile(), new Data18MovieParsingProfile(),
		        new ExcaliburFilmsParsingProfile(), new IAFDParsingProfile());
		ScraperGroupAmalgamationPreference preferences = new ScraperGroupAmalgamationPreference(ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP, overallOrdering);

		allAmalgamationOrderingPreferences.put(ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP, preferences);
	}

	private void setCustomOrderingForField(ScraperGroupAmalgamationPreference preferences, String fieldName, DataItemSource... sources) throws NoSuchFieldException {
		var ordering = DataItemSourceAmalgamationPreference.createPreferenceOrdering(sources);
		preferences.setCustomOrderingForField(fieldName, ordering);
	}

	private void initializeJAVCensoredGroupDefaultPreferences() {
		System.out.println("Initializing default jav preferences");

		//JAV Preferences

		var overallOrdering = DataItemSourceAmalgamationPreference.createPreferenceOrdering(new AV123ParsingProfile(), new JavLibraryParsingProfile(), new AvMooParsingProfile(),
		        new SquarePlusParsingProfile(), new JavBusParsingProfile(), new ActionJavParsingProfile(), new DmmParsingProfile());

		ScraperGroupAmalgamationPreference preferences = new ScraperGroupAmalgamationPreference(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP, overallOrdering);

		//Specific preferences for each field of JAV
		try {
			setCustomOrderingForField(preferences, "originalTitle", new DmmParsingProfile(), new JavLibraryParsingProfile(), new AvMooParsingProfile(), new JavBusParsingProfile());
			setCustomOrderingForField(preferences, "id", new AV123ParsingProfile(), new JavLibraryParsingProfile(), new ActionJavParsingProfile(), new SquarePlusParsingProfile(), new AvMooParsingProfile(), new JavBusParsingProfile());
			setCustomOrderingForField(preferences, "title", new JavLibraryParsingProfile(), new ActionJavParsingProfile(), new SquarePlusParsingProfile(), new AvMooParsingProfile(), new JavBusParsingProfile(), new DmmParsingProfile());
			setCustomOrderingForField(preferences, "plot", new ActionJavParsingProfile(), new DmmParsingProfile());
			setCustomOrderingForField(preferences, "set", new AvMooParsingProfile(), new JavBusParsingProfile(), new DmmParsingProfile());
			setCustomOrderingForField(preferences, "studio", new JavLibraryParsingProfile(), new ActionJavParsingProfile(), new AvMooParsingProfile(), new JavBusParsingProfile(), new SquarePlusParsingProfile(), new DmmParsingProfile());
			setCustomOrderingForField(preferences, "genres", new JavLibraryParsingProfile(), new AvMooParsingProfile(), new JavBusParsingProfile(), new SquarePlusParsingProfile(), new ActionJavParsingProfile(), new DmmParsingProfile());

			var bestContentForActorsAndDirectors = DataItemSourceAmalgamationPreference.createPreferenceOrdering(new JavLibraryParsingProfile(),
			        new AvMooParsingProfile(), new JavBusParsingProfile(), new ActionJavParsingProfile(), new DmmParsingProfile(), new SquarePlusParsingProfile());
			preferences.setCustomOrderingForField("actors", bestContentForActorsAndDirectors);
			preferences.setCustomOrderingForField("directors", bestContentForActorsAndDirectors);

			var bestContentForPosterAndFanart = DataItemSourceAmalgamationPreference.createPreferenceOrdering(new DmmParsingProfile(),
			        new JavLibraryParsingProfile(), new ActionJavParsingProfile(), new SquarePlusParsingProfile(), new AvMooParsingProfile(), new JavBusParsingProfile());
			preferences.setCustomOrderingForField("posters", bestContentForPosterAndFanart);
			preferences.setCustomOrderingForField("fanart", bestContentForPosterAndFanart);
			preferences.setCustomOrderingForField("extraFanart", bestContentForPosterAndFanart);

			setCustomOrderingForField(preferences, "trailer", new DmmParsingProfile());
			setCustomOrderingForField(preferences, "rating", new JavLibraryParsingProfile(), new DmmParsingProfile());

			var bestContentForDateAndTime = DataItemSourceAmalgamationPreference.createPreferenceOrdering(new DmmParsingProfile(), new JavLibraryParsingProfile(),
			        new ActionJavParsingProfile(), new SquarePlusParsingProfile(), new AvMooParsingProfile(), new JavBusParsingProfile());
			preferences.setCustomOrderingForField("year", bestContentForDateAndTime);
			preferences.setCustomOrderingForField("releaseDate", bestContentForDateAndTime);
			preferences.setCustomOrderingForField("runtime", bestContentForDateAndTime);

			allAmalgamationOrderingPreferences.put(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP, preferences);
		} catch (NoSuchFieldException e) {
			// This should not happen if field names are correct.
			throw new RuntimeException("Error initializing default JAV preferences", e);
		}
	}

	public void reinitializeDefaultPreferences() {
		allAmalgamationOrderingPreferences.clear();
		boolean saveToDisk = false;
		initializeDefaultPreferences(saveToDisk);
	}

	public Map<ScraperGroupName, ScraperGroupAmalgamationPreference> getAllAmalgamationOrderingPreferences() {
		return allAmalgamationOrderingPreferences;
	}

	public void setAllAmalgamationOrderingPreferences(Map<ScraperGroupName, ScraperGroupAmalgamationPreference> allAmalgamationOrderingPreferences) {
		this.allAmalgamationOrderingPreferences = allAmalgamationOrderingPreferences;
	}

}

