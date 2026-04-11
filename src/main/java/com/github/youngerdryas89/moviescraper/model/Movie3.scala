package com.github.youngerdryas89.moviescraper.model

import com.github.youngerdryas89.moviescraper.controller.{FileDownloaderUtilities, ScraperGroupName, Similarity}
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.{DetermineMovie, SecurityPassthrough, SiteParsingProfile}
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.Data18MovieParsingProfile
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.Data18WebContentParsingProfile
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.DmmParsingProfile
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.IAFDParsingProfile
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.JavLibraryParsingProfile
import com.github.youngerdryas89.moviescraper.controller.xmlserialization.KodiXmlMovieBean
import com.github.youngerdryas89.moviescraper.model.dataitem._
import com.github.youngerdryas89.moviescraper.model.dataitem.Runtime
import com.github.youngerdryas89.moviescraper.model.preferences.MoviescraperPreferences
import com.github.youngerdryas89.moviescraper.view.FileDetailPanel
import com.github.youngerdryas89.moviescraper.view.GUIMain
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.jetbrains.annotations.NotNull
import org.jsoup.nodes.Document

import java.awt.image.RenderedImage
import java.io._
import java.nio.file.AccessDeniedException
import java.nio.file.Files
import java.nio.file.LinkOption
import java.util
import java.util.Comparator
import java.util.stream.Collectors
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.stream.FileImageOutputStream

case class Movie(
    var actors: util.ArrayList[Actor],
    var directors: util.ArrayList[Director],
    var fanart: Array[Thumb],
    var extraFanart: Array[Thumb],
    var preferredFanartToWriteToDisk: Thumb,
    var genres: util.ArrayList[Genre],
    var id: ID,
    var mpaa: MPAARating,
    var originalTitle: OriginalTitle,
    var outline: Outline,
    var plot: Plot,
    var posters: Array[Thumb],
    var rating: Rating,
    var releaseDate: ReleaseDate,
    var runtime: Runtime,
    var series: Series,
    var sortTitle: SortTitle,
    var studio: Studio,
    var tagline: Tagline,
    var tags: util.ArrayList[Tag],
    var title: Title,
    var allTitles: java.util.List[Title],
    var top250: Top250,
    var trailer: Trailer,
    var votes: Votes,
    var year: Year,
    var fileName: String
)

object MovieOps {

  def createMovieFromSite(siteToScrapeFrom: SiteParsingProfile, parent: GUIMain): Movie = {
    val title = siteToScrapeFrom.scrapeTitle()
    val originalTitle = siteToScrapeFrom.scrapeOriginalTitle()
    val id = siteToScrapeFrom.scrapeID()
    val sortTitle = siteToScrapeFrom.scrapeSortTitle()
    val series = siteToScrapeFrom.scrapeSet()
    val rating = siteToScrapeFrom.scrapeRating()
    val year = siteToScrapeFrom.scrapeYear()
    val top250 = siteToScrapeFrom.scrapeTop250()
    val trailer = siteToScrapeFrom.scrapeTrailer()
    val votes = siteToScrapeFrom.scrapeVotes()
    val outline = siteToScrapeFrom.scrapeOutline()
    val plot = siteToScrapeFrom.scrapePlot()
    val tagline = siteToScrapeFrom.scrapeTagline()
    val studio = siteToScrapeFrom.scrapeStudio()
    val releaseDate = siteToScrapeFrom.scrapeReleaseDate()
    val runtime = siteToScrapeFrom.scrapeRuntime()
    var posters = siteToScrapeFrom.scrapePosters(parent.getFileDetailPanel.cropPosters())
    if (posters.length > 0 && posters(0).hasDerivations) {
      posters(0) = posters(0).derivedChild()
    }
    val fanart = siteToScrapeFrom.scrapeFanart()
    val extraFanart = siteToScrapeFrom.scrapeExtraFanart()
    val mpaa = siteToScrapeFrom.scrapeMPAA()
    val actors = siteToScrapeFrom.scrapeActors()
    val genres = siteToScrapeFrom.scrapeGenres()
    val tags = siteToScrapeFrom.scrapeTags()
    val directors = siteToScrapeFrom.scrapeDirectors()

    val movie = Movie(
      actors,
      directors,
      fanart,
      extraFanart,
      null, // preferredFanartToWriteToDisk
      genres,
      id,
      mpaa,
      originalTitle,
      outline,
      plot,
      posters,
      rating,
      releaseDate,
      runtime,
      series,
      sortTitle,
      studio,
      tagline,
      tags,
      title,
      new util.ArrayList[Title](), // allTitles
      top250,
      trailer,
      votes,
      year,
      null // fileName
    )

    setAllDataItemSources(movie, siteToScrapeFrom)

    val fileNameOfScrapedMovie = siteToScrapeFrom.getFileNameOfScrapedMovie
    if (fileNameOfScrapedMovie != null && fileNameOfScrapedMovie.trim.length > 0) {
      movie.fileName = fileNameOfScrapedMovie
    }

    val scraperPreferences = MoviescraperPreferences.getInstance()
    if (scraperPreferences.getUseFileNameAsTitle && movie.fileName != null && movie.fileName.length > 0) {
      movie.title = new Title(movie.fileName)
      movie.title.setDataItemSource(new DefaultDataItemSource())
    }

    appendIDToStartOfTitle(movie)
    movie
  }

