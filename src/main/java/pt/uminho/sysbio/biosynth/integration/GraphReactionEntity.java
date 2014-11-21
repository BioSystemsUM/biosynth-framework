package pt.uminho.sysbio.biosynth.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.Orientation;
import pt.uminho.sysbio.biosynthframework.Reaction;

public class GraphReactionEntity extends AbstractGraphEntity implements Reaction {
	
	private Map<GraphMetaboliteProxyEntity, Double> left = new HashMap<> ();
	private Map<GraphMetaboliteProxyEntity, Double> right = new HashMap<> ();
	public List<GraphReactionProxyEntity> crossreferences = new ArrayList<> ();
	
	@Override
	public String getEntry() { return (String)this.properties.get("entry");}
	public void setEntry(String entry) { properties.put("entry", entry);};
	
	@Override
	public String getName() {
		if (!this.properties.containsKey("name")) return null;
		return this.properties.get("name").toString();
	}
	
	@Override
	public Orientation getOrientation() {
		if (!this.properties.containsKey("orientation")) return null;
		return Orientation.valueOf((String) this.properties.get("orientation"));
	}
	public void setOrientation(Orientation orientation) { properties.put("orientation", orientation);}
	
	public Map<GraphMetaboliteProxyEntity, Double> getLeft() { return left;}
	public void setLeft(Map<GraphMetaboliteProxyEntity, Double> left) { this.left = left;}
	
	public Map<GraphMetaboliteProxyEntity, Double> getRight() { return right;}
	public void setRight(Map<GraphMetaboliteProxyEntity, Double> right) { this.right = right;}
	
	public List<GraphReactionProxyEntity> getCrossreferences() {
		return crossreferences;
	}
	public void setCrossreferences(List<GraphReactionProxyEntity> crossreferences) {
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
			for (GraphMetaboliteProxyEntity l : left.keySet()) {
				sb.append(String.format("[%f,\n%s]\n", left.get(l), l));
//				sb.append(l.getLeft().getClass().getSimpleName()).append("\n")
//				  .append(p.getLeft()).append(" => \n")
//				  .append(p.getRight().getClass().getSimpleName()).append("\n")
//				  .append(p.getRight());
			}
		}
		
		sb.append("Right Metabolites:\n");
		if (right.isEmpty()) {
			sb.append("=========Empty=========\n");
		} else {
			for (GraphMetaboliteProxyEntity r : right.keySet()) {
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
			for (GraphReactionProxyEntity x : crossreferences) {
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
	@Override
	public Map<String, Double> getLeftStoichiometry() {
		Map<String, Double> leftMap = new HashMap<> ();
		for (GraphMetaboliteProxyEntity entity : this.left.keySet()) {
			leftMap.put(entity.getId().toString(), this.left.get(entity));
		}
		return leftMap;
	}
	@Override
	public void setLeftStoichiometry(Map<String, Double> left) {
		this.left.keySet().clear();
		for (String entry : left.keySet()) {
			Long id = Long.parseLong(entry);
			GraphMetaboliteProxyEntity entity = new GraphMetaboliteProxyEntity();
			entity.setId(id);
//			entity.setEntry(entry);
			this.left.put(entity, left.get(entry));
		}
	}
	@Override
	public Map<String, Double> getRightStoichiometry() {
		Map<String, Double> rightMap = new HashMap<> ();
		for (GraphMetaboliteProxyEntity entity : this.right.keySet()) {
			rightMap.put(entity.getId().toString(), this.right.get(entity));
		}
		return rightMap;
	}
	@Override
	public void setRightStoichiometry(Map<String, Double> right) {
		this.right.keySet().clear();
		for (String entry : right.keySet()) {
			Long id = Long.parseLong(entry);
			GraphMetaboliteProxyEntity entity = new GraphMetaboliteProxyEntity();
			entity.setId(id);
			
			this.right.put(entity, left.get(entry));
		}
	}	
	

	
	@Override
	public boolean isTranslocation() {
		// TODO Auto-generated method stub
		return false;
	}




}
