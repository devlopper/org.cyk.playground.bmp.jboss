package org.cyk.playground.bpm.jboss;

public class JpaUsingHibernateUnitTest extends AbstractJpaTestCase {
	
	/**/
	
	@Override
	protected String getPersistenceProviderName() {
		return "Hibernate";
	}	

	@Override
	protected String getPersistenceUnitPropertyNameExportSchemaDdlToDatabase() {
		return "hibernate.hbm2ddl.auto";
	}
	
	@Override
	protected String getPersistenceUnitPropertyValueExportSchemaDdlToDatabase() {
		return "update";
	}
	
	@Override
	protected String getPersistenceUnitPropertyNameShowSql() {
		return "hibernate.show_sql";
	}

	@Override
	protected String getPersistenceUnitPropertyValueShowSql() {
		return "true";
	}
	
	@Override
	protected String getPersistenceUnitPropertyNameTransactionManagerLookupClass() {
		return "hibernate.transaction.manager_lookup_class";
	}
	
	@Override
	protected String getPersistenceUnitPropertyValueTransactionManagerLookupClass() {
		return "org.hibernate.transaction.BTMTransactionManagerLookup";
	}

	/**/
}
