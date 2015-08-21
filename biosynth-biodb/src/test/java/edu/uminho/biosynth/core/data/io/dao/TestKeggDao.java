package edu.uminho.biosynth.core.data.io.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggReactionEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.hibernate.GenericEntityDaoImpl;

@SuppressWarnings("deprecation")
public class TestKeggDao {

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
//	public void testSaveMetabolite() {
//		IGenericDao dao = new GenericEntityDaoImpl(sessionFactory);
//		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
//		KeggCompoundMetaboliteEntity cpd = new KeggCompoundMetaboliteEntity();
//		cpd.setEntry("T00755");
//		cpd.setName("4-Hydroxy-3-methoxy-benzaldehyde;Vanillin;Vanillaldehyde;4-Hydroxy-3-methoxybenzaldehyde");
//		cpd.setFormula("C8H8O3");
//		cpd.setMass(152.0473);
//		cpd.setMolWeight(152.1473);
//		cpd.setRemark("Same as: D00091");
//		cpd.setDescription("Manual");
//		cpd.setSource("KEGG");
//		cpd.setMetaboliteClass("COMPOUND");
//		String[] enzymes = {"1.1.3.38", "1.2.1.67", "1.2.3.9", "1.13.11.43", "4.1.2.41"};
//		cpd.setEnzymes(new ArrayList<String> (Arrays.asList(enzymes)));
//		dao.save(cpd);
//		tx.commit();
//		
//		
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testLoadMetabolite() {
//		IGenericDao dao = new GenericEntityDaoImpl(sessionFactory);
//		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
//		KeggCompoundMetaboliteEntity cpd1 = dao.find(KeggCompoundMetaboliteEntity.class, 1);
//		KeggCompoundMetaboliteEntity cpd2 = dao.criteria(KeggCompoundMetaboliteEntity.class, Restrictions.eq("entry", "T00755")).get(0);
//		System.out.println(cpd1.getFormula());
//		System.out.println(cpd2.getName());
//		tx.commit();
//		
//		
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testSaveReaction() {
//		IGenericDao dao = new GenericEntityDaoImpl(sessionFactory);
//		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
//		KeggReactionEntity rxn = new KeggReactionEntity();
//		rxn.setEntry("R00010");
//		rxn.setName("alpha,alpha-trehalose glucohydrolase");
//		rxn.setDefinition("alpha,alpha-Trehalose + H2O <=> 2 D-Glucose");
//		rxn.setEquation("C01083 + C00001 <=> 2 C00031");
//		rxn.setRemark("Same as: R06103");
//		rxn.setComment("No comment");
//		rxn.setSource("KEGG");
//		rxn.setEnzymes(Arrays.asList(new String[] {"3.2.1.28"}));
//		rxn.setPathways(Arrays.asList(new String[] {"rn00500", "rn01100"}));
//		rxn.setRpairs(Arrays.asList(new String[] {"RP00453", "RP05679"}));
//		rxn.setOrthologies(Arrays.asList(new String[] {"K01194"}));
//		dao.save(rxn);
//		tx.commit();
//		
//		
//		fail("Not yet implemented");
//	}

}
