package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.scrapers

import com.github.youngerdryas89.moviescraper.model.dataitem.Actor
import com.github.youngerdryas89.moviescraper.model.dataitem.Actresses
import com.github.youngerdryas89.moviescraper.model.dataitem.Director
import com.github.youngerdryas89.moviescraper.model.dataitem.Directors
import com.github.youngerdryas89.moviescraper.model.dataitem.Genre
import com.github.youngerdryas89.moviescraper.model.dataitem.Identity
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
import com.github.youngerdryas89.moviescraper.model.dataitem.Tag
import com.github.youngerdryas89.moviescraper.model.dataitem.Tagline
import com.github.youngerdryas89.moviescraper.model.dataitem.Title
import com.github.youngerdryas89.moviescraper.model.dataitem.Top250
import com.github.youngerdryas89.moviescraper.model.dataitem.Trailer
import com.github.youngerdryas89.moviescraper.model.dataitem.Votes
import com.github.youngerdryas89.moviescraper.model.dataitem.Year

data class ScraperProfile(
    val scrapeTitle: () -> Title,
    val scrapeOriginalTitle: () -> OriginalTitle,
    val scrapeSortTitle: () -> SortTitle,
    val scrapeSet: () -> Series,
    val scrapeRating: () -> Rating,
    val scrapeReleaseDate: () -> ReleaseDate,
    val scrapeYear: () -> Year,
    val scrapeTop250: () -> Top250,
    val scrapeVotes: () -> Votes,
    val scrapeOutline: () -> Outline,
    val scrapePlot: () -> Plot,
    val scrapeTagline: () -> Tagline,
    val scrapeRuntime: () -> Runtime,
    val scrapePosters: (cropPosters: Boolean) -> List<String>,
    val scrapeFanart: () -> List<String>,
    val scrapeExtraFanart: () -> List<String>,
    val scrapeMPAA: () -> MPAARating,
    val scrapeID: () -> Identity.Id,
    val scrapeGenres: () -> List<Genre>,
    val scrapeActors: () -> Actresses,
    val scrapeDirectors: () -> Directors,
    val scrapeStudio: () -> Studio,
    val scrapeTrailer: () -> Trailer,
    val scrapeTags: () -> List<Tag>
)
