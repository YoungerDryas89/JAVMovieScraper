package com.github.youngerdryas89.controller.siteparsingprofile;

import javafx.collections.transformation.SortedList;
import javafx.util.Pair;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetermineMovie {
    private class TagData{
       public String tag;
       public String studio;
       public Integer max_code_length, min_code_length;

       public TagData(String[] data){
           this.tag = data[0];
           this.studio = data[1];
           this.min_code_length = Integer.valueOf(data[2]);
           this.max_code_length = Integer.valueOf(data[3]);
       }
    }

    private List<TagData> tags = new ArrayList<>();
    private HashMap<String, List<String>> studios = new HashMap<>();
    public DetermineMovie(){
        try(
                InputStream is = getClass().getClassLoader().getResourceAsStream("tags");
                Scanner scanner = new Scanner(is);

        ){
            scanner.useDelimiter(System.lineSeparator());
            var tags_tmp = new HashMap<String, String>();
            while(scanner.hasNextLine()){


                var line = scanner.nextLine();
                if(line.isEmpty()) continue;

                var elems = line.split(";");
                var returnTag = new TagData(elems);
                tags.add(returnTag);

            }
            tags.sort(new Comparator<TagData>() {
                @Override
                public int compare(TagData o1, TagData o2) {
                    return Integer.compare(o2.tag.length(), o1.tag.length());
                }
            });
        }catch (IOException e){
            System.err.println(e.getMessage());
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
