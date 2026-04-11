package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val Data18MovieParsingProfileInfo = SpecificProfileInfo(
    name = "Data18Movie",
    languages = nonEmptyListOf(Language.ENGLISH),
    domainNames = mapOf(
        Language.ENGLISH to "data18.com"
    ),
    profileIconFilename = "Data18.png",
    domainName = "data18.com"
)
