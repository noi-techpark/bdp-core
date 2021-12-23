package it.bz.idm.bdp.writer;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dal.util.JPAUtil;

@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(StartupApplicationListener.class);

    @Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOG.info("ENTITYMANAGER: Warmup to have a faster first connection...");
		EntityManager em = null;
		try {
			em = JPAUtil.createEntityManager();
			LOG.info("ENTITYMANAGER: Warmup was a success!");
		} catch (IllegalStateException ex) {
			LOG.error("ENTITYMANAGER: Unable to create the EntityManager at startup! With message => " + ex.getMessage());
		} finally {
			if (em != null && em.isOpen())
				em.close();
		}
		LOG.info("ENTITYMANAGER: Warmup DONE!");
    }
}
