package com.example.serviceexample;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class contactAdapter extends ArrayAdapter<String[]> {
	
	private Context mContext;
	private List<String[]> contact;
	private TextView text1,text2;
	public ArrayList<Integer> selectedIds = new ArrayList<Integer>();

	public contactAdapter(Context context, int resource, List<String[]> objects) {
		super(context, resource, objects);
		mContext = context;
		contact = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ContactsActivity c = new ContactsActivity();
	    View v = convertView;
	    System.out.println("Getview"+position);
	    if (v == null) {

	        LayoutInflater vi;
	        vi = LayoutInflater.from(getContext());
	        v = vi.inflate(R.layout.custom_list, null);

	    }
	   
	    text1 = (TextView)v.findViewById(R.id.textView1);
	    text2 = (TextView)v.findViewById(R.id.textView2);
	    String[] a = contact.get(position);
	    text1.setText(a[0]);
	    text2.setText(a[1]);
	    System.out.println(selectedIds);
	    if(selectedIds.contains(position))
	    {
	    	
	    	v.setBackgroundColor(mContext.getResources().getColor(R.color.green500));
	    }
	    else
	    {
	    	v.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
	    }
		return v;
	    
	}
}
