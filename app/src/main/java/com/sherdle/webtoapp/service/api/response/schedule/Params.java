package com.sherdle.webtoapp.service.api.response.schedule;

import com.google.gson.annotations.SerializedName;

public class Params{

	@SerializedName("Isha")
	private int isha;

	@SerializedName("Fajr")
	private int fajr;

	public void setIsha(int isha){
		this.isha = isha;
	}

	public int getIsha(){
		return isha;
	}

	public void setFajr(int fajr){
		this.fajr = fajr;
	}

	public int getFajr(){
		return fajr;
	}
}