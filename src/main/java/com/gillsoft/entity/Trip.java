package com.gillsoft.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "trip")
public class Trip implements Serializable {
	
	private static final long serialVersionUID = -4691421760310767659L;

	@Id
	private int id;
	
	@Column(name = "route_id")
	private int routeId;
	
	@Column(name = "carrier_code")
	private String carrierCode;
	
	@Column(name = "execution_date")
	@Temporal(TemporalType.DATE)
	private Date execution;
	
	private boolean available;
	
	@Column(name = "deleted_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date deletedAt;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "trip_id")
	@OrderBy("index")
	private List<TripPath> path;
	
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

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public Date getExecution() {
		return execution;
	}

	public void setExecution(Date execution) {
		this.execution = execution;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public Date getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Date deletedAt) {
		this.deletedAt = deletedAt;
	}

	public List<TripPath> getPath() {
		return path;
	}

	public void setPath(List<TripPath> path) {
		this.path = path;
	}
	
}
