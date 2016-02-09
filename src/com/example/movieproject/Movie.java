package com.example.movieproject;

import org.json.JSONObject;

import android.R.string;
import android.util.Log;

public class Movie {
	private static final String TAG = "Movie Class";
	long id;
	private String title = "";
	private String descreption = "";
	private String url = "";

	
	public Movie (String title, String descreption, String url){
		this.setTitle(title);
		this.setDescreption(descreption);
		this.setUrl(url);;
	}

	public void setDescreption(String descreption) {
		this.descreption = descreption;
	}
	public String getDescreption() {
		return descreption;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return getTitle();
	}





	public String getTitle() {
		return title;
	}





	public void setTitle(String title) {
		this.title = title;
	}
}
