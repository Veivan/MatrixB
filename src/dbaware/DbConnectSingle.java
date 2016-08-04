package dbaware;

import inrtfs.IAccount;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
		List<IAccount> AccList = new ArrayList<IAccount>();

		try {
			dbConnect();
			String query = "SELECT [user_id] FROM [dbo].[TwAccounts]";
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				ConcreteAcc acc = new ConcreteAcc(rs.getLong(1));
				AccList.add(acc);
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
		conn = null;
		return AccList;
	}
}
