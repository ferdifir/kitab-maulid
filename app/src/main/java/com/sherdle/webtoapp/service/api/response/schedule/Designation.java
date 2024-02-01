package com.sherdle.webtoapp.service.api.response.schedule;

import com.google.gson.annotations.SerializedName;

public class Designation{

	@SerializedName("expanded")
	private String expanded;

	@SerializedName("abbreviated")
	private String abbreviated;

	public String getExpanded(){
		return expanded;
	}

	public String getAbbreviated(){
		return abbreviated;
	}
}