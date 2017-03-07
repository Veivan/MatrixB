package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import model.ElementCredentials;
import network.OAuthPasswordAuthenticator;
import service.Utils;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class testOAuthPasswordAuthenticator {
	OAuthPasswordAuthenticator auth;
	ElementCredentials creds;
	String proxyHost = "194.171.38.134";
	int proxyPort = 80;

	@Before
	public void setUp() throws Exception {
		this.creds = Utils.ReadINI();
		if (this.creds == null)
			fail("Cannot get credentials");

		// Creating twitter
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(creds.getCONSUMER_KEY())
				.setOAuthConsumerSecret(creds.getCONSUMER_SECRET())
				.setHttpConnectionTimeout(30000)
				//.setOAuthAccessToken(creds.getACCESS_TOKEN())
				//.setOAuthAccessTokenSecret(creds.getACCESS_TOKEN_SECRET())
				;

		
		 if (proxyHost != null) cb.setHttpProxyHost(proxyHost); 
		 if (proxyPort != 0) cb.setHttpProxyPort(proxyPort); 
		 /*if
		 * (proxyUser != null) cb.setHttpProxyUser(proxyUser); if (proxyPassword
		 * != null) cb.setHttpProxyPassword(proxyPassword); if (raw)
		 * cb.setJSONStoreEnabled(true);
		 */
		
		Configuration conf = cb.build();
		TwitterFactory tf = new TwitterFactory(conf);
		Twitter twitter = tf.getInstance();

		auth = new OAuthPasswordAuthenticator(twitter, creds, null);
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
