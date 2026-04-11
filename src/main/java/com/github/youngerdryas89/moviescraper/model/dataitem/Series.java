package com.github.youngerdryas89.moviescraper.model.dataitem;

public class Series extends MovieDataItem {

	private String set;
	public static final Series BLANK_SERIES = new Series("");

	public String getSet() {
		return set;
	}

	public void setSet(String set) {
		this.set = sanitizeString(set);
	}

	public Series(String set) {
		setSet(set);
	}

	@Override
	public String toString() {
		return "Set [set=\"" + set + "\"" + dataItemSourceToString() + "]";
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	public Series() {
		set = "";
	}

}
