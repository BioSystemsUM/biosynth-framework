package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.uminho.biosynth.core.algorithm.graph.BreadthFirstSearch;
import edu.uminho.biosynth.core.components.representation.basic.graph.DefaultBinaryEdge;
import edu.uminho.biosynth.core.components.representation.basic.graph.UndirectedGraph;
import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraDataDao;
import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraMetadataDao;
import edu.uminho.biosynth.core.data.integration.chimera.dao.IntegrationCollectionUtilities;
import edu.uminho.biosynth.core.data.integration.chimera.domain.CompositeMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedClusterMember;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMember;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.ClusteringStrategy;
import edu.uminho.biosynth.core.data.integration.generator.IKeyGenerator;

@Service
@Transactional(readOnly=true, value="chimerametadata")
public class ChimeraIntegrationServiceImpl implements ChimeraIntegrationService{

	private static Logger LOGGER = Logger.getLogger(ChimeraIntegrationServiceImpl.class);
	
	@Autowired
	private ChimeraDataDao data;
	@Autowired
	private ChimeraMetadataDao meta;
	
	private IntegrationSet currentIntegrationSet;
	
	@Override
	public IntegrationSet getCurrentIntegrationSet() { return currentIntegrationSet;}
	public void setCurrentIntegrationSet(IntegrationSet currentIntegrationSet) { this.currentIntegrationSet = currentIntegrationSet;}

	private IKeyGenerator<String> clusterIdGenerator;
	
	public ChimeraDataDao getData() { return data;}
	public void setData(ChimeraDataDao data) { this.data = data;}

	public ChimeraMetadataDao getMeta() { return meta;}
	public void setMeta(ChimeraMetadataDao meta) { this.meta = meta;}
	
	public IKeyGenerator<String> getClusterIdGenerator() { return clusterIdGenerator;}
	public void setClusterIdGenerator(IKeyGenerator<String> clusterIdGenerator) { this.clusterIdGenerator = clusterIdGenerator;}
	
	@Override
	public IntegrationSet getIntegrationSetByEntry(String entry) {
		IntegrationSet integrationSet = this.meta.getIntegrationSet(entry);
		
		if (integrationSet == null) {
			LOGGER.warn(String.format("Integration Set [%s] not found", entry));
			return null;
		}
		
		return integrationSet;
	}
	
	@Override
	public IntegrationSet getIntegrationSetById(Long id) {
		IntegrationSet integrationSet = this.meta.getIntegrationSet(id);
		
		if (integrationSet == null) {
			LOGGER.warn(String.format("Integration Set [%d] not found", id));
			return null;
		}
		
		return integrationSet;
	}
	
	@Override
	public IntegrationSet createNewIntegrationSet(String name, String description) {
		//check if name exists
		//whine if exists
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName(name);
		integrationSet.setDescription(description);
		meta.saveIntegrationSet(integrationSet);
		
		return integrationSet;
	}

	@Override
	public IntegrationSet changeIntegrationSet(Long id) {
		IntegrationSet integrationSet = this.meta.getIntegrationSet(id);
		
		if (integrationSet == null) {
			LOGGER.warn(String.format("Integration Set [%d] not found", id));
			return null;
		}
		
		this.currentIntegrationSet = integrationSet;
		LOGGER.info(String.format("Current Integration Set changed to %s", this.currentIntegrationSet));
		
		return integrationSet;
	}
	
	@Override
	public IntegrationSet changeIntegrationSet(String id) {
		IntegrationSet integrationSet = this.meta.getIntegrationSet(id);
		
		if (integrationSet == null) {
			LOGGER.warn(String.format("Integration Set [%s] not found", id));
			return null;
		}
		
		this.currentIntegrationSet = integrationSet;
		LOGGER.info(String.format("Current Integration Set changed to %s", this.currentIntegrationSet));
		
		return integrationSet;
	}
	
	@Override
	public void resetIntegrationSet(IntegrationSet integrationSet) {
		if (integrationSet == null) {
//			LOGGER.warn("No Integration Set selected - operation aborted");
			return;
		}
		LOGGER.info(String.format("Reset atempt to Integration Set %s", this.currentIntegrationSet));
		
		List<Long> clusters = new ArrayList<> (integrationSet.getIntegratedClustersMap().keySet());
		for (Long clusterId : clusters){
			IntegratedCluster cluster = integrationSet.getIntegratedClustersMap().remove(clusterId);
			LOGGER.info(String.format("Removing cluster %s", cluster));
			this.meta.deleteCluster(cluster);
		}
		
//		this.meta.saveIntegrationSet(currentIntegrationSet);
	}

