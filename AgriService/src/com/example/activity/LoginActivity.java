package com.example.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.agriservice.R;
import com.example.util.HttpUtil;

public class LoginActivity extends BaseActivity {

	private  Button cancelBtn , loginBtn ;
	private EditText userNameEditText , pwdeEditText ;
	public static String username = "";
	private String password = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		//实例化视图组件
		cancelBtn = (Button) findViewById(R.id.cancel) ;
		loginBtn = (Button) findViewById(R.id.ok ) ;
		userNameEditText = (EditText)findViewById(R.id.username) ;
		pwdeEditText = (EditText) findViewById(R.id.password ) ;
		
		// 设置登录按钮登陆监听器
		loginBtn.setOnClickListener( new OnClickListener( ) {
			@Override
			public void onClick(View v ) {
				// TODO Auto-generated method stub
				  username = userNameEditText.getText().toString() ;
				  password = pwdeEditText.getText().toString() ;
				
				new Thread(new Runnable() {  
		        @Override  
		        public void run() {  
		            // 登陆认证httpclient
//		        	Login( username, password ) ;
		        	Intent intent = new Intent( ) ;
					intent.setClass(LoginActivity.this, MainInterface.class);
					startActivityForResult(intent, RESULT_OK ) ;
					finish();
		        }  
		    }).start();
				
			}
		}) ;
		
		cancelBtn.setOnClickListener(new OnClickListener( ) {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish( )  ;
			}
		}) ;
	}

	
	private void Login( String username , String password ){
		
		//URL地址，访问指定网站的servlet
		String urlStr =HttpUtil.BASE_URL+"/LoginServlet" ;

		// 如果传递参数个数比较多，可以对传递的参数进行封装
		List<NameValuePair> params = new ArrayList<NameValuePair>() ;
		params.add(new BasicNameValuePair("username", username)) ;
		params.add(new BasicNameValuePair("password", password) ) ;
		
		
		
		String msg = HttpUtil.queryStringForPost(urlStr, params);
		
		if( msg.equals("登录成功") ){
			
			Intent intent = new Intent( ) ;
			intent.setClass(LoginActivity.this, MainInterface.class);
			startActivityForResult(intent, RESULT_OK ) ;
			finish();
		}
			
		if( msg.equals("登录失败") ){}
//			showMessageDialog(msg, R.drawable.info_32,  null );
		
	}
	
	protected void showMessageDialog( String message , int iconId ,
			DialogInterface.OnClickListener onClickListener ){
	 
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this ) ;
		dialogBuilder.setIcon(iconId ) ;        //  设置图标
		dialogBuilder.setTitle(message) ;   // 设置标题
		dialogBuilder.setPositiveButton("确定", onClickListener) ;  //设置确定按钮事件
		dialogBuilder.create().show() ;  // 创建、显示对话框
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
