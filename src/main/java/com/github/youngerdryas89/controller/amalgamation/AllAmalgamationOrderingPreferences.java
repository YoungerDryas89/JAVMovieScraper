package com.github.youngerdryas89.controller.amalgamation;

import java.io.*;
import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Collectors;

import com.cedarsoftware.util.io.JsonIoException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.github.youngerdryas89.controller.siteparsingprofile.SiteParsingProfile.ScraperGroupName;
import com.github.youngerdryas89.controller.siteparsingprofile.specific.*;
import org.apache.commons.io.IOUtils;

public class AllAmalgamationOrderingPreferences {

	Map<ScraperGroupName, ScraperGroupAmalgamationPreference> allAmalgamationOrderingPreferences;
	private static final String settingsFileName = "AmalgamationSettings.xml";
	private XStream xstream;

	public AllAmalgamationOrderingPreferences() {
		allAmalgamationOrderingPreferences = new Hashtable<>();
		xstream = new XStream(new DomDriver());
		xstream.allowTypesByWildcard(
				new String[]{
						"com.github.youngerdryas89.controller.amalgamation.*",
						"com.github.youngerdryas89.controller.siteparsingprofile.*",
						"com.github.youngerdryas89.model.preferences.Settings.*",
						"com.github.youngerdryas89.model.dataitem.*",
						"com.github.youngerdryas89.controller.siteparsingprofile.specific.*",
                        "com.github.youngerdryas89.scraper.*"
				}
		);
		xstream.alias("ScraperGroupAmalgamationPreferences", ScraperGroupAmalgamationPreference.class);
		xstream.alias("ScraperGroupName", ScraperGroupName.class);
		xstream.alias("OrderingSettings", AllAmalgamationOrderingPreferences.class);

		initializeValuesFromPreferenceFile();
	}

