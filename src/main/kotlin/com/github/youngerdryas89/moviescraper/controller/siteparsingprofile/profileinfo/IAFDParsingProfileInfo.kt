package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val IAFDParsingProfileInfo = SpecificProfileInfo(
    name = "IAFD",
    languages = nonEmptyListOf(Language.ENGLISH),
    domainNames = mapOf(
        Language.ENGLISH to "iafd.com"
    ),
    profileIconFilename = "IAFD.png",
    domainName = "iafd.com"
)
