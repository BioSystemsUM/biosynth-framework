package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.kernel.impl.core.NodeProxy;
import org.springframework.beans.factory.annotation.Autowired;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.data.integration.chimera.domain.components.IntegratedMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundNodeLabel;
import scala.collection.convert.Wrappers.SeqWrapper;

public class Neo4jChimeraDataDaoImpl implements ChimeraDataDao {
	
	private static Logger LOGGER = Logger.getLogger(Neo4jChimeraDataDaoImpl.class);
	
	@Autowired
	private GraphDatabaseService graphDatabaseService;
	
	private ExecutionEngine executionEngine;
	
	public Neo4jChimeraDataDaoImpl() { }
	
	@PostConstruct
	public void initialize() {
		if (executionEngine == null) {
			this.setGraphDatabaseService(graphDatabaseService);
		}
		LOGGER.info(String.format("%s initialized !", this));
	}

	public GraphDatabaseService getGraphDatabaseService() {
		return graphDatabaseService;
	}

	public void setGraphDatabaseService(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
		this.executionEngine = new ExecutionEngine(graphDatabaseService);
	}
	
	public Node getMetaboliteNodeByEntry(String entry, CompoundNodeLabel type) {
		Node node = null;
		ResourceIterable<Node> res = this.graphDatabaseService.findNodesByLabelAndProperty(type, "entry", entry);
		Iterator<Node> i = res.iterator();
		while (i.hasNext()) {
			//entry should have unique constraint therefore no more 
			//than one node should be found per compoud label
			if (node != null) {
				System.err.println("error duplicate entry missing unique constraint");
			}
			node = i.next();
		}
		
		return node;
	}

	@Override
	public List<Long> getClusterByQuery(String query) {
		List<Long> clusterElements = new ArrayList<> ();
		ExecutionResult res = this.executionEngine.execute(query);
		List<Object> nodeList = IteratorUtil.asList(res.columnAs(res.columns().iterator().next()));
		for (Object obj: nodeList) {
			if (obj instanceof SeqWrapper) {
				for (Object node: (SeqWrapper<?>) obj) {
					Long id;
					if (node instanceof Long) {
						id = (Long) node;
					} else {
						NodeProxy proxy = (NodeProxy) node;
						System.out.println(proxy.getLabels());
						id = proxy.getId();
					}
					clusterElements.add(id);
				}
			} else {
				Node node = (Node) obj;
				clusterElements.add(node.getId());
			}
		}
		return clusterElements;
	}

	private void createAndAdd(Map<String, List<Object>> map, String key, Object value) {
		if (!map.containsKey(key)) {
			map.put(key, new ArrayList<>());
		}
		map.get(key).add(value);
	}
	
