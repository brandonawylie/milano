package com.codeday.milano;

import java.util.ArrayList;

import com.example.milano.R;

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
	PostAdapter adapter;
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
		adapter = new PostAdapter(this, favorites);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v("MAIN", "clicked button");
				Intent i = new Intent(getApplicationContext(),
						ViewPostActivity.class);
				i.putExtra("post", favorites.get(position));
				startActivityForResult(i, MainActivity.VIEW_POST_REQUEST);
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			for (int i = 0; i < adapter.values.size(); i++) {
//				Post cur = adapter.values.get(i);
//				if (!cur.starred) {
//					adapter.values.remove(cur);
//				}
//			}
			Intent resultIntent = new Intent();
			setResult(Activity.RESULT_OK, resultIntent);
			resultIntent.putExtra("posts", adapter.values);
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MainActivity.VIEW_POST_REQUEST) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				Post post = (Post) data.getExtras().getSerializable("post");
				for (int i = 0; i < adapter.values.size(); i++) {
					if (adapter.values.get(i).equals(post))
						adapter.values.get(i).starred = post.starred;
				}
				adapter.notifyDataSetChanged();
				// TODO: Reload list view
			}
		}
	}
}
