package com.domin.pojo;

public class ChinaBorderShare {

	private String countryName;

	private String region;

	private Double area;

	private Long population;

	private String flagSvgUrl;

	public ChinaBorderShare(String countryName, String region, Double area, Long population, String flagSvgUrl) {
		super();
		this.countryName = countryName;
		this.region = region;
		this.area = area;
		this.population = population;
		this.flagSvgUrl = flagSvgUrl;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
		this.area = area;
	}

	public Long getPopulation() {
		return population;
	}

	public void setPopulation(Long population) {
		this.population = population;
	}

	public String getFlagSvgUrl() {
		return flagSvgUrl;
	}

	public void setFlagSvgUrl(String flagSvgUrl) {
		this.flagSvgUrl = flagSvgUrl;
	}
}
