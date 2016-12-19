package tests;

import static org.junit.Assert.*;
import jobs.JobAtom;
import main.ConcreteAcc;
import model.ElementProxy;
import model.MatrixAct;
import network.T4jClient;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import service.Constants;

public class testT4jClient {
	T4jClient t4wclient; 
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		JobAtom job = new JobAtom(5L, "TWIT", "thht2");

		ConcreteAcc acc = new ConcreteAcc(1L);
		MatrixAct theact = new MatrixAct(job, acc);
		
		//t4wclient = new T4jClient(theact, new ElementProxy("103.59.57.218", 45554, Constants.ProxyType.SOCKS));
		//t4wclient = new T4jClient(theact, new ElementProxy("183.232.25.100", 1080, Constants.ProxyType.SOCKS));
		t4wclient = new T4jClient(theact, new ElementProxy("178.215.111.70", 9999, Constants.ProxyType.HTTPS));
	}

	@Test
	public void testExecute() {
		try {
			t4wclient.Execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
