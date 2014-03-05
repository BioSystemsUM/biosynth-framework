package edu.uminho.biosynth.core.data.integration.neo4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

public class Neo4jBiggMetaboliteDaoImpl extends AbstractNeo4jDao implements IMetaboliteDao<BiggMetaboliteEntity> {

	private ExecutionEngine engine;
	
	public Neo4jBiggMetaboliteDaoImpl() {}
	
	public Neo4jBiggMetaboliteDaoImpl(GraphDatabaseService graphdb) {
		engine = new ExecutionEngine(graphdb);
	}
	
	@Override
	public BiggMetaboliteEntity find(Serializable id) {
//		ExecutionEngine engine = new ExecutionEngine(graphdb);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("entry", id);
		
		String query = null;
		if (id instanceof Integer) {
			query = "MATCH (cpd:BiGG {id:{id}}) RETURN cpd";
		} else if (id instanceof String) {
			query = "MATCH (cpd:BiGG {entry:{entry}}) RETURN cpd";
		} else {
			return null;
		}
		
		ExecutionResult result = engine.execute(query, params);
//		System.out.println(result.dumpToString());
		Iterator<Node> iterator = result.columnAs("cpd");
		List<Node> nodes = IteratorUtil.asList(iterator);
		if (nodes.size() > 1) System.err.println("ERROR id not unique - integretity error");
//		System.out.println(nodes);
		BiggMetaboliteEntity cpd = null;
		for (Node node : nodes) {
//			System.out.println(node);
			cpd = new BiggMetaboliteEntity();
			cpd.setId( (Integer) node.getProperty("id"));
			cpd.setEntry( (String) node.getProperty("entry"));
			cpd.setFormula( (String) node.getProperty("formula"));
			cpd.setCharge( (Integer) node.getProperty("charge"));
		}
		
		return cpd;
	}
	
	public void remove(Serializable id) {
//		ExecutionEngine engine = new ExecutionEngine(graphdb);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("entry", id);
		
		String query = null;
		if (id instanceof Integer) {
			query = "MATCH (cpd:BiGG {id:{id}}) DELETE cpd";
		} else if (id instanceof String) {
			query = "MATCH (cpd:BiGG {entry:{entry}}) DELETE cpd";
		} else {
			return;
		}
		
		engine.execute(query, params);
	}
	
	public boolean hasMetabolite(Serializable id) {
//		ExecutionEngine engine = new ExecutionEngine(graphdb);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("entry", id);
		
		String query = null;
		if (id instanceof Integer) {
			query = "MATCH (cpd:BiGG {id:{id}}) RETURN cpd AS c1";
		} else if (id instanceof String) {
			query = "MATCH (cpd:BiGG {entry:{entry}}) RETURN cpd AS c1";
		} else {
			return false;
		}
		
		ExecutionResult result = engine.execute(query, params);
		Iterator<Node> iterator = result.columnAs("c1");
		List<Node> nodes = IteratorUtil.asList(iterator);
		if (nodes.size() > 1) System.err.println("ERROR id not unique - integretity error");
		
		return false;
	}

	@Override
	public List<BiggMetaboliteEntity> findAll() {
//		ExecutionEngine engine = new ExecutionEngine(graphdb);
		ExecutionResult result = engine.execute("MATCH (cpd:BiGG) RETURN cpd");
		Iterator<Node> iterator = result.columnAs("cpd");
		List<Node> nodes = IteratorUtil.asList(iterator);
		List<BiggMetaboliteEntity> res = new ArrayList<> ();
		for (Node node : nodes) {
			res.add(this.nodeToBiggMetaboliteEntity(node));
		}
		return res;
	}
	
	private BiggMetaboliteEntity nodeToBiggMetaboliteEntity(Node node) {
		BiggMetaboliteEntity cpd = new BiggMetaboliteEntity();
		cpd.setId( (Integer) node.getProperty("id"));
		cpd.setEntry( (String) node.getProperty("entry"));
		cpd.setFormula( (String) node.getProperty("formula"));
		cpd.setCharge( (Integer) node.getProperty("charge"));
		return cpd;
	}

	@Override
	public Serializable save(BiggMetaboliteEntity cpd) {
//		ExecutionEngine engine = new ExecutionEngine(graphdb);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", cpd.getId());
		params.put("entry", cpd.getEntry());
		params.put("name", cpd.getName());
		params.put("formula", cpd.getFormula());
		params.put("charge", cpd.getCharge());
		
		engine.execute("MERGE (cpd:BiGG:Compound {entry:{entry}, id:{id}}) ON CREATE SET "
				+ "cpd.created_at=timestamp(), cpd.updated_at=timestamp(), cpd.name={name}, cpd.formula={formula}, "
				+ "cpd.charge={charge}"
				+ "ON MATCH SET "
				+ "cpd.updated_at=timestamp(), cpd.name={name}, cpd.formula={formula}, cpd.charge={charge}", params);
		
		engine.execute("MERGE (m:Formula {formula:{formula}}) ", params);
		engine.execute("MERGE (m:Charge {charge:{charge}}) ", params);
		engine.execute("MERGE (m:Name {name:{name}}) ", params);
		engine.execute("MATCH (cpd:BiGG {id:{id}}), (f:Formula {formula:{formula}}) MERGE (cpd)-[r:HasFormula]->(f)", params);
		engine.execute("MATCH (cpd:BiGG {id:{id}}), (c:Charge {charge:{charge}}) MERGE (cpd)-[r:HasCharge]->(c)", params);
		engine.execute("MATCH (cpd:BiGG {id:{id}}), (n:Name {name:{name}}) MERGE (cpd)-[r:HasName]->(n)", params);
		
		for (String cmp : cpd.getCompartments()) {
			params.put("compartment", cmp);
			engine.execute("MERGE (c:Compartment {compartment:{compartment}}) ", params);
			engine.execute("MATCH (cpd:BiGG {id:{id}}), (c:Compartment {compartment:{compartment}}) MERGE (cpd)-[r:FoundIn]->(c)", params);
		}
		
		for (BiggMetaboliteCrossReferenceEntity xref : cpd.getCrossReferences()) {
			switch (xref.getType()) {
				case DATABASE:
					String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
					String dbEntry = xref.getValue(); //Also need to translate if necessary
					params.put("dbEntry", dbEntry);
					engine.execute("MERGE (cpd:" + dbLabel + ":Compound {entry:{dbEntry}}) ", params);
					engine.execute("MATCH (cpd1:BiGG {id:{id}}), (cpd2:" + dbLabel + " {entry:{dbEntry}}) MERGE (cpd1)-[r:HasCrossreferenceTo]->(cpd2)", params);
					break;
				case MODEL:
					String modelId = xref.getValue();
					params.put("modelId", modelId);
					engine.execute("MERGE (m:Model {id:{modelId}}) ", params);
					engine.execute("MATCH (cpd:BiGG {id:{id}}), (m:Model {id:{modelId}}) MERGE (cpd)-[r:FoundIn]->(m)", params);
					break;
				default:
					throw new RuntimeException("unsupported type " + xref.getType());
			}

		}
		return null;
	}

}
