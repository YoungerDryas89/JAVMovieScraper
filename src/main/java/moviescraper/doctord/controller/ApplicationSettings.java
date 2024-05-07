package moviescraper.doctord.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.*;

import org.apache.commons.lang3.SystemUtils;

public class ApplicationSettings {
    public enum  OperatingSystem {
        Linux, Windows, Mac, Unknown
    }
    Path FirefoxLocation, GeckoDriver, ChromeLocation, ChromeDriver;
    OperatingSystem os;
    Logger LOGGER = Logger.getLogger(ApplicationSettings.class.getName());
    boolean preferFirefox = true;
    public ApplicationSettings(){
        var ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        LOGGER.addHandler(ch);
        LOGGER.setLevel(Level.ALL);
    }

    public void initialize(){
        if(SystemUtils.IS_OS_WINDOWS){
            os = OperatingSystem.Windows;
        } else if(SystemUtils.IS_OS_LINUX){
            os = OperatingSystem.Linux;
        } else if(SystemUtils.IS_OS_MAC_OSX){
            os = OperatingSystem.Mac;
        } else {
            os = OperatingSystem.Unknown;
        }


        if(isLinux()){
            String[] firefox_locations = {
                    "/usr/bin/firefox",
                    "/usr/bin/firefox-bin",
                    "/usr/bin/firefox-esr",
                    "/opt/firefox/firefox-bin"
            };
            FirefoxLocation = assignIfFileExists(firefox_locations);



            String[] chrome_locations = {
                    "/usr/bin/chromium",
                    "/usr/bin/google-chrome-stable",
                    "/opt/google/chrome/chrome",
                    "/opt/google/chrome/google-chrome"
            };
            ChromeLocation = assignIfFileExists(chrome_locations);


            String[] chromedriver_locations = {
                    "/usr/bin/chromedriver",
                    "/usr/bin/chromedriver-bin",
                    "/home/" + System.getenv("HOME") + "/.local/bin/chromedriver"
            };
            ChromeDriver = assignIfFileExists(chromedriver_locations);
            if(ChromeDriver == null){
                // TODO: Show warnings message box telling that the application will not work properly
                LOGGER.warning("Chromedriver was not found");
            }

            String[] geckodriver = {
                    "/usr/bin/geckodriver",
                    "/home/" + System.getenv("HOME") + "/.local/bin/geckodriver"
            };
            GeckoDriver = assignIfFileExists(geckodriver);
            if(GeckoDriver == null){
                LOGGER.warning("GeckoDriver was not found");
            }
            // TODO: Possibly use bundled driver if any external drivers aren't available

        } else if(isWindows()){
            String[] firefox_locations = {
                    ""
            };

            FirefoxLocation = assignIfFileExists(firefox_locations);

            String[] chrome_locations = {
                    ""
            };
            ChromeDriver = assignIfFileExists(chrome_locations);
        }

        if(GeckoDriver == null && ChromeDriver == null){
            LOGGER.warning("Neither the geckodriver or chromedriver where found; critical functionality will be missing!");
        } else {
            if(GeckoDriver != null){
                LOGGER.log(Level.CONFIG, "Located geckodriver at " + GeckoDriver.toString());
            }

            if(ChromeDriver != null){
                LOGGER.log(Level.CONFIG, "Located chromedriver at " + ChromeDriver.toString());
            }
        }

        if(FirefoxLocation == null && ChromeLocation == null){
            // TODO: Show warning message box
        } else {
            if(FirefoxLocation != null){
                LOGGER.log(Level.CONFIG, "Located firefox at " + FirefoxLocation.toString());
            }

            if(ChromeLocation != null){
                LOGGER.log(Level.CONFIG, "Located chrome at " + ChromeLocation.toString());
            }
        }
    }

    public boolean isWindows(){
        return os == OperatingSystem.Windows;
    }

    public boolean isLinux(){
        return os == OperatingSystem.Linux;
    }

    public boolean isMacOSX(){
        return os == OperatingSystem.Mac;
    }

    public Path firefoxPath(){
        return FirefoxLocation;
    }

    private Path assignIfFileExists(String[] files){
        Path returnPath;
        for(var elem : files){
            if(Files.exists(Path.of(elem))){
                returnPath = Path.of(elem);
                return returnPath;
            }
        }
        return null;
    }
}
