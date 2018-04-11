package it.bz.idm.bdp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.JPAUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"/META-INF/spring/applicationContext.xml"})
public class SecurityIT extends AbstractJUnit4SpringContextTests{
	public EntityManager entityManager = JPAUtil.createEntityManager();
	
	
	@Test
	public void testJPAUtilInit() {
		assertNotNull(entityManager);
	}

	@Test
	public void testRoleTable() {
		BDPRole role = BDPRole.findByName(entityManager, "role1");
		assertNull(role);
	}
}
