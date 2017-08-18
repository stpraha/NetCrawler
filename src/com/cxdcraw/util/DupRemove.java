package com.cxdcraw.util;
import java.io.*;
import java.util.*;

public class DupRemove
{
	public static void Delet(String picPath) throws IOException 
	{
		LinkedList<FileProperty> list = showDir(new File(picPath));
		
		//接下来将list里的数据按大小排序
		Sort.sortWithSize(list);
		
		for(FileProperty i : list)
		{
			System.out.println(i.getName());
			System.out.println(i.getSize());
		}
				
		//一堆一样大的只留一份	
		FileProperty temp = list.poll();
		FileProperty temp1;
		
		while(!list.isEmpty())
		{	
			temp1 = list.poll();
			while(temp.getSize() == temp1.getSize())
			{
				try
				{
					File file = new File(picPath + temp1.getName());
					if(file.delete())
					{
						System.out.println("delete file :  " + temp1.getName());
					}
					else
					{
						System.out.println("delete failure :  " + temp.getName());
					}
				}
				catch(Exception e)
				{
					throw e;
				}
						
				if(list.isEmpty())
				{
					break;
				}
				
				temp1 = list.poll();
			}
			temp = temp1;
		}
	}
	
	private static LinkedList<FileProperty> showDir(File file) throws IOException
	{
		LinkedList<FileProperty> list = new LinkedList<FileProperty>();
		
		if(file.isDirectory())
		{
			File[] files = file.listFiles();
			//System.out.println(files.length);
			for(int i = 0;i < files.length ;i++)
			{	
				FileProperty fpy = new FileProperty();
				fpy.setName(files[i].getName());
				fpy.setSize(files[i].length());
				
				list.add(fpy);
			}
		}
		return list;
	}
}

class FileProperty
{
	private String name;
	private long size;
	
	FileProperty()
	{
		
	}
	
	FileProperty(String name,int size)
	{
		this.name = name;
		this.size = size;
	}
	
	public void setSize(long l)
	{
		this.size = l;
	}
	
	public long getSize()
	{
		return size;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public String getName() 
	{
		return name;
	}
}

class Sort implements Comparator<FileProperty>
{
	public static LinkedList<FileProperty> sortWithSize(LinkedList<FileProperty> list)
	{
		Comparator<FileProperty> cmp = new Sort();
		Collections.sort(list,cmp);
		
		return list;
	}

	public int compare(FileProperty o1, FileProperty o2) {
		// TODO Auto-generated method stub
		int flag;
		if(o1.getSize() > o2.getSize())
		{
			flag = 1;
		}
		else if (o1.getSize() < o2.getSize())
		{
			flag = -1;
		}
		else if(o1.getSize() == o2.getSize())
		{
			flag = 0;
		}
		else flag = 0;
		return flag;
	}
}

