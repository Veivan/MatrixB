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
import jobs.Homeworks;
import jobs.JobAtom;
import jobs.JobList;
import model.ConcreteAcc;
import model.ElementCredentials;
import model.ElementProxy;
import model.MatrixAct;

public class DbConnector {

	private Connection conn = null;
	private String db_connect_string = ";databaseName=MatrixB;";

	static Logger logger = LoggerFactory.getLogger(DbConnector.class);

	public DbConnector() {
		try {
			this.db_connect_string = Utils.ReadConnStrINI()
					+ this.db_connect_string;
		} catch (Exception e) {
			logger.error("DbConnector exception", e);
		}
	}

	/**
	 * @return the conn
	 */
	public Connection getConn() {
		return conn;
	}

	private String db_userid = "sa";
	private String db_password = "123456";

	private void dbConnect() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		conn = DriverManager.getConnection(db_connect_string, db_userid,
				db_password);
	}

	/**
	 * Returns enabled Accounts from DB
	 */
	public List<IAccount> getAccounts() {
		List<IAccount> accounts = new ArrayList<IAccount>();
		try {
			dbConnect();
			String query = "SELECT [user_id] FROM [dbo].[mAccounts] WHERE [enabled] = 1";
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				ConcreteAcc acc = new ConcreteAcc(rs.getLong("user_id"));
				accounts.add(acc);
			}
			pstmt.close();
			pstmt = null;
			if (conn != null)
				conn.close();
			conn = null;
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
			dbConnect();
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
			if (conn != null)
				conn.close();
			conn = null;
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
			dbConnect();
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
			if (conn != null)
				conn.close();
			conn = null;
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
			dbConnect();
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
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			DbConnector.logger.error("SaveAcc2Db exception", e);
		}

		return user_id;
	}

	/**
	 * Returns free proxies from DB
	 */
	public List<ElementProxy> getFreeProxies() {
		List<ElementProxy> proxylist = new ArrayList<ElementProxy>();
		try {
			dbConnect();
			String query = "{call [dbo].[spProxyFreeSelect]}";
			CallableStatement sp = conn.prepareCall(query);
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
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			logger.error("getFreeProxies spProxyFreeSelect exception", e);
		}
		return proxylist;
	}

	/**
	 * Set proxy "alive"
	 */
	public void setProxyIsAlive(long ProxyID, boolean IsAlive) {
		try {
			dbConnect();
			String query = "UPDATE [dbo].[mProxies] SET [alive] = ? WHERE [ProxyID] = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, IsAlive ? 1 : 0);
			pstmt.setLong(2, ProxyID);
			pstmt.execute();
			pstmt.close();
			pstmt = null;
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			logger.error("setProxyIsAlive exception", e);
		}
	}

	/**
	 * Set proxy "blocked"
	 */
	public void setProxyIsBlocked(long ProxyID, boolean Isblocked) {
		try {
			dbConnect();
			String query = "UPDATE [dbo].[mProxies] SET [blocked] = ? WHERE [ProxyID] = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, Isblocked ? 1 : 0);
			pstmt.setLong(2, ProxyID);
			pstmt.execute();
			pstmt.close();
			pstmt = null;
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			logger.error("setProxyIsBlocked exception", e);
		}
	}

	/**
	 * Устанавливает прокси для указанного аккаунта
	 */
	public void setProxy4Acc(long accID, ElementProxy accproxy) {
		long ProxyID = (accproxy == null) ? 0 : accproxy.getProxyID();
		try {
			dbConnect();
			String query = "{call [dbo].[spProxy4AccUpdate](?,?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setLong(1, accID);
			sp.setLong(2, ProxyID);
			sp.execute();
			sp.close();
			sp = null;
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			logger.error("setProxy4Acc spProxy4AccUpdate exception", e);
		}
	}

	/**
	 * Возвращает назначенный прокси для указанного аккаунта
	 */
	public ElementProxy getProxy4Acc(long AccID) {
		ElementProxy proxy = null;
		try {
			dbConnect();
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
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			logger.error("getProxy spProxy4AccSelect exception", e);
		}
		return proxy;
	}

	/**
	 * Возвращает токены для указанного аккаунта
	 */
	public ElementCredentials getCredentials(long AccID) {
		ElementCredentials creds = null;
		try {
			dbConnect();
			String query = "{call [dbo].[spCredsSelect](?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setLong(1, AccID);
			ResultSet rs = sp.executeQuery();
			// Читаем только первую запись
			rs.next();
			creds = new ElementCredentials(rs.getString("cons_key"),
					rs.getString("cons_secret"), rs.getString("name"),
					rs.getString("pass"), rs.getString("token"),
					rs.getString("token_secret"), rs.getInt("user_id"),
					rs.getInt("id_app"));
			rs.close();
			sp.close();
			sp = null;
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			logger.error("getCredentials spCredsSelect exception", e);
		}
		return creds;
	}

	/**
	 * Сохраняет токены для указанного аккаунта
	 */
	public void SaveToken(long accID, long id_app, String token,
			String tokenSecret) {
		try {
			dbConnect();
			String query = "INSERT INTO [dbo].[mTokens] ([user_id],[id_app],[token],[token_secret]) VALUES (?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, accID);
			pstmt.setLong(2, id_app);
			pstmt.setString(3, token);
			pstmt.setString(4, tokenSecret);
			pstmt.execute();
			pstmt.close();
			pstmt = null;
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			logger.error("StoreActResult failed", e);
		}
	}

	/**
	 * Сохраняет результат выполнения задания
	 */
	public void StoreActResult(MatrixAct act, boolean result, String failreason) {
		try {
			dbConnect();
			String query = "INSERT INTO [dbo].[mExecution] ([user_id],[id_task],[act_id],[result],[failreason],[execdate]) VALUES (?,?,?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, act.getAcc().getAccID());
			pstmt.setLong(2, act.getJob().JobID);
			pstmt.setLong(3, act.getSelfID());
			pstmt.setBoolean(4, result);
			pstmt.setString(5, failreason);
			long dt = act.getJob().timestamp == 0l ? System.currentTimeMillis() / 1000
					: act.getJob().timestamp;
			pstmt.setLong(6, dt);
			pstmt.execute();
			pstmt.close();
			pstmt = null;
			if (conn != null)
				conn.close();
			conn = null;
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
			dbConnect();
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
			if (conn != null)
				conn.close();
			conn = null;
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
			dbConnect();
			String query = "UPDATE [dbo].[mAccounts] SET [enabled] = ? WHERE [user_id] = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, IsEnabled ? 1 : 0);
			pstmt.setLong(2, accID);
			pstmt.execute();
			pstmt.close();
			pstmt = null;
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			logger.error("setAccIsEnabled exception", e);
		}
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
