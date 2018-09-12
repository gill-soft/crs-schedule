package com.gillsoft.entity;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import com.gillsoft.model.Locale;

@MappedSuperclass
public class LocationI18n extends Location {
	
	private static final long serialVersionUID = -8747653848045828164L;

	@Enumerated(EnumType.STRING)
	private Locale locale;

	private String name;

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