  private def setAllDataItemSources(movie: Movie, siteToScrapeFrom: SiteParsingProfile): Unit = {
    movie.originalTitle.setDataItemSource(siteToScrapeFrom)
    movie.title.setDataItemSource(siteToScrapeFrom)
    movie.sortTitle.setDataItemSource(siteToScrapeFrom)
    movie.series.setDataItemSource(siteToScrapeFrom)
    movie.rating.setDataItemSource(siteToScrapeFrom)
    movie.year.setDataItemSource(siteToScrapeFrom)
    movie.top250.setDataItemSource(siteToScrapeFrom)
    movie.trailer.setDataItemSource(siteToScrapeFrom)
    movie.votes.setDataItemSource(siteToScrapeFrom)
    movie.outline.setDataItemSource(siteToScrapeFrom)
    movie.plot.setDataItemSource(siteToScrapeFrom)
    movie.tagline.setDataItemSource(siteToScrapeFrom)
    movie.studio.setDataItemSource(siteToScrapeFrom)
    movie.releaseDate.setDataItemSource(siteToScrapeFrom)
    movie.runtime.setDataItemSource(siteToScrapeFrom)
    setDataItemSourceOnThumbs(movie.posters, siteToScrapeFrom)
    setDataItemSourceOnThumbs(movie.fanart, siteToScrapeFrom)
    setDataItemSourceOnThumbs(movie.extraFanart, siteToScrapeFrom)
    movie.mpaa.setDataItemSource(siteToScrapeFrom)
    movie.id.setDataItemSource(siteToScrapeFrom)

    movie.actors.forEach(_.setDataItemSource(siteToScrapeFrom))
    movie.genres.forEach(_.setDataItemSource(siteToScrapeFrom))
    movie.tags.forEach(_.setDataItemSource(siteToScrapeFrom))
    movie.directors.forEach(_.setDataItemSource(siteToScrapeFrom))
  }

  private def appendIDToStartOfTitle(movie: Movie): Unit = {
    if (movie.id != null && movie.id.getId != null && movie.id.getId.trim.length > 0 && hasValidTitle(movie)) {
      if (MoviescraperPreferences.getInstance().getAppendIDToStartOfTitle)
        movie.title.setTitle(movie.id.getId + " - " + movie.title.getTitle)
      else
        movie.title.setTitle(movie.title.getTitle.replace(movie.id.getId, ""))
    }
  }

