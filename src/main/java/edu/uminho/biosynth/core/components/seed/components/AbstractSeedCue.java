package edu.uminho.biosynth.core.components.seed.components;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractSeedCue implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @Column(name="ID")
    @GeneratedValue
    private Integer id;
	
	@Column(name="COUNT") private float count;
	@Column(name="CUE_UUID") private String cue_uuid;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public float getCount() {
		return count;
	}
	public void setCount(float count) {
		this.count = count;
	}
	
	public String getCue_uuid() {
		return cue_uuid;
	}
	public void setCue_uuid(String cue_uuid) {
		this.cue_uuid = cue_uuid;
	}
	
	@Override
	public String toString() {
		final char sep = ',';
		final char ini = '<';
		final char end = '>';
		StringBuilder sb = new StringBuilder();
		sb.append(ini);
		sb.append("count:").append(count).append(sep);
		sb.append("cue_uuid:").append(cue_uuid);
		sb.append(end);
		return sb.toString();
	}
}
