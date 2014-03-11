package edu.uminho.biosynth.core.components.biodb.chebi.components;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.biodb.chebi.ChebiDumpMetaboliteEntity;

@Entity
@Table(name="reference")
public class ChebiDumpMetaboliteReferenceEntity {
//	CREATE TABLE `reference` (
//			  `id` INT NOT NULL,
//			  `compound_id` INT NOT NULL,
//			  `reference_id` VARCHAR(60) NOT NULL,
//			  `reference_db_name` VARCHAR(60) NOT NULL,
//			  `location_in_ref` VARCHAR(90),
//			  `reference_name` VARCHAR(512),
//			  PRIMARY KEY (`id`)
//			) ENGINE=InnoDB;
	@Id
	@Column(name="id", nullable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="compound_id")
	private ChebiDumpMetaboliteEntity chebiDumpMetaboliteEntity;
	
	@Column(name="reference_id", nullable=false, length=60)
	private String referenceId;
	
	@Column(name="reference_db_name", nullable=false, length=60)
	private String referenceDbName;
	
	@Column(name="location_in_ref", nullable=false, length=90)
	private String locationInRef;
	
	@Column(name="reference_name", nullable=false, length=512)
	private String referenceName;
	
	@Override
	public String toString() {
		final char sep = ',';
		final char ini = '<';
		final char end = '>';
		StringBuilder sb = new StringBuilder();
		sb.append(ini);
		sb.append(id).append(sep);
		sb.append(referenceId).append(sep);
		sb.append(referenceDbName).append(sep);
		sb.append(locationInRef).append(sep);
		sb.append(referenceName);
		sb.append(end);
		return sb.toString();
	}
}
