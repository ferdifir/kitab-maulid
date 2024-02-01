package com.sherdle.webtoapp.service.api.response.schedule;

import com.google.gson.annotations.SerializedName;

public class Method{

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	@SerializedName("params")
	private Params params;

	public String getName(){
		return name;
	}

	public int getId(){
		return id;
	}

	public Params getParams(){
		return params;
	}
}