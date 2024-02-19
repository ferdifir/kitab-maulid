package com.sherdle.webtoapp.service.api.response.schedule;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("11")
	private List<MonthData> november;

	@SerializedName("1")
	private List<MonthData> january;

	@SerializedName("12")
	private List<MonthData> december;

	@SerializedName("2")
	private List<MonthData> february;

	@SerializedName("3")
	private List<MonthData> march;

	@SerializedName("4")
	private List<MonthData> april;

	@SerializedName("5")
	private List<MonthData> may;

	@SerializedName("6")
	private List<MonthData> june;

	@SerializedName("7")
	private List<MonthData> july;

	@SerializedName("8")
	private List<MonthData> august;

	@SerializedName("9")
	private List<MonthData> september;

	@SerializedName("10")
	private List<MonthData> october;

	public void setNovember(List<MonthData> november){
		this.november = november;
	}

	public List<MonthData> getNovember(){
		return november;
	}

	public void setJanuary(List<MonthData> january){
		this.january = january;
	}

	public List<MonthData> getJanuary(){
		return january;
	}

	public void setDecember(List<MonthData> december){
		this.december = december;
	}

	public List<MonthData> getDecember(){
		return december;
	}

	public void setFebruary(List<MonthData> february){
		this.february = february;
	}

	public List<MonthData> getFebruary(){
		return february;
	}

	public void setMarch(List<MonthData> march){
		this.march = march;
	}

	public List<MonthData> getMarch(){
		return march;
	}

	public void setApril(List<MonthData> april){
		this.april = april;
	}

	public List<MonthData> getApril(){
		return april;
	}

	public void setMay(List<MonthData> may){
		this.may = may;
	}

	public List<MonthData> getMay(){
		return may;
	}

	public void setJune(List<MonthData> june){
		this.june = june;
	}

	public List<MonthData> getJune(){
		return june;
	}

	public void setJuly(List<MonthData> july){
		this.july = july;
	}

	public List<MonthData> getJuly(){
		return july;
	}

	public void setAugust(List<MonthData> august){
		this.august = august;
	}

	public List<MonthData> getAugust(){
		return august;
	}

	public void setSeptember(List<MonthData> september){
		this.september = september;
	}

	public List<MonthData> getSeptember(){
		return september;
	}

	public void setOctober(List<MonthData> october){
		this.october = october;
	}

	public List<MonthData> getOctober(){
		return october;
	}
}