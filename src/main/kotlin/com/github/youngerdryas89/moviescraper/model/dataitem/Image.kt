package com.github.youngerdryas89.moviescraper.model.dataitem

abstract class Imaging {
    abstract val url: String
    abstract var dataItemSource: DataItemSource?

    data class ImageLink(override val url: String, override var dataItemSource: DataItemSource? = null) : Imaging()

}
