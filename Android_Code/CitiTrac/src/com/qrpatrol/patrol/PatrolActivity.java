package com.qrpatrol.patrol;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.qrpatrol.activity.DashboardActivity;
import com.qrpatrol.android.R;
import com.qrpatrol.async_task.AsyncResponseForQRPatrol;
import com.qrpatrol.async_task.AsyncTaskForQRPatrol;
import com.qrpatrol.modal.CheckPoint;
import com.qrpatrol.modal.Event;
import com.qrpatrol.modal.Officer;
import com.qrpatrol.util.CheckPointSelection;
import com.qrpatrol.util.CoveredCheckPointDialog;
import com.qrpatrol.util.QRParser;
import com.qrpatrol.util.QRPatrolDatabaseHandler;
import com.qrpatrol.util.Util;

public class PatrolActivity extends Activity implements LocationListener,
		AsyncResponseForQRPatrol, CheckPointSelection {

	private Button scan, mme, test, sos, incident, endTour;
	private LinearLayout events, home, locate;
	private TextView latestAction;
	private String eventName = "", patrolId = "0";

	private SharedPreferences patrolPrefs;
	private QRParser qrParser;
	private QRPatrolDatabaseHandler dbHandler;
	private ImageView back, menu;
	public GoogleMap googleMap;
	private int locflag = 0;
	private int scannedCheckPoint = 0;
	private String getOfficerId;
	private GPSTracker gpsTracker;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_patrol);

		initializeLayout();
		setClickListners();
		initiliseMap();
	}

	public void initializeLayout() {
		
		gpsTracker = new GPSTracker(this);
		
		qrParser = new QRParser(PatrolActivity.this);
		dbHandler = new QRPatrolDatabaseHandler(PatrolActivity.this);
		back = (ImageView) findViewById(R.id.back);
		scan = (Button) findViewById(R.id.scan);
		mme = (Button) findViewById(R.id.mme);
		test = (Button) findViewById(R.id.test);
		sos = (Button) findViewById(R.id.sos);
		incident = (Button) findViewById(R.id.incident);
		endTour = (Button) findViewById(R.id.endTour);
		events = (LinearLayout) findViewById(R.id.events);
		home = (LinearLayout) findViewById(R.id.home);
		locate = (LinearLayout) findViewById(R.id.locate);
		menu = (ImageView) findViewById(R.id.menu);
		menu.setVisibility(View.GONE);

		patrolPrefs = getSharedPreferences("patrol_prefs", MODE_PRIVATE);
		patrolId = patrolPrefs.getString("patrolId", "0");
		eventName = patrolPrefs.getString("eventName", "");

		
		latestAction = (TextView) findViewById(R.id.latestAction);

		if (!patrolId.equals("0")) {
			latestAction.setText(eventName);
		} else {
			scan.setText("START Patrol");
			mme.setEnabled(false);
			test.setEnabled(false);
			sos.setEnabled(false);
			endTour.setEnabled(false);
			incident.setEnabled(false);
			events.setEnabled(false);
		}
		// execute this code only if net is not working
		try{
		if(DashboardActivity.officer.getIsTestInProcess().equalsIgnoreCase("true")){
			test.setText("Test In Process");
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void setClickListners() {
		scan.setOnClickListener(listener);
		mme.setOnClickListener(listener);
		test.setOnClickListener(listener);
		sos.setOnClickListener(listener);
		incident.setOnClickListener(listener);
		endTour.setOnClickListener(listener);
		events.setOnClickListener(listener);
		home.setOnClickListener(listener);
		locate.setOnClickListener(listener);
		back.setOnClickListener(listener);
	}

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			if (v == scan) {
				if (patrolId.equals("0")) {
					eventName = "START";
				} else {
					eventName = "SCAN";
				}
				reportEvent(eventName);
			} else if (v == mme) {
				eventName = "MME";
				// reportEvent(eventName);
				Intent intent = new Intent(PatrolActivity.this,	MultiMediaActivity.class);
				intent.putExtra("patrolID", patrolId);
				startActivity(intent);
				
				// reportEvent(eventName);
			} else if (v == test) {
				eventName = test.getText().toString();
				if(eventName.equalsIgnoreCase("BEGIN TEST")){
					CoveredCheckPointDialog dialog = new CoveredCheckPointDialog(PatrolActivity.this, "Please scan some CheckPoint before beginning some test.");
					dialog.show();
				}else{
					Intent intent = new Intent(PatrolActivity.this, TestInProcessActivity.class);
					intent.putExtra("patrolId", patrolId);
					startActivity(intent);
				}
//				reportEvent(eventName);
			} else if (v == sos) {
				eventName = "SOS";
				reportEvent(eventName);
			} else if (v == incident) {
				eventName = "INCIDENT";
				// reportEvent(eventName);

				Intent intent = new Intent(PatrolActivity.this,
						IncidentListActivity.class);
				intent.putExtra("ShiftId", DashboardActivity.officer.getShiftId());
				intent.putExtra("OfficerId", DashboardActivity.officer.getOfficerId());
				startActivity(intent);
			} else if (v == endTour) {
				eventName = "END";
				reportEvent(eventName);
			} else if (v == events) {
				Intent intent = new Intent(PatrolActivity.this,
						EventListActivity.class);
				startActivity(intent);
			} else if (v == home) {
				finish();
			} else if (v == locate) {
				locflag = 0;

				/*try {
					locationManager.requestLocationUpdates(provider, 1000, 0,
							PatrolActivity.this);

					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {

							getLastKnownLocation();
						}
					}, 4000);

				} catch (Exception e) {
					e.printStackTrace();
					getLastKnownLocation();
				}*/

			} else if (v == back) {
				finish();
			}
		}
	};

	private void getLastKnownLocation() {
		// TODO Auto-generated method stub
		// Getting Current Location
		if (locflag == 0) {
//			Location location = locationManager.getLastKnownLocation(provider);
			DashboardActivity.myLat = gpsTracker.getLocation().getLatitude();

			// Getting longitude of the current location
			DashboardActivity.myLon = gpsTracker.getLocation().getLongitude();
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					DashboardActivity.myLat, DashboardActivity.myLon), 18));
		}
		// MarkerOptions sta_marker = new MarkerOptions().position(new
		// LatLng(location.getLatitude(),location.getLongitude()));
		// googleMap.addMarker(sta_marker);
	}

	public void reportEvent(String eventName) {

		if (Util.isNetworkAvailable(PatrolActivity.this)) {
			if (eventName.equalsIgnoreCase("SCAN")) {
				try {

					Intent intent = new Intent(
							"com.google.zxing.client.android.SCAN");
					intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE
					// for bar// codes
					startActivityForResult(intent, 0);

				} catch (Exception e) {

					Uri marketUri = Uri
							.parse("market://details?id=com.google.zxing.client.android");
					Intent marketIntent = new Intent(Intent.ACTION_VIEW,
							marketUri);
					startActivity(marketIntent);

				}
			} else {
				if((eventName.equalsIgnoreCase("INCIDENT") || eventName.equalsIgnoreCase("SCAN") || eventName.equalsIgnoreCase("MME"))
						&& scannedCheckPoint == 0){
					Util.alertMessage(PatrolActivity.this, "Please assign CheckPoint to the event");
				}else{
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs
							.add(new BasicNameValuePair("patrol_id", patrolId));
					nameValuePairs.add(new BasicNameValuePair("officer_id", DashboardActivity.officer
							.getOfficerId()));
					nameValuePairs.add(new BasicNameValuePair("shift_id", DashboardActivity.officer
							.getShiftId()));
					nameValuePairs.add(new BasicNameValuePair("event_name",
							eventName));
					nameValuePairs.add(new BasicNameValuePair("latitude", ""
							+ String.valueOf(DashboardActivity.myLat)));
					nameValuePairs.add(new BasicNameValuePair("longitude", ""
							+ String.valueOf(DashboardActivity.myLon)));
					nameValuePairs
							.add(new BasicNameValuePair("checkpoint_id", String.valueOf(scannedCheckPoint)));
					nameValuePairs.add(new BasicNameValuePair("img", ""));
					nameValuePairs.add(new BasicNameValuePair("notes", ""));
	
					AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(
							PatrolActivity.this, "report-event", nameValuePairs,
							true, "Please wait...");
					mLogin.delegate = (AsyncResponseForQRPatrol) PatrolActivity.this;
					mLogin.execute();
				}
			}
		} else {
			Util.alertMessage(PatrolActivity.this,
					"Please check your internet connection");
		}
	}

	@Override
	public void processFinish(String output, String methodName) {
		Log.e("Output", output);
		if (methodName.equals("report-event")) {
			Editor editor = patrolPrefs.edit();

			String response = qrParser.getEventResponse(output);

			int flag = 0;
			latestAction.setText(eventName);

			if (eventName.equals("START")
					&& !response.equalsIgnoreCase("failure")) {
				patrolId = response;
				flag = 1;
				editor.putString("eventName", eventName);
				editor.putString("patrolId", patrolId);
				editor.commit();

				scan.setText("SCAN QR Code");
				mme.setEnabled(true);
				test.setEnabled(true);
				sos.setEnabled(true);
				endTour.setEnabled(true);
				incident.setEnabled(true);
				events.setEnabled(true);
				
				
				getOfficerId = DashboardActivity.officer.getOfficerId();
			    SharedPreferences.Editor editor1 = getSharedPreferences("tutor", MODE_PRIVATE).edit();
			     editor1.putString("getOfficerId", getOfficerId);
			     editor1.commit();
			    
			    startService(new Intent(PatrolActivity.this,MyService.class));
			    
			    
			} else {
				flag = 2;
				editor.putString("eventName", eventName);
				editor.commit();

				if (eventName.equals("END")) {
					editor.clear();
					editor.commit();

					dbHandler.deleteEvents();
					dbHandler.markCheckPointsAsUnChecked();
					DashboardActivity.officer.setIsTestInProcess("false");
					DashboardActivity.officer.setCheckPointBeingTested("0");
					
					AlertDialog.Builder alert = new AlertDialog.Builder(
							PatrolActivity.this);
					alert.setMessage("Your Patrol has been ended");
					alert.setTitle("CitiTrac");
					alert.setPositiveButton("Okay",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									stopService(new Intent(PatrolActivity.this, MyService.class));
									finish();
								}
							});
					alert.show();
				}
			}

			if (!response.equalsIgnoreCase("failure")) {
				Util.showToast(PatrolActivity.this,
						"Event submitted successfully.");

				if(eventName.equalsIgnoreCase("Begin Test")){
					test.setText("Test In Process");
					DashboardActivity.officer.setIsTestInProcess("true");
					DashboardActivity.officer.setCheckPointBeingTested(String.valueOf(scannedCheckPoint));
					
					Intent intent = new Intent(PatrolActivity.this, TestInProcessActivity.class);
					intent.putExtra("patrolId", patrolId);
					startActivity(intent);
				}

				if (Util.isNetworkAvailable(PatrolActivity.this)) {
					Util.fetchEvents(PatrolActivity.this, patrolId, false);
				}
			}
		} else if (methodName.equals("fetch-event")) {
			ArrayList<Event> events = qrParser.getEvents(output);
			dbHandler.saveEvents(events, patrolId);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			eventName = patrolPrefs.getString("eventName", "");
			latestAction.setText(eventName);
			if (eventName.equalsIgnoreCase("INCIDENT") || eventName.equalsIgnoreCase("MME") || eventName.equalsIgnoreCase("SCAN") || eventName.contains("Reported") || eventName.equals("End Test")) {
				if(eventName.equalsIgnoreCase("End Test")){
					test.setText("Begin Test");
				}
				
				if (Util.isNetworkAvailable(PatrolActivity.this)) {
					Util.fetchEvents(PatrolActivity.this, patrolId, false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void initiliseMap() {
		if (googleMap == null) {
			try {
				/*locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				criteria = new Criteria();
				provider = locationManager.getBestProvider(criteria, true);

				boolean enabledGPS = locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER);
				boolean enabledWiFi = locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

				// Check if enabled and if not send user to the GSP settings
				if (!enabledGPS && !enabledWiFi) {
					Toast.makeText(PatrolActivity.this, "GPS signal not found",
							Toast.LENGTH_LONG).show();
					Intent intent = new Intent(
							Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				} else {*/
				
				DashboardActivity.myLat = gpsTracker.getLocation().getLatitude();
				DashboardActivity.myLon = gpsTracker.getLocation().getLongitude();
				
					googleMap = ((MapFragment) getFragmentManager()
							.findFragmentById(R.id.map)).getMap();
					googleMap.setMyLocationEnabled(true);
					googleMap.getUiSettings().setRotateGesturesEnabled(true);
					googleMap.getUiSettings().setZoomControlsEnabled(false);
					googleMap.getUiSettings().setMapToolbarEnabled(false);
					googleMap.getUiSettings().setMyLocationButtonEnabled(false);

					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
							new LatLng(DashboardActivity.myLat,
									DashboardActivity.myLon), 18));
					locflag = 1;

//				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onLocationChanged(Location location) {
		try {
			// TODO Auto-generated method stub
			DashboardActivity.myLat = location.getLatitude();

			// Getting longitude of the current location
			DashboardActivity.myLon = location.getLongitude();
		} catch (Exception e) {
			location = null;
			e.printStackTrace();
		}

		if (locflag == 0 && location != null) {
			// Showing the current location in Google Map
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					DashboardActivity.myLat, DashboardActivity.myLon), 18));
			locflag = 1;
//			locationManager.requestLocationUpdates(provider, 5000, 0, this);
		}

		// Zoom in the Google Map
		// googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {

			if (resultCode == RESULT_OK) {
				String contents = data.getStringExtra("SCAN_RESULT");
				Log.d("","contentscontents"+contents);
				
				try{
					scannedCheckPoint = Integer.parseInt(contents);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				gettingCheckPoint(contents);
				
				
				
				
				//return_string.setText(contents);
			}
			if (resultCode == RESULT_CANCELED) {
				// handle cancel
			}
		}
	}

	private void gettingCheckPoint(String contents) {
		// TODO Auto-generated method stub
		CheckPoint checkPoint = dbHandler.getCheckPoint(contents, null);
		
		if (checkPoint==null) {
			Util.alertMessage(PatrolActivity.this, "Invalid CheckPoint. Please try again...");
			scannedCheckPoint = 0;
		}else{
			Intent intent=new Intent(PatrolActivity.this,ScanPatroluserActivity.class);
			intent.putExtra("checkpoint", checkPoint);
			intent.putExtra("Officer", DashboardActivity.officer);
			intent.putExtra("patrolID", patrolId);
			
			startActivity(intent);
		}
	}

	@Override
	public void OnCheckPointSelected(CheckPoint checkPoint) {
		// TODO Auto-generated method stub
		scannedCheckPoint = Integer.parseInt(checkPoint.getCheckPointId());
		Log.e("reporting test event", eventName);
		reportEvent(eventName);
	}

}