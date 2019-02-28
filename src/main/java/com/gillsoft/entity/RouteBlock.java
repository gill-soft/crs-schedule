package com.gillsoft.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gillsoft.model.Regularity;

@Entity
@Table(name = "route_path_blocked")
@JsonInclude(Include.NON_NULL)
public class RouteBlock implements Serializable {

	private static final long serialVersionUID = -7548286462034120071L;
	
	@Id
	@JsonIgnore
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
	
	@JsonIgnore
	private String regularity;
	
	@Column(name = "regularity_days", nullable = true)
	@JsonIgnore
	private String jsonRegularityDays;
	
	@Transient
	@JsonIgnore
	private Integer departFromIndex;
	
	@Transient
	@JsonIgnore
	private Integer departToIndex;
	
	@Transient
	@JsonIgnore
	private Integer arriveFromIndex;
	
	@Transient
	@JsonIgnore
	private Integer arriveToIndex;

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
	
	@JsonProperty("regularity")
	public Regularity getRegularityValue() {
		return Regularity.valueOf(getRegularity().replaceAll(" ", "_").toUpperCase());
	}

	public void setRegularity(String regularity) {
		this.regularity = regularity;
	}

	public String getJsonRegularityDays() {
		return jsonRegularityDays;
	}
	
	@JsonProperty("regularityDays")
	public Set<Integer> getRegularityDays() {
		if (jsonRegularityDays == null) {
			return null;
		}
		try {
			return new ObjectMapper().readValue(jsonRegularityDays, new TypeReference<Set<Integer>>() {});
		} catch (IOException e) {
			return null;
		}
	}

	public void setJsonRegularityDays(String jsonRegularityDays) {
		this.jsonRegularityDays = jsonRegularityDays;
	}

	public Integer getDepartFromIndex() {
		return departFromIndex;
	}

	public void setDepartFromIndex(Integer departFromIndex) {
		this.departFromIndex = departFromIndex;
	}

	public Integer getDepartToIndex() {
		return departToIndex;
	}

	public void setDepartToIndex(Integer departToIndex) {
		this.departToIndex = departToIndex;
	}

	public Integer getArriveFromIndex() {
		return arriveFromIndex;
	}

	public void setArriveFromIndex(Integer arriveFromIndex) {
		this.arriveFromIndex = arriveFromIndex;
	}

	public Integer getArriveToIndex() {
		return arriveToIndex;
	}

	public void setArriveToIndex(Integer arriveToIndex) {
		this.arriveToIndex = arriveToIndex;
	}

}
