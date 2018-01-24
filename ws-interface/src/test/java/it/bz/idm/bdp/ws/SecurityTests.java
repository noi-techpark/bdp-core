package it.bz.idm.bdp.ws;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.bz.idm.bdp.ws.security.JWTTokenAuthFilter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext*.xml"})
public class SecurityTests {
	
	@Autowired
	JWTTokenAuthFilter filter;
	
	@Test
	public void testInitAuthFilter(){
		assertNotNull(filter);
		assertNotNull(filter.util);
	}
	
	@Test
	public void testAuthentication(){
	}
}
