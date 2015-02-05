package pt.uminho.sysbio.biosynth.integration.etl.biodb.seed;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetaboliteStructureEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetaboliteEntity;

public class SeedMetaboliteTransform
extends AbstractMetaboliteTransform<SeedMetaboliteEntity>{

	private static final String SEED_METABOLITE_LABEL = MetaboliteMajorLabel.Seed.toString();
	
	public SeedMetaboliteTransform() {
		super(SEED_METABOLITE_LABEL, new BiobaseMetaboliteEtlDictionary<>(SeedMetaboliteEntity.class));
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphMetaboliteEntity centralMetaboliteEntity,
			SeedMetaboliteEntity entity) {
		
//		throw new RuntimeException("Implement me please !");
//		centralMetaboliteEntity.addPropertyEntity(
//				this.buildPropertyLinkPair(
//						PROPERTY_UNIQUE_KEY, 
//						entity.getInchi(), 
//						METABOLITE_INCHI_LABEL, 
//						METABOLITE_INCHI_RELATIONSHIP_TYPE));
		for (SeedMetaboliteStructureEntity structure : entity.getStructures()) {
			centralMetaboliteEntity.addPropertyEntity(
					this.buildPropertyLinkPair(
							PROPERTY_UNIQUE_KEY, 
							structure.getStructure(), 
							METABOLITE_SMILE_LABEL, 
							METABOLITE_SMILE_RELATIONSHIP_TYPE));
		}
		centralMetaboliteEntity.addPropertyEntity(
				this.buildPropertyLinkPair(
						PROPERTY_UNIQUE_KEY, 
						entity.getDefaultCharge(), 
						METABOLITE_CHARGE_LABEL, 
						METABOLITE_CHARGE_RELATIONSHIP_TYPE));
	}

//	@Override
//	protected void configureCrossreferences(
//			CentralMetaboliteEntity centralMetaboliteEntity,
//			SeedMetaboliteEntity entity) {
//		
//		List<CentralMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
//		
//		for (SeedMetaboliteCrossreferenceEntity xref : entity.getCrossreferences()) {
//			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
//			String dbEntry = xref.getValue(); //Also need to translate if necessary
//			CentralMetaboliteProxyEntity proxy = new CentralMetaboliteProxyEntity();
//			proxy.setEntry(dbEntry);
//			proxy.setMajorLabel(dbLabel);
//			proxy.addLabel(METABOLITE_LABEL);
//			crossreferences.add(proxy);
//		}
//		
//		centralMetaboliteEntity.setCrossreferences(crossreferences);
//	}

}
