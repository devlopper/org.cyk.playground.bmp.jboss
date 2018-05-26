package org.cyk.playground.bpm.jboss;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.drools.core.impl.EnvironmentFactory;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class JpaEclipseLinkUnitTest extends JbpmJUnitBaseTestCase {

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
	
	@Test
	public void setUpDataSource(){
		// create the entity manager factory and register it in the environment
		Map<String, String> properties = new HashMap<String, String>();
	    properties.put("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:hsql");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "persistenceUnitEclipseLink",properties);
		Environment env = EnvironmentFactory.newEnvironment();
		env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, emf );

		// create runtime manager with single process - hello.bpmn
        createRuntimeManager("org/cyk/playground/bpm/jboss/demo.bpmn");
        // take RuntimeManager to work with process engine
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        // get access to KieSession instance
        KieSession knowledgeSession = runtimeEngine.getKieSession();
        
		// create a new knowledge session that uses JPA to store the runtime state
		StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( knowledgeSession.getKieBase(), null, env );
	}
}
