package it.bz.idm.bdp.ninja.security;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.Authentication;

public class SecurityUtils {

	public static List<String> getRolesFromAuthentication(Authentication auth) {
		List<String> result = new ArrayList<>();
		if (auth instanceof KeycloakAuthenticationToken) {
			SimpleKeycloakAccount user = (SimpleKeycloakAccount) auth.getDetails();
			for (String role : user.getRoles()) {
				if (role.startsWith("BDP_")) {
					result.add(role.replaceFirst("BDP_", ""));
				}
			}
		}

		if (result.isEmpty() || !result.contains("GUEST")) {
			result.add("GUEST");
		}
		return result;
	}


}
