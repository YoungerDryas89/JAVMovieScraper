package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile

import arrow.core.NonEmptyList
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.model.SearchResult
import org.jsoup.Connection
import org.jsoup.nodes.Document
import java.io.File


sealed class SearchStringInput {
    abstract val attribute: String

    // This is the inferredId
    data class Id(override val attribute: String) : SearchStringInput()
    data class OverridenId(override val attribute: String) : SearchStringInput()
    // TODO: Need a data class for specific attributes. E.g title
}

// TODO: This needs a better name
sealed class DerivedURL {
    abstract val url: String
    data class DirectURL(override val url: String) : DerivedURL()
    data class SearchURL(override val url: String) : DerivedURL()
}

data class SpecificProfileInfo(
    val name : String,
    val domainName : String,
    val profileIconFilename: String?,
    val languages : NonEmptyList<Language>,
    val domainNames: Map<Language, String>,
    val cleanseFilename: ((File) -> String)?,
    val tryInferIdentity: ((String) -> SearchStringInput),
    val fetchSearchResults: ((String) -> List<SearchResult>),
    val fetchDirectPage: ((String) -> Connection.Response)?,
    val createURLFromInput: ((SearchStringInput, Language) -> DerivedURL)?
)
