package pt.uminho.sysbio.biosynthframework.biodb.seed;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;

@Entity
@Table(name="seed_metabolite_crossreference")
public class SeedMetaboliteCrossreferenceEntity extends GenericCrossReference {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private SeedMetaboliteEntity seedCompoundEntity;
	
	public SeedMetaboliteCrossreferenceEntity() {
		super(null, null, null);
	}
	public SeedMetaboliteCrossreferenceEntity(Type type, String reference, String value) {
		super(type, reference, value);
	}
	public SeedMetaboliteCrossreferenceEntity(GenericCrossReference crossReference) {
		super(crossReference.getType(), crossReference.getRef(), crossReference.getValue());
	}

	public SeedMetaboliteEntity getSeedCompoundEntity() {
		return seedCompoundEntity;
	}
	public void setSeedCompoundEntity(SeedMetaboliteEntity seedCompoundEntity) {
		this.seedCompoundEntity = seedCompoundEntity;
	}
}
