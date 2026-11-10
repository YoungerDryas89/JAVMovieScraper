package com.github.youngerdryas89.moviescraper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.github.youngerdryas89.moviescraper.model.preferences.ScrapeSession;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.github.youngerdryas89.moviescraper.controller.Renamer;
import com.github.youngerdryas89.moviescraper.controller.releaserenamer.WebReleaseRenamer;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.AvEntertainmentParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.CaribbeancomParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.CaribbeancomPremiumParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.Data18MovieParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.Data18WebContentParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.DmmParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.ExcaliburFilmsParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.HeyzoParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.IAFDParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.JavBusParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.JavLibraryParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.Kin8tengokuParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.MyTokyoHotParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.OnePondoParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.TheMovieDatabaseParsingProfile;
import com.github.youngerdryas89.moviescraper.model.Movie;
import com.github.youngerdryas89.moviescraper.model.preferences.MoviescraperPreferences;
import com.github.youngerdryas89.moviescraper.view.GUIMain;
import org.apache.commons.io.FileUtils;

import javax.swing.*;

public class Main {
	public static void main(String[] args) throws Exception {
		ScrapeSession session = new ScrapeSession();

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		//Prevent text area font from looking different than text field font
		UIManager.getDefaults().put("TextArea.font", UIManager.getFont("TextField.font"));

		GUIMain window = new GUIMain(session);
		window.run();
	}
}