	@Override
	public void deleteIntegrationSet(IntegrationSet integrationSet) {
		this.meta.deleteIntegrationSet(integrationSet);
	}
	
	public IntegratedCluster createCluster(String name, Set<Long> elements, String description) {
		if (elements.isEmpty()) return null;

		Set<Long> clusterElements = elements;
		IntegratedCluster cluster = new IntegratedCluster();
		cluster.setIntegrationSet(this.currentIntegrationSet);
		cluster.setName(name);
		cluster.setDescription(description);
		
		for (Long memberId: clusterElements) {
			IntegratedMember member = new IntegratedMember();
			member.setId(memberId);
			this.meta.saveIntegratedMember(member);
			
			IntegratedClusterMember clusterMember = new IntegratedClusterMember();
			clusterMember.setCluster(cluster);
			clusterMember.setMember(member);
			
			cluster.getMembers().add(clusterMember);
		}
		
		this.meta.saveIntegratedCluster(cluster);
		
		return cluster;
	}
	
	public IntegratedCluster updateCluster(IntegratedCluster integratedCluster, String name, Set<Long> elements, String description, Long iid) {
		LOGGER.debug(String.format("Updated Integrated Cluster[%d]", integratedCluster.getId()));
		
		return integratedCluster;
	}
	
	public IntegratedCluster mergeCluster(Set<Long> cidList, String name, Set<Long> elements, String description, Long iid) {
//		for ()
//		IntegratedCluster cluster = this.meta.getIntegratedClusterById(cid);
		
		LOGGER.debug(String.format("Merged Integrated Clusterd%s", cidList));
		
		return null;
	}
	
	public IntegratedCluster createCluster(String name, Set<Long> elements, String description, Long iid) {
		if (elements.isEmpty()) return null;

		Set<Long> clusterElements = elements;
		IntegratedCluster cluster = new IntegratedCluster();
		cluster.setIntegrationSet(this.meta.getIntegrationSet(iid));
		cluster.setName(name);
		cluster.setDescription(description);
		
		for (Long memberId: clusterElements) {
			IntegratedMember member = new IntegratedMember();
			member.setId(memberId);
			this.meta.saveIntegratedMember(member);
			
			IntegratedClusterMember clusterMember = new IntegratedClusterMember();
			clusterMember.setCluster(cluster);
			clusterMember.setMember(member);
			
			cluster.getMembers().add(clusterMember);
		}
		
		this.meta.saveIntegratedCluster(cluster);
		
		LOGGER.debug(String.format("Created Integrated Cluster[%d]", cluster.getId()));
		
		return cluster;
	}
	
	public List<IntegratedCluster> pageClusters(Long iid, int firstResult, int maxResults) {
		List<IntegratedCluster> clusters = this.meta.getIntegratedClustersByPage(iid, firstResult, maxResults);
		return clusters;
	}

	@Override
	public IntegratedCluster createCluster(String query) {
		Map<Long, Long> assignedMembers = this.meta.getAllAssignedIntegratedMembers(this.currentIntegrationSet.getId());
		List<Long> memberIdList = new ArrayList<> (assignedMembers.keySet());
		Set<Long> clusterElements = new HashSet<> (this.data.getClusterByQuery(query));
		memberIdList.retainAll(clusterElements);
		if (!memberIdList.isEmpty()) {
			LOGGER.warn(String.format("Create Cluster [%s] FAILED. Membership conflict %s", query, memberIdList));
			return null;
		}
		LOGGER.debug(String.format("[%s] generated %d elements", query, clusterElements.size()));
		return this.createCluster(clusterIdGenerator.generateKey(), clusterElements, query);
	}
	
	public void createClusterCascade(String query) {
		List<Long> compoundIds = this.data.getAllMetaboliteIds();
		List<Long> visitedIds = new ArrayList<> ();
		for (Long id: compoundIds) {
			if (!visitedIds.contains(id)) {
				String query_ = String.format(query, id);
				System.out.println(query_);
				Set<Long> clusterElements = new HashSet<> (this.data.getClusterByQuery(query_));
				visitedIds.addAll(clusterElements);
				this.createCluster(clusterIdGenerator.generateKey(), clusterElements, query_);
				System.out.println("Generated Cluster: root -> " + id + " size -> " + clusterElements.size());
			}
		}
		//error id merge !
	}
	
