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
import service.Constants.ProxyType;
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

	private void dbConnect() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		conn = DriverManager.getConnection(db_connect_string, db_userid,
				db_password);
	}

	public List<IAccount> getAccounts() {
		List<IAccount> accounts = new ArrayList<IAccount>();

		/*
		 * / Debug ConcreteAcc acc1 = new ConcreteAcc(1); ConcreteAcc acc2 = new
		 * ConcreteAcc(2); ConcreteAcc acc3 = new ConcreteAcc(3); ConcreteAcc
		 * acc4 = new ConcreteAcc(4); accounts.add(acc1); accounts.add(acc2);
		 * accounts.add(acc3); accounts.add(acc4);
		 */

		// Accounts from DB
		try {
			dbConnect();
			String query = "SELECT [user_id] FROM [dbo].[mAccounts]";
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				ConcreteAcc acc = new ConcreteAcc(rs.getLong(1));
				accounts.add(acc);
			}
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			logger.error("getAccounts exception", e);
			logger.debug("getAccounts exception", e);
		}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error("getAccounts conn.close exception", e);
				logger.debug("getAccounts conn.close exception", e);
			}
		conn = null;
		return accounts;
	}

	// Возвращает назначенный прокси для указанного аккаунта
	public ElementProxy getProxy(long AccID) {
		ElementProxy proxy = null;
		try {
			dbConnect();
			String query = "{call [dbo].[spProxy4AccSelect](?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setLong(1, AccID);
			ResultSet rs = sp.executeQuery();
			// Читаем только первую запись
			rs.next();
			proxy = new ElementProxy(rs.getString(4), rs.getInt(5),
					ProxyType.valueOf(rs.getString(7)));
			rs.close();
			sp.close();
			sp = null;
		} catch (Exception e) {
			logger.error("getProxy spProxy4AccSelect exception", e);
			logger.debug("getProxy spProxy4AccSelect exception", e);
		}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error("getProxy conn.close exception", e);
				logger.debug("getProxy conn.close exception", e);
			}
		return proxy;
	}

	// Возвращает токены для указанного аккаунта
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
			creds = new ElementCredentials(rs.getString(6), rs.getString(7),
					rs.getString(2), rs.getString(3), rs.getString(4),
					rs.getString(5));
			rs.close();
			sp.close();
			sp = null;
		} catch (Exception e) {
			logger.error("getCredentials spCredsSelect exception", e);
			logger.debug("getCredentials spCredsSelect exception", e);
		}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error("getCredentials conn.close exception", e);
				logger.debug("getCredentials conn.close exception", e);
			}
		return creds;
	}

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
			long dt = act.getJob().timestamp == 0l ? System.currentTimeMillis() / 1000  : act.getJob().timestamp;
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

	// Возвращает текущее расписание заданий.
	// Надо сортировать элементы в списках по одному алгоритму для правильного
	// сравнения
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
				JobAtom job = new JobAtom(rs.getLong(1), rs.getString(6),
						rs.getString(4));
				JobAtomList.add(job);
			}
			rs.close();
			sp.close();
			sp = null;
		} catch (Exception e) {
			logger.error("getHomeworks spTasksSelect exception", e);
			logger.debug("getHomeworks spTasksSelect exception", e);
		}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error("getHomeworks conn.close exception", e);
				logger.debug("getHomeworks conn.close exception", e);
			}
		conn = null;

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
