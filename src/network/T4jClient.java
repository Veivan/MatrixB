package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.Constants;
import service.Constants.ProxyType;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import jobs.JobAtom;
import model.ElementCredentials;
import model.ElementProxy;
import model.MatrixAct;
import inrtfs.IAccount;
import inrtfs.IJobExecutor;

public class T4jClient implements IJobExecutor {

	private long ID;
	private JobAtom job;
	private IAccount acc;

	private String ip;
	private int port;
	private Constants.ProxyType proxyType;
	private CloseableHttpClient httpclient;

	private ElementCredentials creds;

	public T4jClient(MatrixAct theact, ElementProxy dbproxy) {
		this.ID = theact.getSelfID();
		this.job = theact.getJob();
		this.acc = theact.getAcc();
		this.ip = dbproxy.getIp();
		this.port = dbproxy.getPort();
		this.proxyType = dbproxy.getProxyType();
	}

	static Logger logger = LoggerFactory.getLogger(T4jClient.class);

	@Override
	public void Execute() {
		logger.info("T4jClient run Action : {} {} accID = {} ID = {}",
				this.job.Type.name(), Constants.dfm.format(this.job.timestamp),
				this.acc.getAccID(), this.ID);

	}

	/**
	 * Build configuration object with credentials and proxy settings
	 * 
	 * @return
	 */
	private Configuration buildTwitterConfiguration() {
		logger.debug("creating twitter configuration");
		ConfigurationBuilder cb = new ConfigurationBuilder();

		/*
		 * cb.setOAuthConsumerKey(oauthConsumerKey)
		 * .setOAuthConsumerSecret(oauthConsumerSecret)
		 * .setOAuthAccessToken(oauthAccessToken)
		 * .setOAuthAccessTokenSecret(oauthAccessTokenSecret);
		 * 
		 * if (proxyHost != null) cb.setHttpProxyHost(proxyHost); if (proxyPort
		 * != null) cb.setHttpProxyPort(Integer.parseInt(proxyPort)); if
		 * (proxyUser != null) cb.setHttpProxyUser(proxyUser); if (proxyPassword
		 * != null) cb.setHttpProxyPassword(proxyPassword); if (raw)
		 * cb.setJSONStoreEnabled(true);
		 */
		logger.debug("twitter configuration created");
		return cb.build();
	}

	private boolean GetCredentials(boolean IsDebug) {
		if (Constants.IsDebugCreds) {

			try {
				ReadINI();
			} catch (FileNotFoundException e) {
				logger.error("ReaderIni FileNotFoundException : ", e);
				logger.debug("ReaderIni FileNotFoundException : ", e);
			} catch (IOException e) {
				logger.error("ReaderIni IOException : ", e);
				logger.debug("ReaderIni IOException : ", e);
			}

			if (this.creds == null)
			{
				logger.error("Can't get credentials for acc =  : {}", this.acc.getAccID());
				logger.debug("Can't get credentials for acc =  : {}", this.acc.getAccID());
				return false;
			}

		} 
		
		// TODO Read from DB
		/*else {  
			dbproxy = ProxyGetter.getProxy(this.theact.getAcc().getAccID());
			if (dbproxy == null) {
				logger.error("TWClient cant get proxy");
				logger.debug("TWClient cant get proxy");
				return false;
			}
		} */
		
/*		if (this.creds.getACCESS_TOKEN().isEmpty())
			getOAuthAccessTokenSilent();
*/		
		return true;
	}

	/*/ Getting token without PIN
	public void getOAuthAccessTokenSilent() throws Exception {
		try {
			ReadINI();
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			final RequestToken requestToken = twitter.getOAuthRequestToken(DEFAULT_OAUTH_CALLBACK);
			//final RequestToken requestToken = twitter.getOAuthRequestToken();
			final String oauth_token = requestToken.getToken();
			System.out.println("Got request token.");
			System.out.println("Request token: " + oauth_token);
			System.out.println("Request token secret: "
					+ requestToken.getTokenSecret());
			AccessToken accessToken = null;

						
			// make sure cookies is turn on
			CookieHandler.setDefault(new CookieManager());

			String page = GetPageContent(requestToken.getAuthorizationURL());
//			List<NameValuePair> postParams = Utils.getFormParams(page, USER, USER_PASS);

			String authenticity_token = Utils.readAuthenticityToken(page);
			if (authenticity_token.isEmpty())
				throw new AuthenticationException(
						"Cannot get authenticity_token.");

			final Configuration conf = twitter.getConfiguration();
			System.out.println("OAuthAuthorizationURL : " + conf.getOAuthAuthorizationURL());
		
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();

			paramList.add(new  BasicNameValuePair("oauth_token", URLEncoder.encode(oauth_token, "UTF-8")));
			paramList.add(new  BasicNameValuePair("session[username_or_email]", URLEncoder.encode(USER, "UTF-8")));
			paramList.add(new  BasicNameValuePair("session[password]", URLEncoder.encode(USER_PASS, "UTF-8")));
			paramList.add(new  BasicNameValuePair("authenticity_token", URLEncoder.encode(authenticity_token, "UTF-8")));

			String page2 = sendPost(conf.getOAuthAuthorizationURL().toString(), paramList);

			final String oauth_verifier = Utils.readOauthVerifier(page2);
			// parseParameters(callback_url.substring(callback_url.indexOf("?")
			// + 1)).get(OAUTH_VERIFIER);

			if (oauth_verifier.isEmpty())
				throw new AuthenticationException("Cannot get OAuth verifier.");

			try {
				accessToken = twitter.getOAuthAccessToken(requestToken,
						oauth_verifier);
			} catch (TwitterException te) {
				if (401 == te.getStatusCode()) {
					System.out.println("Unable to get the access token.");
				} else {
					te.printStackTrace();
				}
			}

			System.out.println("Got access token.");
			System.out.println("Access token: " + accessToken.getToken());
			System.out.println("Access token secret: "
					+ accessToken.getTokenSecret());
			SetPropsINI(accessToken);
			System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get accessToken: " + te.getMessage());
			System.exit(-1);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("Failed to read the system input.");
			System.exit(-1);
		}
	}
*/
	private void ReadINI() throws FileNotFoundException, IOException {
		Properties props = new Properties();
			props.load(new FileInputStream(new File("example.ini")));
			String CONSUMER_KEY = props.getProperty("CONSUMER_KEY");
			String CONSUMER_SECRET = props.getProperty("CONSUMER_SECRET");
			String USER = props.getProperty("USER");
			String USER_PASS = props.getProperty("USER_PASS");
			String ACCESS_TOKEN = props.getProperty("ACCESS_TOKEN");
			String ACCESS_TOKEN_SECRET = props
					.getProperty("ACCESS_TOKEN_SECRET");
			
			this.creds = new ElementCredentials(CONSUMER_KEY, CONSUMER_SECRET, USER, USER_PASS, ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
	}

	public static class AuthenticationException extends IOException {
		AuthenticationException() {
		}

		AuthenticationException(final Exception cause) {
			super(cause);
		}

		AuthenticationException(final String message) {
			super(message);
		}
	}

	public static final class AuthenticityTokenException extends AuthenticationException {
		AuthenticityTokenException() {
			super("Can't get authenticity token.");
		}
	}

	public static final class InvalidOAuthTokenException extends AuthenticationException {
		InvalidOAuthTokenException() {
			super("Invalid OAuth token.");
		}

	}

}
