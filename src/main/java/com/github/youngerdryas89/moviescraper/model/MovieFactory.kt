package com.github.youngerdryas89.moviescraper.model

import com.github.youngerdryas89.moviescraper.controller.xmlserialization.KodiXmlMovieBean
import com.github.youngerdryas89.moviescraper.model.dataitem.*
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileInputStream
import java.io.IOException

object MovieFactory {
    /**
     * Create a movie by reading in a values from a nfo file created by previously scraping the movie and then writing the metadata out to the file
     * 
     * @param nfoFile
     * @throws IOException
     */
    @JvmStatic
	@Throws(IOException::class)
    fun createMovieFromNfo(nfoFile: File): Movie? {
        var movieFromNfo: Movie? = null
        FileInputStream(nfoFile).use { fisTargetFile ->
            var targetFileStr = IOUtils.toString(fisTargetFile, "UTF-8")
            // Sometimes there's some junk before the prolog tag. Find the start of the XML content and use that.
            val xmlStartIndex = targetFileStr.indexOf("<?xml")
            if (xmlStartIndex > 0) {
                targetFileStr = targetFileStr.substring(xmlStartIndex)
            } else if (xmlStartIndex == -1) {
                // If there's no XML tag at all, we can't parse it.
                return null
            }
            val xmlMovieBean = KodiXmlMovieBean.makeFromXML(targetFileStr)
            if (xmlMovieBean != null) {
                movieFromNfo = xmlMovieBean.toMovie()
            }
            return movieFromNfo
        }
    }

    @JvmStatic
	fun createEmptyMovie(): Movie {
        val actors = ArrayList<Actor?>()
        val directors = ArrayList<Director?>()
        val genres = ArrayList<Genre?>()
        val tags = ArrayList<Tag?>()

        val fanart = arrayOfNulls<Thumb>(0)
        val extraFanart = arrayOfNulls<Thumb>(0)
        val posters = arrayOfNulls<Thumb>(0)

        val id = ID.BLANK_ID
        val mpaa = MPAARating.BLANK_RATING
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
        val title = Title.BLANK_TITLE
        val top250 = Top250.BLANK_TOP250
        val trailer = Trailer.BLANK_TRAILER
        val votes = Votes.BLANK_VOTES
        val year = Year.BLANK_YEAR

        return Movie(
            actors,
            directors,
            fanart,
            extraFanart,
            genres,
            tags,
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
            title,
            top250,
            trailer,
            votes,
            year
        )
    }
}
