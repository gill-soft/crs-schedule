package com.gillsoft.entity;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.gillsoft.model.Regularity;
import com.gillsoft.model.RouteKind;
import com.gillsoft.model.RouteType;
import com.gillsoft.model.ScheduleRoute;

@Entity
@Table(name = "route")
@JsonInclude(Include.NON_EMPTY)
public class Route implements Serializable {
	
	private static final long serialVersionUID = -3416104767678662352L;

	@Id
	private int id;
	
	private String code;
	
	@Column(name = "carrier_code")
	private String carrierCode;
	
	@Enumerated(EnumType.STRING)
	private RouteType type;
	
	@Enumerated(EnumType.STRING)
	private RouteKind kind;
	
	@Column(name = "started_at")
	@Temporal(TemporalType.DATE)
	private Date startedAt;
	
	@Column(name = "ended_at")
	@Temporal(TemporalType.DATE)
	private Date endedAt;
	
	private boolean available;
	
	private String regularity;
	
	@Column(name = "regularity_days")
	private String regularityDays;
	
	@Column(name = "insurance_code")
	private String insuranceCode;
	
	@Column(name = "sales_depth")
	private short salesDepth;
	
	@Column(name = "reservation_time")
	private short reservationTime;
	
	@Column(name = "bus_code")
	private String busCode;
	
	private String seats;
	
	@Column(name = "sale_close_before")
	private int saleCloseBefore;
	
	@Column(name = "reservation_close_before")
	private int reservationCloseBefore;
	
	@Column(name = "bus_number")
	private String busNumber;
	
	private String tags;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "route_id")
	@OrderBy("index")
	private Set<PathPoint> path;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "route_id")
	private Set<Tariff> tariffs;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

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

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getRegularity() {
		return regularity;
	}

	public void setRegularity(String regularity) {
		this.regularity = regularity;
	}

	public String getRegularityDays() {
		return regularityDays;
	}

	public void setRegularityDays(String regularityDays) {
		this.regularityDays = regularityDays;
	}

	public String getInsuranceCode() {
		return insuranceCode;
	}

	public void setInsuranceCode(String insuranceCode) {
		this.insuranceCode = insuranceCode;
	}

	public short getSalesDepth() {
		return salesDepth;
	}

	public void setSalesDepth(short salesDepth) {
		this.salesDepth = salesDepth;
	}

	public short getReservationTime() {
		return reservationTime;
	}

	public void setReservationTime(short reservationTime) {
		this.reservationTime = reservationTime;
	}

	public String getBusCode() {
		return busCode;
	}

	public void setBusCode(String busCode) {
		this.busCode = busCode;
	}

	public String getSeats() {
		return seats;
	}

	public void setSeats(String seats) {
		this.seats = seats;
	}

	public int getSaleCloseBefore() {
		return saleCloseBefore;
	}

	public void setSaleCloseBefore(int saleCloseBefore) {
		this.saleCloseBefore = saleCloseBefore;
	}

	public int getReservationCloseBefore() {
		return reservationCloseBefore;
	}

	public void setReservationCloseBefore(int reservationCloseBefore) {
		this.reservationCloseBefore = reservationCloseBefore;
	}

	public String getBusNumber() {
		return busNumber;
	}

	public void setBusNumber(String busNumber) {
		this.busNumber = busNumber;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Set<PathPoint> getPath() {
		return path;
	}

	public void setPath(Set<PathPoint> path) {
		this.path = path;
	}

	public Set<Tariff> getTariffs() {
		return tariffs;
	}

	public void setTariffs(Set<Tariff> tariffs) {
		this.tariffs = tariffs;
	}
	
	public ScheduleRoute create() {
		ScheduleRoute scheduleRoute = new ScheduleRoute();
		scheduleRoute.setId(String.valueOf(getId()));
		scheduleRoute.setNumber(getCode());
		scheduleRoute.setType(getType());
		scheduleRoute.setKind(getKind());
		scheduleRoute.setStartedAt(getStartedAt());
		scheduleRoute.setEndedAt(getEndedAt());
		scheduleRoute.setRegularity(Regularity.getEnum(getRegularity()));
		scheduleRoute.setBus(getBusCode());
		scheduleRoute.setTags(getTags());
		scheduleRoute.setCarrier(getCarrierCode());
		scheduleRoute.setInsurance(getInsuranceCode());
		scheduleRoute.setPath(new ArrayList<>());
		scheduleRoute.setCurrency(getTariffs().iterator().next()
				.getGrids().iterator().next().getCurrency());
		return scheduleRoute;
	}
	
}
