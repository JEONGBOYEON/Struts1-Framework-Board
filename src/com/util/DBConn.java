package com.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConn {
	
	private static Connection conn=null;
	
	//¿¬°á
	public static Connection getConnection(){
		String url="jdbc:oracle:thin:@192.168.16.11:1521:TestDB";
		String user="suzi";
		String pwd="a123";
		
		if(conn==null){
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				conn=DriverManager.getConnection(url,user,pwd);
				
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
		
		return conn;
	}
	
	//²÷¾îÁÜ
	public static void close(){
		if(conn==null)
			return;
		try {
			if(!conn.isClosed())
				conn.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		conn=null;
	}

}
