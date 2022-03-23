package es.arvo.dspace.orciduploader.model;

public class PublicationDateOrcidUploader {

	private String year;
	private String month;
	private String day;

	public PublicationDateOrcidUploader(String year) {
		this.year = year;
	}

	public PublicationDateOrcidUploader(String year, String month) {
		this.year = year;
		this.month = month;
	}

	public PublicationDateOrcidUploader(String year, String month, String day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public String getYear() {
		return year;
	}

	public String getMonth() {
		return month;
	}
	
	public String getDay() {
		return day;
	}

	@Override
	public String toString() {
		String result = "";

		result += "<common:publication-date>";
		result += "<common:year>" + year + "</common:year>";
		if(month != null && !month.isEmpty())
			result += "<common:month>" + month + "</common:month>";
		if(day != null && !day.isEmpty())
			result += "<common:day>" + day + "</common:day>";
		result += "</common:publication-date>";
		
		return result;
	}

}
