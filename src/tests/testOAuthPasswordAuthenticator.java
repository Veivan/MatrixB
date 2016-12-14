package tests;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import model.ElementCredentials;
import network.OAuthPasswordAuthenticator;
import service.Constants;
import service.Utils;
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
		this.creds = Utils.ReadINI();
		if (this.creds == null)
			fail("Cannot get credentials");
		auth = new OAuthPasswordAuthenticator(proxy, creds);
	}

	@Test
	public void test() throws Exception {
		AccessToken accessToken = auth.getOAuthAccessTokenSilent();
		assertNotNull(accessToken);
	}

}
