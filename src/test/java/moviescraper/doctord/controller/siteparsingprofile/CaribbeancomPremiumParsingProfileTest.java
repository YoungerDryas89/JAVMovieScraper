package moviescraper.doctord.controller.siteparsingprofile;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import moviescraper.doctord.controller.siteparsingprofile.specific.CaribbeancomPremiumParsingProfile;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.OriginalTitle;
import moviescraper.doctord.model.dataitem.Plot;
import moviescraper.doctord.model.dataitem.Rating;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Trailer;
import moviescraper.doctord.model.dataitem.Year;
import moviescraper.doctord.controller.languagetranslation.Language;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

public class CaribbeancomPremiumParsingProfileTest {

	static File file = new File("C:/Temp/Caribbeancompr 122716_008.avi");
	static CaribbeancomPremiumParsingProfile parser;

	@BeforeClass
	public static void initialize() {
		parser = new CaribbeancomPremiumParsingProfile();
		parser.setScrapingLanguage(Language.ENGLISH);
		String searchString = parser.createSearchString(file);
		try {
			SearchResult[] searchResults = parser.getSearchResults("122716_008");
			Document document = SiteParsingProfile.getDocument(searchResults[0]);
			System.out.println("Scrape: " + document.location());
			parser.setDocument(document);
            parser.prepareData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("static-access")
	@Test
	public void testFindID() {
		String findIDTagFromFile = parser.getIdFromUrl();
		assertEquals(findIDTagFromFile, "122716_008");
	}

	@Test
	public void testScrapeTitle() {
		Title title = parser.scrapeTitle();
		//this assumes translation is done. if this test fails, it could be because translation is not done or the web
		//based translation service has changed how they do translation, so try to just see if the title is close to
		//this one and adjust as needed to fix the test case
		assertEquals("Akari Asagiri, Rei Kitajima, Maki Houjou, Reiko Kobayakawa", title.getTitle());
	}

	@Test
	public void testScrapeOriginalTitle() {
		OriginalTitle originalTitle = parser.scrapeOriginalTitle();
		assertEquals("Wrong original title", "THE 熟女　~妖艶で悩ましい美魔女たち~", originalTitle.getOriginalTitle());
	}

	@Test
	public void testScrapeRating() {
		Rating rating = parser.scrapeRating();
		assertEquals("Wrong rating", "", rating.getRatingOutOfTen());
	}

	@Test
	public void testScrapeYear() {
		Year year = parser.scrapeYear();
		assertEquals("Wrong year", "", year.getYear());
	}

	@Test
	public void testScrapeReleaseDate() {
		ReleaseDate releaseDate = parser.scrapeReleaseDate();
		assertEquals("Wrong release date", "", releaseDate.getReleaseDate());
	}

	@Test
	public void testScrapePlot() {
        String moviePlot = "芸能界も高齢化ならAV界だって同じ！？熟女界のビッグ4による、豪華淫乱なオムニバスが登場！年齢も場数も重ねるごとに味と深みを増してゆく極上の美魔女たちの痴態をたっぷり収録！数百、いや数千もの肉棒を受け入れてきた大ベテラン女優にしか出せない妖艶なフェロモンが画面から滲み出てきます。男を翻弄する言葉遣いに腰振り、顔面や男根に自ら跨り快楽を追及する貪欲さ、何よりセックスを積極的に楽しむ姿勢が伝わってくる臨場感がタマりません！";
		Plot plot = parser.scrapePlot();
		assertEquals("", moviePlot, plot.getPlot());
	}

	@Test
	public void testScrapeRuntime() {
		moviescraper.doctord.model.dataitem.Runtime movieRuntime = parser.scrapeRuntime();
		assertEquals("Wrong runtime", "189", movieRuntime.getRuntime());
	}

	@Test
	public void testScrapeActors() {
		ArrayList<Actor> actorList = parser.scrapeActors();
		assertEquals("Wrong actor", "Akari Asagiri", actorList.get(0).getName());
	}

	@Test
	public void testScrapeGenre() {
		ArrayList<Genre> genreList = parser.scrapeGenres();
		assertEquals("Wrong genre size", 16, genreList.size());
		assertEquals("Wrong genre", "AV Idol", genreList.get(0).getGenre());
	}

	@Test
	public void testTrailer() {
		Trailer trailer = parser.scrapeTrailer();
		assertEquals("Wrong trailer", "https://smovie.caribbeancompr.com/sample/movies/122716_008/480p.mp4", trailer.getTrailer());
	}

	@Test
	public void testScrapePoster() {
		Thumb[] posters = parser.scrapePosters(true);
		assertEquals("Poster size not right", true, posters.length > 0);
		assertEquals("Wrong poster url", "https://www.caribbeancompr.com/moviepages/122716_008/images/l_l.jpg", posters[0].getThumbURL().toString());
	}

}
