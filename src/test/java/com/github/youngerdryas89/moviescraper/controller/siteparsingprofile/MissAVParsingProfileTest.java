package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MissAVParsingProfileTest {
    static com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.MissAVParsingProfile parser;

    @BeforeClass
    public static void initialize() throws IOException {
        parser = new com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.MissAVParsingProfile();
        var result = parser.getSearchResults(parser.createSearchStringFromId("DANDY-680"));
        var document = SiteParsingProfile.downloadDocumentFromURLString(result[0].getUrlPath());
        parser.setDocument(document);
        parser.prepareData();
    }

    @Test
    public void title(){
        var title = parser.scrapeTitle();
        assertEquals("DANDY-680 Mother-in-law who has too strong sexual desire to have sex many times while there is no father does not refuse raw insertion of unequaled son", title.getTitle());
    }

    @Test
    public void originalTitle(){
        var ogTitle = parser.scrapeOriginalTitle();
        assertEquals("父親がいない間に何度もセックスしたがる性欲が強すぎる義母は絶倫息子の生挿入も拒まない", ogTitle.getOriginalTitle());
    }

    @Test
    public void series(){
        var series = parser.scrapeSet();
        assertEquals("○○がいない間に何度もセックスしたがる", series.getSet());
    }

    @Test
    public void releaseDate(){
        var date = parser.scrapeReleaseDate();
        assertEquals("2019-09-12", date.getReleaseDate());
    }

    @Test
    public void plot(){
        var plot = parser.scrapePlot();
        assertEquals("The footage taken when the three family members went on a trip showed the forbidden relationship between their son and mother-in-law. A mother with fair skin, soft skin, and beautiful breasts gently receives her son's hot sperm with her face and co ○ (10 ejaculation?) A mother who gently caresses her son Ji ○ po with her mouth and hands ... !! Please see the vivid images of such a mother and son.",
                plot.getPlot());
    }

    /*@Test
    public void duration(){
        var duration = parser.scrapeRuntime();
        assertEquals("176", duration.getRuntime());
    }*/

    @Test
    public void id(){
        var id = parser.scrapeID();
        assertEquals("DANDY-680", id.getId());
    }

    @Test
    public void genres(){
        var genres = parser.scrapeGenres();
        assertEquals(8, genres.size());
    }

    @Test
    public void director(){
        var director = parser.scrapeDirectors();
        assertEquals("マジカルキクタソ", director.getFirst().getName());
    }

    @Test
    public void studio(){
        var studio = parser.scrapeStudio();
        assertEquals("DANDY", studio.getStudio());
    }
}
