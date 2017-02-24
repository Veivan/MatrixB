package tests;

import java.io.FileNotFoundException;

import jobs.JobAtom;
import model.ConcreteAcc;
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
		//JobAtom job = new JobAtom(12L, "TWIT", "#helpchildren"); 

		/*String name = "Лорик Соловаева";
		String url = "";
		String location = "Гондурас";
		String description = "Мне бы в небо...";
		JobAtom job = new JobAtom(10L, "UPDATEPROFILE", name, url, location, description); */
		
		/*byte[] img = Utils.readBytesFromFile("c:\\temp\\a1.png");	
		JobAtom job = new JobAtom(10L, "SETBANNER", img); */ 
		
		/*byte[] img = Utils.readBytesFromFile("c:\\temp\\pexels-photo-67475.jpeg");	
		JobAtom job = new JobAtom(10L, "SETAVA", img); */
				
		//JobAtom job = new JobAtom(12L, "READHOMETIMELINE", "");
		
		//JobAtom job = new JobAtom(100L, "NEWUSER", "");  

		//JobAtom job = new JobAtom(101L, "CHECKENABLED", "");  
				
		JobAtom job = new JobAtom(100L, "RETWIT", "");  

		ConcreteAcc acc = new ConcreteAcc(130L);
		MatrixAct theact = new MatrixAct(job, acc);
		
		String proxy = "167.114.171.178:80"; // good HTTPS  
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
	
/*	public static void main(String[] args) throws FileNotFoundException {
		testT4jClient t= new testT4jClient();
		JobAtom job = new JobAtom(12L, "TWIT", "#helpchildren"); 
		ConcreteAcc acc = new ConcreteAcc(130L);
		MatrixAct theact = new MatrixAct(job, acc);
		String proxy = "185.2.101.31:3128"; // good HTTPS 
		String[] sp = proxy.split(":");
		if (sp.length > 1) {
			t.proxyIP = sp[0];
			t.proxyPort = Integer.parseInt(sp[1]);
		}
		
		T4jClient t4wclient = new T4jClient(theact, new ElementProxy(t.proxyIP, t.proxyPort, Constants.ProxyType.HTTP, -1)); 		
		try {
			t4wclient.Execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} */


}
