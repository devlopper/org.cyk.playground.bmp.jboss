package org.cyk.playground.bpm.jboss;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.drools.core.impl.EnvironmentFactory;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public abstract class AbstractJpaTestCase extends JbpmJUnitBaseTestCase {

	/* JPA Method Test*/
	
	@Test
	public void createEntityManagerFactory(){
		Assert.assertNotNull(getEntityManagerFactory(getPersistenceUnitNameResourceLocal()));
	}
	
	@Test
	public void createEntityManager(){
		Assert.assertNotNull(getEntityManager(getPersistenceUnitNameResourceLocal()));
	}
	
	@Test
	public void createQuerySelectProcessInstanceInfo(){
		Assert.assertNotNull(getQuery("SELECT p FROM ProcessInstanceInfo p",getPersistenceUnitNameResourceLocal()));
	}
	
	/* JBPM couple to JPA Method Test */
	
	@Test
	public void createJbpmSessionWithJpaEnabled(){
		Assert.assertNotNull(getStatefulKnowledgeSession("org/cyk/playground/bpm/jboss/demo.bpmn"));
	}
	
	@Test
	public void startProcessWithJpaEnabled(){
		StatefulKnowledgeSession session = getStatefulKnowledgeSession("org/cyk/playground/bpm/jboss/demo.bpmn");
        session.startProcess("com.sample.hello");
        session.dispose();
	}
	
	@Test
	public void executeHumanProcess01() {
		RuntimeManager runtimeManager = createRuntimeManager("org/cyk/playground/bpm/jboss/withhuman/process01.bpmn");
		RuntimeEngine runtimeEngine = getRuntimeEngine();
		KieSession session = runtimeEngine.getKieSession();
		TaskService taskService = runtimeEngine.getTaskService();
		
		ProcessInstance processInstance = session.startProcess("org.cyk.playground.bpm.jboss.process01");

		assertProcessInstanceActive(processInstance.getId(), session);
		assertNodeTriggered(processInstance.getId(), "Task 1");
		
		// let john execute Task 1
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		TaskSummary task = list.get(0);
		System.out.println("John is executing task " + task.getName());
		taskService.start(task.getId(), "john");
		taskService.complete(task.getId(), "john", null);

		assertNodeTriggered(processInstance.getId(), "Task 2");
		
		// let mary execute Task 2
		list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		task = list.get(0);
		System.out.println("Mary is executing task " + task.getName());
		taskService.start(task.getId(), "mary");
		taskService.complete(task.getId(), "mary", null);

		assertProcessInstanceCompleted(processInstance.getId());
		
		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();
	}
	
	@Test
	public void executeHumanProcessValidateSale() {
		Properties properties = new Properties();
		properties.put("StructureAnalyser", "");
		properties.put("ContentAnalyser", "");
		properties.put("Processor", "");
		properties.put("Validator", "");
		userGroupCallback = new JBossUserGroupCallbackImpl(properties);
		
		RuntimeManager runtimeManager = createRuntimeManager("org/cyk/playground/bpm/jboss/withhuman/Validate Sale.bpmn2");
		RuntimeEngine runtimeEngine = getRuntimeEngine();
		KieSession session = runtimeEngine.getKieSession();
		
		TaskService taskService = runtimeEngine.getTaskService();
		
		ProcessInstance processInstance = session.startProcess("org.cyk.playground.bpm.jboss.ValidateSale");

		assertProcessInstanceActive(processInstance.getId());
		//assertNodeExists(processInstance, "Structure Analysis");
		
		assertNodeTriggered(processInstance.getId(), "Structure Analysis");
		// let sale structure analyser execute Task Structure Analysis
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("StructureAnalyser", "en-UK");
		TaskSummary task = list.get(0);
		System.out.println("StructureAnalyser is executing task " + task.getName());
		taskService.start(task.getId(), "StructureAnalyser");
		taskService.complete(task.getId(), "StructureAnalyser", null);
	
		assertNodeTriggered(processInstance.getId(), "Content Analysis");
		list = taskService.getTasksAssignedAsPotentialOwner("ContentAnalyser", "en-UK");
		task = list.get(0);
		System.out.println("ContentAnalyser is executing task " + task.getName());
		taskService.start(task.getId(), "ContentAnalyser");
		taskService.complete(task.getId(), "ContentAnalyser", null);
		
		assertNodeTriggered(processInstance.getId(), "Processing");
		list = taskService.getTasksAssignedAsPotentialOwner("Processor", "en-UK");
		task = list.get(0);
		System.out.println("Processor is executing task " + task.getName());
		taskService.start(task.getId(), "Processor");
		taskService.complete(task.getId(), "Processor", null);
		
		assertNodeTriggered(processInstance.getId(), "Validation");
		list = taskService.getTasksAssignedAsPotentialOwner("Validator", "en-UK");
		task = list.get(0);
		System.out.println("Validator is executing task " + task.getName());
		taskService.start(task.getId(), "Validator");
		taskService.complete(task.getId(), "Validator", null);
		
		assertProcessInstanceCompleted(processInstance.getId());
		
		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();
	}
	
	/**/
	
	protected abstract String getPersistenceProviderName();
	
	protected String getPersistenceUnitName(){
		return "persistenceUnit"+getPersistenceProviderName();
	}
	
	protected String getPersistenceUnitNameResourceLocal(){
		return getPersistenceUnitName()+"ResourceLocal";
	}
	
	protected String getPersistenceUnitNameJta(){
		return getPersistenceUnitName()+"Jta";
	}
	
	protected Map<String, String> getPersistenceUnitProperties(){
		Map<String, String> properties = new HashMap<String, String>();
	    properties.put(getPersistenceUnitPropertyNameJdbcUrl(), getPersistenceUnitPropertyValueJdbcUrl());
	    properties.put(getPersistenceUnitPropertyNameExportSchemaDdlToDatabase(), getPersistenceUnitPropertyValueExportSchemaDdlToDatabase());
	    properties.put(getPersistenceUnitPropertyNameShowSql(), getPersistenceUnitPropertyValueShowSql());
	    properties.put(getPersistenceUnitPropertyNameTransactionManagerLookupClass(), getPersistenceUnitPropertyValueTransactionManagerLookupClass());
	    return properties;
	}
	
	protected String getPersistenceUnitPropertyNameJdbcUrl(){
		return "javax.persistence.jdbc.url";
	}
	
	protected String getPersistenceUnitPropertyValueJdbcUrl(){
		return "jdbc:hsqldb:mem:hsql";
	}
	
	protected abstract String getPersistenceUnitPropertyNameExportSchemaDdlToDatabase();
	protected abstract String getPersistenceUnitPropertyValueExportSchemaDdlToDatabase();
	protected abstract String getPersistenceUnitPropertyNameShowSql();
	protected abstract String getPersistenceUnitPropertyValueShowSql();
	protected abstract String getPersistenceUnitPropertyNameTransactionManagerLookupClass();
	protected abstract String getPersistenceUnitPropertyValueTransactionManagerLookupClass();
	
	protected EntityManagerFactory getEntityManagerFactory(String persistenceUnitName){
		Map<String, String> properties = getPersistenceUnitProperties();
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName,properties);
		return entityManagerFactory;
	}
	
	protected EntityManager getEntityManager(String persistenceUnitName){
		EntityManager entityManager = getEntityManagerFactory(persistenceUnitName).createEntityManager();
		return entityManager;
	}
	
	protected Query getQuery(String string,String persistenceUnitName){
		return getEntityManager(persistenceUnitName).createQuery(string);
	}
	
	/**/
	
	protected StatefulKnowledgeSession getStatefulKnowledgeSession(String processFileName){
		Environment environment = EnvironmentFactory.newEnvironment();
		// create the entity manager factory and register it in the environment
		environment.set(EnvironmentName.ENTITY_MANAGER_FACTORY, getEntityManagerFactory(getPersistenceUnitNameJta()) );
		// create runtime manager with single process
        createRuntimeManager(processFileName);
        // take RuntimeManager to work with process engine
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        // get access to KieSession instance
        KieSession knowledgeSession = runtimeEngine.getKieSession();
		// create a new knowledge session that uses JPA to store the runtime state
        StatefulKnowledgeSession statefulKnowledgeSession = JPAKnowledgeService.newStatefulKnowledgeSession( knowledgeSession.getKieBase(), null, environment );
        return statefulKnowledgeSession;
	}
	
	
	/**/
	
	public AbstractJpaTestCase() {
		super(true, true);
	}
}