	public List<IntegratedCluster> createClusterCascade(ClusteringStrategy strategy, List<Long> elementsToCascade) {
		List<IntegratedCluster> res = new ArrayList<> ();

		List<Long> visitedIds = new ArrayList<> ();
		for (Long id: elementsToCascade) {
			if (!visitedIds.contains(id)) {
				strategy.setInitialNode(id);
				IntegratedCluster cluster = this.createCluster(strategy);
				for (IntegratedClusterMember elements:cluster.getMembers()) {
					visitedIds.add(elements.getMember().getId());
				}
				res.add(cluster);
//				this.createCluster(clusterIdGenerator.generateKey(), clusterElements, query_);
				System.out.println("Generated Cluster: root -> " + id + " size -> " + cluster.getMembers().size());
			}
		}
		
		return res;
	}

	private IntegratedCluster mergeCluster(IntegratedCluster integratedCluster) {
		if (integratedCluster == null) return null;
		
		Set<Long> clusterMembers = new HashSet<> (integratedCluster.listAllIntegratedMemberIds());
		
		//Detect Collisions
		List<IntegratedCluster> collision = 
				this.meta.getIntegratedClusterByMemberIds(clusterMembers.toArray(new Long[0]));
		//Union All Collisions
		if (collision.isEmpty()) return integratedCluster;
		
		LOGGER.warn(String.format("MERGE with Membership conflict %s performing cluster join.", collision));
		
		if (collision.size() == 1) {
			IntegratedCluster cluster = collision.iterator().next();
			if (cluster.listAllIntegratedMemberIds().containsAll(clusterMembers)) {
				return cluster;
			}
		}
		
		for (IntegratedCluster c : collision) {
			clusterMembers.addAll(c.listAllIntegratedMemberIds());
			this.meta.deleteCluster(c);
		}
		
		IntegratedCluster union = this.generateCluster(integratedCluster.getName(), clusterMembers, integratedCluster.getDescription());
		this.meta.saveIntegratedCluster(union);
		
		return union;
	}
	
	@Override
	public IntegratedCluster mergeCluster(String query) {
		IntegratedCluster integratedCluster = this.generateCluster(query);
		
		return this.mergeCluster(integratedCluster);
	}
	
	@Override
	public IntegratedCluster mergeCluster(ClusteringStrategy strategy) {
		IntegratedCluster integratedCluster = this.generateCluster(strategy);
		
		return this.mergeCluster(integratedCluster);
		
		
//		Map<Long, Long> assignedMembers = this.meta.getAllAssignedIntegratedMembers(this.currentIntegrationSet.getId());
//		List<Long> memberIdList = new ArrayList<> (assignedMembers.keySet());
//		List<Long> clusterElements = strategy.execute();
//		memberIdList.retainAll(clusterElements);
//		Set<Long> joinClusterList = new HashSet<> ();
//		if (!memberIdList.isEmpty()) {
//			LOGGER.warn(String.format("Merge Cluster [%s] WITH. Membership conflict %s performing cluster join.", strategy.toString(), memberIdList));
//			
//			for (Long memberId: memberIdList) {
//				joinClusterList.add(assignedMembers.get(memberId));
////				LOGGER.warn(String.format("Cluster %d marked as candidate", memberIdList));
//			}
//			for (Long clusterId: joinClusterList) {
////				IntegratedCluster cluster = this.meta.getIntegratedCluster();
//				//ADD ALL IDS TO clusterElements
//				//DELETE CLUSTER
//			}
//			
//		}
//		LOGGER.debug(String.format("%s obtained %d elements", strategy, clusterElements.size()));
//		return this.createCluster(clusterIdGenerator.generateKey(), clusterElements, strategy.toString());
	}
	
	@Override
	public IntegratedCluster mergeCluster(String name, Set<Long> elements,
			String description) {
		IntegratedCluster integratedCluster = this.generateCluster(name, elements, description);
		
		return this.mergeCluster(integratedCluster);
	}
	
