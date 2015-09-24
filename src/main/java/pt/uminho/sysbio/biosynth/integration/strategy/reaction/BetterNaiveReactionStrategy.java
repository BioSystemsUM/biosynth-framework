package pt.uminho.sysbio.biosynth.integration.strategy.reaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.IntegrationUtils;
import pt.uminho.sysbio.biosynthframework.GenericReaction;

public class BetterNaiveReactionStrategy extends LesserNaiveReactionStrategy {

	private final static Logger LOGGER = LoggerFactory.getLogger(BetterNaiveReactionStrategy.class);
	
	private double offsetScore = 0.0;
	
	public BetterNaiveReactionStrategy(
			GraphDatabaseService graphDatabaseService,
			Map<Long, Long> metaboliteUnificationMap) {
		super(graphDatabaseService, metaboliteUnificationMap);
	}
	
	public double getOffsetScore() { return offsetScore;}
	public void setOffsetScore(double offsetScore) { this.offsetScore = offsetScore;}

	
	public Set<Long> mapReactions(GenericReaction pivot, Set<Long> left, Set<Long> right) {
		List<GenericReaction> reactionSelection = new ArrayList<> ();
		Set<Long> superResult = super.mapReactions(pivot, left, right, reactionSelection);
		if (superResult.isEmpty()) return superResult;
//		superResult.add(initialNode.getId());
		
		int size = reactionSelection.size();
		double[][] scoreMatrix = new double[size][size];
		Map<Long, Set<Long>> matchingSets = new HashMap<> ();
		double max = 0.0;
		
		LOGGER.debug("Setup Similarity Matrix");
		
		for (int i = 0; i < size; i++) {
			int a = i;
			
			GenericReaction rxnA = reactionSelection.get(a);
			Set<Long> matches = new HashSet<> ();
			matches.add(rxnA.getId());
			matchingSets.put(rxnA.getId(), matches);
			
			for (int j = i + 1; j < size; j++) {
				int b = j;
				
				GenericReaction rxnB = reactionSelection.get(b);
				double score = similarityScore(rxnA, rxnB);
				scoreMatrix[i][j] = score;
				scoreMatrix[j][i] = score;
				max = score > max ? score : max;
			}
		}
		
		LOGGER.debug("Maximum Similarity Score: " + max);
		
		LOGGER.debug("Allow Cluster from score: " + (max - offsetScore));
		
		for (int i = 0; i < size; i++) {
			GenericReaction rxnA = reactionSelection.get(i);
			Long id = rxnA.getId();
			for (int j = 0; j < size; j++) {
				if (scoreMatrix[i][j] >= max - offsetScore) {
					GenericReaction rxnB = reactionSelection.get(j);
					matchingSets.get(id).add(rxnB.getId());
				}
			}
		}
		
		reactionSelection.clear();
		return matchingSets.get(pivot.getId());
	}
	
	@Override
	public Set<Long> execute() {
		Set<Long> superResult = super.execute();
		
		if (superResult.isEmpty()) return superResult;
		superResult.add(initialNode.getId());
		
		int size = reactions.size();
		double[][] scoreMatrix = new double[size][size];
		Map<Long, Set<Long>> matchingSets = new HashMap<> ();
		double max = 0.0;
		
		LOGGER.debug("Setup Similarity Matrix");
		
		for (int i = 0; i < size; i++) {
			int a = i;
			
			GenericReaction rxnA = reactions.get(a);
			Set<Long> matches = new HashSet<> ();
			matches.add(rxnA.getId());
			matchingSets.put(rxnA.getId(), matches);
			
			for (int j = i + 1; j < size; j++) {
				int b = j;
				
				GenericReaction rxnB = reactions.get(b);
				double score = similarityScore(rxnA, rxnB);
				scoreMatrix[i][j] = score;
				scoreMatrix[j][i] = score;
				max = score > max ? score : max;
			}
		}
		
		LOGGER.debug("Maximum Similarity Score: " + max);
		
		LOGGER.debug("Allow Cluster from score: " + (max - offsetScore));
		
		for (int i = 0; i < size; i++) {
			GenericReaction rxnA = reactions.get(i);
			Long id = rxnA.getId();
			for (int j = 0; j < size; j++) {
				if (scoreMatrix[i][j] >= max - offsetScore) {
					GenericReaction rxnB = reactions.get(j);
					matchingSets.get(id).add(rxnB.getId());
				}
			}
		}
		
		reactions.clear();
//		System.out.println(matchingSets);
		return matchingSets.get(initialNode.getId());
		
//		int bestMatchSize = 0;
//		Long bestMatchKey = null;
//		for (Long id : matchingSets.keySet()) {
//			if (matchingSets.get(id).size() > bestMatchSize) {
//				bestMatchSize = matchingSets.get(id).size();
//				bestMatchKey = id;
//			}
//		}
//		
//		LOGGER.debug("Largest Match Set: " + matchingSets.get(bestMatchKey).toString());
//		
//		
//		return matchingSets.get(bestMatchKey);
	}
	
	private double similarityScore(GenericReaction rxnA, GenericReaction rxnB) {
		double l = IntegrationUtils.jaccard(rxnA.getLeftStoichiometry().keySet(), rxnB.getLeftStoichiometry().keySet());
		double r = IntegrationUtils.jaccard(rxnA.getRightStoichiometry().keySet(), rxnB.getRightStoichiometry().keySet());
		
//		if (l + r >= 2.0) System.out.println(rxnA.getEntry() + " EQ " + rxnB.getEntry());
		
		return l + r;
	}
}
