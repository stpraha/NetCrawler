package com.cxdcraw.util; 

import java.util.*;
import java.io.*;


import java.lang.*;
import java.net.*;
import java.util.regex.*;

public class AllLink
{	
	static int N = 1;		//修改这个以更改获取广度
	static int ThreadCount = 4;	// 下载的线程数
	
	/**
	 * 这里要继续改进，把MAP，加线程
	 * @param args
	 */
	static int num = 0;
	public static void main(String[] args)
	{	
		//定义即将访问的连接
		//String url = "http://pppp25.com";
		//String url = "http://www.46fk.com";
		//String url = "http://www.meiyuanguan.com";	
		String url = "http://sstushu.com";
		//String url = "http://www.ttse8.com";
		//String url = "http://www.hust.edu.cn";	
		//String url = "http://www.zifangsky.cn";	
		
		//定义Map
		HashMap<String,Boolean> oldMap = new HashMap<String,Boolean>();
		
		//System.out.println("开始获取链接...");
						
		oldMap.put(url, false);
		//将初始连接传递给方法，返回一个储存了所有链接的Map
		oldMap = crawLinks(url,oldMap);
		
		//遍历storeMap中的元素的链接并打印
		Iterator iter = oldMap.entrySet().iterator();
		
		System.out.println("开始将获取到的链接插入数据库...");
		while(iter.hasNext())
		{
			Map.Entry entry = (Map.Entry) iter.next();
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
			//DownloadPic.DWL(entry.getKey().toString());	//传递给DWL的值应为页面链接
			
			StoreSQL.StoreInSql(url, entry.getKey().toString());
		}				
		
		System.out.println("开始下载图片...");
		DownloadPic.DWL(url,ThreadCount);	//(数据库表的地址,下载线程数)  数据库的表设置的名字为url
		
		//删除下载文件夹里大小相同的文件---视作相同文件
		try
		{
			DupRemove.Delet("F:\\picdownload2\\");
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static HashMap<String,Boolean> crawLinks(String baseLink,HashMap<String,Boolean> oldMap)
	{
		//用于储存即将遍历的链接
		String rawLink = "";
		
		//定义newMap
		HashMap<String,Boolean> newMap = new HashMap<String,Boolean>();
		
		//将初始连接放在newdMap中
		newMap.put(baseLink, false);
		
		for(Map.Entry<String,Boolean> Link : oldMap.entrySet())
		{
			//测试用
			if(num == N)
			{
				return oldMap;
			}
			// 如果没被遍历过 则遍历一下
			if(Link.getValue() == false)
			{
				rawLink = Link.getKey();
				//newMap.put("test1111"+ nums, true);
				//发起Get请求
				try
				{
					//将rawLink转成url对象
					URL realURL = new URL(rawLink);
					
					HttpURLConnection connection = (HttpURLConnection)realURL.openConnection();
					connection.setRequestMethod("GET");
					//connection.setConnectTimeout(2000);
					//connection.setReadTimeout(2000);
					
					/**
					 * Gets the status code from an HTTP response message. For example, in the case of the following status lines: 
		 			 * HTTP/1.0 200 OK
		 			 * HTTP/1.0 401 Unauthorized
					 */
					System.out.println(connection.getResponseCode());
					num++;
					
					if(connection.getResponseCode() == 200)
					{						
						InputStream inputStream = connection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
						
						String line = "";
						Pattern pattern1 = Pattern.compile("<a.*?href=[\"']?((https?://)?/?[^\"']+)[\"']?.*?>(.+)</a>");
						//以上这条，<a.*?href= 匹配<a href=   [\"']?匹配单个"或'   ((https?://)?/?[^\"']+)匹配https:或http:或没有   ？？？？
						Matcher matcher1 = null;
						while((line = reader.readLine()) != null )
						{
							//System.out.println(line);						
							matcher1 = pattern1.matcher(line);
							
							if(matcher1.find())
							{
								String newLink = matcher1.group(1).trim();	//返回检测到的链接并去掉头尾空格

								//判断链接是否以http开头
								if(!(newLink.startsWith("http")))
								{
									if(newLink.startsWith("/"))
									{
										newLink = baseLink + newLink;
									}
									else
									{
										newLink = baseLink + "/" + newLink;
									}
								}
								
								//去掉末尾的/
								if(newLink.endsWith("/"))
								{
									newLink = newLink.substring(0, newLink.length()-1);
								}
								
								//去掉末尾的>及之后的
								if(newLink.indexOf('>') != -1)
								{
									int loc = newLink.indexOf('>');
									newLink = newLink.substring(0, loc);
								}
								
								//去掉末尾的" "及之后的
								if(newLink.indexOf(' ') != -1)
								{
									int loc = newLink.indexOf(' ');
									newLink = newLink.substring(0, loc);
								}
								
								
								//去重，去外链
								if( (!oldMap.containsKey(newLink)) && (!newMap.containsKey(newLink)) && (newLink.startsWith(baseLink)) && (!(newLink.endsWith("jpg"))))
								{
									System.out.println(newLink);
									num = 0;
									newMap.put(newLink, false);
								}
							}
						}
					}			
				}
				catch (MalformedURLException e)
				{
					//e.printStackTrace();
				} 
				catch (IOException e)
				{
					//e.printStackTrace();
				}	
				oldMap.replace(rawLink, false, true);		
			}
		}	
		
		//有新链接，继续遍历
		if (!newMap.isEmpty())
		{
			oldMap.putAll(newMap);
			oldMap.putAll(crawLinks(baseLink,oldMap)); //由于Map的特性，不会导致出现重复的键值对
		}
		return oldMap;
	}
}
