package tests;

import network.TWClient;

import org.junit.Before;
import org.junit.Test;

public class TWClientTest {

	TWClient client;

	@Before
	public void setUp() throws Exception {
		client = new TWClient(null);
	}

	@Test
	public void testThread() {
		client.start();	
//	    Assert.assertEquals(true, client.Auth());
	}


}
