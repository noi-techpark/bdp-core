package it.bz.idm.bdp.dal.util;

import org.slf4j.Logger;

import it.bz.idm.bdp.dal.Provenance;
import it.bz.idm.bdp.util.Utils;

import static net.logstash.logback.argument.StructuredArguments.v;

public class Log {

	private Provenance provenance;
	private Logger logger;
	private String methodName;

	public static Log init(Logger logger, String methodName) {
		return new Log(logger, methodName);
	}

	public Log(Logger logger, String methodName, Provenance provenance) {
		this.logger = logger;
		this.provenance = provenance;
		this.methodName = methodName;
	}

	public Log(Logger logger, String methodName) {
		this(logger, methodName, null);
	}

	public Log setProvenance(Provenance provenance) {
		this.provenance = provenance;
		return this;
	}

	public Log setProvenance(String name, String version) {
		this.provenance = new Provenance();
		this.provenance.setDataCollector(name);
		this.provenance.setDataCollectorVersion(version);
		return this;
	}

	public void info(String message) {
		logger.info(
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
