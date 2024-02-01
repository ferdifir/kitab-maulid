package com.sherdle.webtoapp.service.api.response.schedule;

import com.google.gson.annotations.SerializedName;

public class Offset{

	@SerializedName("Sunset")
	private int sunset;

	@SerializedName("Asr")
	private int asr;

	@SerializedName("Isha")
	private int isha;

	@SerializedName("Fajr")
	private int fajr;

	@SerializedName("Dhuhr")
	private int dhuhr;

	@SerializedName("Maghrib")
	private int maghrib;

	@SerializedName("Sunrise")
	private int sunrise;

	@SerializedName("Midnight")
	private int midnight;

	@SerializedName("Imsak")
	private int imsak;

	public int getSunset(){
		return sunset;
	}

	public int getAsr(){
		return asr;
	}

	public int getIsha(){
		return isha;
	}

	public int getFajr(){
		return fajr;
	}

	public int getDhuhr(){
		return dhuhr;
	}

	public int getMaghrib(){
		return maghrib;
	}

	public int getSunrise(){
		return sunrise;
	}

	public int getMidnight(){
		return midnight;
	}

	public int getImsak(){
		return imsak;
	}
}