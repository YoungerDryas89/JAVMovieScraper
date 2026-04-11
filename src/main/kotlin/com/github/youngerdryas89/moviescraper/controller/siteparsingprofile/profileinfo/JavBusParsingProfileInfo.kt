package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

// TODO: Need to get the language pieces of the URL
val JavBusParsingProfileInfo = SpecificProfileInfo(
    name = "JavBus",
    languages = nonEmptyListOf(Language.CHINESE_TRADITIONAL, Language.ENGLISH),
    domainNames = mapOf(
        Language.CHINESE_TRADITIONAL to "javbus.com",
        Language.ENGLISH to "javbus.com"
    ),
    profileIconFilename = "JavBus.png",
    domainName = "javbus.com"
)
