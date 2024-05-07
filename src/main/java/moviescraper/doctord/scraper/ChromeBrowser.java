package moviescraper.doctord.scraper;
import moviescraper.doctord.scraper.chromedriver.ChromeDriverBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChromeBrowser implements HeadlessBrowser {
    ChromeDriver driver;
    private final Logger LOGGER = Logger.getLogger(ChromeBrowser.class.getName());
    public ChromeBrowser(){

    }


    public void initializeBrowser(){
        ChromeOptions options = new ChromeOptions()
                .addArguments("--window-size=1920,1080")
                .addArguments("--headless=new");

        driver = new ChromeDriverBuilder().build(
                options,
                "/home/sai/.local/bin/chromedriver"
        );
    }

    public URL currentURL() throws MalformedURLException {
        URL returnUrl;
        if(driver == null){
            returnUrl = new URL(driver.getCurrentUrl());
        } else {
            returnUrl = new URL("");
        }
        return returnUrl;
    }

    public Document getPageSource(){
        if(driver == null)
            throw new IllegalCallerException("Chrome browser has not been initialized");

        var doc = Jsoup.parse(driver.getPageSource());
        doc.setBaseUri(driver.getCurrentUrl());
        return doc;
    }

    public Document get(URL url) {

        if(driver == null)
            initializeBrowser();

        LOGGER.log(Level.INFO, "Get request on {0}", url.toString());
        Document returnDoc = null;
        try {
            Thread.sleep(Duration.ofSeconds(((int) (Math.random() * 10)) + 5));
            driver.get(url.toString());
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
            returnDoc = Jsoup.parse(driver.getPageSource());
            returnDoc.setBaseUri(driver.getCurrentUrl());
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        return returnDoc;
    }
}