  private def setDataItemSourceOnThumbs(thumbs: Array[Thumb], dataItemSource: DataItemSource): Unit = {
    thumbs.foreach(_.setDataItemSource(dataItemSource))
  }

  @throws[IOException]
  def createMovieFromNfo(nfoFile: File): Option[Movie] = {
    val fisTargetFile = new FileInputStream(nfoFile)
    try {
      var targetFileStr = IOUtils.toString(fisTargetFile, "UTF-8")
      if (targetFileStr.contains("<?xml")) {
        while (targetFileStr.length > 0 && !targetFileStr.startsWith("<?xml")) {
          if (targetFileStr.length > 1) {
            targetFileStr = targetFileStr.substring(1, targetFileStr.length)
          } else {
            return None
          }
        }
      }
      val xmlMovieBean = KodiXmlMovieBean.makeFromXML(targetFileStr)
      if (xmlMovieBean != null) {
        Some(xmlMovieBean.toMovie)
      } else {
        None
      }
    } finally {
      fisTargetFile.close()
    }
  }

  def toXML(movie: Movie): String = movie.title.toXML

  @throws[IOException]
  def writeExtraFanart(movie: Movie, directoryMovieIsIn: File): Unit = {
    if (directoryMovieIsIn != null && directoryMovieIsIn.exists && directoryMovieIsIn.isDirectory && movie.extraFanart.length > 0) {
      val extraFanartFolder = new File(directoryMovieIsIn.getPath + File.separator + "extrafanart")
      FileUtils.forceMkdir(extraFanartFolder)
      var currentExtraFanartNumber = 1
      for (currentExtraFanart <- movie.extraFanart) {
        val fileNameToWrite = new File(extraFanartFolder.getPath + File.separator + "fanart" + currentExtraFanartNumber + ".jpg")
        if (!fileNameToWrite.exists()) {
          System.out.println("Writing extrafanart to " + fileNameToWrite)
          currentExtraFanart.writeImageToFile(fileNameToWrite)
        }
        currentExtraFanartNumber += 1
      }
    }
  }

