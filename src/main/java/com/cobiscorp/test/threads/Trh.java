package com.cobiscorp.test.threads;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Trh extends Thread {
	
	private String ds;
	private Long tbc;
	private Long tac;
	
	public Trh(String ds, Long tbc, Long tac) {
		this.ds = ds;
		this.tbc = tbc * 1000;
		this.tac = tac * 1000;
	}
	
	@Override
	public void run() {
		try {			
			
			Connection con = getConnection();
			System.out.println("[" + this.getId() + "] connection: " + con + ", autocommit:" + con.getAutoCommit());
			execPS(con, "@@spid");
			execPS(con, "@@trancount");				
			
			con.setAutoCommit(false);
			
			SQLWarning warnings = con.getWarnings();
			if(warnings != null) {
				System.out.println("[" + this.getId() + "]" + warnings.getErrorCode() + ": " + warnings.getMessage());
				SQLWarning nextWarning = warnings.getNextWarning();
				if(nextWarning != null) {
					System.out.println("[" + this.getId() + "]" + nextWarning.getErrorCode() + ": " + nextWarning.getMessage());
				}
			}
			
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
	
	private Connection getConnection() throws NamingException, SQLException {
		//JNDI
		Context ctx = new InitialContext();
		DataSource dataSource = (DataSource) ctx.lookup(ds);
		System.out.println("[" + this.getId() + "] Intento de conexion...");
		Connection con = (Connection) (dataSource.getConnection());
		con.setAutoCommit(true);
		// JNDI end
		return con;
	}
	
	private void execPS(Connection con, String query) throws SQLException {
		PreparedStatement ps = con.prepareStatement("select " + query);
		ResultSet executeQuery = ps.executeQuery();
		while(executeQuery.next()) {
			System.out.println("[" + this.getId() + "]" + query + ": " + executeQuery.getInt(1));
		}
	}
}