package com.domin.pojo;

public class BarChart{

	private String countryName;

	private String alpha3Code;

	private Integer borderCount;

	public BarChart(String countryName, String alpha3Code, Integer borderCount) {
		super();
		this.countryName = countryName;
		this.alpha3Code = alpha3Code;
		this.borderCount = borderCount;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getAlpha3Code() {
		return alpha3Code;
	}

	public void setAlpha3Code(String alpha3Code) {
		this.alpha3Code = alpha3Code;
	}

	public Integer getBorderCount() {
		return borderCount;
	}

	public void setBorderCount(Integer borderCount) {
		this.borderCount = borderCount;
	}

	@Override
	public String toString() {
		return "BarChart [countryName=" + countryName + ", alpha3Code=" + alpha3Code + ", borderCount=" + borderCount
				+ "]";
	}
}
