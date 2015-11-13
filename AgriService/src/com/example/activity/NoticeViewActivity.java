package com.example.activity;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.example.agriservice.R;

public class NoticeViewActivity extends BaseActivity {

	private LocalBroadcastManager mLocalBroadcastManager = null;
	public static final String UpdateToday_ACTION="com.example.updatetoday_ACTION";
	private TextView tv = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice_view);
		
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //取消通知
        nm.cancel(getIntent().getExtras().getInt("notificationID"));
       
 	    String message = getIntent().getExtras().getString("message");
 	    Log.d("NoticeViewActivity",message);
        tv = (TextView)findViewById(R.id.message);
        tv.setText(message);
        mLocalBroadcastManager=LocalBroadcastManager.getInstance(this);
        Intent intent=new Intent(UpdateToday_ACTION);
        mLocalBroadcastManager.sendBroadcast(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_notice_view, menu);
		return true;
	}

}
