package moviescraper.doctord.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FirefoxBrowser implements HeadlessBrowser {
    WebDriver webclient;
    private static final Logger LOGGER = Logger.getLogger(DitzyHeadlessBrowser.class.getName());


    public FirefoxBrowser(){
    }

    public URL currentURL() throws MalformedURLException {
        URL returnUrl;
        if(webclient != null){
            returnUrl = new URL(webclient.getCurrentUrl());
        } else {
            returnUrl = new URL("");
        }
        return returnUrl;
    }

    public void initializeBrowser() {
        webclient = new FirefoxDriver();
    }

    public Document getPageSource(){
        if(webclient == null)
            throw new IllegalCallerException("The webdriver has not been initialized.");

        var doc = Jsoup.parse(webclient.getPageSource());
        doc.setBaseUri(webclient.getCurrentUrl());
        return doc;
    }

    public Document get(URL url) {
        if(webclient == null){
            initializeBrowser();
        }

        LOGGER.log(Level.INFO, "Get request on {0}", url.toString());
        Document returnDoc = null;
        try {
            webclient.get(url.toString());
            returnDoc = Jsoup.parse(webclient.getPageSource());
            Thread.sleep(Duration.ofSeconds(((int) (Math.random() * 10)) + 5));
            webclient.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
            returnDoc.setBaseUri(webclient.getCurrentUrl());
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        return returnDoc;
    }


}
