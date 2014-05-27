package com.codeday.milano;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.sql.Date;
import java.util.ArrayList;

import com.example.milano.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	// List<Post> posts = new ArrayList<>();
	ArrayList<Post> posts;
	ListView list;
	PostAdapter adapter;
	static final int CREATE_POST_REQUEST = 1;
	static final int FAVORITES_REQUEST = 2;
	static final int VIEW_POST_REQUEST = 3;

	static final String postsStorageFile = "posts.s";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * Placeholder variables
		 * TODO create a way to save data to disk
		 */
		posts = new ArrayList<Post>();
		for (int i = 0; i < 11; i++) {
			Post p = new Post();
			if (i % 2 == 0)
				p.setTitle("Fizz " + i);
			else if (i % 3 == 0)
				p.setTitle("Buzz " + i);
			else
				p.setTitle("Fizz Buzz " + i);
			p.setCategory("Awesome");
			p.setLocation("Seattle, WA");
			p.setDate(new Date(i * 1000));
			p.setDescription("Selling, or jobs, or rides or whatever.");
			p.setPrice(i*10);
			p.setEmail("john.doe@gmail.com");
			posts.add(p);
		}
		list = (ListView) findViewById(R.id.main_listview);
		adapter = new PostAdapter(this, posts);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v("MAIN", "clicked button");

				Intent i = new Intent(getApplicationContext(),
						ViewPostActivity.class);
				i.putExtra("post", posts.get(position));
				startActivityForResult(i, VIEW_POST_REQUEST);

			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.main_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_post:
			Intent i = new Intent(getApplicationContext(),
					CreatePostActivity.class);
			startActivityForResult(i, CREATE_POST_REQUEST);
			return true;
		case R.id.main_favorites:
			Intent fave = new Intent(getApplicationContext(),
					FavoritesActivity.class);
			fave.putExtra("posts", posts);
			startActivityForResult(fave, FAVORITES_REQUEST);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		//return super.onOptionsItemSelected(item);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request it is that we're responding to
		if (requestCode == CREATE_POST_REQUEST) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				adapter.values.add((Post) data.getExtras().getSerializable(
						"post"));
				adapter.notifyDataSetChanged();
			}
		} else if (requestCode == FAVORITES_REQUEST) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				@SuppressWarnings("unchecked")
				ArrayList<Post> favorites = (ArrayList<Post>) data.getExtras()
						.getSerializable("posts");
				for (int i = 0; i < adapter.values.size(); i++) {
					for (int j = 0; j < favorites.size(); j++) {
						if (adapter.values.get(i).equals(favorites.get(j)))
							adapter.values.get(i).starred = favorites.get(j).starred;
					}
				}
				adapter.notifyDataSetChanged();
				// TODO: Reload list view
			}
		} else if (requestCode == VIEW_POST_REQUEST) {
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

	public void writeOutPosts() {
		FileOutputStream fos = null;
		try {
			fos = openFileOutput(postsStorageFile, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObjectOutputStream os = null;
		try {
			os = new ObjectOutputStream(fos);
			os.writeObject(adapter.values);
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readInPosts() {
		FileInputStream fis = null;
		try {
			fis = openFileInput(postsStorageFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObjectInputStream is = null;
		ArrayList<Post> storedPosts = null;
		try {
			is = new ObjectInputStream(fis);
			storedPosts = (ArrayList<Post>) is.readObject();
			is.close();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (storedPosts == null)
			return;
		posts = storedPosts;
	}
}