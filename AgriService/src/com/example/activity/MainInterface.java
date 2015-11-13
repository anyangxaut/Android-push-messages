package com.example.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import com.example.agriservice.R;

@SuppressWarnings("deprecation")
public class MainInterface extends TabActivity {

	@Override
	public void onCreate( Bundle savedInstanceState){
		super.onCreate(savedInstanceState) ;
		
		
		TabHost tabHost = getTabHost() ;
		tabHost.addTab(tabHost.newTabSpec("TAB1")
				.setIndicator("今日消息",getResources().getDrawable(R.drawable.message))
				.setContent(new Intent(this, TodayMessageActivity.class)));
		
		tabHost.addTab(tabHost.newTabSpec("TAB2")
				.setIndicator("历史消息",getResources().getDrawable(R.drawable.history_message))
				.setContent(new Intent(this , HistoryMesageActivity.class ) ) ) ;
		
		tabHost.addTab(tabHost.newTabSpec("TAB3")
				.setIndicator("系统设置",getResources().getDrawable(R.drawable.setting))
				.setContent(new Intent(this, SetupActivity.class)) ) ;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		        // Group ID
				int groupId = 0;
				// The order position of the item
				int menuItemOrder = Menu.NONE;

				menu.add(groupId, 0, menuItemOrder, "退出").setIcon(R.drawable.icon);
				
				return true;
	}
	
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
