package tests;

import jobs.JobAtom;
import model.ConcreteAcc;
import model.ElementProxy;
import model.MatrixAct;
import network.T4jClient;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.Constants;

public class testT4jClient {
	T4jClient t4wclient;
	private String proxyIP;
	private int proxyPort; 
	
	static Logger logger = LoggerFactory.getLogger(testT4jClient.class);

	@Before
	public void setUp() throws Exception {
		//String TWIT2 = "{\"command\": \"TWIT\" , "
		//		+ " \"twit_id\" : \"1\" ] , "
		//		+ " \"tags\" : [ \"#helpchildren\", \"Дети\" ] , "
		//		+ " \"lat\" : \"55.751244\" , " + " \"lon\" : \"37.618423\" } ";
		//JobAtom job = new JobAtom(12L, "TWIT", "#helpchildren"); 

		/*String TWIT2 = "{\"command\": \"TWIT\" , "
				+ " \"twit_id\" : \"0\" , " 
				+ " \"twcontent\" : \"Крутяк\" , " 
				+ " \"pic_id\" : \"9\" , " 
				+ " \"lat\" : \"55.751244\" , " + " \"lon\" : \"37.618423\" } ";
		JobAtom job = new JobAtom(12L, "TWIT", TWIT2); */ 

		/**/
		//String name = "Лорик Соловаева";
		String name = "Светлана Керимова";
		//String url = "https://www.yandex.ru/";
		String url = "";
		String location = "Гондурас";
		String description = "Мне бы в небо...";
		JobAtom job = new JobAtom(10L, "UPDATEPROFILE", name, url, location, description); 
		
		/*byte[] img = Utils.readBytesFromFile("c:\\temp\\a1.png");	
		JobAtom job = new JobAtom(10L, "SETBANNER", img); */ 
		
		/*byte[] img = Utils.readBytesFromFile("c:\\temp\\pexels-photo-67475.jpeg");	
		JobAtom job = new JobAtom(10L, "SETAVA", img); */
//		String SETAVA = "{\"command\": \"SETAVA\" } ";
//		JobAtom job = new JobAtom(10L, "SETAVA", SETAVA); 
				
		/*String READHOMETIMELINE = "{\"command\": \"READHOMETIMELINE\" } ";
		JobAtom job = new JobAtom(12L, "READHOMETIMELINE", READHOMETIMELINE); */
		
		//String READUSERTIMELINE = "{\"command\": \"READUSERTIMELINE\" } ";
		//JobAtom job = new JobAtom(12L, "READUSERTIMELINE", "ntvru"); 
		
		//JobAtom job = new JobAtom(100L, "NEWUSER", "");  
		//JobAtom job = new JobAtom(101L, "CHECKENABLED", "");  				
		//String RETWIT = "{\"command\": \"RETWIT\" , \"twit_id\" : \"843532240860659713\"} ";
		//String RETWIT = "{\"command\": \"RETWIT\"} ";
		//JobAtom job = new JobAtom(100L, "RETWIT", RETWIT);
		//String LIKE = "{\"command\": \"LIKE\" , \"twit_id\" : \"843532240860659713\"} ";
		//JobAtom job = new JobAtom(102L, "LIKE", LIKE);
			
		//Moscow double lat = 55.751244; double lon = 37.618423
		//"37.781157,-122.398720,10mi" "55.751244,37.618423,1km"
				
		//String query = "q=#helpchildren&geocode=55.751244,37.618423,10km&result_type=recent";
		//String query = "#helpchildren";
		//String SEARCH = "{\"command\": \"SEARCH\" , "	+ " \"query\" : \"#helpchildren\" } ";
		/*String SEARCH = "{\"command\": \"SEARCH\" , "	+ " \"query\" : \"#посудасервиз\" } ";
		JobAtom job = new JobAtom(102L, "SEARCH", SEARCH); */

		ConcreteAcc acc = new ConcreteAcc(2702, 0);  
		MatrixAct theact = new MatrixAct(job, acc);
		
		//String proxy = "37.9.40.132:8085"; // good HTTPS on RuVDS
		String proxy = "178.212.43.163:8080"; // good HTTPS
		//String proxy = "66.110.216.105:39431"; // good SOCKS5  
		
		// 130 51.141.32.241:8080 test error code
		String[] sp = proxy.split(":");
		if (sp.length > 1) {
			proxyIP = sp[0];
			proxyPort = Integer.parseInt(sp[1]);
		}
		
		t4wclient = new T4jClient(theact, new ElementProxy(proxyIP, proxyPort, Constants.ProxyType.HTTP, -1)); 		
		//t4wclient = new T4jClient(theact, new ElementProxy(proxyIP, proxyPort, Constants.ProxyType.SOCKS, -1)); 		
	}

	@Test
	public void testExecute() {
		try {
			t4wclient.Execute();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}
	
/*	
	public static void main(String[] args) {
		testT4jClient t= new testT4jClient();
		String SEARCH = "{\"command\": \"SEARCH\" , "	+ " \"query\" : \"#посудасервиз\" } ";
		JobAtom job = new JobAtom(102L, "SEARCH", SEARCH); 
		//JobAtom job = new JobAtom(12L, "TWIT", "#helpchildren"); 
		ConcreteAcc acc = new ConcreteAcc(2702, 0);
		MatrixAct theact = new MatrixAct(job, acc);
		String proxy = "37.9.40.132:8085"; // good HTTPS  
		String[] sp = proxy.split(":");
		if (sp.length > 1) {
			t.proxyIP = sp[0];
			t.proxyPort = Integer.parseInt(sp[1]);
		}
		
		T4jClient t4wclient = new T4jClient(theact, new ElementProxy(t.proxyIP, t.proxyPort, Constants.ProxyType.HTTP, -1)); 		
		try {
			t4wclient.Execute();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	} 
*/

}
