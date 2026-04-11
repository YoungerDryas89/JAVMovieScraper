package com.github.youngerdryas89.moviescraper.controller.languagetranslation

enum class Language {
    ENGLISH, JAPANESE, CHINESE_SIMPLIFIED, CHINESE_TRADITIONAL, KOREAN, FRENCH, GERMAN, HINDI, MALAYSIAN, THAI, VIETNAMESE, INDONESIAN, FILIPINO, TAIWANESE, PORTUGESE
}

fun nativeText(language: Language) : String {
    return when (language) {
        Language.ENGLISH -> "English"
        Language.JAPANESE -> "日本語"
        Language.CHINESE_TRADITIONAL -> "繁體中文"
        Language.CHINESE_SIMPLIFIED -> "简体中文"
        Language.KOREAN -> "한국어"
        Language.FRENCH -> "Français"
        Language.GERMAN -> "Deutsch"
        Language.HINDI -> "हिन्दी"
        Language.MALAYSIAN -> "Bahasa Melayu"
        Language.THAI -> "ไทย"
        Language.VIETNAMESE -> "Tiếng Việt"
        Language.INDONESIAN -> "Bahasa Indonesia"
        Language.FILIPINO -> "Filipino"
        Language.TAIWANESE -> "繁體中文"
        Language.PORTUGESE -> "Português"
    }
}