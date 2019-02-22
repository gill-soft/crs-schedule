package com.gillsoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "agent_carrier")
public class AgentCarrier implements Serializable {

	private static final long serialVersionUID = -5335067144587462295L;
	
	@Id
	private int id;
	
	@Column(name = "agent_id")
	private int agentId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "carrier_id", nullable = false)
	@Fetch(FetchMode.JOIN)
	private Carrier carrier;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public Carrier getCarrier() {
		return carrier;
	}

	public void setCarrier(Carrier carrier) {
		this.carrier = carrier;
	}

}