	@Override
	public Map<String, List<Object>> getCompositeNode(Long id) {
		System.out.println("Loading composite node -> " + id);
		//Build Self Node
		Map<String, Object> root = this.getEntryProperties(id);
		String sourceEntry = (String) root.get("entry");
		
		
		Map<String, List<Object>> data = new HashMap<> ();
//		data.put("Model", new ArrayList<> ());
		//START cpd=node(0) MATCH composite=(cpd)-[*1..1]->(c) RETURN nodes(composite);
		//START cpd=node(0) MATCH path=(cpd)-[*1..1]->(c) RETURN collect(c)
		ExecutionResult res = this.executionEngine.execute(String.format(
				"START cpd=node(%d) MATCH path=(cpd)-[*1..1]->(c) RETURN collect(c);", id));
//		ExecutionResult res = this.engine.execute(String.format(
//				"START cpd=node(%d) MATCH composite=(cpd)-[*1..1]->(c) RETURN distinct nodes(composite);", id));
		List<Object> list = IteratorUtil.asList(res.columnAs(res.columns().iterator().next()));
		for (Object obj: list) {
			if (obj instanceof SeqWrapper) {
				for (Object node: (SeqWrapper<?>) obj) {
					System.out.println(node);
					NodeProxy proxy = (NodeProxy) node;
//					System.out.println(proxy.getId() + " -> " +  proxy.getLabels());
					if (id.equals(proxy.getId())) {
						for (Label label : proxy.getLabels()) {
							sourceEntry = sourceEntry.concat(label.name()).concat(":");
						}
						sourceEntry = sourceEntry.concat((String) proxy.getProperty("entry"));
//						System.out.println("SELF !");
					} else {
//						System.out.println(proxy.getLabels());
//						System.out.println(proxy.getPropertyKeys());
						Set<String> labels = new HashSet<> ();
						for (Label label: IteratorUtil.asSet(proxy.getLabels()))
							labels.add(label.toString());
						if (labels.contains("Compound")) {
							labels.remove("Compound");
//							System.out.println("Adding Crossreference");
							//These Compound Labels -> Crossreferences !
							IntegratedMetaboliteCrossreferenceEntity xref = new IntegratedMetaboliteCrossreferenceEntity();
//							GenericCrossReference xref = new GenericCrossReference();
							xref.setType(GenericCrossReference.Type.DATABASE);
							xref.setRef(labels.iterator().next());
							
							xref.setValue((String)proxy.getProperty("entry"));
							createAndAdd(data, "crossreferences", xref);
						} else {
//							System.out.println("Adding Property");
							String property = proxy.getPropertyKeys().iterator().next();
							Object value = proxy.getProperty(property);
							createAndAdd(data, property, value);
							//Other Labels -> Properties
						}
					}
//					clusterElements.add(proxy.getId());
				}
			} else {
				throw new RuntimeException("Error query result: id=" + id);
			}
			
			if (data.containsKey("crossreferences")) {
				for (Object xref: data.get("crossreferences")) {
					IntegratedMetaboliteCrossreferenceEntity xref_ = (IntegratedMetaboliteCrossreferenceEntity)xref;
					xref_.setSource(sourceEntry);
				}
			}
		}
		
		return data;
	}

	@Override
	public Map<String, Object> getEntryProperties(Long id) {
		Map<String, Object> propsMap = new HashMap<> ();
		ExecutionResult res = this.executionEngine.execute(String.format(
				"START cpd=node(%d) RETURN cpd;", id));
		
		List<?> list = IteratorUtil.asList(res.columnAs("cpd"));
		String labels = "";
		
		for (Object obj : list) {
			Node node = (Node)obj;
			for (String prop : node.getPropertyKeys()) {
				propsMap.put(prop, node.getProperty(prop));
			}
			for (Label label : node.getLabels()) {
				labels = labels.concat(label.name()).concat(":");
			}
		}
		
		
		
		labels = labels.substring(0, labels.length() - 1);
		
		
		
		propsMap.put("labels", labels);
		propsMap.put("isProxy", false);
		if (propsMap.containsKey("entry") && propsMap.keySet().size() <= 3) {
			propsMap.put("isProxy", true);
		}
		
//		System.out.println("PROPS " + propsMap);
		
		return propsMap;
	}

	@Override
	public List<Long> getAllMetaboliteIds() {
		List<Long> idList = new ArrayList<> ();
		ExecutionResult res = this.executionEngine.execute("MATCH (cpd:Compound {proxy:false}) RETURN ID(cpd) AS id");
		List<?> list = IteratorUtil.asList(res.columnAs("id"));
		for (Object id : list) {
			idList.add((Long) id);
		}
		return idList;
	}

	@Override
	public List<String> getAllProperties() {
		List<String> res = new ArrayList<> ();
		ExecutionResult execRes = executionEngine.execute("START n=node(*) RETURN DISTINCT labels(n)");
		System.out.println(execRes.dumpToString());
		return res;
	}

	@Override
	public List<Long> listAllPropertyIds(String property) {
		List<Long> res = new ArrayList<> ();
		// TODO Auto-generated method stub
		return res;
	}

}