	public List<IntegratedCluster> mergeClusterCascade(String query, List<Long> elementsToCascade) {
		List<Long> clusterIds = new ArrayList<> ();
		
		List<Long> visitedIds = new ArrayList<> ();
		for (Long id: elementsToCascade) {
			if (!visitedIds.contains(id)) {
				String query_ = String.format(query, id);
				IntegratedCluster integratedCluster = this.mergeCluster(query_);
				if (integratedCluster != null) {
					visitedIds.addAll(integratedCluster.listAllIntegratedMemberIds());
					clusterIds.add(integratedCluster.getId());
				}
			}
		}
		
		List<IntegratedCluster> res = new ArrayList<> ();
		for (Long clusterId : clusterIds) {
			IntegratedCluster integratedCluster = this.meta.getIntegratedClusterById(clusterId);
			if (integratedCluster != null) res.add(integratedCluster);
		}
		
		return res;
	}
	
	public List<IntegratedCluster> ai(ClusteringStrategy strategy, List<Long> elementsToCascade) {
		long start, end;
		Map<String, Set<Long>> tempClustersGenerated = new HashMap<> ();
		
System.out.println("Generating New Clusters");
		start = System.currentTimeMillis();
		Set<Long> visitedIds = new HashSet<> ();
		for (Long elementId : elementsToCascade) {
			if (!visitedIds.contains(elementId)) {
				strategy.setInitialNode(elementId);
				Set<Long> clusterElements = strategy.execute();
				if (!clusterElements.isEmpty()) {
					visitedIds.addAll(clusterElements);
					String clusterEntry = this.clusterIdGenerator.generateKey();
					System.out.println(clusterEntry);
					tempClustersGenerated.put(clusterEntry, new HashSet<> (clusterElements));
				}
			}
		}
end = System.currentTimeMillis();
System.out.println("Ok ! [" + (end - start) + "]");
		
System.out.println("Resolving Conflicts");
System.out.println("Before " + tempClustersGenerated.keySet().size());
start = System.currentTimeMillis();
		Map<String, Set<Long>> resolvedClusters = IntegrationCollectionUtilities.resolveConflicts2(
				tempClustersGenerated);
		end = System.currentTimeMillis();
System.out.println("After " + resolvedClusters.keySet().size());
System.out.println("Ok ! [" + (end - start) + "]");

System.out.println("Loading Prev Clusters");
start = System.currentTimeMillis();
		for (IntegratedCluster integratedCluster : this.meta.getAllIntegratedClusters(this.currentIntegrationSet.getId())) {
			String clusterEntry = integratedCluster.getName();
			Set<Long> clustersElements = new HashSet<> (integratedCluster.listAllIntegratedMemberIds());
			resolvedClusters.put(clusterEntry, clustersElements);
		}
end = System.currentTimeMillis();
System.out.println("Ok ! [" + (end - start) + "]");

System.out.println("Resolving Db Conflicts");
System.out.println("Before " + resolvedClusters.keySet().size());
start = System.currentTimeMillis();
		Map<String, Set<Long>> finalClusters = IntegrationCollectionUtilities.resolveConflicts2(
				resolvedClusters);
end = System.currentTimeMillis();
System.out.println("After " + finalClusters.keySet().size());
System.out.println("Ok ! [" + (end - start) + "]");
		
System.out.println("Update Clusters");
start = System.currentTimeMillis();
		
end = System.currentTimeMillis();
System.out.println("Ok ! [" + (end - start) + "]");
		return null;
	}
	
