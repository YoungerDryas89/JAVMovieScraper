package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.scrapers

import arrow.core.Either
import com.github.youngerdryas89.moviescraper.model.ScrapeMetadata
import com.github.youngerdryas89.moviescraper.model.dataitem.DataItemSource


sealed class Extractor {
    abstract val selector: String
    data class Selector(override val selector: String) : Extractor()
    data class Attribute(override val selector: String, val attribute: String) : Extractor()
    data class Text(override val selector: String) : Extractor()
    data class TableRef(val type: String, val key: String)
    data class Yield(val fn: (context: ScrapeMetadata) -> Either<DomainError, DataItemSource>)
}

data class ScrapingInfo(
    val title: Extractor?,
    val originalTitle: Extractor?,
    val sortTitle: Extractor?,
    val series: Extractor?,
    val rating: Extractor?,
    val releaseDate: Extractor?,
    val year: Extractor?,
    val top250: Extractor?,
    val votes: Extractor?,
    val outline: Extractor?,
    val plot: Extractor?,
    val tagline: Extractor?,
    val runtime: Extractor?,
    val posters: Extractor?,
    val fanart: Extractor?,
    val extrafanart: Extractor?,
    val mpaa: Extractor?,
    val id: Extractor?,
    val genres: Extractor?,
    val actors: Extractor?,
    val directors: Extractor?,
    val studio: Extractor?,
    val trailer: Extractor?,
    val tags: Extractor?,
    val table: Extractor?
)