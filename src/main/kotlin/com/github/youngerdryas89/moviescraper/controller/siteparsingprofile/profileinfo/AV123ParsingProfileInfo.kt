package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.Either
import arrow.core.Either.Companion.catch
import arrow.core.flatMap
import arrow.core.nonEmptyListOf
import arrow.core.raise.catch
import arrow.core.raise.context.bind
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import com.github.youngerdryas89.moviescraper.controller.getUnstackedMovieName
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.DerivedURL
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SearchStringInput
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.scrapers.DomainError
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.AV123ParsingProfile
import com.github.youngerdryas89.moviescraper.model.ScrapeMetadata
import com.github.youngerdryas89.moviescraper.scraper.UserAgent
import org.jsoup.Jsoup


fun createURL(input: SearchStringInput, language: Language) : DerivedURL =
    DerivedURL.DirectURL(AV123ParsingProfileInfo.domainName + AV123ParsingProfileInfo.domainNames[language] + input.attribute)

fun extractTableData(context: ScrapeMetadata, attribute: String) : Either<DomainError, Map<String, String>> =
    either {
        val message = catch{ context.document?.expectFirst(".message")?.text()!! }
        ensure(message.isLeft()) { DomainError.ScrapingError("The selected title could not be found.")}
        val table = catch{ context.document?.expectFirst(".content .detail-item")?.children()!!}
        ensure(table.) { DomainError.Nothing }
        val pairs = table.

        mapof

    }



val AV123ParsingProfileInfo = SpecificProfileInfo(
    name = "123AV",
    languages = nonEmptyListOf(
        Language.JAPANESE,
        Language.ENGLISH,
        Language.CHINESE_TRADITIONAL,
        Language.KOREAN,
        Language.MALAYSIAN,
        Language.THAI,
        Language.VIETNAMESE,
        Language.INDONESIAN,
        Language.FILIPINO,
        Language.GERMAN,
        Language.FRENCH,
        Language.HINDI
    ),
    domainNames = mapOf(
        Language.JAPANESE to "/ja/",
        Language.ENGLISH to "/en/",
        Language.CHINESE_TRADITIONAL to "/zh/",
        Language.KOREAN to "/ko/",
        Language.MALAYSIAN to "/ms/",
        Language.THAI to "/th/",
        Language.VIETNAMESE to "/vi/",
        Language.INDONESIAN to "/id/",
        Language.FILIPINO to "/fil/",
        Language.GERMAN to "/de/",
        Language.FRENCH to "/fr/",
        Language.HINDI to "/hi/"
    ),
    profileIconFilename = "123AV.png",
    domainName = "123av.com",
    cleanseFilename = { fn  -> fn.getUnstackedMovieName() },
    fetchSearchResults = throw NotImplementedError(),
    fetchDirectPage = { url -> Jsoup.connect(url).userAgent(UserAgent.getRandomUserAgent()).response() },
    createURLFromInput = ::createURL,
    scraperProfile = TODO()
)
