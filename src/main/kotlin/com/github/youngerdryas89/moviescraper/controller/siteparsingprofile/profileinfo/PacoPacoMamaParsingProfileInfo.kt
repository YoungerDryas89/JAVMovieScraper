package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val PacoPacoMamaParsingProfileInfo = SpecificProfileInfo(
    name = "PacoPacoMama",
    languages = nonEmptyListOf(Language.JAPANESE, Language.ENGLISH),
    domainNames = mapOf(
        Language.JAPANESE to "pacopacomama.com",
        Language.ENGLISH to "en.pacopacomama.com"
    ),
    profileIconFilename = "PacoPacoMama.png",
    domainName = "pacopacomama.com"
)
