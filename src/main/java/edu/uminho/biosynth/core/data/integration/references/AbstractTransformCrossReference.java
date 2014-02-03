package edu.uminho.biosynth.core.data.integration.references;

import java.util.HashMap;
import java.util.Map;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.data.integration.references.IReferenceTransformer;

public abstract class AbstractTransformCrossReference<T extends GenericCrossReference> implements IReferenceTransformer<T> {
	
	protected final Map<String, String> refTransformMap = new HashMap<> ();
	protected final Map<String, Map<String, String>> valueTransformMap = new HashMap<> ();
	
	public void setRefTransformMap (Map<String, String> refTransformMap) {
		this.refTransformMap.clear();
		this.refTransformMap.putAll(refTransformMap);
	}
	public void setValueTransformMap (Map<String, Map<String, String>> valueTransformMap) {
		this.valueTransformMap.clear();
		this.valueTransformMap.putAll(valueTransformMap);
	}
	
	@Override
	abstract public Class<T> getTransformerEntityClass();
	
	@Override
	abstract public GenericCrossReference transform(T crossReference);
	
}
