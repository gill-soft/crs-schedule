package com.gillsoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Location implements Serializable {
	
	private static final long serialVersionUID = 5270066532625323382L;

	@Id
	private int id;
	
	@Column(name = "geo_country_id")
	private Integer geoCountryId;
	
	@Column(name = "geo_region_id")
	private Integer geoRegionId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getGeoCountryId() {
		return geoCountryId;
	}

	public void setGeoCountryId(Integer geoCountryId) {
		this.geoCountryId = geoCountryId;
	}

	public Integer getGeoRegionId() {
		return geoRegionId;
	}

	public void setGeoRegionId(Integer geoRegionId) {
		this.geoRegionId = geoRegionId;
	}
	
}
