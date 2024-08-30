package moviescraper.doctord.controller.siteparsingprofile;
import static org.junit.Assert.*;

import moviescraper.doctord.controller.siteparsingprofile.specific.ActionJavParsingProfile;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class ActionJavParsingProfileTest {
    static ActionJavParsingProfile profile;
    static String movieID = "FC2-PPV-4317137";

    @BeforeClass
    public static void initialize() throws Exception, IOException {
        profile = new ActionJavParsingProfile();
        var searchResults = profile.getSearchResults(profile.createSearchStringFromId(movieID));
        if(searchResults.length > 0){
            var doc = SiteParsingProfile.downloadDocumentFromURLString(searchResults[0].getUrlPath());
            profile.setDocument(doc);
        } else {
            throw new Exception("No search results given!");
        }

    }

    @Test
    public void testId(){
        var id = profile.scrapeID();
        assertEquals("Expected id: FC2-PPV-4317137", id.getId(), "FC2-PPV-4317137");
    }

    @Test
    public void testPoster(){
        var poster = profile.scrapePosters(false);
        assertTrue(poster.length > 0 && poster[0].getThumbURL().toString().equals("https://images.actionjav.com/web_img/covers_hires_full/amateur_-_27908_hd.jpg"));
    }

    @Test
    public void testGenres(){
        var genres = profile.scrapeGenres();
        assertFalse(genres.isEmpty());
    }

    @Test
    public void testActors(){
        var actors = profile.scrapeActors();
        assertEquals(1, actors.size());
    }

    @Test
    public void testStudio(){
        var studio = profile.scrapeStudio();
        assertEquals(studio.getStudio(), "FC2-PPV");
    }

    @Test
    public void testReleaseDate(){
        var releaseDate = profile.scrapeReleaseDate();
        assertEquals(releaseDate.getReleaseDate(), "2024-02-27");
    }

    @Test
    public void testYear(){
        var year = profile.scrapeReleaseDate().getYear();
        assertEquals(year.getYear(), "2024");
    }

    @Test
    public void testTitle(){
        var title = profile.scrapeTitle();
        assertEquals("FC2-PPV-4317137 First shoot Face showing For the sake of ...", title.getTitle());
    }
}
