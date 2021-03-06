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
	public void SaveProxy(String IP, int port, String proxyType, int isAlive) {
		try {
			dbConnect();
			String query = "{call [dbo].[spSaveProxy](?,?,?,?,?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setString("ip", IP);
			sp.setInt("port", port);
			sp.setInt("prtypeID", proxyType == "HTTP" ? 1 : 2);
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
			String query = "SELECT [prstr] = [ip] + ':' + CAST([port] AS NVARCHAR) + ':' + " +
					"LTRIM(RTRIM(D.[typename])) FROM [dbo].[mProxies] P " +
					" JOIN [dbo].[DicProxyType] D ON D.[prtypeID] = P.[prtypeID]";
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

	/**
	 * Returns proxies count from DB
	 */
	public int GetProxsCountFromDB() {
		int prcount = 0;
		try {
			dbConnect();
			String query = "SELECT [total] = COUNT(*) FROM [dbo].[mProxies]";
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			prcount = rs.getInt("total");
			pstmt.close();
			pstmt = null;
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			System.out.println("GetProxsCountFromDB exception : " + e.getMessage());
		}
		return prcount;
	}

	/**
	 * Save image to DB
	 */
	public void SaveImage(byte[] picture, int gender, int ptype_id) {
		try {
			dbConnect();
			String query = "{call [dbo].[spLoadImage](?,?,?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setBytes("pic", picture);
			sp.setInt("gender", gender);
			sp.setInt("ptype_id", ptype_id);
			sp.execute();
			sp.close();
			sp = null;
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			System.out.println("SaveImage exception : " + e.getMessage());
		}
	}

	/**
	 * Save image from file to DB
	 */
	public void SaveImageFromFile(String fname, int gender, int ptype_id) {
		try {
			dbConnect();
			String query = "{call [dbo].[spLoadFile](?,?,?)}";
			CallableStatement sp = conn.prepareCall(query);
			sp.setString("FileName", fname);
			sp.setInt("gender", gender);
			sp.setInt("ptype_id", ptype_id);
			sp.execute();
			sp.close();
			sp = null;
			if (conn != null)
				conn.close();
			conn = null;
		} catch (Exception e) {
			System.out.println("SaveImage exception : " + e.getMessage());
		}
	}

}
