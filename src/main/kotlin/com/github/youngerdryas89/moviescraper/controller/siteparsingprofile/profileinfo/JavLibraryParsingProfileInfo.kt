package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val JavLibraryParsingProfileInfo = SpecificProfileInfo(
    name = "JavLibrary",
    languages = nonEmptyListOf(
        Language.JAPANESE,
        Language.ENGLISH,
        Language.CHINESE_TRADITIONAL,
        Language.CHINESE_SIMPLIFIED
    ),
    domainNames = mapOf(
        Language.JAPANESE to "/ja/",
        Language.ENGLISH to "/en/",
        Language.CHINESE_TRADITIONAL to "/tw/",
        Language.CHINESE_SIMPLIFIED to "/cn/"
    ),
    profileIconFilename = "JavLibrary.png",
    domainName = "javlibrary.com"
)
