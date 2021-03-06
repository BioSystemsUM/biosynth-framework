package edu.uminho.biosynth.mapping.iTO977;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.hibernate.GenericEntityDaoImpl;

@SuppressWarnings("deprecation")
public class TestMapMetabolites {

//	private static SessionFactory sessionFactory;
//	
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession("");
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		sessionFactory.close();
//	}
//
//	@Before
//	public void setUp() throws Exception {
//		sessionFactory.openSession();
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		sessionFactory.getCurrentSession().close();
//	}
//	
//	@Test
//	public void testMapMetabolites() throws Exception {
//		IGenericDao dao = new GenericEntityDaoImpl(sessionFactory);
//		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
//		
//		String rawFileString = BioSynthUtilsIO.readFromFile("./src/test/resources/MISC/iTO977_cpd.tsv");
//		Map<String, GenericMetabolite> metabolites = new HashMap<> (); 
//		String[] lines = rawFileString.split("\n");
//		for (int i = 1; i < lines.length; i++) {
//			GenericMetabolite cpd = new GenericMetabolite();
//			String[] values = lines[i].split("\t");
//			cpd.setEntry(values[2]);
//			cpd.setFormula(values[5]);
//			cpd.setDescription(values[4]);
//			if ( !metabolites.keySet().contains(values[2])) {
//				metabolites.put(values[2], cpd);
//			}
//		}
//		
//		for (String key : metabolites.keySet()) {
//			GenericMetabolite cpd = metabolites.get(key);
//			String[] miriam = cpd.getDescription().split(":");
//			StringBuilder sb = new StringBuilder();
//			if (miriam.length > 1) {
//				String value = miriam[miriam.length - 1];
//				String ref = miriam[miriam.length - 2];
//				List<MnxMetaboliteCrossreferenceEntity> res = dao.criteria(MnxMetaboliteCrossreferenceEntity.class, 
//						Restrictions.and( Restrictions.eq("value", value), Restrictions.eq("ref", ref)));
//				if (res.size() > 1) {
//					System.err.println("ERROR > 1: " + ref + ":" + value);
//				} else if ( res.size() < 1 ) {
//					System.err.println("NOT FOUND: " + ref + ":" + value);
//				} else {
//					MnxMetaboliteCrossreferenceEntity xref = res.get(0);
//					for (MnxMetaboliteCrossreferenceEntity xr : xref.getMnxMetaboliteEntity().getCrossreferences()) {
//						if (xr.getRef().equals("metacyc")) {
//							sb.append(xr).append('\t');
//						}
//					}
//				}
//			}
//			
//			System.out.println(key + "\t" + metabolites.get(key).getDescription() + "\t" + sb.toString());
//			
//		}
//		
//		tx.commit();
//		fail("Not yet implemented");
//	}

}
