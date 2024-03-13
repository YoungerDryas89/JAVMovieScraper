package moviescraper.doctord.model.dataitem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ID extends MovieDataItem {

	private String series, id, fullname;

	public static final ID BLANK_ID = new ID("");

	public String getSeries(){
		return series;
	}

	public void setSeries(String series){
		this.series = series;
	}
	public String getId() {
		return id;
	}

	public String getFullname(){
		return fullname;
	}

	public void setFullname(String fullname){
		this.fullname = sanitizeString(fullname);
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ID [id=\"" + fullname + "\"" + dataItemSourceToString() + "]";
	}

	public ID(String id) {
		super();
		setId(id);
	}

	public ID(String series, String id, String fullname){
		this.series = series;
		this.id = id;
		this.fullname = fullname;
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fullname == null) ? 0 : fullname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ID other = (ID) obj;
		if (fullname == null) {
			if (other.fullname != null)
				return false;
		} else if (!fullname.equals(other.fullname))
			return false;
		return true;
	}

	/**
	 * More lenient version of equal which tries to do some fuzzy logic to see if two scraped JAV DVD release movies
	 * have the same IDs
	 * 
	 * @return true if the two movies essentially have the same ID, with small differences in formatting
	 */
	public boolean equalsJavID(ID otherID) {
		if (this.fullname == null || otherID == null || otherID.getId() == null || fullname.length() == 0 || otherID.getId().length() == 0)
			return false;
		else {
			String thisIDString = fullname.replaceAll("-", "");
			String otherIDString = otherID.getId().replaceAll("-", "");
			/*
			 * if(!thisIDString.endsWith(otherIDString) && !otherIDString.startsWith(thisIDString))
			 * return false;
			 */
			Pattern patternID = Pattern.compile("([0-9]*)(\\D+)(\\d+)(\\D)*");
			Matcher matcherThisIDString = patternID.matcher(thisIDString);
			Matcher matcherOtherIDString = patternID.matcher(otherIDString);

			//Let's say our ID is 73ABC123SO

			//Comment out matches 1 and 4 because they are not needed to check for equality
			//I am leaving them in the code in case I decide later on there are cases where
			//I need to reference them to check for equality

			//String thisIDStartNumbers = ""; // with the example above this would be 73
			String thisIDMovieSeries = ""; // with the example above this would be ABC
			String thisIDMovieNumber = ""; // with the example above this would be 123
			//String thisIDOptionalIgnoredSuffix = ""; // with the example above this would be SO

			//String otherIDStartNumbers = ""; // with the example above this would be 73
			String otherIDMovieSeries = ""; // with the example above this would be ABC
			String otherIDMovieNumber = ""; // with the example above this would be 123
			//String otherIDOptionalIgnoredSuffix = ""; // with the example above this would be SO

			while (matcherThisIDString.find()) {
				//thisIDStartNumbers = matcherThisIDString.group(1);
				thisIDMovieSeries = matcherThisIDString.group(2);
				thisIDMovieNumber = matcherThisIDString.group(3);
				//thisIDOptionalIgnoredSuffix =  matcherThisIDString.group(4);
			}

			while (matcherOtherIDString.find()) {
				//otherIDStartNumbers = matcherOtherIDString.group(1);
				otherIDMovieSeries = matcherOtherIDString.group(2);
				otherIDMovieNumber = matcherOtherIDString.group(3);
				//otherIDOptionalIgnoredSuffix =  matcherOtherIDString.group(4);
			}

			//The part that looks like "ABC" above must be the same (case doesn't matter)
			if (!thisIDMovieSeries.equalsIgnoreCase(otherIDMovieSeries)) {

				// Special case for Moodyz titles with slightly different tags for DVD/VHS, 
				// (MDED -> MDE, MDID -> MDI, MDLD -> MDL...)

				Pattern moodyzPatternID = Pattern.compile("^(MD.)D?$", Pattern.CASE_INSENSITIVE);
				Matcher moodyzMatcher1 = moodyzPatternID.matcher(thisIDMovieSeries);
				Matcher moodyzMatcher2 = moodyzPatternID.matcher(otherIDMovieSeries);

				if (!(moodyzMatcher1.matches() && moodyzMatcher2.matches() && moodyzMatcher1.group(1).equalsIgnoreCase(moodyzMatcher2.group(1))))
					return false;
			}

			//The part that looks like 123 must be the same when treated as an integer
			Integer thisIDMovieNumberInteger = Integer.parseInt(thisIDMovieNumber);
			Integer otherIdMovieNumberInteger = Integer.parseInt(otherIDMovieNumber);
			if (!thisIDMovieNumberInteger.equals(otherIdMovieNumberInteger))
				return false;

		}
		return true;
	}

	public ID() {
		id = "";
	}

}
