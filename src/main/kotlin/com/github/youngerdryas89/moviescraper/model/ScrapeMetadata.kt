package com.github.youngerdryas89.moviescraper.model

import arrow.core.Either
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfile
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileSettings
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.scrapers.ScrapingInfo
import org.jsoup.nodes.Document
import java.io.File
import java.nio.file.Path

typealias SortedMovieFiles = List<File>
typealias MovieFile = File
typealias CleansedTitle = String

data class ScrapeMetadata (
    val directory: Path,
    val document: Document?,
    val parentDirectory: Path,
    val movie: Either<MovieFile, SortedMovieFiles>,
    val siteProfile: ScrapingInfo,
    val siteProfileInfo: SpecificProfileInfo,
    val siteProfileSettings: SpecificProfileSettings,
    val cleansedFilename: CleansedTitle? = null,
    val finishedMovie: MovieData? = null,
    var tableCache: Map<String, String>? = null
)