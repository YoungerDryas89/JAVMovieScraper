package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile;

import javafx.util.Pair;

public class AVMovieProperties {
    public String Filename;
    public String Title;
    public String Studio;
    public String Series;
    public String Label;
    public String Id;
    public Pair<String, String> ProductId;

    public String tag(){
        return ProductId != null? ProductId.getKey() : "";
    }

    public String getId(){
        return Id;
    }
}
