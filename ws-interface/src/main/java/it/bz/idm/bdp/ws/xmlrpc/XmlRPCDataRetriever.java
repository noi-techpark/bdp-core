package it.bz.idm.bdp.ws.xmlrpc;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import it.bz.idm.bdp.dto.ChildDto;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.TypeDto;
import it.bz.idm.bdp.ws.DataRetriever;
import net.spy.memcached.MemcachedClient;

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

	@Override
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
	@Override
	public String[] fetchStations(){
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
	@Override
	public List<List<String>> fetchDataTypes(String station){
		Object[] params = new Object[]{integreenTypology,station};
		try {
			@SuppressWarnings("unchecked")
			List<List<String>> serverResponse = (List<List<String>>) client.execute("DataRetriever.getDataTypes",params);
			return serverResponse;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw new IllegalStateException("Failed to execute request to xmlrpcserver");
		}
	}
	@Override
	public List<TypeDto> fetchTypes(String station){
		Object[] params = new Object[]{integreenTypology,station};
		try {
			client.execute("DataRetriever.getTypes", params);
			return null;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw new IllegalStateException("Failed to execute request to xmlrpcserver");
		}
	}
	@Override
	public List<RecordDto> fetchRecords(String stationId, String typeId, Integer seconds, Integer period) {
		List<Object> data = new ArrayList<Object>() {
			private static final long serialVersionUID = 1L;
			{
				add(stationId);
				add(typeId);
				add(seconds);
				add(period);
			}
		};
		List<? extends Object> dtos = getRecordsByMethodName("DataRetriever.getRecords",new Object[]{integreenTypology,data.toArray()});
		@SuppressWarnings("unchecked")
		List<RecordDto> records=(List<RecordDto>) dtos;
		return records;
	}
	@Override
	public List<RecordDto> fetchRecords(String stationId, String typeId, Long start, Long end, Integer period) {
		List<Object> data = new ArrayList<Object>() {
			private static final long serialVersionUID = 1L;
			{
				add(stationId);
				add(typeId);
				add(start);
				add(end);
				add(period);
			}
		};
		List<? extends Object> dtos = getRecordsByMethodName("DataRetriever.getRecords",new Object[]{integreenTypology,data.toArray()});
		@SuppressWarnings("unchecked")
		List<RecordDto> records=(List<RecordDto>) dtos;
		return records;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StationDto> fetchStationDetails(String stationId) {
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
	@Override
	public Date fetchDateOfLastRecord(String stationId, String typeId, Integer period) {
		List<Object> list = new ArrayList<Object>() {
			private static final long serialVersionUID = 1L;
			{
				add(stationId);
				add(typeId);
				add(period);
			}
		};
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
	@Override
	public RecordDto fetchNewestRecord(String stationId, String typeId, Integer period) {
		Object serverResponse;
		RecordDto recordDto = null;
		try {
			Object params = null;
			serverResponse = client.execute("DataRetriever.getNewestRecord",new Object[]{integreenTypology,params});
			recordDto = (RecordDto) serverResponse;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw new IllegalStateException("Failed to execute request to xmlrpcserver");
		}
		return recordDto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends ChildDto> fetchChildStations(String id) {
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
