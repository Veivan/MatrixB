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
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class testOAuthPasswordAuthenticator {
	OAuthPasswordAuthenticator auth;
	ElementCredentials creds;
	static Proxy proxy;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String ip = "1.2.3.5";
		int port = 8080;
		Constants.ProxyType proxyType = Constants.ProxyType.HTTP;
		SocketAddress addr = new InetSocketAddress(ip, port);
		proxy = new Proxy(
				proxyType == Constants.ProxyType.HTTP ? Proxy.Type.HTTP
						: Proxy.Type.SOCKS, addr);
	}

	@Before
	public void setUp() throws Exception {
		this.creds = Utils.ReadINI();
		if (this.creds == null)
			fail("Cannot get credentials");

		// Creating twitter
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(creds.getCONSUMER_KEY())
				.setOAuthConsumerSecret(creds.getCONSUMER_SECRET())
				.setOAuthAccessToken(creds.getACCESS_TOKEN())
				.setOAuthAccessTokenSecret(creds.getACCESS_TOKEN_SECRET());

		/*
		 * if (proxyHost != null) cb.setHttpProxyHost(proxyHost); if (proxyPort
		 * != null) cb.setHttpProxyPort(Integer.parseInt(proxyPort)); if
		 * (proxyUser != null) cb.setHttpProxyUser(proxyUser); if (proxyPassword
		 * != null) cb.setHttpProxyPassword(proxyPassword); if (raw)
		 * cb.setJSONStoreEnabled(true);
		 */
		
		Configuration conf = cb.build();
		TwitterFactory tf = new TwitterFactory(conf);
		Twitter twitter = tf.getInstance();

		auth = new OAuthPasswordAuthenticator(twitter, creds);
	}

	@Test
	public void test() {
		
		AccessToken accessToken = null;
		try {
			accessToken = auth.getOAuthAccessTokenSilent();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(accessToken);
	}

}
