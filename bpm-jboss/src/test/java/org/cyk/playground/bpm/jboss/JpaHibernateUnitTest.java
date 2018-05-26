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

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class JpaHibernateUnitTest extends JbpmJUnitBaseTestCase {

	private String persistenceUnitName = "persistenceUnitHibernate";
	
	@Test
	public void createEntityManagerFactory(){
		Map<String, String> properties = new HashMap<String, String>();
	    properties.put("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:hsql");
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistenceUnitHibernate",properties);
		Assert.assertNotNull(entityManagerFactory);
	}
	
	@Test
	public void createDataSource(){
		PoolingDataSource dataSource = new PoolingDataSource();
		dataSource.setUniqueName("jdbc/jbpm-ds");
		dataSource.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
		dataSource.setMaxPoolSize(3);
		dataSource.setAllowLocalTransactions(true);
		dataSource.getDriverProperties().put("user", "sa");
		dataSource.getDriverProperties().put("password", "sasa");
		dataSource.getDriverProperties().put("url", "jdbc:h2:mem:jbpm-db");
		dataSource.getDriverProperties().put("driverClassName", "org.h2.Driver");
		dataSource.init();
	}
	
	@Test
	public void createEntityManager(){
		Map<String, String> properties = new HashMap<String, String>();
	    properties.put("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:hsql");
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistenceUnitHibernate",properties);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Assert.assertNotNull(entityManager);
	}
	
	@Test
	public void createQuerySelectProcessInstanceInfo(){
		Map<String, String> properties = new HashMap<String, String>();
	    properties.put("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:hsql");
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistenceUnitHibernate",properties);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query query = entityManager.createQuery("SELECT p FROM ProcessInstanceInfo p");
		Assert.assertNotNull(query);
	}
	
	@Test
	public void setUpDataSource(){
		// create the entity manager factory and register it in the environment
		Map<String, String> properties = new HashMap<String, String>();
	    properties.put("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:hsql");
	    properties.put("hibernate.hbm2ddl.auto", "update");
	    properties.put("hibernate.show_sql", "true");
	    properties.put("hibernate.transaction.manager_lookup_class", "org.hibernate.transaction.BTMTransactionManagerLookup" );
		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "persistenceUnitHibernate",properties);
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
	
        ksession.startProcess( "com.sample.hello" );
        ksession.dispose();
	}
}
