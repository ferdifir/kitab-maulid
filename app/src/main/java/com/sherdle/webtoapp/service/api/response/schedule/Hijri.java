package com.sherdle.webtoapp.service.api.response.schedule;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Hijri{

	@SerializedName("date")
	private String date;

	@SerializedName("month")
	private Month month;

	@SerializedName("holidays")
	private List<String> holidays;

	@SerializedName("year")
	private String year;

	@SerializedName("format")
	private String format;

	@SerializedName("weekday")
	private Weekday weekday;

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

	public List<String> getHolidays(){
		return holidays;
	}

	public String getYear(){
		return year;
	}

	public String getFormat(){
		return format;
	}

	public Weekday getWeekday(){
		return weekday;
	}

	public Designation getDesignation(){
		return designation;
	}

	public String getDay(){
		return day;
	}
}