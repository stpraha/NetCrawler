package com.cxdcraw.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StoreSQL {
	//JDBC �����������ݿ�URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/RUNOOB?useSSL=false";
	
	//���ݿ���û���������
	static final String USER = "root";
	static final String PASS = "a13757407965b";
	
	public static void StoreInSql(String baseurl ,String targeturl)
	{
		//String baseurl = "wwww.baidu.com";
		
		Connection conn = null;  
		//A connection (session) with a specific database. SQL statements are executed and results are returned within the context of a connection.
		
		Statement stmt = null;
		//The object used for executing a static SQL statement and returning the results it produces. 

		try
		{
			//ע��JDBC����
			Class.forName("com.mysql.jdbc.Driver");
			
			//������
			//System.out.println("�������ݿ�...");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);			
			//Attempts to establish a connection to the given database URL. The DriverManager attempts to select an appropriate driver from the set of registered JDBC drivers. 

			//ִ�в�ѯ
			//System.out.println("ʵ����Statement��...");
			stmt = conn.createStatement();
			
			String sql;//���ڴ���sql���
			
			//������Ӧ���
			try
			{
				sql = "CREATE TABLE `crawlinks`.`"+baseurl+"` ( `id` INT NOT NULL AUTO_INCREMENT,`url` VARCHAR(255) NULL,`ifdownloaded` TINYINT NULL,PRIMARY KEY (`id`));";
				stmt.executeUpdate(sql);
			}
			catch(SQLException se)
			{
				//System.out.println("database already exited");
			}
						
			System.out.println("��������...");
			sql = "INSERT INTO `crawlinks`.`"+baseurl+"` ( `url`, `ifdownloaded`) VALUES ('"+ targeturl +"', '0')";	
			
			System.out.println("���ڲ���url�����ݿ⣺ " + baseurl + "  ����Ϊ�� " + targeturl);
			stmt.executeUpdate(sql);
			
			stmt.close();
			conn.close();
		}
		catch(SQLException se)
		{
		    // ���� JDBC ����
            se.printStackTrace();
		}
		catch(Exception e)
		{
            // ���� Class.forName ����
            e.printStackTrace();
        }
		finally
		{
            // �ر���Դ
            try
            {
                if(stmt!=null) stmt.close();
            }
            catch(SQLException se2)
            {
            }// ʲô������
            try
            {
                if(conn!=null) conn.close();
            }
            catch(SQLException se)
            {
                se.printStackTrace();
            }
        }
		System.out.println("����ɹ���");
	}
}
