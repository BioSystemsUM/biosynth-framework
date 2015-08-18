package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.GenericCrossreference;
import pt.uminho.sysbio.biosynthframework.ReferenceType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="kegg_compound_metabolite_crossreference")
public class KeggCompoundMetaboliteCrossreferenceEntity extends GenericCrossreference {

	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private KeggCompoundMetaboliteEntity keggMetaboliteEntity;
	
	public KeggCompoundMetaboliteEntity getKeggMetaboliteEntity() { return keggMetaboliteEntity; }
	public void setKeggMetaboliteEntity(KeggCompoundMetaboliteEntity keggMetaboliteEntity) {
		this.keggMetaboliteEntity = keggMetaboliteEntity;
	}
	
	public KeggCompoundMetaboliteCrossreferenceEntity() { super(null, null, null); }
	public KeggCompoundMetaboliteCrossreferenceEntity(ReferenceType type, String reference, String value) {
		super(type, reference, value);
	}
	

}
