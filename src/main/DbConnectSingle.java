package main;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectSingle {

	private static volatile DbConnectSingle instance;

	private DbConnectSingle() {
	}

	private Connection conn = null;
	private String db_connect_string =
	// local
	//"jdbc:sqlserver://KONSTANTIN-PC;instanceName=SQLEXPRESS14"
	// "jdbc:sqlserver://WIN-2TFLS2PJ38K;instanceName=MSSQL2008R2"
	// AWS
	// "jdbc:sqlserver://WIN-2B897RSG769;instanceName=SQLEXPRESS2014"
	// office
	 "jdbc:sqlserver://014-MSDN;instanceName=SQL12"
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

	/**
	 * Returns Accounts from DB
	 */
	public void SaveProxy(String IP, int port) {
		try {
			dbConnect();
			String query = "{call [dbo].[spSaveProxy](?,?,?,?,?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setString(1, IP);
			sp.setInt(2, port);
			sp.setInt(3, 1);
			sp.setInt(4, 0);
			sp.setInt(5, 1);
			sp.execute();
			sp.close();
			sp = null;
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			System.out.println("SaveProxy exception : " + e.getMessage());
		}
	}

}
