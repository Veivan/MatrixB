package tests;

import static org.junit.Assert.*;
import jobs.JobAtom;
import main.ConcreteAcc;
import model.ElementProxy;
import model.MatrixAct;
import network.T4jClient;

import org.junit.Before;
import org.junit.Test;

import service.Constants;

public class testT4jClient {
	T4jClient t4wclient;
	private String proxyIP;
	private int proxyPort; 
	
	@Before
	public void setUp() throws Exception {
		//JobAtom job = new JobAtom(5L, "TWIT", "Winter coming"); 

		String name = "Лорик Соловаева";
		String url = "";
		String location = "Гондурас";
		String description = "Мне бы в небо...";

		JobAtom job = new JobAtom(10L, "UPDATEPROFILE", name, url, location, description); 
		ConcreteAcc acc = new ConcreteAcc(57L);
		MatrixAct theact = new MatrixAct(job, acc);
		
		//String proxy = "103.59.57.218:45554"; // Constants.ProxyType.SOCKS not works
		//String proxy = "178.215.111.70:9999"; // HTTP not works - timeout expired
		//String proxy = "212.224.76.176:80"; // good but slow
		String proxy = "168.9.128.232:65000"; // good 
		String[] sp = proxy.split(":");
		if (sp.length > 1) {
			proxyIP = sp[0];
			proxyPort = Integer.parseInt(sp[1]);
		}
		
		t4wclient = new T4jClient(theact, new ElementProxy(proxyIP, proxyPort, Constants.ProxyType.HTTP, -1)); 		
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
