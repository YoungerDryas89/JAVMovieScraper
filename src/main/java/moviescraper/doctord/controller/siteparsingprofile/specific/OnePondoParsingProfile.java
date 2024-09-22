package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moviescraper.doctord.controller.languagetranslation.Language;
import org.apache.commons.io.FilenameUtils;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfileJSON;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Director;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.ID;
import moviescraper.doctord.model.dataitem.MPAARating;
import moviescraper.doctord.model.dataitem.OriginalTitle;
import moviescraper.doctord.model.dataitem.Outline;
import moviescraper.doctord.model.dataitem.Plot;
import moviescraper.doctord.model.dataitem.Rating;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Runtime;
import moviescraper.doctord.model.dataitem.Set;
import moviescraper.doctord.model.dataitem.SortTitle;
import moviescraper.doctord.model.dataitem.Studio;
import moviescraper.doctord.model.dataitem.Tagline;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Top250;
import moviescraper.doctord.model.dataitem.Trailer;
import moviescraper.doctord.model.dataitem.Votes;
import moviescraper.doctord.model.dataitem.Year;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;

public class OnePondoParsingProfile extends SiteParsingProfileJSON implements SpecificProfile {

	//private boolean scrapeInEnglish;
	private String englishPage;
	private String japanesePage;

	@Override
	public String getParserName() {
		return "1pondo";
	}

	@Nonnull
    @Override
	public Title scrapeTitle() {
		JSONObject pageJSON = getMovieJSON();
        String title;
		if (scrapingLanguage == Language.ENGLISH) {
            if(!pageJSON.isNull("TitleEn")) {
                title = pageJSON.getString("TitleEn");
            } else {
                title = pageJSON.getString("Title");
            }

		} else {
			title = pageJSON.getString("Title");
		}

        return new Title(title);
	}

	@Nonnull
    @Override
	public OriginalTitle scrapeOriginalTitle() {
		JSONObject pageJSON = getMovieJSON();
		return new OriginalTitle(pageJSON.getString("Title"));
	}

	@Nonnull
    @Override
	public SortTitle scrapeSortTitle() {
		// This site has no sort title information
		return SortTitle.BLANK_SORTTITLE;
	}

	@Nonnull
    @Override
	public Set scrapeSet() {
		// This site has no set information
		return Set.BLANK_SET;
	}

	@Nonnull
    @Override
	public Rating scrapeRating() {
		// This site has no rating information
		return new Rating(0, "0");
	}

	@Nonnull
    @Override
	public Year scrapeYear() {
		JSONObject pageJSON = getMovieJSON();
		String releaseYear = pageJSON.getString("Year");
		return new Year(releaseYear);
	}

	@Nonnull
    @Override
	public ReleaseDate scrapeReleaseDate() {
		JSONObject pageJSON = getMovieJSON();
		String releaseDate = pageJSON.getString("Release");
		return new ReleaseDate(releaseDate);
	}

	@Nonnull
    @Override
	public Top250 scrapeTop250() {
		//This site has no top250 information
		return Top250.BLANK_TOP250;
	}

	@Nonnull
    @Override
	public Votes scrapeVotes() {
		//This site has no vote information
		return Votes.BLANK_VOTES;
	}

	@Nonnull
    @Override
	public Outline scrapeOutline() {
		//This site has no outline for movies
		return Outline.BLANK_OUTLINE;
	}

	@Nonnull
    @Override
	public Plot scrapePlot() {
		var json = getMovieJSON();
        String plot;
		if(scrapingLanguage == Language.ENGLISH){
            if(!json.isNull("DescEn")) {
                plot = json.getString("DescEn");
            } else {
                plot = json.getString("Desc");
            }
		} else {
			plot = json.getString("Desc");
		}

        return new Plot(plot);
	}

	@Nonnull
    @Override
	public Tagline scrapeTagline() {
		// TODO Auto-generated method stub
		return Tagline.BLANK_TAGLINE;
	}

	@Nonnull
    @Override
	public Runtime scrapeRuntime() {
		JSONObject pageJSON = getMovieJSON();
		String duration = String.valueOf(pageJSON.getInt("Duration"));
		return new Runtime(duration);
	}

	@Override
	public Thumb[] scrapePosters(boolean cropPosters) {
		ArrayList<Thumb> thumbList = new ArrayList<>();
		JSONObject pageJSON = getMovieJSON();
		try {
			// Some movies have a special poster "jacket". Use it as the primary poster instead of anything else.
			var jacketURL = "https://www.1pondo.tv/dyn/dla/images/movies/" + pageJSON.getString("MovieID") + "/jacket/jacket.jpg";
			if (fileExistsAtURL(jacketURL, false)) {
				thumbList.add(new Thumb(jacketURL, cropPosters));
				return thumbList.toArray(new Thumb[thumbList.size()]);
			} else {
				String[] thumbnailJsonNodes = {
						"ThumbUltra",
						"ThumbHigh",
						"ThumbMed",
						"ThumbLow"
				};
				// Iterate and make sure no duplicates get added
				for (var elem : thumbnailJsonNodes) {
					var url = pageJSON.getString(elem);
					if(!thumbList.isEmpty()) {
						for (var thumb : thumbList) {
							if (!url.equals(thumb.getThumbURL().toString()) && !url.isEmpty()) {
								thumbList.add(new Thumb(url));
							}
						}
					} else {
						thumbList.add(new Thumb(url));
					}
				}

				return thumbList.toArray(new Thumb[thumbList.size()]);

			}
		} catch (IOException ex) {
			Logger.getLogger(OnePondoParsingProfile.class.getName()).log(Level.SEVERE, null, ex);
		}
		return new Thumb[0];

	}

