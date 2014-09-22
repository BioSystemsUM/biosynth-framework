package edu.uminho.biosynth.core.components.biodb.biocyc.components;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;

@Entity
@Table(name="BIOCYC_METABOLITE_CROSSREF")
public class BioCycMetaboliteCrossreferenceEntity extends GenericCrossReference{

	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private BioCycMetaboliteEntity biocycMetaboliteEntity;
	public BioCycMetaboliteEntity getBiocycMetaboliteEntity() { return biocycMetaboliteEntity; }
	public void setBiocycMetaboliteEntity(BioCycMetaboliteEntity biocycMetaboliteEntity) {
		this.biocycMetaboliteEntity = biocycMetaboliteEntity;
	}
	
	@Column(name="relationship") private String relationship;
	public String getRelationship() { return relationship;}
	public void setRelationship(String relationship) { this.relationship = relationship;}
	
	@Column(name="url") private String url;
	public String getUrl() { return url;}
	public void setUrl(String url) { this.url = url;}
	
	public BioCycMetaboliteCrossreferenceEntity() { super(null, null, null); }
	public BioCycMetaboliteCrossreferenceEntity(Type type, String reference, String value) {
		super(type, reference, value);
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
		sb.append(relationship).append(sep);
		sb.append(url);
		sb.append(end);
		return sb.toString();
	}
}
