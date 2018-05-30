package it.bz.idm.bdp.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import it.bz.idm.bdp.dto.security.AccessTokenDto;
import it.bz.idm.bdp.dto.security.JwtTokenDto;

public class SecurityIT {
	
	private static final String TOKEN_PREFIX = "Bearer ";
	private static final String BAD_REFRESH_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwYXRyaWSrLdJlcnRvbGxhIiwicm9sZXMiOiJST0xFX0hhbGxhIn4.j63Q3dNTNbe9jDV-Y0ZaJYglp1u2mNf26r1Zh9wvHwaXoXlC1Cav_LOxaHDA-79az7osM6cYhC4NMjdro5kP1A";
	
	private String username;
	
	private String password;
	
	private Configuration config;


	public SecurityIT() {
		try {
			config = new PropertiesConfiguration("application.properties");
			this.username = config.getString("test.username");
			this.password = config.getString("test.password");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
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
		try {
			new PropertiesConfiguration("application.properties");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
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
