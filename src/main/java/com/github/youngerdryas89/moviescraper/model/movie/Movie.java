package com.github.youngerdryas89.moviescraper.model.movie;

import java.util.*;

import com.github.youngerdryas89.moviescraper.model.dataitem.Set;

import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfile;
import com.github.youngerdryas89.moviescraper.model.dataitem.*;
import com.github.youngerdryas89.moviescraper.model.dataitem.Runtime;

public class Movie {

	/*
	 * Be careful if you decide you want to change the field names in this class (especially the arrays)
	 * because reflection is used in the movie amalgamation routine to get these fields by name, so you will need to
	 * update the references in the reflective call with the new name as well.
	 */
	private List<Actor> actors = new ArrayList<>();
	private List<Director> directors = new ArrayList<>();
	private List<Thumb> fanart = new ArrayList<>();
	private List<Thumb> extraFanart = new ArrayList<>();
	private Thumb preferredFanartToWriteToDisk;
	private List<Genre> genres = new ArrayList<>();
	private ID id = ID.BLANK_ID;
	private MPAARating mpaa = MPAARating.BLANK_RATING;
	private OriginalTitle originalTitle = OriginalTitle.BLANK_ORIGINALTITLE;
	private Outline outline = Outline.BLANK_OUTLINE;
	private Plot plot = Plot.BLANK_PLOT;
	private List<Thumb> posters = new ArrayList<>();
	private Rating rating = Rating.BLANK_RATING;
	private ReleaseDate releaseDate = ReleaseDate.BLANK_RELEASEDATE;
	private Runtime runtime = Runtime.BLANK_RUNTIME;
	private Set set = Set.BLANK_SET;
	private SortTitle sortTitle = SortTitle.BLANK_SORTTITLE;
	private Studio studio = Studio.BLANK_STUDIO;

	private Tagline tagline = Tagline.BLANK_TAGLINE;
	private List<Tag> tags = new ArrayList<>();

	private Title title = Title.BLANK_TITLE;

	private List<Title> allTitles = new ArrayList<>(); //this is currently not used for much; it used to allow the user to select from one of each title in a drop down box on the file detail panel, but now that amalgamation is here, that feature is not needed as much. It may make sense to put in a generic way to handle selecting between data item sources from amalgamation on a per item basis in the file detail panel, however

	private Top250 top250 = Top250.BLANK_TOP250;

	private Trailer trailer = Trailer.BLANK_TRAILER;

	private Votes votes = Votes.BLANK_VOTES;

	private Year year = Year.BLANK_YEAR;

	private String fileName = "";

	public Movie(List<Actor> actors, List<Director> directors, List<Thumb> fanart, List<Thumb> extraFanart, List<Genre> genres, List<Tag> tags, ID id, MPAARating mpaa,
	        OriginalTitle originalTitle, Outline outline, Plot plot, List<Thumb> posters, Rating rating, ReleaseDate releaseDate, Runtime runtime, Set set, SortTitle sortTitle, Studio studio,
	        Tagline tagline, Title title, Top250 top250, Trailer trailer, Votes votes, Year year) {
		super();
		this.actors = actors;
		this.directors = directors;
		this.fanart = fanart;
		this.extraFanart = extraFanart;
		this.genres = genres;
		this.tags = tags;
		this.id = id;
		this.mpaa = mpaa;
		this.originalTitle = originalTitle;
		this.outline = outline;
		this.plot = plot;
		this.posters = posters;
		this.rating = rating;
		this.releaseDate = releaseDate;
		this.runtime = runtime;
		this.set = set;
		this.sortTitle = sortTitle;
		this.studio = studio;
		this.tagline = tagline;
		this.title = title;
		this.top250 = top250;
		this.trailer = trailer;
		this.votes = votes;
		this.year = year;
	}

	public Movie() {}

