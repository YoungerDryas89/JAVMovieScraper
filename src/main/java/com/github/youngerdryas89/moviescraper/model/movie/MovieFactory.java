package com.github.youngerdryas89.moviescraper.model.movie;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.github.youngerdryas89.moviescraper.controller.FileDownloaderUtilities;
import com.github.youngerdryas89.moviescraper.controller.Similarity;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SecurityPassthrough;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.*;
import com.github.youngerdryas89.moviescraper.controller.xmlserialization.KodiXmlMovieBean;
import com.github.youngerdryas89.moviescraper.model.ImageCache;
import com.github.youngerdryas89.moviescraper.model.RatedResult;
import com.github.youngerdryas89.moviescraper.model.SearchResult;
import com.github.youngerdryas89.moviescraper.model.dataitem.*;
import com.github.youngerdryas89.moviescraper.model.preferences.MoviescraperPreferences;
import com.github.youngerdryas89.moviescraper.view.FileDetailPanel;
import com.github.youngerdryas89.moviescraper.view.GUIMain;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

public class MovieFactory {

	/**
	 * Create a movie by reading in a values from a nfo file created by previously scraping the movie and then writing the metadata out to the file
	 *
	 * @param nfoFile
	 * @throws IOException
	 */
	public static Movie createMovieFromNfo(File nfoFile) throws IOException {
		Movie movieFromNfo = null;
		try (FileInputStream fisTargetFile = new FileInputStream(nfoFile);) {

			String targetFileStr = IOUtils.toString(fisTargetFile, StandardCharsets.UTF_8);
			//Sometimes there's some junk before the prolog tag. Do a workaround to remove that junk.
			//This really isn't the cleanest way to do this, but it'll work for now
			//check first to make sure the string even contains <?xml so we don't loop through an invalid file needlessly
			if (targetFileStr.contains("<?xml")) {
				while (!targetFileStr.isEmpty() && !targetFileStr.startsWith("<?xml")) {
					if (targetFileStr.length() > 1) {
						targetFileStr = targetFileStr.substring(1, targetFileStr.length());
					} else
						break;
				}
			}
			KodiXmlMovieBean xmlMovieBean = KodiXmlMovieBean.makeFromXML(targetFileStr);
			if (xmlMovieBean != null) {

				movieFromNfo = xmlMovieBean.toMovie();
			}
			return movieFromNfo;
		}
	}

