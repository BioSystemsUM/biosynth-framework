package edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycReactionEntity;

public class TestRestBiocycReactionDaoImpl {

	private final static double EPSILON = 0.0000001;
	private static RestBiocycReactionDaoImpl reactionDao;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		reactionDao = new RestBiocycReactionDaoImpl();
		reactionDao.setLocalStorage("D:/home/data/biocyc/");
		reactionDao.setSaveLocalStorage(true);
		reactionDao.setUseLocalStorage(true);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPORPHOBILSYNTH_RXN() {
		BioCycReactionEntity rxn = reactionDao.getReactionByEntry("PORPHOBILSYNTH-RXN");
		
		assertEquals("PORPHOBILSYNTH-RXN", rxn.getEntry());
		assertEquals("LEFT-TO-RIGHT", rxn.getReactionDirection());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(2, rxn.getParents().size());
		assertEquals(2, rxn.getPathways().size());
		assertEquals(1, rxn.getLeft().size());
		assertEquals(3, rxn.getRight().size());
		assertEquals(4, rxn.getEnzymaticReactions().size());
		assertEquals(23, rxn.getCrossReferences().size());
		assertEquals(-33.084015, rxn.getGibbs(), EPSILON);
	}

	@Test
	public void testFLAVONE_APIOSYLTRANSFERASE_RXN() {
		BioCycReactionEntity rxn = reactionDao.getReactionByEntry("FLAVONE-APIOSYLTRANSFERASE-RXN");
		
		assertEquals("FLAVONE-APIOSYLTRANSFERASE-RXN", rxn.getEntry());
		assertEquals("LEFT-TO-RIGHT", rxn.getReactionDirection());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(2, rxn.getParents().size());
		assertEquals(1, rxn.getPathways().size());
		assertEquals(2, rxn.getLeft().size());
		assertEquals(3, rxn.getRight().size());
		assertEquals(1, rxn.getEnzymaticReactions().size());
		assertEquals(1, rxn.getCrossReferences().size());
		assertEquals(8.77002, rxn.getGibbs(), EPSILON);
	}
	
	@Test
	public void testDEOXYADENPHOSPHOR_RXN() {
		BioCycReactionEntity rxn = reactionDao.getReactionByEntry("DEOXYADENPHOSPHOR-RXN");
		
		assertEquals("DEOXYADENPHOSPHOR-RXN", rxn.getEntry());
		assertEquals("REVERSIBLE", rxn.getReactionDirection());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(2, rxn.getParents().size());
		assertEquals(1, rxn.getEcNumbers().size());
		assertEquals(1, rxn.getPathways().size());
		assertEquals(2, rxn.getLeft().size());
		assertEquals(2, rxn.getRight().size());
		assertEquals(1, rxn.getEnzymaticReactions().size());
		assertEquals(2, rxn.getCrossReferences().size());
		assertEquals(13.640015, rxn.getGibbs(), EPSILON);
	}
	
	@Test
	public void test3_1_11_4_RXN() {
		BioCycReactionEntity rxn = reactionDao.getReactionByEntry("3.1.11.4-RXN");
		
		System.out.println(rxn);
		
		assertEquals("3.1.11.4-RXN", rxn.getEntry());
		assertEquals(null, rxn.getReactionDirection());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(0, rxn.getEnzymaticReactions().size());
		assertEquals(1, rxn.getLeft().size());
		assertEquals(2, rxn.getRight().size());
		assertEquals(2, rxn.getParents().size());
		assertEquals(1, rxn.getEcNumbers().size());
		assertEquals(0, rxn.getEnzymaticReactions().size());
		assertEquals(0, rxn.getCrossReferences().size());
		assertEquals(null, rxn.getGibbs());
	}
	
	@Test
	public void test1_2_1_66_RXN() {
		BioCycReactionEntity rxn = reactionDao.getReactionByEntry("1.2.1.66-RXN");
		
		System.out.println(rxn);
		
		assertEquals("1.2.1.66-RXN", rxn.getEntry());
		assertEquals("LEFT-TO-RIGHT", rxn.getReactionDirection());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(false, rxn.getOrphan());
		assertEquals(2, rxn.getParents().size());
		assertEquals(1, rxn.getEcNumbers().size());
		assertEquals(1, rxn.getPathways().size());
		assertEquals(2, rxn.getLeft().size());
		assertEquals(3, rxn.getRight().size());
		assertEquals(1, rxn.getEnzymaticReactions().size());
		assertEquals(2, rxn.getCrossReferences().size());
		assertEquals(-6.080078, rxn.getGibbs(), EPSILON);
	}
	
	
	@Test
	public void testRXN0_3283() {
		BioCycReactionEntity rxn = reactionDao.getReactionByEntry("RXN0-3283");
		
		System.out.println(rxn);
		
		assertEquals("RXN0-3283", rxn.getEntry());
		assertEquals(null, rxn.getReactionDirection());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(null, rxn.getOrphan());
		assertEquals(2, rxn.getParents().size());
		assertEquals(0, rxn.getEcNumbers().size());
		assertEquals(0, rxn.getPathways().size());
		assertEquals(2, rxn.getLeft().size());
		assertEquals(1, rxn.getRight().size());
		assertEquals(0, rxn.getEnzymaticReactions().size());
		assertEquals(0, rxn.getCrossReferences().size());
		assertEquals(null, rxn.getGibbs());
	}
//	@Test
//	public void testGetAllReactions() {
//		for (String rxnEntry : reactionDao.getAllReactionEntries()) {
//			System.out.print(rxnEntry);
//			reactionDao.getReactionByEntry(rxnEntry);
//			System.out.println(" OK !");
//		}
//
//		assertEquals(true, true);
//	}
}
