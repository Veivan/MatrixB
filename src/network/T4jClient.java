package network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.Base64;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbaware.DbConnectSingle;
import service.Constants;
import service.Utils;
import twitter4j.Status;
import twitter4j.StatusUpdate;
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

	private MatrixAct act;
	private long ID;
	private JobAtom job;
	private IAccount acc;

	private String ip;
	private int port;
	private Constants.ProxyType proxyType;
	private ElementCredentials creds;
	private Twitter twitter;
	private String failreason = "";

	public T4jClient(MatrixAct theact, ElementProxy dbproxy) {
		this.act = theact;
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
		boolean result = false;
		logger.info("T4jClient run Action : {} {} accID = {} ID = {}",
				this.job.Type.name(), Constants.dfm.format(this.job.timestamp),
				this.acc.getAccID(), this.ID);

		if (!GetCredentials()) {
			logger.info(
					"T4jClient can't got credentials : {} {} accID = {} ID = {}",
					this.job.Type.name(),
					Constants.dfm.format(this.job.timestamp),
					this.acc.getAccID(), this.ID);
			failreason = "T4jClient can't got credentials";
		} else {
			logger.info(
					"T4jClient got twitter instance : {} {} accID = {} ID = {}",
					this.job.Type.name(),
					Constants.dfm.format(this.job.timestamp),
					this.acc.getAccID(), this.ID);

			result = OperateTwitter(this.job);
		}
		logger.info(
				"T4jClient Action result is {} : {} {} accID = {} ID = {}",
				result,
				this.job.Type.name(),
				Constants.dfm.format(this.job.timestamp),
				this.acc.getAccID(), this.ID);
		dbConnector.StoreActResult(this.act, result, failreason);
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

	private boolean OperateTwitter(JobAtom job) {
		Constants.JobType jobType = job.Type;
		boolean result = false;
		try {
			switch (jobType) {
			case TWIT:
				Status status = null;
				StatusUpdate latestStatus = null;
				if (job.TContent.contains("#helpchildren")) {
					// Получение id и картинки
					String page = Utils
							.GetPageContent(Constants.URL_RANDOM_SERVLET);
					JSONObject json = new JSONObject(page);
					int id = json.getInt("id");
					String picenc = json.getString("picture");
					byte[] decodedBytes = Base64.getDecoder().decode(
							picenc.getBytes());

					// UploadedMedia upmedia = twitter.uploadMedia(fileName,
					// is);
					// Формирование Статуса
					InputStream is = new ByteArrayInputStream(decodedBytes);
					String fileName = Integer.toString(id) + ".jpg";
					String message = job.TContent + Integer.toString(id);
					latestStatus = new StatusUpdate(message);
					// Загрузка картинки в твиттер
					latestStatus.setMedia(fileName, is);
					// setLocation(GeoLocation location)
				} else
					latestStatus = new StatusUpdate(job.TContent);
				// Твиттинг
				status = twitter.updateStatus(latestStatus);

				result = true;
				break;
			case SETAVA:
				// User updateProfileImage(File image) throws TwitterException;
				// void updateProfileBanner(File image) throws TwitterException;
				/*
				 * try { // Get timeline // gets Twitter instance with default
				 * credentials Twitter twitter = new
				 * TwitterFactory().getInstance(); User user =
				 * twitter.verifyCredentials(); List<Status> statuses =
				 * twitter.getHomeTimeline(); System.out.println("Showing @" +
				 * user.getScreenName() + "'s home timeline."); for (Status
				 * status : statuses) { System.out.println("@" +
				 * status.getUser().getScreenName() + " - " + status.getText());
				 * } } catch (TwitterException te) { te.printStackTrace();
				 * System.out.println("Failed to get timeline: " +
				 * te.getMessage()); System.exit(-1); }
				 */
				break;
			case DIRECT:
				break;
			case LIKE:
				break;
			case RETWIT:
				break;
			case REPLAY:
				break;
			case SETBACKGROUND:
				// User updateProfileBackgroundImage(File image, boolean tile)
				break;
			case FOLLOW:
				break;
			case UNFOLLOW:
				break;
			default:
				break;
			}
		} catch (Exception e) {
			String premess = "Failed to OperateTwitter";
			logger.error(premess, e);
			logger.debug(premess, e);
			result = false;
			failreason = premess + " : " + e.getMessage();
		}
		return result;
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

		if (this.ip != null)
			cb.setHttpProxyHost(this.ip);
		if (this.port != 0)
			cb.setHttpProxyPort(this.port);

		/*
		 * if (proxyUser != null) cb.setHttpProxyUser(proxyUser); if
		 * (proxyPassword != null) cb.setHttpProxyPassword(proxyPassword); if
		 * (raw) cb.setJSONStoreEnabled(true);
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
