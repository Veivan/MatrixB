package dbaware;

import inrtfs.IAccount;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
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
	// "jdbc:sqlserver://WIN-VTEXJXYLHHY;instanceName=SQLEXPRESS"
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

		// Debug
		ConcreteAcc acc1 = new ConcreteAcc(1);
		ConcreteAcc acc2 = new ConcreteAcc(2);
		ConcreteAcc acc3 = new ConcreteAcc(3);
		ConcreteAcc acc4 = new ConcreteAcc(4);
		accounts.add(acc1);
		accounts.add(acc2);
		accounts.add(acc3);
		accounts.add(acc4);

		/*
		 * / Accounts from DB try { dbConnect(); String query =
		 * "SELECT [user_id] FROM [dbo].[mAccounts]"; PreparedStatement pstmt =
		 * conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery();
		 * 
		 * while (rs.next()) { ConcreteAcc acc = new ConcreteAcc(rs.getLong(1));
		 * accounts.add(acc); } pstmt.close(); pstmt = null; if (conn != null)
		 * conn.close(); conn = null; } catch (Exception e) {
		 * e.printStackTrace(); } if (conn != null) try { conn.close(); } catch
		 * (SQLException e) { e.printStackTrace(); } conn = null;
		 */

		return accounts;
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
		JobAtom job = new JobAtom(5, Constants.JobType.SETAVA);
		homeworks.AddJob(job);

		for (int i = 0; i < 2; i++) {
			job = new JobAtom(i, Constants.JobType.LIKE);
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
