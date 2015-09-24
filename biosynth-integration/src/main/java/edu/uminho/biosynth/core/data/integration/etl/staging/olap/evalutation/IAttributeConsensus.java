package edu.uminho.biosynth.core.data.integration.etl.staging.olap.evalutation;

import java.util.List;

public interface IAttributeConsensus<T> {
	public double score(List<T> values);
	public T consensus(List<T> values);
}
