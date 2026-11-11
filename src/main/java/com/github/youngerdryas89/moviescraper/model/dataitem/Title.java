package com.github.youngerdryas89.moviescraper.model.dataitem;

public class Title extends MovieDataItem {

    public final static Title BLANK_TITLE = new Title("");
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = sanitizeString(title);
	}

	public Title(String title) {
		super();
		setTitle(title);
	}

	@Override
	public String toXML() {
		return "<title>" + title + "</title>";
	}

	@Override
	public String toString() {
		return "Title [title=\"" + title + "\"" + dataItemSourceToString() + "]";
	}

	public Title() {
		title = "";
	}

	/**
	 * @return true if the movie has a non-null, non-zero length title, false otherwise
	 */
	public boolean hasValidTitle() {
		return (title != null && !title.isEmpty());
	}

}
