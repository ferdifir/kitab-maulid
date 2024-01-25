package com.sherdle.webtoapp.model;

import com.google.gson.annotations.SerializedName;

public class Params{

	@SerializedName("Isha")
	private int isha;

	@SerializedName("Fajr")
	private int fajr;

	public int getIsha(){
		return isha;
	}

	public int getFajr(){
		return fajr;
	}
}