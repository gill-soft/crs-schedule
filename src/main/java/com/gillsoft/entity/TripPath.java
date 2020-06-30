package com.gillsoft.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gillsoft.model.Locality;

@Entity
@Table(name = "trip_path")
public class TripPath implements Serializable {
	
	private static final long serialVersionUID = -5013074596211467887L;

	@Id
	private int id;
	
	private short index;
	
	@Column(name = "geo_point_id")
	private Integer geoPointId;
	
	@Column(name = "geo_locality_id")
	private int geoLocalityId;
	
	@Column(name = "route_path_id")
	private int routePathId;
	
	@Column(name = "depart_date")
	@Temporal(TemporalType.DATE)
	private Date departure;
	
	@Column(name = "depart_time")
	@Temporal(TemporalType.TIME)
	private Date departureTime;
	
	@Column(name = "seats", columnDefinition = "jsonb")
	@JsonIgnore
	private String jsonSeats;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public short getIndex() {
		return index;
	}

	public void setIndex(short index) {
		this.index = index;
	}

	public Integer getGeoPointId() {
		return geoPointId;
	}

	public void setGeoPointId(Integer geoPointId) {
		this.geoPointId = geoPointId;
	}

	public int getGeoLocalityId() {
		return geoLocalityId;
	}

	public void setGeoLocalityId(int geoLocalityId) {
		this.geoLocalityId = geoLocalityId;
	}

	public Map<String, String> getSeats() {
		try {
			return new ObjectMapper().readValue(jsonSeats, new TypeReference<Map<String, String>>() {});
		} catch (IOException e) {
			return null;
		}
	}

	public int getRoutePathId() {
		return routePathId;
	}

	public void setRoutePathId(int routePathId) {
		this.routePathId = routePathId;
	}

	public Date getDeparture() {
		return departure;
	}

	public void setDeparture(Date departure) {
		this.departure = departure;
	}

	public Date getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	public String getJsonSeats() {
		return jsonSeats;
	}

	public void setJsonSeats(String jsonSeats) {
		this.jsonSeats = jsonSeats;
	}
	
	public Locality createLocality() {
		Locality locality = new Locality(String.valueOf(getGeoPointId()));
		locality.setParent(new Locality(String.valueOf(getGeoLocalityId())));
		return locality;
	}
	
}
