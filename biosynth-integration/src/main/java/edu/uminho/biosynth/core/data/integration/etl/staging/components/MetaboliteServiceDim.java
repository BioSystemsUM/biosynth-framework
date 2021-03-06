package edu.uminho.biosynth.core.data.integration.etl.staging.components;

// Generated 14-Feb-2014 20:21:31 by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * MetaboliteServiceDim generated by hbm2java
 */
@Entity
@Table(name = "metabolite_service_dim")
public class MetaboliteServiceDim implements java.io.Serializable {

	private String serviceName;
	private String serviceVersion;
	private String dbType;
	private Set<MetaboliteStga> metaboliteStgas = new HashSet<MetaboliteStga>(0);

	public MetaboliteServiceDim() {
	}

	public MetaboliteServiceDim(String serviceName) {
		this.serviceName = serviceName;
	}

	public MetaboliteServiceDim(String serviceName, String serviceVersion,
			Set<MetaboliteStga> metaboliteStgas) {
		this.serviceName = serviceName;
		this.serviceVersion = serviceVersion;
		this.metaboliteStgas = metaboliteStgas;
	}

	@Id
	@Column(name = "service_name", unique = true, nullable = false)
	public String getServiceName() {
		return this.serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Column(name = "db_type")
	public String getDbType() {
		return this.dbType;
	}
	
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	@Column(name = "service_version")
	public String getServiceVersion() {
		return this.serviceVersion;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "metaboliteServiceDim")
	public Set<MetaboliteStga> getMetaboliteStgas() {
		return this.metaboliteStgas;
	}

	public void setMetaboliteStgas(Set<MetaboliteStga> metaboliteStgas) {
		this.metaboliteStgas = metaboliteStgas;
	}

}
