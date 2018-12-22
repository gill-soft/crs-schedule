package com.gillsoft.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.gillsoft.model.TariffType;

@Entity
@Table(name = "tariff")
@JsonInclude(Include.NON_EMPTY)
public class Tariff implements Serializable {
	
	private static final long serialVersionUID = 7706894521670239971L;

	@Id
	private int id;
	
	private int status;
	
	@Column(name = "started_at")
	@Temporal(TemporalType.DATE)
	private Date startedAt;
	
	@Column(name = "ended_at")
	@Temporal(TemporalType.DATE)
	private Date endedAt;
	
	private String kind;
	
	@Enumerated(EnumType.STRING)
	private TariffType type;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "tariff_id")
	private Set<TariffGrid> grids;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public TariffType getType() {
		return type;
	}

	public void setType(TariffType type) {
		this.type = type;
	}

	public Set<TariffGrid> getGrids() {
		return grids;
	}

	public void setGrids(Set<TariffGrid> grids) {
		this.grids = grids;
	}

}