	public List<IntegratedCluster> mergeClusterCascade(ClusteringStrategy strategy, List<Long> elementsToCascade) {
		Map<Long, Set<Long>> clusterIdToClusterElements = new HashMap<> ();
		//Newly generated Clusters
		Map<Long, Set<Long>> elementsToClusterIds_ = new HashMap<> ();
		//Previous generated Clusters shouldbe Long x Long
		Map<Long, Long> elementsToClusterIds = new HashMap<> ();
		
		Set<Long> visitedIds = new HashSet<> ();
		
		long i = 0; //this.meta.getLastClusterEntry(this.currentIntegrationSet.getId());
		for (Long elementId : elementsToCascade) {
			
			if (!visitedIds.contains(elementId)) {
				strategy.setInitialNode(elementId);
				Set<Long> clusterElements = strategy.execute();
				
				if (!clusterElements.isEmpty()) {
				
						visitedIds.addAll(clusterElements);
						clusterIdToClusterElements.put(i++, new HashSet<> (clusterElements));
						for (Long eid : clusterElements) {
							elementsToClusterIds_.put(eid, new HashSet<Long> ());
						}
				
				}
//				LOGGER.trace(String.format("%d/%d", visitedIds.size(), elementsToCascade.size()));
			}
		}
		
		System.out.println("Done !");
		System.out.println("Solving Query Conflicts");
		
		Set<Long> deleted = new HashSet<> ();
		Map<Long, Long> elementsToClustersFixed = IntegrationCollectionUtilities.resolveConflicts(
				clusterIdToClusterElements, null, deleted);
		System.out.println("These died ... " + deleted);
		
		for (Long integratedClusterId : this.meta.getAllIntegratedClusterIds(this.currentIntegrationSet.getId())) {
			IntegratedCluster integratedCluster = this.meta.getIntegratedClusterById(integratedClusterId);
			for (Long eid : integratedCluster.listAllIntegratedMemberIds()) {
				elementsToClusterIds.put(eid, integratedClusterId);
			}
		}
		
		System.out.println(String.format("ElementsToClusterIds Size %d", elementsToClusterIds.size()));
		
		Map<Long, Set<Long>> conflictMapDbVsGenerated = new HashMap<> ();
		for (Long eid : elementsToClusterIds.keySet()) {
			conflictMapDbVsGenerated.put(eid, new HashSet<Long> ());
		}
		for (Long eid : elementsToClustersFixed.keySet()) {
			conflictMapDbVsGenerated.put(eid, new HashSet<Long> ());
		}
		for (Long eid : elementsToClusterIds.keySet()) {
			conflictMapDbVsGenerated.get(eid).add(elementsToClusterIds.get(eid));
		}
		for (Long eid : elementsToClustersFixed.keySet()) {
			conflictMapDbVsGenerated.get(eid).add(elementsToClustersFixed.get(eid));
		}
		
//		Map<Long, Long> finalClusters = IntegrationCollectionUtilities.resolveConflicts(
//				clusterIdToClusterElements, null, deleted);
//		System.out.println("These died ... " + deleted);

//		int progress = 0;
//		int total = elementsToCascade.size();
//		
//		List<Long> clusterIds = new ArrayList<> ();
//		
//		List<Long> visitedIds = new ArrayList<> ();
//		for (Long id: elementsToCascade) {
//			LOGGER.trace(String.format("%d/%d", ++progress, total));
//			if (!visitedIds.contains(id)) {
//				strategy.setInitialNode(id);
//				IntegratedCluster integratedCluster = this.mergeCluster(strategy);
//				if (integratedCluster != null) {
//					visitedIds.addAll(integratedCluster.listAllIntegratedMemberIds());
//					clusterIds.add(integratedCluster.getId());
//				}
//			}
//		}
//		
//		List<IntegratedCluster> res = new ArrayList<> ();
//		for (Long clusterId : clusterIds) {
//			IntegratedCluster integratedCluster = this.meta.getIntegratedClusterById(clusterId);
//			if (integratedCluster != null) res.add(integratedCluster);
//		}
		
		return null;
	}
	
	@Override
	public List<IntegrationSet> getAllIntegrationSets() {
		List<IntegrationSet> res = new ArrayList<> ();
		for (Serializable id: this.meta.getAllIntegrationSetsId()) {
			res.add(this.meta.getIntegrationSet(id));
		}
		return res;
	}
	
	@Override
	public Map<String, Integer> getDataStatistics() {
		Map<String, Integer> res = new HashMap<> ();
		for (String property: this.data.getAllProperties()) {
			res.put(property, this.data.listAllPropertyIds(property).size());
		}
		System.out.println("RETURNED THIS $$$$$$$$$$" + res);
		return res;
	}
	
