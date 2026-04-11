package com.github.youngerdryas89.moviescraper.model.dataitem

sealed class Titling {
    abstract val title: String

    data class Title(
        override val title: String
    ) : Titling()

    data class OriginalTitle(
        override val title: String
    ) : Titling()

    data class SortTitle(
        override val title: String
    ) : Titling()
}

val BLANK_TITLE = Titling.Title("")
val BLANK_ORIGINAL_TITLE = Titling.OriginalTitle("")
val BLANK_SORT_TITLE = Titling.SortTitle("")