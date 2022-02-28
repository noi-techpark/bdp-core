package it.bz.idm.bdp.writer;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import it.bz.idm.bdp.dto.ExceptionDto;

import static net.logstash.logback.argument.StructuredArguments.v;

/**
 * Interceptor on each http request to log its name and performance stats
 */
@Component
public class CustomRequestInterceptor extends HandlerInterceptorAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(
		CustomRequestInterceptor.class
	);

	@Override
	public boolean preHandle(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler
	) {
		Instant now = Instant.now();
		String uuid = UUID.randomUUID().toString();
		Map<String, Object> logPayload = new HashMap<>();
		logPayload.put("request_state", "START");
		logPayload.put("request_uuid", uuid);
		logPayload.put("request_path", request.getRequestURI().substring(request.getContextPath().length()));
		logPayload.put("start_time", now.toString());
		logPayload.put("start_epochmilli", now.toEpochMilli());
		request.setAttribute("log_payload", logPayload);
		return true;
	}

	@Override
	public void afterCompletion(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler,
		Exception ex
	) {
		Instant now = Instant.now();
		@SuppressWarnings("unchecked")
		Map<String, Object> logPayload = (Map<String, Object>) request.getAttribute("log_payload");
		long startTime = (Long) logPayload.get("start_epochmilli");
		logPayload.put("end_time", now.toString());
		logPayload.put("end_epochmilli", now.toEpochMilli());
		logPayload.put("response_time_ms", now.toEpochMilli() - startTime);
		logPayload.put("http_status_code", response.getStatus());
		ExceptionDto exceptionDto = (ExceptionDto) request.getAttribute("exception_dto");
		Exception exception = (Exception) request.getAttribute("exception");
		if (exception == null && exceptionDto == null) {
			logPayload.put("request_state", "END");
			LOG.info("API call", v("api_request_info", logPayload));
		} else {
			logPayload.put("request_state", "ERROR");
			logPayload.put("exception_dto", exceptionDto);
			logPayload.put("exception", exception);
			exception.printStackTrace();
			LOG.error("API call", v("api_request_info", logPayload));
		}
	}
}
