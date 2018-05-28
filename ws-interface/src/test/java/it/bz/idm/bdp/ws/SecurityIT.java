package it.bz.idm.bdp.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import it.bz.idm.bdp.dto.security.AccessTokenDto;
import it.bz.idm.bdp.dto.security.JwtTokenDto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext*.xml"})
public class SecurityIT {
	
	private static final String TOKEN_PREFIX = "Bearer ";
	private static final String BAD_REFRESH_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwYXRyaWSrLdJlcnRvbGxhIiwicm9sZXMiOiJST0xFX0hhbGxhIn4.j63Q3dNTNbe9jDV-Y0ZaJYglp1u2mNf26r1Zh9wvHwaXoXlC1Cav_LOxaHDA-79az7osM6cYhC4NMjdro5kP1A";
	
	@Value("testusername")
	private String username;
	
	@Value("testpassword")
	private String password;
	

	public RestClient client = new RestClient() {
		
		@Override
		public String initIntegreenTypology() {
			return "Not important";
		}
	};
	
	@Test
	public void testFetchRefreshTokenAuthentication(){
		JwtTokenDto authenticationToken = client.fetchRefreshToken(username,password);
		assertNotNull(authenticationToken);
		assertTrue(authenticationToken.getRefreshToken()!= null && !authenticationToken.getRefreshToken().isEmpty());
	}
	
	@Test
	public void testFetchAccessTokenAuthentication(){
		JwtTokenDto refreshToken = client.fetchRefreshToken(username,password);
		assertNotNull(refreshToken.getRefreshToken());
		AccessTokenDto authenticationToken = client.fetchAccessToken(TOKEN_PREFIX+refreshToken.getRefreshToken());
		assertNotNull(authenticationToken);
		assertTrue(authenticationToken.getExpireDate() != null && new Date().getTime() < authenticationToken.getExpireDate());
		assertTrue(authenticationToken.getToken() != null && !authenticationToken.getToken().isEmpty());
	}
	@Test
	public void testFetchAccessTokenAuthenticationWithWrongToken(){
		try {
		client.fetchAccessToken(TOKEN_PREFIX+BAD_REFRESH_TOKEN);
		}catch (Exception e) {
			assertTrue(e instanceof WebClientResponseException);
			WebClientResponseException we = (WebClientResponseException) e;
			assertEquals(HttpStatus.UNAUTHORIZED, we.getStatusCode());
		}
	}
	


	
}
