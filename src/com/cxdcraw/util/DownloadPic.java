package com.cxdcraw.util;

import java.util.*;
import java.util.Date;
import java.sql.*;
import java.io.*;
import java.lang.*;
import java.net.*;
import java.text.DateFormat;
import java.util.regex.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import com.mysql.jdbc.PreparedStatement;

/**
 * ������߳�����
 */
class DownloadPicWithHtml {
	public static void ReadHtmlMsg(String url)
	{		
		//String url = "http://pppp25.com/htm/2017/8/11/p03/382408.html";
			
		/**
		 * ��������ҳ���ͼƬ��ַ
		 */
		Pattern pattern1 = Pattern.compile("<img.*?src=(.*?)[^>]*?>");   
		//<img(�������Ϊ\n)src=(�������Ϊ\n)(0���������������>)
		Pattern pattern2 = Pattern.compile("http:\"?(.*?)([^\"])*");
		//http:(0����1����)(0�����������Ϊ\n)(��������Ϊ��)(Ϊ"��>�� �κοհ��ַ�)
		Queue<String> queue1 = new LinkedList<String>();
		Queue<String> queue2 = new LinkedList<String>();
		try
		{
			URL targetUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
			connection.setRequestMethod("GET");
			//connection.setConnectTimeout(2000);
			//connection.setReadTimeout(2000);
			
			if(connection.getResponseCode() == 200)
			{
				InputStream inputStream = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
				
				String line = "";
				String line2 = "";
				Matcher matcher1 = null;
				Matcher matcher2 = null;
				
				while( (line = reader.readLine()) != null)
				{
					//System.out.println(line);	
					matcher1 = pattern1.matcher(line);
					while(matcher1.find())
					{
						queue1.offer(matcher1.group());
					}
				}
				
				while(!queue1.isEmpty())
				{
					line2 = queue1.poll();
					matcher2 = pattern2.matcher(line2);
					while(matcher2.find())
					{
						//System.out.println(matcher2.group());
						//queue2.offer(matcher2.group());
						
						//��
						if(!matcher2.group().endsWith("zc=1"))
						{
							System.out.println(matcher2.group());
							queue2.offer(matcher2.group());
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
		/**
		 * ��ͼƬ��ִַ��ͼƬ���س���
		 */
		while(!queue2.isEmpty())
		{
			DownloadPicWithPicurl(queue2.poll());
		}		
	}

	/**
	 * ͼƬ���أ�
	 * 
	 * �Ľ���������·����ѡ��
	 * @param url ͼƬ��ַ
	 */
	private static void DownloadPicWithPicurl(String url)
	{
		Date time = new Date();
		DateFormat df = DateFormat.getTimeInstance();//ֻ��ʾ��ʱ����  
		
		try
		{
			URL picUrl = new URL(url);
			HttpURLConnection picConnection = (HttpURLConnection) picUrl.openConnection();		
			picConnection.setConnectTimeout(2000);
			picConnection.setReadTimeout(20000);	
			
			if((picConnection.getResponseCode() == 200) && (!url.endsWith("asp")))
			{
				String picName = df.format(time) + ".jpg";
				picName = picName.replaceAll(":", "");
					
				InputStream picin = picConnection.getInputStream();
				//BufferedReader reader = new BufferedReader(new InputStreamReader(picin,"UTF-8"));
				
				byte[] bs = new byte[1024]; 
				int len;
				String storepath ="F:\\picdownload2\\" + "\\" + picName;
				
				File file= new File(storepath);
				while(file.exists())
				{
					picName = picName.replaceAll(".jpg", "") + "1" + ".jpg";
					storepath ="F:\\picdownload2\\" + "\\" + picName;
					file= new File(storepath);
				}
				
				FileOutputStream fileOutputStream = new FileOutputStream(new File(storepath));
								
				System.out.println(url + picName);
                while( (len = picin.read(bs)) != -1)
                {
                	fileOutputStream.write(bs, 0, len);
                }
                fileOutputStream.flush();          
				fileOutputStream.close();
				picin.close();
			}			
		}
		catch (MalformedURLException e) 
		{  
			e.printStackTrace();  
		} 
		catch (IOException e) 
		{  
			e.printStackTrace();  
		}
    } 
}

public class DownloadPic 
{
	/**
	 * ���ϼ�������գ����ݿ���ַ���߳���
	 * @param args
	 * @param threadCount
	 */
	public static void DWL(String databaseurl,int threadCount)
	{
		//int threadCount = 3;
		
		List<LinkMessege>[] l = DistributeURL.find(threadCount,databaseurl);
		
		/*
		for(int i = 0; i <threadCount ;i++)
		{
			for(RUNOOB p:l[i])
			{
				System.out.println(p.getId());
			}
		}
		*/
		
		ThreadGroup threadGroup = new ThreadGroup("DownloadThreadGroup");
		
		//ʹ�����̳߳�
		for(int i = 0 ; i < threadCount ; i++)
		{
			RunnableDemo R = new RunnableDemo("Thread-" + i,i,l[i]);
			Thread thread = new Thread(threadGroup,R);
			thread.start();
		}
	}
}

/**
 * ��������ݿ��ж�������ҳ��ַ���ݵĶ���
 * @author Administrator
 *
 */
class LinkMessege
{
	private int id ;
	private String name;
	private String url;
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public String getName()
	{

		return name;
	}

	public void setName(String name) 
	{

		this.name = name;
	}
	public String getUrl()
	{

		return url;
	}

	public void setUrl(String url) 
	{

		this.url = url;
	}
}

/**
 * �����ݿ���ȡ�����Ӳ�����
 * @author Administrator
 *
 */
class DistributeURL
{
	public static List<LinkMessege>[] find(int threadCount,String databaseurl)
	{		
		List<LinkMessege>[] list =(ArrayList<LinkMessege>[]) new ArrayList[threadCount];	//��ʼ������List���������߳�����ͬ
		for(int n = 0; n < threadCount;n++)
		{
			list[n] = new ArrayList<LinkMessege>();
		}
			
		Connection conn;
		Statement stmt;

		int dataCount = 0;//�����ܸ���
		String sql0 = "SELECT count(*) FROM crawlinks.`" + databaseurl + "`;"; 
		//��ȡ�����ܸ���
		try
		{	
			conn = getConnection();
			stmt = conn.createStatement();
			ResultSet dc = stmt.executeQuery(sql0);
			while(dc.next())
			{
				dataCount = dc.getInt(1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(dataCount);
		
		//����������ƽ����������List��
		String sql = "select * from crawlinks.`" + databaseurl + "` order by id asc limit ?,?";
		for(int i = 0; i < threadCount; i ++)
		{
			try
			{	
				conn = getConnection();
				//��ȡpreparedStatement
				PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
				//��sql����еĵ�һ��������ֵ
				ps.setInt(1, (dataCount/threadCount)*(i));	//��һ���ʺţ���������
				//��sql����еĵڶ���ֵ��ֵ
				ps.setInt(2, (dataCount/threadCount));	//�ڶ����ʺţ�ȡ����
				//ִ�в�ѯ����
				ResultSet rs = ps.executeQuery();
			
				while(rs.next())
				{
					LinkMessege r = new LinkMessege();
					r.setId(rs.getInt("id"));
					r.setName(rs.getString("ifdownloaded"));
					r.setUrl(rs.getString("url"));
					
					list[i].add(r);	//ѹ��List
					
					stmt = conn.createStatement();
					String sql1 = "UPDATE `crawlinks`.`" + databaseurl + "` SET `ifdownloaded`='1' WHERE `id`='" + rs.getInt("id") + "';";
					stmt.executeUpdate(sql1);
					stmt.close();
				}
				// �ر�ResultSet
				rs.close();

				// �ر�PreparedStatement
				ps.close();	
				
				conn.close();
			}
			catch (SQLException e) 
			{

				e.printStackTrace();
			}	
		}
		
		return list;	//����List����
	}
	
	private static Connection getConnection()
	{
		// ���ݿ�����
		Connection conn = null;

		try {
			// �������ݿ�������ע�ᵽ����������
			Class.forName("com.mysql.jdbc.Driver");

			// ���ݿ������ַ���
			String url = "jdbc:mysql://localhost:3306/crawlinks?useSSL=false";

			// ���ݿ��û���
			String username = "root";

			// ���ݿ�����
			String password = "a13757407965b";

			// ����Connection����
			conn = DriverManager.getConnection(url,username,password);
		} catch (ClassNotFoundException e) {

			e.printStackTrace();

		} catch (SQLException e) {

			e.printStackTrace();
		}
		// �������ݿ�����
		return conn;
	}
}

class RunnableDemo implements Runnable
{
	private Thread t;
	private String threadName;
	private int threadNum;
	private List<LinkMessege> list;
	
	RunnableDemo(String name,int threadNum,List<LinkMessege> list)
	{
		this.threadName = name;
		this.threadNum = threadNum;
		this.list = list;
	}
	
	public void run()
	{
		for(LinkMessege p :list)
		{
			DownloadPicWithHtml dlp = new DownloadPicWithHtml();
			System.out.println("Thread " + threadNum +" is now downloading form " + p.getUrl() );
			dlp.ReadHtmlMsg(p.getUrl());
		}
	}
	
	public void start()
	{
		if( t== null)
		{
			t = new Thread(this,threadName);
			t.start();
		}
	}
}