package pt.uminho.sysbio.biosynthframework.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.Operator;
import pt.uminho.sysbio.biosynthframework.MultiNodeTree;

public class SbmlUtils {
  
  private static final Logger logger = LoggerFactory.getLogger(SbmlUtils.class);
  
  public static Map<String, String> sbmlNotesFieldMapping = new HashMap<> ();
  
  public static String extractString(String string) {
    String[] split = string.replace("</p>", "").split(":");
    if (split.length > 1) {
      String formula = split[1].trim();
      if (!formula.isEmpty()) {
        return formula;
      }
    }
    
    return null;
  }
  
  public static List<String> gprTreeToString(MultiNodeTree<Object> tree, Function<Object, String> f) {
    if (tree == null) {
      return null;
    }
    if (tree.body instanceof Operator) {
      List<String> terms = new ArrayList<> ();
      String opStr = ((Operator) tree.body).toString().toLowerCase();
      for (MultiNodeTree<Object> c : tree.getChilds()) {
        terms.addAll(gprTreeToString(c, f));
//        sb.append(gprTreeToString(c, o) + " " + op);
      }
      String expr = StringUtils.join(terms, " " + opStr + " ");
      if (terms.size() > 1) {
        expr = String.format("(%s)", expr);
      }
      
      List<String> w = new ArrayList<> ();
      w.add(expr);
      return w;
    } else {
      List<String> terms = new ArrayList<> ();
      terms.add(f.apply(tree.body));
      return terms;
    }
  }
  
  public static Map<String, String> parseNotes(List<String> notes) {
    Map<String, String> data = new HashMap<> ();

    sbmlNotesFieldMapping.put("FORMULA:", "formula");
    sbmlNotesFieldMapping.put("CHARGE:", "charge");
    sbmlNotesFieldMapping.put("EQUATION:", "equation");
    sbmlNotesFieldMapping.put("GPR_ASSOCIATION:", "gene_association");
    sbmlNotesFieldMapping.put("GENE_ASSOCIATION:", "gene_association");
    sbmlNotesFieldMapping.put("GENE ASSOCIATION:", "gene_association");
    sbmlNotesFieldMapping.put("GENE_LIST:", "gene_list");
    sbmlNotesFieldMapping.put("PROTEIN_ASSOCIATION:", "protein_association");
    sbmlNotesFieldMapping.put("SUBSYSTEM:", "subsystem");
    sbmlNotesFieldMapping.put("PROTEIN_CLASS:", "protein_class");
    sbmlNotesFieldMapping.put("CONFIDENCE LEVEL:", "confidence_level");
    sbmlNotesFieldMapping.put("EC NUMBER:", "ec_number");
    sbmlNotesFieldMapping.put("EC_NUMBER:", "ec_number");
    sbmlNotesFieldMapping.put("EC:", "ec_number");
    sbmlNotesFieldMapping.put("AUTHORS:", "authors");
    sbmlNotesFieldMapping.put("COMPARTMENT:", "compartment");
    sbmlNotesFieldMapping.put("KEGG ID:", "kegg");
    sbmlNotesFieldMapping.put("KEGG_RID:", "kegg");
    sbmlNotesFieldMapping.put("PUBCHEM ID:", "pubchem");
    sbmlNotesFieldMapping.put("CHEBI ID:", "chebi");
    
    for (String line : notes) {
      if (line.replaceAll(" ", "").equals("<p></p>")) {
        logger.debug("skipped empty paragraph");
      } else {
        boolean detected = false;
        for (String field : sbmlNotesFieldMapping.keySet()) {
          if (line.toUpperCase().contains(field)) {
            detected = true;
            String str = extractString(line);
            if (str != null) {
              if (data.put(sbmlNotesFieldMapping.get(field), str) != null) {
                logger.warn("duplicate field {} -> {}", sbmlNotesFieldMapping.get(field), field);
              }
            }
          }
        }
        
        if (!detected) {
          logger.warn("Unknown field: {}", line);
        }
      }

    }
    
    return data;
  }
}