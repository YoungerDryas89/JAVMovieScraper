package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.scrapers

import com.github.youngerdryas89.moviescraper.model.ScrapeMetadata
import com.github.youngerdryas89.moviescraper.model.dataitem.Actor
import com.github.youngerdryas89.moviescraper.model.dataitem.Actresses
import com.github.youngerdryas89.moviescraper.model.dataitem.Director
import com.github.youngerdryas89.moviescraper.model.dataitem.Directors
import com.github.youngerdryas89.moviescraper.model.dataitem.Genre
import com.github.youngerdryas89.moviescraper.model.dataitem.ID
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
import com.github.youngerdryas89.moviescraper.model.dataitem.Titling
import com.github.youngerdryas89.moviescraper.model.dataitem.Top250
import com.github.youngerdryas89.moviescraper.model.dataitem.Trailer
import com.github.youngerdryas89.moviescraper.model.dataitem.Votes
import com.github.youngerdryas89.moviescraper.model.dataitem.Year

data class ScraperProfile(
    val scrapeTitle: (context: ScrapeMetadata) -> Titling.Title?,
    val scrapeOriginalTitle: (context: ScrapeMetadata) -> Titling.OriginalTitle?,
    val scrapeSortTitle: (context: ScrapeMetadata) -> Titling.SortTitle?,
    val scrapeSet: (context: ScrapeMetadata) -> Series?,
    val scrapeRating: (context: ScrapeMetadata) -> Rating?,
    val scrapeReleaseDate: (context: ScrapeMetadata) -> ReleaseDate?,
    val scrapeYear: (context: ScrapeMetadata) -> Year?,
    val scrapeTop250: (context: ScrapeMetadata) -> Top250?,
    val scrapeVotes: (context: ScrapeMetadata) -> Votes?,
    val scrapeOutline: (context: ScrapeMetadata) -> Outline?,
    val scrapePlot: (context: ScrapeMetadata) -> Plot?,
    val scrapeTagline: (context: ScrapeMetadata) -> Tagline?,
    val scrapeRuntime: (context: ScrapeMetadata) -> Runtime?,
    val scrapePosters: (context: ScrapeMetadata) -> List<String>,
    val scrapeFanart: (context: ScrapeMetadata) -> List<String>,
    val scrapeExtraFanart: (context: ScrapeMetadata) -> List<String>,
    val scrapeMPAA: (context: ScrapeMetadata) -> MPAARating,
    val scrapeID: (context: ScrapeMetadata) -> ID?,
    val scrapeGenres: (context: ScrapeMetadata) -> List<Genre>,
    val scrapeActors: (context: ScrapeMetadata) -> Actresses,
    val scrapeDirectors: (context: ScrapeMetadata) -> Directors,
    val scrapeStudio: (context: ScrapeMetadata) -> Studio?,
    val scrapeTrailer: (context: ScrapeMetadata) -> Trailer?,
    val scrapeTags: (context: ScrapeMetadata) -> List<Tag>
)
