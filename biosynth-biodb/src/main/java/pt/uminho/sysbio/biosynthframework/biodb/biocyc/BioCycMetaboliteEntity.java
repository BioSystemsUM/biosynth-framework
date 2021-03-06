package pt.uminho.sysbio.biosynthframework.biodb.biocyc;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.annotations.Charge;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@Entity
@Table(name="biocyc_metabolite")
public class BioCycMetaboliteEntity extends GenericMetabolite {
	
	private static final long serialVersionUID = 1L;
	
	@MetaProperty
	@Column(name="frame_id") private String frameId;
	public String getFrameId() { return frameId;}
	public void setFrameId(String frameId) { this.frameId = frameId;}

	@MetaProperty
	@Column(name="MOLW") private Double molWeight;
	public Double getMolWeight() { return molWeight;}
	public void setMolWeight(Double molWeight) { this.molWeight = molWeight;}
	
	@MetaProperty
	@Column(name="CMLMOLW") private Double cmlMolWeight;
	public Double getCmlMolWeight() { return cmlMolWeight;}
	public void setCmlMolWeight(Double cmlMolWeight) { this.cmlMolWeight = cmlMolWeight;}

	@MetaProperty
	@Column(name="INCHI", length=16383) private String inchi;
	public String getInchi() { return inchi;}
	public void setInChI(String inchi) { this.inchi = inchi;}

	@MetaProperty
	@Column(name="SMILES", length=16383) private String smiles;
	public String getSmiles() { return smiles;}
	public void setSmiles(String smiles) { this.smiles = smiles;}

	@MetaProperty
	@Column(name="GIBBS") private Double gibbs;
	public Double getGibbs() { return gibbs;}
	public void setGibbs(Double gibbs) { this.gibbs = gibbs;}
	
	@Charge
	@MetaProperty
	@Column(name="CHARGE") private Integer charge;
	public Integer getCharge() { return charge; }
	public void setCharge(Integer charge) { this.charge = charge; }
	
	@MetaProperty
	@Column(name="B_COMMENT", length=16383) private String comment;
	public String getComment() { return comment;}
	public void setComment(String comment) { this.comment = comment;}
	
	
	@ElementCollection @LazyCollection(LazyCollectionOption.EXTRA)
	@CollectionTable(name="BIOCYC_METABOLITE_SUBCLASS", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="SUBCLASS", length=63)
	protected List<String> subclasses = new ArrayList<> ();
	public List<String> getSubclasses() { return subclasses;}
	public void setSubclasses(List<String> subclasses) { this.subclasses = subclasses;}

	@ElementCollection @LazyCollection(LazyCollectionOption.EXTRA)
	@CollectionTable(name="BIOCYC_METABOLITE_PARENT", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="PARENT", length=63)
	protected List<String> parents = new ArrayList<> ();
	public List<String> getParents() { return parents;}
	public void setParents(List<String> parents) { this.parents = parents;}

	@ElementCollection @LazyCollection(LazyCollectionOption.EXTRA)
	@CollectionTable(name="BIOCYC_METABOLITE_INSTANCE", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="INSTANCE", length=63)
	protected List<String> instances = new ArrayList<> ();
	public List<String> getInstances() { return instances;}
	public void setInstances(List<String> instances) { this.instances = instances;}

	@ElementCollection @LazyCollection(LazyCollectionOption.EXTRA)
	@CollectionTable(name="BIOCYC_METABOLITE_REACTION", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="REACTION", length=63)
	protected List<String> reactions = new ArrayList<> ();
	public List<String> getReactions() { return reactions;}
	public void setReactions(List<String> reactions) { this.reactions = reactions;}
	
	@ElementCollection @LazyCollection(LazyCollectionOption.EXTRA)
	@CollectionTable(name="BIOCYC_METABOLITE_SYNONYM", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="SYNONYM", length=1023)
	private List<String> synonyms = new ArrayList<> ();
	public List<String> getSynonyms() { return synonyms;}
	public void setSynonyms(List<String> synonyms) { this.synonyms = synonyms;}

	@LazyCollection(LazyCollectionOption.EXTRA)
	@OneToMany(mappedBy = "biocycMetaboliteEntity", cascade = CascadeType.ALL)
	private List<BioCycMetaboliteCrossreferenceEntity> crossReferences = new ArrayList<>();
	public List<BioCycMetaboliteCrossreferenceEntity> getCrossreferences() { return crossReferences; }
	public void setCrossReferences(List<BioCycMetaboliteCrossreferenceEntity> crossReferences) {
		this.crossReferences = crossReferences;
		if (crossReferences != null)
		for (BioCycMetaboliteCrossreferenceEntity crossReference : crossReferences) 
			crossReference.setBiocycMetaboliteEntity(this);
	}
	public void addCrossReference(BioCycMetaboliteCrossreferenceEntity crossReference) {
		crossReference.setBiocycMetaboliteEntity(this);
		this.crossReferences.add(crossReference);
	}
	
	@LazyCollection(LazyCollectionOption.EXTRA)
	@OneToMany(mappedBy = "biocycMetaboliteEntity", cascade = CascadeType.ALL)
	private List<BiocycMetaboliteRegulationEntity> regulations = new ArrayList<>();
	public List<BiocycMetaboliteRegulationEntity> getRegulations() { return regulations;}
    public void setRegulations(List<BiocycMetaboliteRegulationEntity> regulations) {
      this.regulations = regulations;
      for (BiocycMetaboliteRegulationEntity regulation : regulations) {
        regulation.setBiocycMetaboliteEntity(this);
      }
    }
    
    @Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(sep);
		sb.append("comment:").append(this.comment).append(sep);
		sb.append("molWeight:").append(this.molWeight).append(sep);
		sb.append("cmlMolWeight:").append(this.cmlMolWeight).append(sep);
		sb.append("charge:").append(this.charge).append(sep);
		sb.append("gibbs:").append(this.gibbs).append(sep);
		sb.append("Smiles:").append(this.smiles).append(sep);
		sb.append("InChI:").append(this.inchi).append(sep);
		sb.append("Reactions:").append(this.reactions).append(sep);
		sb.append("Synonyms:").append(this.synonyms).append(sep);
		sb.append("Parents:").append(this.parents).append(sep);
		sb.append("Subclasses:").append(this.subclasses).append(sep);
		sb.append("Instances:").append(this.instances).append(sep);
		sb.append("Crossreferences:").append(this.crossReferences);
		return sb.toString();
	}
}
