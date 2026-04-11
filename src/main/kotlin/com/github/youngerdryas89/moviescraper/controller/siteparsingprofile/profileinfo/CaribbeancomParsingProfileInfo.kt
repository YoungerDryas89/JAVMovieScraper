package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val CaribbeancomParsingProfileInfo = SpecificProfileInfo(
    name = "Caribbeancom",
    languages = nonEmptyListOf(
        Language.JAPANESE,
        Language.ENGLISH
    ),
    domainNames = mapOf(
        Language.JAPANESE to "caribbeancom.com",
        Language.ENGLISH to "en.caribbeancom.com"
    ),
    profileIconFilename = "Caribbeancom.png",
    domainName = "caribbeancom.com"
)
