package com.gillsoft.entity;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.gillsoft.model.Lang;

@Entity
@Table(name = "geo_locality")
public class Locality extends LocationPosition {

	private static final long serialVersionUID = 4498819873772572583L;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "geo_locality_id")
	private Set<LocalityI18n> i18n;

	public Set<LocalityI18n> getI18n() {
		return i18n;
	}

	public void setI18n(Set<LocalityI18n> i18n) {
		this.i18n = i18n;
	}
	
	public com.gillsoft.model.Locality create() {
		com.gillsoft.model.Locality locality = super.create();
		for (LocalityI18n localityI18n : i18n) {
			Lang lang = Lang.valueOf(localityI18n.getLocale().toString().toUpperCase());
			if (lang != null) {
				locality.setName(lang, localityI18n.getName());
			}
		}
		return locality;
	}

}
