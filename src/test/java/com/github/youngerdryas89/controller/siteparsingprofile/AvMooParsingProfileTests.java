package com.github.youngerdryas89.controller.siteparsingprofile;

import com.github.youngerdryas89.controller.siteparsingprofile.specific.AvMooParsingProfile;
import com.github.youngerdryas89.model.SearchResult;
import com.github.youngerdryas89.model.dataitem.*;
import com.github.youngerdryas89.model.dataitem.Runtime;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AvMooParsingProfileTests {
    public static File targetFile = new File("/mnt/Mercury/media/pictures/nsfw/straight/Videos/Japanese Adult Video/STARS/STARS-867.mp4");
    private final AvMooParsingProfile parser = new AvMooParsingProfile();
    private static AvMooParsingProfile profile;

    @BeforeClass
    public static void initialize() throws IOException{
        profile = new AvMooParsingProfile();
        SearchResult[] results = profile.getSearchResults(profile.createSearchString(targetFile));
        assertEquals(1, results.length);
        String url = results[0].getUrlPath();
        Document doc = SiteParsingProfile.downloadDocumentFromURLString(url);
        profile.setDocument(doc);
    }

    @BeforeAll
    public static void testSearchResults() throws IOException {
    }

    @Test
    public void testActors(){
        ArrayList<Actor> scrapeActors = profile.scrapeActors();
        assertEquals(1, scrapeActors.size());
    }

    @Test
    public void testTitle(){
        Title scrapeTitle = profile.scrapeTitle();
        assertEquals("サークルの飲み会で酔いつぶれて目が覚めたら先輩の巨乳カノジョ宅にいて…部屋着から見える胸チラ、マンチラに我慢ができず襲ってしまい朝まで巣ごもり浮気SEX MINAMO", scrapeTitle.getTitle());
    }

    @Test
    public void testGenres() {
        ArrayList<Genre> genres = profile.scrapeGenres();
        assertEquals(6, genres.size());
        assertEquals("Creampie", genres.get(0).getGenre());
        assertEquals("Big Tits", genres.get(1).getGenre());
    }

    @Test
    public void testReleaseDate(){
        ReleaseDate releaseDate = profile.scrapeReleaseDate();
        assertEquals("2023-08-08", releaseDate.getReleaseDate());
    }

    @Test
    public void testRuntime(){
        Runtime runtime = profile.scrapeRuntime();
        assertEquals("140", runtime.getRuntime());
    }

    @Test
    public void testPosters() throws IOException{
        Thumb[] posters = profile.scrapePosters(true);
        assertEquals(1, posters.length);
    }

    @Test
    public void testStudio(){
        Studio studio = profile.scrapeStudio();
        assertEquals("SOD Create", studio.getStudio());
    }
}
