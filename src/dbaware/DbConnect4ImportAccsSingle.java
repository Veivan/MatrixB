package dbaware;

import java.sql.CallableStatement;
import java.sql.Connection;

import model.AccIdent;
import model.ElementProxy;

/**
 * Использует композицию - включает DbConnectSingle.
 * Здесь размещены методы, используемые только для импорта акков
 */
public class DbConnect4ImportAccsSingle {
	private DbConnectSingle dbConnector;
	private Connection conn = null;
	
	public DbConnect4ImportAccsSingle()
	{
		dbConnector = DbConnectSingle.getInstance();  			
	}
	
	/**
	 * Сохраняет данные аккаунта в БД
	 */
	public long SaveAcc2Db(AccIdent acc, int group_id) {
		long user_id = -1;
		try {
			dbConnector.dbConnect();
			conn = dbConnector.getConn();
			String query = "{call [dbo].[spAccountAdd](?,?,?,?,?,?,?,?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.registerOutParameter(1, java.sql.Types.BIGINT);

			sp.setLong(1, acc.getUser_id());
			sp.setString(2, acc.getName());
			sp.setString(3, null);
			sp.setString(4, acc.getEmail());
			sp.setString(5, null);
			sp.setString(6, acc.getPass());
			sp.setLong(7, -1);
			sp.setInt(8, group_id);
				
			sp.executeUpdate();
			user_id = sp.getLong(1);

			sp.close();
			sp = null;
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			DbConnectSingle.logger.error("setProxy4Acc spProxy4AccUpdate exception", e);
			DbConnectSingle.logger.debug("setProxy4Acc spProxy4AccUpdate exception", e);
		} 
		
		return user_id;
	}


}
