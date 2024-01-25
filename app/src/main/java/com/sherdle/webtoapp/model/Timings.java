package com.sherdle.webtoapp.model;

import com.google.gson.annotations.SerializedName;

public class Timings{

	@SerializedName("Sunset")
	private String sunset;

	@SerializedName("Asr")
	private String asr;

	@SerializedName("Isha")
	private String isha;

	@SerializedName("Fajr")
	private String fajr;

	@SerializedName("Dhuhr")
	private String dhuhr;

	@SerializedName("Maghrib")
	private String maghrib;

	@SerializedName("Sunrise")
	private String sunrise;

	@SerializedName("Midnight")
	private String midnight;

	@SerializedName("Imsak")
	private String imsak;

	public String getSunset(){
		return sunset;
	}

	public String getAsr(){
		return asr;
	}

	public String getIsha(){
		return isha;
	}

	public String getFajr(){
		return fajr;
	}

	public String getDhuhr(){
		return dhuhr;
	}

	public String getMaghrib(){
		return maghrib;
	}

	public String getSunrise(){
		return sunrise;
	}

	public String getMidnight(){
		return midnight;
	}

	public String getImsak(){
		return imsak;
	}
}