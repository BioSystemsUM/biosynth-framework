package pt.uminho.sysbio.biosynthframework.metabolicmodel;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Deprecated
@MappedSuperclass
public abstract class AbstractMetabolicModel implements IMetabolicModel {
	
	@Id
	@Column(name="id", unique=true)
	protected String id;
	@Column(name="name")
	protected String name;
	
	public AbstractMetabolicModel(String id) { this.id = id;}

	public String getName() { return name;}
	public void setName(String name) { this.name = name;}

	public String getId() { return id;}
	public void setId(String id) { this.id = id;}
	
}
