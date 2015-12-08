package com.qrpatrol.patrol;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.cast.Cast;
import com.qrpatrol.activity.DashboardActivity;
import com.qrpatrol.android.R;
import com.qrpatrol.async_task.AsyncResponseForQRPatrol;
import com.qrpatrol.async_task.AsyncTaskForQRPatrol;
import com.qrpatrol.modal.CheckPoint;
import com.qrpatrol.modal.Officer;
import com.qrpatrol.schedule.ScheduleListActivity;
import com.qrpatrol.util.QRParser;
import com.qrpatrol.util.QRPatrolDatabaseHandler;
import com.qrpatrol.util.Util;

public class ScanPatroluserActivity extends Activity implements
		OnClickListener, AsyncResponseForQRPatrol {
	TextView txtHeader, qrcode, checking_point, snap_picture , notes;
	EditText note_text;
	Button submit_button;
	Intent intent;
	static String  encodedString, getNotes, encode_getNotes;
	private int setimgeui = 0;
	ImageView user_image;
	private QRPatrolDatabaseHandler dbHandler;
	private CheckPoint checkPoint;
	private String patrolID;
	private QRParser qrParser;
	private SharedPreferences patrolPrefs, appPrefs;
	private ImageView menu;
	// Activity request codes
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

	public static final int MEDIA_TYPE_IMAGE = 1;

	// directory name to store captured images and videos
	private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

	private Uri fileUri; // file url to store image/video
	byte[] byteArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanpatroluser);
		intent = getIntent();
		checkPoint = (CheckPoint)intent.getParcelableExtra("checkpoint");

		patrolID = intent.getStringExtra("patrolID");
		
		SetUI();

	}

	private void SetUI() {
		// TODO Auto-generated method stub
		menu = (ImageView)findViewById(R.id.menu);
		menu.setVisibility(View.GONE);
		
		patrolPrefs = getSharedPreferences("patrol_prefs", MODE_PRIVATE);
		appPrefs = getSharedPreferences("qrpatrol_app_prefs", MODE_PRIVATE);
		qrParser = new QRParser(ScanPatroluserActivity.this);
		
		txtHeader = (TextView) findViewById(R.id.txtHeader);
		snap_picture = (TextView) findViewById(R.id.snap_picture);
		qrcode = (TextView) findViewById(R.id.qrcode);
		checking_point = (TextView) findViewById(R.id.checking_point);
		submit_button = (Button) findViewById(R.id.submit_button);
		user_image = (ImageView) findViewById(R.id.user_image);
		note_text = (EditText) findViewById(R.id.note_text);

		notes = (TextView)findViewById(R.id.cpnotes);
		snap_picture.setOnClickListener(this);
		submit_button.setOnClickListener(this);
		txtHeader.setText("Scan Patrol User");
		checking_point.setText(checkPoint.getName());
		notes.setText(checkPoint.getNotes());
		
		SpannableString content = new SpannableString("Snap Your Picture");
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		snap_picture.setText(content);
		
		Util.alertMessage(ScanPatroluserActivity.this, checkPoint.getNotes());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.snap_picture:
			// record video
			captureImage();
			break;

		case R.id.submit_button:
			getNotes = note_text.getText().toString();
			/*try {
				encode_getNotes = URLEncoder.encode(getNotes, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			if (setimgeui == 1) {
				if (getNotes.length() > 0) {
					if (Util.isNetworkAvailable(ScanPatroluserActivity.this)) {

						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

						nameValuePairs.add(new BasicNameValuePair("patrol_id",
								patrolID));
						nameValuePairs.add(new BasicNameValuePair("officer_id",
								DashboardActivity.officer.getOfficerId()));
						nameValuePairs.add(new BasicNameValuePair("shift_id",
								DashboardActivity.officer.getShiftId()));

						nameValuePairs.add(new BasicNameValuePair("event_name",
								"SCAN"));
						nameValuePairs.add(new BasicNameValuePair("latitude",
								String.valueOf(DashboardActivity.myLat)));
						nameValuePairs.add(new BasicNameValuePair("longitude",
								String.valueOf(DashboardActivity.myLon)));

						nameValuePairs.add(new BasicNameValuePair(
								"checkpoint_id", checkPoint.getCheckPointId()));

						nameValuePairs.add(new BasicNameValuePair("img",
								encodedString));
						nameValuePairs.add(new BasicNameValuePair("notes",
								getNotes));

						AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(
								ScanPatroluserActivity.this, "report-event",
								nameValuePairs, true, "Please wait...");
						mLogin.delegate = (AsyncResponseForQRPatrol) ScanPatroluserActivity.this;
						mLogin.execute();
					} else {
						Util.alertMessage(ScanPatroluserActivity.this,
								"Please check your internet connection");
					}

				} else {

					Toast.makeText(getApplicationContext(),
							"Please enter notes", Toast.LENGTH_LONG)
							.show();
				}

			} else {
				Toast.makeText(getApplicationContext(),
						"Please snap your picture", Toast.LENGTH_LONG).show();
			}

			break;
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
		try {
			// hide video preview

			user_image.setVisibility(View.VISIBLE);

			// bimatp factory
			BitmapFactory.Options options = new BitmapFactory.Options();

			// downsizing image as it throws OutOfMemory Exception for larger
			// images
			options.inSampleSize = 8;

			final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
					options);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byteArray = stream.toByteArray();
			// Encode Image to String
			encodedString = Base64.encodeToString(byteArray, 0);
			Log.d("", "encodedString" + encodedString);
			setimgeui = 1;
			user_image.setImageBitmap(ExifUtils.rotateBitmap(fileUri.getPath(), bitmap));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/*
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
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");

		} else {
			return null;
		}

		return mediaFile;
	}

	/*
	 * Here we store the file url as it will be null after returning from camera
	 * app
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// save file url in bundle as it will be null on scren orientation
		// changes
		outState.putParcelable("file_uri", fileUri);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// get the file url
		fileUri = savedInstanceState.getParcelable("file_uri");
	}

	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		if(methodName.equals("report-event")){
			String response = qrParser.getEventResponse(output);
			if(!response.equals("failure")){
				
				Editor editor = patrolPrefs.edit();
				editor.putString("eventName", "SCAN");
				editor.commit();
				
				Util.showToast(ScanPatroluserActivity.this, "Event submitted successfully.");
				finish();
			}
		}
	}
}
