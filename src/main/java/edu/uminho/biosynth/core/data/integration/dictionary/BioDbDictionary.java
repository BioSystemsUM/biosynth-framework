package edu.uminho.biosynth.core.data.integration.dictionary;

import java.util.HashMap;
import java.util.Map;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundNodeLabel;

public class BioDbDictionary {
	
	public static String translateDatabase(String db) {
		if (getDbDictionary().containsKey(db)) {
			return getDbDictionary().get(db);
		}
		System.err.println(db + " not found in translation dictionary");
		return "NOTFOUND";
	}
	
	public static Map<String, String> getDbDictionary() {
		/**
		 * Some Rules:
		 *    Labels cannot start with Numbers or have symbols
		 *    Only characters and '_'
		 */
		Map<String, String> dictionary = new HashMap<> ();
		dictionary.put("biopath", "BioPath");
		dictionary.put("upa", "UniPathway");
		//PlantCyc
		dictionary.put("PLANTCYC:MAIZE", "PlantCyc:MaizeCyc");
		//BioCyc
		dictionary.put("KNApSAcK", "KNApSAcK");
		dictionary.put("BIOCYC:ARA", "BioCyc:AraCyc");
		dictionary.put("METACYC", "BioCyc:MetaCyc");
		dictionary.put("metacyc", "BioCyc:MetaCyc");
		dictionary.put("BIGG", CompoundNodeLabel.BiGG.toString());
		dictionary.put("bigg", CompoundNodeLabel.BiGG.toString());
		dictionary.put("LIGAND-CPD", CompoundNodeLabel.KEGG.toString());
		dictionary.put("KEGG", CompoundNodeLabel.KEGG.toString());
		dictionary.put("kegg", CompoundNodeLabel.KEGG.toString());
		//KEGG
		dictionary.put("JCGGDB", "JCGGDB");
		dictionary.put("GlycomeDB", "GlycomeDB");
		dictionary.put("CCSD", "CCSD");
		dictionary.put("PUBCHEM", "PubChem");
		dictionary.put("PubChem", "PubChem");
		dictionary.put("PDB-CCD", "PDB");
		dictionary.put("chebi", CompoundNodeLabel.ChEBI.toString());
		dictionary.put("ChEBI", CompoundNodeLabel.ChEBI.toString());
		dictionary.put("CHEBI", CompoundNodeLabel.ChEBI.toString());
		dictionary.put("CAS", "CAS");
		dictionary.put("NIKKAJI", "NIKKAJI");
		dictionary.put("3DMET", "MET3D");
		dictionary.put("seed", CompoundNodeLabel.Seed.toString());
		
		dictionary.put("hmdb", CompoundNodeLabel.HMDB.toString());
		
		dictionary.put("reactome", CompoundNodeLabel.Reactome.toString());
		
		dictionary.put("brenda", CompoundNodeLabel.BRENDA.toString());
		return dictionary;
	}
	
	public static String getDbReferenceKey(Class<?> entityType) {
		if (entityType.getClass().equals(BiggMetaboliteEntity.class)) {
			return "BIGG";
		}
		
		return null;
	}
}
