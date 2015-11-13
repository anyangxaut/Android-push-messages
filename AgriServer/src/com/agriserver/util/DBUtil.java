package com.agriserver.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * 与数据库建立连接
 *
 */
public class DBUtil {

	//连接数据库
	public Connection openConnection( ){
		Properties prop = new Properties() ;
		String driver = null ;
		String url = null ;
		String username = null ;
		String password = null ;
		
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream("DBConfig.properties"));
			driver =prop.getProperty("driver") ;
			url = prop.getProperty("url") ;
			username = prop.getProperty("username") ;
			password= prop.getProperty("password") ;
			
			//Class.forName(driver);这个方法的作用是装载className这个字符串指定的类
			/*
			 * 具有指定名的类的 Class 对象。
			 * 通俗的说就是：获得字符串参数中指定的类，并初始化该类
			 * */
			Class.forName(driver) ;
			
			return DriverManager.getConnection(url, username, password) ;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return null ;
				
	}
}
