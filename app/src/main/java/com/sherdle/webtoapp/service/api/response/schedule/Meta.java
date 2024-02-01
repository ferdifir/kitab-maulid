package com.sherdle.webtoapp.service.api.response.schedule;

import com.google.gson.annotations.SerializedName;

public class Meta{

	@SerializedName("method")
	private Method method;

	@SerializedName("offset")
	private Offset offset;

	@SerializedName("school")
	private String school;

	@SerializedName("timezone")
	private String timezone;

	@SerializedName("midnightMode")
	private String midnightMode;

	@SerializedName("latitude")
	private Object latitude;

	@SerializedName("longitude")
	private Object longitude;

	@SerializedName("latitudeAdjustmentMethod")
	private String latitudeAdjustmentMethod;

	public Method getMethod(){
		return method;
	}

	public Offset getOffset(){
		return offset;
	}

	public String getSchool(){
		return school;
	}

	public String getTimezone(){
		return timezone;
	}

	public String getMidnightMode(){
		return midnightMode;
	}

	public Object getLatitude(){
		return latitude;
	}

	public Object getLongitude(){
		return longitude;
	}

	public String getLatitudeAdjustmentMethod(){
		return latitudeAdjustmentMethod;
	}
}