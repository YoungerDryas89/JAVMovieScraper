package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val SquarePlusParsingProfileInfo = SpecificProfileInfo(
    name = "SquarePlus",
    languages = nonEmptyListOf(
        Language.ENGLISH
    ),
    domainNames = mapOf(
        Language.ENGLISH to "squareplus.com"
    ),
    profileIconFilename = "SquarePlus.png",
    domainName = "squareplus.com"
)
