package org.cyk.playground.bpm.jboss;

import java.util.HashMap;

import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;


public class SetupUnitTest extends JbpmJUnitBaseTestCase  {

	@Test
	public void processFromFile00(){
		// create runtime manager with single process - hello.bpmn
        createRuntimeManager("org/cyk/playground/bpm/jboss/demo.bpmn");
        // take RuntimeManager to work with process engine
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        // get access to KieSession instance
        KieSession knowledgeSession = runtimeEngine.getKieSession();
		
		HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Francesco");        
        knowledgeSession.startProcess("com.sample.hello",params);
        knowledgeSession.dispose();
	}
	
	@Test
	public void processFromFile01(){
		// create runtime manager with single process - hello.bpmn
        createRuntimeManager("org/cyk/playground/bpm/jboss/demo01.bpmn");
        // take RuntimeManager to work with process engine
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        // get access to KieSession instance
        KieSession knowledgeSession = runtimeEngine.getKieSession();
        
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Francesco");        
        knowledgeSession.startProcess("defaultPackage.New_Process",params);
        knowledgeSession.dispose();
	}
	
	@Test
	public void processFromApi(){
		RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("com.sample.hello");
		
		factory
			// Header
			    .name("com.sample.hello")
			    .packageName("com.sample")
			    // Nodes
			    .startNode(1).name("Start").done()
			    .actionNode(2).name("Action")
			        .action("java", "System.out.println(\"Hello World ! This is from the API\");").done()
			    .endNode(3).name("End").done()
			    // Connections
			    .connection(1, 2)
			    .connection(2, 3);
		RuleFlowProcess process = factory.validate().getProcess();
		
		KieServices ks = KieServices.Factory.get();
		KieFileSystem kfs = ks.newKieFileSystem();
		Resource resource = ks.getResources().newByteArrayResource(XmlBPMNProcessDumper.INSTANCE.dump(process).getBytes());
		resource.setSourcePath("helloworld.bpmn2");
		kfs.write(resource);
		ReleaseId releaseId = ks.newReleaseId("org.jbpm", "helloworld", "1.0");
		kfs.generateAndWritePomXML(releaseId);
		ks.newKieBuilder(kfs).buildAll();
		KieSession kieSession = ks.newKieContainer(releaseId).newKieSession();
		kieSession.startProcess("com.sample.hello");		
		kieSession.dispose();
	}

}
