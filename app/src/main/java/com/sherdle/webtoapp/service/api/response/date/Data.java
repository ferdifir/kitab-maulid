package com.sherdle.webtoapp.service.api.response.date;

import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("hijri")
	private Hijri hijri;

	@SerializedName("gregorian")
	private Gregorian gregorian;

	public Hijri getHijri(){
		return hijri;
	}

	public Gregorian getGregorian(){
		return gregorian;
	}
}