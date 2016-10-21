package dbaware;

import inrtfs.IAccount;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import service.Constants;
import jobs.Homeworks;
import jobs.JobAtom;
import jobs.JobList;
import main.ConcreteAcc;

public class DbConnectSingle {
	private static volatile DbConnectSingle instance;

	private DbConnectSingle() {
	}

	private Connection conn = null;
	private String db_connect_string =
	// local
	// "jdbc:sqlserver://WIN-2TFLS2PJ38K;instanceName=MSSQL2008R2"
	// AWS
	// "jdbc:sqlserver://WIN-VTEXJXYLHHY;instanceName=SQLEXPRESS"
	// office
	"jdbc:sqlserver://014-MSDN;instanceName=SQL12" + ";databaseName=MatrixB;";
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
		accounts.add(acc1);
		accounts.add(acc2);

		/*/ Accounts from DB
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
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		conn = null; */
		
		return accounts;
	}
	
	// Возвращает текущее расписание заданий.
	// Надо сортировать элементы в списках по одному алгоритму для правильного сравнения
	public Homeworks getHomeworks() {
		Homeworks newschedule = new Homeworks();
		MakeHowmworks(newschedule);
		return newschedule;
	}

	private static void MakeHowmworks(Homeworks howmworks) {

		JobList ReTwitList = new JobList(Constants.ReTwit,
				Constants.JobType.ReTwit);
		JobList TwitList = new JobList(Constants.Twit,
				Constants.JobType.Twit);
		JobList SetAvaList = new JobList(Constants.SetAva,
				Constants.JobType.SetAva);

		for (int i = 0; i < 50; i++) {
			JobAtom job = new JobAtom(i, Constants.JobType.Like);
			TwitList.AddJob(job);
		}

		howmworks.AddList(ReTwitList);
		howmworks.AddList(TwitList);
		howmworks.AddList(SetAvaList);
	}

}
