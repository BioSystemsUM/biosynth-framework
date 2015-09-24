package edu.uminho.biosynth.core.data.integration.neo4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BioDbDictionary;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

public class Neo4jKeggCompoundMetaboliteDaoImpl extends AbstractNeo4jDao<KeggCompoundMetaboliteEntity> implements MetaboliteDao<KeggCompoundMetaboliteEntity>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jKeggCompoundMetaboliteDaoImpl.class);
	
	private static Label compoundLabel = CompoundNodeLabel.KEGG;
	
//	private String nullToString(Object obj) {
//		return obj==null?"null":obj.toString();
//	}
	public Neo4jKeggCompoundMetaboliteDaoImpl(GraphDatabaseService graphdb) { 
		super(graphdb);
	}
	
	

//	@Override
	public KeggCompoundMetaboliteEntity find(Serializable id) {
		Node node = graphDatabaseService.findNodesByLabelAndProperty(compoundLabel, "entry", id).iterator().next();
		KeggCompoundMetaboliteEntity cpd = nodeToObject(node);
		return cpd;
	}

	@Override
	public Serializable save(KeggCompoundMetaboliteEntity cpd) {

		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("cpdAttributes", attributes);
		params.put("entry", cpd.getEntry());
		params.put("name", cpd.getName().toLowerCase());
		params.put("formula", cpd.getFormula());
		params.put("comment", cpd.getComment());
		params.put("molWeight", cpd.getMolWeight());
		params.put("remark", cpd.getRemark());
		params.put("mass", cpd.getMass());
		params.put("inchi", cpd.getInchi());
		params.put("inchikey", cpd.getInchiKey());
		params.put("smiles", cpd.getSmiles());
		
		String labels[] = {CompoundNodeLabel.Metabolite.toString(), 
				CompoundNodeLabel.KEGG.toString(), CompoundNodeLabel.LigandCompound.toString()};
		
		String query = "MERGE (cpd:%s {entry:{entry}}) ON CREATE SET "
				+ "cpd.created_at=timestamp(), cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.mass={mass}, "
				+ "cpd.inchi={inchi}, cpd.inchikey={inchikey}, cpd.smiles={smiles}, "
				+ "cpd.comment={comment}, cpd.molWeight={molWeight}, cpd.remark={remark}, cpd.proxy=false "
				+ "ON MATCH SET "
				+ "cpd.updated_at=timestamp(), "
				+ "cpd.name={name}, cpd.formula={formula}, cpd.mass={mass}, "
				+ "cpd.inchi={inchi}, cpd.inchikey={inchikey}, cpd.smiles={smiles}, "
				+ "cpd.comment={comment}, cpd.molWeight={molWeight}, cpd.remark={remark}, cpd.proxy=false";
		String query_ = String.format(query, StringUtils.join(labels, ':'));
		
		executionEngine.execute( query_, params);
		
		//MERGE (t:Test {entry:"K"}) ON MATCH SET t.formula="foo"
		//
		
		if (cpd.getFormula() != null) {
			LOGGER.debug(String.format("Relationship (%s)-[HasFormula]->(%s)", cpd.getEntry(), cpd.getFormula()));
			executionEngine.execute("MERGE (m:Formula {formula:{formula}}) ", params);
			executionEngine.execute("MATCH (cpd:LigandCompound {entry:{entry}}), (f:Formula {formula:{formula}}) MERGE (cpd)-[r:HasFormula]->(f)", params);
		}
		if (params.get("smiles") != null) {
			LOGGER.debug(String.format("Relationship (%s)-[HasSMILES]->(%s)", cpd.getEntry(), cpd.getSmiles()));
			executionEngine.execute("MERGE (s:SMILES {smiles:{smiles}}) ", params);
			executionEngine.execute("MATCH (cpd:LigandCompound {entry:{entry}}), (s:SMILES {smiles:{smiles}}) MERGE (cpd)-[r:HasSMILES]->(s)", params);
		}
		if (params.get("inchi") != null) {
			LOGGER.debug(String.format("Relationship (%s)-[HasInChI]->(%s)", cpd.getEntry(), cpd.getInchi()));
			executionEngine.execute("MERGE (i:InChI {inchi:{inchi}}) ON CREATE SET i.inchikey={inchikey} ON MATCH SET i.inchikey={inchikey}", params);
			executionEngine.execute("MATCH (cpd:LigandCompound {entry:{entry}}), (i:InChI {inchi:{inchi}}) MERGE (cpd)-[r:HasInChI]->(i)", params);
		}
		for (String name : cpd.getNames()) {
			LOGGER.debug(String.format("Relationship (%s)-[HasName]->(%s)", cpd.getEntry(), name));
			params.put("name", name.toLowerCase());
			executionEngine.execute("MERGE (m:Name {name:{name}}) ", params);
			executionEngine.execute("MATCH (cpd:LigandCompound {entry:{entry}}), (n:Name {name:{name}}) MERGE (cpd)-[r:HasName]->(n)", params);
		}

		
		for (KeggCompoundMetaboliteCrossreferenceEntity xref : cpd.getCrossreferences()) {
			
			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
			String dbEntry = xref.getValue(); //Also need to translate if necessary
			params.put("dbEntry", dbEntry);
			
			LOGGER.debug(String.format("Relationship (%s)-[HasCrossreferenceTo]->(%s:%s)", cpd.getEntry(), dbLabel, dbEntry));
			
			executionEngine.execute("MERGE (cpd:" + dbLabel + ":Compound {entry:{dbEntry}}) ON CREATE SET cpd.proxy=true", params);
			executionEngine.execute("MATCH (cpd1:LigandCompound {entry:{entry}}), (cpd2:" + dbLabel + " {entry:{dbEntry}}) MERGE (cpd1)-[r:HasCrossreferenceTo]->(cpd2)", params);
		}
		
		return null;
	}

