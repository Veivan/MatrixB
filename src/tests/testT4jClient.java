package tests;

import jobs.JobAtom;
import model.ConcreteAcc;
import model.ElementProxy;
import model.MatrixAct;
import network.T4jClient;

import org.junit.Before;
import org.junit.Test;

import service.Constants;
import service.Utils;

public class testT4jClient {
	T4jClient t4wclient;
	private String proxyIP;
	private int proxyPort; 
	
	@Before
	public void setUp() throws Exception {
		//JobAtom job = new JobAtom(5L, "TWIT", "Winter coming"); 

		/*String name = "Лорик Соловаева";
		String url = "";
		String location = "Гондурас";
		String description = "Мне бы в небо...";
		JobAtom job = new JobAtom(10L, "UPDATEPROFILE", name, url, location, description); */
		
		/*byte[] img = Utils.readBytesFromFile("c:\\temp\\a1.png");	
		JobAtom job = new JobAtom(10L, "SETBANNER", img); */ 
		
		/*byte[] img = Utils.readBytesFromFile("c:\\temp\\pexels-photo-67475.jpeg");	
		JobAtom job = new JobAtom(10L, "SETAVA", img); */
				
		//JobAtom job = new JobAtom(12L, "READTIMELINE", "");
		
		JobAtom job = new JobAtom(100L, "NEWUSER", ""); 
				
		ConcreteAcc acc = new ConcreteAcc(56L);
		MatrixAct theact = new MatrixAct(job, acc);
		
		//String proxy = "103.59.57.218:45554"; // Constants.ProxyType.SOCKS not works
		//String proxy = "178.215.111.70:9999"; // HTTP not works - timeout expired

		String proxy = "188.166.28.38:3128"; // good HTTPS
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
