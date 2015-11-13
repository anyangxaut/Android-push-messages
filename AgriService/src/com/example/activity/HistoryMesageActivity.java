package com.example.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.agriservice.R;

public class HistoryMesageActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_mesage);
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
