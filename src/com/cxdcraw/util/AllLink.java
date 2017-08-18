package com.cxdcraw.util; 

import java.util.*;
import java.io.*;


import java.lang.*;
import java.net.*;
import java.util.regex.*;

public class AllLink
{	
	static int N = 1;		//�޸�����Ը��Ļ�ȡ���
	static int ThreadCount = 4;	// ���ص��߳���
	
	/**
	 * ����Ҫ�����Ľ�����MAP�����߳�
	 * @param args
	 */
	static int num = 0;
	public static void main(String[] args)
	{	
		//���弴�����ʵ�����
		//String url = "http://pppp25.com";
		//String url = "http://www.46fk.com";
		//String url = "http://www.meiyuanguan.com";	
		String url = "http://sstushu.com";
		//String url = "http://www.ttse8.com";
		//String url = "http://www.hust.edu.cn";	
		//String url = "http://www.zifangsky.cn";	
		
		//����Map
		HashMap<String,Boolean> oldMap = new HashMap<String,Boolean>();
		
		//System.out.println("��ʼ��ȡ����...");
						
		oldMap.put(url, false);
		//����ʼ���Ӵ��ݸ�����������һ���������������ӵ�Map
		oldMap = crawLinks(url,oldMap);
		
		//����storeMap�е�Ԫ�ص����Ӳ���ӡ
		Iterator iter = oldMap.entrySet().iterator();
		
		System.out.println("��ʼ����ȡ�������Ӳ������ݿ�...");
		while(iter.hasNext())
		{
			Map.Entry entry = (Map.Entry) iter.next();
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
			//DownloadPic.DWL(entry.getKey().toString());	//���ݸ�DWL��ֵӦΪҳ������
			
			StoreSQL.StoreInSql(url, entry.getKey().toString());
		}				
		
		System.out.println("��ʼ����ͼƬ...");
		DownloadPic.DWL(url,ThreadCount);	//(���ݿ��ĵ�ַ,�����߳���)  ���ݿ�ı����õ�����Ϊurl
		
		//ɾ�������ļ������С��ͬ���ļ�---������ͬ�ļ�
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
		//���ڴ��漴������������
		String rawLink = "";
		
		//����newMap
		HashMap<String,Boolean> newMap = new HashMap<String,Boolean>();
		
		//����ʼ���ӷ���newdMap��
		newMap.put(baseLink, false);
		
		for(Map.Entry<String,Boolean> Link : oldMap.entrySet())
		{
			//������
			if(num == N)
			{
				return oldMap;
			}
			// ���û�������� �����һ��
			if(Link.getValue() == false)
			{
				rawLink = Link.getKey();
				//newMap.put("test1111"+ nums, true);
				//����Get����
				try
				{
					//��rawLinkת��url����
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
						//����������<a.*?href= ƥ��<a href=   [\"']?ƥ�䵥��"��'   ((https?://)?/?[^\"']+)ƥ��https:��http:��û��   ��������
						Matcher matcher1 = null;
						while((line = reader.readLine()) != null )
						{
							//System.out.println(line);						
							matcher1 = pattern1.matcher(line);
							
							if(matcher1.find())
							{
								String newLink = matcher1.group(1).trim();	//���ؼ�⵽�����Ӳ�ȥ��ͷβ�ո�

								//�ж������Ƿ���http��ͷ
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
								
								//ȥ��ĩβ��/
								if(newLink.endsWith("/"))
								{
									newLink = newLink.substring(0, newLink.length()-1);
								}
								
								//ȥ��ĩβ��>��֮���
								if(newLink.indexOf('>') != -1)
								{
									int loc = newLink.indexOf('>');
									newLink = newLink.substring(0, loc);
								}
								
								//ȥ��ĩβ��" "��֮���
								if(newLink.indexOf(' ') != -1)
								{
									int loc = newLink.indexOf(' ');
									newLink = newLink.substring(0, loc);
								}
								
								
								//ȥ�أ�ȥ����
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
		
		//�������ӣ���������
		if (!newMap.isEmpty())
		{
			oldMap.putAll(newMap);
			oldMap.putAll(crawLinks(baseLink,oldMap)); //����Map�����ԣ����ᵼ�³����ظ��ļ�ֵ��
		}
		return oldMap;
	}
}
