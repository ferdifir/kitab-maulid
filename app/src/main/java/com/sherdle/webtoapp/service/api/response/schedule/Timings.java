package com.sherdle.webtoapp.service.api.response.schedule;

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

	@SerializedName("Lastthird")
	private String lastthird;

	@SerializedName("Firstthird")
	private String firstthird;

	@SerializedName("Sunrise")
	private String sunrise;

	@SerializedName("Midnight")
	private String midnight;

	@SerializedName("Imsak")
	private String imsak;

	public void setSunset(String sunset){
		this.sunset = sunset;
	}

	public String getSunset(){
		return sunset;
	}

	public void setAsr(String asr){
		this.asr = asr;
	}

	public String getAsr(){
		return asr;
	}

	public void setIsha(String isha){
		this.isha = isha;
	}

	public String getIsha(){
		return isha;
	}

	public void setFajr(String fajr){
		this.fajr = fajr;
	}

	public String getFajr(){
		return fajr;
	}

	public void setDhuhr(String dhuhr){
		this.dhuhr = dhuhr;
	}

	public String getDhuhr(){
		return dhuhr;
	}

	public void setMaghrib(String maghrib){
		this.maghrib = maghrib;
	}

	public String getMaghrib(){
		return maghrib;
	}

	public void setLastthird(String lastthird){
		this.lastthird = lastthird;
	}

	public String getLastthird(){
		return lastthird;
	}

	public void setFirstthird(String firstthird){
		this.firstthird = firstthird;
	}

	public String getFirstthird(){
		return firstthird;
	}

	public void setSunrise(String sunrise){
		this.sunrise = sunrise;
	}

	public String getSunrise(){
		return sunrise;
	}

	public void setMidnight(String midnight){
		this.midnight = midnight;
	}

	public String getMidnight(){
		return midnight;
	}

	public void setImsak(String imsak){
		this.imsak = imsak;
	}

	public String getImsak(){
		return imsak;
	}
}