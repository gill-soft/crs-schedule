package com.gillsoft.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.gillsoft.model.Price;
import com.gillsoft.model.RoutePoint;

@JsonInclude(Include.NON_NULL)
public class ScheduleRoutePoint extends RoutePoint {

	private static final long serialVersionUID = 2251927102270402861L;
	
	private short index;
	
	private List<ScheduleRoutePoint> destinations;
	
	private Price price;

	public short getIndex() {
		return index;
	}

	public void setIndex(short index) {
		this.index = index;
	}

	public List<ScheduleRoutePoint> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<ScheduleRoutePoint> destinations) {
		this.destinations = destinations;
	}

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	} 

}