	//Version that allows us to update the GUI while scraping
	public static Movie scrapeMovie(File movieFile, SiteParsingProfile siteToParseFrom, String urlToScrapeFromDMM, boolean useURLtoScrapeFrom, @NotNull GUIMain parent) throws IOException {

		//If the user manually canceled the results on this scraper in a dialog box, just return a null movie
		if (siteToParseFrom.getDiscardResults())
			return null;

		String searchString;
		FileDetailPanel panel = parent.getFileDetailPanel();

		if(panel.shouldOverrideInferredId() && !panel.inferredId().equals("N/A"))
			searchString = siteToParseFrom.createSearchStringFromId(panel.inferredId());
		else
			searchString = siteToParseFrom.createSearchString(movieFile);

		SearchResult[] searchResults = null;
		int searchResultNumberToUse = 0;

		//no URL was passed in so we gotta figure it ourselves
		if (!useURLtoScrapeFrom) {
			searchResults = siteToParseFrom.getSearchResults(searchString);
			int levDistanceOfCurrentMatch = 999999; // just some super high number
			String idFromMovieFile;
			if(panel.shouldOverrideInferredId() && (!panel.inferredId().isEmpty() || !panel.inferredId().equals("N/A")))
				idFromMovieFile = panel.inferredId();
			else
				idFromMovieFile = SiteParsingProfile.findIDTagFromFile(movieFile, siteToParseFrom.isFirstWordOfFileIsID());


			if(!panel.shouldOverrideInferredId())
				panel.setInferredId(idFromMovieFile);

			if(searchResults.length == 0){
				// TODO: Need something better and more user friendly than just simply printing this out to the console.
				System.err.println("No title could be found with the provided Id.");
			}
			//loop through search results and see if URL happens to contain ID number in the URL. This will improve accuracy!
            if(siteToParseFrom.getScraperGroupNames().contains(SiteParsingProfile.ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP)) {
                for (int i = 0; i < searchResults.length; i++) {
                    String urltoMatch = searchResults[i].getUrlPath().toLowerCase();
                    String idFromMovieFileToMatch = idFromMovieFile.toLowerCase().replaceAll("-", "");
                    //System.out.println("Comparing " + searchResults[i].toLowerCase() + " to " + idFromMovieFile.toLowerCase().replaceAll("-", ""));
                    if (urltoMatch.contains(idFromMovieFileToMatch) || (searchResults.length < 2)) {
                        //let's do some fuzzy logic searching to try to get the "best" match in case we got some that are pretty close
                        //and update the variables accordingly so we know what our best match so far is
                        int candidateLevDistanceOfCurrentMatch = StringUtils.getLevenshteinDistance(urltoMatch.toLowerCase(), idFromMovieFileToMatch);
                        if ((candidateLevDistanceOfCurrentMatch < levDistanceOfCurrentMatch)) {
                            levDistanceOfCurrentMatch = candidateLevDistanceOfCurrentMatch;
                            searchResultNumberToUse = i;
                        }
                    }
                }
            }else {
                String title = siteToParseFrom.cleanseFilenameForBaseName(movieFile).toLowerCase();
                var rated = Stream.of(searchResults)
                        .map(result -> {
                            var levNormalized = Similarity.calculateNormalizedLevenshteinDistance(title, result.getLabel());
                            var jaccard = Similarity.calculateJaccardIndex(title, result.getLabel());
                            var swa = (0.5 * jaccard) + ((1 - 0.5) * levNormalized);
                            return new RatedResult(result, swa);
                        })
                        .sorted(Comparator.comparingDouble(RatedResult::probability))
                        .toList();
                searchResultNumberToUse = List.of(searchResults).indexOf(rated.getFirst().result());
            }
		}
		//just use the URL to parse from the parameter
		else {
			searchResults = new SearchResult[1];

			if (siteToParseFrom instanceof DmmParsingProfile)
				searchResults[0] = new SearchResult(urlToScrapeFromDMM);
			else if (siteToParseFrom instanceof Data18MovieParsingProfile || siteToParseFrom instanceof Data18WebContentParsingProfile)
				searchResults[0] = new SearchResult(urlToScrapeFromDMM);
			else if (siteToParseFrom instanceof JavLibraryParsingProfile)
				searchResults[0] = new SearchResult(((JavLibraryParsingProfile) siteToParseFrom).getOverrideURLJavLibrary());
			else if (siteToParseFrom instanceof IAFDParsingProfile)
				searchResults[0] = new SearchResult(urlToScrapeFromDMM);

			//override any of the above if we have specifically set an override url
			if (siteToParseFrom.getOverridenSearchResult() != null) {
				searchResults[0] = siteToParseFrom.getOverridenSearchResult();
				searchResultNumberToUse = 0;
			}

		}
		if (searchResults.length > 0 && !searchResults[searchResultNumberToUse].getUrlPath().isEmpty()) {
			System.out.println("Scraping this webpage for movie: " + searchResults[searchResultNumberToUse].getUrlPath());
			//for now just set the movie to the first thing found unless we found a link which had something close to the ID
			SearchResult searchResultToUse = searchResults[searchResultNumberToUse];
			var response = siteToParseFrom.downloadDocument(searchResultToUse);
			if(response == null || response.statusCode() != 200 || response.statusCode() > 399){
				if(response != null) {
					System.err.println("Failed to connect to: " + searchResultToUse.getUrlPath());
					System.err.println(response.statusCode() + " " + response.statusMessage());
					throw new RuntimeException("Failed to connect to: " + searchResultToUse.getUrlPath() + "\n" + response.statusCode() + " " + response.statusMessage());
				} else {
					System.err.println("Unable to connect to: " + searchResultToUse.getUrlPath() + ", perhaps internet access is cut?");
					throw new RuntimeException("Unable to connect to: " + searchResultToUse.getUrlPath() + ", perhaps internet access is cut?");
				}
			}

			Document searchMatch = response.parse();

			//Handle any captchas etc that prevent us from getting our result
			if (SecurityPassthrough.class.isAssignableFrom(siteToParseFrom.getClass())) {
				SecurityPassthrough siteParsingProfileSecurityPassthrough = (SecurityPassthrough) siteToParseFrom;
				if (siteParsingProfileSecurityPassthrough.requiresSecurityPassthrough(searchMatch)) {
					searchMatch = siteParsingProfileSecurityPassthrough.runSecurityPassthrough(searchMatch, searchResultToUse);
				}
			}
			siteToParseFrom.setDocument(searchMatch);
            siteToParseFrom.prepareData();
			siteToParseFrom.setOverrideURLDMM(urlToScrapeFromDMM);

			// TODO: Needs to move on to MoveScraper
            return new Movie(siteToParseFrom, parent);
		} else //no movie match found
		{
			return null;
		}
	}

