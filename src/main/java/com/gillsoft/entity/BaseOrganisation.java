package com.gillsoft.entity;

import java.io.Serializable;
import java.util.Collections;

import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
public class BaseOrganisation implements Serializable {
	
	private static final long serialVersionUID = -2464020789841137726L;

	@Id
	private int id;
	
	private String code;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	@JsonIgnore
	private Organisation organisation;

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

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}
	
	public com.gillsoft.model.Organisation create() {
		com.gillsoft.model.Organisation organisation = new com.gillsoft.model.Organisation();
		organisation.setName(getOrganisation().getName());
		organisation.setAddress(getOrganisation().getAddress());
		if (getOrganisation().getPhone() != null
				&& !getOrganisation().getPhone().isEmpty()) {
			organisation.setPhones(Collections.singletonList(getOrganisation().getPhone()));
		}
		if (getOrganisation().getEmail() != null
				&& !getOrganisation().getEmail().isEmpty()) {
			organisation.setEmails(Collections.singletonList(getOrganisation().getEmail()));
		}
		return organisation;
	}

}
