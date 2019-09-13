package it.bz.idm.bdp.ninja.security;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class SecurityUtils {

	private static final String BASE64_PATTERN = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$";
	private static final String KEY_ALGORITHM = "RSA";
	private static final String CERTIFICATE_TYPE = "X.509";

	public static boolean isBase64(final String input) {
		return input.matches(BASE64_PATTERN);
	}

	/**
	 * Create a public key out of a string, which can either be a PEM header format  with
	 * BEGIN/END CERTIFICATE, or a JWT token encoded in Base64 or not. It can also be a x5c
	 * certificate, which is a chain of certs. The method tries to find the right way to
	 * parse the string into a valid {@link PublicKey} object.
	 *
	 * @param key
	 * @return {@link PublicKey}
	 * @throws CertificateException
	 */
	public static PublicKey getPublicKey(final String key) throws CertificateException {
		/*
		 * The PEM header and trailer lines for a cert contain
		 * '-----BEGIN CERTIFICATE-----\n' and '\n-----END CERTIFICATE-----'.
		 * We remove those lines and linebreaks before decoding (if necessary).
		 */
		String cleanKey = key.replace("-----BEGIN CERTIFICATE-----", "")
							 .replace("-----END CERTIFICATE-----", "")
							 .replace("\n", "");

		byte[] byteArrayKey = Base64.getDecoder().decode(cleanKey);

		/* Generate a X.509 public key out of a public key byte array */
		try {
			X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteArrayKey);
			KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
			return kf.generatePublic(X509publicKey);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
			/* Ignore, we have other things to try before failing... */
		}

		/*
		 * Lets see if it is a x5c (a X.509) certificate, which is not
		 * a public key, but an array of certs forming a chain if necessary.
		 *
		 * See https://tools.ietf.org/html/rfc7515#section-4.1.6
		 */
		InputStream certstream = new ByteArrayInputStream (byteArrayKey);
		Certificate cert = CertificateFactory.getInstance(CERTIFICATE_TYPE).generateCertificate(certstream);
		return cert.getPublicKey();
	}

	public static List<String> csvToList(final String input) {
		List<String> result = new ArrayList<String>();
		for (String token : input.split(",")) {
			result.add(token.trim());
		}
		return result;
	}

	public static List<String> getRolesFromAuthentication(Authentication auth) {
		List<String> result = new ArrayList<String>();
		if (auth.getPrincipal() instanceof JWTUser) {
			JWTUser user = (JWTUser) auth.getPrincipal();
			for (GrantedAuthority role : user.getAuthorities()) {
				if (role.getAuthority().startsWith("BDP_")) {
					result.add(role.getAuthority().replaceFirst("BDP_", ""));
				}
			}
		}

		if (result.isEmpty() || !result.contains("GUEST")) {
			result.add("GUEST");
		}
		return result;
	}


}
