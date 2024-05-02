package moviescraper.doctord.scraper;

import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URL;

public interface HeadlessBrowser {
    void initializeBrowser();
    Document getPageSource();
    Document get(URL url);
    URL currentURL() throws MalformedURLException;
}
