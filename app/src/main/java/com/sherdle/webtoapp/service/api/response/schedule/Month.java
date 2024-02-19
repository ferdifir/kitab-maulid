package com.sherdle.webtoapp.service.api.response.schedule;

import com.google.gson.annotations.SerializedName;

public class Month{

	@SerializedName("number")
	private int number;

	@SerializedName("en")
	private String en;

	@SerializedName("ar")
	private String ar;

	public void setNumber(int number){
		this.number = number;
	}

	public int getNumber(){
		return number;
	}

	public void setEn(String en){
		this.en = en;
	}

	public String getEn(){
		return en;
	}

	public void setAr(String ar){
		this.ar = ar;
	}

	public String getAr(){
		return ar;
	}
}