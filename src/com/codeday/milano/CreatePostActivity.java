package com.codeday.milano;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.milano.R;

public class CreatePostActivity extends ActionBarActivity {
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;

	// directory name to store captured images and videos
	private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
	private Uri fileUri; // file url to store image/video
	private ArrayList<Uri> fileUris = new ArrayList<Uri>();
	String[] category = new String[] { "Category", "Electronics", "Jobs",
			"Rides" };
	
	private Animator mCurrentAnimator;

	// The system "short" animation time duration, in milliseconds. This
	// duration is ideal for subtle animations or animations that occur
	// very frequently.
	private int mShortAnimationDuration = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_post);

		Spinner spinner = (Spinner) findViewById(R.id.createpost_category);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, category);
		adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spinner.setAdapter(adapter);

		OnClickListener tlistener = new OnClickListener() {

			@Override
			public void onClick(View v) {
			}

		};
		EditText title = (EditText) findViewById(R.id.createpost_title);
		title.setOnClickListener(tlistener);

		EditText location = (EditText) findViewById(R.id.createpost_location);
		location.setOnClickListener(tlistener);

		EditText price = (EditText) findViewById(R.id.createpost_price);
		price.setOnClickListener(tlistener);

		EditText description = (EditText) findViewById(R.id.createpost_description);
		description.setOnClickListener(tlistener);

		OnClickListener ulistener = new OnClickListener() {

			public void onClick(View v) {
				Button b = (Button) v;
				captureImage();
			}
		};

		Button upload = (Button) findViewById(R.id.createpost_upload);
		upload.setOnClickListener(ulistener);

	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.createpost_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.createpost_submit:
			EditText title = (EditText) findViewById(R.id.createpost_title);
			EditText location = (EditText) findViewById(R.id.createpost_location);
			EditText price = (EditText) findViewById(R.id.createpost_price);
			EditText description = (EditText) findViewById(R.id.createpost_description);
			EditText email = (EditText) findViewById(R.id.createpost_email);
			Spinner spinner = (Spinner) findViewById(R.id.createpost_category);
			Post p = new Post();
			StringBuilder err = new StringBuilder();
			if (price.getText().toString().isEmpty()) {
				err.append("Please enter a price\n");
			}
			if (title.getText().toString().isEmpty()) {
				err.append("Please enter a title\n");
			}
			if (location.getText().toString().isEmpty()) {
				err.append("Please enter a location\n");
			}
			if (description.getText().toString().isEmpty()) {
				err.append("Please enter a description\n");
			}
			if (email.getText().toString().isEmpty()) {
				err.append("Please enter an email\n");
			}
			if (spinner.getSelectedItemPosition() == 0) {
				err.append("Please enter a category\n");
			}
			if (err.length() != 0) {
				Toast.makeText(CreatePostActivity.this, err.substring(0, err.length() - 1), Toast.LENGTH_LONG).show();
				return super.onOptionsItemSelected(item);
			}
			p.setTitle(title.getText().toString());
			p.setDescription(description.getText().toString());
			p.setPrice(Integer.parseInt(price.getText().toString()));
			p.setLocation(location.getText().toString());
			p.setEmail(email.getText().toString());
			java.util.Date date = new java.util.Date();
			p.setDate(new Date(date.getTime()));
			p.setCategory(category[spinner.getSelectedItemPosition()]);
			
			for (int i = 0; i < fileUris.size(); i++)
				p.addImage(fileUris.get(i).getPath());
			Intent resultIntent = new Intent();
			setResult(Activity.RESULT_OK, resultIntent);
			resultIntent.putExtra("post", p);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		//return super.onOptionsItemSelected(item);
	}

	private boolean isDeviceSupportCamera() {
		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/*
	 * Capturing Camera Image will lauch camera app requrest image capture
	 */
	private void captureImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// start the image capture Intent
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	}

	/**
	 * Receiving activity result method will be called after closing the camera
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if the result is capturing Image
		if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// successfully captured the image
				// display it in image view
				previewCapturedImage();
			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled Image capture
				Toast.makeText(getApplicationContext(),
						"User cancelled image capture", Toast.LENGTH_SHORT)
						.show();
			} else {
				// failed to capture image
				Toast.makeText(getApplicationContext(),
						"Sorry! Failed to capture image", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	/*
	 * Display image from a path to ImageView
	 */
	private void previewCapturedImage() {
		// iv.setText(fileUri.toString());
		try {
			LinearLayout layout = (LinearLayout) findViewById(R.id.createpost_imageholder);
			final ImageButton ib = new ImageButton(this);
			ib.setVisibility(View.VISIBLE);
			
			// downsizing image as it throws OutOfMemory Exception for larger
			// images
			final Bitmap bitmap = getbitpam(fileUri.getPath(), 256);
			//final Bitmap bitmap = Bitmap.createBitmap(bbitmap, 0, 0, bbitmap.getWidth(), bbitmap.getHeight(), matrix, true);
			ib.setImageBitmap(bitmap);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(fileUri.getPath());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	          //final Bitmap imgthumBitmap = BitmapFactory.decodeStream(fis);
			ib.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					zoomImageFromThumb(ib,
							getbitpam(fileUri.getPath(), 1024));
				}
			});
			fileUris.add(fileUri);
			layout.addView(ib);
			Toast.makeText(CreatePostActivity.this, fileUris.toString(),
					Toast.LENGTH_LONG).show();
		} catch (NullPointerException e) {
			e.printStackTrace();
			Toast.makeText(CreatePostActivity.this, "fail",
					Toast.LENGTH_LONG).show();
		}
	}
	
	public Bitmap getbitpam(String path, int size){
	    Bitmap imgthumBitmap=null;
	     try    
	     {

	         final int THUMBNAIL_SIZE = 256;

	         FileInputStream fis = new FileInputStream(path);
	          imgthumBitmap = BitmapFactory.decodeStream(fis);
	          Matrix matrix = new Matrix();
	          matrix.postRotate(90);
	         imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap,
	                size, size, false);
	         imgthumBitmap = Bitmap.createBitmap(imgthumBitmap, 0, 0, imgthumBitmap.getWidth(), imgthumBitmap.getHeight(), matrix, true);
	        ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream(); 
	        imgthumBitmap.compress(Bitmap.CompressFormat.JPEG, 100,bytearroutstream);


	     }
	     catch(Exception ex) {

	     }
	     return imgthumBitmap;
	}

	/**
	 * Creating file uri to store image/video
	 */
	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/*
	 * returning image / video
	 */
	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new java.util.Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	/**
	 * Here we store the file url as it will be null after returning from camera
	 * app
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// save file url in bundle as it will be null on scren orientation
		// changes
		outState.putSerializable("file_uris", fileUris);
	}
	
	private void zoomImageFromThumb(final View thumbView, Bitmap bm) {
		// If there's an animation in progress, cancel it
		// immediately and proceed with this one.
		if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}

		// Load the high-resolution "zoomed-in" image.
		final ImageView expandedImageView = (ImageView) findViewById(R.id.expanded_image_1);
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
		findViewById(R.id.container_1).getGlobalVisibleRect(finalBounds,
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

	/*
	 * Here we restore the fileUri again
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// get the file url
		fileUris = savedInstanceState.getParcelable("file_uris");
	}
	Toast t;
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (t == null)
			t = Toast.makeText(this, "There are unsaved changes press back again to exit", Toast.LENGTH_SHORT);
		
		if (t != null && t.getView().isShown())
			return super.onKeyDown(keyCode, event);
		
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        t.show();
			return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
}