  @throws[IOException]
  def writeToFile(movie: Movie, nfoFile: File, posterFile: File, fanartFile: File, currentlySelectedFolderJpgFile: File, targetFolderForExtraFanartFolderAndActorFolder: File, trailerFile: File, preferences: MoviescraperPreferences, uncropButtonPressed: Boolean): Unit = {
    if (!movie.title.getTitle.startsWith(movie.id.getId)) {
      appendIDToStartOfTitle(movie)
    }
    var xml = new KodiXmlMovieBean(movie).toXML
    xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>" + "\n" + xml
    if (nfoFile != null && xml.length > 0) nfoFile.delete()
    FileUtils.writeStringToFile(nfoFile, xml, org.apache.commons.lang3.CharEncoding.UTF_8)

    var posterToSaveToDisk: Thumb = null
    if (movie.posters != null && movie.posters.length > 0) posterToSaveToDisk = movie.posters(0)

    if (uncropButtonPressed) {
      if (posterToSaveToDisk.hasDerivations) posterToSaveToDisk = posterToSaveToDisk.derivedChild
    } else {
      if (posterToSaveToDisk.isModified) posterToSaveToDisk = posterToSaveToDisk.getOriginalImage
    }

    assert(posterToSaveToDisk != null)

    val writePoster = preferences.getWriteFanartAndPostersPreference
    val writeFanart = preferences.getWriteFanartAndPostersPreference
    val writePosterIfAlreadyExists = preferences.getOverWriteFanartAndPostersPreference
    val writeFanartIfAlreadyExists = preferences.getOverWriteFanartAndPostersPreference
    val createFolderJpgEnabledPreference = preferences.getCreateFolderJpgEnabledPreference

    if (movie.posters.length > 0 && (writePoster || createFolderJpgEnabledPreference) && ((posterFile.exists == writePosterIfAlreadyExists) || (!posterFile.exists || createFolderJpgEnabledPreference))) {
      if (posterToSaveToDisk != null && (posterToSaveToDisk.isModified || createFolderJpgEnabledPreference || !posterFile.exists || writePosterIfAlreadyExists)) {
        val iter = ImageIO.getImageWritersByFormatName("jpeg")
        val writer = iter.next
        val iwp = writer.getDefaultWriteParam
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
        iwp.setCompressionQuality(1)
        val image = new IIOImage(posterToSaveToDisk.getThumbImage.asInstanceOf[RenderedImage], null, null)

        if (writePoster && posterToSaveToDisk.isModified) {
          System.out.println("Writing poster to " + posterFile)
          try {
            val posterFileOutput = new FileImageOutputStream(posterFile)
            try {
              writer.setOutput(posterFileOutput)
              writer.write(null, image, iwp)
            } finally {
              posterFileOutput.close()
            }
          } catch {
            case e: IOException => System.err.println(e.getMessage)
          }
        } else if ((!posterFile.exists || writePosterIfAlreadyExists) && posterToSaveToDisk.getThumbURL != null) {
          System.out.println("Writing poster file from nfo: " + posterFile)
          FileDownloaderUtilities.writeURLToFile(posterToSaveToDisk.getThumbURL, posterFile, posterToSaveToDisk.getReferrerURL)
          ImageCache.replaceIfPresent(posterFile.toURI.toURL, posterToSaveToDisk.getThumbImage)
        }

        if (createFolderJpgEnabledPreference && currentlySelectedFolderJpgFile != null) {
          if (!posterToSaveToDisk.isModified && (!currentlySelectedFolderJpgFile.exists || (currentlySelectedFolderJpgFile.exists && writePosterIfAlreadyExists))) {
            try {
              System.out.println("Writing folder.jpg (no changes) to " + currentlySelectedFolderJpgFile)
              FileDownloaderUtilities.writeURLToFile(posterToSaveToDisk.getThumbURL, currentlySelectedFolderJpgFile, posterToSaveToDisk.getReferrerURL)
            } catch {
              case e: IOException => System.err.println(e.getMessage)
            }
          } else {
            if (!currentlySelectedFolderJpgFile.exists || (currentlySelectedFolderJpgFile.exists && writePosterIfAlreadyExists)) {
              System.out.println("Writing folder to " + currentlySelectedFolderJpgFile)
              try {
                val folderFileOutput = new FileImageOutputStream(currentlySelectedFolderJpgFile)
                try {
                  writer.setOutput(folderFileOutput)
                  writer.write(null, image, iwp)
                } finally {
                  folderFileOutput.close()
                }
              } catch {
                case e: IOException => System.err.println(e.getMessage)
              }
            } else {
              System.out.println("Skipping overwrite of folder.jpg due to preference setting")
            }
          }
        }
        writer.dispose()
      }
    }

    if (uncropButtonPressed && posterToSaveToDisk.isModified) {
      ImageCache.replaceIfPresent(posterFile.toURI.toURL, posterToSaveToDisk.getThumbImage)
      ImageCache.removeImageFromCache(posterToSaveToDisk.getThumbURL, false)
    }

    if (movie.fanart.length > 0 && writeFanart && ((fanartFile.exists == writeFanartIfAlreadyExists) || !fanartFile.exists)) {
      if (movie.fanart != null && movie.fanart.length > 0) {
        val fanartToSaveToDisk = if (movie.preferredFanartToWriteToDisk != null) movie.preferredFanartToWriteToDisk else movie.fanart(0)
        System.out.println("saving out first fanart to " + fanartFile)

        if (fanartToSaveToDisk.getImageIconThumbImage != null && fanartToSaveToDisk.isModified) {
          try {
            ImageIO.write(fanartToSaveToDisk.toBufferedImage, "jpg", fanartFile)
            ImageCache.replaceIfPresent(fanartFile.toURI.toURL, fanartToSaveToDisk.getThumbImage)
          } catch {
            case e: IOException =>
              System.err.println("Failed to write fanart due to io error")
              e.printStackTrace()
          }
        } else {
          FileDownloaderUtilities.writeURLToFile(fanartToSaveToDisk.getThumbURL, fanartFile, fanartToSaveToDisk.getReferrerURL)
          ImageCache.replaceIfPresent(fanartFile.toURI.toURL, fanartToSaveToDisk.getThumbImage)
        }
      }
    }

    if (targetFolderForExtraFanartFolderAndActorFolder != null && preferences.getExtraFanartScrapingEnabledPreference) {
      System.out.println("Starting write of extra fanart into " + targetFolderForExtraFanartFolderAndActorFolder)
      writeExtraFanart(movie, targetFolderForExtraFanartFolderAndActorFolder)
    }

    if (preferences.getDownloadActorImagesToActorFolderPreference && targetFolderForExtraFanartFolderAndActorFolder != null) {
      System.out.println("Writing .actor images into " + targetFolderForExtraFanartFolderAndActorFolder)
      writeActorImagesToFolder(movie, targetFolderForExtraFanartFolderAndActorFolder)
    }

    val trailerToWrite = movie.trailer
    if (preferences.getWriteTrailerToFile && trailerToWrite != null && trailerToWrite.getTrailer.length > 0) {
      trailerToWrite.writeTrailerToFile(trailerFile)
    }
  }