	public void writeExtraFanart(Movie movie, File directoryMovieIsIn) throws IOException {
		if (directoryMovieIsIn != null && directoryMovieIsIn.exists() && directoryMovieIsIn.isDirectory() && !movie.getExtraFanart().isEmpty()) {
			File extraFanartFolder = new File(directoryMovieIsIn.getPath() + File.separator + "extrafanart");
			FileUtils.forceMkdir(extraFanartFolder);
			int currentExtraFanartNumber = 1;
			for (Thumb currentExtraFanart : movie.getExtraFanart()) {
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

	public static void writeToFile(Movie movie, File nfoFile, File posterFile, File fanartFile, File currentlySelectedFolderJpgFile, File targetFolderForExtraFanartFolderAndActorFolder, File trailerFile,
							MoviescraperPreferences preferences, boolean uncropButtonPressed) throws IOException {
		// Output the movie to XML using XStream and a proxy class to
		// translate things to a format that Kodi expects

		String xml = new KodiXmlMovieBean(preferences, movie).toXML();
		// add the xml header since xstream doesn't do this
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>" + "\n" + xml;
		//System.out.println("Xml I am writing to file: \n" + xml);

		if (nfoFile != null && xml.length() > 0)
			nfoFile.delete();
		FileUtils.writeStringToFile(nfoFile, xml, org.apache.commons.lang3.CharEncoding.UTF_8);

		Thumb parentPoster = null;
		Thumb posterToSaveToDisk = null;
		if (movie.hasPoster())
			posterToSaveToDisk = movie.getPosters().getFirst();

		if(uncropButtonPressed){
			if(posterToSaveToDisk.hasDerivations())
				posterToSaveToDisk = posterToSaveToDisk.derivedChild();

		} else {
			if(posterToSaveToDisk.isModified())
				posterToSaveToDisk = posterToSaveToDisk.getOriginalImage();

		}

		assert posterToSaveToDisk != null;

		boolean writePoster = preferences.writeFanartAndPosters();
		boolean writeFanart = preferences.writeFanartAndPosters();
		boolean writePosterIfAlreadyExists = preferences.overwriteFanartAndPosters();
		boolean writeFanartIfAlreadyExists = preferences.overwriteFanartAndPosters();
		boolean createFolderJpgEnabledPreference = preferences.createFolderJpg();

		// save the first poster out
		// maybe we did some clipping, so we're going to have to reencode it
		if (movie.hasPoster() && (writePoster || createFolderJpgEnabledPreference)
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
		if (movie.hasFanart() && writeFanart && ((fanartFile.exists() == writeFanartIfAlreadyExists) || !fanartFile.exists())) {
			if (movie.hasFanart()) {
				Thumb fanartToSaveToDisk;
				if (movie.getPreferredFanartToWriteToDisk() != null)
					fanartToSaveToDisk = movie.getPreferredFanartToWriteToDisk();
				else
					fanartToSaveToDisk = movie.getFanart().getFirst();
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
		if (targetFolderForExtraFanartFolderAndActorFolder != null && preferences.extraFanartScrapingEnabled()) {
			System.out.println("Starting write of extra fanart into " + targetFolderForExtraFanartFolderAndActorFolder);
			writeExtraFanart(targetFolderForExtraFanartFolderAndActorFolder);
		}

		//write the .actor images, if the preference for it is set
		if (preferences.downloadActorImagesToActorFolder() && targetFolderForExtraFanartFolderAndActorFolder != null) {
			System.out.println("Writing .actor images into " + targetFolderForExtraFanartFolderAndActorFolder);
			writeActorImagesToFolder(targetFolderForExtraFanartFolderAndActorFolder);
		}

		//write out the trailer, if the preference for it is set
		Trailer trailerToWrite = movie.getTrailer();
		if (preferences.writeTrailerToFile() && trailerToWrite != null && trailerToWrite.getTrailer().length() > 0) {
			trailerToWrite.writeTrailerToFile(trailerFile);
		}
	}

	public static void writeActorImagesToFolder(Movie movie, File targetFolder) throws IOException {
		File actorFolder = null;
		if (targetFolder.isDirectory()) {
			actorFolder = new File(targetFolder + File.separator + ".actors");
		} else if (targetFolder.isFile()) {
			actorFolder = new File(targetFolder.getParent() + File.separator + ".actors");
		}
		//Don't create an empty .actors folder with no actors underneath it
		if (movie.hasAtLeastOneActorThumbnail() && actorFolder != null) {
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

			for (Actor currentActor : movie.getActors()) {
				String currentActorToFileName = currentActor.getName().replace(' ', '_');
				File fileNameToWrite = new File(actorFolder.getPath() + File.separator + currentActorToFileName + ".jpg");
				currentActor.writeImageToFile(fileNameToWrite);
				//reload from disk instead of cache since the cache is now pointing to the wrong image and the disk has the correct newly edited one
				if (currentActor.isThumbEdited())
					ImageCache.removeImageFromCache(fileNameToWrite.toURI().toURL(), false);
			}

		}
	}

}
