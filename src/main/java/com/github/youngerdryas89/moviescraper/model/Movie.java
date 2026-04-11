package com.github.youngerdryas89.moviescraper.model;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.*;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import com.github.youngerdryas89.moviescraper.controller.Similarity;
import com.github.youngerdryas89.moviescraper.model.dataitem.Set;
import com.github.youngerdryas89.moviescraper.view.FileDetailPanel;
import com.github.youngerdryas89.moviescraper.view.GUIMain;
import org.apache.commons.io.FileUtils;

import com.github.youngerdryas89.moviescraper.controller.FileDownloaderUtilities;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SecurityPassthrough;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.xmlserialization.KodiXmlMovieBean;
import com.github.youngerdryas89.moviescraper.model.dataitem.*;
import com.github.youngerdryas89.moviescraper.model.dataitem.Runtime;
import com.github.youngerdryas89.moviescraper.model.preferences.MoviescraperPreferences;

public class Movie {

	/*
	 * Be careful if you decide you want to change the field names in this class (especially the arrays)
	 * because reflection is used in the movie amalgamation routine to get these fields by name, so you will need to
	 * update the references in the reflective call with the new name as well.
	 */
	private ArrayList<Actor> actors;
	private ArrayList<Director> directors;
	private Thumb[] fanart;
	private Thumb[] extraFanart;
	private Thumb preferredFanartToWriteToDisk;
	private ArrayList<Genre> genres;
	private ID id;
	private MPAARating mpaa;
	private OriginalTitle originalTitle;
	private Outline outline;
	private Plot plot;
	private Thumb[] posters;
	private Rating rating;
	private ReleaseDate releaseDate;
	private Runtime runtime;
	private Set set;
	private SortTitle sortTitle;
	private Studio studio;

	private Tagline tagline;
	private ArrayList<Tag> tags;

	private Title title;

	private List<Title> allTitles = new ArrayList<>(); //this is currently not used for much; it used to allow the user to select from one of each title in a drop down box on the file detail panel, but now that amalgamation is here, that feature is not needed as much. It may make sense to put in a generic way to handle selecting between data item sources from amalgamation on a per item basis in the file detail panel, however

	private Top250 top250;

	private Trailer trailer;

	private Votes votes;

	private Year year;

	private String fileName;

