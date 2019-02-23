package com.cxdcraw.util; 

import java.util.*;
import java.io.*;


import java.lang.*;
import java.net.*;
import java.util.regex.*;

public class AllLink
{	
	static int N = 1;		//淇敼杩欎釜浠ユ洿鏀硅幏鍙栧箍搴�
	static int ThreadCount = 4;	// 涓嬭浇鐨勭嚎绋嬫暟
	
	/**
	 * 杩欓噷瑕佺户缁敼杩涳紝鎶奙AP锛屽姞绾跨▼
	 * @param args
	 */
	static int num = 0;
	
	public static void main(String[] args) {
		Set<String> linkSet = getAllLinks("http://www.huaxincem.com/");
		
		for(String s:linkSet) {
			System.out.println("mmmain   " + s);
		}
	}
	
	public static Set<String> getAllLinks(String url)
	{	
		//瀹氫箟鍗冲皢璁块棶鐨勮繛鎺�
		//String url = "http://www.tidepharm.com/";	
		
		//瀹氫箟Map
		HashMap<String,Boolean> oldMap = new HashMap<String,Boolean>();
		
		//System.out.println("寮�濮嬭幏鍙栭摼鎺�...");
						
		oldMap.put(url, false);
		//灏嗗垵濮嬭繛鎺ヤ紶閫掔粰鏂规硶锛岃繑鍥炰竴涓偍瀛樹簡鎵�鏈夐摼鎺ョ殑Map
		oldMap = crawLinks(url,oldMap);
		
		//閬嶅巻storeMap涓殑鍏冪礌鐨勯摼鎺ュ苟鎵撳嵃
		Iterator iter = oldMap.entrySet().iterator();
		
		System.out.println("开始将获取到的链接插入数据库...");
		
		while(iter.hasNext())
		{
			Map.Entry entry = (Map.Entry) iter.next();
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
			//DownloadPic.DWL(entry.getKey().toString());	//浼犻�掔粰DWL鐨勫�煎簲涓洪〉闈㈤摼鎺�
			
			//StoreSQL.StoreInSql(url, entry.getKey().toString());
		}				
		
		System.out.println("开始下载图片...");
		//DownloadPic.DWL(url,ThreadCount);	//(鏁版嵁搴撹〃鐨勫湴鍧�,涓嬭浇绾跨▼鏁�)  鏁版嵁搴撶殑琛ㄨ缃殑鍚嶅瓧涓簎rl
		
		//鍒犻櫎涓嬭浇鏂囦欢澶归噷澶у皬鐩稿悓鐨勬枃浠�---瑙嗕綔鐩稿悓鏂囦欢
		try
		{
			DupRemove.Delet("F:\\picdownload2\\");
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return oldMap.keySet();
	}

	public static HashMap<String,Boolean> crawLinks(String baseLink,HashMap<String,Boolean> oldMap)
	{
		//鐢ㄤ簬鍌ㄥ瓨鍗冲皢閬嶅巻鐨勯摼鎺�
		String rawLink = "";
		
		//瀹氫箟newMap
		HashMap<String,Boolean> newMap = new HashMap<String,Boolean>();
		
		//灏嗗垵濮嬭繛鎺ユ斁鍦╪ewdMap涓�
		newMap.put(baseLink, false);
		//System.out.println(baseLink);
		
		for(Map.Entry<String,Boolean> Link : oldMap.entrySet())
		{
			//娴嬭瘯鐢�
			if(num == N)
			{
				return oldMap;
			}
			// 濡傛灉娌¤閬嶅巻杩� 鍒欓亶鍘嗕竴涓�
			if(Link.getValue() == false)
			{
				rawLink = Link.getKey();
				//newMap.put("test1111"+ nums, true);
				//鍙戣捣Get璇锋眰
				try
				{
					//灏唕awLink杞垚url瀵硅薄
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
						//浠ヤ笂杩欐潯锛�<a.*?href= 鍖归厤<a href=   [\"']?鍖归厤鍗曚釜"鎴�'   ((https?://)?/?[^\"']+)鍖归厤https:鎴杊ttp:鎴栨病鏈�   锛燂紵锛燂紵
						Matcher matcher1 = null;
						while((line = reader.readLine()) != null )
						{
							//System.out.println(line);						
							matcher1 = pattern1.matcher(line);
							
							if(matcher1.find())
							{
								String newLink = matcher1.group(1).trim();	//杩斿洖妫�娴嬪埌鐨勯摼鎺ュ苟鍘绘帀澶村熬绌烘牸
								System.out.println("werqwerqwerqweR" + newLink);
								//鍒ゆ柇閾炬帴鏄惁浠ttp寮�澶�
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
								//System.out.println(newLink);
								
								//鍘绘帀鏈熬鐨�/
								if(newLink.endsWith("/"))
								{
									newLink = newLink.substring(0, newLink.length()-1);
								}
								
								//鍘绘帀鏈熬鐨�>鍙婁箣鍚庣殑
								if(newLink.indexOf('>') != -1)
								{
									int loc = newLink.indexOf('>');
									newLink = newLink.substring(0, loc);
								}
								
								//鍘绘帀鏈熬鐨�" "鍙婁箣鍚庣殑
								if(newLink.indexOf(' ') != -1)
								{
									int loc = newLink.indexOf(' ');
									newLink = newLink.substring(0, loc);
								}
								System.out.println(newLink);
								
								//鍘婚噸锛屽幓澶栭摼
								if( (!oldMap.containsKey(newLink)) && (!newMap.containsKey(newLink)) && (newLink.startsWith(baseLink)) && (!(newLink.endsWith("jpg"))))
								{
									num = 0;
									newMap.put(newLink, false);
								}
							}
						}
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
				oldMap.replace(rawLink, false, true);		
			}
		}	
		
		//鏈夋柊閾炬帴锛岀户缁亶鍘�
		if (!newMap.isEmpty())
		{
			oldMap.putAll(newMap);
			oldMap.putAll(crawLinks(baseLink,oldMap)); //鐢变簬Map鐨勭壒鎬э紝涓嶄細瀵艰嚧鍑虹幇閲嶅鐨勯敭鍊煎
		}
		return oldMap;
	}
}
