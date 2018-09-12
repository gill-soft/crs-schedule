package com.gillsoft.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "geo_point_i18n")
public class PointI18n extends LocationI18n {
	
	private static final long serialVersionUID = -6251713372802341064L;

	@Column(name = "geo_point_id")
	private Integer geoPointId;
	
	@Column(name = "geo_locality_id")
	private Integer geoLocalityId;
	
	private String address;

	public Integer getGeoPointId() {
		return geoPointId;
	}

	public void setGeoPointId(Integer geoPointId) {
		this.geoPointId = geoPointId;
	}

	public Integer getGeoLocalityId() {
		return geoLocalityId;
	}

	public void setGeoLocalityId(Integer geoLocalityId) {
		this.geoLocalityId = geoLocalityId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
