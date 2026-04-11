package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val TenMusumeParsingProfileInfo = SpecificProfileInfo(
    name = "10Musume",
    languages = nonEmptyListOf(Language.JAPANESE, Language.ENGLISH),
    domainNames = mapOf(
        Language.JAPANESE to "10musume.com",
        Language.ENGLISH to "en.10musume.com"
    ),
    profileIconFilename = "10Musume.png",
    domainName = "10musume.com"
)
