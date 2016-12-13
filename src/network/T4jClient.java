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
	private Twitter twitter = new TwitterFactory().getInstance();

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

		if (!GetCredentials()) return;
		
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

	private boolean GetCredentials() {
		if (Constants.IsDebugCreds) {
			ReadINI();
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

		try {
			if (this.creds == null)
				throw new AuthenticationException( String.format("Cannot get credentials for acc = {}", this.acc.getAccID()) ); 
			
			if (this.creds.getCONSUMER_KEY().isEmpty() || this.creds.getCONSUMER_SECRET().isEmpty() || this.creds.getUSER().isEmpty() || this.creds.getUSER_PASS().isEmpty())
				throw new AuthenticationException( String.format("Empty incoming credentials for acc = {}", this.acc.getAccID()) ); 

			if (this.creds.getACCESS_TOKEN().isEmpty() && this.creds.getACCESS_TOKEN_SECRET().isEmpty())
				getOAuthAccessTokenSilent();
				
		} catch (AuthenticationException e) {
			return false;			
		} catch (Exception e) {
			logger.error("ERROR : ", e);
			logger.debug("ERROR : ", e);
			return false;
		}

		return true;
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
			
			this.creds = new ElementCredentials(CONSUMER_KEY, CONSUMER_SECRET, USER, USER_PASS, ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
		} catch (Exception e) {
			logger.error("ERROR ReaderIni : ", e);
			logger.debug("ERROR ReaderIni: ", e);
		}
	}

	public static class AuthenticationException extends IOException {

		private static final long serialVersionUID = -104987171972968260L;

		AuthenticationException() {
		}

		AuthenticationException(final Exception cause) {
			super(cause);
			logger.error(cause.getMessage());
			logger.debug(cause.getMessage());
		}

		AuthenticationException(final String message) {
			super(message);
			logger.error(message);
			logger.debug(message);
		}
	}

	public static final class AuthenticityTokenException extends AuthenticationException {

		private static final long serialVersionUID = 410500716069698968L;

		AuthenticityTokenException() {
			super("Can't get authenticity token.");
		}
	}

	public static final class InvalidOAuthTokenException extends AuthenticationException {

		private static final long serialVersionUID = -2338352601674116348L;

		InvalidOAuthTokenException() {
			super("Invalid OAuth token.");
		}

	}

}
