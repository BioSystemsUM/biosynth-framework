package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggTokens;

public class KeggECNumberEntity extends KeggEntity{
	
	protected Set<String> genes;
	protected Set<String> pathways;
	protected Set<String> orthologs;
	
	
	public void addGenes(Collection<String> gs){
		if(genes==null)
			genes = new HashSet<>();
		genes.addAll(gs);
	}
	
	public void addPathway(String pathway){
		if(pathways==null)
			pathways = new HashSet<>();
		pathways.add(pathway);
	}
	
	public void addOrtholog(String ortholog){
		if(orthologs==null)
			orthologs = new HashSet<>();
		orthologs.add(ortholog);
	}
	
	
	public Set<String> getGenes() {
		return genes;
	}
	public void setGenes(Set<String> genes) {
		this.genes = genes;
	}
	public Set<String> getPathways() {
		return pathways;
	}
	public void setPathways(Set<String> pathways) {
		this.pathways = pathways;
	}
	public Set<String> getOrthologs() {
		return orthologs;
	}
	public void setOrthologs(Set<String> orthologs) {
		this.orthologs = orthologs;
	}

	
	@Override
	public void addProperty(String key, String value) {
		Object addedValue = null;
		if(key.equals(KeggTokens.ENTRY))
		{
			Pattern p = Pattern.compile(KeggTokens.ECNUMBER_REGEXP);
			Matcher m = p.matcher(value);
			if(m.find())
			{
				entry = m.group();
				addedValue = entry;
			}
		}
		else if(key.equals(KeggTokens.GENES))
		{
			addedValue = getGenesFromValue(value);
			addGenes((Set<String>) addedValue);
		}
		else if(key.equals(KeggTokens.ORTHOLOGY))
		{
			addedValue = getOrthologyFromValue(value);
			addOrtholog((String) addedValue);
		}
		else if(key.equals(KeggTokens.PATHWAY))
		{
			addedValue = getPathwayFromValue(value);
			addPathway((String) addedValue);
		}
		
		if(addedValue==null)
			super.addProperty(key, value);
	}

}
