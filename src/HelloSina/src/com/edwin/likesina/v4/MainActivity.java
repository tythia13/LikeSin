package com.edwin.likesina.v4;
 
 

import com.edwin.likesina.R;
import com.edwin.likesina.R.id;
import com.edwin.likesina.R.layout;
import com.edwin.likesina.R.menu;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FragmentTabHost mTabHost;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate1");
		setContentView(R.layout.activity_main); 
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("simple").setIndicator("Simple"),
                FragmentStackSupport.CountingFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("contacts").setIndicator("Contacts"),
                LoaderCursorSupport.CursorLoaderListFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("custom").setIndicator("Custom"),
                LoaderCustomSupport.AppListFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("throttle").setIndicator("Throttle"),
                LoaderThrottleSupport.ThrottledLoaderListFragment.class, null);
        Log.i(TAG,"onCreate2");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    protected void onStart()
    {
        Log.i(TAG,"onStart");
        super.onStart();
    }
    
	@Override
	protected void onResume()
	{
	    Log.i(TAG,"onResume");
	    super.onResume();
	}
	
	@Override
	protected void onPause()
	{
        Log.i(TAG,"onPause");
	    super.onPause();
	}
	
	@Override
	protected void onStop()
	{
	    Log.i(TAG,"onStop");
	    super.onStop();
	}
	
	@Override
	protected void onDestroy()
	{
	    Log.i(TAG,"onDestroy");
	    super.onDestroy();
	}

}
