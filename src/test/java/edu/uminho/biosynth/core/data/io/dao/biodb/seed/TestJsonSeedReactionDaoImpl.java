package edu.uminho.biosynth.core.data.io.dao.biodb.seed;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.seed.JsonSeedReactionDaoImpl;

public class TestJsonSeedReactionDaoImpl {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void test() {
		JsonSeedReactionDaoImpl a = new JsonSeedReactionDaoImpl(new FileSystemResource("D:/home/data/seed/seed.json"));
		a.omg();
//		a.comp();
		a.defaultNameSpace();
		a.publics();
		a.uuid();
		assertEquals("", "");
	}

}
