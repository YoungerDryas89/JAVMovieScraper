package moviescraper.doctord.controller.siteparsingprofile;

import moviescraper.doctord.controller.siteparsingprofile.specific.NJavParsingProfile;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class NJavParsingProfileTest {
    static NJavParsingProfile parser;


    @BeforeClass
    public static void initialize() throws IOException {
        parser = new NJavParsingProfile();
        var result = parser.getSearchResults("siro-5283");
        var doc = SiteParsingProfile.getDocument(result[0]);
        parser.setDocument(doc);
        parser.prepareData();
    }

    @Test
    public void title(){
        var title = parser.scrapeTitle();
        assertEquals("SIRO-5283 ``I want to see my own sex'' A short beautiful girl with high erotic curiosity applied! - Her white skin trembles with unstoppable pleasure! - Once sex starts, you can't stop! - unstoppable! - [First shoot] AV application online → AV experience shooting 2172", title.getTitle());
    }

    @Test
    public void id(){
        var id = parser.scrapeID();
        assertEquals(id.getId(), "SIRO-5283");
    }

    @Test
    public void releaseDate(){
        assertEquals("2024-06-18", parser.scrapeReleaseDate().getReleaseDate());
    }

    @Test
    public void runtime(){
        assertEquals(parser.scrapeRuntime().getRuntime(), "00:59:45");
    }

    @Test
    public void genres(){
        assertEquals(6, parser.scrapeGenres().size());
    }

    @Test
    public void set(){
        var series = parser.scrapeSet().getSet();
        assertEquals("[First Shot] Av Application On The Net → Av Experience Shooting", series);
    }

    @Test
    public void plot(){
        var plot = "We will meet at Garden Place, where Christmas roses are blooming. - The person who came to apply today was [Momo-chan, 20 years old], a student attending a vocational school, and she was nervous and had a cute honeycomb face. - She is a kind child who answers my questions with a smile. - It seems that she doesn't have a boyfriend because she is busy studying at school and hasn't met anyone. - When I asked her why she applied, she told me that she wanted to see herself having sex! - I couldn't believe my ears either. - I can't believe such a cute and good girl would say such a thing. - The image of her shyly taking off her clothes is pretty and fleeting, and gives a sense of emoness and eroticism. - She wants to have sex that will leave a lasting impression on the viewers. - Please take a look at her sex, which is like the language of Christmas roses.";
        assertEquals(plot, parser.scrapePlot().getPlot());
    }

}
