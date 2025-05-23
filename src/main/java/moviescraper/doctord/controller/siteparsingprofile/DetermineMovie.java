package moviescraper.doctord.controller.siteparsingprofile;

import javafx.util.Pair;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetermineMovie {
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
