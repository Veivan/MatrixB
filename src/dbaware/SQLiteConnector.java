package dbaware;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

import model.Regimen;

public class SQLiteConnector {
	private static volatile SQLiteConnector instance;
	private static String dbname = "test.db";
	// SQLite connection string
	private static String url = "jdbc:sqlite:" + dbname;

	public static SQLiteConnector getInstance() {
		if (instance == null)
			synchronized (SQLiteConnector.class) {
				if (instance == null)
					instance = new SQLiteConnector();
			}
		return instance;
	}

	/**
	 * Connect to the test.db database
	 *
	 * @return the Connection object
	 * @throws Exception
	 */
	private static Connection connect() throws SQLException {
		Connection conn = DriverManager.getConnection(url);

		// SQL statement for creating a new table
		String sql = "CREATE TABLE IF NOT EXISTS groupregim (\n"
				+ "	groupid integer,\n" + "	WakeHour integer,\n"
				+ "	BedHour integer\n" + ");";

		try (Statement stmt = conn.createStatement()) {
			// create a new table
			stmt.execute(sql);
		} catch (SQLException e) {
			throw e;
		}

		return conn;
	}

	/**
	 * Insert a new row into the groupregim table
	 *
	 * @param groupid
	 * @param WakeHour
	 * @param BedHour
	 */
	public void insert(int groupid, int WakeHour, int BedHour) {
		String sql = "INSERT INTO groupregim(groupid,WakeHour,BedHour) VALUES(?,?,?)";
		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, groupid);
			pstmt.setInt(2, WakeHour);
			pstmt.setInt(3, BedHour);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * select all rows in the groupregim table
	 */
	public void selectAll() {
		String sql = "SELECT groupid,WakeHour,BedHour FROM groupregim";

		try (Connection conn = connect();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql)) {

			// loop through the result set
			while (rs.next()) {
				System.out.println(rs.getInt("groupid") + "\t"
						+ rs.getInt("WakeHour") + "\t" + rs.getInt("BedHour"));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * select Regim by group ID
	 */
	public Regimen selectRegimByGroupID(int groupid) {
		String sql = "SELECT groupid, WakeHour, BedHour FROM groupregim WHERE groupid = ?";
		Regimen regim = null;
		try (Connection conn = connect();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, groupid);
			ResultSet rs = pstmt.executeQuery();
			// loop through the result set
			while (rs.next()) {				
				regim = new Regimen(rs.getInt("WakeHour"), rs.getInt("BedHour"));
				/*System.out.println(rs.getInt("groupid") + "\t"
						+ rs.getInt("WakeHour") + "\t" + rs.getInt("BedHour")); */
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return regim;
	}

	public static void main(String[] args) {
		SQLiteConnector dbConnector = SQLiteConnector.getInstance();
		dbConnector.insert(5, 3, 7);
		dbConnector.insert(9, 7, 11);
		dbConnector.insert(10, 11, 15);
		dbConnector.insert(11, 15, 19);
		dbConnector.insert(12, 19, 23);
		dbConnector.selectAll();
		//dbConnector.selectRegimByGroupID(2);
	}

}
