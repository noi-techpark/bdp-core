package it.bz.idm.bdp.reader.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
	protected final Log logger = LogFactory.getLog(this.getClass());

	@Override
	public void commence(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException authException)
			throws IOException, ServletException {
		String contentType = request.getContentType();
		logger.info(contentType);
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized" );
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
	}

}
