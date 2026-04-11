package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val AV123ParsingProfileInfo = SpecificProfileInfo(
    name = "123AV",
    languages = nonEmptyListOf(
        Language.JAPANESE,
        Language.ENGLISH,
        Language.CHINESE_TRADITIONAL,
        Language.KOREAN,
        Language.MALAYSIAN,
        Language.THAI,
        Language.VIETNAMESE,
        Language.INDONESIAN,
        Language.FILIPINO,
        Language.GERMAN,
        Language.FRENCH,
        Language.HINDI
    ),
    domainNames = mapOf(
        Language.JAPANESE to "/ja/",
        Language.ENGLISH to "/en/",
        Language.CHINESE_TRADITIONAL to "/zh/",
        Language.KOREAN to "/ko/",
        Language.MALAYSIAN to "/ms/",
        Language.THAI to "/th/",
        Language.VIETNAMESE to "/vi/",
        Language.INDONESIAN to "/id/",
        Language.FILIPINO to "/fil/",
        Language.GERMAN to "/de/",
        Language.FRENCH to "/fr/",
        Language.HINDI to "/hi/"
    ),
    profileIconFilename = "123AV.png",
    domainName = "123av.com"
)
