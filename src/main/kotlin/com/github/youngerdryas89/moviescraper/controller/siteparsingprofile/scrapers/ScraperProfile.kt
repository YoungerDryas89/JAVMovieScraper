package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.scrapers

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.left
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


sealed interface DomainError {
    data class ScrapingError(val message: String) : DomainError
    data class NetworkError(val statusCode: Int, val message: String) : DomainError

    // When the css query succeeds but whatever we're looking for is empty
    // or doesn't have the information
    object Nothing : DomainError

    // For when the site doesn't display a type information
    object NotAvailable : DomainError

    // Just for when the function is not implemented
    object NotImplemented : DomainError

}

data class ScraperProfile(
    val scrapeTitle: (context: ScrapeMetadata) -> Either<DomainError, Titling.Title> = { _ -> DomainError.NotAvailable.left() },
    val scrapeOriginalTitle: (context: ScrapeMetadata) -> Either<DomainError, Titling.OriginalTitle> = { _ -> DomainError.NotAvailable.left() },
    val scrapeSortTitle: (context: ScrapeMetadata) -> Either<DomainError, Titling.SortTitle> = { _ -> DomainError.NotAvailable.left() },
    val scrapeSet: (context: ScrapeMetadata) -> Either<DomainError, Series?> = { _ -> DomainError.NotAvailable.left() },
    val scrapeRating: (context: ScrapeMetadata) -> Either<DomainError, Rating?> = { _ -> DomainError.NotAvailable.left() },
    val scrapeReleaseDate: (context: ScrapeMetadata) -> Either<DomainError, ReleaseDate?> = { _ -> DomainError.NotAvailable.left() },
    val scrapeYear: (context: ScrapeMetadata) -> Either<DomainError, Year?> = { _ -> DomainError.NotAvailable.left() },
    val scrapeTop250: (context: ScrapeMetadata) -> Either<DomainError, Top250?> = { _ -> DomainError.NotAvailable.left() },
    val scrapeVotes: (context: ScrapeMetadata) -> Either<DomainError, Votes?> = { _ -> DomainError.NotAvailable.left() },
    val scrapeOutline: (context: ScrapeMetadata) -> Either<DomainError, Outline?> = { _ -> DomainError.NotAvailable.left() },
    val scrapePlot: (context: ScrapeMetadata) -> Either<DomainError, Plot?> = { _ -> DomainError.NotAvailable.left() },
    val scrapeTagline: (context: ScrapeMetadata) -> Either<DomainError, Tagline?> = { _ -> DomainError.NotAvailable.left() },
    val scrapeRuntime: (context: ScrapeMetadata) -> Either<DomainError, Runtime?> = { _ -> DomainError.NotAvailable.left() },
    val scrapePosters: (context: ScrapeMetadata) -> Either<DomainError, NonEmptyList<String>> = { _ -> DomainError.NotAvailable.left() },
    val scrapeFanart: (context: ScrapeMetadata) -> Either<DomainError, NonEmptyList<String>> = { _ -> DomainError.NotAvailable.left() },
    val scrapeExtraFanart: (context: ScrapeMetadata) -> Either<DomainError, NonEmptyList<String>> = { _ -> DomainError.NotAvailable.left() },
    val scrapeMPAA: (context: ScrapeMetadata) -> MPAARating = { _ -> MPAARating.RATING_XXX},
    val scrapeID: (context: ScrapeMetadata) -> Either<DomainError, ID> = { _ -> DomainError.NotAvailable.left() },
    val scrapeGenres: (context: ScrapeMetadata) -> Either<DomainError, NonEmptyList<Genre>?> = { _ -> DomainError.NotAvailable.left() },
    val scrapeActors: (context: ScrapeMetadata) -> Either<DomainError, Actresses?> = { _ -> DomainError.NotAvailable.left() },
    val scrapeDirectors: (context: ScrapeMetadata) -> Either<DomainError, Directors?> = { _ -> DomainError.NotAvailable.left() },
    val scrapeStudio: (context: ScrapeMetadata) -> Either<DomainError, Studio?> = { _ -> DomainError.NotAvailable.left() },
    val scrapeTrailer: (context: ScrapeMetadata) -> Either<DomainError, Trailer?> = { _ -> DomainError.NotAvailable.left() },
    val scrapeTags: (context: ScrapeMetadata) -> Either<DomainError, NonEmptyList<Tag>?> = { _ -> DomainError.NotAvailable.left() }
)
