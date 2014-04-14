package edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Repository;

import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycReactionEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionCrossReferenceEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionEcNumberEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionLeftEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionRightEntity;
import edu.uminho.biosynth.core.data.io.dao.ReactionDao;
import edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc.parser.BioCycReactionXMLParser;

@Repository
public class RestBiocycReactionDaoImpl extends AbstractRestfullBiocycDao 
		implements ReactionDao<BioCycReactionEntity> {

	private static Logger LOGGER = Logger.getLogger(RestBiocycReactionDaoImpl.class);
	
	@Override
	public BioCycReactionEntity getReactionById(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BioCycReactionEntity getReactionByEntry(String entry) {
		String restRxnQuery = String.format(RestBiocycReactionDaoImpl.xmlGet, pgdb, entry);
		BioCycReactionEntity rxn = null;
		
		
		LOGGER.debug(String.format("Query: %s", restRxnQuery));
		try {
			String localPath = String.format("%sreaction/%s/%s.xml", this.getLocalStorage(), pgdb, entry);
			
			String xmlDoc = null;
			
			LOGGER.debug(String.format("Local Path: %s", localPath));
			xmlDoc = this.getLocalOrWeb(restRxnQuery, localPath);
			BioCycReactionXMLParser parser = new BioCycReactionXMLParser(xmlDoc);
			
			rxn = new BioCycReactionEntity();
			
//			String source = parser.getSource();
			String entry_ = parser.getEntry();
			List<BioCycReactionEcNumberEntity> ecNumberEntities = parser.getEcNumbers();
			List<BioCycReactionLeftEntity> leftEntities = parser.getLeft();
			List<BioCycReactionRightEntity> rightEntities = parser.getRight();
			List<BioCycReactionCrossReferenceEntity> crossReferences = parser.getCrossReferences();
			List<String> parentStrings = parser.getParents();
			List<String> pathwayStrings = parser.getPathways();
			List<String> enzymaticReactionStrings = parser.getEnzymaticReactions();
			Boolean orphan = parser.isOrphan();
			Boolean physioRel = parser.isPhysiologicallyRelevant();

			rxn.setEcNumbers(ecNumberEntities);
			rxn.setPhysiologicallyRelevant(physioRel);
			rxn.setParents(parentStrings);
			rxn.setPathways(pathwayStrings);
			rxn.setEnzymaticReactions(enzymaticReactionStrings);
			rxn.setOrphan(orphan);
			rxn.setEntry(entry_);
			rxn.setLeft(leftEntities);
			rxn.setRight(rightEntities);
			rxn.setCrossReferences(crossReferences);

			
		} catch (IOException e) {
			LOGGER.error(String.format("IO ERROR - %s", e.getMessage()));
		}
		
		
		return rxn;
	}

	@Override
	public BioCycReactionEntity saveReaction(BioCycReactionEntity reaction) {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public Set<Serializable> getAllReactionIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllReactionEntries() {
		List<String> rxnEntryList = new ArrayList<>();
		
		try {
			String params = String.format("[x:x<-%s^^%s]", pgdb, "reactions");
			String restXmlQuery = String.format(xmlquery, URLEncoder.encode(params, "UTF-8"));
			String localPath = this.getLocalStorage() + "query" + "/reaction.xml";
			
			String httpResponseString = getLocalOrWeb(restXmlQuery, localPath);
			JSONObject jsDoc = XML.toJSONObject(httpResponseString);
			JSONArray compoundJsArray = jsDoc.getJSONObject("ptools-xml").getJSONArray("Reaction");
			for (int i = 0; i < compoundJsArray.length(); i++) {
				String entry = compoundJsArray.getJSONObject(i).getString("frameid");
//				if ( this.entryPrefix.length() > 0) {
//					entry = entryPrefix + ":" + entry;
//				}
				rxnEntryList.add( entry);
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(String.format("UnsupportedEncodingException [%s]", e.getMessage()));
		} catch (JSONException e) {
			LOGGER.error(String.format("JSONException [%s]", e.getMessage()));
		} catch (IOException e) {
			LOGGER.error(String.format("IOException [%s]", e.getMessage()));
		}
		
		return new HashSet<String> (rxnEntryList);
	}

}
