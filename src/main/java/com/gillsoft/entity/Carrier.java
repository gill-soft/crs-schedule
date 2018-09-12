package com.gillsoft.entity;

import java.util.Collections;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "organization_carrier")
public class Carrier extends BaseOrganisation {
	
	private static final long serialVersionUID = -3542920526134201413L;
	
	@Column(name = "contact_passenger_phone")
	private String contactPhone;

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	
	@Override
	public com.gillsoft.model.Organisation create() {
		com.gillsoft.model.Organisation organisation = super.create();
		if (getContactPhone() != null
				&& !getContactPhone().isEmpty()) {
			organisation.setPhones(Collections.singletonList(getContactPhone()));
		}
		return organisation;
	}

}
