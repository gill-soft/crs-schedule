package com.gillsoft.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.gillsoft.model.Lang;

@Entity
@Table(name = "geo_point")
public class Point extends LocationPosition {

	private static final long serialVersionUID = 3205300446379635750L;
	
	@Column(name = "geo_locality_id")
	private Integer geoLocalityId;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "geo_point_id")
	private Set<PointI18n> i18n;

	public Integer getGeoLocalityId() {
		return geoLocalityId;
	}

	public void setGeoLocalityId(Integer geoLocalityId) {
		this.geoLocalityId = geoLocalityId;
	}

	public Set<PointI18n> getI18n() {
		return i18n;
	}

	public void setI18n(Set<PointI18n> i18n) {
		this.i18n = i18n;
	}
	
	public com.gillsoft.model.Locality create() {
		com.gillsoft.model.Locality locality = super.create();
		for (PointI18n pointI18n : i18n) {
			Lang lang = Lang.valueOf(pointI18n.getLocale().toString().toUpperCase());
			if (lang != null) {
				locality.setName(lang, pointI18n.getName());
				locality.setAddress(lang, pointI18n.getAddress());
			}
		}
		return locality;
	}
	
}