	@Override
	public IntegratedCluster createCluster(ClusteringStrategy strategy) {
		Map<Long, Long> assignedMembers = this.meta.getAllAssignedIntegratedMembers(this.currentIntegrationSet.getId());
		List<Long> memberIdList = new ArrayList<> (assignedMembers.keySet());
		Set<Long> clusterElements = strategy.execute();
		memberIdList.retainAll(clusterElements);
		if (!memberIdList.isEmpty()) {
			LOGGER.warn(String.format("Create Cluster [%s] FAILED. Membership conflict %s", strategy.toString(), memberIdList));
			return null;
		}
		LOGGER.debug(String.format("[%s] generated %d elements", strategy, clusterElements.size()));
		return this.createCluster(clusterIdGenerator.generateKey(), clusterElements, strategy.toString()); 
	}
	
	
	public List<IntegratedCluster> splitClusterByProperty(Long clusterId, String property, String field) {
		List<IntegratedCluster> res = new ArrayList<> ();
		IntegratedCluster cluster = this.meta.getIntegratedClusterById(clusterId);
		Map<Object, Set<Long>> propertyMemberMap = new HashMap<> ();
		for (IntegratedClusterMember members : cluster.getMembers()) {
			Long memberId = members.getMember().getId();
			CompositeMetaboliteEntity cpd = this.data.getCompositeMetabolite(memberId);
			if (cpd.getProperties().containsKey(property)) {
				String value = (String) cpd.getProperties().get(property).get(field);
				if (!propertyMemberMap.containsKey(value)) {
					propertyMemberMap.put(value, new HashSet<Long> ());
				}
				propertyMemberMap.get(value).add(memberId);
			}
		}
		
		System.out.println(propertyMemberMap);
		return res;
	}
	
	@Override
	public List<Long> listAllIntegratedCompounds() {
		return this.meta.getAllIntegratedClusterMembersId();
	}
	
	@Override
	public List<Long> listAllUnintegratedCompounds() {
		Set<Long> res = new HashSet<> (this.data.getAllMetaboliteIds());
		Set<Long> integrated = new HashSet<> (this.listAllIntegratedCompounds());
		res.removeAll(integrated);
		return new ArrayList<> (res);
	}
	
	
	public IntegratedCluster generateCluster(String name, Set<Long> elements, String description) {
		Set<Long> clusterElements = elements;
		
		if (clusterElements.isEmpty()) return null;
		
		IntegratedCluster integratedCluster = new IntegratedCluster();
		integratedCluster.setIntegrationSet(this.currentIntegrationSet);
		integratedCluster.setName(name);
		integratedCluster.setDescription(description);
		
		for (Long memberId: clusterElements) {
			IntegratedMember member = new IntegratedMember();
			member.setId(memberId);
			this.meta.saveIntegratedMember(member);
			
			IntegratedClusterMember clusterMember = new IntegratedClusterMember();
			clusterMember.setCluster(integratedCluster);
			clusterMember.setMember(member);
			
			integratedCluster.getMembers().add(clusterMember);
		}
		
		return integratedCluster; 
	}
	
	public IntegratedCluster generateCluster(ClusteringStrategy clusteringStrategy) {
		Set<Long> clusterElements = clusteringStrategy.execute();
		
		if (clusterElements.isEmpty()) return null;
		
		return this.generateCluster( this.clusterIdGenerator.generateKey(), clusterElements, clusteringStrategy.toString()); 
	}
	
	public IntegratedCluster generateCluster(String query) {
		Set<Long> clusterElements = new HashSet<> (this.data.getClusterByQuery(query));
		
		if (clusterElements.isEmpty()) return null;

		return this.generateCluster( this.clusterIdGenerator.generateKey(), clusterElements, query); 
	}
	@Override
	public int countIntegratedClustersByIntegrationId(Long iid) {
		return this.meta.getIntegrationSet(iid).getIntegratedClustersMap().size();
	}
	
