package com.github.youngerdryas89.moviescraper.controller.xmlserialization;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.net.MalformedURLException;
import java.util.List;

import com.github.youngerdryas89.moviescraper.model.movie.Movie;
import com.github.youngerdryas89.moviescraper.model.dataitem.*;
import com.github.youngerdryas89.moviescraper.model.dataitem.Runtime;
import com.github.youngerdryas89.moviescraper.model.preferences.MoviescraperPreferences;

/**
 * Class which handles serializing a Movie object to and from XML
 */
public class KodiXmlMovieBean {

	private String title;
	private String originaltitle;
	private String sorttitle;
	private String set;
	private String year;
	private String top250;
	private String trailer;
	private String votes;
	private String rating;
	private String outline;
	private String plot;
	private String tagline;
	private String runtime;
	private String releasedate;
	private String studio;
	private List<String> thumb = new ArrayList<>();
	private KodiXmlFanartBean fanart;
	private String mpaa;
	private String id;
	private List<String> genre = new ArrayList<>();
	private List<String> tag = new ArrayList<>();
	// private String[] credits; add in later
	private List<KodiXmlActorBean> actor;
	private List<String> director;

	public static KodiXmlMovieBean makeFromXML(String xml) {
		XStream xstream = KodiXmlMovieBean.getXMLSerializer();
		xstream.ignoreUnknownElements();
		try {
			KodiXmlMovieBean beanToReturn = (KodiXmlMovieBean) xstream.fromXML(xml);
			return beanToReturn;
		} catch (Exception e) {
			System.err.println("File read from nfo is not in Kodi XML format. This movie will not be read in.");
			return null;
		}

	}

	/**
	 * Constructor - handles conversion of a Movie object to a KodiXmlMovieBean object.
	 * Program preferences are taken into account when performing the object conversion so that, for example,
	 * certain fields will not be written to the XML
	 * 
	 * @param movie - Movie to create the KodiXmlMovieBean from
	 */
	public KodiXmlMovieBean(MoviescraperPreferences preferences, Movie movie) {
		title = movie.getTitle().getTitle();
		originaltitle = movie.getOriginalTitle().getOriginalTitle();
		sorttitle = movie.getSortTitle().getSortTitle();
		set = movie.getSet().getSet();
		year = movie.getYear().getYear();
		top250 = movie.getTop250().getTop250();
		trailer = movie.getTrailer().getTrailer();
		votes = movie.getVotes().getVotes();
		rating = movie.getRating().getRatingOutOfTen();
		outline = movie.getOutline().getOutline();
		plot = movie.getPlot().getPlot();
		tagline = movie.getTagline().getTagline();
		runtime = movie.getRuntime().getRuntime();
		releasedate = movie.getReleaseDate().getReleaseDate();
		studio = movie.getStudio().getStudio();

		// thumb - aka Posters
		// Preference value allows the user to not write out poster values to the file
		if (preferences.writeThumbTagsForPosterAndFanartToNfo())
			thumb = movie.getPosters().stream().map(Thumb::getThumbURL).map(URL::toString).toList();

		//fanart
		// Preference value allows the user to not write out fanart values to the file
		if (preferences.writeThumbTagsForPosterAndFanartToNfo()) {
			fanart = new KodiXmlFanartBean(movie.getFanart());
		}

		mpaa = movie.getMpaa().getMPAARating();
		id = movie.getId().getId();
		// genre
		genre = movie.getGenres().stream().map(Genre::getGenre).toList();

		// tag
		tag = movie.getTags().stream().map(Tag::getTag).toList();

		// director
		director = movie.getDirectors().stream().map(Person::getName).toList();

		// actor
		actor = new ArrayList<>(movie.getActors().size());
		for (Actor currentActor : movie.getActors()) {
			if (currentActor.getThumb() != null && currentActor.getThumb().getThumbURL() != null) {
				actor.add(new KodiXmlActorBean(currentActor.getName(), currentActor.getRole(), currentActor.getThumb().getThumbURL().toString()));
			} else {
				actor.add(new KodiXmlActorBean(currentActor.getName(), currentActor.getRole(), ""));
			}
		}
	}

	public Movie toMovie() throws IOException {

		ArrayList<Actor> actors = new ArrayList<>();
		if (actor != null) {
			actors = new ArrayList<>(actor.size());
			for (KodiXmlActorBean currentActor : actor) {
				actors.add(currentActor.toActor());
			}
		}
		try {

			List<Thumb> posterThumbs = thumb.stream().map(Thumb::new).toList();
			List<Thumb> fanartThumbs = fanart.getThumb().stream().map(Thumb::new).toList();
			List<Genre> genres = genre.stream().map(Genre::new).toList();
			List<Tag> tags = tag.stream().map(Tag::new).toList();
			List<Director> directors = director.stream().map(Director::new).toList();


			List<Thumb> emptyExtraFanrt = new ArrayList<>();
			Movie movie = new Movie(actors, directors, fanartThumbs, emptyExtraFanrt, genres, tags, new ID(id), new MPAARating(mpaa), new OriginalTitle(originaltitle), new Outline(outline),
					new Plot(plot), posterThumbs, new Rating(10, rating), new ReleaseDate(releasedate), new Runtime(runtime), new Set(set), new SortTitle(sorttitle), new Studio(studio),
					new Tagline(tagline), new Title(title), new Top250(top250), new Trailer(trailer), new Votes(votes), new Year(year));
			return movie;
		}catch (MalformedURLException e){
			// TODO: Need come back to this. It can't be the case that a URL could potentially be malformed past its creation.
			throw e;
		}
	}

	public String toXML() {
		String xml = getXMLSerializer().toXML(this);
		return xml;
	}

	private static XStream getXMLSerializer() {
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		xstream.allowTypesByWildcard(new String[] {
				"com.github.youngerdryas89.moviescraper.controller.xmlserialization.*"
		});
		xstream.omitField(Thumb.class, "thumbImage");
		xstream.alias("movie", KodiXmlMovieBean.class);
		xstream.alias("thumb", Thumb.class);
		xstream.alias("actor", Actor.class);
		xstream.alias("actor", KodiXmlActorBean.class);
		xstream.alias("fanart", KodiXmlFanartBean.class);
		xstream.addImplicitCollection(KodiXmlMovieBean.class, "actor");
		xstream.addImplicitArray(KodiXmlMovieBean.class, "thumb", "thumb");
		xstream.addImplicitArray(KodiXmlMovieBean.class, "director", "director");
		xstream.addImplicitArray(KodiXmlFanartBean.class, "thumb", "thumb");
		xstream.addImplicitArray(KodiXmlMovieBean.class, "genre", "genre");
		xstream.addImplicitArray(KodiXmlMovieBean.class, "tag", "tag");
		return xstream;
	}

}
