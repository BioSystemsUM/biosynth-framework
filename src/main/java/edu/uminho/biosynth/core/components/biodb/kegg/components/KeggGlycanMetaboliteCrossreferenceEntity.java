package edu.uminho.biosynth.core.components.biodb.kegg.components;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggGlycanMetaboliteEntity;

@Entity
@Table(name="kegg_glycan_metabolite_crossreference")
public class KeggGlycanMetaboliteCrossreferenceEntity extends GenericCrossReference {

	private static final long serialVersionUID = -5573712793876140763L;
	
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private KeggGlycanMetaboliteEntity keggGlycanMetaboliteEntity;
	public KeggGlycanMetaboliteEntity getKeggGlycanMetaboliteEntity() { return keggGlycanMetaboliteEntity; }
	public void setKeggGlycanMetaboliteEntity(KeggGlycanMetaboliteEntity keggGlycanMetaboliteEntity) {
		this.keggGlycanMetaboliteEntity = keggGlycanMetaboliteEntity;
	}
	
	public KeggGlycanMetaboliteCrossreferenceEntity() { super(null, null, null); }
	public KeggGlycanMetaboliteCrossreferenceEntity(Type type, String reference, String value) {
		super(type, reference, value);
	}
}