//	@Override
	public List<KeggCompoundMetaboliteEntity> findAll() {
		ExecutionResult result = executionEngine.execute("MATCH (cpd:KEGG) RETURN cpd");
		Iterator<Node> iterator = result.columnAs("cpd");
		List<Node> nodes = IteratorUtil.asList(iterator);
		List<KeggCompoundMetaboliteEntity> res = new ArrayList<> ();
		for (Node node : nodes) {
			KeggCompoundMetaboliteEntity cpd = this.nodeToObject(node);
			if (cpd != null) res.add(cpd);
		}
		return res;
	}



	@Override
	protected KeggCompoundMetaboliteEntity nodeToObject(Node node) {
		if (IteratorUtil.asList(node.getPropertyKeys()).size() == 1) return null;
		KeggCompoundMetaboliteEntity cpd = new KeggCompoundMetaboliteEntity();
		cpd.setEntry( (String) node.getProperty("entry"));
		if (node.hasProperty("formula")) cpd.setFormula( (String) node.getProperty("formula"));
		if (node.hasProperty("comment")) cpd.setComment( (String) node.getProperty("comment"));
		if (node.hasProperty("remark")) cpd.setRemark( (String) node.getProperty("remark"));
		if (node.hasProperty("mass")) cpd.setMass((Double) node.getProperty("mass"));
		return cpd;
	}



	@Override
	public List<Serializable> getAllMetaboliteIds() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public KeggCompoundMetaboliteEntity getMetaboliteById(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public KeggCompoundMetaboliteEntity saveMetabolite(
			KeggCompoundMetaboliteEntity metabolite) {
		this.save(metabolite);
		return null;
	}



	@Override
	public Serializable saveMetabolite(Object entity) {
		return this.save(KeggCompoundMetaboliteEntity.class.cast(entity));
	}



	@Override
	public KeggCompoundMetaboliteEntity getMetaboliteByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<String> getAllMetaboliteEntries() {
		List<String> res = new ArrayList<> ();
		Iterator<String> iterator = executionEngine.execute(
				"MATCH (cpd:KEGG {proxy:false}) RETURN cpd.entry AS entries").columnAs("entries");
		while (iterator.hasNext()) {
			res.add(iterator.next());
		}
		return res;
	}
}
