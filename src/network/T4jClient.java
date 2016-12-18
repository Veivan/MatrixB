package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbaware.DbConnectSingle;
import service.Constants;
import service.Utils;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
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
	private ElementCredentials creds;
	private Twitter twitter;

	public T4jClient(MatrixAct theact, ElementProxy dbproxy) {
		this.ID = theact.getSelfID();
		this.job = theact.getJob();
		this.acc = theact.getAcc();
		this.ip = dbproxy.getIp();
		this.port = dbproxy.getPort();
		this.proxyType = dbproxy.getProxyType();
	}

	static Logger logger = LoggerFactory.getLogger(T4jClient.class);
	DbConnectSingle dbConnector = DbConnectSingle.getInstance();

	@Override
	public void Execute() {
		logger.info("T4jClient run Action : {} {} accID = {} ID = {}",
				this.job.Type.name(), Constants.dfm.format(this.job.timestamp),
				this.acc.getAccID(), this.ID);

		if (!GetCredentials()) {
			logger.info(
					"T4jClient can't got credentials : {} {} accID = {} ID = {}",
					this.job.Type.name(),
					Constants.dfm.format(this.job.timestamp),
					this.acc.getAccID(), this.ID);
			return;
		}
		logger.info(
				"T4jClient got twitter instance : {} {} accID = {} ID = {}",
				this.job.Type.name(), Constants.dfm.format(this.job.timestamp),
				this.acc.getAccID(), this.ID);

	}

	private boolean GetCredentials() {
		try {
			if (Constants.IsDebugCreds) {
				this.creds = Utils.ReadINI();
			} else {
				this.creds = dbConnector.getCredentials(this.acc.getAccID());
			}

			if (this.creds == null)
				throw new AuthenticationException(String.format(
						"Cannot get credentials for acc = {}",
						this.acc.getAccID()));

			if (this.creds.getCONSUMER_KEY().isEmpty()
					|| this.creds.getCONSUMER_SECRET().isEmpty()
					|| this.creds.getUSER().isEmpty()
					|| this.creds.getUSER_PASS().isEmpty())
				throw new AuthenticationException(String.format(
						"Empty incoming credentials for acc = {}",
						this.acc.getAccID()));

			SocketAddress addr = new InetSocketAddress(this.ip, this.port);
			Proxy proxy = new Proxy(
					this.proxyType == Constants.ProxyType.HTTPS ? Proxy.Type.HTTP
							: Proxy.Type.SOCKS, addr);
			// Creating twitter
			Configuration conf = buildTwitterConfiguration(creds, proxy);
			TwitterFactory tf = new TwitterFactory(conf);
			this.twitter = tf.getInstance();

			if (this.creds.getACCESS_TOKEN().isEmpty()
					&& this.creds.getACCESS_TOKEN_SECRET().isEmpty()) {
				OAuthPasswordAuthenticator auth = new OAuthPasswordAuthenticator(
						this.twitter, this.creds);
				AccessToken accessToken = auth.getOAuthAccessTokenSilent();
				if (accessToken != null) {
					creds.setACCESS_TOKEN(accessToken.getToken());
					creds.setACCESS_TOKEN_SECRET(accessToken.getTokenSecret());

					// TODO Need save accessToken in DB
					this.twitter.setOAuthAccessToken(accessToken);
				} else
					throw new AuthenticationException(String.format(
							"AccessToken is null for acc = {}",
							this.acc.getAccID()));
			}
		} catch (AuthenticationException e) {
			return false;
		} catch (Exception e) {
			logger.error("ERROR : ", e);
			logger.debug("ERROR : ", e);
			return false;
		}

		return true;
	}

	/**
	 * Build configuration object with credentials and proxy settings
	 * 
	 * @return
	 */
	private Configuration buildTwitterConfiguration(
			final ElementCredentials creds, final Proxy proxy) {
		logger.debug("creating twitter configuration");
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

		logger.debug("twitter configuration created");
		return cb.build();
	}

	public static class AuthenticationException extends IOException {
		private static final long serialVersionUID = -104987171972968260L;
		private final String ident = "AuthenticationException : ";

		AuthenticationException() {
		}

		AuthenticationException(final Exception cause) {
			super(cause);
			logger.error(ident + cause.getMessage());
			logger.debug(ident + cause.getMessage());
		}

		AuthenticationException(final String message) {
			super(message);
			logger.error(ident + message);
			logger.debug(ident + message);
		}
	}
}
