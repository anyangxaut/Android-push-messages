package com.example.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.agriservice.IBackService;
import com.example.agriservice.R;
import com.example.agriservice.SocketService;
import com.example.util.MySQLite;

public class SetupActivity extends BaseActivity {

	private static final String TAG = "SetupActivity";
	private static final int Notification_ID_BASE = 1;
	private boolean serviceFlags = true;
	
	private MySQLite mySqlite = null;
	private SQLiteDatabase dbWriter;// 获得数据库可写操作的对象
	
	private CheckSwitchButton mCheckSwithcButton1 = null;
	private CheckSwitchButton mCheckSwithcButton2 = null;
	private CheckSwitchButton mCheckSwithcButton3 = null;

	private LocalBroadcastManager mLocalBroadcastManager = null;
	private MessageBackReciver mReciver = null;
	private IntentFilter mIntentFilter = null;
	private Intent serviceIntent = null;
	
	private IBackService iBackService = null;
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			iBackService = null;

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iBackService = IBackService.Stub.asInterface(service);
		}
	};
	
	
	class MessageBackReciver extends BroadcastReceiver {
//		private WeakReference<TextView> textView;
//
//		public MessageBackReciver(TextView tv) {
//			textView = new WeakReference<TextView>(tv);
//		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
//			TextView tv = textView.get();
			if (action.equals(SocketService.HEART_BEAT_ACTION)) {
//				if (null != tv) {
//					tv.setText("Get a heart heat");
//				}
			} else {
				String message = intent.getStringExtra("message");
                save(message);
                notification(message);
			}
		};
	}
	
	private void save(String msg){
		
		Log.d("save", "save");
	    mySqlite = new MySQLite(this);// 实例化MySqklite对象
        dbWriter = mySqlite.getWritableDatabase();// 获得数据库可写操作权限
		//删除数据库
//        deleteDatabase("agriservice");

        addDB(msg);
	}
	//删除表
    private void drop(SQLiteDatabase db){          
        //删除表的SQL语句        
        String sql ="DROP TABLE message";           
       //执行SQL       
       db.execSQL(sql);     
   }   
	
//添加数据
  public void addDB(String msg) {
	  Log.d("addDB", "addDB");
      // 获得ContentValues对象
      ContentValues cv = new ContentValues();

          try {
                 //将msg转化为json对象
        	     JSONObject json = new JSONObject(msg);
                 // ContentValues对象的方法
			     cv.put("messageid", json.getString("msgId"));
	             cv.put("topic", json.getString("topic"));
	             cv.put("time", json.getString("time"));
	             cv.put("content", json.getString("msgContent"));
	             // dbWriter的dbWriter方法实现数据的同步
	             dbWriter.insert("message", null, cv);
	          
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
          dbWriter.close();
  }
  
//  // 删除数据
//  public void deleteDB() {
//      dbWriter.delete("user", "_id=1", null);
//  }
//  // 修改数据
//  public void updateDB() {
//      ContentValues cv = new ContentValues();
//      cv.put("name", "Hello");// 修改后的数据的存储
//      dbWriter.update("user", cv, "_id=2", null);
//  }
	
	private void notification(String msg){
		
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		
		//点击通知后跳转的页面
	    Intent intent = new Intent(this, NoticeViewActivity.class);  
	    Bundle bundle = new Bundle();
	    bundle.putInt("notificationID", Notification_ID_BASE);
        bundle.putString("message", msg);
        Log.d(TAG, msg);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//清除上次的activity，效果是ui界面刷新
        intent.addFlags(Intent.FILL_IN_DATA);
        intent.putExtras(bundle);
	    PendingIntent pendingIntent = PendingIntent.getActivity(this, Notification_ID_BASE, intent, Notification_ID_BASE);  
		
		mBuilder.setContentTitle("agriservice")//设置通知栏标题  
	    .setContentText(msg) //消息内容
	    .setContentIntent(pendingIntent) //设置通知栏点击意图  
	//  .setNumber(number) //设置通知集合的数量  
	    .setTicker(msg) //通知首次出现在通知栏，带上升动画效果的  
	    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间  
	    .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级  
	//  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消    
	    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)  
	    .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合  
	    //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission  
	    .setSmallIcon(R.drawable.logo);//设置通知小ICON  
		
		nm.notify(Notification_ID_BASE, mBuilder.build()); 
		
//	    //新建状态栏通知  
//		Notification baseNF = new Notification();  
//	       
//	    //设置通知在状态栏显示的图标  
//	    baseNF.icon = R.drawable.logo;  
//	      
//	    //通知时在状态栏显示的内容  
//	    baseNF.tickerText = msg;  
//	      
//	    //通知的默认参数 DEFAULT_SOUND, DEFAULT_VIBRATE, DEFAULT_LIGHTS.   
//	    //如果要全部采用默认值, 用 DEFAULT_ALL.  
//	    //此处采用默认声音  
//	    baseNF.defaults = Notification.DEFAULT_SOUND;  
//	    
//	      Intent intent = new Intent(this, TodayMessageActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("message", msg);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//这行代码会解决此问题
//        intent.addFlags(Intent.FILL_IN_DATA);
//        intent.putExtras(bundle);
//
//        PendingIntent pt=PendingIntent.getActivity(this, Notification_ID_BASE, intent, PendingIntent.FLAG_UPDATE_CURRENT);                
//	      
//	    //第二个参数 ：下拉状态栏时显示的消息标题 expanded message title  
//	    //第三个参数：下拉状态栏时显示的消息内容 expanded message text  
//	    //第四个参数：点击该通知时执行页面跳转  
//	    baseNF.setLatestEventInfo(this, "agriservice", msg, pt);  
//	      
//	    //发出状态栏通知  
//	    //The first parameter is the unique ID for the Notification   
//	    // and the second is the Notification object.  
//	    nm.notify(Notification_ID_BASE, baseNF);  
		
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		
		mCheckSwithcButton1 = (CheckSwitchButton)findViewById(R.id.mCheckSwithcButton1);
		mCheckSwithcButton2 = (CheckSwitchButton)findViewById(R.id.mCheckSwithcButton2);
		mCheckSwithcButton3 = (CheckSwitchButton)findViewById(R.id.mCheckSwithcButton3);
		
	    serviceIntent = new Intent(this, SocketService.class) ;
		
		mReciver = new MessageBackReciver();

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(SocketService.HEART_BEAT_ACTION);
		mIntentFilter.addAction(SocketService.MESSAGE_ACTION);
		
		//开启服务
		mCheckSwithcButton1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
					if(isChecked){  
					            //选中  
							startService(serviceIntent) ;
							serviceFlags = true;
							 
					  }else{  
					            //未选中  
							stopService(serviceIntent) ;
							serviceFlags = false;
					        }  
					}
				});
		
		//本地存储
        mCheckSwithcButton2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				 if(isChecked){  
			            //选中  
					 
			        }else{  
			            //未选中  
			        	
			        }  
			}
		});
        
        //消息订阅
        mCheckSwithcButton3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				 if(isChecked){  
			            //选中  
					 
			        }else{  
			            //未选中  
			        	
			        }  
			}
		});

		
	}

	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(TAG, "onStart");
		mLocalBroadcastManager.registerReceiver(mReciver, mIntentFilter);
		bindService(serviceIntent, conn, BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(TAG, "onStop");	
	}
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		if(serviceFlags){
			stopService(serviceIntent) ;
		}
		unbindService(conn);
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
