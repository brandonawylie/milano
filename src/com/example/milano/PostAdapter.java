package com.example.milano;

import java.sql.Date;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PostAdapter extends BaseAdapter {
	private Activity activity;
	final ArrayList<Post> values;
	private static LayoutInflater inflater = null;

	public PostAdapter(Activity a, ArrayList<Post> values) {
		activity = a;
		this.values = values;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.post_list_item1, parent, false);
		ImageView iv = (ImageView) convertView.findViewById(R.id.post_star);
		final int pos = position;
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				values.get(pos).starred = !values.get(pos).starred;
				if (values.get(pos).starred) {
					ImageView iv = (ImageView) v;
					iv.setImageResource(R.drawable.starred);
				} else {
					ImageView iv = (ImageView) v;
					iv.setImageResource(R.drawable.unstarred);
				}
				
			}
		});
		if (values.get(pos).starred) {
			iv.setImageResource(R.drawable.starred);
		} else {
			iv.setImageResource(R.drawable.unstarred);
		}
		TextView titleView = (TextView) convertView
				.findViewById(R.id.post_title);
		TextView locationView = (TextView) convertView
				.findViewById(R.id.post_location);
		TextView dateView = (TextView) convertView.findViewById(R.id.post_date);
		TextView priceView = (TextView) convertView
				.findViewById(R.id.post_price);
		Post p = values.get(position);
		titleView.setText(p.getTitle());
		locationView.setText(p.getLocation());
		Date d1 = p.getDate();
		java.util.Date tmp = new java.util.Date();
		Date d2 = new Date(tmp.getTime());
		long diff = d2.getTime() - d1.getTime();
		 
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		
		StringBuilder date = new StringBuilder();
		date.append("posted ");
		if (diffDays > 0){
			date.append("24+ hrs ");
		} else if (diffHours > 0){
			if (diffHours == 1)
				date.append("" + diffHours + " hr ");
			else
				date.append("" + diffHours + " hrs ");
			
			if (diffMinutes > 0){
				if (diffMinutes == 1)
					date.append("" + diffMinutes + " min ");
				else
					date.append("" + diffMinutes + " mins ");
			}
		}
		
		
		date.append("ago");
		dateView.setText(date.toString());
		priceView.setText("$" + p.getPrice());
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return values.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

}
