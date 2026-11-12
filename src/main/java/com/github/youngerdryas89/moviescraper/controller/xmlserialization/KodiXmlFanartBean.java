package com.github.youngerdryas89.moviescraper.controller.xmlserialization;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.github.youngerdryas89.moviescraper.model.dataitem.Thumb;

/**
 * Helper class for serializing a fanart object to and from XML
 */
public class KodiXmlFanartBean {
	private List<String> thumb = new ArrayList<>();

	public KodiXmlFanartBean(List<Thumb> thumb) {
		this.thumb = thumb.stream().map(Thumb::getThumbURL).map(URL::toString).toList();
	}

	public List<String> getThumb() {
		return thumb;
	}

	public void setThumb(List<String> thumb) {
		this.thumb = thumb;
	}
}
