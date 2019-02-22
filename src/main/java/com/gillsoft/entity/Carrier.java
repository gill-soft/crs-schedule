package com.gillsoft.entity;

import java.util.Collections;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "organization_carrier")
public class Carrier extends BaseOrganisation {
	
	private static final long serialVersionUID = -3542920526134201413L;
	
	@Column(name = "contact_passenger_phone")
	private String contactPhone;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "carrier")
	@JsonIgnore
	private Set<AgentCarrier> carriers;

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	
	public Set<AgentCarrier> getCarriers() {
		return carriers;
	}

	public void setCarriers(Set<AgentCarrier> carriers) {
		this.carriers = carriers;
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
