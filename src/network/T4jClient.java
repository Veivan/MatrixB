package network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

	private ElementProxy dbproxy;
	private ElementCredentials creds;
	private Twitter twitter;
	private String failreason = "";

	public T4jClient(MatrixAct theact, ElementProxy dbproxy) {
		this.act = theact;
		this.ID = theact.getSelfID();
		this.job = theact.getJob();
		this.acc = theact.getAcc();
		this.dbproxy = dbproxy;
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
		logger.info("T4jClient Action result is {} : {} {} accID = {} ID = {}",
				result, this.job.Type.name(),
				Constants.dfm.format(this.job.timestamp), this.acc.getAccID(),
				this.ID);
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

			if (Utils.empty(this.creds.getCONSUMER_KEY())
					|| Utils.empty(this.creds.getCONSUMER_SECRET())
					|| Utils.empty(this.creds.getUSER())
					|| Utils.empty(this.creds.getUSER_PASS()))
				throw new AuthenticationException(String.format(
						"Empty incoming credentials for acc = {}",
						this.acc.getAccID()));

			// Creating twitter
			Configuration conf = buildTwitterConfiguration(creds, dbproxy);
			TwitterFactory tf = new TwitterFactory(conf);
			this.twitter = tf.getInstance();

			if (Utils.empty(this.creds.getACCESS_TOKEN())
					&& Utils.empty(this.creds.getACCESS_TOKEN_SECRET())) {
				OAuthPasswordAuthenticator auth = new OAuthPasswordAuthenticator(
						this.twitter, this.creds);
				AccessToken accessToken = null;
				for (int i = 0; i < 3; i++) {
					try {
						accessToken = auth.getOAuthAccessTokenSilent();
						if (accessToken != null) break;						
					} catch (Exception e) {
						String msg = String.format(
								"Get accessToken shot %d ERROR : ", i);
						logger.error(msg, e);
					}
				}

				if (accessToken != null) {
					creds.setACCESS_TOKEN(accessToken.getToken());
					creds.setACCESS_TOKEN_SECRET(accessToken.getTokenSecret());

					this.twitter.setOAuthAccessToken(accessToken);
					dbConnector.SaveToken(this.acc.getAccID(),
							this.creds.getId_app(), accessToken.getToken(),
							accessToken.getTokenSecret());
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
				StatusUpdate latestStatus = null;
				if (job.TContent.contains("#helpchildren")) {
					// Получение id и картинки
					String page = Utils
							.GetPageContent(Constants.URL_RANDOM_SERVLET);
					JSONObject json = new JSONObject(page);
					int id = json.getInt("id");
					String pname = json.getString("name");
					String ppage = json.getString("age");
					String picenc = json.getString("picture");
					byte[] decodedBytes = Base64.getDecoder().decode(
							picenc.getBytes());

					// Формирование Статуса
					InputStream is = new ByteArrayInputStream(decodedBytes);
					String fileName = Integer.toString(id) + ".jpg";

					String message = String.format(
							"%s %s. Вы можете помочь.%n", pname, ppage)
							+ "http://helpchildren.online/?id="
							+ id
							+ " "
							+ job.TContent; // + #подарижизнь
					latestStatus = new StatusUpdate(message);
					// Загрузка картинки в твиттер
					latestStatus.setMedia(fileName, is);
					// setLocation(GeoLocation location)
				} else
					latestStatus = new StatusUpdate(job.TContent);
				// Твиттинг
				Status status = twitter.updateStatus(latestStatus);

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
			final ElementCredentials creds, final ElementProxy dbproxy) {
		logger.debug("creating twitter configuration");
		ConfigurationBuilder cb = new ConfigurationBuilder();

		cb.setOAuthConsumerKey(creds.getCONSUMER_KEY())
				.setOAuthConsumerSecret(creds.getCONSUMER_SECRET())
				.setOAuthAccessToken(creds.getACCESS_TOKEN())
				.setOAuthAccessTokenSecret(creds.getACCESS_TOKEN_SECRET());

		String ip = dbproxy.getIp();
		int port = dbproxy.getPort();
		if (ip != null)
			cb.setHttpProxyHost(ip);
		if (port != 0)
			cb.setHttpProxyPort(port);

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
