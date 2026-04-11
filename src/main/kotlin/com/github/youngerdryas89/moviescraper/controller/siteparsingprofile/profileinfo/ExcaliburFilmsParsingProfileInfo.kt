package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val ExcaliburFilmsParsingProfileInfo = SpecificProfileInfo(
    name = "ExcaliburFilms",
    languages = nonEmptyListOf(Language.ENGLISH),
    domainNames = mapOf(
        Language.ENGLISH to "excaliburfilms.com"
    ),
    profileIconFilename = "ExcaliburFilms.png",
    domainName = "excaliburfilms.com"
)
