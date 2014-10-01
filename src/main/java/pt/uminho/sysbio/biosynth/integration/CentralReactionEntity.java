package pt.uminho.sysbio.biosynth.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uminho.biosynth.core.components.Orientation;
import edu.uminho.biosynth.core.components.Reaction;

public class CentralReactionEntity extends AbstractCentralEntity implements Reaction {
	
	private Map<CentralMetaboliteProxyEntity, Double> left = new HashMap<> ();
	private Map<CentralMetaboliteProxyEntity, Double> right = new HashMap<> ();
	public List<CentralReactionProxyEntity> crossreferences = new ArrayList<> ();
	
	@Override
	public String getEntry() { return (String)this.properties.get("entry");}
	public void setEntry(String entry) { properties.put("entry", entry);};
	
	@Override
	public Orientation getOrientation() {
		return Orientation.valueOf((String) this.properties.get("orientation"));
	}
	public void setOrientation(Orientation orientation) { properties.put("orientation", orientation);}
	
	public Map<CentralMetaboliteProxyEntity, Double> getLeft() { return left;}
	public void setLeft(Map<CentralMetaboliteProxyEntity, Double> left) { this.left = left;}
	
	public Map<CentralMetaboliteProxyEntity, Double> getRight() { return right;}
	public void setRight(Map<CentralMetaboliteProxyEntity, Double> right) { this.right = right;}
	
	public List<CentralReactionProxyEntity> getCrossreferences() {
		return crossreferences;
	}
	public void setCrossreferences(List<CentralReactionProxyEntity> crossreferences) {
		this.crossreferences = crossreferences;
	}
//	
//	@Override
//	public void setReactantStoichiometry(Map<String,Double> reactantStoichiometry) {
//		super.setReactantStoichiometry(reactantStoichiometry);
//	};
//	
//	@Override
//	public void setProductStoichiometry(Map<String, Double> productStoichiometry) {
//		super.setProductStoichiometry(productStoichiometry);
//	}
	

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("Left Metabolites:\n");
		if (left.isEmpty()) {
			sb.append("=========Empty=========\n");
		} else {
			for (CentralMetaboliteProxyEntity l : left.keySet()) {
				sb.append(String.format("[%f,\n%s]\n", left.get(l), l));
//				sb.append(l.getLeft().getClass().getSimpleName()).append("\n")
//				  .append(p.getLeft()).append(" => \n")
//				  .append(p.getRight().getClass().getSimpleName()).append("\n")
//				  .append(p.getRight());
			}
		}
		if (right.isEmpty()) {
			sb.append("=========Empty=========\n");
		} else {
			for (CentralMetaboliteProxyEntity r : right.keySet()) {
				sb.append(String.format("[%f,\n%s]\n", right.get(r), r));
//				sb.append(l.getLeft().getClass().getSimpleName()).append("\n")
//				  .append(p.getLeft()).append(" => \n")
//				  .append(p.getRight().getClass().getSimpleName()).append("\n")
//				  .append(p.getRight());
			}
		}
		
		sb.append("Crossreference Properties:\n");
		if (crossreferences.isEmpty()) {
			sb.append("=========Empty=========\n");
		} else {
			for (CentralReactionProxyEntity x : crossreferences) {
				sb.append(x);
			}
		}
		
		return sb.toString();
//		StringBuilder sb = new StringBuilder(super.toString()).append("\n");
//		sb.append("MajorLabel: ").append(this.majorLabel).append("\n");
//		sb.append("Labels: ").append(this.labels).append("\n");
//		sb.append("Properties:\n");
//		for (String key : this.properties.keySet())
//			sb.append(String.format("\t%s: %s\n", key, this.properties.get(key)));
//		return sb.toString();
	}



}
