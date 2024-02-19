package com.sherdle.webtoapp.service.api.response.schedule;

import com.google.gson.annotations.SerializedName;

public class Designation{

	@SerializedName("expanded")
	private String expanded;

	@SerializedName("abbreviated")
	private String abbreviated;

	public void setExpanded(String expanded){
		this.expanded = expanded;
	}

	public String getExpanded(){
		return expanded;
	}

	public void setAbbreviated(String abbreviated){
		this.abbreviated = abbreviated;
	}

	public String getAbbreviated(){
		return abbreviated;
	}
}