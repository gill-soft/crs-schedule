package com.gillsoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "route_agent_blocked")
public class RouteAgentBlock implements Serializable {

	private static final long serialVersionUID = -3754248141732609578L;
	
	@Id
	private int id;
	
	@Column(name = "route_id")
	private int routeId;
	
	@Column(name = "agent_id")
	private int agentId;

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

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
	
}
