package edu.uminho.biosynth.core.data.io.dao;

import java.io.File;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HelperHbmConfigInitializer {

	private static final Logger LOGGER = Logger.getLogger(HelperHbmConfigInitializer.class);
	
	public static SessionFactory initializeHibernateSession(String cfg) {
		Configuration config = new Configuration().configure(cfg);
//		Configuration config = new Configuration().configure("hibernate_debug_mysql.cfg.xml");
		LOGGER.info(config.getProperty("hibernate.dialect"));
		
		ServiceRegistry servReg = 
				new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
		SessionFactory sessionFactory = config.buildSessionFactory(servReg);
		
		return sessionFactory;
	}
	
	public static SessionFactory initializeHibernateSession(File cfg) {
		Configuration config = new Configuration().configure(cfg);
		LOGGER.info(config.getProperty("hibernate.dialect"));
		
		ServiceRegistry servReg = 
				new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
		SessionFactory sessionFactory = config.buildSessionFactory(servReg);
		
		return sessionFactory;
	}
	
	public static SessionFactory initializeHibernateSession(File cfg, Class<?>...classes) {
		Configuration config = new Configuration().configure(cfg);
		for (Class<?> c : classes) {
			config.addAnnotatedClass(c);
		}
		LOGGER.info(config.getProperty("hibernate.dialect"));
		
		ServiceRegistry servReg = 
				new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
		SessionFactory sessionFactory = config.buildSessionFactory(servReg);
		
		return sessionFactory;
	}
	
	@Deprecated
	public static SessionFactory initializeMySQLHibernateSession(
			File cfg, Class<?>...classes) {
		
		Configuration config = new Configuration().configure(cfg);
		for (Class<?> c : classes) {
			config.setProperty("", "");
			config.setProperty("", "");
			config.setProperty("", "");
			config.addAnnotatedClass(c);
		}
		LOGGER.info(config.getProperty("hibernate.dialect"));
		
		ServiceRegistry servReg = 
				new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
		SessionFactory sessionFactory = config.buildSessionFactory(servReg);
		
		return sessionFactory;
	}
}
