package com.gillsoft.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.gillsoft.model.Locality;
import com.gillsoft.model.ScheduleRoutePoint;

@Entity
@Table(name = "route_path")
@JsonInclude(Include.NON_EMPTY)
public class PathPoint implements Serializable {
	
	private static final long serialVersionUID = 5462024691720016389L;

	@Id
	private int id;
	
	@Column(name = "route_id")
	private int routeId;

	private short index;
	
	@Column(name = "geo_point_id")
	private Integer geoPointId;
	
	@Column(name = "geo_locality_id")
	private Integer geoLocalityId;
	
	@Column(name = "depart_day")
	private short departDay;
	
	@Column(name = "depart_time")
	@Temporal(TemporalType.TIME)
	private Date departTime;
	
	@Column(name = "arrive_day")
	private short arriveDay;
	
	@Column(name = "arrive_time")
	@Temporal(TemporalType.TIME)
	private Date arriveTime;
	
	private String platform;
	
	private short distance;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
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

	public Integer getGeoLocalityId() {
		return geoLocalityId;
	}

	public void setGeoLocalityId(Integer geoLocalityId) {
		this.geoLocalityId = geoLocalityId;
	}

	public short getDepartDay() {
		return departDay;
	}

	public void setDepartDay(short departDay) {
		this.departDay = departDay;
	}

	public Date getDepartTime() {
		return departTime;
	}

	public void setDepartTime(Date departTime) {
		this.departTime = departTime;
	}

	public short getArriveDay() {
		return arriveDay;
	}

	public void setArriveDay(short arriveDay) {
		this.arriveDay = arriveDay;
	}

	public Date getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(Date arriveTime) {
		this.arriveTime = arriveTime;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public short getDistance() {
		return distance;
	}

	public void setDistance(short distance) {
		this.distance = distance;
	}
	
	public ScheduleRoutePoint create() {
		ScheduleRoutePoint point = new ScheduleRoutePoint();
		point.setId(String.valueOf(getId()));
		point.setIndex(getIndex());
		point.setDistance((int) getDistance());
		point.setPlatform(getPlatform());
		point.setDepartureTime(getDepartTime().toString());
		point.setArrivalTime(getArriveTime().toString());
		point.setArrivalDay((int) getArriveDay());
		point.setLocality(new Locality(String.valueOf(getGeoPointId())));
		point.getLocality().setParent(new Locality(String.valueOf(getGeoLocalityId())));
		return point;
	}
	
}
