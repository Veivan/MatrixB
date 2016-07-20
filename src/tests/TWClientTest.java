package tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import main.TWClient;

public class TWClientTest {

	TWClient client;

	@Before
	public void setUp() throws Exception {
		client = new TWClient();
		client.setData("MyData");
	}

	@Test
	public void testGetData() {
		Assert.assertEquals("MyData", client.getData());
	}

	@Test
	public void testAddData() {
		client.addData("Add");
	    Assert.assertEquals("MyDataAdd", client.getData());
	}

	@Test
	public void testAuth() {
	    Assert.assertEquals(true, client.Auth());
	}

}
