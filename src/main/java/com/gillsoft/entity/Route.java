package com.gillsoft.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gillsoft.model.Organisation;
import com.gillsoft.model.Regularity;
import com.gillsoft.model.RouteKind;
import com.gillsoft.model.RouteType;
import com.gillsoft.model.ScheduleRoute;
import com.gillsoft.model.Vehicle;
import com.gillsoft.util.StringUtil;

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
	
	private String type;
	
	private String kind;
	
	@Column(name = "started_at")
	@Temporal(TemporalType.DATE)
	private Date startedAt;
	
	@Column(name = "ended_at")
	@Temporal(TemporalType.DATE)
	private Date endedAt;
	
	private boolean available;
	
	private String regularity;
	
	@Column(name = "regularity_days")
	private String jsonRegularityDays;
	
	@Column(name = "insurance_code")
	private String insuranceCode;
	
	@Column(name = "sales_depth")
	private short salesDepth;
	
	@Column(name = "reservation_time")
	private short reservationTime;
	
	@Column(name = "bus_code")
	private String busCode;
	
	private String seats;
	
	@Column(name = "is_test")
	private boolean test;
	
	@Column(name = "sale_close_before")
	private int saleCloseBefore;
	
	@Column(name = "reservation_close_before")
	private int reservationCloseBefore;
	
	@Column(name = "deleted_at")
	private Date deletedAt;
	
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
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "route_id")
	private Set<RouteAgentBlock> agentBlocks;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
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

	public String getJsonRegularityDays() {
		return jsonRegularityDays;
	}
	
	public List<Integer> getRegularityDays() {
		try {
			return new ObjectMapper().readValue(jsonRegularityDays, new TypeReference<List<Integer>>() {});
		} catch (IOException e) {
			return null;
		}
	}

	public void setJsonRegularityDays(String jsonRegularityDays) {
		this.jsonRegularityDays = jsonRegularityDays;
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

	public boolean isTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
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

	public Date getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Date deletedAt) {
		this.deletedAt = deletedAt;
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
	
	public Set<RouteAgentBlock> getAgentBlocks() {
		return agentBlocks;
	}

	public void setAgentBlocks(Set<RouteAgentBlock> agentBlocks) {
		this.agentBlocks = agentBlocks;
	}

	public ScheduleRoute create() {
		ScheduleRoute scheduleRoute = new ScheduleRoute();
		scheduleRoute.setId(String.valueOf(getId()));
		scheduleRoute.setNumber(getCode());
		scheduleRoute.setType(RouteType.valueOf(getType().toUpperCase()));
		scheduleRoute.setKind(RouteKind.valueOf(getKind().toUpperCase()));
		scheduleRoute.setStartedAt(getStartedAt());
		scheduleRoute.setEndedAt(getEndedAt());
		scheduleRoute.setRegularity(Regularity.valueOf(getRegularity().replaceAll(" ", "_").toUpperCase()));
		scheduleRoute.setRegularityDays(getRegularityDays());
		scheduleRoute.setVehicle(new Vehicle(StringUtil.md5(getBusCode())));
		if (getTags() != null) {
			scheduleRoute.setAdditionals(new HashMap<>());
			scheduleRoute.getAdditionals().put("tags", getTags());
		}
		scheduleRoute.setCarrier(new Organisation(getCarrierCode()));
		scheduleRoute.setInsurance(new Organisation(getInsuranceCode()));
		scheduleRoute.setCurrency(getTariffs().iterator().next()
				.getGrids().iterator().next().getCurrency());
		return scheduleRoute;
	}
	
}
