package com.example.movieproject;

public class MovieSearch {
	
	private String year = "";
	private String imdbId = "";
	private String title = "";
	
	public MovieSearch (String title, String year,  String imdbId){
		this.setTitle(title);
		this.setYear(year);
		this.setImdbId(imdbId);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public String getImdbId() {
		return imdbId;
	}

	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
	}
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	public String toString() {
		return this.title;
	}
	
}
