package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.DefaultBinaryEdge;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.UndirectedGraph;
import pt.uminho.sysbio.metropolis.network.graph.algorithm.BreadthFirstSearch;

public class IntegrationCollectionUtilities {

	private final static Logger LOGGER = LoggerFactory.getLogger(IntegrationCollectionUtilities.class);
	
	public static<K, V> Map<V, Set<K>> invertMapKeyToSet(Map<K, Set<V>> map) {
		Map<V, Set<K>> res = new HashMap<> ();
		for (K key : map.keySet()) {
			for (V value : map.get(key)) {
				res.put(value, new HashSet<K> ());
			}
		}
		for (K key : map.keySet()) {
			for (V value : map.get(key)) {
				res.get(value).add(key);
			}
		}
		
		return res;
	}
	
	public static<K,V> Map<V, Set<K>> invertMap(Map<K,V> map) {
		Map<V, Set<K>> res = new HashMap<> ();
		for (K k : map.keySet()) {
			V v = map.get(k);
			if (!res.containsKey(v)) res.put(v, new HashSet<K> ());
			res.get(v).add(k);
		}
		return res;
	}
	
	/**
	 * Resolve conflicts between clusters.
	 * Let C be a cluster defined by a set of elements E. Every cluster that 
	 * shares the same element must be merged together.
	 * 
	 * Example: C<sub>1</sub> = [a, c, f], C<sub>2</sub> = [h, k], C<sub>3</sub> = [a, n, m],
	 * then C<sub>1</sub> must be merged with C<sub>3</sub> since they have the <b>a</b>
	 *  element in common.
	 * 
	 * @param clusterMap is the map that maps cluster id to the set of its elements.
	 * @param elementToClusterMap is the map that maps elements to clusters that they belong.
	 * @return a map that maps each element to a single cluster
	 */
	public static<EID, CID> Map<EID, CID> resolveConflicts(Map<CID, Set<EID>> clusterMap,
			Collection<CID> survived, Collection<CID> deleted) {
		
		Map<EID, Set<CID>> eidToCid = invertMapKeyToSet(clusterMap);
		
		/**
		 * Build graph
		 * res  : solution the map that maps each EID to a single CID
		 * eids : all eids to map
		 * graph: the graph
		 */
		Map<EID, CID> res = new HashMap<> ();
		Set<EID> eids = new HashSet<> ();
		UndirectedGraph<EID, Integer> graph = new UndirectedGraph<>();
		Integer counter = 0;
		for (CID cid : clusterMap.keySet()) {
			EID prev = null;
			Set<EID> cluster = clusterMap.get(cid);
			if (cluster.size() == 1) {
				graph.addVertex(cluster.iterator().next());
			} else {
				for (EID eid : clusterMap.get(cid)) {
					eids.add(eid);
					if (prev != null) {
						DefaultBinaryEdge<Integer, EID> edge = new DefaultBinaryEdge<>(counter++, prev, eid);
						graph.addEdge(edge);
					}
					prev = eid;
				}
			}
		}
		
		/**
		 * why some vertex are not found ???
		 */
		Set<CID> cidsSurvived = new HashSet<> ();
		Set<EID> eidsProcessed = new HashSet<> ();
		for (EID eid : eids) {
			if (!eidsProcessed.contains(eid)) {
				Set<EID> cluster = BreadthFirstSearch.run(graph, eid);
				eidsProcessed.addAll(cluster);
				if (!cluster.isEmpty()) {
					CID cid = eidToCid.get(cluster.iterator().next()).iterator().next();
					cidsSurvived.add(cid);
					for (EID eid_ : cluster) {
						res.put(eid_, cid);
					}
				}
//				System.out.println(cluster);
			}
		}
		
		if (survived != null) {
			survived.clear();
			survived.addAll(cidsSurvived);
		}
		
		if (deleted != null) {
			deleted.clear();
			deleted.addAll(clusterMap.keySet());
			deleted.removeAll(cidsSurvived);
		}
		
		return res;
	}
	
