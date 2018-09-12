package com.gillsoft.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Schedule implements Serializable {
	
	private static final long serialVersionUID = -5643434464341993622L;

	private String id;
	
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date time;

	private Map<String, Organisation> organisations;

	private Map<String, Locality> localities;

	private Map<String, Locality> parents;
	
	private List<Route> routes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Map<String, Organisation> getOrganisations() {
		return organisations;
	}

	public void setOrganisations(Map<String, Organisation> organisations) {
		this.organisations = organisations;
	}

	public Map<String, Locality> getLocalities() {
		return localities;
	}

	public void setLocalities(Map<String, Locality> localities) {
		this.localities = localities;
	}

	public Map<String, Locality> getParents() {
		return parents;
	}

	public void setParents(Map<String, Locality> parents) {
		this.parents = parents;
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}
	
}
