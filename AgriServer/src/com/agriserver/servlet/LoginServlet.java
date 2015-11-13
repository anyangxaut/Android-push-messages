package com.agriserver.servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.agriserver.util.DBOperation;

/**
 * 对用户名，密码进行验证
 * @author anyang
 *
 */
public class LoginServlet extends HttpServlet {
	protected void doGet ( HttpServletRequest  request , 
			HttpServletResponse response ) throws ServletException,IOException {
		String username = request.getParameter("username") ;
		String password = request.getParameter("password" ) ;
//		System.out.println( username+":" +password ) ;
		
		response.setContentType("text/html") ;
		response.setCharacterEncoding("utf-8") ;
		PrintWriter out = response.getWriter() ;
		
		String msg = null ;
		
		if( username !=null && !username.equals("") && password != null && !password.equals("")){
			//查询数据库，验证用户是否存在
			String sql = "select count(*) from userinfo where UserName='"+username +"' and Password='"+password+"'" ;
			
			DBOperation dbOperation= new DBOperation() ;
			ResultSet rs = dbOperation.findsql( sql ) ;
			
			try {
				if (rs. next()  ) {
					if( rs.getInt(1)==0)
						msg= "登录失败" ;
					else {
						msg="登录成功";
					}
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace() ;
			}
			out.print( msg ) ;
			out.flush() ;
			out.close() ;
		}
	}
	
	protected void doPost( HttpServletRequest request ,HttpServletResponse response) 
			throws ServletException,IOException {
	
		doGet(request, response) ;
	}

}
