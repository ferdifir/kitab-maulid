package com.sherdle.webtoapp.model;

import com.google.gson.annotations.SerializedName;

public class Date{

	@SerializedName("readable")
	private String readable;

	@SerializedName("hijri")
	private Hijri hijri;

	@SerializedName("gregorian")
	private Gregorian gregorian;

	@SerializedName("timestamp")
	private String timestamp;

	public String getReadable(){
		return readable;
	}

	public Hijri getHijri(){
		return hijri;
	}

	public Gregorian getGregorian(){
		return gregorian;
	}

	public String getTimestamp(){
		return timestamp;
	}
}