	public AllAmalgamationOrderingPreferences(AllAmalgamationOrderingPreferences other){
		allAmalgamationOrderingPreferences = other.allAmalgamationOrderingPreferences.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@Override
	public String toString() {
		return allAmalgamationOrderingPreferences.toString();
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

		File inputFile = new File(settingsFileName);
		if (!inputFile.exists()) {
			boolean saveToDisk = true;
			initializeDefaultPreferences(saveToDisk);
			System.out.println("No file existed for amalgamation preferences. Used default preferences.");
		} else {
			try (FileInputStream inputFromFile = new FileInputStream(settingsFileName);) {
				var data = IOUtils.toString(inputFromFile, "UTF-8");
				if(!data.isEmpty())
					allAmalgamationOrderingPreferences = (Map<ScraperGroupName, ScraperGroupAmalgamationPreference>) xstream.fromXML(data);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (JsonIoException e) {
				System.out.println("Preference file is not compatible between versions - reinitializing preference file");
				initializeDefaultPreferences(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void saveToPreferencesFile() {
		try (FileOutputStream writer = new FileOutputStream(settingsFileName)) {
			var data = xstream.toXML(allAmalgamationOrderingPreferences);
			writer.write(data.getBytes());
			System.out.println("Saved amalgamation preferences to " + settingsFileName);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
	}

	private void initializeAmericanAdultDVDScraperGroupDefaultPreferences() {
		System.out.println("Initializing default american adult dvd preferences");
		DataItemSourceAmalgamationPreference overallOrdering = new DataItemSourceAmalgamationPreference(new TheMovieDatabaseParsingProfile(), new Data18MovieParsingProfile(),
		        new ExcaliburFilmsParsingProfile(), new IAFDParsingProfile());
		ScraperGroupAmalgamationPreference preferences = new ScraperGroupAmalgamationPreference(ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP, overallOrdering);

		allAmalgamationOrderingPreferences.put(ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP, preferences);
	}

	private void initializeJAVCensoredGroupDefaultPreferences() {
		System.out.println("Initializing default jav preferences");

		//JAV Preferences

		DataItemSourceAmalgamationPreference overallOrdering = new DataItemSourceAmalgamationPreference(new NJavParsingProfile(), new JavLibraryParsingProfile(), new AvMooParsingProfile(),
		        new SquarePlusParsingProfile(), new JavBusParsingProfile(), new ActionJavParsingProfile(), new DmmParsingProfile());

		ScraperGroupAmalgamationPreference preferences = new ScraperGroupAmalgamationPreference(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP, overallOrdering);

		//Specific preferences for each field of JAV
		try {
			// DMM, JavLibrary, AvMoo have japanese title, only DMM is scraped at the moment
			DataItemSourceAmalgamationPreference bestContentForOriginalTitle = new DataItemSourceAmalgamationPreference(new DmmParsingProfile(), new JavLibraryParsingProfile(),
			        new AvMooParsingProfile(), new JavBusParsingProfile());
			preferences.setCustomOrderingForField("originalTitle", bestContentForOriginalTitle);

			// R18 has the absolute best title information. Pick any english
			// site first, fallback to machine translated DMM
			DataItemSourceAmalgamationPreference bestContentForID = new DataItemSourceAmalgamationPreference(new NJavParsingProfile(), new JavLibraryParsingProfile(),
			        new ActionJavParsingProfile(), new SquarePlusParsingProfile(), new AvMooParsingProfile(), new JavBusParsingProfile());
			preferences.setCustomOrderingForField("id", bestContentForID);

			// R18 has the absolute best title information. Pick any english
			// site first, fallback to machine translated DMM
			DataItemSourceAmalgamationPreference bestContentForTitle = new DataItemSourceAmalgamationPreference(new JavLibraryParsingProfile(), new ActionJavParsingProfile(),
			        new SquarePlusParsingProfile(), new AvMooParsingProfile(), new JavBusParsingProfile(), new DmmParsingProfile());
			preferences.setCustomOrderingForField("title", bestContentForTitle);

			// R18 has the best plot data for english. Set the plot from
			// ActionJav only if R18 didn't have one already
			DataItemSourceAmalgamationPreference bestContentForPlot = new DataItemSourceAmalgamationPreference(new ActionJavParsingProfile(), new DmmParsingProfile());
			preferences.setCustomOrderingForField("plot", bestContentForPlot);

			// R18 has the best set data for english, AvMoo is OK
			DataItemSourceAmalgamationPreference bestContentForSet = new DataItemSourceAmalgamationPreference(new AvMooParsingProfile(), new JavBusParsingProfile(),
			        new DmmParsingProfile());
			preferences.setCustomOrderingForField("set", bestContentForSet);

			// R18 has the best studio data for english
			DataItemSourceAmalgamationPreference bestContentForStudio = new DataItemSourceAmalgamationPreference(new JavLibraryParsingProfile(), new ActionJavParsingProfile(),
			        new AvMooParsingProfile(), new JavBusParsingProfile(), new SquarePlusParsingProfile(), new DmmParsingProfile());
			preferences.setCustomOrderingForField("studio", bestContentForStudio);

			// R18 has the best genre data for english, fallback to machine
			// translated DMM data
			DataItemSourceAmalgamationPreference bestContentForGenres = new DataItemSourceAmalgamationPreference(new JavLibraryParsingProfile(), new AvMooParsingProfile(),
			        new JavBusParsingProfile(), new SquarePlusParsingProfile(), new ActionJavParsingProfile(), new DmmParsingProfile());
			preferences.setCustomOrderingForField("genres", bestContentForGenres);

			// Get ActionJav actors if both JavLib and R18 didn't have any.
			DataItemSourceAmalgamationPreference bestContentForActorsAndDirectors = new DataItemSourceAmalgamationPreference(new JavLibraryParsingProfile(),
			        new AvMooParsingProfile(), new JavBusParsingProfile(), new ActionJavParsingProfile(), new DmmParsingProfile(), new SquarePlusParsingProfile());
			preferences.setCustomOrderingForField("actors", bestContentForActorsAndDirectors);
			preferences.setCustomOrderingForField("directors", bestContentForActorsAndDirectors);

			// DMM always has the best fanart and posters and extraFanart
			DataItemSourceAmalgamationPreference bestContentForPosterAndFanart = new DataItemSourceAmalgamationPreference(new DmmParsingProfile(),
			        new JavLibraryParsingProfile(), new ActionJavParsingProfile(), new SquarePlusParsingProfile(), new AvMooParsingProfile(), new JavBusParsingProfile());
			preferences.setCustomOrderingForField("posters", bestContentForPosterAndFanart);
			preferences.setCustomOrderingForField("fanart", bestContentForPosterAndFanart);
			preferences.setCustomOrderingForField("extraFanart", bestContentForPosterAndFanart);

			// Both DMM and R18 have the same trailer from their respective
			// sites
			DataItemSourceAmalgamationPreference bestContentForTrailer = new DataItemSourceAmalgamationPreference(new DmmParsingProfile());
			preferences.setCustomOrderingForField("trailer", bestContentForTrailer);

			// Only DMM and JavLibrary has ratings
			DataItemSourceAmalgamationPreference bestContentForRating = new DataItemSourceAmalgamationPreference(new JavLibraryParsingProfile(), new DmmParsingProfile());
			preferences.setCustomOrderingForField("rating", bestContentForRating);

			// Non localized data: year, release date, runtime...
			DataItemSourceAmalgamationPreference bestContentForDateAndTime = new DataItemSourceAmalgamationPreference(new DmmParsingProfile(), new JavLibraryParsingProfile(),
			        new ActionJavParsingProfile(), new SquarePlusParsingProfile(), new AvMooParsingProfile(), new JavBusParsingProfile());
			preferences.setCustomOrderingForField("year", bestContentForDateAndTime);
			preferences.setCustomOrderingForField("releaseDate", bestContentForDateAndTime);
			preferences.setCustomOrderingForField("runtime", bestContentForDateAndTime);

			allAmalgamationOrderingPreferences.put(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP, preferences);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
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
