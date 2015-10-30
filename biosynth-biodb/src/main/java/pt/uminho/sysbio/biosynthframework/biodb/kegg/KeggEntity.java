package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggTokens;

public abstract class KeggEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	protected String entry;
	protected List<String> names;
	protected String definition;
	
	protected Map<String, List<String>> properties; 
	
	
	public KeggEntity(){
		properties = new HashMap<>();
	}
	

	public void addProperty(String key, String value){
		if(key.equals("ENTRY"))
			entry = value;
		else if(key.equals(KeggTokens.NAME))
		{
			if(names==null)
				names = new ArrayList<>();
			names.add(value);
		}
		else if(key.equals(KeggTokens.DEFINITION))
		{
			if(definition==null)
				definition = value;
			else
				definition += "<br>" + value;
		}
		else
		{
			if(!properties.containsKey(key))
				properties.put(key, new ArrayList<String>());
			properties.get(key).add(value);
		}
	}
	
	public void addProperty(String key, List<String> values){
		if(key.equals("ENTRY"))
			entry = values.get(0);
		else if(key.equals(KeggTokens.NAME))
		{
			if(names==null)
				names = new ArrayList<>();
			names.addAll(values);
		}
		else if(key.equals(KeggTokens.DEFINITION))
		{
			definition = "";
			for(String s : values)
				definition += s + "\n";
		}
		else
		{
			if(!properties.containsKey(key))
				properties.put(key, new ArrayList<String>());
			properties.get(key).addAll(values);
		}
	}
	
	public Set<String> getGenesFromValue(String value){
		Pattern p = Pattern.compile(KeggTokens.KOG_GENES_REGEXP);
		Matcher m = p.matcher(value);
		Set<String> gs = null;
		if(m.find())
		{
			gs = new HashSet<>();
//			String org = m.group(1).toLowerCase();
			p = Pattern.compile(KeggTokens.GENE_WITH_NAME);
			for(String g : m.group(2).split("\\s+"))
			{
				m = p.matcher(g);
//				gs.add(m.find() ? org+":"+m.group(1) : org+":"+g);
				gs.add(m.find() ? m.group(1) : g);
			}
		}
		return gs;
	}
	
	public String getModuleFromValue(String value){
		return retrieveValueRegExp(value, KeggTokens.MODULE_WITH_NAME);
	}
	
	public String getPathwayFromValue(String value){
		return retrieveValueRegExp(value, KeggTokens.PATHWAY_WITH_NAME);
	}
	
	public String getCompoundFromValue(String value){
		return retrieveValueRegExp(value, KeggTokens.COMPOUND_WITH_NAME);
	}
	
	public String getReactionFromValue(String value){
		return retrieveValueRegExp(value, KeggTokens.REACTION_WITH_NAME);
	}
	
	public Set<String> getOrthologyFromValue(String value){
		return retrieveValuesRegExp(value, KeggTokens.ORTHOLOG_REGEXP);
	}
	
	protected String retrieveValueRegExp(String value, String exp){
		Pattern p = Pattern.compile(exp);
		Matcher m = p.matcher(value);
		return m.find() ? m.group(1) : null;
	}
	
	protected Set<String> retrieveValuesRegExp(String value, String exp){
		Pattern p = Pattern.compile(exp);
		Matcher m = p.matcher(value);
		Set<String> res = new TreeSet<>();
		while(m.find())
			res.add(m.group());
		return res.size()==0 ? null : res;
	}
	
	public Set<String> getEcNumbersFromDefinition(String value){
		Set<String> res = new HashSet<>();
		Pattern	p = Pattern.compile(KeggTokens.ECNUMBER_REGEXP);
		Matcher	m = p.matcher(value);
		while(m.find())
			res.add(m.group());
		return res.size()==0 ? null : res;
	}
	
	public List<String> removeProperty(String key){
		return properties.remove(key);
	}
	
	public boolean removePropertyValue(String key, String value){
		return properties.containsValue(key)
			? properties.get(key).remove(value)
			: false;
	}
	
	public List<String> getPropertyValues(String key){
		return properties.containsKey(key) ? properties.get(key) : null;
	}
	
	public String getPropertyFirstValue(String key){
		return properties.containsKey(key) ? properties.get(key).get(0) : null;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}


	public String getDefinition() {
		return definition;
	}


	public void setDefinition(String definition) {
		this.definition = definition;
	}


	public Map<String, List<String>> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, List<String>> properties) {
		this.properties = properties;
	}
}
