package com.github.youngerdryas89.moviescraper.model

import com.github.youngerdryas89.moviescraper.model.dataitem.Actresses
import com.github.youngerdryas89.moviescraper.model.dataitem.BLANK_ORIGINAL_TITLE
import com.github.youngerdryas89.moviescraper.model.dataitem.BLANK_SORT_TITLE
import com.github.youngerdryas89.moviescraper.model.dataitem.BLANK_TITLE
import com.github.youngerdryas89.moviescraper.model.dataitem.DataItemSource
import com.github.youngerdryas89.moviescraper.model.dataitem.Directors
import com.github.youngerdryas89.moviescraper.model.dataitem.Genre
import com.github.youngerdryas89.moviescraper.model.dataitem.ID
import com.github.youngerdryas89.moviescraper.model.dataitem.MPAARating
import com.github.youngerdryas89.moviescraper.model.dataitem.OriginalTitle
import com.github.youngerdryas89.moviescraper.model.dataitem.Outline
import com.github.youngerdryas89.moviescraper.model.dataitem.Plot
import com.github.youngerdryas89.moviescraper.model.dataitem.Rating
import com.github.youngerdryas89.moviescraper.model.dataitem.ReleaseDate
import com.github.youngerdryas89.moviescraper.model.dataitem.Runtime
import com.github.youngerdryas89.moviescraper.model.dataitem.Series
import com.github.youngerdryas89.moviescraper.model.dataitem.SortTitle
import com.github.youngerdryas89.moviescraper.model.dataitem.Studio
import com.github.youngerdryas89.moviescraper.model.dataitem.Thumb
import com.github.youngerdryas89.moviescraper.model.dataitem.Title
import com.github.youngerdryas89.moviescraper.model.dataitem.Titling
import com.github.youngerdryas89.moviescraper.model.dataitem.Top250
import com.github.youngerdryas89.moviescraper.model.dataitem.Trailer
import com.github.youngerdryas89.moviescraper.model.dataitem.Votes
import com.github.youngerdryas89.moviescraper.model.dataitem.Year

data class MovieData(
    val actors : Actresses?,
    val directors : Directors?,
    val fanart : List<Thumb> = emptyList(),
    val extrafanart : List<Thumb>,
    val genres : List<Genre>,
    val id : ID? = ID.BLANK_ID,
    val mpaarating : MPAARating = MPAARating.RATING_XXX,
    val originalTitle : Titling.OriginalTitle?,
    val outline: Outline?,
    val plot: Plot?,
    val posters : List<Thumb>,
    val rating : Rating?,
    val releaseDate : ReleaseDate?,
    val runtime: Runtime?,
    val series: Series?,
    val sortTitle: Titling.SortTitle?,
    val studio: Studio?,
    val title: Titling.Title?,
    val top250: Top250?,
    val trailer : Trailer?,
    val votes : Votes?,
    val year : Year?,
    val fileName : String?,
    val scraperSource : DataItemSource?
    ) {
}

fun setAllDataItemSources(movieData: MovieData) : MovieData {

    return movieData.copy(
//        actors= movieData.actors.map { actor -> actor.dataItemSource = movieData.scraperSource; actor},
//        directors = movieData.directors.map { director -> director.dataItemSource = movieData.scraperSource; director },
        fanart =  movieData.fanart.map { fanart -> fanart.dataItemSource = movieData.scraperSource; fanart },
        extrafanart = movieData.extrafanart.map { extrafanart -> extrafanart.dataItemSource = movieData.scraperSource; extrafanart },
        genres = movieData.genres.map { genres -> genres.dataItemSource = movieData.scraperSource; genres },
        id= movieData.id.let { id -> id?.dataItemSource = movieData.scraperSource; id },
        mpaarating = movieData.mpaarating.let { rating -> rating?.dataItemSource = movieData.scraperSource; rating },
//        originalTitle= movieData.originalTitle.let { title -> title?.dataItemSource = movieData.scraperSource; title },
        outline= movieData.outline.let { outline -> outline?.dataItemSource = movieData.scraperSource; outline },
        plot = movieData.plot.let { plot -> plot?.dataItemSource = movieData.scraperSource; plot },
        posters = movieData.posters.map { poster -> poster.dataItemSource = movieData.scraperSource; poster },
        rating = movieData.rating?.let { rating -> rating.dataItemSource = movieData.scraperSource; rating },
        releaseDate = movieData.releaseDate.let { releaseDate -> releaseDate?.dataItemSource = movieData.scraperSource; releaseDate },
        runtime = movieData.runtime.let { runtime -> runtime?.dataItemSource = movieData.scraperSource; runtime },
        series = movieData.series.let { set -> set?.dataItemSource = movieData.scraperSource; set },
//        sortTitle = movieData.sortTitle.let { title -> title?.dataItemSource = movieData.scraperSource; title },
        top250 = movieData.top250.let { pos -> pos?.dataItemSource = movieData.scraperSource; pos },
        trailer = movieData.trailer.let { trailer -> trailer?.dataItemSource = movieData.scraperSource; trailer },
        votes = movieData.votes.let { votes -> votes?.dataItemSource = movieData.scraperSource; votes },
        year = movieData.year?.let { year -> year.dataItemSource = movieData.scraperSource; year },
        fileName = movieData.fileName,
        scraperSource = movieData.scraperSource
    )

}


fun createEmptyMovie() : MovieData {
    return MovieData(
        fanart = emptyList(),
        extrafanart = emptyList(),
        genres = emptyList(),
        id = ID.BLANK_ID,
        mpaarating = MPAARating.BLANK_RATING,
        originalTitle = BLANK_ORIGINAL_TITLE,
        outline = Outline.BLANK_OUTLINE,
        plot = Plot.BLANK_PLOT,
        posters = emptyList(),
        rating = Rating.BLANK_RATING,
        releaseDate = ReleaseDate.BLANK_RELEASEDATE,
        runtime = Runtime.BLANK_RUNTIME,
        series = Series.BLANK_SERIES,
        sortTitle = BLANK_SORT_TITLE,
        studio = Studio.BLANK_STUDIO,
        //tagline = Tagline.BLANK_TAGLINE,
        title = BLANK_TITLE,
        top250 = Top250.BLANK_TOP250,
        trailer = Trailer.BLANK_TRAILER,
        votes = Votes.BLANK_VOTES,
        year = Year.BLANK_YEAR,
        fileName = "",
        actors = TODO(),
        directors = TODO(),
        scraperSource = TODO(),
    )
}

fun hasPosters(movie: MovieData) : Boolean {
    return movie.posters.isNotEmpty()
}

//fun getUnstackMovieName(file: File){
//    var
//}
//
//
//fun createMovieFromNfo(nfoFile : File) : MovieData? {
//    return null
//}
