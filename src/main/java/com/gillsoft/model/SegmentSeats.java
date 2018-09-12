package com.gillsoft.model;

import java.io.Serializable;

public class SegmentSeats implements Serializable {

	private static final long serialVersionUID = -6284286048329711419L;
	
	private Locality from;
	private Locality to;
	private int seats;

	public SegmentSeats() {
		
	}

	public SegmentSeats(Locality from, Locality to, int seats) {
		this.from = from;
		this.to = to;
		this.seats = seats;
	}

	public Locality getFrom() {
		return from;
	}

	public void setFrom(Locality from) {
		this.from = from;
	}

	public Locality getTo() {
		return to;
	}

	public void setTo(Locality to) {
		this.to = to;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

}