	public static<EID, CID> Map<CID, Set<EID>> resolveConflicts2(Map<CID, Set<EID>> clusterMap) {
		
		Map<EID, Set<CID>> eidToCid = invertMapKeyToSet(clusterMap);
		
		Map<CID, Set<EID>> res = new HashMap<> ();
		Set<EID> eids = new HashSet<> ();
		UndirectedGraph<EID, Integer> graph = new UndirectedGraph<>();
		Integer counter = 0;
		for (CID cid : clusterMap.keySet()) {
			EID prev = null;
			Set<EID> eids_ = clusterMap.get(cid);
			if (eids_.size() < 2) {
				for (EID eid : eids_) {
					graph.addVertex(eid);
					eids.add(eid);
				}
			} else {
				for (EID eid : eids_) {
					eids.add(eid);
					if (prev != null) {
						DefaultBinaryEdge<Integer, EID> edge = new DefaultBinaryEdge<>(counter++, prev, eid);
						graph.addEdge(edge);
					}
					prev = eid;
				}
			}
		}

		Set<EID> eidsProcessed = new HashSet<> ();
		for (EID eid : eids) {
			if (!eidsProcessed.contains(eid)) {
				Set<EID> cluster = BreadthFirstSearch.run(graph, eid);
				eidsProcessed.addAll(cluster);
				CID cid = eidToCid.get(cluster.iterator().next()).iterator().next();
				res.put(cid, cluster);
//				System.out.println(cluster);
			}
		}
		
		return res;
	}
//	
//	public static void resolveMerging(
//			//Input
//			Map<Long, Set<Long>> setA,
//			Map<Long, Set<Long>> setB,
//			//Output Collections
//			List<Set<Long>> newClusters, 
//			Map<Set<Long>, Set<Long>> toMerge, 
//			Set<Long> unaffected) {
//		
//		Map<Long, Long> elementToClusterId = new HashMap<> ();
//		for (Long cid : setB.keySet()) {
//			for (Long eid : setB.get(cid)) {
//				if (cid == null || eid == null) {
//					throw new RuntimeException("Error null id found " + cid + setB.get(cid));
//				}
//				elementToClusterId.put(eid, cid);
//			}
//		}
//		for (Long cid : setA.keySet()) {
//			for (Long eid : setA.get(cid)) {
//				if (cid == null || eid == null) {
//					throw new RuntimeException("Error null id found " + cid + setA.get(cid));
//				}
//				elementToClusterId.put(eid, cid);
//			}
//		}
//		
//		UndirectedGraph<Long, Integer> graph = new UndirectedGraph<>();
//		Integer counter = 0;
//		Set<Long> eids = new HashSet<> ();
//		
//		for (Set<Long> cluster : uniqueMembershipClusters) {
//			Long prev = null;
//			for (Long eid : cluster) {
//				eids.add(eid);
//				if (prev != null) {
//					DefaultBinaryEdge<Integer, Long> edge = new DefaultBinaryEdge<>(counter++, prev, eid);
//					graph.addEdge(edge);
//				} else if (cluster.size() < 2) {
//					graph.addVertex(eid);
//				}
//				prev = eid;
//			}
//		}
//		
//		
//		Set<Set<Long>> oldClusters = new HashSet<> ();
//		Set<Long> oldElements = new HashSet<> ();
//		for (Set<Long> cluster : setB.values()) {
//			oldClusters.add(cluster);
//			oldElements.addAll(cluster);
//			Long prev = null;
//			for (Long eid : cluster) {
//				eids.add(eid);
//				if (prev != null) {
//					DefaultBinaryEdge<Integer, Long> edge = new DefaultBinaryEdge<>(counter++, prev, eid);
//					graph.addEdge(edge);
//				} else if (cluster.size() < 2) {
//					graph.addVertex(eid);
//				}
//				prev = eid;
//			}
//		}
//		
//		Set<Long> eidsProcessed = new HashSet<> ();
//		for (Long eid : eids) {
//			if (!eidsProcessed.contains(eid)) {
//				Set<Long> cluster = BreadthFirstSearch.run(graph, eid);
//				
//				if (!cluster.isEmpty()) {
//					eidsProcessed.addAll(cluster);
//					
//					Set<Long> aux_ = new HashSet<> (cluster);
//					aux_.retainAll(oldElements);
//					
//					//Unaffected if ALL COLOR BLACK
//					//New if ALL COLOR RED
//					//Merge if Mixed Color
//					
//					//Test if the cluster have previous elements
//					if (aux_.isEmpty()) {
//						//if all elements are new then the entire cluster is new
//						newClusters.add(cluster);
//					} else if (oldClusters.contains(cluster)){
//						//if previous clusters have an identical cluster
//						unaffected.add(elementToClusterId.get(cluster.iterator().next()));
//					} else {
//						//Else there are elements from previous clusters
//						//if can be either an update
//						//or update and delete (if more than one CID is present)
//						Set<Long> cidList = new HashSet<> ();
//						for (Long eid_ : cluster) {
//							Long cid_;
//							if ((cid_ = elementToClusterId.get(eid_)) != null) {
//								cidList.add(cid_);
//							}
//						}
//						toMerge.put(cidList, cluster);
//					}
//				}
//			}
//		}
//	}
//	
	
