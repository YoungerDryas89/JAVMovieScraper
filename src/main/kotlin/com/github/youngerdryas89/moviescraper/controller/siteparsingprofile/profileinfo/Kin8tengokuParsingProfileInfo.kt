package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val Kin8tengokuParsingProfileInfo = SpecificProfileInfo(
    name = "Kin8tengoku",
    languages = nonEmptyListOf(
        Language.JAPANESE,
        Language.ENGLISH
    ),
    domainNames = mapOf(
        Language.JAPANESE to "kin8tengoku.com",
        Language.ENGLISH to "en.kin8tengoku.com"
    ),
    profileIconFilename = "Kin8tengoku.png",
    domainName = "kin8tengoku.com"
)