	public Movie(ArrayList<Actor> actors, ArrayList<Director> directors, Thumb[] fanart, Thumb[] extraFanart, ArrayList<Genre> genres, ArrayList<Tag> tags, ID id, MPAARating mpaa,
	        OriginalTitle originalTitle, Outline outline, Plot plot, Thumb[] posters, Rating rating, ReleaseDate releaseDate, Runtime runtime, Set set, SortTitle sortTitle, Studio studio,
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

	public Movie(SiteParsingProfile siteToScrapeFrom, GUIMain parent) {
		title = siteToScrapeFrom.scrapeTitle();

		originalTitle = siteToScrapeFrom.scrapeOriginalTitle();
        id = siteToScrapeFrom.scrapeID();
		sortTitle = siteToScrapeFrom.scrapeSortTitle();
		set = siteToScrapeFrom.scrapeSet();
		rating = siteToScrapeFrom.scrapeRating();
		year = siteToScrapeFrom.scrapeYear();
		top250 = siteToScrapeFrom.scrapeTop250();
		trailer = siteToScrapeFrom.scrapeTrailer();
		votes = siteToScrapeFrom.scrapeVotes();
		outline = siteToScrapeFrom.scrapeOutline();
		plot = siteToScrapeFrom.scrapePlot();
		tagline = siteToScrapeFrom.scrapeTagline();
		studio = siteToScrapeFrom.scrapeStudio();
		releaseDate = siteToScrapeFrom.scrapeReleaseDate();
		runtime = siteToScrapeFrom.scrapeRuntime();
		posters = siteToScrapeFrom.scrapePosters(parent.getFileDetailPanel().cropPosters());
		if(posters.length > 0 && posters[0].hasDerivations())
			posters[0] = posters[0].derivedChild();

		fanart = siteToScrapeFrom.scrapeFanart();
		extraFanart = siteToScrapeFrom.scrapeExtraFanart();
		mpaa = siteToScrapeFrom.scrapeMPAA();
		actors = siteToScrapeFrom.scrapeActors();
		genres = siteToScrapeFrom.scrapeGenres();
		tags = siteToScrapeFrom.scrapeTags();
		directors = siteToScrapeFrom.scrapeDirectors();

		setAllDataItemSources(siteToScrapeFrom);

		String fileNameOfScrapedMovie = siteToScrapeFrom.getFileNameOfScrapedMovie();
		if (fileNameOfScrapedMovie != null && fileNameOfScrapedMovie.trim().length() > 0) {
			fileName = fileNameOfScrapedMovie;
		}

		MoviescraperPreferences scraperPreferences = MoviescraperPreferences.getInstance();
		if (scraperPreferences.getUseFileNameAsTitle() && fileName != null && fileName.length() > 0) {
			title = new Title(fileName);
			title.setDataItemSource(new DefaultDataItemSource());
		}

		appendIDToStartOfTitle();

	}

	/**
	 * @param siteToScrapeFrom
	 */
	private void setAllDataItemSources(SiteParsingProfile siteToScrapeFrom) {
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

	/**
	 * If the appropriate preference is set, add the ID number to the end of the title field
	 */
	private void appendIDToStartOfTitle() {
		if(id != null && id.getId() != null && id.getId().trim().length() > 0 && hasValidTitle()) {
			if (MoviescraperPreferences.getInstance().getAppendIDToStartOfTitle())
				title.setTitle(id.getId() + " - " + title.getTitle());
			else
				title.setTitle(title.getTitle().replace(id.getId(), ""));
		}
	}

	private void setDataItemSourceOnThumbs(Thumb[] thumbs, DataItemSource dataItemSource) {
		for (Thumb thumb : thumbs) {
			thumb.setDataItemSource(dataItemSource);
		}
	}

	public ArrayList<Actor> getActors() {
		return actors;
	}

	public ArrayList<Director> getDirectors() {
		return directors;
	}

	public Thumb[] getFanart() {
		return fanart;
	}

	public ArrayList<Genre> getGenres() {
		return genres;
	}

	public ArrayList<Tag> getTags() {
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

	public Thumb[] getPosters() {
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

	public void setActors(ArrayList<Actor> actors) {
		this.actors = actors;
	}

	public void setDirectors(ArrayList<Director> directors) {
		this.directors = directors;
	}

	public void setFanart(Thumb[] fanart) {
		this.fanart = fanart;
	}

	public void setGenres(ArrayList<Genre> genres) {
		this.genres = genres;
	}

	public void setTags(ArrayList<Tag> tags) {
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

	public void setPosters(Thumb[] posters) {
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
		        + ", runtime=" + runtime + ", posters=" + Arrays.toString(posters) + ", fanart=" + Arrays.toString(fanart) + ", extrafanart = " + Arrays.toString(extraFanart) + ", mpaa=" + mpaa
		        + ", id=" + id + ", genres=" + genres + ", tags=" + tags + ", actors=" + actors + ", directors=" + directors + "]";
	}

	public String toXML() {
		return title.toXML();
	}

	public void writeExtraFanart(File directoryMovieIsIn) throws IOException {
		if (directoryMovieIsIn != null && directoryMovieIsIn.exists() && directoryMovieIsIn.isDirectory() && getExtraFanart().length > 0) {
			File extraFanartFolder = new File(directoryMovieIsIn.getPath() + File.separator + "extrafanart");
			FileUtils.forceMkdir(extraFanartFolder);
			int currentExtraFanartNumber = 1;
			for (Thumb currentExtraFanart : this.getExtraFanart()) {
				File fileNameToWrite = new File(extraFanartFolder.getPath() + File.separator + "fanart" + currentExtraFanartNumber + ".jpg");

				//no need to overwrite perfectly good extra fanart since this stuff doesn't change. this will also save time when rescraping since extra IO isn't done.
				if (!fileNameToWrite.exists()) {
					System.out.println("Writing extrafanart to " + fileNameToWrite);
					currentExtraFanart.writeImageToFile(fileNameToWrite);
				}
				currentExtraFanartNumber++;
			}
		}
	}

	public void writeToFile(File nfoFile, File posterFile, File fanartFile, File currentlySelectedFolderJpgFile, File targetFolderForExtraFanartFolderAndActorFolder, File trailerFile,
	        MoviescraperPreferences preferences, boolean uncropButtonPressed) throws IOException {
		// Output the movie to XML using XStream and a proxy class to
		// translate things to a format that Kodi expects

		//ID only appended if preference set and not already at the start of the title
		if (!title.getTitle().startsWith(id.getId())) {
			appendIDToStartOfTitle();
		}

		String xml = new KodiXmlMovieBean(this).toXML();
		// add the xml header since xstream doesn't do this
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>" + "\n" + xml;
		//System.out.println("Xml I am writing to file: \n" + xml);

		if (nfoFile != null && xml.length() > 0)
			nfoFile.delete();
		FileUtils.writeStringToFile(nfoFile, xml, org.apache.commons.lang3.CharEncoding.UTF_8);

		Thumb parentPoster = null;
		Thumb posterToSaveToDisk = null;
		if (posters != null && posters.length > 0)
			posterToSaveToDisk = posters[0];

		if(uncropButtonPressed){
			if(posterToSaveToDisk.hasDerivations())
				posterToSaveToDisk = posterToSaveToDisk.derivedChild();

		} else {
			if(posterToSaveToDisk.isModified())
				posterToSaveToDisk = posterToSaveToDisk.getOriginalImage();

		}

		assert posterToSaveToDisk != null;

		boolean writePoster = preferences.getWriteFanartAndPostersPreference();
		boolean writeFanart = preferences.getWriteFanartAndPostersPreference();
		boolean writePosterIfAlreadyExists = preferences.getOverWriteFanartAndPostersPreference();
		boolean writeFanartIfAlreadyExists = preferences.getOverWriteFanartAndPostersPreference();
		boolean createFolderJpgEnabledPreference = preferences.getCreateFolderJpgEnabledPreference();

		// save the first poster out
		// maybe we did some clipping, so we're going to have to reencode it
		if (this.getPosters().length > 0 && (writePoster || createFolderJpgEnabledPreference)
		        && ((posterFile.exists() == writePosterIfAlreadyExists) || (!posterFile.exists() || (createFolderJpgEnabledPreference)))) {
			if (posterToSaveToDisk != null && (posterToSaveToDisk.isModified() || createFolderJpgEnabledPreference || !posterFile.exists() || writePosterIfAlreadyExists)) {
				//reencode the jpg since we probably did a resize
				Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
				ImageWriter writer = (ImageWriter) iter.next();
				// instantiate an ImageWriteParam object with default compression options
				ImageWriteParam iwp = writer.getDefaultWriteParam();
				iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				iwp.setCompressionQuality(1); // an float between 0 and 1
				// 1 specifies minimum compression and maximum quality
				IIOImage image;
				image = new IIOImage((RenderedImage) posterToSaveToDisk.getThumbImage(), null, null);

				if (writePoster && posterToSaveToDisk.isModified()) {
					System.out.println("Writing poster to " + posterFile);
					try (FileImageOutputStream posterFileOutput = new FileImageOutputStream(posterFile);) {
						writer.setOutput(posterFileOutput);
						writer.write(null, image, iwp);
					} catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                }
				//write out the poster file without reencoding it and resizing it
				else if ((!posterFile.exists() || writePosterIfAlreadyExists) && posterToSaveToDisk.getThumbURL() != null) {
                    System.out.println("Writing poster file from nfo: " + posterFile);
                    FileDownloaderUtilities.writeURLToFile(posterToSaveToDisk.getThumbURL(), posterFile, posterToSaveToDisk.getReferrerURL());
                    ImageCache.replaceIfPresent(posterFile.toURI().toURL(), posterToSaveToDisk.getThumbImage());
                }
				if (createFolderJpgEnabledPreference && currentlySelectedFolderJpgFile != null) {
					// if the image is not modified
					if (!posterToSaveToDisk.isModified() && (!currentlySelectedFolderJpgFile.exists() || (currentlySelectedFolderJpgFile.exists() && writePosterIfAlreadyExists))) {
                        try {
                            System.out.println("Writing folder.jpg (no changes) to " + currentlySelectedFolderJpgFile);
                            FileDownloaderUtilities.writeURLToFile(posterToSaveToDisk.getThumbURL(), currentlySelectedFolderJpgFile, posterToSaveToDisk.getReferrerURL());
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                    } else {
						if (!currentlySelectedFolderJpgFile.exists() || (currentlySelectedFolderJpgFile.exists() && writePosterIfAlreadyExists)) {
							System.out.println("Writing folder to " + currentlySelectedFolderJpgFile);
							try (FileImageOutputStream folderFileOutput = new FileImageOutputStream(currentlySelectedFolderJpgFile);) {
								writer.setOutput(folderFileOutput);
								writer.write(null, image, iwp);
							} catch (IOException e) {
                                System.err.println(e.getMessage());
                            }
                        } else {
							System.out.println("Skipping overwrite of folder.jpg due to preference setting");
						}
					}
				}
				writer.dispose();
			}
		}

		if(uncropButtonPressed && posterToSaveToDisk.isModified()){
            ImageCache.replaceIfPresent(posterFile.toURI().toURL(), posterToSaveToDisk.getThumbImage());
            ImageCache.removeImageFromCache(posterToSaveToDisk.getThumbURL(), false);
		}

		// save the first fanart out
		// we didn't modify it so we can write it directly from the URL
		if (this.getFanart().length > 0 && writeFanart && ((fanartFile.exists() == writeFanartIfAlreadyExists) || !fanartFile.exists())) {
			if (fanart != null && fanart.length > 0) {
				Thumb fanartToSaveToDisk;
				if (preferredFanartToWriteToDisk != null)
					fanartToSaveToDisk = preferredFanartToWriteToDisk;
				else
					fanartToSaveToDisk = fanart[0];
				System.out.println("saving out first fanart to " + fanartFile);

				//can save ourself redownloading the image if it's already in memory, but we dont want to reencode the image, so only do this if it's modified
				if (fanartToSaveToDisk.getImageIconThumbImage() != null && fanartToSaveToDisk.isModified()) {
					try {
						ImageIO.write(fanartToSaveToDisk.toBufferedImage(), "jpg", fanartFile);
                        ImageCache.replaceIfPresent(fanartFile.toURI().toURL(), fanartToSaveToDisk.getThumbImage());
					} catch (IOException e) {
						System.err.println("Failed to write fanart due to io error");
						e.printStackTrace();
					}
                }
				//download the url and save it out to disk
				else {
                    FileDownloaderUtilities.writeURLToFile(fanartToSaveToDisk.getThumbURL(), fanartFile, fanartToSaveToDisk.getReferrerURL());
                    ImageCache.replaceIfPresent(fanartFile.toURI().toURL(), fanartToSaveToDisk.getThumbImage());
                }
			}
		}

		//write out the extrafanart, if the preference for it is set
		if (targetFolderForExtraFanartFolderAndActorFolder != null && preferences.getExtraFanartScrapingEnabledPreference()) {
			System.out.println("Starting write of extra fanart into " + targetFolderForExtraFanartFolderAndActorFolder);
			writeExtraFanart(targetFolderForExtraFanartFolderAndActorFolder);
		}

		//write the .actor images, if the preference for it is set
		if (preferences.getDownloadActorImagesToActorFolderPreference() && targetFolderForExtraFanartFolderAndActorFolder != null) {
			System.out.println("Writing .actor images into " + targetFolderForExtraFanartFolderAndActorFolder);
			writeActorImagesToFolder(targetFolderForExtraFanartFolderAndActorFolder);
		}

		//write out the trailer, if the preference for it is set
		Trailer trailerToWrite = getTrailer();
		if (preferences.getWriteTrailerToFile() && trailerToWrite != null && trailerToWrite.getTrailer().length() > 0) {
			trailerToWrite.writeTrailerToFile(trailerFile);
		}
	}

	public void writeActorImagesToFolder(File targetFolder) throws IOException {
		File actorFolder = null;
		if (targetFolder.isDirectory()) {
			actorFolder = new File(targetFolder + File.separator + ".actors");
		} else if (targetFolder.isFile()) {
			actorFolder = new File(targetFolder.getParent() + File.separator + ".actors");
		}
		//Don't create an empty .actors folder with no actors underneath it
		if (this.hasAtLeastOneActorThumbnail() && actorFolder != null) {
			FileUtils.forceMkdir(actorFolder);
			//on windows this new folder should have the hidden attribute; on unix it is already "hidden" by having a . in front of the name
			Path path = actorFolder.toPath();
			//if statement needed for Linux checking .actors hidden flag when .actors is a symlink
			if (!Files.isHidden(path)) {
				Boolean hidden = (Boolean) Files.getAttribute(path, "dos:hidden", LinkOption.NOFOLLOW_LINKS);
				if (hidden != null && !hidden) {
					try {
						Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
					} catch (AccessDeniedException e) {
						System.err.println("I was not allowed to make .actors folder hidden. This is not a big deal - continuing with write of actor files...");
					}
				}
			}

			for (Actor currentActor : this.getActors()) {
				String currentActorToFileName = currentActor.getName().replace(' ', '_');
				File fileNameToWrite = new File(actorFolder.getPath() + File.separator + currentActorToFileName + ".jpg");
				currentActor.writeImageToFile(fileNameToWrite);
				//reload from disk instead of cache since the cache is now pointing to the wrong image and the disk has the correct newly edited one
				if (currentActor.isThumbEdited())
					ImageCache.removeImageFromCache(fileNameToWrite.toURI().toURL(), false);
			}

		}
	}

	public boolean hasPoster() {
        return this.posters.length > 0;
	}

	/*
	 * private String [] searchResultsHelperForScrapeMovie(File movieFile, SiteParsingProfile siteToParseFrom)
	 * {
	 * String [] searchResults = siteToParseFrom.getSearchResults(searchString);
	 * int levDistanceOfCurrentMatch = 999999; // just some super high number
	 * String idFromMovieFile = SiteParsingProfile.findIDTagFromFile(movieFile);
	 * 
	 * //loop through search results and see if URL happens to contain ID number in the URL. This will improve accuracy!
	 * for (int i = 0; i < searchResults.length; i++)
	 * {
	 * String urltoMatch = searchResults[i].toLowerCase();
	 * String idFromMovieFileToMatch = idFromMovieFile.toLowerCase().replaceAll("-", "");
	 * //System.out.println("Comparing " + searchResults[i].toLowerCase() + " to " + idFromMovieFile.toLowerCase().replaceAll("-", ""));
	 * if (urltoMatch.contains(idFromMovieFileToMatch))
	 * {
	 * //let's do some fuzzy logic searching to try to get the "best" match in case we got some that are pretty close
	 * //and update the variables accordingly so we know what our best match so far is
	 * int candidateLevDistanceOfCurrentMatch = StringUtils.getLevenshteinDistance(urltoMatch.toLowerCase(), idFromMovieFileToMatch);
	 * if (candidateLevDistanceOfCurrentMatch < levDistanceOfCurrentMatch)
	 * {
	 * levDistanceOfCurrentMatch = candidateLevDistanceOfCurrentMatch;
	 * searchResultNumberToUse = i;
	 * }
	 * }
	 * }
	 * return searchResults;
	 * }
	 */

	public boolean hasAtLeastOneActorThumbnail() {
		for (Actor currentActor : actors) {
			if (currentActor.getThumb() != null && currentActor.getThumb().getThumbURL() != null && !currentActor.getThumb().getThumbURL().equals("")) {
				return true;
			}
		}
		return false;
	}

	public Thumb[] getExtraFanart() {
		return extraFanart;
	}

	public void setExtraFanart(Thumb[] extraFanart) {
		this.extraFanart = extraFanart;
	}

	public Trailer getTrailer() {
		return trailer;
	}

	public void setTrailer(Trailer trailer) {
		this.trailer = trailer;
	}

	public boolean hasFanart() {
		if (this.fanart.length > 0)
			return true;
		else
			return false;
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

			ArrayList<Thumb> existingPosters = new ArrayList<>(Arrays.asList(getPosters()));
			boolean didListContainPoster = existingPosters.remove(posterToGoToFront);
			if (didListContainPoster) {
				existingPosters.add(0, posterToGoToFront);
				Thumb[] posterArray = new Thumb[existingPosters.size()];
				setPosters(existingPosters.toArray(posterArray));
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

			ArrayList<Thumb> existingFanarts = new ArrayList<>(Arrays.asList(getFanart()));
			boolean didListContainPoster = existingFanarts.remove(fanartToGoToFront);
			if (didListContainPoster) {
				existingFanarts.add(0, fanartToGoToFront);
				Thumb[] fanartArray = new Thumb[existingFanarts.size()];
				setFanart(existingFanarts.toArray(fanartArray));
			}
		}
	}

	/**
	 * @return true if the movie has a non-null, non-zero length title, false otherwise
	 */
	public boolean hasValidTitle() {
		return (title != null && title.getTitle() != null && title.getTitle().length() > 0);
	}

}