  @throws[IOException]
  def writeActorImagesToFolder(movie: Movie, targetFolder: File): Unit = {
    var actorFolder: File = null
    if (targetFolder.isDirectory) {
      actorFolder = new File(targetFolder + File.separator + ".actors")
    } else if (targetFolder.isFile) {
      actorFolder = new File(targetFolder.getParent + File.separator + ".actors")
    }

    if (hasAtLeastOneActorThumbnail(movie) && actorFolder != null) {
      FileUtils.forceMkdir(actorFolder)
      val path = actorFolder.toPath
      if (!Files.isHidden(path)) {
        val hidden = Files.getAttribute(path, "dos:hidden", LinkOption.NOFOLLOW_LINKS).asInstanceOf[Boolean]
        if (hidden != null && !hidden) {
          try {
            Files.setAttribute(path, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS)
          } catch {
            case e: AccessDeniedException => System.err.println("I was not allowed to make .actors folder hidden. This is not a big deal - continuing with write of actor files...")
          }
        }
      }
      for (currentActor <- movie.actors.asScala) {
        val currentActorToFileName = currentActor.getName.replace(' ', '_')
        val fileNameToWrite = new File(actorFolder.getPath + File.separator + currentActorToFileName + ".jpg")
        currentActor.writeImageToFile(fileNameToWrite)
        if (currentActor.isThumbEdited) ImageCache.removeImageFromCache(fileNameToWrite.toURI.toURL, false)
      }
    }
  }

  def hasPoster(movie: Movie): Boolean = movie.posters.length > 0

