package com.github.youngerdryas89.moviescraper.model.dataitem

import arrow.core.NonEmptyList


@JvmInline value class Name(val name: String)

sealed class Staff {
    abstract val name: Name
    abstract val thumbnailURL: String?

    data class Director(
        override val name: Name,
        override val thumbnailURL: String?
    ) : Staff()

    data class Actor(
        override val name: Name,
        override val thumbnailURL: String?,
        val role: String? = null
    ) : Staff()
}

typealias Actresses = NonEmptyList<Staff.Actor>
typealias Directors = NonEmptyList<Staff.Director>