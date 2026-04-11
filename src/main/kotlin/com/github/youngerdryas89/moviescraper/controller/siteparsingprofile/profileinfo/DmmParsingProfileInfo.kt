package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val DmmParsingProfileInfo = SpecificProfileInfo(
    name = "DMM.co.jp",
    languages = nonEmptyListOf(Language.JAPANESE),
    domainNames = mapOf(
        Language.JAPANESE to "dmm.co.jp"
    ),
    profileIconFilename = "DMM.png",
    domainName = "dmm.co.jp"
)
