package com.github.youngerdryas89.moviescraper.view;

import java.util.ArrayList;
import com.github.youngerdryas89.moviescraper.model.dataitem.Tag;
import com.github.youngerdryas89.moviescraper.model.preferences.MoviescraperPreferences;
import com.github.youngerdryas89.moviescraper.model.preferences.ScrapeSession;

public class FavoriteTagPickerPanel extends FavoriteItemPickerPanel {

	private static final long serialVersionUID = 1L;
	public final static String listSeperator = ",,";
	MoviescraperPreferences preferences;
	ScrapeSession session;

	public FavoriteTagPickerPanel(ScrapeSession session) {
		super();
		this.session = session;
		this.preferences = session.getPreferences();
	}

	@Override
	public String[] getSettingValues() {
		ArrayList<Tag> favoriteTagArrayList = getFavoriteTagsFromPreferences();
		String[] tagArray = new String[favoriteTagArrayList.size()];
		for (int i = 0; i < favoriteTagArrayList.size(); i++) {
			tagArray[i] = favoriteTagArrayList.get(i).getTag();
		}
		return tagArray;
	}

	public static ArrayList<Tag> getFavoriteTagsFromPreferences(MoviescraperPreferences preferences) {
		String[] existingFavoriteTagsArray = preferences.frequentlyUsedTags().split(listSeperator);
		ArrayList<Tag> favoriteTagsToReturn = new ArrayList<>(existingFavoriteTagsArray.length);
		for (String existingFavoriteTag : existingFavoriteTagsArray) {
			favoriteTagsToReturn.add(new Tag(existingFavoriteTag));
		}
		//alphabetical order list
		favoriteTagsToReturn.sort((p1, p2) -> p1.getTag().compareTo(p2.getTag()));
		return favoriteTagsToReturn;
	}

	@Override
	public void storeSettingValues() {
		String preferenceValue = "";
		for (int i = 0; i < favoritesListModel.size(); i++) {
			preferenceValue += favoritesListModel.get(i);
			if (i != favoritesListModel.size() - 1)
				preferenceValue += listSeperator;
		}
		session.setPreferences(preferences.toBuilder().frequentlyUsedTags(preferenceValue).build());

	}

}
