package com.github.youngerdryas89.moviescraper.controller.amalgamation;

import java.lang.reflect.Field;
import java.util.*;

import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfile.ScraperGroupName;
import com.github.youngerdryas89.moviescraper.model.Movie;
import com.github.youngerdryas89.moviescraper.model.dataitem.DataItemSource;

/**
 * A ScraperGroupAmalgamtionPreference is the preferred order of scrapers to use when amalgamating data
 * plus an optional list of fields that have their own ordering of scrapers to use just for that field
 */
public class ScraperGroupAmalgamationPreference {

	private ScraperGroupName scraperGroupName;

	List<DataItemSource> overallOrdering;

	Map<String, List<DataItemSource>> customAmalgamationOrderPerField;


    public ScraperGroupAmalgamationPreference(){
        overallOrdering = new ArrayList<>();
        customAmalgamationOrderPerField = new HashMap<>();
    }

	public ScraperGroupAmalgamationPreference(ScraperGroupName scraperGroupName, List<DataItemSource> overallOrdering) {
		this.scraperGroupName = scraperGroupName;
		this.overallOrdering = overallOrdering;
	}

	public ScraperGroupAmalgamationPreference(ScraperGroupAmalgamationPreference other) {
		this.scraperGroupName = other.scraperGroupName;
		if (other.overallOrdering != null) {
			this.overallOrdering = new ArrayList<>(other.overallOrdering);
		} else {
			this.overallOrdering = new ArrayList<>();
		}
		if (other.customAmalgamationOrderPerField != null) {
			this.customAmalgamationOrderPerField = new HashMap<>();
			other.customAmalgamationOrderPerField.forEach((key, value) -> this.customAmalgamationOrderPerField.put(key, new ArrayList<>(value)));
		} else {
			this.customAmalgamationOrderPerField = new HashMap<>();
		}
	}

	/**
	 * Get the specific ordering for this field, or the overall ordering if there wasn't a specific ordering found
	 * 
	 * @param field
	 * @return
	 */
	public List<DataItemSource> getAmalgamationPreference(Field field) {
		if (field != null && customAmalgamationOrderPerField != null && customAmalgamationOrderPerField.containsKey(field.getName())) {
			return customAmalgamationOrderPerField.get(field.getName());
		} else {
			return overallOrdering;
		}
	}

    public List<DataItemSource> getPreference(String field) {
        assert field != null;
        assert !field.isEmpty();

        if(customAmalgamationOrderPerField != null && customAmalgamationOrderPerField.containsKey(field))
            return customAmalgamationOrderPerField.get(field);
        else
            return overallOrdering;
    }

	/**
	 * @return the list of scrapers this scraper group should scrape when doing amalgamating
	 */
	public List<DataItemSource> getActiveScrapersUsedInOverallPreference() {
		List<DataItemSource> activeScrapers = new ArrayList<>();
		for (DataItemSource currentItemSource : overallOrdering) {
			System.out.println("currentItemSource = " + currentItemSource + " with disabled = " + currentItemSource.isDisabled());
			//create a new instance may cause a problem if we depend on local state set in the parsing profile
			//so I may need to revisit this. I did this because there was a problem with the TMDB scraper saving local state inside itself as it was scraping

			if (!currentItemSource.isDisabled()) {
				activeScrapers.add(currentItemSource.createInstanceOfSameType());
			}
		}
		return activeScrapers;
	}

	/**
	 * Get the specific ordering for this field, or null if there isn't one set
	 * 
	 * @param field - field to look up the ordering on
	 */
	public List<DataItemSource> getSpecificAmalgamationPreference(Field field) {
		if (field != null && customAmalgamationOrderPerField != null && customAmalgamationOrderPerField.containsKey(field.getName())) {
			return customAmalgamationOrderPerField.get(field.getName());
		}
		return null;
	}

	public List<DataItemSource> getOverallAmalgamationPreference() {
		return overallOrdering;
	}

	public void setCustomOrderingForField(Field field, List<DataItemSource> newValue) {
		if (customAmalgamationOrderPerField == null) {
			customAmalgamationOrderPerField = new Hashtable<>(Movie.class.getDeclaredFields().length);
		}
		customAmalgamationOrderPerField.put(field.getName(), newValue);
	}

	public void setCustomOrderingForField(String fieldName, List<DataItemSource> newValue) throws NoSuchFieldException, SecurityException {
		setCustomOrderingForField(Movie.class.getDeclaredField(fieldName), newValue);
	}

	public void removeCustomOrderingForField(Field field) {
		customAmalgamationOrderPerField.remove(field.getName());
	}

	public static List<Field> getMoviefieldNames() {
		LinkedList<Field> fieldNames = new LinkedList<>();
		Movie currentMovie = Movie.getEmptyMovie();
		String[] disallowedFieldNames = { "readTimeout", "connectionTimeout", "preferredFanartToWriteToDisk", "allTitles", "fileName", "$assertionsDisabled" };
		ArrayList<String> disallowedFieldNamesArrayList = new ArrayList<>(Arrays.asList(disallowedFieldNames));
		for (Field field : currentMovie.getClass().getDeclaredFields()) {
			String fieldName = field.getName();
			if (!disallowedFieldNamesArrayList.contains(fieldName)) {

				fieldNames.add(field);
			}
		}

		return fieldNames;
	}

	@Override
	public String toString() {
		return "ScraperGroupAmalgamationPreference [scraperGroupName = " + scraperGroupName.toString() + " overallOrdering = " + overallOrdering.toString() + " customAmalgamationPerField "
		        + customAmalgamationOrderPerField + "]";
	}

	public String toFriendlyString() {
		return scraperGroupName.toString();
	}

	public ScraperGroupName getScraperGroupName() {
		return scraperGroupName;
	}

}
