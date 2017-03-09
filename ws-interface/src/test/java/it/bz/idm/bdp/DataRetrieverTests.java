package it.bz.idm.bdp;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.xmlrpc.XmlRPCDataRetriever;

public class DataRetrieverTests {

	private DataRetriever retriever;
	@Before
	public void doBefore(){
		 retriever= new XmlRPCDataRetriever() {
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
