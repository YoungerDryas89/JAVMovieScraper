package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile;

import com.github.youngerdryas89.moviescraper.model.Movie;
import com.github.youngerdryas89.moviescraper.model.MovieFactory;
import javafx.util.Pair;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetermineMovie {
    final static Pattern FC2Pattern = Pattern.compile("(?i)(:?FC2-PPV)[-_\\s](?<id>(\\d{7}))");
    final static Pattern OnePondoPattern = Pattern.compile("(?i)(:?1Pondo[-_\\s]?)?(?<id>\\d{6}[_-]\\d{1,3}?(:?-1PON)?)");
    final static Pattern TokyoHotPattern = Pattern.compile("(?i)(:?Tokyo-?Hot)?[-_\\s\\S]?(?<productId>[nk]\\d{4})");
    final static Pattern CaribbeancomPattern = Pattern.compile("(?i)(?<id>(?<series>carib|caribbeancom(pr)?|caribbeancom premium)\\s?[-_\\s]\\s?(?<number>\\d{6}[_-]\\d{3}))");
    final static Pattern CaribbeancomPatternReverse = Pattern.compile("(?i)(?<id>(?<number>\\d{6}[_-]\\d{3})\\s?[-_\\s]\\s?(?<series>carib(pr)?|caribbeancom(pr)?|caribbeancom premium))");
    final static Pattern TenMusumePattern = Pattern.compile("(?i)(?<id>\\d{6}_\\d{2,3})(\\-|_)?10MU");
    final static Pattern avGeneralIdextract = Pattern.compile("(?i)(?<tag>(?:\\d{3,4})?[a-z]+|[a-z]{1,2}\\d+)[^a-z0-9_](?<num>\\d+)");
    final static Pattern kinten8gokuPattern = Pattern.compile("(?i)(?:Kin8tengoku|KIN8)[-_\\s](?<num>\\d+)");
    protected static DetermineMovie dproperties = new DetermineMovie();

    /**
     * Gets the ID number from the file and considers stripped out multipart file identifiers like CD1, CD2, etc
     * The ID number needs to be the last word in the filename or the next to the last word in the file name if the file name
     * ends with something like CD1 or Disc 1
     * So this filename "My Movie ABC-123 CD1" would return the id as ABC-123
     * This filename "My Movie ABC-123" would return the id as ABC-123
     *
     * @param file - file to find the ID tag from
     * @param firstWordOfFileIsID - if true, just uses the first word in the file (seperated by space) as the ID number
     * otherwise use the method described above
     * @return
     */
    public static String findIDTagFromFile(File file, boolean firstWordOfFileIsID) {
        // TODO: Need something better and more specific than this function
        String fileNameNoExtension;
        if (file.isDirectory()) {
            fileNameNoExtension = file.getName();
        } else
            fileNameNoExtension = FilenameUtils.removeExtension(file.getName());
        if (file.getPath().endsWith(".nfo")) {
            try {
                Movie movie = MovieFactory.createMovieFromNfo(file);
                return movie.getId().getId();
            } catch (IOException ex) {
                System.out.println("Cannot load this file as nfo. Try from filename");
            }

        }

        String id = null;
        Matcher match = CaribbeancomPattern.matcher(fileNameNoExtension);
        if(match.find()){
            assert (match.group("id") != null);
            assert (match.group("series") != null);
            assert (match.group("number") != null);
            if(match.group("series").contains("pr")){
                return "caribbeancompr-" + match.group("number");
            } else {
                return "caribbeancom-" + match.group("number");
            }
        }

        match = CaribbeancomPatternReverse.matcher(fileNameNoExtension);
        if(match.find()){
            assert (match.group("id") != null);
            assert (match.group("series") != null);
            assert (match.group("number") != null);
            if(match.group("series").contains("pr")){
                return "caribbeancompr" + match.group("number");
            } else {
                return "caribbeancom-" + match.group("number");
            }
        }


        match = FC2Pattern.matcher(fileNameNoExtension);
        if(match.find()){
            assert (match.group("id") != null);
            id = match.group("id");
            return "FC2-PPV-" + id;
        }

        match = kinten8gokuPattern.matcher(fileNameNoExtension);
        if(match.find()){
            id = "KIN8-" + match.group("num");
            return id;
        }

        match = OnePondoPattern.matcher(fileNameNoExtension);
        if(match.find()){
            assert (match.group("id") != null);
            id = match.group("id");
            return id;
        }

        match = TenMusumePattern.matcher(fileNameNoExtension);
        if(match.find()){
            assert (match.group("id") != null);
            id = match.group("id");
            return id;
        }

        match = TokyoHotPattern.matcher(fileNameNoExtension);
        if(match.find()){
            assert(match.group("productId") != null);
            id = match.group("productId");
            return id;
        }

var result = dproperties.determineIdFromTitle(fileNameNoExtension);
if(result != null){
return result.getKey() + "-" + result.getValue();
}

        match = avGeneralIdextract.matcher(fileNameNoExtension);
        if(match.find()){
            assert(match.group("tag") != null);
            assert(match.group("num") != null);

            id = match.group("tag") + "-" + match.group("num");
        }
        return id;
    }

    private record TagData(String tag, String studio, int code_length){}
    private final List<TagData> tags = new ArrayList<>(2574);
    public DetermineMovie(){
        try (BufferedReader bin = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("tags")))) {
            String line;
            for(int i = 0; i <tags.size(); i++){
                if((line = bin.readLine()) != null) {
                    var elems = line.split(";");
                    tags.add(new TagData(elems[0], elems[1], Integer.parseInt(elems[2])));
                }
            }
            tags.sort(new Comparator<TagData>() {
                @Override
                public int compare(TagData o1, TagData o2) {
                    return Integer.compare(o2.tag.length(), o1.tag.length());
                }
            });
        } catch (NullPointerException|IOException e) {
            System.err.println("Failed to load `tags` resource; Please check if it exists");
        }
    }

    public Pair<String, String> determineIdFromTitle(String title){
        String matchTagPattern = "(?i)(?<id>\\b$ID)[^a-z0-9_](?<num>\\d+)";
        String matchTag2 = "(?i)\\b$ID";
        for(var tag: this.tags){
            Pattern matchTag2Pattern = Pattern.compile(matchTag2.replaceFirst("\\$ID", tag.tag));
            if(matchTag2Pattern.matcher(title).find()){

                matchTagPattern = matchTagPattern.replaceFirst("\\$ID", tag.tag);
                Pattern matchTag = Pattern.compile(matchTagPattern);
                Matcher getTagMatch = matchTag.matcher(title);
                if(getTagMatch.find()){
                    return new Pair<String, String>(tag.tag, getTagMatch.group("num"));
                }

            }
        }
        return null;
    }

    public String getStudioById(String id){
        String studio = "";
        String matchIdWordBoundary = "\b%ID";
        for(var tag : this.tags){
            Pattern matchId = Pattern.compile(matchIdWordBoundary.replaceFirst("%ID", id));
            if(matchId.matcher(id).find()){
                studio = tag.studio;
            }
        }
        return studio;
    }


    public AVMovieProperties determinePropertiesFromString(String title){
        AVMovieProperties properties = new AVMovieProperties();
        var id = determineIdFromTitle(title);

        properties.ProductId = determineIdFromTitle(title);
        properties.Studio = getStudioById(properties.tag());
        return properties;
    }
}
