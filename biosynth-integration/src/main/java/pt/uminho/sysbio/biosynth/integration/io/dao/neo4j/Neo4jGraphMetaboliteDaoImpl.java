package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphEdgeEntity;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphPropertyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphRelationshipEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.MetaboliteHeterogeneousDao;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosExternalDataNode;
public class Neo4jGraphMetaboliteDaoImpl 
extends AbstractNeo4jGraphDao<GraphMetaboliteEntity>
implements MetaboliteHeterogeneousDao<GraphMetaboliteEntity>{

  public static int RELATIONSHIP_TYPE_LIMIT = 10;

  private static final Logger logger = LoggerFactory.getLogger(Neo4jGraphMetaboliteDaoImpl.class);
  protected static final Label METABOLITE_LABEL = GlobalLabel.Metabolite;
  protected static final RelationshipType CROSSREFERENCE_RELATIONSHIP = 
      MetaboliteRelationshipType.has_crossreference_to;

  @Autowired
  public Neo4jGraphMetaboliteDaoImpl(GraphDatabaseService graphDatabaseService) {
    super(graphDatabaseService);
  }

  @Override
  public GraphMetaboliteEntity getMetaboliteById(String tagsss, Serializable id) {
    Node node = graphDatabaseService.getNodeById(Long.parseLong(id.toString()));
    if (!node.hasLabel(GlobalLabel.Metabolite)) {
      return null;
    }
    
    BiodbMetaboliteNode cpdNode = graphDatabaseService.getMetabolite(Long.parseLong(id.toString()));

    if (cpdNode == null) {
      return null;
    }
    
    logger.debug(String.format("Found %s - %s", cpdNode, Neo4jUtils.getLabels(cpdNode)));

    GraphMetaboliteEntity metaboliteEntity = new GraphMetaboliteEntity();
    metaboliteEntity.setProperties(Neo4jUtils.getPropertiesMap(cpdNode));
    metaboliteEntity.setId(cpdNode.getId());
    metaboliteEntity.getLabels().addAll(Neo4jUtils.getLabelsAsString(cpdNode));
    setupConnectedLinks(metaboliteEntity, cpdNode);

    return metaboliteEntity;
  }

  @Override
  public GraphMetaboliteEntity getMetaboliteByEntry(String tag, String entry) {
    MetaboliteMajorLabel database;
    try {
      database = MetaboliteMajorLabel.valueOf(tag);
    } catch (IllegalArgumentException e) {
      logger.warn("IA - {}", e.getMessage());
      return null;
    }
    
    BiodbMetaboliteNode node = graphDatabaseService.getMetabolite(entry, database);

    if (node == null) {
      logger.debug("Metabolite [{}:{}] not found", tag, entry);
      return null;
    }

    logger.debug(String.format("Found %s for %s:%s", node, tag, entry));


    return getMetaboliteById("", node.getId());
  }

  private void setupConnectedLinks(GraphMetaboliteEntity entity, Node node) {

    for (Relationship relationship : node.getRelationships(Direction.OUTGOING)) {
      String relationshipType = relationship.getType().name();
      if (entity.getConnectionTypeCounter(relationshipType) < RELATIONSHIP_TYPE_LIMIT) {
        Node otherNode = relationship.getOtherNode(node);
        logger.trace(String.format("%s -[:%s]-> %s", node, relationship.getType().name(), otherNode));
        AbstractGraphEdgeEntity edgeEntity = deserialize(relationship);
        AbstractGraphNodeEntity nodeEntity = deserialize(otherNode);
        logger.trace(String.format("%s -[:%s]-> %s", node, relationship.getType().name(), nodeEntity.getLabels()));
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
    if (node.hasLabel(GlobalLabel.EXTERNAL_DATA)) {
      logger.debug("reloading node");
      node = graphDatabaseService.getNodeById(node.getId());
    }
    GraphMetaboliteEntity entity = new GraphMetaboliteEntity();
    entity.setId(node.getId());
    entity.setLabels(Neo4jUtils.getLabelsAsString(node));
    entity.setProperties(node.getAllProperties());
    String majorLabel = null;
    if (entity.getLabels().contains(GlobalLabel.MetaboliteProperty.toString())) {
      for (String label : entity.getLabels()) {
        if (isMetabolitePropertyLabel(label)) majorLabel = label;
      }
    }
    if (entity.getLabels().contains(GlobalLabel.Metabolite.toString())) {
      for (String label : entity.getLabels()) {
        if (isMetaboliteDatabase(label)) majorLabel = label;
      }
    }
    entity.setMajorLabel(majorLabel);
    return entity;
  }

  private boolean isMetabolitePropertyLabel(String label) {
    try {
      MetabolitePropertyLabel.valueOf(label);
      logger.trace(label + " is a MetabolitePropertyLabel");
      return true;
    } catch (IllegalArgumentException e) {
      logger.trace(label + " is not MetabolitePropertyLabel - " + e.getMessage());
    }

    return false;
  }

  @Deprecated
  private boolean isMetaboliteDatabase(String label) {
    return Neo4jUtils.isMetaboliteDatabase(label);
  }

  @Override
  public GraphMetaboliteEntity saveMetabolite(String tag, GraphMetaboliteEntity metabolite) {
    logger.debug("save metabolite {}, {}", metabolite.getEntry(), metabolite.getVersion());
    
    if (metabolite.getEntry() == null)
      logger.warn("Missing entry");
    if (metabolite.getMajorLabel() == null)
      logger.warn("Missing major label");
    if (!metabolite.getLabels().contains(GlobalLabel.Metabolite.toString()))
      logger.warn("Expected GlobalLabel.Metabolite found " + metabolite.getLabels());
    if (metabolite.getProperty("proxy", null) == null)
      logger.warn("Proxy property not found");

    super.saveGraphEntity(metabolite);
    //		Node node = graphDatabaseService.getNodeById(metabolite.getId());
    //		node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
    //		System.out.println(Neo4jUtils.getPropertiesMap(node));
    return metabolite;
  };

  @Deprecated
  public GraphMetaboliteEntity saveMetabolite_(String tag, GraphMetaboliteEntity metabolite) {
    boolean create = true;
    System.out.println(metabolite.getMajorLabel());
    System.out.println(metabolite.getEntry());
    for (Node node : graphDatabaseService.listNodes(
        DynamicLabel.label(metabolite.getMajorLabel()), 
        "entry", 
        metabolite.getEntry())) {
      create = false;
      logger.debug(String.format("Found Previous node with entry:%s", metabolite.getEntry()));
      logger.debug(String.format("MODE:UPDATE %s", node));
      //SCD Track changes
      Neo4jUtils.applyProperties(node, metabolite.getProperties());

      for (Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity> proxyPair : metabolite.getCrossreferences()) {
        this.createOrLinkToProxy(node, proxyPair);
      }
      for (Pair<GraphPropertyEntity, GraphRelationshipEntity> pair 
          : metabolite.getPropertyEntities()) {
        this.createOrLinkToProperty(node, pair);
      }

      node.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, metabolite.getMajorLabel());
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);

      metabolite.setId(node.getId());
    }

    if (create) {
      Node node = graphDatabaseService.createNode();
      logger.debug("Create " + node);

      node.addLabel(DynamicLabel.label(metabolite.getMajorLabel()));
      for (String label : metabolite.getLabels())
        node.addLabel(DynamicLabel.label(label));

      Neo4jUtils.applyProperties(node, metabolite.getProperties());

      for (Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity> proxyPair : metabolite.getCrossreferences()) {
        this.createOrLinkToProxy(node, proxyPair);
      }

      for (Pair<GraphPropertyEntity, GraphRelationshipEntity> pair 
          : metabolite.getPropertyEntities()) {
        this.createOrLinkToProperty(node, pair);
      }

      node.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, metabolite.getMajorLabel());
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);

      metabolite.setId(node.getId());
    }

    return metabolite;
  }

  @Deprecated
  private void createOrLinkToProperty(Node parent, 
      Pair<GraphPropertyEntity, GraphRelationshipEntity> propertyLinkPair) {

    logger.debug(String.format("Resolving %s => %s", propertyLinkPair.getRight(), propertyLinkPair.getLeft()));
    /*
     * Create or Update Link + Property
     * a) - If Property exists
     *      1 - Create Link To Property
     * b) - If Property does not exists
     *      1 - Create Property
     *      2 - Add Major Label
     *      3 - Add Labels
     *      4 - Add Data
     *      5 - Create Link To Property
     */
    boolean create = true;
    GraphPropertyEntity propertyEntity = propertyLinkPair.getLeft();
    GraphRelationshipEntity relationshipEntity = propertyLinkPair.getRight();
    for (Node propertyNode : graphDatabaseService
        .listNodes(
            DynamicLabel.label(propertyEntity.getMajorLabel()), 
            null,
            //						propertyEntity.uniqueProperty, 
            propertyEntity.getUniqueKey())) {
      logger.debug("Link To Node/Proxy " + propertyNode);
      create = false;

      //TODO: SET TO UPDATE
      Relationship relationship = parent.createRelationshipTo(propertyNode, 
          DynamicRelationshipType.withName((
              relationshipEntity.getMajorLabel())));
      for (String key : relationshipEntity.getProperties().keySet()) {
        relationship.setProperty(key, relationshipEntity.getProperties().get(key));
      }
    }

    //b) - If Property does not exists
    if (create) {
      //1 - Create Property
      Node propertyNode = graphDatabaseService.createNode();
      logger.debug(String.format("Create Property %s -> %s", propertyEntity.getMajorLabel(), propertyNode));
      //2 - Add Major Label
      propertyNode.addLabel(DynamicLabel.label(propertyEntity.getMajorLabel()));

      //3 - Add Labels
      for (String label : propertyEntity.getLabels()) {
        propertyNode.addLabel(DynamicLabel.label(label));
        logger.trace(String.format("Add label [%s] -> %s", label, propertyNode));
      }

      //4 - Add Data
      for (String key : propertyEntity.getProperties().keySet()) {
        Object value = propertyEntity.getProperties().get(key);
        propertyNode.setProperty(key, value);
        logger.trace(String.format("Add key:value [%s] -> %s", key, value, propertyNode));
      }
      //5 - Create Link To Property
      Relationship relationship = parent.createRelationshipTo(propertyNode, 
          DynamicRelationshipType.withName((
              relationshipEntity.getMajorLabel())));
      for (String key : relationshipEntity.getProperties().keySet()) {
        relationship.setProperty(key, relationshipEntity.getProperties().get(key));
      }
    }
  }

  @Deprecated
  private void createOrLinkToProxy(Node parent, Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity> proxyPair) {
    boolean create = true;
    GraphMetaboliteProxyEntity proxy = proxyPair.getLeft();
    GraphRelationshipEntity relationshipEntity = proxyPair.getRight();
    RelationshipType relationshipType = DynamicRelationshipType.withName(relationshipEntity.getMajorLabel());
    for (Node proxyNode : graphDatabaseService
        .listNodes(
            DynamicLabel.label(proxy.getMajorLabel()), 
            "entry", 
            proxy.getEntry())) {
      logger.debug("Link To Node/Proxy " + proxyNode);
      create = false;

      //TODO: SET TO UPDATE
      Relationship relationship = parent.createRelationshipTo(proxyNode, relationshipType);
      Neo4jUtils.setPropertiesMap(relationshipEntity.getProperties(), relationship);
    }

    if (create) {
      Node proxyNode = graphDatabaseService.createNode();
      logger.debug(String.format("Create Property %s -> %s", proxy.getMajorLabel(), proxyNode));
      proxyNode.addLabel(DynamicLabel.label(proxy.getMajorLabel()));

      for (String label : proxy.getLabels())
        proxyNode.addLabel(DynamicLabel.label(label));

      proxyNode.setProperty("entry", proxy.getEntry());
      proxyNode.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, proxy.getMajorLabel());
      proxyNode.setProperty(Neo4jDefinitions.PROXY_PROPERTY, true);
      Relationship relationship = parent.createRelationshipTo(proxyNode, relationshipType);
      Neo4jUtils.setPropertiesMap(relationshipEntity.getProperties(), relationship);
    }
  }

  @Override
  public List<Long> getGlobalAllMetaboliteIds() {
    List<Long> result = new ArrayList<> ();
    for (Node node : graphDatabaseService
        .listNodes(METABOLITE_LABEL)) {

      result.add(node.getId());
    }
    return result;
  }

  @Override
  public List<Long> getAllMetaboliteIds(String tag) {
    List<Long> result = new ArrayList<> ();
    if (!isMetaboliteDatabase(tag)) return result;
    for (Node node : graphDatabaseService
        .listNodes(DynamicLabel.label(tag))) {

      result.add(node.getId());
    }
    return result;
  }

  @Override
  public List<String> getAllMetaboliteEntries(String tag) {
    List<String> result = new ArrayList<> ();
    if (!isMetaboliteDatabase(tag)) return result;
    for (Node node : graphDatabaseService
        .listNodes(DynamicLabel.label(tag))) {

      result.add((String)node.getProperty("entry"));
    }
    return result;
  }

  @Override
  public GraphMetaboliteEntity loadMetaboliteById(long id) {
    Node node = graphDatabaseService.getNodeById(id);
    if (!node.hasLabel(GlobalLabel.Metabolite)) return null;

    logger.debug(String.format("Found %s - %s", node, Neo4jUtils.getLabels(node)));

    GraphMetaboliteEntity metaboliteEntity = new GraphMetaboliteEntity();
    metaboliteEntity.setProperties(Neo4jUtils.getPropertiesMap(node));
    metaboliteEntity.setId(node.getId());
    metaboliteEntity.getLabels().addAll(Neo4jUtils.getLabelsAsString(node));

    return metaboliteEntity;
  }

}