	@Override
	public Thumb[] scrapeFanart() {
		try {
			ArrayList<Thumb> thumbList = new ArrayList<>();
			String bannerURL = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/str.jpg";
			String backgroundURLOne = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/1.jpg";
			String backgroundURLTwo = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/2.jpg";
			String popupOneURL = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/popu/1.jpg";
			String popupTwoURL = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/popu/2.jpg";
			String popupThreeURL = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/popu/3.jpg";
			String popupFourURL = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/popu.jpg";
			if (SiteParsingProfile.fileExistsAtURL(bannerURL, false))
				thumbList.add(new Thumb(bannerURL));
			if (SiteParsingProfile.fileExistsAtURL(popupOneURL, false))
				thumbList.add(new Thumb(popupOneURL));
			if (SiteParsingProfile.fileExistsAtURL(popupTwoURL, false))
				thumbList.add(new Thumb(popupTwoURL));
			if (SiteParsingProfile.fileExistsAtURL(popupThreeURL, false))
				thumbList.add(new Thumb(popupThreeURL));
			//combine the two background images together to make the fanart if we are on a page that has split things into two images
			if (SiteParsingProfile.fileExistsAtURL(backgroundURLOne, false) && SiteParsingProfile.fileExistsAtURL(backgroundURLTwo, false)) {
				try {
					/*
					 * BufferedImage img1 = ImageIO.read(new URL(backgroundURLOne));
					 * BufferedImage img2 = ImageIO.read(new URL(backgroundURLTwo));
					 * BufferedImage joinedImage = joinBufferedImage(img1, img2);
					 * Thumb joinedImageThumb = new Thumb(backgroundURLTwo);
					 * joinedImageThumb.setImage(joinedImage);
					 * //we did an operation to join the images, so we'll need to re-encode the jpgs. set the modified flag to true
					 * //so we know to do this
					 * joinedImageThumb.setIsModified(true);
					 */

					Thumb joinedImageThumb = new Thumb(backgroundURLOne, backgroundURLTwo);
					thumbList.add(joinedImageThumb);
				} catch (IOException e) {
					thumbList.add(new Thumb(backgroundURLTwo));
				}

			} else if (SiteParsingProfile.fileExistsAtURL(backgroundURLTwo, false))
				thumbList.add(new Thumb(backgroundURLTwo));
			if (SiteParsingProfile.fileExistsAtURL(popupFourURL, false))
				thumbList.add(new Thumb(popupFourURL));
			return thumbList.toArray(new Thumb[thumbList.size()]);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		return scrapeFanart();
	}

	@Nonnull
    @Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Nonnull
    @Override
	public ID scrapeID() {
		JSONObject pageJSON = getMovieJSON();
		String movieID = pageJSON.getString("MovieID");
		return new ID(movieID);
	}

	@Nonnull
    @Override
	public ArrayList<Genre> scrapeGenres() {
		//For now, I wasn't able to find any genres on the page
		ArrayList<Genre> genreList = new ArrayList<>();
		return genreList;
	}

	@Nonnull
    @Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<>(1);
		JSONObject pageJSON = getMovieJSON();
		JSONArray actors;
		if(scrapingLanguage == Language.ENGLISH) {
            if(!pageJSON.isNull("AcressesEn")) {
                actors = pageJSON.getJSONArray("ActressesEn");
            } else {
                actors = pageJSON.getJSONArray("ActressesJa");
            }
		} else {
			actors = pageJSON.getJSONArray("ActressesJa");
		}
		for (Object actor : actors) {
			actorList.add(new Actor((String) actor, "", null));
		}

		return actorList;
	}

	@Nonnull
    @Override
	public ArrayList<Director> scrapeDirectors() {
		//No Directors listed for this site, return an empty list
		ArrayList<Director> directorList = new ArrayList<>();
		return directorList;
	}

	@Nonnull
    @Override
	public Studio scrapeStudio() {
		return new Studio("1pondo");
	}

	@Nonnull
    @Override
	public Trailer scrapeTrailer() {
		ID movieID = scrapeID();
		String potentialTrailerURL = "http://smovie.1pondo.tv/moviepages/" + movieID.getId() + "/sample/sample.avi";
		if (SiteParsingProfile.fileExistsAtURL(potentialTrailerURL))
			return new Trailer(potentialTrailerURL);
		else
			return Trailer.BLANK_TRAILER;
	}

	@Nonnull
    @Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		return createSearchStringFromId(findIDTagFromFile(file));
	}
        
        @Override
        public String createSearchStringFromId(String Id){
            Id = Id.toLowerCase();
            if (Id == null)
		return null;

            return "https://www.1pondo.tv/dyn/phpauto/movie_details/movie_id/" + Id + ".json";
        }

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		SearchResult searchResult = new SearchResult(searchString);
		searchResult.setJSONSearchResult(true);
		SearchResult[] searchResultArray = { searchResult };
		return searchResultArray;
	}

	public static String findIDTagFromFile(File file) {
		return findIDTag(FilenameUtils.getName(file.getName()));
	}

	public static String findIDTag(String fileName) {
		Pattern pattern = Pattern.compile("[0-9]{6}_[0-9]{3}");
		Matcher matcher = pattern.matcher(fileName);
		if (matcher.find()) {
			String searchString = matcher.group();
			return searchString;
		}
		return null;
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new OnePondoParsingProfile();
	}

}
