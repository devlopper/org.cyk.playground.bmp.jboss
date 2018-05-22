package org.cyk.playground.bpm.jboss;

import java.util.HashMap;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.junit.Test;

public class SetupUnitTest {

	@Test
	public void processFromFile00(){
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		knowledgeBuilder.add(ResourceFactory.newClassPathResource("org/cyk/playground/bpm/jboss/demo.bpmn"), ResourceType.BPMN2);
		KnowledgeBase knowledgeBase = knowledgeBuilder.newKnowledgeBase();			
		StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();		
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Francesco");        
        knowledgeSession.startProcess("com.sample.hello",params);
        knowledgeSession.dispose();
	}
	
	@Test
	public void processFromFile01(){
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		knowledgeBuilder.add(ResourceFactory.newClassPathResource("org/cyk/playground/bpm/jboss/demo01.bpmn"), ResourceType.BPMN2);
		KnowledgeBase knowledgeBase = knowledgeBuilder.newKnowledgeBase();			
		StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();		
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
		String asXml = XmlBPMNProcessDumper.INSTANCE.dump(process);
		
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		knowledgeBuilder.add(ResourceFactory.newByteArrayResource(asXml.getBytes()), ResourceType.BPMN2);
		KnowledgeBase knowledgeBase = knowledgeBuilder.newKnowledgeBase();
		StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Francesco"); 
        ksession.startProcess("com.sample.hello",params);
        ksession.dispose();
	}

}
