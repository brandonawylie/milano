package com.codeday.milano;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.milano.R;

public class ViewPostActivity extends ActionBarActivity {
	Post post;

	// Hold a reference to the current animator,
	// so that it can be canceled mid-way.
	private Animator mCurrentAnimator;

	// The system "short" animation time duration, in milliseconds. This
	// duration is ideal for subtle animations or animations that occur
	// very frequently.
	private int mShortAnimationDuration = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_post);

		Bundle extras = getIntent().getExtras();
		post = (Post) extras.getSerializable("post");

		TextView titleView = (TextView) findViewById(R.id.viewpost_title);
		titleView.setText(post.getTitle());
		TextView locationView = (TextView) findViewById(R.id.viewpost_location);
		locationView.setText(post.getLocation());
		TextView dateView = (TextView) findViewById(R.id.viewpost_date);
		dateView.setText(post.getDate().toString());
		TextView descriptionView = (TextView) findViewById(R.id.viewpost_description);
		descriptionView.setText(post.getDescription());
		LinearLayout images = (LinearLayout) findViewById(R.id.viewpost_images);
		for (final String uri : post.getImages()) {
			final ImageButton ib = new ImageButton(this);

			final Bitmap bitmap = getbitpam(uri);
			ib.setImageBitmap(bitmap);
			images.addView(ib);

			ib.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					zoomImageFromThumb(ib, bitmap);

				}
			});
		}
		if (post.getImages().isEmpty()) {
			TextView tv = new TextView(this);
			tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 256));
			tv.setText("[No images]");
			images.addView(tv);
			tv.setGravity(Gravity.CENTER);
		}

		ImageView iv = (ImageView) findViewById(R.id.post_star);
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				post.starred = !post.starred;
				boolean star = post.starred;
				if (star) {
					ImageView iv = (ImageView) v;
					iv.setImageResource(R.drawable.starred);
				} else {
					ImageView iv = (ImageView) v;
					iv.setImageResource(R.drawable.unstarred);
				}

			}
		});
		if (post.starred) {
			iv.setImageResource(R.drawable.starred);
		} else {
			iv.setImageResource(R.drawable.unstarred);
		}
	}

	public Bitmap getbitpam(String path) {
		Bitmap imgthumBitmap = null;
		try {

			final int THUMBNAIL_SIZE = 256;

			FileInputStream fis = new FileInputStream(path);
			imgthumBitmap = BitmapFactory.decodeStream(fis);

			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap, THUMBNAIL_SIZE,
					THUMBNAIL_SIZE, false);
			imgthumBitmap = Bitmap.createBitmap(imgthumBitmap, 0, 0,
					imgthumBitmap.getWidth(), imgthumBitmap.getHeight(),
					matrix, true);

			ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
			imgthumBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
					bytearroutstream);

		} catch (Exception ex) {

		}
		return imgthumBitmap;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.viewpost_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.viewpost_reply:
			// TODO get the email, and open associated app
			Toast.makeText(ViewPostActivity.this, "reply", Toast.LENGTH_LONG)
					.show();
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			sendIntent.setType("text/plain");

			// sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new
			// String[]{"john@gmail.com"});
			sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					new String[] { post.getEmail() });
			startActivity(Intent.createChooser(sendIntent, "Email:"));

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void zoomImageFromThumb(final View thumbView, Bitmap bm) {
		// If there's an animation in progress, cancel it
		// immediately and proceed with this one.
		if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}

		// Load the high-resolution "zoomed-in" image.
		final ImageView expandedImageView = (ImageView) findViewById(R.id.expanded_image);
		expandedImageView.setImageBitmap(bm);

		// Calculate the starting and ending bounds for the zoomed-in image.
		// This step involves lots of math. Yay, math.
		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();

		// The start bounds are the global visible rectangle of the thumbnail,
		// and the final bounds are the global visible rectangle of the
		// container
		// view. Also set the container view's offset as the origin for the
		// bounds, since that's the origin for the positioning animation
		// properties (X, Y).
		thumbView.getGlobalVisibleRect(startBounds);
		findViewById(R.id.container).getGlobalVisibleRect(finalBounds,
				globalOffset);
		startBounds.offset(-globalOffset.x, -globalOffset.y);
		finalBounds.offset(-globalOffset.x, -globalOffset.y);

		// Adjust the start bounds to be the same aspect ratio as the final
		// bounds using the "center crop" technique. This prevents undesirable
		// stretching during the animation. Also calculate the start scaling
		// factor (the end scaling factor is always 1.0).
		float startScale;
		if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds
				.width() / startBounds.height()) {
			// Extend start bounds horizontally
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		} else {
			// Extend start bounds vertically
			startScale = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}

		// Hide the thumbnail and show the zoomed-in view. When the animation
		// begins, it will position the zoomed-in view in the place of the
		// thumbnail.
		thumbView.setAlpha(0f);
		expandedImageView.setVisibility(View.VISIBLE);

		// Set the pivot point for SCALE_X and SCALE_Y transformations
		// to the top-left corner of the zoomed-in view (the default
		// is the center of the view).
		expandedImageView.setPivotX(0f);
		expandedImageView.setPivotY(0f);

		// Construct and run the parallel animation of the four translation and
		// scale properties (X, Y, SCALE_X, and SCALE_Y).
		AnimatorSet set = new AnimatorSet();
		set.play(
				ObjectAnimator.ofFloat(expandedImageView, View.X,
						startBounds.left, finalBounds.left))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
						startBounds.top, finalBounds.top))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
						startScale, 1f))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y,
						startScale, 1f));
		set.setDuration(mShortAnimationDuration);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mCurrentAnimator = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mCurrentAnimator = null;
			}
		});
		set.start();
		mCurrentAnimator = set;

		// Upon clicking the zoomed-in image, it should zoom back down
		// to the original bounds and show the thumbnail instead of
		// the expanded image.
		final float startScaleFinal = startScale;
		expandedImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mCurrentAnimator != null) {
					mCurrentAnimator.cancel();
				}

				// Animate the four positioning/sizing properties in parallel,
				// back to their original values.
				AnimatorSet set = new AnimatorSet();
				set.play(
						ObjectAnimator.ofFloat(expandedImageView, View.X,
								startBounds.left))
						.with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
								startBounds.top))
						.with(ObjectAnimator.ofFloat(expandedImageView,
								View.SCALE_X, startScaleFinal))
						.with(ObjectAnimator.ofFloat(expandedImageView,
								View.SCALE_Y, startScaleFinal));
				set.setDuration(mShortAnimationDuration);
				set.setInterpolator(new DecelerateInterpolator());
				set.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}

					@Override
					public void onAnimationCancel(Animator animation) {
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}
				});
				set.start();
				mCurrentAnimator = set;
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent resultIntent = new Intent();
			setResult(Activity.RESULT_OK, resultIntent);
			resultIntent.putExtra("post", post);
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}
