package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val AvEntertainmentParsingProfileInfo = SpecificProfileInfo(
    name = "AvEntertainment",
    languages = nonEmptyListOf(
        Language.JAPANESE,
        Language.ENGLISH
    ),
    domainNames = mapOf(
        Language.JAPANESE to "lang=2&culture=ja-JP",
        Language.ENGLISH to "lang=1&culture=en-US"
    ),
    profileIconFilename = "AvEntertainment.png",
    domainName = "aventertainments.com"
)
