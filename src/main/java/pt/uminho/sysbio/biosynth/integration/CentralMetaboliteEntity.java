package pt.uminho.sysbio.biosynth.integration;

import java.util.ArrayList;
import java.util.List;

import edu.uminho.biosynth.core.components.Metabolite;

public class CentralMetaboliteEntity extends AbstractCentralEntity implements Metabolite {

	private List<CentralMetaboliteProxyEntity> crossreferences = new  ArrayList<> ();
	private List<CentralMetabolitePropertyEntity> propertyEntities = new ArrayList<> ();

	
	public void setEntry(String entry) {
		properties.put("entry", entry);
	};
//	
//	@Override
//	public void setFormula(String formula) {
//		this.formula = formula;
//		properties.put("formula", formula);
//	};
	
	public List<CentralMetaboliteProxyEntity> getCrossreferences() {
		return crossreferences;
	}
	public void setCrossreferences(
			List<CentralMetaboliteProxyEntity> crossreferences) {
		this.crossreferences = crossreferences;
	}
	public void addCrossreference(CentralMetaboliteProxyEntity crossreference) {
		this.crossreferences.add(crossreference);
	}

	public List<CentralMetabolitePropertyEntity> getPropertyEntities() {
		return propertyEntities;
	}
	public void setPropertyEntities(
			List<CentralMetabolitePropertyEntity> propertyEntities) {
		this.propertyEntities = propertyEntities;
	}
	public void addPropertyEntity(CentralMetabolitePropertyEntity propertyEntity) {
		if (propertyEntity != null)
			this.propertyEntities.add(propertyEntity);
	}

	@Override
	public String getName() {
		return (String)this.properties.get("name");
	}

	@Override
	public String getFormula() {
		return (String)this.properties.get("formula");
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("Strong Properties:\n");
		if (propertyEntities.isEmpty()) {
			sb.append("=========Empty=========\n");
		} else {
			for (CentralMetabolitePropertyEntity p : propertyEntities) {
				sb.append(p);
			}
		}
		sb.append("Crossreference Properties:\n");
		if (crossreferences.isEmpty()) {
			sb.append("=========Empty=========\n");
		} else {
			for (CentralMetaboliteProxyEntity x : crossreferences) {
				sb.append(x);
			}
		}
		return sb.toString();
	}
}
