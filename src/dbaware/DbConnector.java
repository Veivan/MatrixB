package dbaware;

import inrtfs.IAccount;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.Constants;
import service.GenderChecker.Gender;
import service.Utils;
import twitter4j.Status;
import twitter4j.User;
import jobs.Homeworks;
import jobs.JobAtom;
import jobs.JobList;
import model.ConcreteAcc;
import model.ElementCredentials;
import model.ElementProxy;
import model.MatrixAct;
import model.TwFriend;

/**
 * Класс для работы с БД. Singleton
 */
public class DbConnector {
	private static volatile DbConnector instance;
	private static String db_connect_string = ";databaseName=MatrixB;";
	private final static String db_userid = "sa";
	private final static String db_password = "123456";

	private Connection conn = null;

	static Logger logger = LoggerFactory.getLogger(DbConnector.class);

	private DbConnector() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			db_connect_string = Utils.ReadConnStrINI() + db_connect_string;
		} catch (Exception e) {
			logger.error("DbConnector exception", e);
		}
	}

	public static DbConnector getInstance() {
		if (instance == null)
			synchronized (DbConnector.class) {
				if (instance == null)
					instance = new DbConnector();
			}
		return instance;
	}

	/**
	 * @return new connection
	 * @throws SQLException
	 */
	private Connection getConnection() throws SQLException {
		//Connection conn = null;
		if (conn == null)
		conn = DriverManager.getConnection(db_connect_string, db_userid,
				db_password);
		return conn;
	}

	public void freeConnection(Connection conn) {
		return;
		/*if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("freeConnection exception", e);
			}
			conn = null;
		}*/
	}

	/**
	 * Returns enabled Accounts from DB
	 */
	public List<IAccount> getAccounts() {
		return getAccounts(null, true);
	}

	public List<IAccount> getAccounts(Integer group_id, Boolean enabled) {
		List<IAccount> accounts = new ArrayList<IAccount>();
		try {
			Connection conn = getConnection();
			// String query =
			// "SELECT [user_id] FROM [dbo].[mAccounts] WHERE [enabled] = 1";
			String query = "{call [dbo].[spAccountsSelect](?,?)}";
			CallableStatement sp = conn.prepareCall(query);
			if (group_id == null)
				sp.setNull("group_id", java.sql.Types.INTEGER);
			else
				sp.setInt("group_id", group_id);
			if (enabled == null)
				sp.setNull("enabled", java.sql.Types.BIT);
			else
				sp.setBoolean("enabled", enabled);
			ResultSet rs = sp.executeQuery();
			while (rs.next()) {
				ConcreteAcc acc = new ConcreteAcc(rs.getLong("user_id"));
				accounts.add(acc);
			}
			rs.close();
			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("getAccounts exception", e);
		}
		return accounts;
	}

	/**
	 * Возвращает случайную картинку из БД
	 */
	public byte[] getRandomPicture(Gender gender, int ptype_id) {
		byte[] bytes = null;
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[spGetRandomImage](?,?,?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.registerOutParameter(3, java.sql.Types.BLOB);
			if (gender == Gender.NEUTRAL)
				sp.setNull("gender", java.sql.Types.BIT);
			else
				sp.setBoolean("gender", gender.ordinal() != 0);
			sp.setInt("ptype_id", ptype_id);

			sp.executeUpdate();
			Blob pic = sp.getBlob("pic");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			InputStream in = pic.getBinaryStream();
			int n = 0;
			while ((n = in.read(buf)) >= 0) {
				baos.write(buf, 0, n);
			}
			in.close();
			bytes = baos.toByteArray();
			baos.close();
			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("getRandomPicture exception", e);
		}
		return bytes;
	}

	/**
	 * Returns Single Account from DB
	 */
	public IAccount getAccount(Long user_id) {
		ConcreteAcc acc = null;
		try {
			Connection conn = getConnection();
			String query = "SELECT TOP 1 [name],[screen_name],[email],[phone],[pass],[twitter_id] = ISNULL([twitter_id], -1) "
					+ ",[mailpass], [gender] = ISNULL([gender], 2) FROM [dbo].[mAccounts] WHERE [user_id] = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, user_id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				acc = new ConcreteAcc(user_id, rs.getString("email"),
						rs.getString("pass"), rs.getString("screen_name"),
						rs.getString("phone"), rs.getString("mailpass"));
				acc.setName(rs.getString("name"));
				acc.setTwitter_id(rs.getLong("twitter_id"));
				acc.setGender(Gender.values()[rs.getInt("gender")]);
			}
			pstmt.close();
			pstmt = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("getAccounts exception", e);
		}
		return acc;
	}

	/**
	 * Сохраняет данные аккаунта в БД
	 */
	public long SaveAcc2Db(ConcreteAcc acc, int group_id) {
		long user_id = -1;
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[spAccountAdd](?,?,?,?,?,?,?,?,?,?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.registerOutParameter(1, java.sql.Types.BIGINT);

			sp.setLong(1, acc.getAccID());
			sp.setString(2, acc.getName());
			sp.setString(3, acc.getScreenname());
			sp.setString(4, acc.getEmail());
			sp.setString(5, acc.getPhone());
			sp.setString(6, acc.getPass());
			sp.setLong(7, acc.getTwitter_id());
			sp.setInt(8, group_id);
			sp.setString(9, acc.getMailpass());
			int genderint = acc.getGender().ordinal();
			if (genderint == 2)
				sp.setNull(10, java.sql.Types.INTEGER);
			else
				sp.setInt(10, genderint);

			sp.executeUpdate();
			user_id = sp.getLong(1);

			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			DbConnector.logger.error("SaveAcc2Db exception", e);
		}

		return user_id;
	}

	/**
	 * Сохраняет расширенные данные аккаунта в БД
	 */
	public void SaveAccExtended(Long user_id, User user) {
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[spAccountUpdExt](?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			CallableStatement sp = conn.prepareCall(query);

			sp.setLong("user_id", user_id);
			sp.setLong("twitter_id", user.getId());
			sp.setString("location", user.getLocation());
			sp.setInt("followers_count", user.getFollowersCount());
			sp.setInt("friends_count", user.getFriendsCount());
			sp.setInt("listed_count", user.getListedCount());
			sp.setInt("statuses_count", user.getStatusesCount());
			sp.setString("url", user.getURL());
			sp.setString("description", user.getDescription());
			sp.setObject("created_at",
					Utils.getDateTimeOffset(user.getCreatedAt()),
					microsoft.sql.Types.DATETIMEOFFSET);
			sp.setInt("utc_offset", user.getUtcOffset());
			sp.setString("time_zone", user.getTimeZone());
			sp.setString("lang", user.getLang());
			sp.setBoolean("geo_enabled", user.isGeoEnabled());

			Status st = user.getStatus();
			if (st != null)
				sp.setObject("lasttweet_at",
						Utils.getDateTimeOffset(st.getCreatedAt()),
						microsoft.sql.Types.DATETIMEOFFSET);
			else
				sp.setNull("lasttweet_at", microsoft.sql.Types.DATETIMEOFFSET);

			sp.setBoolean("default_profile", user.isDefaultProfile());
			sp.setBoolean("default_profile_image", user.isDefaultProfileImage());
			sp.setBoolean("verified", user.isVerified());

			sp.execute();
			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			DbConnector.logger.error("SaveAccExtended exception", e);
		}

	}

	/**
	 * Returns free proxies from DB
	 */
	public List<ElementProxy> getFreeProxies(long AccID) {
		List<ElementProxy> proxylist = new ArrayList<ElementProxy>();
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[spProxyFreeSelect](?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setLong("user_id", AccID);
			ResultSet rs = sp.executeQuery();
			while (rs.next()) {
				ElementProxy proxy = new ElementProxy(rs.getString("ip"),
						rs.getInt("port"), Constants.ProxyType.valueOf(rs
								.getString("typename")), rs.getLong("ProxyID"));
				proxylist.add(proxy);
			}
			rs.close();
			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("getFreeProxies exception", e);
		}
		return proxylist;
	}

	/**
	 * Set proxy "alive"
	 */
	public void setProxyIsAlive(long ProxyID, boolean IsAlive) {
		try {
			Connection conn = getConnection();
			String query = "UPDATE [dbo].[mProxies] SET [alive] = ? WHERE [ProxyID] = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, IsAlive ? 1 : 0);
			pstmt.setLong(2, ProxyID);
			pstmt.execute();
			pstmt.close();
			pstmt = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("setProxyIsAlive exception", e);
		}
	}

	/**
	 * Set proxy "blocked"
	 */
	public void setProxyIsBlocked(long ProxyID, boolean Isblocked) {
		try {
			Connection conn = getConnection();
			String query = "UPDATE [dbo].[mProxies] SET [blocked] = ? WHERE [ProxyID] = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, Isblocked ? 1 : 0);
			pstmt.setLong(2, ProxyID);
			pstmt.execute();
			pstmt.close();
			pstmt = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("setProxyIsBlocked exception", e);
		}
	}

	/**
	 * Освобождает прокси от указанного аккаунта
	 */
	public void makeProxy4AccFree(long accID) {
		long ProxyID = -1;
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[spProxy4AccUpdate](?,?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setLong(1, accID);
			sp.setLong(2, ProxyID);
			sp.execute();
			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("makeProxy4AccFree exception", e);
		}
	}

	/**
	 * Устанавливает прокси для указанного аккаунта
	 */
	public void setProxy4Acc(long accID, ElementProxy accproxy) {
		long ProxyID = (accproxy == null) ? 0 : accproxy.getProxyID();
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[spProxy4AccUpdate](?,?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setLong(1, accID);
			sp.setLong(2, ProxyID);
			sp.execute();
			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("setProxy4Acc exception", e);
		}
	}

	/**
	 * Возвращает назначенный прокси для указанного аккаунта
	 */
	public ElementProxy getProxy4Acc(long AccID) {
		ElementProxy proxy = null;
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[spProxy4AccSelect](?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setLong(1, AccID);
			ResultSet rs = sp.executeQuery();
			// Читаем только первую запись
			if (rs.next())
				proxy = new ElementProxy(rs.getString("ip"), rs.getInt("port"),
						Constants.ProxyType.valueOf(rs.getString("typename")),
						rs.getLong("ProxyID"));
			rs.close();
			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("getProxy4Acc exception", e);
		}
		return proxy;
	}

	/**
	 * Возвращает токены для указанного аккаунта
	 */
	public ElementCredentials getCredentials(long AccID) {
		ElementCredentials creds = null;
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[spCredsSelect](?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setLong(1, AccID);
			ResultSet rs = sp.executeQuery();
			// Читаем только первую запись
			rs.next();
			creds = new ElementCredentials(rs.getString("cons_key"),
					rs.getString("cons_secret"), rs.getString("screen_name"),
					rs.getString("pass"), rs.getString("token"),
					rs.getString("token_secret"), rs.getInt("user_id"),
					rs.getInt("id_app"));
			rs.close();
			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("getCredentials exception", e);
		}
		return creds;
	}

	/**
	 * Сохраняет токены для указанного аккаунта
	 */
	public void SaveToken(long accID, long id_app, String token,
			String tokenSecret) {
		try {
			Connection conn = getConnection();
			String query = "INSERT INTO [dbo].[mTokens] ([user_id],[id_app],[token],[token_secret]) VALUES (?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, accID);
			pstmt.setLong(2, id_app);
			pstmt.setString(3, token);
			pstmt.setString(4, tokenSecret);
			pstmt.execute();
			pstmt.close();
			pstmt = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("SaveToken failed", e);
		}
	}

	/**
	 * Сохраняет результат выполнения задания
	 */
	public void StoreActResult(MatrixAct act, boolean result, String failreason) {
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[spExecutionInsert](?,?,?,?,?,?)}";
			CallableStatement sp = conn.prepareCall(query);

			sp.setLong("user_id", act.getAcc().getAccID());
			sp.setLong("id_task", act.getJob().JobID);
			sp.setLong("act_id", act.getSelfID());
			sp.setBoolean("result", result);
			sp.setString("failreason", failreason);
			long dt = act.getJob().timestamp == 0l ? System.currentTimeMillis() / 1000
					: act.getJob().timestamp / 1000;
			sp.setLong("execdate", dt);

			sp.execute();
			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("StoreActResult failed", e);
		}
	}

	/**
	 * Возвращает текущее расписание заданий. Надо сортировать элементы в
	 * списках по одному алгоритму для правильного сравнения
	 */
	public Homeworks getHomeworks(long moment) {

		List<JobAtom> JobAtomList = new ArrayList<JobAtom>();

		Date now = new Date(moment);
		// Tasks from DB
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[spTasksSelect](?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setDate(1, now);
			ResultSet rs = sp.executeQuery();
			while (rs.next()) {
				JobAtom job = new JobAtom(rs.getLong("id_Task"),
						rs.getString("TypeMean"), rs.getString("TContent"));
				job.group_id = rs.getInt("group_id");
				JobAtomList.add(job);
			}
			rs.close();
			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("getHomeworks spTasksSelect exception", e);
		}

		Homeworks newschedule = new Homeworks();
		MakeHowmworks(newschedule, JobAtomList);
		// MakeHowmworks(newschedule);
		return newschedule;
	}

	/**
	 * Set acc "enabled"
	 */
	public void setAccIsEnabled(long accID, boolean IsEnabled) {
		try {
			Connection conn = getConnection();
			String query = "UPDATE [dbo].[mAccounts] SET [enabled] = ? WHERE [user_id] = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, IsEnabled ? 1 : 0);
			pstmt.setLong(2, accID);
			pstmt.execute();
			pstmt.close();
			pstmt = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("setAccIsEnabled exception", e);
		}
	}

	/**
	 * Save Status in DB
	 */
	public void StoreStatus(Status status) {
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[spStatusAdd](?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			CallableStatement sp = conn.prepareCall(query);

			sp.setLong("tw_id", status.getId());
			sp.setString("status", status.toString());
			sp.setLong("creator_id", status.getUser().getId());
			sp.setObject("created_at",
					Utils.getDateTimeOffset(status.getCreatedAt()),
					microsoft.sql.Types.DATETIMEOFFSET);
			sp.setInt("favorite_count", status.getFavoriteCount());
			sp.setString("in_reply_to_screen_name",
					status.getInReplyToScreenName());
			sp.setLong("in_reply_to_status_id", status.getInReplyToStatusId());
			sp.setLong("in_reply_to_user_id", status.getInReplyToUserId());
			sp.setString("lang", status.getLang());
			sp.setInt("retweet_count", status.getRetweetCount());
			sp.setString("text", status.getText());
			sp.setNull("place_json", java.sql.Types.NVARCHAR);
			sp.setNull("coordinates_json", java.sql.Types.NVARCHAR);
			sp.setBoolean("favorited", status.isFavorited());
			sp.setBoolean("retweeted", status.isRetweeted());
			sp.setBoolean("isRetweet", status.isRetweet());

			long retweeted_id = -1L;
			if (status.isRetweet()) {
				retweeted_id = status.getRetweetedStatus().getId();
			}

			if (retweeted_id > 0)
				sp.setLong("retweeted_id", retweeted_id);
			else
				sp.setNull("retweeted_id", java.sql.Types.BIGINT);

			sp.execute();
			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			DbConnector.logger.error("StoreStatus exception", e);
		}
	}

	/**
	 * Check if Status retweeted by user
	 */
	public boolean isRetweetedByUser(long statusId, long accID) {
		boolean result = false;
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[isRetweetedByUser](?,?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setLong("tw_id", statusId);
			sp.setLong("user_id", accID);
			ResultSet rs = sp.executeQuery();
			if (rs.next())
				result = rs.getBoolean("result");
			rs.close();
			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("isRetweetedByUser exception", e);
		}
		return result;
	}

	/**
	 * Save Timing in DB
	 */
	public void StoreTiming(long accID, ArrayList<JobAtom> timing) {
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[spTimingAdd](?,?)}";
			CallableStatement sp = conn.prepareCall(query);

			sp.registerOutParameter("tmng_id", java.sql.Types.BIGINT);
			sp.setLong("user_id", accID);
			sp.setNull("tmng_id", java.sql.Types.BIGINT);
			sp.executeUpdate();
			long tmng_id = sp.getLong("tmng_id");
			sp.close();

			query = "{call [dbo].[spTimingRecordAdd](?,?,?)}";

			for (JobAtom job : timing) {
				sp = conn.prepareCall(query);
				sp.setLong("tmng_id", tmng_id);
				sp.setString("TaskType", job.Type.toString());
				sp.setLong("tstamp", job.timestamp / 1000);
				sp.execute();
				sp.close();
			}

			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			DbConnector.logger.error("StoreTiming exception", e);
		}
	}

	/**
	 * Get random ScreenName ant twitter_id to follow it
	 */
	public TwFriend GetRandomScreenName(long accID) {
		TwFriend friend = null;
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[spGetRandomScreenName](?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setLong("user_id", accID);
			ResultSet rs = sp.executeQuery();
			if (rs.next())
				friend = new TwFriend(rs.getString("screen_name"),
						rs.getLong("twitter_id"));
			rs.close();
			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			DbConnector.logger.error("GetRandomScreenName exception", e);
		}
		return friend;
	}

	/**
	 * Save single Follow Info in DB
	 */
	public void StoreFollowInfo(long accID, long twitter_id, Boolean fwtype) {
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[spFollowInfoUpd](?,?,?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setLong("user_id", accID);
			sp.setLong("twitter_id", twitter_id);
			if (fwtype == null)
				sp.setNull("fwtype", java.sql.Types.BIT);
			else
				sp.setBoolean("fwtype", fwtype);
			sp.execute();
			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			DbConnector.logger.error("StoreFollowInfo exception", e);
		}
	}

	/**
	 * Returns executed tasks info from DB
	 */
	public List<Long> getExecutionInfo(long accID, long moment) {
		Date now = new Date(moment);
		List<Long> listIds = new ArrayList<Long>();
		try {
			Connection conn = getConnection();
			String query = "{call [dbo].[spExecutionSelect](?,?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setLong("user_id", accID);
			sp.setDate("tdate", now);
			ResultSet rs = sp.executeQuery();
			while (rs.next()) {
				listIds.add(rs.getLong("id_task"));
			}
			rs.close();
			sp.close();
			sp = null;
			freeConnection(conn);
		} catch (Exception e) {
			logger.error("getExecutionInfo exception", e);
		}
		return listIds;
	}

	private static void MakeHowmworks(Homeworks homeworks,
			List<JobAtom> JobAtomList) {
		for (JobAtom job : JobAtomList) {
			JobAtom jobcopy = new JobAtom(job);
			homeworks.AddJob(jobcopy);
		}
	}

	// 4 debug without DB
	private static void MakeHowmworks(Homeworks homeworks) {
		JobAtom job = new JobAtom(5L, "SETAVA", "");
		homeworks.AddJob(job);

		for (int i = 0; i < 2; i++) {
			job = new JobAtom((long) i, "LIKE", "");
			homeworks.AddJob(job);
		}

		homeworks.First();
		for (JobList jobList : homeworks) {
			jobList.First();
			logger.debug("joblist : {} {}", jobList.getType(),
					jobList.getPriority());
		}
	}

}
