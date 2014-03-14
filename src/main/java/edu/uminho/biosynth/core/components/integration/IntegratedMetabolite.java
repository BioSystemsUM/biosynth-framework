package edu.uminho.biosynth.core.components.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;

public class IntegratedMetabolite extends GenericMetabolite {

	private final static Logger LOGGER = Logger.getLogger(IntegratedMetabolite.class.getName());
	
	private static final long serialVersionUID = 1L;
	
	private List<GenericMetabolite> referenceMetabolites = new ArrayList<> ();
	
	
	public void addMetabolite(GenericMetabolite cpd) {
		LOGGER.log(Level.INFO, "Adding metabolite " + cpd.getEntry());
		Class<?> cpdClass = cpd.getClass();
		switch (cpdClass.getName()) {
		case "edu.uminho.biosynth.core.components.kegg.KeggMetaboliteEntity":
			this.addKeggMetabolite((KeggCompoundMetaboliteEntity) cpd);
			break;
		default:
			LOGGER.log(Level.SEVERE, "Metabolite class unmapped " + cpdClass.getName());
			break;
		}
	}
	
	public void addKeggMetabolite(KeggCompoundMetaboliteEntity cpdKegg) {
		this.referenceMetabolites.add(cpdKegg);
	}

	public void addBiocycMetabolite(BioCycMetaboliteEntity cpdBio) {
		
	}
	
	public void integrate() {
		
	}
}
