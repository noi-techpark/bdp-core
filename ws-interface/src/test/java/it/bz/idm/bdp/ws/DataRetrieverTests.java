package it.bz.idm.bdp.ws;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.ws.DataRetriever;
import it.bz.idm.bdp.ws.RestClient;


public class DataRetrieverTests {

	private DataRetriever retriever;
	@Before
	public void doBefore(){
		 retriever= new RestClient(){
			@Override
			public String initIntegreenTypology() {
				return "TestStation";
			}
		};
	}
	
	@Test
	public void testInitDataRetriever(){
		assertNotNull(retriever.integreenTypology);
		assertNotNull(retriever.config);
	}
}
