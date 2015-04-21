package com.example.serviceexample;

import java.util.ArrayList;

import com.example.serviceexample.FeedReaderContract.FeedEntry;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ContactsActivity extends ActionBarActivity {
	
	private ListView contactList;
	private ArrayList<String[]> items;
	private ArrayList<Integer> positions;
	//private ArrayAdapter<String> contactAdapter;
	private contactAdapter cAdapter;
	private FeedReaderDbHelper mDbHelper;
	private ArrayList<String> contactNameList,contactNoList = new ArrayList<String>();
	private ArrayList<String> tmp  = new ArrayList<String>();
	private SQLiteDatabase db;
	private View view1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		
		
		ActionBar bar = this.getSupportActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple500)));
		mDbHelper = new FeedReaderDbHelper(this);
		 db = mDbHelper.getWritableDatabase();
		
		items = new ArrayList<String[]>();
		Intent intent = getIntent();
		contactNameList = intent.getStringArrayListExtra("contactName");
		contactNoList = intent.getStringArrayListExtra("contactNo");

		
		contactList = (ListView)findViewById(R.id.listView1);
		positions = new ArrayList<Integer>();
		cAdapter = new contactAdapter(this,android.R.layout.simple_list_item_activated_1,items);
		contactList.setAdapter(cAdapter);
		
		for(int i=0;i<contactNoList.size();i++)
		{
			String[] set = {contactNameList.get(i),contactNoList.get(i)};
			cAdapter.add(set);
			
		}
		contactList.setOnItemClickListener(listener);	
        
	}

	private OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ArrayList<Integer> selectedIds = cAdapter.selectedIds;
			

			if(selectedIds.contains(position))
			{
				selectedIds.remove(selectedIds.indexOf(position));
				view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
			}
			else
			{
				selectedIds.add(position);
				view.setBackgroundColor(getResources().getColor(R.color.yellowAccent1));
			}
			cAdapter.notifyDataSetChanged();
		}
		
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts, menu);
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
	public void addContacts(View view)
	{
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);  
		startActivityForResult(intent, 1);
	}
	
	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String phoneNo = null ;
		String ContactName = null;
		if(data !=null)
		{
			Uri uri = data.getData();
			Cursor cursor = getContentResolver().query(uri, null, null, null, null);
			cursor.moveToFirst();
	
			int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
			int  p =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
			
			int contactNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			
			phoneNo = cursor.getString(phoneIndex);
			ContactName = cursor.getString(contactNameIndex);
			
			String[] contactSet = {ContactName,phoneNo};
			
			
			String o = cursor.getString(p);
			
			if(contactNoList.contains(phoneNo) || tmp.contains(phoneNo))
			{
				Toast.makeText(this, "Contact already exist", Toast.LENGTH_SHORT).show();
			}
			else
			{
				cAdapter.add(contactSet);
				Log.i("ContactsActivity","Elseloop");
				System.out.println(contactNoList);
			
			Log.i("ContactsActivity", phoneNo);
			Log.i("ContactsActivity", ContactName);
			Log.i("ContactsActivity", o);
			ContentValues values = new ContentValues();
			values.put(FeedEntry.COLUMN_NAME_CNAME, ContactName);
			values.put(FeedEntry.COLUMN_NAME_NUMBER, phoneNo);
			long newRowId = db.insert(
			         FeedEntry.TABLE_NAME,
			         null,
			         values);
			System.out.println(newRowId);
			tmp.add(phoneNo);
			}
		}
			
	}
	public void removeContacts(View view)
	{
		
		String[] selected_phoneNo;
		ArrayList<String> selectedNoList = new ArrayList<String>();
		String[] selectedNo;
		SparseBooleanArray selected = contactList.getCheckedItemPositions();
		int count = cAdapter.getCount();
		Log.i("ContactAdapter", cAdapter.getCount()+"");
		Log.i("boolean", selected.size()+"");
		System.out.println(contactList.getCheckedItemPositions());
		
		for(int i=count-1;i>=0;i--)
		{
			System.out.println(selected.valueAt(i));
			if(selected.get(i))
			{
				System.out.println(i);
				String[] contact = cAdapter.getItem(i);
				selectedNoList.add(contact[1]); 
				cAdapter.remove(contact);
				int pos = contactNoList.indexOf(contact[1]);
				int pos1 = tmp.indexOf(contact[1]);
				
				if(pos>=0)
					contactNoList.remove(pos);
				if(pos1>=0)
					tmp.remove(pos1);			
			}
		}
		contactList.clearChoices();
		cAdapter.selectedIds.clear();
		cAdapter.notifyDataSetChanged();
		
		
		for(int i=0;i<selectedNoList.size();i++)
		{
			
			String selection = FeedEntry.COLUMN_NAME_NUMBER + " = '"+selectedNoList.get(i)+"'";
			db.delete(FeedEntry.TABLE_NAME, selection, null);
		}
		
		// Specify arguments in placeholder order.
		
		// Issue SQL statement.
		
		
	/*	System.out.println(positions.size());
		
		if(contactAdapter !=null) {
			
			for(int i=0;i<positions.size();i++) 
			{
				
			}
		}
		positions.clear(); */
	}
	
	@Override
	public void onResume()
	{
		
		super.onResume();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		System.out.println("OnDestroy");
		cAdapter.clear();
		cAdapter.notifyDataSetChanged();
		items.clear();
		contactNoList.clear();
		contactNameList.clear();
		tmp.clear();
	}
}
