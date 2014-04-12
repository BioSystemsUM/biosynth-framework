package edu.uminho.biosynth.core.components;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class StoichiometryPair implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
    @Column(name="id")
    @GeneratedValue
	private Integer id;
	public Integer getId() { return id;}
	public void setId(Integer id) { this.id = id;}
	
    @Column(name="value") protected Double value;
	public Double getValue() { return value;}
	public void setValue(Double value) { this.value = value;}
	
    @Column(name="metabolite_id") protected Integer cpdKey;
    @Column(name="metabolite_entry") protected String cpdEntry;

	public Integer getCpdKey() {
		return cpdKey;
	}
	public void setCpdKey(Integer cpdKey) {
		this.cpdKey = cpdKey;
	}
	
	public String getCpdEntry() {
		return cpdEntry;
	}
	public void setCpdEntry(String cpdEntry) {
		this.cpdEntry = cpdEntry;
	}
	
	@Override
	public String toString() {
		final char sep = ',';
		final char ini = '<';
		final char end = '>';
		StringBuilder sb = new StringBuilder();
		sb.append(ini);
		sb.append(this.cpdEntry).append(sep);
		sb.append(this.value).append(sep);
		sb.append(end);
		return sb.toString();
	}
}
