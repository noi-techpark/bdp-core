package it.bz.idm.bdp.xmlrpc;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import it.bz.idm.bdp.DataPusher;
import it.bz.idm.bdp.util.IntegreenException;
public abstract class XMLRPCPusher extends DataPusher {
	
	private static final String PROTOCOLL 					= "http://";
	protected XmlRpcClient client;

	public void connectToDataCenterCollector(String serverHost, Integer serverPort,String siteName) throws MalformedURLException {
		if (serverHost == null)
			serverHost = DEFAULT_HOST;
		if (serverPort == null)
			serverPort = DEFAULT_PORT;
		if (siteName == null)
			siteName = DEFAULT_ENDPOINT;
		if (client==null) {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		    config.setServerURL(new URL(PROTOCOLL + serverHost + ":" + serverPort + siteName));
			config.setEnabledForExtensions(true);
			config.setGzipRequesting(true);
			client = new XmlRpcClient();
			client.setConfig(config);
		}
	}

	public void connectToDataCenterCollector() {
		try {
			connectToDataCenterCollector(null, null, null);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println(PROTOCOLL+DEFAULT_HOST+":"+DEFAULT_PORT+DEFAULT_ENDPOINT+" is not well formed pls contact Sysadmin");
		}
	}

	/*
	 * pushes Data through the xmlrpc client to the collector
	 * @param datasourceName the identifier of the datasource required by the collector to associate the right stationtype
	 * @param data any kind of data, you need to handle it on the collector side
	 */

	public Object pushData(String path, String integreenTypology, Object[] data) {
		
		Object[] params = new Object[2];
		params[0] = integreenTypology;
		params[1] = data;
		try {
			return client.execute(path, params);
		} catch (XmlRpcException e) {
			e.printStackTrace();
			return new IntegreenException(e);
		}
	}
	public Object pushData(Object[] data) {
		connectToDataCenterCollector();
		return pushData("DataCollector.pushRecords", integreenTypology,data);
	}
	public Object pushData(String integreenTypology, Object[] data) {
		connectToDataCenterCollector();
		return pushData("DataCollector.pushRecords", integreenTypology,data);
	}

	public Object syncStations(Object[] data) {
		connectToDataCenterCollector();
		return pushData("DataCollector.syncStations", integreenTypology, data);
	}

	public Object syncStations(String integreenTypology, Object[] data) {
		connectToDataCenterCollector();
		return pushData("DataCollector.syncStations", integreenTypology, data);
	}

	public Object syncDataTypes(Object[] data){
		connectToDataCenterCollector();
		return pushData("DataCollector.syncDataTypes",integreenTypology, data);
	}
	public Object syncDataTypes(String integreenTypology, Object[] data){
		connectToDataCenterCollector();
		return pushData("DataCollector.syncDataTypes",integreenTypology, data);
	}

	public Object getDateOfLastRecord(
			String stationCode, String dataType, Integer period) {
		Object[] params = new Object[]{integreenTypology,stationCode,dataType,period};
		try {
			connectToDataCenterCollector();
			return client.execute("DataCollector.getDateOfLastRecord",params);
		} catch (XmlRpcException e) {
			e.printStackTrace();
			return new IntegreenException(e);
		}
	}


}
