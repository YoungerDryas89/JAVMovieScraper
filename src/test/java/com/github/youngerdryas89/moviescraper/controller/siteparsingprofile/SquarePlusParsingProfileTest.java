package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile;

import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.SquarePlusParsingProfile;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;


public class SquarePlusParsingProfileTest {
    static String fn = "PPPE-023";
    private static SquarePlusParsingProfile profile;

    @BeforeClass
    public static void initialize(){
        profile = new SquarePlusParsingProfile();
        var searchString = profile.createSearchStringFromId(fn);
        try {
            var document = SiteParsingProfile.downloadDocumentFromURLString(profile.getSearchResults(searchString)[0].getUrlPath());
            profile.setDocument(document);
            profile.prepareData();
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testId(){
        var scrapedId = profile.scrapeID();
        assertEquals("Wrong Id", "PPPE-023", scrapedId.getId());
    }

    @Test
    public void testTitle() {
        var scrapedTitle = profile.scrapeTitle();
        assertEquals("Wrong title", "THE LAST WORK - Hitomi", scrapedTitle.getTitle());
    }

    @Test
    public void testReleaseDate(){
        var date = profile.scrapeReleaseDate();
        assertEquals("Wrong release date!", "2022-04-22", date.getReleaseDate());
    }
}
