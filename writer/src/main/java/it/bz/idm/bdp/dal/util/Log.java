package it.bz.idm.bdp.dal.util;

import org.slf4j.Logger;

import it.bz.idm.bdp.dal.Provenance;
import it.bz.idm.bdp.util.Utils;

import static net.logstash.logback.argument.StructuredArguments.v;

public class Log {

	private Provenance provenance;
	private Logger logger;
	private String methodName;

	public Log(Logger logger, String methodName, Provenance provenance) {
		this.logger = logger;
		this.provenance = provenance;
		this.methodName = methodName;
	}

	public Log(Logger logger, String methodName) {
		this(logger, methodName, null);
	}

	public void setProvenance(Provenance provenance) {
		this.provenance = provenance;
	}

	public void warn(String message) {
		logger.warn(
			"{}: {}",
			methodName,
			message,
			v("api_request_info",
				Utils.mapOf(
					"provenance_name", provenance == null ? null : provenance.getDataCollector(),
					"provenance_version", provenance == null ? null : provenance.getDataCollectorVersion()
				)
			)
		);
	}

	public void error(String message, Object... arguments) {
		logger.error(
			"{}: {}",
			methodName,
			message,
			v("api_request_info",
				Utils.mapOf(
					"provenance_name", provenance == null ? null : provenance.getDataCollector(),
					"provenance_version", provenance == null ? null : provenance.getDataCollectorVersion()
				)
			),
			arguments
		);
	}

}
