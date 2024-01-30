package com.sherdle.webtoapp.service.api.response.date;

import com.google.gson.annotations.SerializedName;

public class HijrDateResponse{

	@SerializedName("code")
	private int code;

	@SerializedName("data")
	private Data data;

	@SerializedName("status")
	private String status;

	public int getCode(){
		return code;
	}

	public Data getData(){
		return data;
	}

	public String getStatus(){
		return status;
	}
}