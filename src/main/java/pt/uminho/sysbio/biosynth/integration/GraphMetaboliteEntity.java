package pt.uminho.sysbio.biosynth.integration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import pt.uminho.sysbio.biosynthframework.Metabolite;

public class GraphMetaboliteEntity extends AbstractGraphEntity implements Metabolite {

	private List<Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity>> crossreferences = new  ArrayList<> ();
	private List<Pair<GraphPropertyEntity, GraphRelationshipEntity>> propertyEntities = new ArrayList<> ();

//	
//	@Override
//	public void setFormula(String formula) {
//		this.formula = formula;
//		properties.put("formula", formula);
//	};
	

	public void addCrossreference(Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity> crossreferencePair) {
		this.crossreferences.add(crossreferencePair);
	}
	
	public void addCrossreference(GraphMetaboliteProxyEntity proxyEntity, GraphRelationshipEntity relationshipEntity) {
		Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity> xrefPair = new ImmutablePair<>(proxyEntity, relationshipEntity);
		this.crossreferences.add(xrefPair);
	}

	public List<Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity>> getCrossreferences() {
		return crossreferences;
	}

	public void setCrossreferences(
			List<Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity>> crossreferences) {
		this.crossreferences = crossreferences;
	}

	public List<Pair<GraphPropertyEntity, GraphRelationshipEntity>> getPropertyEntities() {
		return propertyEntities;
	}
	public void setPropertyEntities(
			List<Pair<GraphPropertyEntity, GraphRelationshipEntity>> propertyEntities) {
		this.propertyEntities = propertyEntities;
	}
	public void addPropertyEntity(Pair<GraphPropertyEntity, GraphRelationshipEntity> propertyEntity) {
		if (propertyEntity != null) {
			if (propertyEntity.getLeft() == null || propertyEntity.getRight() == null) {
				throw new RuntimeException();
			}
			this.propertyEntities.add(propertyEntity);
		}
	}
	
	@Override
	public String getEntry() { return (String)this.properties.get("entry");}
	public void setEntry(String entry) { properties.put("entry", entry);};

	@Override
	public String getName() { return (String)this.properties.get("name");}
	public void setName(String name) { this.properties.put("name", name);}
	
	@Override
	public String getFormula() { return (String)this.properties.get("formula");}
	public void setFormula(String formula) { this.properties.put("formula", formula);}
	
	public Boolean isProxy() {
		if (!this.properties.containsKey("proxy")) return null;
		return (boolean) this.properties.get("proxy");
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("\nStrong Properties:\n");
		if (propertyEntities.isEmpty()) {
			sb.append("=========Empty=========\n");
		} else {
			for (Pair<?, ?> p : propertyEntities) {
				sb.append("#").append(p.getLeft().getClass().getSimpleName()).append("#")
				  .append(p.getLeft()).append(" => ")
				  .append("#").append(p.getRight().getClass().getSimpleName()).append("#")
				  .append(p.getRight()).append("\n");
			}
		}
		sb.append("Crossreference Properties:\n");
		if (crossreferences.isEmpty()) {
			sb.append("=========Empty=========\n");
		} else {
			for (Pair<?, ?> p : crossreferences) {
				sb.append("#").append(p.getLeft().getClass().getSimpleName()).append("#")
				  .append(p.getLeft()).append(" => ")
				  .append("#").append(p.getRight().getClass().getSimpleName()).append("#")
				  .append(p.getRight()).append("\n");
			}
		}
		return sb.toString();
	}
}
