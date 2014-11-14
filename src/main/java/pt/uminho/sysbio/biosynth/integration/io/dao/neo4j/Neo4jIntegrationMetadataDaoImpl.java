package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMember;
import pt.uminho.sysbio.biosynth.integration.IntegratedMember;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.AbstractNeo4jDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.IntegrationMetadataDao;
import edu.uminho.biosynth.core.data.integration.chimera.domain.CurationEdge;

public class Neo4jIntegrationMetadataDaoImpl extends AbstractNeo4jDao implements IntegrationMetadataDao {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jIntegrationMetadataDaoImpl.class);
	
	@Autowired
	public Neo4jIntegrationMetadataDaoImpl(
			GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
	}
	
	@Override
	public List<Long> getAllIntegrationSetsId() {
		List<Long> ids = new ArrayList<> ();
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(IntegrationNodeLabel.IntegrationSet)) {
			ids.add(node.getId());
		}
		return ids;
	}
	
	@Override
	public List<String> getAllIntegrationSetsEntries() {
		List<String> result = new ArrayList<> ();
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(IntegrationNodeLabel.IntegrationSet)) {
			result.add((String) node.getProperty("entry"));
		}
		return result;
	}

	@Override
	public IntegrationSet saveIntegrationSet(IntegrationSet integrationSet) {
		String cypher = String.format(
				"MERGE (iid:%s {entry:{entry}, description:{description}}) RETURN iid AS IID", 
				IntegrationNodeLabel.IntegrationSet);
		Map<String, Object> params = new HashMap<> ();
		params.put("entry", integrationSet.getName());
		params.put("description", nullToString(integrationSet.getDescription()));

		LOGGER.debug(String.format("Execute:%s with %s", cypher, params));
		Node node = this.getExecutionResultGetSingle("IID", this.executionEngine.execute(cypher, params));
		integrationSet.setId(node.getId());
		return integrationSet;
	}

	@Override
	public void deleteIntegrationSet(IntegrationSet integrationSet) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented !");
	}

	@Override
	public IntegrationSet getIntegrationSet(Long id) {
		Node node = this.graphDatabaseService.getNodeById(id);
		return this.nodeToIntegrationSet(node);
	}

	@Override
	public IntegrationSet getIntegrationSet(String entry) {
		String cypher = String.format(
				"MATCH (iid:%s {entry:{entry}) RETURN iid", 
				IntegrationNodeLabel.IntegrationSet);
		Map<String, Object> params = new HashMap<> ();
		params.put("entry", entry);
		ExecutionResult executionResult = this.executionEngine.execute(cypher, params);
		System.out.println(executionResult.dumpToString());
		throw new RuntimeException("Not implemented !");
//		return null;
	}

	@Override
	public IntegratedCluster getIntegratedClusterByEntry(final String entry,
			Long integrationSetId) {
		Node integrationSetNode = graphDatabaseService.getNodeById(integrationSetId);
		if (integrationSetNode == null || 
				!integrationSetNode.hasLabel(IntegrationNodeLabel.IntegrationSet)) 
			return null;
		
		Long nodeId = null;
		for (Path path : graphDatabaseService
				.traversalDescription()
				.depthFirst()
				.evaluator(Evaluators.toDepth(1))
				.evaluator(new Evaluator() {

					@Override
					public Evaluation evaluate(Path path) {
//						System.out.println(path.endNode().getProperty("entry", ""));
						if(path.endNode().getProperty("entry", "").equals(entry)){
							return Evaluation.INCLUDE_AND_CONTINUE;
						} else {
							return Evaluation.EXCLUDE_AND_CONTINUE;
						}
					}
				})
				.traverse(integrationSetNode)) {
			
			nodeId = path.endNode().getId();
		}
		
		return getIntegratedClusterById(nodeId);
	}

	@Override
	public IntegratedCluster getIntegratedClusterById(Long id) {
		Node cidNode = graphDatabaseService.getNodeById(id);
		IntegrationSet integrationSet = null;
		for (Relationship relationship : cidNode
				.getRelationships(IntegrationRelationshipType.IntegratedMetaboliteCluster)) {
			Node iidNode = relationship.getOtherNode(cidNode);
			if (integrationSet != null) {
				LOGGER.debug("SOME ERROR FIXME !");
			}
			integrationSet = this.getIntegrationSet(iidNode.getId());
		}
		IntegratedCluster integratedCluster = nodeToIntegratedMetaboliteCluster(cidNode);
		integratedCluster.setIntegrationSet(integrationSet);
		List<IntegratedClusterMember> integratedClusterMembers = new ArrayList<> ();
		for (Relationship relationship : cidNode
				.getRelationships(IntegrationRelationshipType.Integrates)) {
			Node eidNode = relationship.getOtherNode(cidNode);
			IntegratedMember integratedMember = nodeToIntegratedMetaboliteMember(eidNode);
			IntegratedClusterMember integratedClusterMember = new IntegratedClusterMember();
			integratedClusterMember.setMember(integratedMember);
			integratedClusterMember.setCluster(integratedCluster);
			integratedClusterMembers.add(integratedClusterMember);
		}
		integratedCluster.setMembers(integratedClusterMembers);
		
		return integratedCluster;
	}

	@Override
	public List<IntegratedCluster> getAllIntegratedClusters(
			Long integrationSetId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IntegratedCluster> getIntegratedClustersByPage(
			Long integrationSetId, int firstResult, int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IntegratedCluster> getIntegratedClusterByMemberIds(
			Long... memberIds) {
		
		return null;
	}

	@Override
	public List<Long> getAllIntegratedClusterIds(Long integrationSetId) {
		
		Node iidNode = graphDatabaseService.getNodeById(integrationSetId);
		if (iidNode == null || !iidNode.hasLabel(IntegrationNodeLabel.IntegrationSet)) return null;
		
		LOGGER.debug(String.format("Loading metabolite clusters for integration set [%d]", integrationSetId));
		List<Long> result = new ArrayList<> ();
		for (Relationship relationship : iidNode
				.getRelationships(IntegrationRelationshipType.IntegratedMetaboliteCluster)) {

			Node cidNode = relationship.getOtherNode(iidNode);
			result.add(cidNode.getId());
		}
		
		return result;
	}

	@Override
	public Set<Long> getAllIntegratedClusterIds(IntegrationSet integrationSetId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntegratedCluster saveIntegratedCluster(IntegratedCluster cluster) {
		if (cluster.getIntegrationSet() == null) {
			LOGGER.error("No integration set assigned to cluster");
			return null;
		}
		if (cluster.getClusterType() == null || cluster.getClusterType().isEmpty()) {
			LOGGER.error("Invalid Cluster Type");
			return null;
		}
		
		if (cluster.getIntegrationSet().getId() == null) {
			LOGGER.debug("Generating Integration Set");
			this.saveIntegrationSet(cluster.getIntegrationSet());
		}
		
		// Generate Members
		List<Node> members = new ArrayList<> ();
		for (IntegratedClusterMember clusterMember : cluster.getMembers()) {
			LOGGER.debug("Generating Member");
			IntegratedMember integratedMember = clusterMember.getMember(); 
			this.saveIntegratedMember(integratedMember);
			members.add(graphDatabaseService.getNodeById(integratedMember.getId()));
		}
		// Generate Cluster
		LOGGER.debug("Generating Cluster");
		Label clusterLabel = IntegrationNodeLabel.valueOf(cluster.getClusterType());
		String cypher = String.format(
				"MERGE (cid:%s {entry:{entry}, description:{description}}) RETURN cid AS CID", 
				clusterLabel);
		Map<String, Object> params = new HashMap<> ();
		params.put("entry", cluster.getEntry());
		params.put("description", nullToString(cluster.getDescription()));
		
		LOGGER.debug(String.format("Execute:%s with %s", cypher, params)); 
		Node clusterNode = getExecutionResultGetSingle("CID", this.executionEngine.execute(cypher, params));
		
		for (Relationship relationship : clusterNode.getRelationships()) {
			System.out.println(relationship.getId());
			System.out.println(relationship.getType());
		}
		
		for (Relationship relationship : clusterNode.getRelationships(IntegrationRelationshipType.Integrates)) {
			System.out.println("I was connected before !" + relationship.getOtherNode(clusterNode).getId());
		}
		
		cluster.setId(clusterNode.getId());
		// Create Links
		for (Node otherNode : members) {
			clusterNode.createRelationshipTo(otherNode, IntegrationRelationshipType.Integrates);
		}
		// Link to Set
		Node integrationSetNode = graphDatabaseService.getNodeById(cluster.getIntegrationSet().getId());
		integrationSetNode.createRelationshipTo(clusterNode, IntegrationRelationshipType.IntegratedMetaboliteCluster);
		
		return cluster;
	}

	@Override
	public void removeMembersFromIntegratedCluster(
			IntegratedCluster integratedCluster, Set<Long> toRemove) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void mergeCluster(List<Long> ids, Long integrationId) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void updateCluster(IntegratedCluster cluster) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteCluster(IntegratedCluster cluster) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLastClusterEntry(Long integrationSetId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteClusterMember(IntegratedClusterMember member) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Long> getAllIntegratedClusterMembersId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, String> getIntegratedClusterWithElement(Long iid, Long eid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntegratedMember getIntegratedMember(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntegratedMember getOrCreateIntegratedMember(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntegratedMember saveIntegratedMember(IntegratedMember member) {
		try {	
			Label memberLabel = IntegrationNodeLabel.valueOf(member.getMemberType());
			String cypher = String.format(
					"MERGE (eid:%s {id:{id}, description:{description}}) RETURN eid AS EID", 
					memberLabel);
			Map<String, Object> params = new HashMap<> ();
			params.put("id", member.getReferenceId());
			params.put("description", nullToString(member.getDescription()));
			
			LOGGER.debug(String.format("Execute:%s with %s", cypher, params));
			Node node = getExecutionResultGetSingle("EID", this.executionEngine.execute(cypher, params));
			member.setId(node.getId());
		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return member;
	}

	@Override
	public List<Long> getAllIntegratedMembersId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getAllIntegratedMembersId(IntegrationSet integrationSet,
			boolean distinct) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, Long> getAllAssignedIntegratedMembers(Long integrationSetId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countIntegratedMembers(IntegrationSet integrationSet,
			boolean distinct) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, Map<String, Integer>> countMeta(Long iid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveCurationEdge(CurationEdge curationEdge) {
		// TODO Auto-generated method stub
		
	}
	
	private IntegrationSet nodeToIntegrationSet(Node node) {
		if (node == null || !node.hasLabel(IntegrationNodeLabel.IntegrationSet)) return null;
		
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setId(node.getId());
		integrationSet.setDescription((String) node.getProperty("description"));
		integrationSet.setName((String) node.getProperty("entry"));
		return integrationSet;
	}
	
	private IntegratedCluster nodeToIntegratedMetaboliteCluster(Node node) {
		if (node == null || !node.hasLabel(IntegrationNodeLabel.MetaboliteCluster)) return null;
		
		IntegratedCluster integratedCluster = new IntegratedCluster();
		integratedCluster.setId(node.getId());
		integratedCluster.setDescription((String) node.getProperty("description"));
		integratedCluster.setEntry((String) node.getProperty("entry"));
		return integratedCluster;
	}
	
	private IntegratedMember nodeToIntegratedMetaboliteMember(Node node) {
		if (node == null || !node.hasLabel(IntegrationNodeLabel.MetaboliteMember)) return null;
		
		IntegratedMember integratedMember = new IntegratedMember();
		integratedMember.setId((Long)node.getProperty("id"));
		integratedMember.setDescription((String) node.getProperty("description"));
		return integratedMember;
	}
	
	private Node getExecutionResultGetSingle(String column, ExecutionResult executionResult) {
		if (executionResult == null) return null;
		
		Node node = null;
		for (Object object : IteratorUtil.asList(executionResult.columnAs(column))) {
			if (node != null) LOGGER.warn("Integrity failure. Not unique result.");
			node = (Node) object;
		}
		return node;
	}
	
	private Object nullToString(Object object) {
		if (object == null) return "null";
		return object;
	}

	
	@Override
	public String lookupClusterEntryByMemberId(Long iid, Long eid) {
//		Node memberNode = graphDatabaseService.findNodesByLabelAndProperty(label, key, value)
		
		//START i=node(0) MATCH (i)-[r1:IntegratedMetaboliteCluster]->(c:MetaboliteCluster)-[r2:Integrates]->(m {id:245779}) RETURN id(m) as id
		final String columnName = "id";
		final String iidToCidRelationship = IntegrationRelationshipType.IntegratedMetaboliteCluster.toString();
		final String cidToEidRelationship = IntegrationRelationshipType.Integrates.toString();
		final String integratedClusterLabel = IntegrationNodeLabel.MetaboliteCluster.toString();
		String query = String.format("START i=node({iid}) MATCH "
				+ "(i)-[r1:%s]->(c:%s)"
				+ "-[r2:%s]->(m {id:{eid}}) RETURN c.entry as id", 
				iidToCidRelationship, 
				integratedClusterLabel, 
				cidToEidRelationship, 
				columnName);
		
		Map<String, Object> params = new HashMap<> ();
		params.put("iid", iid);
		params.put("eid", eid);
		List<?> clusterEntryList = IteratorUtil.asList(executionEngine.execute(query, params).columnAs(columnName));
		
//		String query = String.format("MATCH (n {id:{id}}) RETURN id(n) AS %s", columnName);
//		Map<String, Object> params = new HashMap<> ();
//		params.put("id", eid);
//		
//		List<?> a = IteratorUtil.asList(executionEngine.execute(query, params).columnAs(columnName));
		
		if (clusterEntryList.isEmpty()) return null;
		if (clusterEntryList.size() > 1) {
			LOGGER.error("Duplicate: " + query + " :: " +  params);
			for (Object o : clusterEntryList) {
				System.out.println("--> [" + o + "]");
			}
		}
		
		String entry = (String) clusterEntryList.iterator().next();
		
		return entry;
	}

	@Override
	public Long lookupClusterIdByMemberId(Long iid, Long eid) {
		// TODO Auto-generated method stub
		return null;
	}


	
//	private Node executionEngineMerge() {
//		String cypher = String.format(
//				"MERGE (eid:%s {id:{id}, description:{description}}) RETURN iid", 
//				IntegrationLabel.MetaboliteMember);
//		Map<String, Object> params = new HashMap<> ();
//		params.put("id", member.getId());
//		params.put("description", member.getDescription());
//		
//		LOGGER.debug(String.format("Execute:%s with %s", cypher, params));
//		this.executionEngine.execute(cypher, params);
//	}
}
