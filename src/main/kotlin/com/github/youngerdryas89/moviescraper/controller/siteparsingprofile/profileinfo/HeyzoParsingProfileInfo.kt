package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val HeyzoParsingProfileInfo = SpecificProfileInfo(
    name = "Heyzo",
    languages = nonEmptyListOf(Language.JAPANESE, Language.ENGLISH),
    domainNames = mapOf(
        Language.JAPANESE to "www.heyzo.com",
        Language.ENGLISH to "en.heyzo.com"
    ),
    profileIconFilename = "Heyzo.png",
    domainName = "heyzo.com"
)
