package com.gillsoft.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class ScheduleRoute extends Route {

	private static final long serialVersionUID = -7254426063149819136L;
	
	private RouteType type;
	
	private RouteKind kind;
	
	private String carrier;
	
	private String insurance;
	
	private String bus;
	
	private Date startedAt;
	
	private Date endedAt;
	
	private Regularity regularity;
	
	private String regularityDays;
	
	private String tags;
	
	private String currency;

	public RouteType getType() {
		return type;
	}

	public void setType(RouteType type) {
		this.type = type;
	}

	public RouteKind getKind() {
		return kind;
	}

	public void setKind(RouteKind kind) {
		this.kind = kind;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getInsurance() {
		return insurance;
	}

	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}

	public String getBus() {
		return bus;
	}

	public void setBus(String bus) {
		this.bus = bus;
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

	public Regularity getRegularity() {
		return regularity;
	}

	public void setRegularity(Regularity regularity) {
		this.regularity = regularity;
	}

	public String getRegularityDays() {
		return regularityDays;
	}

	public void setRegularityDays(String regularityDays) {
		this.regularityDays = regularityDays;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
