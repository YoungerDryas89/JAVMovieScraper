package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.DerivedURL
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.DetermineMovie
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SearchStringInput
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo
import com.github.youngerdryas89.moviescraper.model.SearchResult
import com.github.youngerdryas89.moviescraper.scraper.UserAgent
import org.jsoup.Jsoup
import java.io.File

val ActionJavParsingProfileInfo = SpecificProfileInfo(
    name = "ActionJav",
    languages = nonEmptyListOf(
        Language.ENGLISH
    ),
    domainNames = mapOf(
        Language.ENGLISH to "actionjav.com"
    ),
    profileIconFilename = "ActionJavIcon.png",
    domainName = "actionjav.com",
    cleanseFilename = TODO(),
    fetchSearchResults = ::getSearchResults,
    fetchDirectPage = { url -> Jsoup.connect(url).userAgent(UserAgent.getRandomUserAgent()).response() },
    createURLFromInput = ::createURLFromInput_,
    tryInferIdentity = { id ->
        // TODO: Need something that doesn't need re-conversion to File
        val id_ = DetermineMovie.findIDTagFromFile(File(id), false)
        SearchStringInput.Id(id_)
    }
)


fun createURLFromInput_(input: SearchStringInput, language: Language): DerivedURL {
    return DerivedURL.SearchURL("https://" + ActionJavParsingProfileInfo.domainName + "/?view=search&item=" + input.attribute)
}

// TODO: Need a way to handle errors
fun getSearchResults(url: String): List<SearchResult> {
    val doc = Jsoup.connect(url).userAgent(UserAgent.getRandomUserAgent()).get()
    val elems = doc.select("div.card")
    // The map function creates the list, we just need to return it.
    return elems.map { e ->
        // Use selectFirst for clarity and safety.
        val urlElement = e.selectFirst("a")
        val title = e.selectFirst(".movie-list-title")?.text()
        SearchResult(urlElement?.attr("href"), title)
    }
}