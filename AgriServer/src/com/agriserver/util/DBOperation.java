package com.agriserver.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 该类作用：数据库操作，增删查改
 * 
 * 主要方法：excutesql，closeConn和findsql
 * closeConn：关闭数据库连接
 * excutesql：适用于实现增删改操作，不适用于查询，且一次性可以执行多条sql语句
 * findsql：用于查询功能的方法，且只适用于查询
 *
 */
public class DBOperation {

	public Connection con =null ;
	public Statement st  = null ;
	public ResultSet rs = null ;
    
	public boolean excutesql(String[ ] sql) {
		boolean bool = true ;
		Statement st = null;
		
		DBUtil util =new DBUtil() ;
		Connection con = util.openConnection() ;
		try {
			
//			自动提交：在做记录更新时，系统会自动提交,不能保持事务的一致性，也就不能保证数据完整。
//			手动提交：它则把事务处理将由你来完成，在发生异常时，可以进行事务回滚。保持事务的一致。
			con.setAutoCommit(false);
			st = con.createStatement() ;
			
			for ( int i = 0 ; i<sql.length ; i++ ){
				System.out.println(sql ) ;
				st.addBatch(sql[i ]) ;
			}
			
			st.executeBatch() ;
			// 因为是手动提交，所以采用该命令con(Connection ) 
			con.commit() ;    
			
			System.out.println( "执行成功") ;
		} catch (SQLException e) {
			// TODO: handle exception
			try {
				
				System.out.println("执行失败") ;
				bool = false ;
				// 执行失败，因此要进行回滚
				con.rollback() ;
			} catch (SQLException e2) {
				// TODO: handle exception
				e2.printStackTrace() ;
			}
			
			e.printStackTrace( ) ;
		}finally{
			
			if( st != null ){
				try {
					// 关闭连接
					st.close() ;
					if( con != null){
						con.close() ;
					}
				} catch (SQLException e) {
					// TODO: handle exception
					e.printStackTrace() ;
				}
			}
		}
		
		return bool ;  // bool 未经改动为true
		
	}


	//用于查询功能的方法，且只适用于查询
	public ResultSet findsql( String sql ){
		
		try {
			DBUtil util = new DBUtil() ;
			con = util.openConnection() ;
			
			st = con.createStatement() ;
			rs = st.executeQuery(sql) ;
			
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace() ;
		}
		return rs ;
	}


	public void closeConn() {
		if (rs != null ) {
			try {
				rs.close() ;
				if( st != null){
					st.close();
					if(con != null){
						con.close() ;
					}
				}
			} catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace() ;
			}
		}
	}  // end_of_closeConn
	
}
