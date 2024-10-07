package com.github.youngerdryas89.scraper;

public class DitzyHeadlessBrowserSingle {

	private static DitzyHeadlessBrowser instance;

	static public DitzyHeadlessBrowser getBrowser() {
		if (instance == null) {
			instance = new DitzyHeadlessBrowser();
		}
		return instance;
	}
}
