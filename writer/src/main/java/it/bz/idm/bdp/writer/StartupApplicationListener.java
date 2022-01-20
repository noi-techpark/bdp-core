package it.bz.idm.bdp.writer;

import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dal.util.PropertiesWithEnv;

import java.io.IOException;

import javax.persistence.EntityManager;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartupApplicationListener
	implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(
		StartupApplicationListener.class
	);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		PropertiesWithEnv prop;
		try {
			prop = PropertiesWithEnv.fromActiveSpringProfile();
			if (prop.getProperty("flyway.enabled").equalsIgnoreCase("true")) {
				Flyway flyway = Flyway
					.configure()
					.dataSource(
						prop.getProperty("flyway.url"),
						prop.getProperty("flyway.user"),
						prop.getProperty("flyway.password")
					)
					.schemas(prop.getProperty("flyway.defaultSchema"))
					.defaultSchema(prop.getProperty("flyway.defaultSchema"))
					.locations(prop.getProperty("flyway.locations"))
					.load();
				LOG.info("FLYWAY: Start migration for on " + prop.getProperty("flyway.url"));
				flyway.migrate();
				LOG.info("FLYWAY: Migrations done...");
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to run FLYWAY migrations: " + e);
		}

		LOG.info("ENTITYMANAGER: Warmup to have a faster first connection...");
		EntityManager em = null;
		try {
			em = JPAUtil.createEntityManager();
			LOG.info("ENTITYMANAGER: Warmup was a success!");
		} catch (IllegalStateException ex) {
			LOG.error(
				"ENTITYMANAGER: Unable to create the EntityManager at startup! With message => " +
				ex.getMessage()
			);
		} finally {
			if (em != null && em.isOpen()) em.close();
		}
		LOG.info("ENTITYMANAGER: Warmup DONE!");
	}
}
