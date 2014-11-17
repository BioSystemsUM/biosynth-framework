package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.strategy.AbstractNeo4jClusteringStrategy;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundNodeLabel;

public class BiocycFirstDegreeCrossreferenceClusteringStrategy extends AbstractNeo4jClusteringStrategy {

	public BiocycFirstDegreeCrossreferenceClusteringStrategy(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
		initialNodeLabel = CompoundNodeLabel.MetaCyc;
	}
	
	private String getIsotopeFormula(Node node) {
		Set<Node> formulaNodes = new HashSet<> ();
		for (Relationship relationship : node.getRelationships(MetaboliteRelationshipType.HasMolecularFormula)) {
			formulaNodes.add(relationship.getOtherNode(node));
		}
		
		if (formulaNodes.size() > 1) System.out.println("More than one formula");
		
		for (Node formulaNode : formulaNodes) {
			if (formulaNode.hasLabel(MetabolitePropertyLabel.MolecularFormula)) {
				for (Relationship relationship : formulaNode
						.getRelationships(MetaboliteRelationshipType.Isomorphic)) {
					Node isotopeFormulaNode = relationship.getOtherNode(formulaNode);
					String isotopeFormula = (String) isotopeFormulaNode.getProperty("formula");
					if (!isotopeFormula.trim().isEmpty()) return isotopeFormula.trim();
				}
			}
		}
		return null;
	}
	
	@Override
	public Set<Long> execute() {
		Set<Long> ids = new HashSet<> ();
		String inchi = this.initialNode.hasProperty("inchi")?(String)this.initialNode.getProperty("inchi"):null;
//		String formula = this.initialNode.hasProperty("formula")?(String)this.initialNode.getProperty("inchi"):null;
		String formula = this.getIsotopeFormula(initialNode);
		Integer charge = this.initialNode.hasProperty("charge")?(Integer)this.initialNode.getProperty("charge"):null;
		for (Relationship relationship : this.initialNode
				.getRelationships(MetaboliteRelationshipType.HasCrossreferenceTo)) {
			
			Node other = relationship.getOtherNode(initialNode);
			String otherInchi = other.hasProperty("inchi")?(String)other.getProperty("inchi"):null;
//			String otherFormula = other.hasProperty("formula")?(String)this.initialNode.getProperty("inchi"):null;
			String otherFormula = this.getIsotopeFormula(other);
			Integer otherCharge = other.hasProperty("charge")?(Integer)other.getProperty("charge"):null;
			
			if (!((boolean)other.getProperty("proxy"))) {
				if (inchi != null && inchi.equals(otherInchi)) {
					ids.add(other.getId());
				}
				if (formula != null && charge != null
						&& formula.equals(otherFormula)
						&& charge.equals(otherCharge)) {
					ids.add(other.getId());
				} 
			}
		}
		
		if (!ids.isEmpty()) ids.add(this.initialNode.getId());
		
		return ids;
	}

}
