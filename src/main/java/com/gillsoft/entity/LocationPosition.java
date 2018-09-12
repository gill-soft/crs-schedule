package com.gillsoft.entity;

import java.math.BigDecimal;

import javax.persistence.MappedSuperclass;

import com.gillsoft.model.Locality;

@MappedSuperclass
public class LocationPosition extends Location {

	private static final long serialVersionUID = 8669371811305642288L;

	private Byte type;
	
	private BigDecimal latitude;
	
	private BigDecimal longitude;

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	
	public Locality create() {
		Locality locality = new Locality();
		locality.setLatitude(getLatitude());
		locality.setLongitude(getLongitude());
		return locality;
	}
	
}
