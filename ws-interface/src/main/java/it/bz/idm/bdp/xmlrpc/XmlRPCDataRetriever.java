package it.bz.idm.bdp.xmlrpc;


import it.bz.idm.bdp.DataRetriever;
import it.bz.idm.bdp.util.IntegreenException;
import it.bz.idm.bdp.dto.ChildDto;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.spy.memcached.MemcachedClient;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public abstract class XmlRPCDataRetriever extends DataRetriever{
	protected static final String MEMCACHE_HOST_KEY 						= "memcache.host";
	protected static final String MEMCACHE_PORT_KEY 						= "memcache.port";
	protected static final String MEMCACHE_TIMEOUT_KEY 					= "memcache.timeout";
	protected static final String MEMCACHE_CACHINGTIME_KEY 				= "memcache.cachingtime";
	protected static final String PROTOCOL 								= "http://";
	protected static XmlRpcClient client;
	protected MemcachedClient memClient;
	private boolean cachingEnabled = false;

	public void connect(String serverHost, Integer serverPort,String site) {
		if (serverHost == null)
			serverHost = DEFAULT_HOST;
		if (serverPort == null)
			serverPort = DEFAULT_PORT;
		if (site == null)
			site = DEFAULT_ENDPOINT;
		if (client == null) {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			try {
				config.setServerURL(new URL(PROTOCOL + serverHost + ":" + serverPort + site));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			config.setEnabledForExtensions(true);
			config.setGzipRequesting(true);
			client = new XmlRpcClient();
			client.setConfig(config);
		}
		if (cachingEnabled && memClient == null){
			try {
				memClient = new MemcachedClient(new InetSocketAddress(config.getString(MEMCACHE_HOST_KEY), config.getInt(MEMCACHE_PORT_KEY)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void connect(String serverHost) {
		connect(serverHost, null,null);
	}

	public void connect() {
		connect(null, null, null);
	}	

	protected List<? extends Object> getRecordsByMethodName(String pMethodName,
			Object[] params) {
		Object object = null;	
		List<Object> parameterList= new ArrayList<Object>(Arrays.asList((Object[])params[1]));
		parameterList.add(0,params[0]);
		if (cachingEnabled){
			StringBuffer memcachedKey = new StringBuffer();
			for (Object obj : params){
				String stringToAppend;
				if (obj == null || obj instanceof Date)
					continue;
				else 
					stringToAppend = obj.toString();
				memcachedKey.append(stringToAppend);
			}
			String key = memcachedKey.toString().replaceAll("\\s+","");
			object = memClient.get(key);
			if (object == null){
				try {
					object = requestFromDispatcher(pMethodName, parameterList.toArray());
					Object[] response = (Object[]) object;
					List<? extends Object> dtos= Arrays.asList(response);
					int timeToCache = config.getInt(MEMCACHE_CACHINGTIME_KEY);
					if (!dtos.isEmpty() && dtos.get(0) instanceof SimpleRecordDto) {
						SimpleRecordDto rec = (SimpleRecordDto) dtos.get(dtos.size()-1);
						Long secondsOfTimestampOver = (rec.getTimestamp()/1000)%rec.getPeriod();
						Long secondsNowToLastPeriod = (new Date().getTime()/1000)%rec.getPeriod();
						timeToCache = new Long(rec.getPeriod()-secondsNowToLastPeriod-secondsOfTimestampOver).intValue();
					}
					memClient.set(key,timeToCache, object);
				} catch (XmlRpcException e) {
					e.printStackTrace();
				}
			}
		}else{
			try {
				object = requestFromDispatcher(pMethodName, parameterList.toArray());
			} catch (XmlRpcException e) {
				e.printStackTrace();
			}
		}
		Object[] response = (Object[]) object;
		List<? extends Object> dtos= Arrays.asList(response);
		return dtos;
	}
	private Object requestFromDispatcher(String pMethodName, Object[] params)
			throws XmlRpcException {
		Object object;
		object = client.execute(pMethodName,params);
		return object;
	}
	public String[] getStations(){
		Object[] params= new Object[]{integreenTypology};
		Object execute;
		try {
			execute = client.execute("DataRetriever.getStations",params);
			Object[] stationIds=(Object[]) execute;
			return Arrays.copyOf(stationIds, stationIds.length, String[].class);
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw new IllegalStateException("Failed to execute request to xmlrpcserver");
		}
	}

	public List<Object> getDataTypes(String station){
		Object[] params = new Object[]{integreenTypology,station};
		Object[] objects;
		try {
			objects = (Object[]) client.execute("DataRetriever.getDataTypes",params);
			List<Object> serverResponse= Arrays.asList(objects);
			return serverResponse;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw new IllegalStateException("Failed to execute request to xmlrpcserver");
		}
	}
	public List<Object> getTypes(String station){
		Object[] params = new Object[]{integreenTypology,station};
		Object[] objects;
		try {
			Object execute = client.execute("DataRetriever.getTypes",params);
			objects = (Object[]) execute;
			List<Object> list = Arrays.asList(objects);
			return list;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw new IllegalStateException("Failed to execute request to xmlrpcserver");
		}
	}

	@SuppressWarnings("unchecked")
	public List<RecordDto> getRecords(Object... params){
		List<? extends Object> dtos = getRecordsByMethodName("DataRetriever.getRecords",new Object[]{integreenTypology,params});
		List<RecordDto> records=(List<RecordDto>) dtos;
		return records;
	}

	public List<StationDto> getStationDetails(String stationId) {
		Object[] params= new Object[]{integreenTypology,stationId};
		Object execute;
		try {
			execute = client.execute("DataRetriever.getStationDetails",params);
			Object[] stations=(Object[]) execute;
			List<? extends Object> asList = Arrays.asList(stations);
			return (List<StationDto>) asList;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw new IllegalStateException("Failed to execute request to xmlrpcserver");
		}
	}

	public Date getDateOfLastRecord(Object... params) {
		List<Object> list = new ArrayList<Object>(Arrays.asList(params));
		list.add(0, integreenTypology);
		Object serverResponse;
		try {
			serverResponse = client.execute("DataRetriever.getDateOfLastRecord",list.toArray());
			return (Date)serverResponse;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw new IllegalStateException("Failed to execute request to xmlrpcserver");
		}
	}

	public Object getNewestRecord(Object... params) {
		Object serverResponse;
		try {
			serverResponse = client.execute("DataRetriever.getNewestRecord",new Object[]{integreenTypology,params});
			if (serverResponse instanceof RecordDto)
				return (RecordDto) serverResponse;
			else{
				IntegreenException ex = (IntegreenException) serverResponse;
				return ex;
			}
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw new IllegalStateException("Failed to execute request to xmlrpcserver");
		}
	}
	public List<? extends ChildDto> getChildren(String id) {
		Object[] params= new Object[]{integreenTypology,id};
		Object execute;
		try {
			execute = client.execute("DataRetriever.getChildren",params);
			Object[] stationIds=(Object[]) execute;
			List<? extends Object> dtos = Arrays.asList(stationIds);
			return (List<ChildDto>) dtos;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw new IllegalStateException("Failed to execute request to xmlrpcserver");
		}
	}
	public boolean isCachingEnabled() {
		return cachingEnabled;
	}
	public void setCachingEnabled(boolean cachingEnabled) {
		this.cachingEnabled = cachingEnabled;
	}	
}
