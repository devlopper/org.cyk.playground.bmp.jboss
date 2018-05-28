package org.cyk.playground.bpm.jboss;

public class JpaUsingEclipseLinkUnitTest extends AbstractJpaTestCase {
	
	/**/
	
	@Override
	protected String getPersistenceProviderName() {
		return "EclipseLink";
	}
	
	@Override
	protected String getPersistenceUnitPropertyNameExportSchemaDdlToDatabase() {
		return "eclipselink.ddl-generation";
	}
	
	@Override
	protected String getPersistenceUnitPropertyValueExportSchemaDdlToDatabase() {
		return "create-tables";
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
