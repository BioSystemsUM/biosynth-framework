package pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph;

import java.util.Set;

public interface HyperEdge<V, E> {
	public Set<V> getA();
	public Set<V> getB();
}
