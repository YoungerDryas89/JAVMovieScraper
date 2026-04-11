package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.profileinfo

import arrow.core.nonEmptyListOf
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SpecificProfileInfo

val MissAVParsingProfileInfo = SpecificProfileInfo(
    name = "MissAV",
    languages = nonEmptyListOf(
        Language.JAPANESE,
        Language.ENGLISH,
        Language.CHINESE_TRADITIONAL,
        Language.CHINESE_SIMPLIFIED,
        Language.KOREAN,
        Language.MALAYSIAN,
        Language.VIETNAMESE,
        Language.THAI,
        Language.INDONESIAN,
        Language.FRENCH,
        Language.GERMAN,
        Language.PORTUGESE
    ),
    domainNames = mapOf(
        Language.JAPANESE to "/ja/",
        Language.ENGLISH to "/en/",
        Language.CHINESE_TRADITIONAL to "",
        Language.CHINESE_SIMPLIFIED to "/cn/",
        Language.KOREAN to "/ko/",
        Language.MALAYSIAN to "/ms/",
        Language.VIETNAMESE to "/vi/",
        Language.THAI to "/th/",
        Language.INDONESIAN to "/id/",
        Language.FRENCH to "/fr/",
        Language.GERMAN to "/de/",
        Language.PORTUGESE to "/pt/"
    ),
    profileIconFilename = "MissAV.png",
    domainName = "missav.com"
)
