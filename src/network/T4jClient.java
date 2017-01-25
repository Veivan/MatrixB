package network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbaware.DbConnectSingle;
import service.CustExeptions.AuthenticationException;
import service.CustExeptions.ProxyException;
import service.Constants;
import service.GenderChecker;
import service.GenderChecker.Gender;
import service.Utils;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import jobs.JobAtom;
import model.ElementCredentials;
import model.ElementProxy;
import model.MatrixAct;
import model.ConcreteAcc;
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

			result = OperateTwitter();
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

				AccessToken accessToken = null;
				OAuthPasswordAuthenticator auth = new OAuthPasswordAuthenticator(
						this.twitter, this.creds);
				for (int j = 0; j < Constants.cTryProxyCount; j++) {
					for (int i = 0; i < Constants.cTrySameProxyCount; i++) {
						String msg = String
								.format("Get accessToken shot %d with proxy %d ERROR : ",
										i+1, j+1);
						try {
							accessToken = auth.getOAuthAccessTokenSilent();
							if (accessToken != null)
								break;
						} catch (ProxyException e) {
							logger.error(msg, e);

							dbConnector.setProxyIsAlive(dbproxy.getProxyID(),
									false);

							// Getting twitter with another proxy
							dbproxy = ProxyGetter.getProxy(this.acc.getAccID());
							if (dbproxy == null) {
								throw new AuthenticationException(
										String.format(
												" cant get proxy for acc = {}",
												this.acc.getAccID()));
							} else {
								// Creating twitter
								conf = buildTwitterConfiguration(creds, dbproxy);
								tf = new TwitterFactory(conf);
								this.twitter = tf.getInstance();
							}
							break;
						} catch (TwitterException te) {
							// При возникновении TwitterException, не связанных
							// с сетью (400...500)
							// не баним прокси, а выходим из цикла
							throw new AuthenticationException(String.format(
									" Сant get AccessToken for acc = {} - {}",
									this.acc.getAccID(), te.getMessage()));
						} catch (Exception e) {
							logger.error(msg, e);
						}
					}
					if (accessToken != null)
						break;
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
		} catch (Exception e) {
			logger.error("ERROR : ", e);
			return false;
		}

		return true;
	}

	/**
	 * directly update status
	 * 
	 * @return Status
	 */
	private Status SendTwit() throws Exception {
		StatusUpdate latestStatus = null;
		if (job.TContent.contains("#helpchildren")) {
			// Получение id и картинки
			String page = Utils.GetPageContent(Constants.URL_RANDOM_SERVLET);
			JSONObject json = new JSONObject(page);
			int id = json.getInt("id");
			String pname = json.getString("name");
			String ppage = json.getString("age");
			String picenc = json.getString("picture");
			byte[] decodedBytes = Base64.getDecoder().decode(picenc.getBytes());

			// Формирование Статуса
			InputStream is = new ByteArrayInputStream(decodedBytes);
			String fileName = Integer.toString(id) + ".jpg";

			String message = String.format("%s %s. Вы можете помочь.%n", pname,
					ppage)
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
		return twitter.updateStatus(latestStatus);
	}

	/**
	 * directly make new user
	 * 
	 * @return
	 * @throws TwitterException
	 * @throws IOException
	 */
	private void MakeUser(boolean fillprof, User user) throws TwitterException,
			IOException {
		// Определение пола
		Gender gender = GenderChecker.get_gender(user.getName());
		// Сохранение дополнительных данных в БД
		((ConcreteAcc) this.acc).setName(user.getName());
		((ConcreteAcc) this.acc).setTwitter_id(user.getId());
		((ConcreteAcc) this.acc).setGender(gender);
		dbConnector.SaveAcc2Db((ConcreteAcc) acc, -1);

		if (fillprof) {
			// Установка картинок для акка
			int ptype_id = 1;
			// BANNERIMG
			byte[] bytes = dbConnector.getRandomPicture(gender, ptype_id);
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			twitter.updateProfileBanner(bis);
			bis.close();
			ptype_id = 2;
			// PROFILEIMG
			bytes = dbConnector.getRandomPicture(gender, ptype_id);
			bis = new ByteArrayInputStream(bytes);
			twitter.updateProfileImage(bis);
			bis.close();
		}
	}

	private boolean OperateTwitter() {
		Constants.JobType jobType = this.job.Type;
		byte[] buf = null;
		ByteArrayInputStream bis = null;
		List<Status> statuses = null;
		boolean result = false;
		try {
			for (int i = 0; i < Constants.cTrySameProxyCount; i++) {
				String msg = String
						.format("OperateTwitter shot %d ERROR : ", i+1);
				try {
					User user = twitter.verifyCredentials();
					switch (jobType) {
					case TWIT:
						Status status = SendTwit();
						result = true;
						break;
					case SETAVA:
						buf = job.getProfileImage();
						bis = new ByteArrayInputStream(buf);
						twitter.updateProfileImage(bis);
						bis.close();
						result = true;
						break;
					case SETBANNER:
						buf = job.getProfileBanner();
						bis = new ByteArrayInputStream(buf);
						twitter.updateProfileBanner(bis);
						bis.close();
						result = true;
						break;
					case UPDATEPROFILE:
						User us = twitter.updateProfile(job.getName(),
								job.getUrl(), job.getLocation(),
								job.getDescription());
						System.out.println(us.getScreenName() + " : "
								+ us.getId());
						result = true;
						break;
					case READHOMETIMELINE:
						statuses = twitter.getHomeTimeline();
						System.out.println("Showing @" + user.getScreenName()
								+ "'s home timeline.");
						for (Status stat : statuses) {
							System.out.println(stat.getCreatedAt() + " @"
									+ stat.getUser().getScreenName() + " - "
									+ stat.getText());
						}
						result = true;
						break;
					case READUSERTIMELINE:
						statuses = twitter.getUserTimeline();
						System.out.println("Showing @" + user.getScreenName()
								+ "'s user timeline.");
						for (Status stat : statuses) {
							System.out.println(stat.getCreatedAt() + " @"
									+ stat.getUser().getScreenName() + " - "
									+ stat.getText());
						}
						result = true;
						break;
					case CHECKENABLED:
						boolean IsEnabled = false;
						statuses = twitter.getUserTimeline();
						if (statuses == null || statuses.size() == 0)
							IsEnabled = true;
						else {
							long interval = Utils.getDateDiff(statuses.get(0)
									.getCreatedAt().getTime(),
									System.currentTimeMillis(), TimeUnit.DAYS);
							IsEnabled = interval > Constants.cIntervalOfLastUse;
						}
						System.out.println("@" + user.getScreenName() + " is "
								+ (IsEnabled ? "enabled" : "disabled"));
						result = true;
						break;
					case NEWUSER:
						MakeUser(true, user);
						result = true;
						break;
					case NEWUSERBRUT:
						MakeUser(false, user);
						result = true;
						break;
					case DIRECT:
						break;
					case LIKE:
						break;
					case RETWIT:
						break;
					case REPLAY:
						break;
					case FOLLOW:
						break;
					case UNFOLLOW:
						break;
					default:
						break;
					}
				} catch (TwitterException te) {
					// При возникновении TwitterException, не связанных
					// с сетью (400...500) выходим из цикла
					if (te.isCausedByNetworkIssue()) {
						logger.error(msg, te);
					} else {
						throw te;
					}
				} catch (Exception e) {
					logger.error(msg, e);
				}
				if (result == true)
					break;
			}
		} catch (Exception e) {
			String premess = "Failed to OperateTwitter";
			logger.error(premess, e);
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

}
