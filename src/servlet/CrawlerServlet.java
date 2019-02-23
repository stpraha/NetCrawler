package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.org.apache.regexp.internal.RESyntaxException;

import com.cxdcraw.util.*;

@WebServlet("/CrawlerServlet")
public class CrawlerServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-type", "text/html; charset=UTF-8");
		String originUrl = "";
		boolean startCrawl = false;
		try {
			originUrl = request.getParameter("web2crawl");
			System.out.println(originUrl);
			startCrawl = request.getParameter("startCrawl").equals("1");
			System.out.println(startCrawl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		StartCrawl startC = new StartCrawl(originUrl);
		
		Set<String> linkSet = startC.getUrls();
		
		String links = linkSet.toString();
		
		try {
			PrintWriter out = response.getWriter();
			out.print(links);
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
//		for(String s:links) {
//			System.out.println("sdfas   " + s);
//		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}

class StartCrawl {
	private String url = "";
	
	StartCrawl(String url) {
		System.out.println("fasdfasdfasdf");
		this.url = url;
	}
	
	public Set<String> getUrls() {
		return AllLink.getAllLinks(url);
	}
}

