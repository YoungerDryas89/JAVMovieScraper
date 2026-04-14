package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.Either.Companion.catch
import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.github.youngerdryas89.moviescraper.controller.getUnstackedMovieName
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.DerivedURL
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.DetermineMovie
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SearchStringInput
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.scrapers.DomainError
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.scrapers.ScraperProfile
import com.github.youngerdryas89.moviescraper.model.SearchResult
import com.github.youngerdryas89.moviescraper.model.dataitem.*
import com.github.youngerdryas89.moviescraper.scraper.UserAgent
import org.jsoup.Jsoup
import java.io.File

val ActionJavParsingProfile_ = ScraperProfile(
    scrapeTitle = { context ->
        either { // Start an 'either' block to use 'ensure' and 'bind'
            val titleText = catch {
                context.document?.selectFirst("table.p-2 > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2) > span:nth-child(1)")
                    ?.text()!!
            }.mapLeft { DomainError.ScrapingError("CSS query matching failed when trying to scrape title") }.bind()

            ensure(titleText.isNotEmpty()) { DomainError.Nothing } // If titleText is empty, raise DomainError.Nothing
            Titling.Title(titleText) // If we reach here, titleText is valid, return it
        }
    },
    scrapeRuntime = { context ->
        either {
            val runtimeText = catch {
                context.document?.expectFirst("td:containsOwn(Runtime) + td")
                    ?.text()
                    ?.split(" ")[0]!!
            }.mapLeft { DomainError.ScrapingError("CSS query matching failed when trying to scrape runtime") }.bind()

            ensure(runtimeText.isNotEmpty()) { DomainError.Nothing }
            Runtime(runtimeText)
        }
    },
    scrapePosters = { context ->
        either {
            val link = catch {
                context.document?.expectFirst("div.bg-white:nth-child(2) > div:nth-child(1) > center:nth-child(1) > a:nth-child(1) > img:nth-child(1)")
                    ?.attr("src")!!
            }.mapLeft { DomainError.ScrapingError("CSS query matching failed when trying to scrape posters") }.bind()

            ensure(link.isNotBlank()) { DomainError.Nothing }
            nonEmptyListOf(link)
        }
    },
    scrapeID = { context ->
        either {
            val idString = catch {
                context.document?.selectFirst("table.p-2 > tbody:nth-child(1) > tr:nth-child(4) > td:nth-child(2) > span:nth-child(1)")
                    ?.text()
                    ?.split(" ")[0]!!
            }.mapLeft { DomainError.ScrapingError("CSS query matching failed when trying to scrape ID") }.bind()

            ensure(idString.isNotEmpty()) { DomainError.Nothing }
            ID(idString)
        }
    },
    scrapeActors = { context ->
        either {
            val actorElements = catch {
                context.document?.select("table.p-2 > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(2) > span:nth-child(1) > a:nth-child(1)")!!
            }.mapLeft { DomainError.ScrapingError("CSS query matching failed when trying to scrape actors") }.bind()

            ensure(actorElements.isNotEmpty()) { DomainError.Nothing }

            Actresses(actorElements.map { elem -> Staff.Actor(Name(elem.text()), null, null) })
        }
    },
    scrapeStudio = { context ->
        either {
            val studioName = catch {
                context.document?.selectFirst("table.p-2 > tbody:nth-child(1) > tr:nth-child(3) > td:nth-child(2) > span:nth-child(1)")
                    ?.text()!!
            }.mapLeft { DomainError.ScrapingError("CSS query matching failed when trying to scrape studio") }.bind()

            ensure(studioName.isNotEmpty()) { DomainError.Nothing }
            Studio(studioName)
        }
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
    scraperProfile = ActionJavParsingProfile_
)
