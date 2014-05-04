package edu.uminho.biosynth.core.components;

import java.util.Map;

public class UnreversibleReaction {
	private String entry;
	private Map<String, StoichiometryPair> substrates;
	private Map<String, StoichiometryPair> reactants;
	
	public String getEntry() {
		return entry;
	}
	public void setEntry(String entry) {
		this.entry = entry;
	}
	public Map<String, StoichiometryPair> getSubstrates() {
		return substrates;
	}
	public void setSubstrates(Map<String, StoichiometryPair> substrates) {
		this.substrates = substrates;
	}
	public Map<String, StoichiometryPair> getReactants() {
		return reactants;
	}
	public void setReactants(Map<String, StoichiometryPair> reactants) {
		this.reactants = reactants;
	}
}
