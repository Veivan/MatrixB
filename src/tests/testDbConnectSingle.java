package tests;

import static org.junit.Assert.*;

import java.util.List;

import model.ElementProxy;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dbaware.DbConnectSingle;

public class testDbConnectSingle {
	DbConnectSingle dbConnector;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		dbConnector = DbConnectSingle.getInstance();
	}

	@Test
	public void testGetFreeProxies() {
		List<ElementProxy> proxylist = dbConnector.getFreeProxies();
		assertFalse(proxylist.size() == 0);
	}

	@Test
	public void testGetProxy4Acc() {
		int AccID = 1;
		ElementProxy accproxy = dbConnector.getProxy4Acc(AccID);
		assertNotNull(accproxy); 		
	}

}
