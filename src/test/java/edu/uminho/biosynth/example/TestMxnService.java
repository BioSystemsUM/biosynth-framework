package edu.uminho.biosynth.example;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.biodb.mnx.components.MnxReactionCrossReferenceEntity;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.data.service.MnxService;

public class TestMxnService {
	
	private static SessionFactory sessionFactory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Configuration config = new Configuration();
		config.configure();
		ServiceRegistry servReg = 
				new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
		sessionFactory = config.buildSessionFactory(servReg);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.close();
	}

	@Before
	public void setUp() throws Exception {
		sessionFactory.openSession();
	}

	@After
	public void tearDown() throws Exception {
		sessionFactory.getCurrentSession().close();
	}

	@Test
	public void test() {
		final String[] keggId = {"R08101", "R08105", "R08108", "R08109", "R08110"};
		
		IGenericDao dao = new GenericEntityDaoImpl(sessionFactory);
		MnxService service = new MnxService(dao);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		List<MnxReactionCrossReferenceEntity> res = service.getReactionCrossreferences("kegg", "R00066");
		for (MnxReactionCrossReferenceEntity xrefKegg : res) {
			for (MnxReactionCrossReferenceEntity rxnXref : xrefKegg.getMnxReactionEntity().getCrossReferences()) {
				if (rxnXref.getRef().equals("metacyc")) {
					System.out.println(rxnXref);
				}
			}
		}
		tx.commit();
		
	}

}
