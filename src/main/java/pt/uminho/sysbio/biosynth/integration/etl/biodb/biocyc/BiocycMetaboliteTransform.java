package pt.uminho.sysbio.biosynth.integration.etl.biodb.biocyc;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;

public class BiocycMetaboliteTransform
extends AbstractMetaboliteTransform<BioCycMetaboliteEntity>{

//	private final String BIOCYC_P_COMPOUND_METABOLITE_LABEL;
	private final static Logger LOGGER = LoggerFactory.getLogger(BiocycMetaboliteTransform.class);
	
	private Map<String, String> biggInternalIdToEntryMap;
	
	public BiocycMetaboliteTransform(String majorLabel, Map<String, String> biggInternalIdToEntryMap) {
		super(majorLabel, new BiobaseMetaboliteEtlDictionary<>(BioCycMetaboliteEntity.class));
		this.biggInternalIdToEntryMap = biggInternalIdToEntryMap;
//		this.BIOCYC_P_COMPOUND_METABOLITE_LABEL = majorLabel;
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphMetaboliteEntity centralMetaboliteEntity,
			BioCycMetaboliteEntity entity) {
		
		this.configureGenericPropertyLink(centralMetaboliteEntity, entity.getCharge(), MetabolitePropertyLabel.Charge, MetaboliteRelationshipType.has_charge);
		this.configureGenericPropertyLink(centralMetaboliteEntity, entity.getInchi(), MetabolitePropertyLabel.InChI, MetaboliteRelationshipType.has_inchi);
		this.configureGenericPropertyLink(centralMetaboliteEntity, entity.getSmiles(), MetabolitePropertyLabel.SMILES, MetaboliteRelationshipType.has_smiles);

		for (String parent : entity.getParents()) {
			centralMetaboliteEntity.addConnectedEntity(
					this.buildPair(
					new SomeNodeFactory()
							.withEntry(parent)
							.withLabel(GlobalLabel.BioCyc)
							.buildGraphMetaboliteProxyEntity(MetaboliteMajorLabel.valueOf(majorLabel)), 
					new SomeNodeFactory().buildMetaboliteEdge(
							MetaboliteRelationshipType.instance_of)));
		}
		for (String instance : entity.getInstances()) {
			centralMetaboliteEntity.addConnectedEntity(
					this.buildPair(
					new SomeNodeFactory()
							.withEntry(instance)
							.withLabel(GlobalLabel.BioCyc)
							.buildGraphMetaboliteProxyEntity(MetaboliteMajorLabel.valueOf(majorLabel)), 
					new SomeNodeFactory().buildMetaboliteEdge(
							MetaboliteRelationshipType.parent_of)));
		}
		
//		centralMetaboliteEntity.addPropertyEntity(
//		this.buildPropertyLinkPair(
//				PROPERTY_UNIQUE_KEY, 
//				entity.getInchi(), 
//				METABOLITE_INCHI_LABEL, 
//				METABOLITE_INCHI_RELATIONSHIP_TYPE));
//centralMetaboliteEntity.addPropertyEntity(
//		this.buildPropertyLinkPair(
//				PROPERTY_UNIQUE_KEY, 
//				entity.getSmiles(), 
//				METABOLITE_SMILE_LABEL, 
//				METABOLITE_SMILE_RELATIONSHIP_TYPE));
//centralMetaboliteEntity.addPropertyEntity(
//		this.buildPropertyLinkPair(
//				PROPERTY_UNIQUE_KEY, 
//				entity.getCharge(), 
//				METABOLITE_CHARGE_LABEL, 
//				METABOLITE_CHARGE_RELATIONSHIP_TYPE));
//		centralMetaboliteEntity.addPropertyEntity(
//		this.buildPropertyLinkPair(
//				PROPERTY_UNIQUE_KEY, 
//				parent, 
//				SUPER_METABOLITE_LABEL, 
//				METABOLITE_INSTANCE_RELATIONSHIP_TYPE));
	}
	
	@Override
	protected void configureNameLink(
			GraphMetaboliteEntity centralMetaboliteEntity,
			BioCycMetaboliteEntity entity) {
		
		for (String name : entity.getSynonyms()) {
			configureNameLink(centralMetaboliteEntity, name);
		}
		
		super.configureNameLink(centralMetaboliteEntity, entity);
	}
	
	@Override
		protected void configureCrossreferences(
				GraphMetaboliteEntity centralMetaboliteEntity,
				BioCycMetaboliteEntity metabolite) {
			for (BioCycMetaboliteCrossreferenceEntity xref : metabolite.getCrossreferences()) {
				if (xref.getRef().toLowerCase().equals("bigg")) {
					if (biggInternalIdToEntryMap.containsKey(xref.getValue())) {
						xref.setValue(biggInternalIdToEntryMap.get(xref.getValue()));
					}
					LOGGER.debug("Internal Id replaced: " + xref);
				}
			}
			super.configureCrossreferences(centralMetaboliteEntity, metabolite);
		}

//	@Override
//	protected void configureCrossreferences(
//			CentralMetaboliteEntity centralMetaboliteEntity,
//			BioCycMetaboliteEntity entity) {
//
//		List<CentralMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
//		
//		for (BioCycMetaboliteCrossreferenceEntity xref : entity.getCrossreferences()) {
//			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
//			String dbEntry = xref.getValue(); //Also need to translate if necessary
//			CentralMetaboliteProxyEntity proxy = new CentralMetaboliteProxyEntity();
//			proxy.setEntry(dbEntry);
//			proxy.setMajorLabel(dbLabel);
//			proxy.putProperty("reference", xref.getRef());
//			proxy.addLabel(METABOLITE_LABEL);
//			crossreferences.add(proxy);
//		}
//		
//		centralMetaboliteEntity.setCrossreferences(crossreferences);
//	}

}