	@Override
	public List<IntegratedCluster> generateIntegratedClusters(
			Long iid,
			ClusteringStrategy clusteringStrategy,
			Set<Long> initial, Set<Long> domain,
			ConflictDecision conflictDecision) {
		
		
		
		//Generate Clusters
		List<Set<Long>> generatedClusters = this.generateClusters(clusteringStrategy, initial, domain);
		LOGGER.info(String.format("Generated an initial of %d clusters.", generatedClusters.size()));
		//Fix Membership Conflict
		List<Set<Long>> uniqueMembershipClusters = this.resolveMembershipConflict(generatedClusters);
		LOGGER.info(String.format("Resolved initial clusters merge. Clusters reduced to %d from %d", uniqueMembershipClusters.size(), generatedClusters.size()));
		//Load database clusters
		Map<Long, Set<Long>> prevClusters = new HashMap<> ();
		for (IntegratedCluster integratedCluster : this.meta.getAllIntegratedClusters(iid)) {
			Long cid = integratedCluster.getId();
			Set<Long> clustersElements = new HashSet<> (integratedCluster.listAllIntegratedMemberIds());
			prevClusters.put(cid, clustersElements);
		}
		LOGGER.info(String.format("Resolved initial clusters merge. Clusters reduced to %d from %d", uniqueMembershipClusters.size(), generatedClusters.size()));
		
		//Apply conflict decision
		List<Set<Long>> newClusters = new ArrayList<> ();
		Map<Set<Long>, Set<Long>> toMerge = new HashMap<> ();
		Set<Long> unaffected = new HashSet<> ();
		
		this.resolveMerging(uniqueMembershipClusters, prevClusters, newClusters, toMerge, unaffected);
		
		LOGGER.info(String.format("Resolved merging sets. New Clusters %d. Unaffected %d. To merged %d.", 
				newClusters.size(), unaffected.size(), toMerge.size()));
		
		switch (conflictDecision) {
			case ABORT:
				//Warn conflict case and abort operation
				if (!toMerge.isEmpty()) {
					return null;
				}
				break;
				
			case SKIP:
				//Warn skipped clusters
				break;
			case UPDATE:
				//Warn merge
				//Update Clusters
				for (Set<Long> cidList : toMerge.keySet()) {
					if (cidList.size() > 1) {
						LOGGER.warn(String.format("", cidList));
					} else if (cidList.size() == 1) {
						
					} else {
						LOGGER.error(String.format("ZERO ELEMENT !", cidList));
					}
				}
				break;
			case MERGE:
				//Update And Merge Clusters
				for (Set<Long> cidList : toMerge.keySet()) {
					if (cidList.size() > 0) {
						if (cidList.size() == 1) {
							IntegratedCluster cluster = this.meta.getIntegratedClusterById(cidList.iterator().next());
							this.updateCluster(cluster, cluster.getName(), toMerge.get(cidList), cluster.getDescription(), iid);
						} else {
							IntegratedCluster cluster = this.meta.getIntegratedClusterById(cidList.iterator().next());
							this.mergeCluster(cidList, cluster.getName(), toMerge.get(cidList), cluster.getDescription(), iid);
						}
						//SOME MERGE FUNCTION (select a single CID join rest and delete prevs)
					} else {
						LOGGER.error(String.format("ZERO ELEMENT !", cidList));
					}
				}
				break;
			default:
				throw new RuntimeException(String.format("Unimplemented Decision %s", conflictDecision));
		}
		
		//Create New Clusters
		for (Set<Long> cluster : newClusters) {
			String entry = this.clusterIdGenerator.generateKey();
//			System.out.println("CREATE " + entry);
			this.createCluster(entry, cluster, String.format("%s[%s]", "strategy", "initialNode"), iid);
		}
		
		return null;
	}
	
	
	private void resolveMerging(
			//Input
			List<Set<Long>> uniqueMembershipClusters,
			Map<Long, Set<Long>> prevClusters,
			//Output Collections
			List<Set<Long>> newClusters, 
			Map<Set<Long>, Set<Long>> toMerge, 
			Set<Long> unaffected) {
		
		System.out.println("prev clusters " + prevClusters.size());
		
		Map<Long, Long> elementToClusterId = new HashMap<> ();
		for (Long cid : prevClusters.keySet()) {
			for (Long eid : prevClusters.get(cid)) {
				if (cid == null || eid == null) {
					throw new RuntimeException("Error null id found " + cid + prevClusters.get(cid));
				}
				elementToClusterId.put(eid, cid);
			}
		}
		
		UndirectedGraph<Long, Integer> graph = new UndirectedGraph<>();
		Integer counter = 0;
		Set<Long> eids = new HashSet<> ();
		for (Set<Long> cluster : uniqueMembershipClusters) {
			Long prev = null;
			for (Long eid : cluster) {
				eids.add(eid);
				if (prev != null) {
					DefaultBinaryEdge<Integer, Long> edge = new DefaultBinaryEdge<>(counter++, prev, eid);
					graph.addEdge(edge);
				} else if (cluster.size() < 2) {
					graph.addVertex(eid);
				}
				prev = eid;
			}
		}
		
		
		Set<Set<Long>> oldClusters = new HashSet<> ();
		Set<Long> oldElements = new HashSet<> ();
		for (Set<Long> cluster : prevClusters.values()) {
			oldClusters.add(cluster);
			oldElements.addAll(cluster);
			Long prev = null;
			for (Long eid : cluster) {
				eids.add(eid);
				if (prev != null) {
					DefaultBinaryEdge<Integer, Long> edge = new DefaultBinaryEdge<>(counter++, prev, eid);
					graph.addEdge(edge);
				} else if (cluster.size() < 2) {
					graph.addVertex(eid);
				}
				prev = eid;
			}
		}
		
		Set<Long> eidsProcessed = new HashSet<> ();
		for (Long eid : eids) {
			if (!eidsProcessed.contains(eid)) {
				Set<Long> cluster = BreadthFirstSearch.run(graph, eid);
				
				if (!cluster.isEmpty()) {
					eidsProcessed.addAll(cluster);
					
					Set<Long> aux_ = new HashSet<> (cluster);
					aux_.retainAll(oldElements);
					
					//Unaffected if ALL COLOR BLACK
					//New if ALL COLOR RED
					//Merge if Mixed Color
					
					//Test if the cluster have previous elements
					if (aux_.isEmpty()) {
						//if all elements are new then the entire cluster is new
						newClusters.add(cluster);
					} else if (oldClusters.contains(cluster)){
						//if previous clusters have an identical cluster
						unaffected.add(elementToClusterId.get(cluster.iterator().next()));
					} else {
						//Else there are elements from previous clusters
						//if can be either an update
						//or update and delete (if more than one CID is present)
						Set<Long> cidList = new HashSet<> ();
						for (Long eid_ : cluster) {
							Long cid_;
							if ((cid_ = elementToClusterId.get(eid_)) != null) {
								cidList.add(cid_);
							}
						}
						toMerge.put(cidList, cluster);
					}
				}
			}
		}
	}
	
