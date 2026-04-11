package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val  CaribbeancomPremiumParsingProfileInfo: SpecificProfileInfo = SpecificProfileInfo(
    name = "CaribbeancomPremium",
    languages = nonEmptyListOf(
        Language.JAPANESE,
        Language.ENGLISH
    ),
    domainName = TODO(),
    profileIconFilename = TODO(),
    domainNames = TODO()
)
