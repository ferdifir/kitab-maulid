package com.sherdle.webtoapp.service.api.response.date;

import com.google.gson.annotations.SerializedName;

public class Hijri{

	@SerializedName("date")
	private String date;

	@SerializedName("month")
	private Month month;

	@SerializedName("year")
	private String year;

	@SerializedName("format")
	private String format;

	@SerializedName("designation")
	private Designation designation;

	@SerializedName("day")
	private String day;

	public String getDate(){
		return date;
	}

	public Month getMonth(){
		return month;
	}

	public String getYear(){
		return year;
	}

	public String getFormat(){
		return format;
	}

	public Designation getDesignation(){
		return designation;
	}

	public String getDay(){
		return day;
	}
}