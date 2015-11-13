package com.example.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.agriservice.R;
import com.example.agriservice.SocketService;
import com.example.util.MySQLite;

public class TodayMessageActivity extends BaseActivity {

	private String TAG = "TodayMessageActivity";
	private TextView tv = null;
    private SQLiteDatabase dbReader;// 获得数据库可写操作的对象
	private MySQLite mySqlite = null;
	private StringBuilder sb = new StringBuilder();
	
	private LocalBroadcastManager mLocalBroadcastManager = null;
	private MessageBackReciver mReciver = null;
	private IntentFilter mIntentFilter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_today_message);
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		mReciver = new MessageBackReciver();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(NoticeViewActivity.UpdateToday_ACTION);
		tv = (TextView)findViewById(R.id.message);
		update();
	}
	
public void update(){
	sb.setLength(0);
	mySqlite = new MySQLite(this);// 实例化MySqklite对象
    dbReader = mySqlite.getReadableDatabase();// 获得数据库可读操作权限 
    selectDB();
    tv.setText(sb);
}
	
// 查询数据
  public void selectDB() {
      // 游标存储数据
//	  table:表名，不能为null
//	  columns:要查询的列名，可以是多个，可以为null，表示查询所有列
//	  selection:查询条件，比如id=? and name=? 可以为null
//	  selectionArgs:对查询条件赋值，一个问号对应一个值，按顺序 可以为null
//	  groupby:分组
//	  having:语法have，可以为null
//	  orderBy：语法，按xx排序，可以为null
      Cursor cursor = dbReader.query("message", null, null, null, null, null,
              "time desc");
       while (cursor.moveToNext()) {// 判断游标的移动
       String str = cursor.getString(cursor.getColumnIndex("topic")) + "\n" +
    		   cursor.getString(cursor.getColumnIndex("time")) + "\n" +
    		   cursor.getString(cursor.getColumnIndex("content"));//得到游标中列名content对应值
       sb.append(str + "\n\n");
       Log.d("TodayMessageActivity", sb.toString());
       }
       dbReader.close();
  }
  
  
	class MessageBackReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(SocketService.HEART_BEAT_ACTION)) {
			} else {
				update();
			}
		};
	}
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(TAG, "onStart");
		mLocalBroadcastManager.registerReceiver(mReciver, mIntentFilter);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(TAG, "onStop");
		mLocalBroadcastManager.unregisterReceiver(mReciver);
		
	}
  

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// is activity withing a tabactivity
	    if (getParent() != null) 
	    {
	        return getParent().onCreateOptionsMenu(menu);
	    }
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:{
			BaseActivity.exit();
			break;
		}
		default:break;
		}
		return true;
	}

}
