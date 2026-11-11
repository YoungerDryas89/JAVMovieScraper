package com.github.youngerdryas89.moviescraper.view;

import java.util.ArrayList;
import java.util.List;

import com.github.youngerdryas89.moviescraper.model.dataitem.Genre;
import com.github.youngerdryas89.moviescraper.model.preferences.MoviescraperPreferences;
import com.github.youngerdryas89.moviescraper.model.preferences.ScrapeSession;

public class FavoriteGenrePickerPanel extends FavoriteItemPickerPanel {

	private static final long serialVersionUID = 1L;
	public final static String listSeperator = ",,";
	ScrapeSession session;
	MoviescraperPreferences preferences;

	public FavoriteGenrePickerPanel(ScrapeSession session) {
		super();
		this.session = session;
		this.preferences = session.getPreferences();
	}

	@Override
	public String[] getSettingValues() {
		List<Genre> favoriteGenreArrayList = getFavoriteGenresFromPreferences(preferences);
		String[] genreArray = new String[favoriteGenreArrayList.size()];
		for (int i = 0; i < favoriteGenreArrayList.size(); i++) {
			genreArray[i] = favoriteGenreArrayList.get(i).getGenre();
		}
		return genreArray;
	}

	public static List<Genre> getFavoriteGenresFromPreferences(MoviescraperPreferences prefs) {
		String[] existingFavoriteGenresArray = prefs.frequentlyUsedGenres().split(listSeperator);
		List<Genre> favoriteGenresToReturn = new ArrayList<>(existingFavoriteGenresArray.length);
		for (String existingFavoriteGenre : existingFavoriteGenresArray) {
			favoriteGenresToReturn.add(new Genre(existingFavoriteGenre));
		}
		//alphabetical order list
		favoriteGenresToReturn.sort((p1, p2) -> p1.getGenre().compareTo(p2.getGenre()));
		return favoriteGenresToReturn;
	}

	@Override
	public void storeSettingValues() {
		String preferenceValue = "";
		for (int i = 0; i < favoritesListModel.size(); i++) {
			preferenceValue += favoritesListModel.get(i);
			if (i != favoritesListModel.size() - 1)
				preferenceValue += listSeperator;
		}
		session.setPreferences(preferences.toBuilder().frequentlyUsedGenres(preferenceValue).build());
	}

}
