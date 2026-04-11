package com.github.youngerdryas89.moviescraper.controller;

import com.github.youngerdryas89.moviescraper.model.MovieFilenameFilter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FilenameFilter;

public class FileUtilities {
    static String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos) + replacement + string.substring(pos + toReplace.length(), string.length());
        } else {
            return string;
        }
    }

    //returns the movie file path without anything like CD1, Disc A, etc and also gets rid of the file extension
    //Example: MyMovie ABC-123 CD1.avi returns MyMovie ABC-123
    //Example2: MyMovie ABC-123.avi returns MyMovie ABC-123
    static String getUnstackedMovieName(File file) {
        String fileName = file.toString();
        fileName = replaceLast(fileName, file.getName(), stripDiscNumber(FilenameUtils.removeExtension(file.getName())));
        return fileName;
    }

    static String getFileNameOfNfo(File file, Boolean nfoNamedMovieDotNfo) {
        if (nfoNamedMovieDotNfo) {
            return file.getPath() + File.separator + "movie.nfo";
        } else
            return getTargetFilePath(file, ".nfo");
    }

    static String getFileNameOfPoster(File file, boolean getNoMovieNameInImageFiles) {
        if (getNoMovieNameInImageFiles) {
            if (file.isDirectory()) {
                return file.getPath() + File.separator + "poster.jpg";
            } else {
                return file.getParent() + File.separator + "poster.jpg";
            }
        } else
            return getTargetFilePath(file, "-poster.jpg");
    }

    static String getFileNameOfFolderJpg(File selectedValue) {

        if (selectedValue.isDirectory()) {
            return selectedValue.getPath() + File.separator + "folder.jpg";
        } else
            return selectedValue.getParent() + File.separator + "folder.jpg";
    }

    public static String getFileNameOfExtraFanartFolderName(File selectedValue) {
        if (selectedValue != null && selectedValue.isDirectory()) {
            return selectedValue.getPath();
        } else if (selectedValue != null && selectedValue.isFile()) {
            return selectedValue.getParent();
        } else
            return null;
    }

    public static String getFileNameOfTrailer(File selectedValue) {
        //sometimes the trailer has a different extension
        //than the movie so we will try to brute force a find by trying all movie name extensions
        for (String extension : MovieFilenameFilter.acceptedMovieExtensions) {
            String potentialTrailer = tryToFindActualTrailerHelper(selectedValue, "." + extension);
            if (potentialTrailer != null)
                return potentialTrailer;
        }
        return getTargetFilePath(selectedValue, "-trailer.mp4");
    }

    /**
     * Checks for the given file a trailer file exists for it for the given file name extension
     *
     * @param selectedValue - base file name of movie or nfo
     * @param extension     - the file name extension we are checking
     * @return - the path to the file if it found the trailer, otherwise null
     */
    static String tryToFindActualTrailerHelper(File selectedValue, String extension) {
        String potentialPath = getTargetFilePath(selectedValue, "-trailer" + extension);
        File trailerCandidate = new File(potentialPath);
        if (trailerCandidate.exists())
            return potentialPath;
        return null;
    }

    public static String getFileNameOfFanart(File file, boolean getNoMovieNameInImageFiles) {
        if (getNoMovieNameInImageFiles) {
            if (file.isDirectory()) {
                return file.getPath() + File.separator + "fanart.jpg";
            } else {
                return file.getParent() + File.separator + "fanart.jpg";
            }
        } else
            return getTargetFilePath(file, "-fanart.jpg");
    }

    static String getTargetFilePath(File file, String extension) {
        if (!file.isDirectory()) {
            String nfoName = FileExtensionsKt.getUnstackedMovieName(file) + extension;
            return nfoName;
        }
        //look in the directory for an nfo file, otherwise we will make one based on the last word (JAVID of the folder name)
        else {
            final String extensionFromParameter = extension;
            //getting the nfo files in this directory, if any
            File[] directoryContents = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File directory, String fileName) {
                    return fileName.endsWith(extensionFromParameter);
                }
            });
            //if there are 1 or more files, it's not really in spec, so just return the first one
            if (directoryContents.length > 0) {
                return directoryContents[0].getPath();
            } else {
                //no file found in directory, so we will be setting the target to create one in that directory
                File[] directoryContentsOfAllFiles = file.listFiles(new MovieFilenameFilter());
                if (directoryContentsOfAllFiles.length > 0) {
                    //check to see if there's at least one file in the directory that is a movie and go by naming based off the first file found
                    for (File currentFile : directoryContentsOfAllFiles) {
                        if (currentFile.isFile()) {
                            String targetFileName = getUnstackedMovieName(currentFile) + extension;
                            //System.out.println("returning " + targetFileName);
                            return targetFileName;
                        }
                    }
                }
                //Use the folder name as the basis for the filename created
                return new File(file.getAbsolutePath() + File.separator + file.getName() + extension).getPath();
            }
        }
    }

    public static String stripDiscNumber(String fileNameNoExtension) {
		//replace <cd/dvd/part/pt/disk/disc/d> <0-N>  (case insensitive) with empty
		String discNumberStripped = fileNameNoExtension.replaceAll("(?i)[ _.]+(?:cd|dvd|p(?:ar)?t|dis[ck]|d)[ _.]*[0-9]+$", "");
		//replace <cd/dvd/part/pt/disk/disc/d> <a-d> (case insensitive) with empty
		discNumberStripped = discNumberStripped.replaceAll("(?i)[ _.]+(?:cd|dvd|p(?:ar)?t|dis[ck]|d)[ _.]*[a-d]$", "");
		return discNumberStripped.trim();
	}
}
