package com.gillsoft.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "route_path_blocked")
public class RouteBlock implements Serializable {

	private static final long serialVersionUID = -7548286462034120071L;
	
	@Id
	private int id;
	
	@Column(name = "route_id")
	private int routeId;
	
	@Column(name = "depart_path_from", nullable = true)
	private Integer departFrom;
	
	@Column(name = "depart_path_to", nullable = true)
	private Integer departTo;
	
	@Column(name = "arrive_path_from", nullable = true)
	private Integer arriveFrom;
	
	@Column(name = "arrive_path_to", nullable = true)
	private Integer arriveTo;
	
	@Column(name = "started_at", nullable = true)
	@Temporal(TemporalType.DATE)
	private Date startedAt;
	
	@Column(name = "ended_at", nullable = true)
	@Temporal(TemporalType.DATE)
	private Date endedAt;
	
	private String regularity;
	
	@Column(name = "regularity_days", nullable = true)
	private String jsonRegularityDays;

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

	public Integer getDepartFrom() {
		return departFrom;
	}

	public void setDepartFrom(Integer departFrom) {
		this.departFrom = departFrom;
	}

	public Integer getDepartTo() {
		return departTo;
	}

	public void setDepartTo(Integer departTo) {
		this.departTo = departTo;
	}

	public Integer getArriveFrom() {
		return arriveFrom;
	}

	public void setArriveFrom(Integer arriveFrom) {
		this.arriveFrom = arriveFrom;
	}

	public Integer getArriveTo() {
		return arriveTo;
	}

	public void setArriveTo(Integer arriveTo) {
		this.arriveTo = arriveTo;
	}

	public Date getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}

	public Date getEndedAt() {
		return endedAt;
	}

	public void setEndedAt(Date endedAt) {
		this.endedAt = endedAt;
	}

	public String getRegularity() {
		return regularity;
	}

	public void setRegularity(String regularity) {
		this.regularity = regularity;
	}

	public String getJsonRegularityDays() {
		return jsonRegularityDays;
	}

	public void setJsonRegularityDays(String jsonRegularityDays) {
		this.jsonRegularityDays = jsonRegularityDays;
	}

}
