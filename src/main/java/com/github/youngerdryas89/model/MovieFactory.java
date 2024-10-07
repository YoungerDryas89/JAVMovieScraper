package com.github.youngerdryas89.model;

import java.util.ArrayList;
import com.github.youngerdryas89.model.dataitem.Actor;
import com.github.youngerdryas89.model.dataitem.Director;
import com.github.youngerdryas89.model.dataitem.Genre;
import com.github.youngerdryas89.model.dataitem.ID;
import com.github.youngerdryas89.model.dataitem.MPAARating;
import com.github.youngerdryas89.model.dataitem.OriginalTitle;
import com.github.youngerdryas89.model.dataitem.Outline;
import com.github.youngerdryas89.model.dataitem.Plot;
import com.github.youngerdryas89.model.dataitem.Rating;
import com.github.youngerdryas89.model.dataitem.ReleaseDate;
import com.github.youngerdryas89.model.dataitem.Runtime;
import com.github.youngerdryas89.model.dataitem.Set;
import com.github.youngerdryas89.model.dataitem.SortTitle;
import com.github.youngerdryas89.model.dataitem.Studio;
import com.github.youngerdryas89.model.dataitem.Tag;
import com.github.youngerdryas89.model.dataitem.Tagline;
import com.github.youngerdryas89.model.dataitem.Thumb;
import com.github.youngerdryas89.model.dataitem.Title;
import com.github.youngerdryas89.model.dataitem.Top250;
import com.github.youngerdryas89.model.dataitem.Trailer;
import com.github.youngerdryas89.model.dataitem.Votes;
import com.github.youngerdryas89.model.dataitem.Year;

public class MovieFactory {

	/**
	 * @return A movie object with every field initialized to either blank (for things like string values) or having zero values (array/list objects)
	 */
	public static Movie createEmptyMovie() {

		ArrayList<Actor> actors = new ArrayList<>();

		ArrayList<Director> directors = new ArrayList<>();

		Thumb[] fanart = new Thumb[0];
		Thumb[] extraFanart = new Thumb[0];

		ArrayList<Genre> genres = new ArrayList<>();

		ArrayList<Tag> tags = new ArrayList<>();

		ID id = ID.BLANK_ID;
		MPAARating mpaa = MPAARating.BLANK_RATING;
		OriginalTitle originalTitle = OriginalTitle.BLANK_ORIGINALTITLE;
		Outline outline = Outline.BLANK_OUTLINE;
		Plot plot = Plot.BLANK_PLOT;
		Thumb[] posters = new Thumb[0];

		Rating rating = Rating.BLANK_RATING;
		ReleaseDate releaseDate = ReleaseDate.BLANK_RELEASEDATE;
		Runtime runtime = Runtime.BLANK_RUNTIME;
		Set set = Set.BLANK_SET;
		SortTitle sortTitle = SortTitle.BLANK_SORTTITLE;
		Studio studio = Studio.BLANK_STUDIO;
		Tagline tagline = Tagline.BLANK_TAGLINE;
		Title title = new Title("");
		Top250 top250 = Top250.BLANK_TOP250;
		Trailer trailer = Trailer.BLANK_TRAILER;
		Votes votes = Votes.BLANK_VOTES;
		Year year = Year.BLANK_YEAR;

		return new Movie(actors, directors, fanart, extraFanart, genres, tags, id, mpaa, originalTitle, outline, plot, posters, rating, releaseDate, runtime, set, sortTitle, studio, tagline, title,
		        top250, trailer, votes, year);
	}

}
