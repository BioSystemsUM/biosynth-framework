package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggTokens;

public class KeggGeneEntity extends KeggEntity{

  private static final long serialVersionUID = 1L;

  protected String nucleotidesSeq;
  protected String aminoacidsSeq;
  protected Set<String> ecNumbers;
  protected Set<String> modules;
  protected Set<String> pathways;
  protected Set<String> orthologs;
  
  public Set<String> organism = new HashSet<> ();
  public Set<String> dblinks = new HashSet<> ();
  public Set<String> motif = new HashSet<> ();
  public Set<String> position = new HashSet<> ();
  public Set<String> structure = new HashSet<> ();


  public void addEcNumber(String ec){
    if(ecNumbers==null)
      ecNumbers = new HashSet<>();
    ecNumbers.add(ec);
  }

  public void addEcNumbers(Collection<String> ecs){
    if(ecNumbers==null)
      ecNumbers = new HashSet<>();
    ecNumbers.addAll(ecs);
  }

  public void addModule(String module){
    if(modules==null)
      modules = new HashSet<>();
    modules.add(module);
  }

  public void addPathway(String pathway){
    if(pathways==null)
      pathways = new HashSet<>();
    pathways.add(pathway);
  }

  public void addOrthologs(Set<String> ortholog){
    if(orthologs==null)
      orthologs = new HashSet<>();
    orthologs.addAll(ortholog);
  }

  public String[] getEntryAndCDSFromValue(String value){
    String[] ts = value.split("\\s+");
    return ts.length>2 ? new String[]{ts[0], ts[1], ts[2]} : new String[]{ts[0]};
  }

  public String getNucleotidesSeq() {
    return nucleotidesSeq;
  }
  public void setNucleotidesSeq(String nucleotidesSeq) {
    this.nucleotidesSeq = nucleotidesSeq;
  }
  public String getAminoacidsSeq() {
    return aminoacidsSeq;
  }
  public void setAminoacidsSeq(String aminoacidsSeq) {
    this.aminoacidsSeq = aminoacidsSeq;
  }
  public Set<String> getEcNumbers() {
    return ecNumbers;
  }
  public void setEcNumbers(Set<String> ecNumbers) {
    this.ecNumbers = ecNumbers;
  }
  public Set<String> getModules() {
    return modules;
  }
  public void setModules(Set<String> modules) {
    this.modules = modules;
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



  @SuppressWarnings("unchecked")
  @Override
  public void addProperty(String key, String value) {
    Object addedValue = null;
    if(key.equals(KeggTokens.ENTRY))
    {
      addedValue = getEntryAndCDSFromValue(value);
      String[] v = (String[]) addedValue;
      entry = v[0];
      if(v.length>2)
        super.addProperty(v[1], v[2]);
    }
    else if(key.equals(KeggTokens.DEFINITION))
    {
      addedValue = getEcNumbersFromDefinition(value);
      if(addedValue!=null)
        addEcNumbers((Set<String>) addedValue);
    }
    else if(key.equals(KeggTokens.AASEQ))
    {
      //			System.out.println(">" + value + "<");
      if(value.matches("[0-9]+")) // ignoring first row with the symbols counting
        addedValue = "";
      else
      {
        if(aminoacidsSeq==null)
          aminoacidsSeq = value;
        else
          aminoacidsSeq += value;
        addedValue = aminoacidsSeq;
      }

    }
    else if(key.equals(KeggTokens.NTSEQ))
    {
      if(value.matches("[0-9]+")) // ignoring first row with the symbols counting
        addedValue = "";
      else
      {
        if(nucleotidesSeq==null)
          nucleotidesSeq = value;
        else
          nucleotidesSeq += value;
        addedValue = nucleotidesSeq;
      }
    }
    else if(key.equals(KeggTokens.ORTHOLOGY))
    {
      addedValue = getOrthologyFromValue(value);
      if(addedValue!=null)
        addOrthologs((Set<String>) addedValue);
    }
    else if(key.equals(KeggTokens.MODULE))
    {
      addedValue = getModuleFromValue(value);
      if(addedValue!=null)
        addModule((String) addedValue);
    }
    else if(key.equals(KeggTokens.PATHWAY))
    {
      addedValue = getPathwayFromValue(value);
      if(addedValue!=null)
        addPathway((String) addedValue);
    }


    if(addedValue==null)
      super.addProperty(key, value);
//    System.out.println(key + " " + value);
    switch (key) {
      case "MOTIF": motif.add(value); break;
      case "POSITION": position.add(value); break;
      case "ORGANISM": organism.add(value); break;
      case "DBLINKS": dblinks.add(value); break;
      case "STRUCTURE": structure.add(value); break;
      default:
        break;
    }
  }

  @Override
  public void addProperty(String key, List<String> values) {

    if(key.equals(KeggTokens.AASEQ))
    {
      aminoacidsSeq = values.get(1); // ignoring first row with the symbols counting
      for(int i=2; i<values.size(); i++)
        aminoacidsSeq += values.get(i);
    }
    else if(key.equals(KeggTokens.NTSEQ))
    {
      nucleotidesSeq = values.get(1); // ignoring first row with the symbols counting
      for(int i=2; i<values.size(); i++)
        nucleotidesSeq += values.get(i);
    }
    else
      super.addProperty(key, values);
  }

}
