package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile

import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language

data class SpecificProfileSettings(
    val art: ArtSettings,
    val naming: NamingSettings,
    val scraper: ScraperSettings,
//    val network: NetworkSettings = NetworkSettings()
)

data class ArtSettings(
    val writeArt: Boolean = true,
    val overwriteExisting: Boolean = true,
    val downloadActorImages: Boolean = true,
    val extraFanartEnabled: Boolean = true,
    val createFolderJpg: Boolean = false,
    val namingScheme: ImageNaming = ImageNaming.INCLUDE_MOVIE_NAME
)

enum class ImageNaming {
    INCLUDE_MOVIE_NAME, // movie-fanart.jpg
    GENERIC             // fanart.jpg (previously noMovieNameInImageFiles = true)
}

data class NamingSettings(
    val renameFile: Boolean = false,
    val filenameSanitizer: String = "[\\\\/:*?\"<>|\\r\\n]|[ ]+$|(?<=[^.])[.]+$|(?<=.{250})(.+)(?=[.]\\p{Alnum}{3}$)",
    val moviePattern: String = "<TITLE> [<ACTORS>] (<YEAR>) [<ID>]",
    val folderPattern: String = "<BASEDIRECTORY><PATHSEPERATOR>",
    val nfoNaming: NfoNaming = NfoNaming.FILENAME_NFO
)

enum class NfoNaming {
    MOVIE_NFO,   // movie.nfo
    FILENAME_NFO // <filename>.nfo
}

data class ScraperSettings(
    val selectionMode: SelectionMode = SelectionMode.AUTOMATIC,
    val promptForUrl: Boolean = false,
    val useFilenameAsTitle: Boolean = false,
    val appendIdToStartOfTitle: Boolean = false,
    val preferredLanguage: Language
)

enum class SelectionMode {
    MANUAL, AUTOMATIC
}

enum class ScraperState {
    ACTIVE,
    SILENT,
    DISABLED
}
