package pt.uminho.sysbio.biosynth.integration.etl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphPropertyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphRelationshipEntity;
import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMember;
import pt.uminho.sysbio.biosynth.integration.io.dao.MetaboliteHeterogeneousDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;
import pt.uminho.sysbio.biosynthframework.util.FormulaConverter;

public class MetaboliteClusterQualityScreener implements EtlQualityScreen<IntegratedCluster> {

	private FormulaConverter formulaConverter;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MetaboliteClusterQualityScreener.class);
	
	private MetaboliteHeterogeneousDao<GraphMetaboliteEntity> metaboliteDao;
	
	@Autowired
	public MetaboliteClusterQualityScreener(
			MetaboliteHeterogeneousDao<GraphMetaboliteEntity> metaboliteDao, FormulaConverter formulaConverter) {
		this.formulaConverter = formulaConverter;
		this.metaboliteDao = metaboliteDao;
	}
	
	@Override
	public void evaluate(IntegratedCluster entity) {
		Set<MetaboliteQualityLabel> qualityLabels = this.yay(entity);
		
		System.out.println(qualityLabels);
	}
	
	public Set<MetaboliteQualityLabel> yay(IntegratedCluster integratedCluster) {
		
		List<GraphMetaboliteEntity> cpdList = new ArrayList<> ();
	
		LOGGER.debug("Load Metabolites ...");
		for (IntegratedClusterMember member : integratedCluster.getMembers()) {
			Long referenceId = member.getMember().getReferenceId();
			GraphMetaboliteEntity cpd = metaboliteDao.getMetaboliteById("", referenceId);
			
			if (cpd != null) {
				cpdList.add(cpd);
			}
		}
		
		return this.yay(cpdList);
	}
	
	public Set<MetaboliteQualityLabel> yay(List<GraphMetaboliteEntity> cpdList) {
		Set<MetaboliteQualityLabel> qualityLabels = new HashSet<> ();
		if (cpdList.isEmpty()) {
			LOGGER.warn("String empty cluster.");	
			return qualityLabels;
		}
		
		qualityLabels.addAll(verifyFormulas(cpdList));
		qualityLabels.addAll(verifyChemicalCharges(cpdList));
		qualityLabels.addAll(verifyStructure(cpdList));
		qualityLabels.addAll(verifyCrossreference(cpdList));
		
		return qualityLabels;
	}
	
	public Set<MetaboliteQualityLabel> verifyFormulas(List<GraphMetaboliteEntity> cpdList) {
		LOGGER.debug("Checking formulas ...");
		Set<MetaboliteQualityLabel> qualityLabels = new HashSet<> ();
		
		Map<Object, Integer> occurenceMap = this.collectPropertyKeys(cpdList, MetabolitePropertyLabel.MolecularFormula.toString());
		
		if (occurenceMap.size() == 1) {
			qualityLabels.add(MetaboliteQualityLabel.EXACT_FORMULA);
			return qualityLabels;
		}
		
		if (occurenceMap.isEmpty()) {
			qualityLabels.add(MetaboliteQualityLabel.NO_FORMULA);
			return qualityLabels;
		}
		
		
		List<Map<String, Integer>> atomMapList = new ArrayList<> ();
		for (Object formula_ : occurenceMap.keySet()) {
			String formula = (String) formula_;
			Map<String, Integer> atomMap = formulaConverter.getAtomCountMap(formula);
			LOGGER.trace(String.format("%s => %s", formula, atomMap));
			atomMapList.add(atomMap);
		}
		
		Map<String, Integer> atomMapPivot = atomMapList.get(0);
		for (int i = 1; i < atomMapList.size(); i++) {
			Map<String, Integer> atomMap = atomMapList.get(i);
			for (String atom : atomMapPivot.keySet()) {
				//pivot is never null
				Integer atomCountPivot = atomMapPivot.get(atom);
				//atom map may be null (if formula has distinct elements)
				Integer atomCount = atomMap.get(atom);
				if (atomCount == null) atomCount = 0;
				LOGGER.trace(String.format("Check atom %s frequency ... %d --> %d", atom, atomCountPivot, atomCount));
				if (atomCount != atomCountPivot) {
					if (atom.equals("H")) {
						LOGGER.trace("Hydrogen Mismatch");
						qualityLabels.add(MetaboliteQualityLabel.FORMULA_MISMATCH_HYDROGEN);
					} else {
						LOGGER.trace("Element Mismatch");
						qualityLabels.add(MetaboliteQualityLabel.FORMULA_MISMATCH);
					}
				}
			}
		}
		
		qualityLabels.add(MetaboliteQualityLabel.EXACT_FORMULA);
		
		return qualityLabels;
	}
	
	public Set<MetaboliteQualityLabel> verifyChemicalCharges(List<GraphMetaboliteEntity> cpdList) {
		LOGGER.debug("Checking chemical charge ...");
		Set<MetaboliteQualityLabel> qualityLabels = new HashSet<> ();
		
		Map<Object, Integer> occurenceMap = this.collectPropertyKeys(cpdList, MetabolitePropertyLabel.Charge.toString());
		
		if (occurenceMap.size() > 1) qualityLabels.add(MetaboliteQualityLabel.CHARGE_MISMATCH);
		
		return qualityLabels;
	}
	
	public Set<MetaboliteQualityLabel> verifyStructure(List<GraphMetaboliteEntity> cpdList) {
		LOGGER.debug("Checking structure ...");
		Set<MetaboliteQualityLabel> qualityLabels = new HashSet<> ();
		
		Map<Object, Integer> inchiOccurenceMap = this.collectPropertyKeys(cpdList, MetabolitePropertyLabel.InChI.toString());
		Map<Object, Integer> smilesOccurenceMap = this.collectPropertyKeys(cpdList, MetabolitePropertyLabel.SMILES.toString());
		
		if (inchiOccurenceMap.size() > 1) {
			qualityLabels.add(MetaboliteQualityLabel.INCHI_MISMATCH);
		}
		
		if (smilesOccurenceMap.size() > 1) {
			qualityLabels.add(MetaboliteQualityLabel.SMILES_MISMATCH);
		}
		
		return qualityLabels;
	}
	
	private Map<Object, Integer> collectPropertyKeys(List<GraphMetaboliteEntity> cpdList, String majorLabel) {
		Map<Object, Integer> occurenceMap = new HashMap<> ();
		
		for (GraphMetaboliteEntity cpd: cpdList) {
			for (Pair<GraphPropertyEntity, GraphRelationshipEntity> property : 
				cpd.getPropertyEntities()) {
				if (property.getLeft().getLabels().contains(majorLabel)) {
					LOGGER.trace("Found: " + property.getLeft());
					
					GraphPropertyEntity propertyEntity = property.getLeft(); 
					CollectionUtils.increaseCount(occurenceMap, propertyEntity.getProperty("key", null), 1);
				}
			}
		}
		
		LOGGER.debug("Total: " + occurenceMap);
		
		return occurenceMap;
	}
	
	public Set<MetaboliteQualityLabel> verifyCrossreference(List<GraphMetaboliteEntity> cpdList) {
		LOGGER.debug("Checking cross-reference ...");
		Set<MetaboliteQualityLabel> qualityLabels = new HashSet<> ();
		
		for (GraphMetaboliteEntity cpd : cpdList) {
			for (Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity> proxyEntity : cpd.getCrossreferences()) {
				System.out.println(proxyEntity);
			}
		}
		
		return qualityLabels;
	}
}