  @throws[IOException]
  def scrapeMovie(movieFile: File, siteToParseFrom: SiteParsingProfile, urlToScrapeFromDMM: String, useURLtoScrapeFrom: Boolean, @NotNull parent: GUIMain): Option[Movie] = {
    if (siteToParseFrom.getDiscardResults) return None

    val panel = parent.getFileDetailPanel
    val searchString = if (panel.shouldOverrideInferredId && !(panel.inferredId == "N/A")) siteToParseFrom.createSearchStringFromId(panel.inferredId)
    else siteToParseFrom.createSearchString(movieFile)

    var searchResults: Array[SearchResult] = null
    var searchResultNumberToUse = 0

    if (!useURLtoScrapeFrom) {
      searchResults = siteToParseFrom.getSearchResults(searchString)
      var levDistanceOfCurrentMatch = 999999
      val idFromMovieFile = if (panel.shouldOverrideInferredId && (!panel.inferredId.isEmpty || !(panel.inferredId == "N/A"))) panel.inferredId
      else DetermineMovie.findIDTagFromFile(movieFile, siteToParseFrom.isFirstWordOfFileIsID)

      if (!panel.shouldOverrideInferredId) panel.setInferredId(idFromMovieFile)

      if (searchResults.length == 0) {
        System.err.println("No title could be found with the provided Id.")
      }

      if (siteToParseFrom.getScraperGroupNames.contains(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP)) {
        for (i <- searchResults.indices) {
          val urltoMatch = searchResults(i).getUrlPath.toLowerCase
          val idFromMovieFileToMatch = idFromMovieFile.toLowerCase.replaceAll("-", "")
          if (urltoMatch.contains(idFromMovieFileToMatch) || (searchResults.length < 2)) {
            val candidateLevDistanceOfCurrentMatch = StringUtils.getLevenshteinDistance(urltoMatch.toLowerCase, idFromMovieFileToMatch)
            if (candidateLevDistanceOfCurrentMatch < levDistanceOfCurrentMatch) {
              levDistanceOfCurrentMatch = candidateLevDistanceOfCurrentMatch
              searchResultNumberToUse = i
            }
          }
        }
      } else {
        val title = siteToParseFrom.cleanseFilename(movieFile).toLowerCase
        val rated = searchResults.map(result => {
          val levNormalized = Similarity.calculateNormalizedLevenshteinDistance(title, result.getLabel)
          val jaccard = Similarity.calculateJaccardIndex(title, result.getLabel)
          val swa = (0.5 * jaccard) + ((1 - 0.5) * levNormalized)
          new RatedResult(result, swa)
        }).sorted(Comparator.comparingDouble[RatedResult](_.probability)).collect(Collectors.toList[RatedResult])
        searchResultNumberToUse = util.Arrays.asList(searchResults:_*).indexOf(rated.get(0).result)
      }
    } else {
      searchResults = new Array[SearchResult](1)
      siteToParseFrom match {
        case p: DmmParsingProfile => searchResults(0) = new SearchResult(urlToScrapeFromDMM)
        case p: Data18MovieParsingProfile => searchResults(0) = new SearchResult(urlToScrapeFromDMM)
        case p: Data18WebContentParsingProfile => searchResults(0) = new SearchResult(urlToScrapeFromDMM)
        case p: JavLibraryParsingProfile => searchResults(0) = new SearchResult(p.getOverrideURLJavLibrary)
        case p: IAFDParsingProfile => searchResults(0) = new SearchResult(urlToScrapeFromDMM)
        case _ =>
      }
      if (siteToParseFrom.getOverridenSearchResult != null) {
        searchResults(0) = siteToParseFrom.getOverridenSearchResult
        searchResultNumberToUse = 0
      }
    }

    if (searchResults.length > 0 && !searchResults(searchResultNumberToUse).getUrlPath.isEmpty) {
      System.out.println("Scraping this webpage for movie: " + searchResults(searchResultNumberToUse).getUrlPath)
      val searchResultToUse = searchResults(searchResultNumberToUse)
      val response = siteToParseFrom.downloadDocument(searchResultToUse)
      if (response == null || response.statusCode != 200 || response.statusCode > 399) {
        if (response != null) {
          System.err.println("Failed to connect to: " + searchResultToUse.getUrlPath)
          System.err.println(response.statusCode + " " + response.statusMessage)
          throw new RuntimeException("Failed to connect to: " + searchResultToUse.getUrlPath + "\n" + response.statusCode + " " + response.statusMessage)
        } else {
          System.err.println("Unable to connect to: " + searchResultToUse.getUrlPath + ", perhaps internet access is cut?")
          throw new RuntimeException("Unable to connect to: " + searchResultToUse.getUrlPath + ", perhaps internet access is cut?")
        }
      }
      var searchMatch = response.parse
      if (classOf[SecurityPassthrough].isAssignableFrom(siteToParseFrom.getClass)) {
        val siteParsingProfileSecurityPassthrough = siteToParseFrom.asInstanceOf[SecurityPassthrough]
        if (siteParsingProfileSecurityPassthrough.requiresSecurityPassthrough(searchMatch)) {
          searchMatch = siteParsingProfileSecurityPassthrough.runSecurityPassthrough(searchMatch, searchResultToUse)
        }
      }
      siteToParseFrom.setDocument(searchMatch)
      siteToParseFrom.prepareData()
      siteToParseFrom.setOverrideURLDMM(urlToScrapeFromDMM)
      Some(createMovieFromSite(siteToParseFrom, parent))
    } else {
      None
    }
  }

