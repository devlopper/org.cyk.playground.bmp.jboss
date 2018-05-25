package org.cyk.playground.bpm.jboss;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.Assert;
import org.junit.Test;

public class JpaEclipseLinkUnitTest {

	@Test
	public void createEntityManagerFactory(){
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistenceUnitEclipseLink");
		Assert.assertNotNull(entityManagerFactory);
	}
	
	@Test
	public void createEntityManager(){
		Map<String, String> properties = new HashMap<String, String>();
	    properties.put("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:hsql");
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistenceUnitEclipseLink",properties);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Assert.assertNotNull(entityManager);
	}
	
	@Test
	public void createQuerySelectProcessInstanceInfo(){
		Map<String, String> properties = new HashMap<String, String>();
	    properties.put("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:hsql");
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistenceUnitEclipseLink",properties);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query query = entityManager.createQuery("SELECT p FROM ProcessInstanceInfo p");
		Assert.assertNotNull(query);
	}
}
