package edu.uminho.biosynth.core.components.biodb.mnx.components;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.mnx.MnxMetaboliteEntity;

@Entity
@Table(name="MNX_METABOLITE_CROSSREF")
public class MnxMetaboliteCrossReferenceEntity extends GenericCrossReference {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="ID_METABOLITE")
	private MnxMetaboliteEntity mnxMetaboliteEntity;
	
	@Column(name="EVIDENCE") private String evidence;
	@Column(name="DESCRIPTION") private String description;
	
	public MnxMetaboliteCrossReferenceEntity() {
		super(null, null, null);
	}
	public MnxMetaboliteCrossReferenceEntity(Type type, String reference,
			String value) {
		super(type, reference, value);
	}
	
	public MnxMetaboliteEntity getMnxMetaboliteEntity() {
		return mnxMetaboliteEntity;
	}
	public void setMnxMetaboliteEntity(MnxMetaboliteEntity mnxMetaboliteEntity) {
		this.mnxMetaboliteEntity = mnxMetaboliteEntity;
	}
	
	public String getEvidence() {
		return evidence;
	}
	public void setEvidence(String evidence) {
		this.evidence = evidence;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		final char sep = ',';
		final char ini = '<';
		final char end = '>';
		StringBuilder sb = new StringBuilder();
		sb.append(ini);
		sb.append(type).append(sep);
		sb.append(ref).append(sep);
		sb.append(value).append(sep);
		sb.append(evidence);
		sb.append(end);
		return sb.toString();
	}

}
