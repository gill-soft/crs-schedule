package com.gillsoft.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "tariff_grid")
@JsonInclude(Include.NON_EMPTY)
public class TariffGrid implements Serializable {
	
	private static final long serialVersionUID = -7590026325344565899L;

	@Id
	private int id;
	
	private String currency;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "tariff_grid_id")
	@OrderBy("value")
	private Set<RoutePathTariff> values;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Set<RoutePathTariff> getValues() {
		return values;
	}

	public void setValues(Set<RoutePathTariff> values) {
		this.values = values;
	}

}
