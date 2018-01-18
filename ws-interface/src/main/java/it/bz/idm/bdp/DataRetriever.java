package it.bz.idm.bdp;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;


public abstract class DataRetriever implements IntegreenRunnable{
	private static final String APPLICATION_PROPERTIES_FILE 		= "application.properties";
	protected static final String ENDPOINT_KEY		   				= "endpoint";
	protected static final String HOST_KEY							= "host";
	protected static final String PORT_KEY							= "port";
	protected static final String SSL_KEY							= "ssl";

	
	protected static String DEFAULT_HOST 							= "localhost";
	protected static Integer DEFAULT_PORT							= 8080;
	protected static String DEFAULT_ENDPOINT						= "";
	protected static boolean DEFAULT_SSL							= false;

	
	protected Configuration config;
	protected String integreenTypology;

	public abstract void connect();
	public abstract String initIntegreenTypology();
	
	public DataRetriever() {
		initConfig();
		connect();
		this.integreenTypology = initIntegreenTypology();
	}

	private void initConfig() {
		if (config == null)
			try {
				config = new PropertiesConfiguration(APPLICATION_PROPERTIES_FILE);
				DEFAULT_HOST =  config.getString(HOST_KEY);
				DEFAULT_PORT =	config.getInt(PORT_KEY);
				DEFAULT_ENDPOINT = config.getString(ENDPOINT_KEY);
				DEFAULT_SSL = config.getBoolean(SSL_KEY);
			} catch (ConfigurationException e1) {
				e1.printStackTrace();
			}
	}

}
