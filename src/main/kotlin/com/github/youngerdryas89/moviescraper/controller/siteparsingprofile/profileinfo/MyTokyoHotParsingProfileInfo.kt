package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val MyTokyoHotParsingProfileInfo = SpecificProfileInfo(
    name = "MyTokyoHot",
    languages = nonEmptyListOf(
        Language.JAPANESE,
        Language.ENGLISH,
        Language.CHINESE_TRADITIONAL
    ),
    domainNames = mapOf(
        Language.JAPANESE to "lang=ja",
        Language.ENGLISH to "lang=en",
        Language.CHINESE_TRADITIONAL to "lang=zh-TW"
    ),
    profileIconFilename = "MyTokyoHot.png",
    domainName = "www.tokyo-hot.com"
)
