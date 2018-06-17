package org.cyk.playground.bpm.jboss;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.test.JBPMHelper;
import org.junit.Test;

public class PersistenceSetupUnitTest {

	@Test
	public void hibernate(){
		JBPMHelper.setupDataSource();
		//Prop
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistenceUnitHibernateJta");
	}
	
}
