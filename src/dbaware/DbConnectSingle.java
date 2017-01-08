package dbaware;

import inrtfs.IAccount;

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
import jobs.Homeworks;
import jobs.JobAtom;
import jobs.JobList;
import main.ConcreteAcc;
import model.ElementCredentials;
import model.ElementProxy;
import model.MatrixAct;

public class DbConnectSingle {
	private static volatile DbConnectSingle instance;

	static Logger logger = LoggerFactory.getLogger(DbConnectSingle.class);

	private DbConnectSingle() {
	}

	private Connection conn = null;
	private String db_connect_string =
	// local
	"jdbc:sqlserver://KONSTANTIN-PC;instanceName=SQLEXPRESS14"
	// "jdbc:sqlserver://WIN-2TFLS2PJ38K;instanceName=MSSQL2008R2"
	// AWS
	// "jdbc:sqlserver://WIN-2B897RSG769;instanceName=SQLEXPRESS2014"
	// office
	// "jdbc:sqlserver://014-MSDN;instanceName=SQL12"
			+ ";databaseName=MatrixB;";
	
	/**
	 * @return the conn
	 */
	public Connection getConn() {
		return conn;
	}

	private String db_userid = "sa";
	private String db_password = "123456";

	public static DbConnectSingle getInstance() {
		if (instance == null) {
			synchronized (DbConnectSingle.class) {
				if (instance == null) {
					instance = new DbConnectSingle();
				}
			}
		}
		return instance;
	}

	void dbConnect() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		conn = DriverManager.getConnection(db_connect_string, db_userid,
				db_password);
	}

	/**
	 * Returns Accounts from DB
	 */
	public List<IAccount> getAccounts() {
		List<IAccount> accounts = new ArrayList<IAccount>();

		/*
		 * / Debug ConcreteAcc acc1 = new ConcreteAcc(1); ConcreteAcc acc2 = new
		 * ConcreteAcc(2); ConcreteAcc acc3 = new ConcreteAcc(3); ConcreteAcc
		 * acc4 = new ConcreteAcc(4); accounts.add(acc1); accounts.add(acc2);
		 * accounts.add(acc3); accounts.add(acc4);
		 */

		try {
			dbConnect();
			String query = "SELECT [user_id] FROM [dbo].[mAccounts]";
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
			logger.debug("getAccounts exception", e);
		}
		return accounts;
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
			logger.debug("getFreeProxies spProxyFreeSelect exception", e);
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
			logger.debug("setProxyIsAlive exception", e);
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
			logger.debug("setProxyIsBlocked exception", e);
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
			logger.debug("setProxy4Acc spProxy4AccUpdate exception", e);
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
			logger.debug("getProxy spProxy4AccSelect exception", e);
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
			creds = new ElementCredentials(rs.getString("cons_key"), rs.getString("cons_secret"),
					rs.getString("name"), rs.getString("pass"), rs.getString("token"),
					rs.getString("token_secret"), rs.getInt("user_id"), rs.getInt("id_app"));
			rs.close();
			sp.close();
			sp = null;
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			logger.error("getCredentials spCredsSelect exception", e);
			logger.debug("getCredentials spCredsSelect exception", e);
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
			logger.debug("StoreActResult failed", e);
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
			logger.debug("StoreActResult failed", e);
		}
	}

	/**
	 * Возвращает текущее расписание заданий. Надо сортировать элементы в
	 * списках по одному алгоритму для правильного сравнения
	 */
	public Homeworks getHomeworks() {

		List<JobAtom> JobAtomList = new ArrayList<JobAtom>();

		Date moment = new Date(System.currentTimeMillis());
		// Tasks from DB
		try {
			dbConnect();
			String query = "{call [dbo].[spTasksSelect](?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setDate(1, moment);
			ResultSet rs = sp.executeQuery();
			while (rs.next()) {
				JobAtom job = new JobAtom(rs.getLong("id_Task"), rs.getString("TypeMean"),
						rs.getString("TContent"));
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
			logger.debug("getHomeworks spTasksSelect exception", e);
		}

		Homeworks newschedule = new Homeworks();
		MakeHowmworks(newschedule, JobAtomList);
		// MakeHowmworks(newschedule);
		return newschedule;
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
