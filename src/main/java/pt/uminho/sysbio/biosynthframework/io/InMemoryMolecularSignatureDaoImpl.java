package pt.uminho.sysbio.biosynthframework.io;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.chemanalysis.MolecularSignature;
import pt.uminho.sysbio.biosynthframework.chemanalysis.Signature;

public class InMemoryMolecularSignatureDaoImpl implements MolecularSignatureDao {

	private Map<Long, Signature> signatureMap = new HashMap<> ();
	private Map<Long, MolecularSignature> msigMap = new HashMap<> ();
	private Map<String, Long> entryToId = new HashMap<> ();
	private Map<Long, String> idToEntry = new HashMap<> ();
	private Map<Signature, Set<Long>> signatureToMsig = new HashMap<> ();
	private Map<String, Set<Long>> hash64ToMsigId = new HashMap<> ();
	private Map<Long, String> msigIdToHash64 = new HashMap<> ();
	
	public Map<Long, Signature> getSignatureMap() { return signatureMap;}
	public void setSignatureMap(Map<Long, Signature> signatureMap) { this.signatureMap = signatureMap;}

	public Map<Long, MolecularSignature> getMsigMap() { return msigMap;}
	public void setMsigMap(Map<Long, MolecularSignature> msigMap) { this.msigMap = msigMap;}

	public Map<String, Long> getEntryToId() { return entryToId;}
	public void setEntryToId(Map<String, Long> entryToId) { this.entryToId = entryToId;}

	public Map<Long, String> getIdToEntry() { return idToEntry;}
	public void setIdToEntry(Map<Long, String> idToEntry) { this.idToEntry = idToEntry;}

	public Map<Signature, Set<Long>> getSignatureToMsig() { return signatureToMsig;}
	public void setSignatureToMsig(Map<Signature, Set<Long>> signatureToMsig) { this.signatureToMsig = signatureToMsig;}

	public Map<String, Set<Long>> getHash64ToMsigId() { return hash64ToMsigId;}
	public void setHash64ToMsigId(Map<String, Set<Long>> hash64ToMsigId) { this.hash64ToMsigId = hash64ToMsigId;}

	public Map<Long, String> getMsigIdToHash64() { return msigIdToHash64;}
	public void setMsigIdToHash64(Map<Long, String> msigIdToHash64) { this.msigIdToHash64 = msigIdToHash64;}

	@Override
	public MolecularSignature getMolecularSignatureById(long msigId) {
		return this.msigMap.get(msigId);
	}

	@Override
	public MolecularSignature getMolecularSignature(long cpdId, int h,
			boolean stereo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteMolecularSignature(long cpdId, int h, boolean stereo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveMolecularSignature(long cpdId,
			MolecularSignature signatureSet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Long> getMoleculeReferencesBySignatureSet(
			MolecularSignature signatureSet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getMoleculeReferencesBySignatureSetId(long signatureSetId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> findMolecularSignatureContainsAny(Set<Signature> signatures) {
		Set<Long> result = new HashSet<> ();
		for (Signature s : signatures) {
			//get signature sets that contain this signature node
			Set<Long> msigIds = this.signatureToMsig.get(s);
			result.addAll(msigIds);
		}
		
		return result;
	}

	@Override
	public Set<Long> findMolecularSignatureContainedIn(
			Map<Signature, Double> signatureMap) {
		Set<Signature> sigs = new HashSet<> (signatureMap.keySet());
		Set<Long> result = new HashSet<> ();
		Set<Long> a = findMolecularSignatureContainsAny(signatureMap.keySet());
		
		for (long sigSetId : a) {
			MolecularSignature msig = this.msigMap.get(sigSetId);
			Set<Signature> sigs_ = new HashSet<> (msig.getSignatureMap().keySet());
			
			if (sigs.containsAll(sigs_)) {
				//check values !
				result.add(sigSetId);
			}
		}
		
		return result;
	}
	
	@Override
	public String getMolecularSignatureHashById(long msigId) {
		return this.msigIdToHash64.get(msigId);
	}
	
	@Override
	public Set<Long> listMolecularSignatureIdBySignature(Signature signature) {
		return new HashSet<> (this.signatureToMsig.get(signature));
	}
	
	@Override
	public MolecularSignature getMolecularSignatureByHash(String msigHash) {
		Set<Long> msigIdSet = this.hash64ToMsigId.get(msigHash);
		if (msigIdSet == null || msigIdSet.isEmpty()) return null;
		long msigId = msigIdSet.iterator().next();
		return getMolecularSignatureById(msigId);
	}

}