	public static<T> void resolveMerging(
			Map<T, Set<Long>> prevClusters,
			Set<T> keep,
			Set<Set<T>> merge) {
		
		Map<Long, Set<T>> elementToClusterId = new HashMap<> ();
		Set<Long> eids = new HashSet<> ();
		UndirectedGraph<Long, T> graph = new UndirectedGraph<>();
		
		LOGGER.debug("Building graph ...");
		for (T entry : prevClusters.keySet()) {
			Set<Long> members = prevClusters.get(entry);
			eids.addAll(members);
//			oldClusters.add(cluster);
//			oldElements.addAll(members);
			Long prev = null;
			for (Long eid : members) {
				eids.add(eid);
				if (!elementToClusterId.containsKey(eid)) {
					elementToClusterId.put(eid, new HashSet<T> ());
				}
				elementToClusterId.get(eid).add(entry);
				
				if (prev != null) {
					DefaultBinaryEdge<T, Long> edge = new DefaultBinaryEdge<T, Long>(entry, prev, eid);
					graph.addEdge(edge);
				} else if (members.size() < 2) {
					graph.addVertex(eid);
				}
				prev = eid;
			}
		}
		
		//track vertices processed
		Set<Long> eidsProcessed = new HashSet<> ();
		
		LOGGER.debug("Begin traversal graph ...");
		//begin traversal
		for (Long eid : eids) {
			if (!eidsProcessed.contains(eid)) {
				Set<Long> cluster = BreadthFirstSearch.run(graph, eid);
				
				if (!cluster.isEmpty()) {
					eidsProcessed.addAll(cluster);
					
					Set<T> cids = new HashSet<> ();
					for (Long eid_ : cluster) {
						cids.addAll(elementToClusterId.get(eid_));
					}
					
					if (cids.isEmpty()) {
						throw new RuntimeException("wut cid is empty ?");
					}
					
					if (cids.size() > 1) {
						merge.add(cids);
					} else {
						keep.add(cids.iterator().next());
					}
					
//					Set<Long> aux_ = new HashSet<> (cluster);
//					aux_.retainAll(oldElements);
//					
//					//Unaffected if ALL COLOR BLACK
//					//New if ALL COLOR RED
//					//Merge if Mixed Color
//					
//					//Test if the cluster have previous elements
//					if (aux_.isEmpty()) {
//						//if all elements are new then the entire cluster is new
//						newClusters.add(cluster);
//					} else if (oldClusters.contains(cluster)){
//						//if previous clusters have an identical cluster
//						unaffected.add(elementToClusterId.get(cluster.iterator().next()));
//					} else {
//						//Else there are elements from previous clusters
//						//if can be either an update
//						//or update and delete (if more than one CID is present)
//						Set<Long> cidList = new HashSet<> ();
//						for (Long eid_ : cluster) {
//							Long cid_;
//							if ((cid_ = elementToClusterId.get(eid_)) != null) {
//								cidList.add(cid_);
//							}
//						}
//						toMerge.put(cidList, cluster);
//					}
				}
			}
		}
	}
	
	public static void resolveMerging(
			//Input
			List<Set<Long>> uniqueMembershipClusters,
			Map<Long, Set<Long>> prevClusters,
			//Output Collections
			List<Set<Long>> newClusters, 
			Map<Set<Long>, Set<Long>> toMerge, 
			Set<Long> unaffected) {
		
//		System.out.println("prev clusters " + prevClusters.size());
		
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
}
