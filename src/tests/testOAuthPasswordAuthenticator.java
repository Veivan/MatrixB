package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.Properties;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import model.ElementCredentials;
import network.OAuthPasswordAuthenticator;
import service.Constants;
import twitter4j.auth.AccessToken;

public class testOAuthPasswordAuthenticator {
	OAuthPasswordAuthenticator auth;
	ElementCredentials creds;
	static Proxy proxy;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String ip = "1.2.3.5";
		int port = 8080;
		Constants.ProxyType proxyType = Constants.ProxyType.HTTPS;
		SocketAddress addr = new InetSocketAddress(ip, port);
		proxy = new Proxy(
				proxyType == Constants.ProxyType.HTTPS ? Proxy.Type.HTTP
						: Proxy.Type.SOCKS, addr);
	}

	@Before
	public void setUp() throws Exception {
		ReadINI();
		if (this.creds == null)
			fail("Cannot get credentials");
		auth = new OAuthPasswordAuthenticator(proxy, creds);
	}

	@Test
	public void test() throws Exception {
		AccessToken accessToken = auth.getOAuthAccessTokenSilent();
		assertNotNull(accessToken);
	}

	private void ReadINI() {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(new File("example.ini")));
			String CONSUMER_KEY = props.getProperty("CONSUMER_KEY");
			String CONSUMER_SECRET = props.getProperty("CONSUMER_SECRET");
			String USER = props.getProperty("USER");
			String USER_PASS = props.getProperty("USER_PASS");
			String ACCESS_TOKEN = props.getProperty("ACCESS_TOKEN");
			String ACCESS_TOKEN_SECRET = props
					.getProperty("ACCESS_TOKEN_SECRET");

			this.creds = new ElementCredentials(CONSUMER_KEY, CONSUMER_SECRET,
					USER, USER_PASS, ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
		} catch (Exception e) {
			System.out.println("ERROR ReaderIni : " + e.getMessage());
		}
	}

}
