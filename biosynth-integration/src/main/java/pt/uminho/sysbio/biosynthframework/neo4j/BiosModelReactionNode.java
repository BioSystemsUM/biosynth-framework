package pt.uminho.sysbio.biosynthframework.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;
import pt.uminho.sysbio.biosynthframework.CompartmentalizedStoichiometry;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class BiosModelReactionNode extends BiodbEntityNode {

  private static final Logger logger = LoggerFactory.getLogger(BiosModelReactionNode.class);
  
  public BiosModelReactionNode(Node node, String databasePath) {
    super(node, databasePath);
  }
  
  public static Map<String, Integer> parse(String str) {
    Map<String, Integer> authors = new TreeMap<>();
    if (str != null && !str.isEmpty()) {
      for (String a : str.split(";")) {
        String author = a.split(":")[0];
        Integer score = Integer.parseInt(a.split(":")[1]);
        authors.put(author, score);
      }
    }
    return authors;
  }
  
  public static String toString(Map<String, Integer> authors) {
    return Joiner.on(';').withKeyValueSeparator(':').join(authors);
  }
  
  public String getSid() {
    return (String) this.getProperty("id", null);
  }
  
  public Set<String> getGeneKBaseIds() {
    BiodbEntityNode gprNode = this.getGpr();
    Set<String> geneIds = new HashSet<>();
    if (gprNode != null) {
      for (Relationship r : gprNode.getRelationships(MetabolicModelRelationshipType.has_gpr_gene)) {
        Node n = r.getOtherNode(gprNode);
        String geneId = (String) n.getProperty("bios_kbase_id", null);
        if (!DataUtils.empty(geneId)) {
          geneIds.add(geneId);              
        }
      }
    }
    
    return geneIds;
  }
  
  public BiodbEntityNode getGpr() {
    Relationship r = this.getSingleRelationship(MetabolicModelRelationshipType.has_gpr, Direction.BOTH);
    if (r != null) {
      return new BiodbEntityNode(r.getOtherNode(node), databasePath);
    }
    
    return null;
  }
  
  public double getStoichiometry(Relationship relationship, double defaultValue) {
    Object o = relationship.getProperty("stoichiometry", null);
    if (o == null) {
      return defaultValue;
    } else if (o instanceof String) {
      return Double.parseDouble(o.toString());
    } else {
      return (double) o;
    }
  }
  
  public Map<Long, Double> getStoichiometry() {
    return getStoichiometry(1);
  }
  
  public Map<Long, Double> getStoichiometry(double defaultValue) {
    Map<Long, Double> stoichiometryMap = new HashMap<>();
    for (Relationship l : this.getRelationships(MetabolicModelRelationshipType.left_component)) {
      Node other = l.getOtherNode(this);
      double value = getStoichiometry(l, defaultValue);
      stoichiometryMap.put(other.getId(), -1 * value);
    }
    for (Relationship r : this.getRelationships(MetabolicModelRelationshipType.right_component)) {
      Node other = r.getOtherNode(this);
      double value = getStoichiometry(r, defaultValue);
      stoichiometryMap.put(other.getId(), value);
    }
    
    return stoichiometryMap;
  }
  
  public CompartmentalizedStoichiometry<Long, Long> getCompartmentalizedStoichiometry(double defaultValue) {
    CompartmentalizedStoichiometry<Long, Long> stoichiometry = new CompartmentalizedStoichiometry<>();
    
    Map<BiosModelSpeciesNode, Double> data = getStoichiometryAsNodes(defaultValue);
    for (BiosModelSpeciesNode spiNode : data.keySet()) {
      double value = data.get(spiNode);
      Node node = spiNode.getCompartment();
      long cmpId = -1;
      if (node != null) {
        cmpId = node.getId();
      }
      stoichiometry.add(spiNode.getId(), cmpId, value);
    }
    
    return stoichiometry;
  }
  
  public Map<BiosModelSpeciesNode, Double> getStoichiometryAsNodes(double defaultValue) {
    Map<BiosModelSpeciesNode, Double> stoichiometryMap = new HashMap<>();
    for (Relationship l : this.getRelationships(MetabolicModelRelationshipType.left_component)) {
      Node other = l.getOtherNode(this);
      double value = getStoichiometry(l, defaultValue);
      stoichiometryMap.put(new BiosModelSpeciesNode(other, databasePath), -1 * value);
    }
    for (Relationship r : this.getRelationships(MetabolicModelRelationshipType.right_component)) {
      Node other = r.getOtherNode(this);
      double value = getStoichiometry(r, defaultValue);
      stoichiometryMap.put(new BiosModelSpeciesNode(other, databasePath), value);
    }
    
    return stoichiometryMap;
  }
  
  public boolean isTranslocation() {
    Set<String> cmps = new HashSet<>();
    Map<BiosModelSpeciesNode, Double> stoich = this.getStoichiometryAsNodes(1);
    for (BiosModelSpeciesNode spiNode : stoich.keySet()) {
      String cmpSid = spiNode.getCompartmentSid();
      cmps.add(cmpSid);
    }
    return cmps.size() > 1;
  }
  
  public Set<BiodbReactionNode> getReferences() {
    Set<BiodbReactionNode> references = new HashSet<>();
    for (Relationship r : this.getRelationships(MetabolicModelRelationshipType.has_crossreference_to)) {
      Node other = r.getOtherNode(node);
      references.add(new BiodbReactionNode(other, databasePath));
    }
    return references;
  }
  
  public Set<BiodbReactionNode> getReferences(ReactionMajorLabel database) {
    Set<BiodbReactionNode> references = new HashSet<>();
    for (Relationship r : this.getRelationships(MetabolicModelRelationshipType.has_crossreference_to)) {
      Node other = r.getOtherNode(node);
      if (other.hasLabel(database)) {
        references.add(new BiodbReactionNode(other, databasePath));        
      }
    }
    return references;
  }

  public Integer getAnnotationScore(BiodbReactionNode rxnNode) {
    Integer score = -1;
    
    Map<String, Integer> users = this.getAnnotationUsers(rxnNode);
    for (Integer v : users.values()) {
      if (score < v) {
        score = v;
      }
    }
    
    return score;
  }
  
  public Map<String, Integer> getAnnotationUsers(BiodbReactionNode rxnNode) {
    Map<String, Integer> users = new HashMap<>();
    Relationship referenceLink = null;
    for (Relationship r : this.getRelationships(
        Direction.OUTGOING, MetabolicModelRelationshipType.has_crossreference_to)) {
      if (r.getOtherNode(this.node).getId() == rxnNode.getId()) {
        referenceLink = r;
      }
    }
    
    if (referenceLink != null) {
      String authorsStr = (String) referenceLink.getProperty("authors", "");
      Map<String, Integer> authors = parse(authorsStr);
      users.putAll(authors);
    }
    
    return users;
  }
  
  public Long addAnnotation(BiodbReactionNode rxnNode, int score, String author) {
    Relationship referenceLink = null;
    for (Relationship r : this.getRelationships(
        Direction.OUTGOING, MetabolicModelRelationshipType.has_crossreference_to)) {
      if (r.getOtherNode(this.node).getId() == rxnNode.getId()) {
        referenceLink = r;
      }
    }
    
    if (referenceLink == null) {
      referenceLink = this.createRelationshipTo(
          rxnNode, MetabolicModelRelationshipType.has_crossreference_to);
      Neo4jUtils.setCreatedTimestamp(referenceLink);
    }
    Neo4jUtils.setUpdatedTimestamp(referenceLink);
    
    String authorsStr = (String) referenceLink.getProperty("authors", "");
    Map<String, Integer> authors = parse(authorsStr);
    authors.put(author, score);
    
    referenceLink.setProperty("authors", toString(authors));
    
    return referenceLink.getId();
  }
  
  public Long addLeftComponent(BiosModelSpeciesNode node, Map<String, Object> properties) {
    Relationship r = this.createStoichiometryLink(node, 
        MetabolicModelRelationshipType.left_component, properties);
    if (r != null) {
      return r.getId();
    }
    return null;
  }
  
  public Long addRightComponent(BiosModelSpeciesNode node, Map<String, Object> properties) {
    Relationship r = this.createStoichiometryLink(node, 
        MetabolicModelRelationshipType.right_component, properties);
    if (r != null) {
      return r.getId();
    }
    return null;
  }
  
  protected Relationship createStoichiometryLink(Node spiNode, RelationshipType t, Map<String, Object> p) {
    Relationship r = null;
    if (!Neo4jUtils.exitsRelationshipBetween(this, spiNode, Direction.BOTH)) {
      logger.info("[LINK] [{}] -[{}]-> [{}]", this, t, spiNode);
      r = this.createRelationshipTo(spiNode, t);
      Neo4jUtils.setPropertiesMap(p, r);
      Neo4jUtils.setTimestamps(r);
    }

    return r;
  }
  
  public Set<Long> deleteGpr() {
    Set<Long> deleted = new HashSet<>();
    Set<Long> deletedR = new HashSet<>();
    for (Relationship r : 
      this.getRelationships(MetabolicModelRelationshipType.has_gpr, Direction.BOTH)) {
      Node gpr = r.getOtherNode(this);
      for (Relationship r2 : gpr.getRelationships()) {
        deletedR.add(r2.getId());
        r2.delete();
      }
      deleted.add(gpr.getId());
      gpr.delete();
    }
    
    logger.info("deleted: R:{}, N:{}", deletedR.size(), deleted.size());
    
    return deleted;
  }
  //stoich etc
}
