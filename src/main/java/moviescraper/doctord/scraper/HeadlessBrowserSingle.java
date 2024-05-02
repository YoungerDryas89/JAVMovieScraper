package moviescraper.doctord.scraper;

public class HeadlessBrowserSingle {
    private static HeadlessBrowser _instance;
    public static HeadlessBrowser instance(){
        if(_instance == null){
            _instance = new FirefoxBrowser();
        }
        return _instance;
    }

}
