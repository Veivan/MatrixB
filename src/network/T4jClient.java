package network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbaware.DbConnector;
import service.Constants.JobType;
import service.Constants.ProxyType;
import service.CustExeptions.AuthRetypeException;
import service.CustExeptions.AuthenticationException;
import service.CustExeptions.ProxyException;
import service.Constants;
import service.GenderChecker;
import service.GenderChecker.Gender;
import service.Utils;
import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
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
import model.TwFriend;
import inrtfs.IAccount;
import inrtfs.IJobExecutor;
import inrtfs.TwiCommand;

public class T4jClient implements IJobExecutor {

	private MatrixAct act;
	private long ID;
	private JobAtom job;
	private IAccount acc;

	private ElementProxy dbproxy;
	private ElementCredentials creds;
	private Twitter twitter;
	private String failreason = "";
	private int errorCode = -1;

	public T4jClient(MatrixAct theact, ElementProxy dbproxy) {
		this.act = theact;
		this.ID = theact.getSelfID();
		this.job = theact.getJob();
		this.acc = theact.getAcc();
		this.dbproxy = dbproxy;
	}

	static Logger logger = LoggerFactory.getLogger(T4jClient.class);
	DbConnector dbConnector = DbConnector.getInstance();

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
		if (errorCode > 0) {
			dbConnector.setAccIsEnabled(this.acc.getAccID(), false, errorCode);
		}
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
						"Cannot get credentials for acc = %d",
						this.acc.getAccID()));

			if (Utils.empty(this.creds.getCONSUMER_KEY())
					|| Utils.empty(this.creds.getCONSUMER_SECRET())
					|| Utils.empty(this.creds.getUSER())
					|| Utils.empty(this.creds.getUSER_PASS()))
				throw new AuthenticationException(String.format(
						"Empty incoming credentials for acc = %d",
						this.acc.getAccID()));

			// Creating twitter
			Configuration conf = buildTwitterConfiguration(creds, dbproxy);
			TwitterFactory tf = new TwitterFactory(conf);
			this.twitter = tf.getInstance();

			if (Utils.empty(this.creds.getACCESS_TOKEN())
					&& Utils.empty(this.creds.getACCESS_TOKEN_SECRET())) {

				AccessToken accessToken = null;
				for (int j = 0; j < Constants.cTryProxyCount; j++) {
					for (int i = 0; i < Constants.cTrySameProxyCount; i++) {
						String msg = String
								.format("Get accessToken shot %d with proxy %d ERROR : ",
										i + 1, j + 1);
						try {
							OAuthPasswordAuthenticator auth = new OAuthPasswordAuthenticator(
									this.twitter, this.creds, this.acc);
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
												" cant get proxy for acc = %d",
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
									" Cant get AccessToken for acc = %d - %s",
									this.acc.getAccID(), te.getMessage()));
						} catch (AuthenticationException e) {
							throw e;
						} catch (AuthRetypeException e) {
							logger.error(msg, e);
							// Creating twitter
							conf = buildTwitterConfiguration(creds, dbproxy);
							tf = new TwitterFactory(conf);
							this.twitter = tf.getInstance();
							continue;
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
							"AccessToken is null for acc = %d",
							this.acc.getAccID()));
			}
		} catch (Exception e) {
			logger.error("ERROR : ", e);
			if (e instanceof AuthenticationException)
				errorCode = ((AuthenticationException) e).getErrorCode();
			dbConnector.makeProxy4AccFree(this.acc.getAccID());
			return false;
		}

		return true;
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
			User user = twitter.verifyCredentials();
			dbConnector.SaveAccExtended(this.acc.getAccID(), user);
			Thread.sleep(Utils.getDelay());

			TwiCommand twicommand = null;
			CommandBuilder combuilder = new CommandBuilder(this.job, twitter, user);

			for (int i = 0; i < Constants.cTrySameProxyCount; i++) {
				String msg = String.format("OperateTwitter shot %d ERROR : ",
						i + 1);
				try {
					switch (jobType) {
					case TWIT:
						twicommand = combuilder.GetCommand();
						twicommand.execute();
						result = true;
						break;
					case READHOMETIMELINE:
						twicommand = combuilder.GetCommand();
						twicommand.execute();
						result = true;
						break;
					case SETAVA:
						if (user.isDefaultProfileImage()) {
							ConcreteAcc theAcc = (ConcreteAcc) this.acc;
							buf = dbConnector.getRandomPicture(
									theAcc.getGender(), 2);
							if (buf != null) {
								bis = new ByteArrayInputStream(buf);
								twitter.updateProfileImage(bis);
								bis.close();
							}
						}
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
						logger.debug("@" + user.getScreenName() + " is "
								+ (IsEnabled ? "enabled" : "disabled"));
						dbConnector.setAccIsEnabled(this.acc.getAccID(),
								IsEnabled, -1);
						if (!IsEnabled)
							dbConnector.makeProxy4AccFree(this.acc.getAccID());
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
						String twit_id2 = job.GetContentProperty("twit_id");
						if (twit_id2.isEmpty() && Utils.DoItByDice())
							LikeOne(twitter);
						else
							LikeConcrete(twitter, Long.parseLong(twit_id2));
						result = true;
						break;
					case RETWIT:
						String twit_id = job.GetContentProperty("twit_id");
						if (twit_id.isEmpty() && Utils.DoItByDice())
							RetwitOne(twitter);
						else
							RetwitConcrete(twitter, Long.parseLong(twit_id));
						result = true;
						break;
					case REPLAY:
						break;
					case FOLLOW:
						if (Utils.DoItByDice()) {
							TwFriend friend = dbConnector
									.GetRandomScreenName(this.acc.getAccID());
							if (friend != null) {
								twitter.createFriendship(friend.getScreenName());
								dbConnector.StoreFollowInfo(
										this.acc.getAccID(),
										friend.getTwitter_id(), true);
							}
						}
						result = true;
						break;
					case UNFOLLOW:
						break;
					case SEARCH:
						MakeTwitSearch();
						result = true;
						break;
					default:
						break;
					}

					final Set<JobType> JobTypeSet = new HashSet<JobType>(
							Arrays.asList(JobType.SETAVA, JobType.SETBANNER,
									JobType.UPDATEPROFILE /*
														 * , JobType.FOLLOW,
														 * JobType.UNFOLLOW
														 */));
					if (JobTypeSet.contains(jobType)) {
						user = twitter.verifyCredentials();
						dbConnector.SaveAccExtended(this.acc.getAccID(), user);
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

			// Скорее всего был плохой прокси - сбросим его и забаним
			if (result == false) {
				dbConnector.setProxy4Acc(this.acc.getAccID(), null);
				throw new ProxyException("Bad proxy when OperateTwitter");
			}

		} catch (Exception e) {
			String premess = "Failed to OperateTwitter";
			logger.error(premess + " acc = " + this.acc.getAccID(), e);
			result = false;
			failreason = premess + " : " + e.getMessage();
			if (e instanceof TwitterException)
				errorCode = ((TwitterException) e).getErrorCode();
		}
		return result;
	}

	/**
	 * Function read home timeline and stores twits 2 DB. Then sort twits by
	 * RetwitCount descending and retweet first twit (with Max RetwitCount).
	 * 
	 * @return
	 */
	private void RetwitOne(Twitter twitter) throws TwitterException {
		List<Status> statuses = twitter.getHomeTimeline();
		if (statuses.size() > 0) {
			List<Status> notMineList = new ArrayList<Status>();
			for (Status stat : statuses) {
				dbConnector.StoreStatus(stat);
				if (!dbConnector.isRetweetedByUser(stat.getId(),
						this.acc.getAccID()))
					notMineList.add(stat);
			}
			if (notMineList.size() > 0) {
				long status_id = Utils.GetPreferedStatus(notMineList,
						Constants.CompareBy.RetwitCount);
				try {
					Thread.sleep(Utils.getDelay());
				} catch (InterruptedException e) {
				}
				logger.debug(
						"T4jClient RetwitOne : status_id = {} accID = {} ID = {} ",
						status_id, this.acc.getAccID(), this.ID);
				Status statusrt = twitter.retweetStatus(status_id);
				dbConnector.StoreStatus(statusrt);
			}
		}
	}

	/**
	 * Function read home timeline and stores twits 2 DB. Then retweet single
	 * twit with twit_id.
	 * 
	 * @return
	 */
	private void RetwitConcrete(Twitter twitter, long twit_id)
			throws TwitterException {
		List<Status> statuses = twitter.getHomeTimeline();
		for (Status stat : statuses)
			dbConnector.StoreStatus(stat);
		try {
			Thread.sleep(Utils.getDelay());
		} catch (InterruptedException e) {
		}
		Status statusrt = twitter.retweetStatus(twit_id);
		dbConnector.StoreStatus(statusrt);
	}

	/**
	 * Function read home timeline and stores twits 2 DB. Then sort twits by
	 * FavoriteCount descending and likes first twit (with Max FavoriteCount).
	 * 
	 * @return
	 */
	private void LikeOne(Twitter twitter) throws TwitterException {
		List<Status> statuses = twitter.getHomeTimeline();
		if (statuses.size() > 0) {
			for (Status stat : statuses) {
				dbConnector.StoreStatus(stat);
			}
			long status_id = Utils.GetPreferedStatus(statuses,
					Constants.CompareBy.FavoriteCount);
			try {
				Thread.sleep(Utils.getDelay());
			} catch (InterruptedException e) {
			}
			logger.debug(
					"T4jClient LikeOne : status_id = {} accID = {} ID = {} ",
					status_id, this.acc.getAccID(), this.ID);
			Status statusrt = twitter.createFavorite(status_id);
			dbConnector.StoreStatus(statusrt);
		}
	}

	/**
	 * Function read home timeline and stores twits 2 DB. Then likes single twit
	 * with twit_id.
	 * 
	 * @return
	 */
	private void LikeConcrete(Twitter twitter, long twit_id)
			throws TwitterException {
		List<Status> statuses = twitter.getHomeTimeline();
		for (Status stat : statuses)
			dbConnector.StoreStatus(stat);
		try {
			Thread.sleep(Utils.getDelay());
		} catch (InterruptedException e) {
		}
		Status statusrt = twitter.createFavorite(twit_id);
		dbConnector.StoreStatus(statusrt);
	}

	/**
	 * Searches twits by condition in job.TContent
	 * 
	 * @return
	 * @throws TwitterException
	 * @throws InterruptedException
	 */
	private void MakeTwitSearch() throws TwitterException, InterruptedException {
		/*
		 * // Moscow double lat = 55.751244; double lon = 37.618423;
		 */
		Query query = new Query(job.GetContentProperty("query"));

		/*
		 * / Добавление Гео try { double lat =
		 * Double.parseDouble(job.GetContentProperty("lat")); double lon =
		 * Double.parseDouble(job.GetContentProperty("lon"));
		 * latestStatus.setLocation(new GeoLocation(lat, lon)); } catch
		 * (Exception e) { }
		 */

		//query.geoCode(new GeoLocation(55.751244, 37.618423), 10.0, "mi");
		QueryResult result = null;
		do {
			result = twitter.search(query);
			List<Status> tweets = result.getTweets();
			for (Status tweet : tweets) {
				System.out.println(tweet.getCreatedAt() + " @"
						+ tweet.getUser().getScreenName() + " - "
						+ tweet.getText());
				dbConnector.StoreStatus(tweet);
			}
			Thread.sleep(5000);
		} while ((query = result.nextQuery()) != null);
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
		// Таким спопсобом передаётся тип прокси в конфигурацию,
		// потому что в конфигурации нет отдельного параметра для этого
		if (dbproxy.getProxyType() == ProxyType.SOCKS)
			ip = Constants.prefixSocks + ip;
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
