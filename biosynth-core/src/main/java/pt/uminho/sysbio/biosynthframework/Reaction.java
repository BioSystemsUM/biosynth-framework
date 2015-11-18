package pt.uminho.sysbio.biosynthframework;

import java.util.Map;



public interface Reaction {
	public Long getId();
	public String getEntry();
	public String getName();
	
	public Boolean isTranslocation();
	public Orientation getOrientation();
	public Map<String, Double> getLeftStoichiometry();
	public void setLeftStoichiometry(Map<String, Double> left);
	
	public Map<String, Double> getRightStoichiometry();
	public void setRightStoichiometry(Map<String, Double> right);
	
	public Map<String, Double> getStoichiometry();
	public void setStoichiometry(Map<String, Double> map);
//	public List<StoichiometryPair> getLeft();
//	public Map<M, Double> getRight();
}
