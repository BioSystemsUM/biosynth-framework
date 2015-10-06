package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestKeggKOsDaoImpl
extends AbstractRestfulKeggDao {

	public static boolean DELAY_ON_IO_ERROR = false;
	
	private static final Logger logger = LoggerFactory.getLogger(RestKeggKOsDaoImpl.class);
	private static final String restRxnQuery = "http://rest.kegg.jp/get/ko:%s";

	public String getKOByEntry(String entry) {
		String restRxnQuery = String.format(RestKeggKOsDaoImpl.restRxnQuery, entry);
		
		String localPath = getPathFolder() + entry ;
//		KeggReactionEntity rxn = new KeggReactionEntity();
		
		String rnFlatFile = null;
		
		try {
			logger.info(restRxnQuery);
			logger.info(localPath);
			rnFlatFile = this.getLocalOrWeb(restRxnQuery, localPath + entry +".txt");
			
//			KeggReactionFlatFileParser parser = new KeggReactionFlatFileParser(rnFlatFile);
//			rxn.setEntry(parser.getEntry());
//			rxn.setName(parser.getName());
//			rxn.setOrientation(Orientation.Reversible);
//			rxn.setComment(parser.getComment());
//			rxn.setRemark(parser.getRemark());
//			rxn.setDefinition(parser.getDefinition());
//			rxn.setEquation(parser.getEquation());
//			rxn.setEnzymes(parser.getEnzymes());
//			rxn.setPathways(parser.getPathways());
//			rxn.setRpairs(parser.getRPairs());
//			rxn.setOrthologies(parser.getOrthologies());
//			rxn.setLeft(parser.getLeft());
//			rxn.setRight(parser.getRight());
			
		} catch (IOException e) {
			logger.error(e.getMessage());
			
//			if (DELAY_ON_IO_ERROR) {
//				try {
//					Thread.sleep(300000);
//				} catch (Exception es) {
//					System.out.println(es.getMessage());
//				}
//			}
//			LOGGER.debug(e.getStackTrace());
			return null;
		}
		return rnFlatFile;
	}

	public Set<String> getAllKOEntries() {
		Set<String> rnIds = new HashSet<>();
		String restListRnQuery = String.format("http://rest.kegg.jp/%s/%s", "list", "ko");
		String localPath = this.getLocalStorage() + "query" + "/kos.txt";
		try {
			String httpResponseString = getLocalOrWeb(restListRnQuery, localPath);
			String[] httpResponseLine = httpResponseString.split("\n");
			for ( int i = 0; i < httpResponseLine.length; i++) {
				String[] values = httpResponseLine[i].split("\\t");
				rnIds.add(values[0].substring(3));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return rnIds;
	}
	
	public String getPathFolder(){
		return this.getLocalStorage()  + "kos" + "/";
	}
	
	public void createFolder(){
		File f = new File(getPathFolder());
		f.mkdirs();
	}
	
	public static void main(String[] args) {
		RestKeggKOsDaoImpl k = new RestKeggKOsDaoImpl();
//		System.out.println(k.getAllKOEntries());
		System.out.println(k.getKOByEntry("K09053"));
	}
}
