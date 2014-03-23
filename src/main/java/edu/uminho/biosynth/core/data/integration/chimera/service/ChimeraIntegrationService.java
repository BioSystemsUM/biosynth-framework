package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.List;
import java.util.Map;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;


public interface ChimeraIntegrationService {
	public IntegrationSet createNewIntegrationSet(String name, String description);
	public List<IntegrationSet> getAllIntegrationSets();
	public void changeIntegrationSet(Long id);
	public void changeIntegrationSet(String id);
	public void resetIntegrationSet();
	public void deleteIntegrationSet();
	public void createCluster(String query);
	public void mergeCluster(String query);
	public void mergeCluster(ClusteringStrategy generator);
	public IntegrationSet getCurrentIntegrationSet();
	public Map<String, Integer> getDataStatistics();
}