	/**
	 * Given a list of clusters (sets) merge clusters that share common elements.
	 * @param clusterList a list of clusters (sets)
	 * @return a list of clusters such that each element belongs to only a single element.
	 */
	private List<Set<Long>> resolveMembershipConflict(List<Set<Long>> clusterList) {
		List<Set<Long>> uniqueMembershipClusters = new ArrayList<> ();
		
		UndirectedGraph<Long, Integer> graph = new UndirectedGraph<>();
		Integer counter = 0;
		Set<Long> eids = new HashSet<> ();
		for (Set<Long> cluster : clusterList) {
			Long prev = null;
			if (cluster.size() > 1) {
				for (Long eid : cluster) {
					eids.add(eid);
					if (prev != null) {
						DefaultBinaryEdge<Integer, Long> edge = new DefaultBinaryEdge<>(counter++, prev, eid);
						graph.addEdge(edge);
					}
					prev = eid;
				}
			} else {
				for (Long eid : cluster) {
					eids.add(eid);
					graph.addVertex(eid);
				}
			}
		}
		
		Set<Long> eidsProcessed = new HashSet<> ();
		for (Long eid : eids) {
			if (!eidsProcessed.contains(eid)) {
				Set<Long> cluster = BreadthFirstSearch.run(graph, eid);
				eidsProcessed.addAll(cluster);
				uniqueMembershipClusters.add(cluster);
//				CID cid = eidToCid.get(cluster.iterator().next()).iterator().next();
//				cidsSurvived.add(cid);
//				for (EID eid_ : cluster) {
//					res.put(eid_, cid);
//				}
//				System.out.println(cluster);
			}
		}
		
		return uniqueMembershipClusters;
	}
	
	/**
	 * For each element <i>i</i> in initial, apply <i>clusteringStrategy</i> to generate the corresponding cluster.
	 * @param clusteringStrategy algorithm to perform the clustering decision
	 * @param initial the initial element
	 * @param domain the domain of the decision
	 * @return a set of clusters (sets)
	 */
	private List<Set<Long>> generateClusters(
			ClusteringStrategy clusteringStrategy,
			Set<Long> initial, Set<Long> domain) {
		
		List<Set<Long>> generatedClusters = new ArrayList<> ();
		
		Set<Long> visitedIds = new HashSet<> ();
		for (Long i : initial) {
			if (!visitedIds.contains(i)) {
				clusteringStrategy.setInitialNode(i);
				Set<Long> clusterElements = clusteringStrategy.execute();
				clusterElements.retainAll(domain);
				if (!clusterElements.isEmpty()) {
					visitedIds.addAll(clusterElements);
					generatedClusters.add(clusterElements);
				}
			}
		}
		
		return generatedClusters;
	}


}
