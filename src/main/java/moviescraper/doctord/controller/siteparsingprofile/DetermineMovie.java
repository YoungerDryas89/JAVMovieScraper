package moviescraper.doctord.controller.siteparsingprofile;

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
        String matchTagPattern = "(?i)(?<id>$ID)\\s?[^a-zA-Z0-9]\\s?(?<num>[a-zA-z0-9]{$MIN,$MAX})";
        String matchIdWordBoundary = "(?i)\b%ID\b";
        String pos = "(?i)%ID(?=[_-\\s])(?<num>[0-9a-z]%LIMITER";
        for(var tag: this.tags){
            if(title.toLowerCase().contains(tag.tag)){
                int index = title.toLowerCase().indexOf(tag.tag);
                if(index > 0){
                    char last_char = title.charAt(index - 1);
                    if(Character.isAlphabetic(last_char) || Character.isDigit(last_char))
                        break;
                }


                matchTagPattern = matchTagPattern.replaceFirst("\\$ID", tag.tag.toUpperCase()).replaceFirst("\\$MIN", String.valueOf(tag.min_code_length)).replaceFirst("\\$MAX", String.valueOf(tag.max_code_length));
                Pattern matchTag = Pattern.compile(matchTagPattern);
                Matcher getTagMatch = matchTag.matcher(title.toLowerCase());
                if(getTagMatch.find()){
                    var idMap = getTagMatch.namedGroups();
                    String assigned_tag = null, number = null;
                    if(idMap.containsKey("id")){
                        assigned_tag = getTagMatch.group("id");
                    }

                    if(idMap.containsKey("num")){
                        number = getTagMatch.group("num");
                    }

                    return new Pair<String, String>(assigned_tag, number);
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
