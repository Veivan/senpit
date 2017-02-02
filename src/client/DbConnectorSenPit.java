package client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbConnectorSenPit {

	private Connection conn = null;
	private String db_connect_string = ";databaseName=MatrixB;";
	private String db_userid = "sa";
	private String db_password = "123456";

	public DbConnectorSenPit() {
		try {
			this.db_connect_string = utils.ReadConnStrINI()
					+ this.db_connect_string;
		} catch (Exception e) {
			System.out.println("DbConnectorSenPit exception : " + e.getMessage());
		}
	}

	private void dbConnect() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		conn = DriverManager.getConnection(db_connect_string, db_userid,
				db_password);
	}

	/**
	 * Save proxy to DB
	 */
	public void SaveProxy(String IP, int port, int isAlive) {
		try {
			dbConnect();
			String query = "{call [dbo].[spSaveProxy](?,?,?,?,?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setString("ip", IP);
			sp.setInt("port", port);
			sp.setInt("prtypeID", 1);
			sp.setInt("id_cn", 0);
			sp.setInt("alive", isAlive);
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

	/**
	 * Returns proxies from DB
	 */
	public List<String> GetProxsFromDB() {
		List<String> list = new ArrayList<String>();
		try {
			dbConnect();
			String query = "SELECT [prstr] = [ip] + ':' + CAST([port] AS NVARCHAR) FROM [dbo].[mProxies]";
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(rs.getString("prstr"));
			}
			pstmt.close();
			pstmt = null;
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			System.out.println("GetProxsFromDB exception : " + e.getMessage());
		}
		return list;
	}

}
