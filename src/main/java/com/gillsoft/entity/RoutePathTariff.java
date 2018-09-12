package com.gillsoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "route_path_tariff")
@JsonInclude(Include.NON_EMPTY)
public class RoutePathTariff implements Serializable {
	
	private static final long serialVersionUID = 449348457236590764L;

	@Id
	private int id;
	
	@Column(name = "route_path_id_from")
	private int routeFromId;
	
	@Column(name = "route_path_id_to")
	private int routeToId;
	
	private Double value;
	
	private Double vat;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRouteFromId() {
		return routeFromId;
	}

	public void setRouteFromId(int routeFromId) {
		this.routeFromId = routeFromId;
	}

	public int getRouteToId() {
		return routeToId;
	}

	public void setRouteToId(int routeToId) {
		this.routeToId = routeToId;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Double getVat() {
		return vat;
	}

	public void setVat(Double vat) {
		this.vat = vat;
	}

}
