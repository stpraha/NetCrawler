package com.cxdcraw.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StoreSQL {
	//JDBC 驱动名及数据库URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/RUNOOB?useSSL=false";
	
	//数据库的用户名与密码
	static final String USER = "root";
	static final String PASS = "************";
	
	public static void StoreInSql(String baseurl ,String targeturl)
	{
		//String baseurl = "wwww.baidu.com";
		
		Connection conn = null;  
		//A connection (session) with a specific database. SQL statements are executed and results are returned within the context of a connection.
		
		Statement stmt = null;
		//The object used for executing a static SQL statement and returning the results it produces. 

		try
		{
			//注册JDBC驱动
			Class.forName("com.mysql.jdbc.Driver");
			
			//打开链接
			//System.out.println("链接数据库...");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);			
			//Attempts to establish a connection to the given database URL. The DriverManager attempts to select an appropriate driver from the set of registered JDBC drivers. 

			//执行查询
			//System.out.println("实例化Statement对...");
			stmt = conn.createStatement();
			
			String sql;//用于传递sql语句
			
			//创建对应表格
			try
			{
				sql = "CREATE TABLE `crawlinks`.`"+baseurl+"` ( `id` INT NOT NULL AUTO_INCREMENT,`url` VARCHAR(255) NULL,`ifdownloaded` TINYINT NULL,PRIMARY KEY (`id`));";
				stmt.executeUpdate(sql);
			}
			catch(SQLException se)
			{
				//System.out.println("database already exited");
			}
						
			System.out.println("插入数据...");
			sql = "INSERT INTO `crawlinks`.`"+baseurl+"` ( `url`, `ifdownloaded`) VALUES ('"+ targeturl +"', '0')";	
			
			System.out.println("正在插入url到数据库： " + baseurl + "  链接为： " + targeturl);
			stmt.executeUpdate(sql);
			
			stmt.close();
			conn.close();
		}
		catch(SQLException se)
		{
		    // 处理 JDBC 错误
            se.printStackTrace();
		}
		catch(Exception e)
		{
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
		finally
		{
            // 关闭资源
            try
            {
                if(stmt!=null) stmt.close();
            }
            catch(SQLException se2)
            {
            }// 什么都不做
            try
            {
                if(conn!=null) conn.close();
            }
            catch(SQLException se)
            {
                se.printStackTrace();
            }
        }
		System.out.println("插入成功！");
	}
}
