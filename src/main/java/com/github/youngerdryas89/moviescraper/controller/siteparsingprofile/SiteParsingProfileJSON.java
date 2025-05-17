package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public abstract class SiteParsingProfileJSON extends SiteParsingProfile {
	private JSONObject movieJSON;

	protected JSONObject getMovieJSON() {
		if (movieJSON == null && document != null) {
			try {
				movieJSON = getJSONObjectFromURL(document.location());
				return movieJSON;
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
		}
		return movieJSON;
	}

	public String getJSONStringFromURL(String url) throws IOException {
		String json = Jsoup.connect(url).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).ignoreContentType(true).execute().body();
		return json;
	}

	public JSONObject getJSONObjectFromString(String jsonString) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonString);
		return jsonObject;
	}

	public JSONObject getJSONObjectFromURL(String url) throws JSONException, IOException {
		return getJSONObjectFromString(getJSONStringFromURL(url));
	}

	public static Connection.Response getDocument(String url) throws IOException {
		try {
			return Jsoup.connect(url).ignoreContentType(true).execute();
		}catch (IOException e){
			System.err.println(e.getMessage());
		}
		return null;
	}

}
