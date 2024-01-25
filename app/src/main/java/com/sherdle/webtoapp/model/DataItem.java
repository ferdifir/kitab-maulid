package com.sherdle.webtoapp.model;

import com.google.gson.annotations.SerializedName;

public class DataItem{

	@SerializedName("date")
	private Date date;

	@SerializedName("meta")
	private Meta meta;

	@SerializedName("timings")
	private Timings timings;

	public Date getDate(){
		return date;
	}

	public Meta getMeta(){
		return meta;
	}

	public Timings getTimings(){
		return timings;
	}
}