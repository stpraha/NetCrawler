package com.cxdcraw.util;

import javax.servlet.http.HttpServletRequest;

public class Test {
	public String getWeb2Crawl(HttpServletRequest request) {
		String url = request.getParameter("web2crawl");
		System.out.println(url);
		return url;
	}
}