  def hasAtLeastOneActorThumbnail(movie: Movie): Boolean = {
    movie.actors.asScala.exists(actor => actor.getThumb != null && actor.getThumb.getThumbURL != null && !actor.getThumb.getThumbURL.equals(""))
  }

  def hasFanart(movie: Movie): Boolean = movie.fanart.length > 0

  def getEmptyMovie: Movie = {
    val actors = new util.ArrayList[Actor]
    val directors = new util.ArrayList[Director]
    val genres = new util.ArrayList[Genre]
    val tags = new util.ArrayList[Tag]
    val fanart = new Array[Thumb](0)
    val extraFanart = new Array[Thumb](0)
    val posters = new Array[Thumb](0)
    val id = new ID("")
    val mpaa = new MPAARating("")
    val originalTitle = OriginalTitle.BLANK_ORIGINALTITLE
    val outline = Outline.BLANK_OUTLINE
    val plot = Plot.BLANK_PLOT
    val rating = Rating.BLANK_RATING
    val releaseDate = ReleaseDate.BLANK_RELEASEDATE
    val runtime = Runtime.BLANK_RUNTIME
    val series = Series.BLANK_SERIES
    val sortTitle = SortTitle.BLANK_SORTTITLE
    val studio = Studio.BLANK_STUDIO
    val tagline = Tagline.BLANK_TAGLINE
    val title = new Title("")
    val top250 = Top250.BLANK_TOP250
    val trailer = new Trailer(null)
    val votes = Votes.BLANK_VOTES
    val year = Year.BLANK_YEAR

    new Movie(actors, directors, fanart, extraFanart, null, genres, id, mpaa, originalTitle, outline, plot, posters, rating, releaseDate, runtime, series, sortTitle, studio, tagline, tags, title, new util.ArrayList[Title](), top250, trailer, votes, year, null)
  }

  def moveExistingPosterToFront(movie: Movie, posterToGoToFront: Thumb): Unit = {
    if (posterToGoToFront != null) {
      val existingPosters = new util.ArrayList(util.Arrays.asList(movie.posters: _*))
      val didListContainPoster = existingPosters.remove(posterToGoToFront)
      if (didListContainPoster) {
        existingPosters.add(0, posterToGoToFront)
        val posterArray = new Array[Thumb](existingPosters.size)
        movie.posters = existingPosters.toArray(posterArray)
      }
    }
  }

  def moveExistingFanartToFront(movie: Movie, fanartToGoToFront: Thumb): Unit = {
    if (fanartToGoToFront != null) {
      val existingFanarts = new util.ArrayList(util.Arrays.asList(movie.fanart: _*))
      val didListContainPoster = existingFanarts.remove(fanartToGoToFront)
      if (didListContainPoster) {
        existingFanarts.add(0, fanartToGoToFront)
        val fanartArray = new Array[Thumb](existingFanarts.size)
        movie.fanart = existingFanarts.toArray(fanartArray)
      }
    }
  }

  def hasValidTitle(movie: Movie): Boolean = movie.title != null && movie.title.getTitle != null && movie.title.getTitle.length > 0
}
