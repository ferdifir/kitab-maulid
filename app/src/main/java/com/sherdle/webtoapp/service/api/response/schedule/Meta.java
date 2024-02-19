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

	public void setMethod(Method method){
		this.method = method;
	}

	public Method getMethod(){
		return method;
	}

	public void setOffset(Offset offset){
		this.offset = offset;
	}

	public Offset getOffset(){
		return offset;
	}

	public void setSchool(String school){
		this.school = school;
	}

	public String getSchool(){
		return school;
	}

	public void setTimezone(String timezone){
		this.timezone = timezone;
	}

	public String getTimezone(){
		return timezone;
	}

	public void setMidnightMode(String midnightMode){
		this.midnightMode = midnightMode;
	}

	public String getMidnightMode(){
		return midnightMode;
	}

	public void setLatitude(Object latitude){
		this.latitude = latitude;
	}

	public Object getLatitude(){
		return latitude;
	}

	public void setLongitude(Object longitude){
		this.longitude = longitude;
	}

	public Object getLongitude(){
		return longitude;
	}

	public void setLatitudeAdjustmentMethod(String latitudeAdjustmentMethod){
		this.latitudeAdjustmentMethod = latitudeAdjustmentMethod;
	}

	public String getLatitudeAdjustmentMethod(){
		return latitudeAdjustmentMethod;
	}
}