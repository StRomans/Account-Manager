package fr.coward.main.database;

import org.flywaydb.core.Flyway;

public class MigrationManager {
	
	private static final String TABLE = "APPLICATION_VERSION";

	public static void migrate(String dataBaseUrl){
		
		Flyway flyway = new Flyway();
		
		flyway.setTable(TABLE);
		flyway.setDataSource(dataBaseUrl, null, null);
		flyway.setBaselineOnMigrate(true);
		
		flyway.setLocations("/scripts");
		
		flyway.migrate();
	}
}
