package moviescraper.doctord.controller.siteparsingprofile.specific;

import moviescraper.doctord.controller.languagetranslation.Language;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfileJSON;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.*;
import moviescraper.doctord.model.dataitem.Runtime;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TenMusumeParsingProfile extends SiteParsingProfileJSON implements SpecificProfile {
    @Override
    public @NotNull Title scrapeTitle() {
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

    @Override
    public @NotNull OriginalTitle scrapeOriginalTitle() {
        JSONObject pageJSON = getMovieJSON();
        return new OriginalTitle(pageJSON.getString("Title"));
    }

    @Override
    public @NotNull SortTitle scrapeSortTitle() {
        return SortTitle.BLANK_SORTTITLE;
    }

    @Override
    public @NotNull Set scrapeSet() {
        return Set.BLANK_SET;
    }

    @Override
    public @NotNull Rating scrapeRating() {
        return Rating.BLANK_RATING;
    }

    @Override
    public @NotNull ReleaseDate scrapeReleaseDate() {
        JSONObject pageJSON = getMovieJSON();
        String releaseDate = pageJSON.getString("Release");
        return new ReleaseDate(releaseDate);
    }

    @Override
    public @NotNull Year scrapeYear() {
        JSONObject pageJSON = getMovieJSON();
        String releaseYear = pageJSON.getString("Year");
        return new Year(releaseYear);
    }

    @Override
    public @NotNull Top250 scrapeTop250() {
        return Top250.BLANK_TOP250;
    }

    @Override
    public @NotNull Votes scrapeVotes() {
        return Votes.BLANK_VOTES;
    }

    @Override
    public @NotNull Outline scrapeOutline() {
        return Outline.BLANK_OUTLINE;
    }

    @Override
    public @NotNull Plot scrapePlot() {
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

    @Override
    public @NotNull Tagline scrapeTagline() {
        return Tagline.BLANK_TAGLINE;
    }

    @Override
    public @NotNull Runtime scrapeRuntime() {
        JSONObject pageJSON = getMovieJSON();
        String duration = String.valueOf(pageJSON.getInt("Duration"));
        return new Runtime(duration);
    }

    @Override
    public @NotNull Thumb[] scrapePosters(boolean cropPosters) {
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
    public @NotNull Thumb[] scrapeFanart() {
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
    public @NotNull Thumb[] scrapeExtraFanart() {
        return new Thumb[0];
    }

    @Override
    public @NotNull MPAARating scrapeMPAA() {
        return MPAARating.RATING_XXX;
    }

    @Override
    public @NotNull ID scrapeID() {
        JSONObject pageJSON = getMovieJSON();
        String movieID = pageJSON.getString("MovieID");
        return new ID(movieID);
    }

    @Override
    public @NotNull ArrayList<Genre> scrapeGenres() {
        ArrayList<Genre> genreList = new ArrayList<>();
        return genreList;
    }

    @Override
    public @NotNull ArrayList<Actor> scrapeActors() {
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

    @Override
    public @NotNull ArrayList<Director> scrapeDirectors() {
        ArrayList<Director> directorList = new ArrayList<>();
        return directorList;
    }

    @Override
    public @NotNull Studio scrapeStudio() {
        return new Studio("10musume");
    }

    @Override
    public @NotNull Trailer scrapeTrailer(){
        ID movieID = scrapeID();
        String potentialTrailerURL = "http://smovie.1pondo.tv/moviepages/" + movieID.getId() + "/sample/sample.avi";
        if (SiteParsingProfile.fileExistsAtURL(potentialTrailerURL))
            return new Trailer(potentialTrailerURL);
        else
            return Trailer.BLANK_TRAILER;
    }

    @Override
    public @NotNull String createSearchString(File file) {
        return createSearchStringFromId(findIDTagFromFile(file, false));
    }

    @Override
    public String createSearchStringFromId(String id) {

        if(id.isEmpty() || id == null)
            return "";
        return "https://www.10musume.com/dyn/phpauto/movie_details/movie_id/" + id + ".json";
    }

    @Override
    public @NotNull SearchResult[] getSearchResults(String searchString) throws IOException {
        SearchResult searchResult = new SearchResult(searchString);
        searchResult.setJSONSearchResult(true);
        SearchResult[] searchResultArray = { searchResult };
        return searchResultArray;
    }

    @Override
    public SiteParsingProfile newInstance() {
        return new TenMusumeParsingProfile();
    }

    @Override
    public String getParserName() {
        return "10Musume";
    }
}
