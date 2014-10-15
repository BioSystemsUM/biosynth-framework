package edu.uminho.biosynth.util;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.components.ReferenceNode;

public class ReferenceNodeVertexTransformer implements IVertexTransformer<ReferenceNode>{

	private static Map<String, String> dbColor = new HashMap<> ();
	
	static {
		dbColor.put(BiggMetaboliteEntity.class.getName(), "blue");
		dbColor.put(BioCycMetaboliteEntity.class.getName(), "orange");
		dbColor.put(KeggCompoundMetaboliteEntity.class.getName(), "green");
		dbColor.put(MnxMetaboliteEntity.class.getName(), "gray");
		dbColor.put(SeedMetaboliteEntity.class.getName(), "red");
		dbColor.put(ChebiMetaboliteEntity.class.getName(), "purple");
	}
	
	@Override
	public DotNode toDotNode(ReferenceNode vertex) {
		DotNode node = new DotNode();
		node.setLabel(vertex.getEntryTypePair().getFirst());
//		System.out.println(vertex.getEntityType().getName());
		if (dbColor.containsKey(vertex.getEntityType().getName())) {
			node.setColor(dbColor.get(vertex.getEntityType().getName()));
		}
		return node;
	}

}
