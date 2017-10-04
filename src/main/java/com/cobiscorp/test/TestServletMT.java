package com.cobiscorp.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cobiscorp.test.threads.SimulaCOBISDS;

@SuppressWarnings("serial")
public class TestServletMT extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String jndiDS = req.getParameter("jndiDS");
		String nroThreads = req.getParameter("nroThreads");
		Long timeBeforeClose = Long.parseLong(req.getParameter("tbc"));
		Long timeAfterClose = Long.parseLong(req.getParameter("tac"));
		
		int NUM = Integer.parseInt(nroThreads);
		Thread[] multiThread = new Thread[NUM];
		for (int i = 0; i < NUM; i++) {
//			multiThread[i] = new Trh(jndiDS, timeBeforeClose, timeAfterClose);
			multiThread[i] = new SimulaCOBISDS(jndiDS, timeBeforeClose, timeAfterClose);
			multiThread[i].start();
		}
	}
}
