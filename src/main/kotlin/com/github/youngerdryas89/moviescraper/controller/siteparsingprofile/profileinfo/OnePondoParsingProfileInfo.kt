package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val OnePondoParsingProfileInfo = SpecificProfileInfo(
    name = "OnePondo",
    languages = nonEmptyListOf(Language.JAPANESE, Language.ENGLISH),
    domainNames = mapOf(
        Language.JAPANESE to "1pondo.tv",
        Language.ENGLISH to "en.1pondo.tv"
    ),
    profileIconFilename = "1Pondo.png",
    domainName = "1pondo.tv"
)