	/**
	 * @param siteToScrapeFrom
	 */
	public void setAllDataItemSources(SiteParsingProfile siteToScrapeFrom) {
		originalTitle.setDataItemSource(siteToScrapeFrom);
		title.setDataItemSource(siteToScrapeFrom);
		sortTitle.setDataItemSource(siteToScrapeFrom);
		set.setDataItemSource(siteToScrapeFrom);
		rating.setDataItemSource(siteToScrapeFrom);
		year.setDataItemSource(siteToScrapeFrom);
		top250.setDataItemSource(siteToScrapeFrom);
		trailer.setDataItemSource(siteToScrapeFrom);
		votes.setDataItemSource(siteToScrapeFrom);
		outline.setDataItemSource(siteToScrapeFrom);
		plot.setDataItemSource(siteToScrapeFrom);
		tagline.setDataItemSource(siteToScrapeFrom);
		studio.setDataItemSource(siteToScrapeFrom);
		releaseDate.setDataItemSource(siteToScrapeFrom);
		runtime.setDataItemSource(siteToScrapeFrom);
		setDataItemSourceOnThumbs(posters, siteToScrapeFrom);
		setDataItemSourceOnThumbs(fanart, siteToScrapeFrom);
		setDataItemSourceOnThumbs(extraFanart, siteToScrapeFrom);
		mpaa.setDataItemSource(siteToScrapeFrom);
		id.setDataItemSource(siteToScrapeFrom);

		for (Actor currentActor : actors)
			currentActor.setDataItemSource(siteToScrapeFrom);

		for (Genre currentGenre : genres)
			currentGenre.setDataItemSource(siteToScrapeFrom);

		for (Tag currentTag : tags) {
			currentTag.setDataItemSource(siteToScrapeFrom);
		}

		for (Director currentDirector : directors)
			currentDirector.setDataItemSource(siteToScrapeFrom);
	}

	private void setDataItemSourceOnThumbs(List<Thumb> thumbs, DataItemSource dataItemSource) {
		for (Thumb thumb : thumbs) {
			thumb.setDataItemSource(dataItemSource);
		}
	}

	/**
	 * If the appropriate preference is set, add the ID number to the end of the title field
	 */

	public List<Actor> getActors() {
		return actors;
	}

	public List<Director> getDirectors() {
		return directors;
	}

	public List<Thumb> getFanart() {
		return fanart;
	}

