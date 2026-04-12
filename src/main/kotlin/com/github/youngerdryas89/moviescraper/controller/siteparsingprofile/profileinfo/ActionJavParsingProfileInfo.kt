package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.getUnstackedMovieName
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.DerivedURL
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.DetermineMovie
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SearchStringInput
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.scrapers.ScraperProfile
import com.github.youngerdryas89.moviescraper.model.SearchResult
import com.github.youngerdryas89.moviescraper.model.dataitem.*
import com.github.youngerdryas89.moviescraper.scraper.UserAgent
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File

val ActionJavParsingProfile_ = ScraperProfile(
    scrapeTitle = { context ->
        val titleElement: Element? = context.document?.selectFirst("table.p-2 > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2) > span:nth-child(1)")
        Titling.Title(title = titleElement?.text() ?: "")
    },
    scrapeOriginalTitle = { _ -> null},
    scrapeSortTitle = { _ -> null},
    scrapeSet = { _ -> null},
    scrapeRating = { _ -> null},
    scrapeReleaseDate = { _ -> null},
    scrapeYear = { _ -> null},
    scrapeTop250 = { _ -> null},
    scrapeVotes = { _ -> null},
    scrapeOutline = { _ -> null},
    scrapePlot = { _ -> null},
    scrapeTagline = { _ -> null},
    scrapeRuntime = { context ->
        val runtime = context.document?.selectFirst("td:containsOwn(Runtime) + td")
        if (runtime != null) {
            Runtime(runtime.text().split(" ")[0])
        } else {
            null
        }
    },
    scrapePosters = { context ->
        val posterElement = context.document?.selectFirst("div.bg-white:nth-child(2) > div:nth-child(1) > center:nth-child(1) > a:nth-child(1) > img:nth-child(1)")
        if(posterElement != null){
            listOf(posterElement.attr("src")).map {  url -> Imaging.ImageLink(url) }
        }
        emptyList()
    },
    scrapeFanart = { _ -> emptyList()},
    scrapeExtraFanart = { _ -> emptyList()},
    scrapeMPAA = { _ -> MPAARating.RATING_XXX},
    scrapeID = { context ->
        val idElement: Element? = context.document?.selectFirst("table.p-2 > tbody:nth-child(1) > tr:nth-child(4) > td:nth-child(2) > span:nth-child(1)")
        if (idElement != null) {
            val id = idElement.text().split(" ")[0]
            ID(id)
        }
        null
    },
    scrapeGenres = { _ ->
        emptyList()
    },
    scrapeActors = { context ->

        context.document?.select("table.p-2 > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(2) > span:nth-child(1) > a:nth-child(1)")
            ?.map { elem ->
                Staff.Actor(Name(elem.text()), null, null)
            } ?: emptyList()

    },
    scrapeDirectors = { _ -> emptyList()},
    scrapeStudio = { context ->

        val studioElement: Element? = context.document?.selectFirst("table.p-2 > tbody:nth-child(1) > tr:nth-child(3) > td:nth-child(2) > span:nth-child(1)")
        if (studioElement != null) {
            Studio(studioElement.text())
        } else {
            null
        }
    },
    scrapeTrailer = { _ -> null},
    scrapeTags = { _ -> emptyList()}
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
    cleanseFilename = { fn -> fn.getUnstackedMovieName()},
    fetchSearchResults = ::getSearchResults,
    fetchDirectPage = { url -> Jsoup.connect(url).userAgent(UserAgent.getRandomUserAgent()).response() },
    createURLFromInput = ::createURLFromInput_,
    tryInferIdentity = { id ->
        // TODO: Need something that doesn't need re-conversion to File
        val id_ = DetermineMovie.findIDTagFromFile(File(id), false)
        SearchStringInput.Id(id_)
    },
    scraperProfile = ActionJavParsingProfile_
)
