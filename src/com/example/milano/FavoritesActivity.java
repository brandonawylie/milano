package com.example.milano;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class FavoritesActivity extends Activity {
	ArrayList<Post> favorites;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorites);

		Bundle extras = getIntent().getExtras();

		ArrayList<Post> posts = (ArrayList<Post>) extras
				.getSerializable("posts");
		favorites = new ArrayList<Post>();
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.favorites_layout);
		for (int i = 0; i < posts.size(); i++) {
			Post cur = posts.get(i);
			if (cur.starred) {
				favorites.add(cur);
			}
		}

		ListView list = (ListView) findViewById(R.id.favorites_list);
		list.setAdapter(new PostAdapter(this, favorites));
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v("MAIN", "clicked button");
				for (int i = 0; i < favorites.size(); i++) {
					Post cur = favorites.get(i);
					if (!cur.starred) {
						favorites.remove(cur);
					}
				}
				Intent i = new Intent(getApplicationContext(), ViewPostActivity.class);
				i.putExtra("post", favorites.get(position));
				startActivity(i);
			}
		});
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent resultIntent = new Intent();
			setResult(Activity.RESULT_OK, resultIntent);
			resultIntent.putExtra("posts", favorites);
			finish();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
}
