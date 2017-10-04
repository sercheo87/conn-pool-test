package com.cobiscorp.test.threads;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class SimulaCOBISDS extends Thread {
	
	/** Variable para el DataSource */
	private String dataSource = "jdbc/CTS_BDD_MF";
	private Long tbc;
	private Long tac;
	
	public SimulaCOBISDS(String ds, Long tbc, Long tac) {
		this.dataSource = ds;
		this.tbc = 1L; //tbc * 1000;
		this.tac = 1L; //tac * 1000;
	}
	
	@Override
	public void run() {
		Connection con;
		try {
			con = getDBConnection();
			System.out.println("[" + this.getId() + "] conexion: " + con);
			System.out.println("[" + this.getId() + "] ejecutando consulta 'select @@trancount'... ");
			execPS(con, "@@trancount");
			System.out.println("[" + this.getId() + "] fin consulta... ");
			if (con != null) {
				System.out.println("[" + this.getId() + "] wait " + tbc + "ms ...");
				Thread.sleep(tbc);
				con.close();
				System.out.println("[" + this.getId() + "] connection closed");
				System.out.println("[" + this.getId() + "] wait " + tac + "ms ...");
				Thread.sleep(tac);
				System.out.println("[" + this.getId() + "] end");
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Connection getDBConnection() throws NamingException, SQLException {

		Connection conn;

		System.out.println("[" + this.getId() + "]dataSourceName:" + dataSource);

		DataSource wDataSource = getDataSource(dataSource);
		System.out.println("[" + this.getId() + "]dataSource:" + wDataSource);
		
		long tini = System.currentTimeMillis();
		conn = wDataSource.getConnection();
		System.out.println("[" + this.getId() + "]Connection:" + conn);
		long tfin = System.currentTimeMillis();
		long wDif = tfin - tini;
		System.out.println("[" + this.getId() + "]tiempo para obtener Conexion:" + wDif + "(ms)");
		
		if(wDif > 1000 ){
			System.out.println("WARN!! excessive time getting database connection : time "+ wDif + "(ms) ");//conn:" + getConnectionInfo(conn));
		}
		
		postConnectionCreation(conn);

		printPID(conn);

//		System.out.println("[" + this.getId() + "]open conn:" + getConnectionInfo(conn));
		
		System.out.println("[" + this.getId() + "]IsolationLevel:" + conn.getTransactionIsolation());
		return conn;
	}
	
	public DataSource getDataSource(String dataSourceName) throws NamingException, SQLException {
		
		Context ctx = new InitialContext();
		DataSource dataSource = (DataSource) ctx.lookup(dataSourceName);
		System.out.println("[" + this.getId() + "] DataSource: " + dataSource);
		return dataSource;
	}
	
	private void postConnectionCreation(Connection connection) throws SQLException {
		System.out.println("[" + this.getId() + "]Sybase: Setting [String] truncation to [OFF]");
		PreparedStatement ps = connection.prepareStatement("set string_rtruncation off"); 
		ps.execute();
	}
	
	private String getConnectionInfo(Connection conn) {
		StringBuffer sb = new StringBuffer();
		sb.append("[" + this.getId() + "] D:" + dataSource + " T:" + Thread.currentThread().getName() + " CN:"
				+ Thread.currentThread().getStackTrace()[4].getClassName() + " MN:"
				+ Thread.currentThread().getStackTrace()[4].getMethodName() + " LN:"
				+ Thread.currentThread().getStackTrace()[4].getLineNumber());

		if (Thread.currentThread().getStackTrace().length > 4) {
			sb.append("\n CN:" + Thread.currentThread().getStackTrace()[5].getClassName() + " MN:"
					+ Thread.currentThread().getStackTrace()[5].getMethodName() + " LN:"
					+ Thread.currentThread().getStackTrace()[5].getLineNumber());
		}
		if (Thread.currentThread().getStackTrace().length > 5) {
			sb.append("\n CN:" + Thread.currentThread().getStackTrace()[6].getClassName() + " MN:"
					+ Thread.currentThread().getStackTrace()[6].getMethodName() + " LN:"
					+ Thread.currentThread().getStackTrace()[6].getLineNumber());
		}
		return sb.toString();

	}
	
	private  void printPID(Connection connection) {
		try {
			PreparedStatement statementPID = connection.prepareStatement("select @@spid");
			if (statementPID.execute()) {
				ResultSet resultSetPID = statementPID.getResultSet();
				if (resultSetPID.next()) {
					System.out.println("[" + this.getId() + "][printPID]ProcessID: " + resultSetPID.getInt(1));
				}
				resultSetPID.close();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	private void execPS(Connection con, String query) throws SQLException {
		PreparedStatement ps = con.prepareStatement("select " + query);
		ResultSet executeQuery = ps.executeQuery();
		while(executeQuery.next()) {
			System.out.println("[" + this.getId() + "]" + query + ": " + executeQuery.getInt(1));
		}
	}
}