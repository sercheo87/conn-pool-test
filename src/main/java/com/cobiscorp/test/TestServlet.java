package com.cobiscorp.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@SuppressWarnings("serial")
public class TestServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		DataSource dataSource = null;
		Connection conn = null;
		Statement stmt = null;
		// resp.setContentType("text/html");

		// PrintWriter writer = resp.getWriter();
		// writer.println("<h1>Connection Pool Test</h1>");

		try {
			// JNDI
			Context ctx = new InitialContext();

			String jndiDS = req.getParameter("jndiDS");
			System.out.println("jndiDS: " + jndiDS);

			String script = req.getParameter("script");
			System.out.println("script: " + script);

			String operation = req.getParameter("operation");
			System.out.println("operation: " + operation);

			String cols = req.getParameter("cols");
			if (cols == null || "".equals(cols)) {
				cols = "1";
			}
			System.out.println("cols: " + cols);

			String wMaxrows = req.getParameter("rows");
			if (wMaxrows == null || "".equals(wMaxrows)) {
				wMaxrows = "10";
			}
			System.out.println("cols: " + cols);
			if (jndiDS == null) {
				jndiDS = "jdbc/CTS_BDD_MF";
			}

			dataSource = (DataSource) ctx.lookup(jndiDS);
			conn = (dataSource.getConnection());
			System.out.println("connection: " + conn);
			// JNDI end

			stmt = conn.createStatement();
			// String sSQL = "select count(*) from cts_serv_catalog";
			if ("select".equals(operation)) {
				stmt.setMaxRows(Integer.parseInt(wMaxrows));
				ResultSet result = stmt.executeQuery(script);
				StringBuilder sb = new StringBuilder();
				while (result.next()) {
					Integer colsI = Integer.parseInt(cols);
					sb.append("--" + result.getRow() + " -->");
					for (int i = 1; i <= colsI; i++) {
						Object rows = result.getObject(i);
						sb.append(" --" + rows);
						// writer.println("<p>" + rows + "</p>");

					}
					sb.append("\n");
					// resp.getWriter().println(sb.toString());
					System.out.println(sb.toString());
				}
				req.setAttribute("resp", sb.toString());
				req.getRequestDispatcher("prueba.jsp").forward(req, resp);

			} else {
				int execute = stmt.executeUpdate(script);
				req.setAttribute("resp", execute);
				req.getRequestDispatcher("prueba.jsp").forward(req, resp);
				// resp.getWriter().println("execute: " + execute);
				System.out.println("execute result: " + execute);
			}

		} catch (SQLException e) {
			System.out.println("Starting exeption");
			SQLWarning warning = null;
			try {
				warning = stmt.getWarnings();
				while (warning != null) {
					System.out.println("STM getMessage: [" + warning.getMessage() + "][" + warning.getCause() + "][" + warning.getErrorCode() + "]");
					warning = warning.getNextWarning();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			while ( e != null ) {
				System.out.println("exection getMessage [" + e.getMessage() + "][" + e.getCause() + "][" + e.getErrorCode() + "]");
				e = e.getNextException();
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				// Thread.sleep(10000);
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("connection closed");
			}

		}
	}
}
