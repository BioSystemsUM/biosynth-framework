package pt.uminho.sysbio.biosynthframework.biodb.chebi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="chebi_metabolite_name")
public class ChebiMetaboliteNameEntity {
	
	@Id
	@GeneratedValue
	@Column(name="id", nullable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private ChebiMetaboliteEntity chebiMetaboliteEntity;
	
	@Column(name="name", nullable=false)
	private String name;
	
	@Column(name="type", nullable=false)
	private String type;
	
	@Column(name="source", nullable=false)
	private String source;
	
	@Column(name="adapted", nullable=false)
	private String adapted;
	
	@Column(name="language", nullable=false)
	private String language;
	
	
	
	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getSource() {
		return source;
	}



	public void setSource(String source) {
		this.source = source;
	}



	public String getAdapted() {
		return adapted;
	}



	public void setAdapted(String adapted) {
		this.adapted = adapted;
	}



	public String getLanguage() {
		return language;
	}



	public void setLanguage(String language) {
		this.language = language;
	}



	public ChebiMetaboliteEntity getChebiMetaboliteEntity() {
		return chebiMetaboliteEntity;
	}



	public void setChebiMetaboliteEntity(ChebiMetaboliteEntity chebiMetaboliteEntity) {
		this.chebiMetaboliteEntity = chebiMetaboliteEntity;
	}



	@Override
	public String toString() {
		final char sep = ',';
		final char ini = '<';
		final char end = '>';
		StringBuilder sb = new StringBuilder();
		sb.append(ini);
		sb.append(id).append(sep);
		sb.append(name).append(sep);
		sb.append(type).append(sep);
		sb.append(source).append(sep);
		sb.append(adapted).append(sep);
		sb.append(language);
		sb.append(end);
		return sb.toString();
	}
}