	public List<Genre> getGenres() {
		return genres;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public ID getId() {
		return id;
	}

	public MPAARating getMpaa() {
		return mpaa;
	}

	public OriginalTitle getOriginalTitle() {
		return originalTitle;
	}

	public Outline getOutline() {
		return outline;
	}

	public Plot getPlot() {
		return plot;
	}

	public List<Thumb> getPosters() {
		return posters;
	}

	public Rating getRating() {
		return rating;
	}

	public Runtime getRuntime() {
		return runtime;
	}

	public Set getSet() {
		return set;
	}

	public SortTitle getSortTitle() {
		return sortTitle;
	}

	public Studio getStudio() {
		return studio;
	}

	public Tagline getTagline() {
		return tagline;
	}

	public Title getTitle() {
		return title;
	}

	public Top250 getTop250() {
		return top250;
	}

	public Votes getVotes() {
		return votes;
	}

	public Year getYear() {
		return year;
	}

	public void setActors(List<Actor> actors) {
		this.actors = actors;
	}

	public void setDirectors(List<Director> directors) {
		this.directors = directors;
	}

	public void setFanart(List<Thumb> fanart) {
		this.fanart = fanart;
	}

	public void setGenres(List<Genre> genres) {
		this.genres = genres;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public void setId(ID id) {
		this.id = id;
	}

	public void setMpaa(MPAARating mpaa) {
		this.mpaa = mpaa;
	}

	public void setOriginalTitle(OriginalTitle originalTitle) {
		this.originalTitle = originalTitle;
	}

	public void setOutline(Outline outline) {
		this.outline = outline;
	}

	public void setPlot(Plot plot) {
		this.plot = plot;
	}

	public void setPosters(List<Thumb> posters) {
		this.posters = posters;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public void setRuntime(Runtime runtime) {
		this.runtime = runtime;
	}

	public void setSet(Set set) {
		this.set = set;
	}

	public void setSortTitle(SortTitle sortTitle) {
		this.sortTitle = sortTitle;
	}

	public void setStudio(Studio studio) {
		this.studio = studio;
	}

	public void setTagline(Tagline tagline) {
		this.tagline = tagline;
	}

	public void setTitle(Title title) {
		this.title = title;
	}

	public void setTop250(Top250 top250) {
		this.top250 = top250;
	}

	public void setVotes(Votes votes) {
		this.votes = votes;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return "Movie [title=" + title + ", originalTitle=" + originalTitle + ", sortTitle=" + sortTitle + ", set=" + set + ", rating=" + rating + ", year=" + year + ", top250=" + top250
		        + ", trailer = " + trailer + ", votes=" + votes + ", outline=" + outline + ", plot=" + plot + ", tagline=" + tagline + ", studio=" + studio + "releaseDate=" + releaseDate
		        + ", runtime=" + runtime + ", posters=" + posters.toString() + ", fanart=" + fanart.toString() + ", extrafanart = " + extraFanart.toString() + ", mpaa=" + mpaa
		        + ", id=" + id + ", genres=" + genres + ", tags=" + tags + ", actors=" + actors + ", directors=" + directors + "]";
	}

	public String toXML() {
		return title.toXML();
	}

	public boolean hasPoster() {
		return !posters.isEmpty();
	}

	public boolean hasAtLeastOneActorThumbnail() {
		for (Actor currentActor : actors) {
			if (currentActor.getThumb() != null && currentActor.getThumb().getThumbURL() != null && !currentActor.getThumb().getThumbURL().equals("")) {
				return true;
			}
		}
		return false;
	}

	public List<Thumb> getExtraFanart() {
		return extraFanart;
	}

	public void setExtraFanart(List<Thumb> extraFanart) {
		this.extraFanart = extraFanart;
	}

	public Trailer getTrailer() {
		return trailer;
	}

	public void setTrailer(Trailer trailer) {
		this.trailer = trailer;
	}

	public boolean hasFanart() {
		return !fanart.isEmpty();
	}

	public List<Title> getAllTitles() {
		return allTitles;
	}

	public void setAllTitles(List<Title> allTitles) {
		this.allTitles = allTitles;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ReleaseDate getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(ReleaseDate releaseDate) {
		this.releaseDate = releaseDate;
	}

	/**
	 * remove the item from the picked from the existing poster list and put it at
	 * the front of the list. if the movie does not contain the poster, no change will be made
	 * 
	 * @param posterToGoToFront - poster to put in front
	 */
	public void moveExistingPosterToFront(Thumb posterToGoToFront) {
		if (posterToGoToFront != null) {

			List<Thumb> existingPosters = new ArrayList<>(getPosters());
			if (posters.remove(posterToGoToFront)) {
				existingPosters.add(0, posterToGoToFront);
				List<Thumb> posterArray = new ArrayList<>(existingPosters.size());
				setPosters(posterArray);
			}
		}
	}

	/**
	 * remove the item from the picked from the existing fanart list and put it at
	 * the front of the list. if the movie does not contain the fanart, no change will be made
	 * 
	 * @param fanartToGoToFront - fanart to put in front
	 */
	public void moveExistingFanartToFront(Thumb fanartToGoToFront) {
		if (fanartToGoToFront != null) {

			ArrayList<Thumb> Fanarts = new ArrayList<>(fanart);
			if (Fanarts.remove(fanartToGoToFront)) {
				Fanarts.addFirst(fanartToGoToFront);
				fanart = Fanarts;
			}
		}
	}


	public boolean hasExtraFanart(){
		return !extraFanart.isEmpty();
	}


    public Thumb getPreferredFanartToWriteToDisk() {
        return preferredFanartToWriteToDisk;
    }

    public void setPreferredFanartToWriteToDisk(Thumb preferredFanartToWriteToDisk) {
        this.preferredFanartToWriteToDisk = preferredFanartToWriteToDisk;
    }
}
