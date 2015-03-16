package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphEdgeEntity;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.GraphReactionProxyEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.AbstractNeo4jDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.ReactionHeterogeneousDao;

public class Neo4jGraphReactionDaoImpl 
extends AbstractNeo4jGraphDao<GraphReactionEntity>
implements ReactionHeterogeneousDao<GraphReactionEntity> {
	public static int RELATIONSHIP_TYPE_LIMIT = 20;
	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jGraphReactionDaoImpl.class);
	protected static final Label REACTION_LABEL = GlobalLabel.Reaction;
	protected static final RelationshipType LEFT_RELATIONSHIP = ReactionRelationshipType.left_component;
	protected static final RelationshipType RIGHT_RELATIONSHIP = ReactionRelationshipType.right_component;
	protected static final RelationshipType CROSSREFERENCE_RELATIONSHIP = 
			ReactionRelationshipType.has_crossreference_to;
	
	public Neo4jGraphReactionDaoImpl(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
	}
	
	@Override
	public GraphReactionEntity getReactionById(String tag, Serializable id) {
		Node node = graphDatabaseService.getNodeById(Long.parseLong(id.toString()));
		if (!node.hasLabel(GlobalLabel.Reaction)) return null;
		
		LOGGER.debug(String.format("Found %s:%s", node, Neo4jUtils.getLabels(node)));
		
		GraphReactionEntity reactionEntity = new GraphReactionEntity();
		
		reactionEntity.setId(node.getId());
		reactionEntity.setLabels(Neo4jUtils.getLabelsAsString(node));
		LOGGER.trace(String.format("Set Properties: %s", Neo4jUtils.getPropertiesMap(node)));
		reactionEntity.setProperties(Neo4jUtils.getPropertiesMap(node));
		
		reactionEntity.setLeft(getReactionMetabolites(node, ReactionRelationshipType.left_component));
		reactionEntity.setRight(getReactionMetabolites(node, ReactionRelationshipType.right_component));
		setupConnectedLinks(reactionEntity, node);
		String majorLabel = (String) reactionEntity.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, null);
		if (majorLabel == null) LOGGER.warn("Major label not found for %s:%s", node, Neo4jUtils.getLabels(node));
		reactionEntity.setMajorLabel(majorLabel);
		
		return reactionEntity;
	}
	
	private Map<GraphMetaboliteProxyEntity, Map<String, Object>> getReactionMetabolites(Node node, ReactionRelationshipType relationshipType) {
		Map<GraphMetaboliteProxyEntity, Map<String, Object>> map = new HashMap<> ();
		
		LOGGER.debug(String.format("Lookup relationship %s", relationshipType));
		for (Relationship relationship : node.getRelationships(relationshipType)) {
			Node other = relationship.getOtherNode(node);
			LOGGER.trace(String.format("Found %s:%s", other, Neo4jUtils.getLabels(other)));
			
			Map<String, Object> propertyContainer = 
					Neo4jUtils.getPropertiesMap(relationship);
			
			Long id = other.getId();
			String entry = (String) other.getProperty("entry", null);
			String majorLabel = (String) other.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, null);
			GraphMetaboliteProxyEntity entity = new GraphMetaboliteProxyEntity();
			entity.setId(id);
			entity.setEntry(entry);
			entity.setMajorLabel(majorLabel);
			
			map.put(entity, propertyContainer);
		}
		
		return map;
	}

	@Override
	public GraphReactionEntity getReactionByEntry(String tag, String entry) {
		ReactionMajorLabel majorLabel = ReactionMajorLabel.valueOf(tag);
		Node node = Neo4jUtils.getUniqueResult(graphDatabaseService
				.findNodesByLabelAndProperty(majorLabel, "entry", entry));
		
		if (node == null) {
			LOGGER.debug(String.format("Reaction [%s:%s] not found", tag, entry));
			return null;
		}
		
		LOGGER.debug(String.format("Found %s for %s:%s", node, tag, entry));
		
		return this.getReactionById("", node.getId());
	}

	@Override
	public GraphReactionEntity saveReaction(String tag,
			GraphReactionEntity reaction) {
		super.saveGraphEntity(reaction);
		Node node = graphDatabaseService.getNodeById(reaction.getId());
		node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
		for (GraphMetaboliteProxyEntity l : reaction.getLeft().keySet()) {
			LOGGER.debug(String.format("Resolving Left Link %s", l.getEntry()));
			this.createOrLinkToMetaboliteProxy(node, l, LEFT_RELATIONSHIP, reaction.getLeft().get(l));
		}
		for (GraphMetaboliteProxyEntity r : reaction.getRight().keySet()) {
			LOGGER.debug(String.format("Resolving Right Link %s", r.getEntry()));
			this.createOrLinkToMetaboliteProxy(node, r, RIGHT_RELATIONSHIP, reaction.getRight().get(r));
		}
		return reaction;
	}
	
	@Deprecated
	public GraphReactionEntity saveReaction_(String tag,
			GraphReactionEntity reaction) {
		
		boolean create = true;
		
		for (Node node : graphDatabaseService.findNodesByLabelAndProperty(
				DynamicLabel.label(reaction.getMajorLabel()), 
				"entry", 
				reaction.getEntry())) {
			create = false;
			LOGGER.debug(String.format("Found Previous node with entry:%s", reaction.getEntry()));
			LOGGER.debug(String.format("MODE:UPDATE %s", node));
			
			for (String label : reaction.getLabels())
				node.addLabel(DynamicLabel.label(label));
			for (String key : reaction.getProperties().keySet())
				node.setProperty(key, reaction.getProperties().get(key));
			
			for (GraphMetaboliteProxyEntity l : reaction.getLeft().keySet()) {
				LOGGER.debug(String.format("Resolving Left Link %s", l.getEntry()));
				this.createOrLinkToMetaboliteProxy(node, l, LEFT_RELATIONSHIP, reaction.getLeft().get(l));
			}
			for (GraphMetaboliteProxyEntity r : reaction.getRight().keySet()) {
				LOGGER.debug(String.format("Resolving Right Link %s", r.getEntry()));
				this.createOrLinkToMetaboliteProxy(node, r, RIGHT_RELATIONSHIP, reaction.getRight().get(r));
			}
//			for (GraphReactionProxyEntity x : reaction.getCrossreferences()) {
//				LOGGER.debug(String.format("Resolving Crossreference Link %s", x.getEntry()));
//				this.createOrLinkToReactionProxy(node, x, CROSSREFERENCE_RELATIONSHIP);
//			}
//			for (Map<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> l : reaction.links) {
//				AbstractGraphEdgeEntity relationship = l.keySet().iterator().next();
//				AbstractGraphNodeEntity entity = l.get(relationship);
//				LOGGER.debug(String.format("Resolving Additional Link %s", entity));
//				this.createOrLinkToNode(node, relationship, entity);
//			}
			
			node.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, reaction.getMajorLabel());
			node.setProperty("proxy", false);
			
			reaction.setId(node.getId());
		}
		
		if (create) {
			Node node = graphDatabaseService.createNode();
			LOGGER.debug(String.format("MODE:CREATE %s", node));
			
			node.addLabel(DynamicLabel.label(reaction.getMajorLabel()));
			
			for (String label : reaction.getLabels())
				node.addLabel(DynamicLabel.label(label));
			for (String key : reaction.getProperties().keySet())
				node.setProperty(key, reaction.getProperties().get(key));
			for (GraphMetaboliteProxyEntity l : reaction.getLeft().keySet()) {
				LOGGER.debug(String.format("Resolving Left Link %s", l.getEntry()));
				this.createOrLinkToMetaboliteProxy(node, l, LEFT_RELATIONSHIP, reaction.getLeft().get(l));
			}
			for (GraphMetaboliteProxyEntity r : reaction.getRight().keySet()) {
				LOGGER.debug(String.format("Resolving Right Link %s", r.getEntry()));
				this.createOrLinkToMetaboliteProxy(node, r, RIGHT_RELATIONSHIP, reaction.getRight().get(r));
			}
//			for (GraphReactionProxyEntity x : reaction.getCrossreferences()) {
//				LOGGER.debug(String.format("Resolving Crossreference Link %s", x.getEntry()));
//				this.createOrLinkToReactionProxy(node, x, CROSSREFERENCE_RELATIONSHIP);
//			}
			
			node.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, reaction.getMajorLabel());
			node.setProperty("proxy", false);
			
			reaction.setId(node.getId());
		}
		return reaction;
	}
	
	private void createOrLinkToNode(Node srcNode, AbstractGraphEdgeEntity edge, AbstractGraphNodeEntity dst) {
		Node dstNode = getOrCreateNode(dst.getMajorLabel(), dst.uniqueKey, dst.getProperty(dst.uniqueKey, null));
		for (String label : dst.getLabels()) {
			dstNode.addLabel(DynamicLabel.label(label));
		}
		for (String key : dst.getProperties().keySet()) {
			dstNode.setProperty(key, dst.getProperties().get(key));
		}
		Relationship relationship = srcNode.createRelationshipTo(dstNode, DynamicRelationshipType.withName(edge.labels.iterator().next()));
		for (String key : edge.properties.keySet()) {
			relationship.setProperty(key, edge.properties.get(key));
		}
	}
	
	private Node getOrCreateNode(String uniqueLabel, String uniqueProperty, Object value) {
		String cypherQuery = String.format("MERGE (n:%s {%s:{value}}) RETURN n AS NODE", uniqueLabel, uniqueProperty);
		Map<String, Object> params = new HashMap<> ();
		params.put(uniqueProperty, value);
		
		LOGGER.debug("Execution Engine: " + cypherQuery + " with " + params);
		Node node = Neo4jUtils.getExecutionResultGetSingle("NODE", executionEngine.execute(cypherQuery, params));
		return node;
	}
	
	private void createOrLinkToReactionProxy(
			Node parent, 
			GraphReactionProxyEntity proxy,
			RelationshipType relationshipType
			) {
		
		boolean create = true;
		for (Node proxyNode : graphDatabaseService
				.findNodesByLabelAndProperty(
						DynamicLabel.label(proxy.getMajorLabel()), 
						"entry", 
						proxy.getEntry())) {
			LOGGER.debug("Link To Node/Proxy " + proxyNode);
			create = false;
			
			//TODO: SET TO UPDATE
			parent.createRelationshipTo(proxyNode, relationshipType);
		}
		
		if (create) {
			Node proxyNode = graphDatabaseService.createNode();
			LOGGER.debug(String.format("Create Property %s -> %s", proxy.getMajorLabel(), proxyNode));
			proxyNode.addLabel(DynamicLabel.label(proxy.getMajorLabel()));
			
			for (String label : proxy.getLabels())
				proxyNode.addLabel(DynamicLabel.label(label));
			
			proxyNode.setProperty("entry", proxy.getEntry());
			proxyNode.setProperty("proxy", true);
			proxyNode.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, proxy.getMajorLabel());
			Relationship relationship = parent.createRelationshipTo(proxyNode, relationshipType);
			
			for (String key : proxy.getProperties().keySet()) {
				relationship.setProperty(key, proxy.getProperties().get(key));
			}
			
		}
	}
	
	private Node createNode(Map<String, Object> nodeProperties) {
		Node node = graphDatabaseService.createNode();
		for (String key : nodeProperties.keySet()) {
			node.setProperty(key, nodeProperties.get(key));
		}
		
		return node;
	}
	
	private Relationship createRelationship(Node src, Node dst, RelationshipType relationshipType, Map<String, Object> relationshipProperties) {
		Relationship relationship = src.createRelationshipTo(dst, relationshipType);
		for (String key : relationshipProperties.keySet()) {
			relationship.setProperty(key, relationshipProperties.get(key));
		}
		
		return relationship;
	}
	
	private void createOrLinkToMetaboliteProxy(
			Node parent, 
			GraphMetaboliteProxyEntity proxy,
			RelationshipType relationshipType,
			Map<String, Object> relationshipPropertyContainer) {
		
		boolean create = true;
		for (Node proxyNode : graphDatabaseService
				.findNodesByLabelAndProperty(
						DynamicLabel.label(proxy.getMajorLabel()), 
						"entry", 
						proxy.getEntry())) {
			LOGGER.debug("Link to previous Node/Proxy " + proxyNode);
			create = false;
			
			//TODO: SET TO UPDATE
			Relationship relationship = parent.createRelationshipTo(proxyNode, relationshipType);
			for (String key : relationshipPropertyContainer.keySet()) {
				relationship.setProperty(key, relationshipPropertyContainer.get(key));
			}
		}
		
		if (create) {
			Node proxyNode = graphDatabaseService.createNode();
			LOGGER.debug(String.format("Link to new proxy %s -> %s", proxy.getMajorLabel(), proxyNode));
			proxyNode.addLabel(DynamicLabel.label(proxy.getMajorLabel()));
			
			for (String label : proxy.getLabels())
				proxyNode.addLabel(DynamicLabel.label(label));
			
			proxyNode.setProperty("entry", proxy.getEntry());
			proxyNode.setProperty("proxy", true);
			proxyNode.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, proxy.getMajorLabel());
			Relationship relationship = parent.createRelationshipTo(proxyNode, relationshipType);
			for (String key : relationshipPropertyContainer.keySet()) {
				relationship.setProperty(key, relationshipPropertyContainer.get(key));
			}
		}
	}

	@Override
	public List<Long> getGlobalAllReactionIds() {
		List<Long> result = new ArrayList<> ();
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(REACTION_LABEL)) {
			
			result.add(node.getId());
		}
		return result;
	}

	@Override
	public List<Long> getAllReactionIds(String tag) {
		//TODO: verify label if valid
		
		List<Long> result = new ArrayList<> ();
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(DynamicLabel.label(tag))) {
			
			result.add(node.getId());
		}
		return result;
	}

	@Override
	public List<String> getAllReactionEntries(String tag) {
		//TODO: verify label if valid
		
		List<String> result = new ArrayList<> ();
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(DynamicLabel.label(tag))) {
			
			result.add((String)node.getProperty("entry"));
		}
		return result;
	}

	private void setupConnectedLinks(GraphReactionEntity entity, Node node) {
		
		for (Relationship relationship : node.getRelationships(Direction.OUTGOING)) {
			String relationshipType = relationship.getType().name();
			if (entity.getConnectionTypeCounter(relationshipType) < RELATIONSHIP_TYPE_LIMIT) {
				Node otherNode = relationship.getOtherNode(node);
				LOGGER.trace(String.format("%s -[:%s]-> %s", node, relationship.getType().name(), otherNode));
				AbstractGraphEdgeEntity edgeEntity = deserialize(relationship);
				AbstractGraphNodeEntity nodeEntity = deserialize(otherNode);
				LOGGER.trace(String.format("%s -[:%s]-> %s", node, relationship.getType().name(), nodeEntity.getLabels()));
				Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> p = new ImmutablePair<>(edgeEntity, nodeEntity);
				entity.addConnectedEntity(p);
//				entity.getConnectedEntities().add(p);
			} else {
				entity.addConnectionTypeCounter(relationshipType);
			}
			
		}
	}
	private AbstractGraphEdgeEntity deserialize(Relationship relationship) {
		AbstractGraphEdgeEntity edgeEntity = new AbstractGraphEdgeEntity();
		String label = relationship.getType().name();
		edgeEntity.setId(relationship.getId());
		edgeEntity.getLabels().add(label);
//		edgeEntity.set
		edgeEntity.setProperties(Neo4jUtils.getPropertiesMap(relationship));
		return edgeEntity;
	}
	
	private AbstractGraphNodeEntity deserialize(Node node) {
		AbstractGraphNodeEntity entity = new AbstractGraphNodeEntity();
		entity.setId(node.getId());
		entity.setLabels(Neo4jUtils.getLabelsAsString(node));
		entity.setProperties(Neo4jUtils.getPropertiesMap(node));
		String majorLabel = (String) node.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY);
//		if (entity.getLabels().contains(GlobalLabel.ReactionProperty.toString())) {
//			for (String label : entity.getLabels()) {
//				if (isMetabolitePropertyLabel(label)) majorLabel = label;
//			}
//		}
//		if (entity.getLabels().contains(GlobalLabel.Reaction.toString())) {
//			for (String label : entity.getLabels()) {
//				if (isMetaboliteMajorLabel(label)) majorLabel = label;
//			}
//		}
		entity.setMajorLabel(majorLabel);
		return entity;
	}
}
