package com.huilian.spider.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.huilian.spider.JdSearchSpiderThread;
import com.huilian.spider.SpiderConstant;

/**
 * 京东 top 100商品爬虫启动servlet
 */
public class StartupJdTop100SpiderServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4747922810927892441L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StartupJdTop100SpiderServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(SpiderConstant.isJdTop100SpiderRunning){
			response.getWriter().append("true");
			return;
		}
		new Thread(new JdSearchSpiderThread()).start();
		
		response.getWriter().append("true");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
