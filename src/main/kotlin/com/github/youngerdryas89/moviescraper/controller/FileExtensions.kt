package com.github.youngerdryas89.moviescraper.controller

import com.github.youngerdryas89.moviescraper.model.MovieFilenameFilter
import org.apache.commons.io.FilenameUtils
import java.io.File

fun String.replaceLast(toReplace: String, replacement: String): String {
    val pos = lastIndexOf(toReplace)
    return if (pos > -1) {
        substring(0, pos) + replacement + substring(pos + toReplace.length)
    } else {
        this
    }
}

//returns the movie file path without anything like CD1, Disc A, etc and also gets rid of the file extension
//Example: MyMovie ABC-123 CD1.avi returns MyMovie ABC-123
//Example2: MyMovie ABC-123.avi returns MyMovie ABC-123
fun File.getUnstackedMovieName(): String {
    var fileName = this.toString()
    fileName = fileName.replaceLast(this.name, FileUtilities.stripDiscNumber(FilenameUtils.removeExtension(this.name)))
    return fileName
}

fun File.getFileNameOfNfo(nfoNamedMovieDotNfo: Boolean): String {
    return if (nfoNamedMovieDotNfo) {
        this.path + File.separator + "movie.nfo"
    } else
        this.getTargetFilePath(".nfo")
}

fun File.getFileNameOfPoster(getNoMovieNameInImageFiles: Boolean): String {
    return if (getNoMovieNameInImageFiles) {
        if (this.isDirectory) {
            this.path + File.separator + "poster.jpg"
        } else {
            this.parent + File.separator + "poster.jpg"
        }
    } else
        this.getTargetFilePath("-poster.jpg")
}

fun File.getFileNameOfFolderJpg(): String {
    return if (this.isDirectory) {
        this.path + File.separator + "folder.jpg"
    } else
        this.parent + File.separator + "folder.jpg"
}

fun File.getFileNameOfExtraFanartFolderName(): String? {
    return when {
        this.isDirectory -> this.path
        this.isFile -> this.parent
        else -> null
    }
}

fun File.getFileNameOfTrailer(): String {
    for (extension in MovieFilenameFilter.acceptedMovieExtensions) {
        val potentialTrailer = this.tryToFindActualTrailerHelper(".$extension")
        if (potentialTrailer != null)
            return potentialTrailer
    }
    return this.getTargetFilePath("-trailer.mp4")
}

fun File.tryToFindActualTrailerHelper(extension: String): String? {
    val potentialPath = this.getTargetFilePath("-trailer$extension")
    val trailerCandidate = File(potentialPath)
    return if (trailerCandidate.exists()) potentialPath else null
}

fun File.getFileNameOfFanart(getNoMovieNameInImageFiles: Boolean): String {
    return if (getNoMovieNameInImageFiles) {
        if (this.isDirectory) {
            this.path + File.separator + "fanart.jpg"
        } else {
            this.parent + File.separator + "fanart.jpg"
        }
    } else
        this.getTargetFilePath("-fanart.jpg")
}

fun File.getTargetFilePath(extension: String): String {
    return if (!this.isDirectory) {
        this.getUnstackedMovieName() + extension
    } else {
        val directoryContents = this.listFiles { _, fileName -> fileName.endsWith(extension) }
        if (directoryContents?.isNotEmpty() == true) {
            directoryContents[0].path
        } else {
            val directoryContentsOfAllFiles = this.listFiles(MovieFilenameFilter())
            if (directoryContentsOfAllFiles?.isNotEmpty() == true) {
                for (currentFile in directoryContentsOfAllFiles) {
                    if (currentFile.isFile) {
                        return currentFile.getUnstackedMovieName() + extension
                    }
                }
            }
            File(this.absolutePath + File.separator + this.name + extension).path
        }
    }
}
