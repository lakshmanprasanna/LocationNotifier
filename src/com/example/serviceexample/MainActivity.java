package com.example.serviceexample;

import java.util.ArrayList;

import com.example.serviceexample.FeedReaderContract.FeedEntry;
import com.example.serviceexample.R.color;

import android.support.v7.app.ActionBarActivity;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity {

	private Intent intent,intent1;
	private LocationHelper locationHelper;
	private static final String TAG = "MainActivity";
	private boolean appstatus = false;
	private View v;
	private TextView textView1,textView2;
	private android.support.v7.app.ActionBar bar;
	private ListView listView;
	
	private ArrayList<String> settings = new ArrayList<String>();
	private ArrayList<String> contactName = new ArrayList<String>();
	private ArrayList<String> contactNo = new ArrayList<String>();
	
	private ValueAnimator colorAnimation,colorAnimation1,colorAnimation2,colorAnimation3;
	
	private OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(position == 0)
			{

				intent1.putStringArrayListExtra("contactName", contactName);
				intent1.putStringArrayListExtra("contactNo", contactNo);
				startActivity(intent1);
				 contactName.clear();
				 contactNo.clear();
			}
			
		}

	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		intent = new Intent(this, MainService.class);
		
		intent1 = new Intent(this,ContactsActivity.class);
		
		textView2 = (TextView)findViewById(R.id.textView6);
		
		locationHelper = new LocationHelper();
		
		listView = (ListView)findViewById(R.id.listView2);
		settings.add("Edit contact List");
		settings.add("Settings");
		settings.add("Help");
		settings.add(" ");
		ArrayAdapter ne = new ArrayAdapter(this, android.R.layout.simple_list_item_1,android.R.id.text1,settings);
		listView.setOnItemClickListener(listener);
		listView.setAdapter(ne);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void StartService(View view)
	{
		locationHelper.initialize(this);
		
		if(locationHelper.isAvailable())
		{
			Log.i(TAG, "Gps Available");
		}
		else
			Log.i(TAG, "Gps not available");
		
		
		if(locationHelper.isGpsEnabled())
		{
			Log.i(TAG, "Gps Enabled");
		}
		else
			Log.i(TAG, "Gps diabled");
		
		if(locationHelper.isNetworkEnabled())
		{
			Log.i(TAG, "Network Enabled");
		}
		else
			Log.i(TAG, "Network diabled");
		Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
		startService(intent);
		
	}
	
	public void StopService(View view)
	{
		Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();	
		stopService(intent);
	}
	public void openContact(View view)
	{
		Intent intent = new Intent(this,ContactsActivity.class);
		startActivity(intent);
	}
	
	 @SuppressLint("NewApi")
	public void startActivity(View view)
	  {
		
		  if(appstatus)
		  {
			  Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
				startService(intent);
				colorAnimation.start();
			  textView1.setText("APP IS ACTIVE");
			  appstatus = false;
			  colorAnimation2.start();
		  }
		  else
		  {
			  colorAnimation1.start();
			  Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();	
				stopService(intent);
			//  v.setBackgroundColor(getResources().getColor(R.color.pressed_color));
			  textView1.setText("APP IS NOT ACTIVE");
			  colorAnimation3.start();
			  appstatus = true;
		  }
	  }
	 
	 @SuppressLint("NewApi")
	@Override
	 protected void onResume()
	 {
		 super.onResume();
		 
		 System.out.println("OnResume");
		 bar = this.getSupportActionBar();
		 MainFragment f = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.mainfragment);
		 v = f.getView();
		 SharedPreferences sharedPref = this.getSharedPreferences("Running",Context.MODE_PRIVATE);
		 int defaultValue = sharedPref.getInt("RunningFlag", 10);
		 textView1 = (TextView)findViewById(R.id.textView1);
		 
		 
		 Integer colorFrom = getResources().getColor(R.color.orange400);
		 Integer colorTo = getResources().getColor(R.color.green400);
		 
		 Integer colorFrom1 = getResources().getColor(R.color.orange500);
		 Integer colorTo1 = getResources().getColor(R.color.green500);
		 
		 
		 colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
		 colorAnimation1 = ValueAnimator.ofObject(new ArgbEvaluator(), colorTo, colorFrom);
		 
		 colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom1, colorTo1);
		 colorAnimation3 = ValueAnimator.ofObject(new ArgbEvaluator(), colorTo1, colorFrom1);
		 
		 colorAnimation.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				v.setBackgroundColor((Integer)animation.getAnimatedValue());
				
			}
			 
		 });
		 
		 colorAnimation1.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					// TODO Auto-generated method stub
					
					v.setBackgroundColor((Integer)animation.getAnimatedValue());
				}
				 
			 });
		 
		 colorAnimation2.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					// TODO Auto-generated method stub
					 bar.setBackgroundDrawable(new ColorDrawable((Integer)animation.getAnimatedValue()));
					  textView2.setBackgroundColor((Integer)animation.getAnimatedValue());
					
				}
				 
			 });
		 
		 colorAnimation3.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					// TODO Auto-generated method stub
					
					 bar.setBackgroundDrawable(new ColorDrawable((Integer)animation.getAnimatedValue()));
					  textView2.setBackgroundColor((Integer)animation.getAnimatedValue());
					
				}
				 
			 });
		 
	
		// colorAnimation.start();
		 
		 
		 
		 if(defaultValue == 1 )
		 {
			 v.setBackgroundColor(getResources().getColor(R.color.green400));
			 textView1.setText("APP IS ACTIVE");
			  appstatus = false;
			  bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.green700)));
			  textView2.setBackgroundColor(getResources().getColor(R.color.green500));
		 }
		 else
		 {
			 v.setBackgroundColor(getResources().getColor(R.color.orange400));
			 textView1.setText("APP IS NOT ACTIVE");
			 bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange700)));
			 textView2.setBackgroundColor(getResources().getColor(R.color.orange500));
			  appstatus = true;
		 }
		
	 
	 FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this);
	 SQLiteDatabase db = mDbHelper.getReadableDatabase();

	// Define a projection that specifies which columns from the database
	// you will actually use after this query.
	String[] projection = {
	    FeedEntry._ID,
	    FeedEntry.COLUMN_NAME_CNAME,
	    FeedEntry.COLUMN_NAME_NUMBER,
	    };
	
	try {
	Cursor cur = db.query(
		    FeedEntry.TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    null,                                // The columns for the WHERE clause
		    null,                            // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                // The sort order
		    );
	
	if(cur !=null)
	{
	  cur.moveToFirst();
      int size = cur.getCount();
      for(int i=0;i<size;i++)
      {
    	  contactName.add(cur.getString(1));
    	  contactNo.add(cur.getString(2));
    	  cur.moveToNext();
      }
	}
	
	}
	catch(Exception e)
	{
		
	}
	
	
   }
	 
	 
	 @Override 
	 public void onDestroy()
	 {
		 super.onDestroy();
		 contactName.clear();
		 contactNo.clear();
	 }


}