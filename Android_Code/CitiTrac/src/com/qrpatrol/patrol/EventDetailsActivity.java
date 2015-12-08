package com.qrpatrol.patrol;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.qrpatrol.android.R;
import com.qrpatrol.modal.Event;
import com.qrpatrol.schedule.ScheduleDetailsActivity;


public class EventDetailsActivity extends Activity{
	private ImageView iv_back,iv_list, menu;
	private TextView tv_title,tv_address,tv_date,tv_note,textView_contactno,tv_altph;
	private TextView tv_city,tv_country,tv_state,tv_zipcode,tv_opentime,tv_closetime, lblNotes, play_video_text;
	private GoogleMap googleMap;
	private LocationManager locationManager;
	private String provider;
	private static double myLat=0, myLon=0;
	private Criteria criteria;
	private Event event;
	private Button getDirections;
	private LinearLayout bottomLayout;
	private ImageView scannedImage;
	private ImageLoader imageLoader;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_scheduledetails);
		initializeLayout();
		setClickListners();
		initiliseMap();
	}

	private void initializeLayout() {
		
		//dbHandler = new QRPatrolDatabaseHandler(ScheduleDetailsActivity.this);
		//parser=new QRParser(ScheduleDetailsActivity.this);
		//officer = (Officer)getIntent().getParcelableExtra("Officer");
		iv_back=(ImageView)findViewById(R.id.back);
		//iv_markdone=(ImageView)findViewById(R.id.imageView_checked);
		tv_title=(TextView)findViewById(R.id.txtHeader);
		play_video_text=(TextView)findViewById(R.id.play_video_text);
		iv_list=(ImageView)findViewById(R.id.menu);
		iv_list.setVisibility(View.GONE);
		//iv_list.setImageResource(R.drawable.list_dot);
		
		tv_date=(TextView)findViewById(R.id.prefferedTime);
		tv_address=(TextView)findViewById(R.id.address);
		tv_note=(TextView)findViewById(R.id.notes);
		textView_contactno=(TextView)findViewById(R.id.contact);
		bottomLayout = (LinearLayout)findViewById(R.id.bottomLayout);
		
		tv_city=(TextView)findViewById(R.id.city);
		tv_state=(TextView)findViewById(R.id.state);
		tv_country=(TextView)findViewById(R.id.country);
		tv_zipcode=(TextView)findViewById(R.id.zipcode);
		lblNotes = (TextView)findViewById(R.id.lblNotes);
		
		tv_altph=(TextView)findViewById(R.id.altContact);
		tv_opentime=(TextView)findViewById(R.id.open_timings);
		tv_closetime=(TextView)findViewById(R.id.close_timings);
		getDirections = (Button)findViewById(R.id.getDirections);

		event = (Event)getIntent().getParcelableExtra("event");
		tv_date.setText("Reported Time : "+event.getReportedTime());
		tv_title.setText(event.getName());
		tv_address.setText(event.getCheckPoint().getAddress());
		
		bottomLayout.setVisibility(View.GONE);
		getDirections.setText("View CheckPoint");
	
		
		if(event.getName().equalsIgnoreCase("Test In Process")){
			lblNotes.setText("Test Case Desc :");
			tv_note.setText(event.getTestDesc());
		}
		
		if(event.getName().equalsIgnoreCase("INCIDENT")){
			lblNotes.setText("Incident Desc. :");
			tv_note.setText(event.getIncidentDesc());
		}else if(event.getName().equalsIgnoreCase("SCAN")){
			lblNotes.setText("Notes :");
			tv_note.setText(event.getNotes());
		}else if(event.getName().equalsIgnoreCase("SOS") || event.getName().equalsIgnoreCase("TEST") || event.getName().equalsIgnoreCase("START")){
			lblNotes.setVisibility(View.GONE);
			tv_note.setVisibility(View.GONE);
		}else if(event.getName().equalsIgnoreCase("MME")){
			tv_note.setText(event.getText());
			if(!(event.getSoundUrl()==null || event.getSoundUrl().equals("null") || event.getSoundUrl().equals(""))){
				play_video_text.setVisibility(View.VISIBLE);
				
				SpannableString content = new SpannableString("Play Video");
				content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
				play_video_text.setText(content);
				play_video_text.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Log.d("","video url"+event.getSoundUrl());
						Intent intent=new Intent(EventDetailsActivity.this,PlayVideoActivity.class);
						intent.putExtra("video_url", event.getSoundUrl());
						startActivity(intent);
					}
				});
			}
		}
		//getIntent().getStringExtra("shiftid");
		
//		event.setOfficerPic("http://lawandorder.blogs.gainesville.com/files/2012/05/Officer-Robert-White.jpg");
		if(event.getName().equalsIgnoreCase("SCAN") || (event.getName().equalsIgnoreCase("MME") && (!(event.getImageUrl()==null || event.getImageUrl().equals("null") || event.getImageUrl().equals("")))))
		{
			scannedImage = (ImageView)findViewById(R.id.scanImage);
			scannedImage.setVisibility(View.VISIBLE);
			
			imageLoader = new ImageLoader(EventDetailsActivity.this);
			imageLoader.DisplayImage(event.getImageUrl(), scannedImage, "", null);
			scannedImage.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Log.e("image found","::: "+event.getImageUrl());
					Intent intent = new Intent(EventDetailsActivity.this, ImageActivity.class);
					intent.putExtra("url", event.getImageUrl());
					startActivity(intent);
					
					/*Dialog dialog = new Dialog(EventDetailsActivity.this);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					ImageView imageView = new ImageView(EventDetailsActivity.this);
					WebView webView = new WebView(EventDetailsActivity.this);
					webView.getSettings().setBuiltInZoomControls(true);
					webView.getSettings().setJavaScriptEnabled(true);
					
					Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
				    int width = display.getWidth();
				    webView.setInitialScale(2);
				    
					imageLoader.DisplayImage(event.getOfficerPic(), scannedImage, "webview", webView);
	
					dialog.setContentView(webView);
					dialog.show();*/
				}
			});
		}
		
		
		
	}
	
	private void setClickListners() {
		iv_back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			
				finish();
				}
			});
		getDirections.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EventDetailsActivity.this, ScheduleDetailsActivity.class);
				intent.putExtra("checkPoint",event.getCheckPoint());
				startActivity(intent);
			}
		});
	}
	public boolean isGoogleMapsInstalled()
	{
	    try
	    {
	        ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
	        return true;
	    } 
	    catch(PackageManager.NameNotFoundException e)
	    {
	        return false;
	    }
	}
	private void initiliseMap() {
		if (googleMap == null) {
			try {
				locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				criteria = new Criteria();
				provider = locationManager.getBestProvider(criteria, true);
				
				boolean enabledGPS = locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER);
				boolean enabledWiFi = locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

				// Check if enabled and if not send user to the GSP settings
				if (!enabledGPS && !enabledWiFi) {
					Toast.makeText(EventDetailsActivity.this, "GPS signal not found",Toast.LENGTH_LONG).show();
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}
				else
				{
					googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
					googleMap.setMyLocationEnabled(true);		
					googleMap.getUiSettings().setRotateGesturesEnabled(true);
					googleMap.getUiSettings().setZoomControlsEnabled(false);
					googleMap.getUiSettings().setMapToolbarEnabled(false);
					googleMap.getUiSettings().setMyLocationButtonEnabled(false);

					MarkerOptions sta_marker = new MarkerOptions().position(new LatLng(Double.valueOf(event.getCheckPoint().getLatitude()),Double.valueOf(event.getCheckPoint().getLongitude())));
					googleMap.addMarker(sta_marker);
					
					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(event.getCheckPoint().getLatitude()), Double.valueOf(event.getCheckPoint().getLongitude())), 15));
					
					/*
					// Getting Current Location
					Location location = locationManager.getLastKnownLocation(provider);	
				    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLat,myLon), 15));

				    MarkerOptions sta_marker = new MarkerOptions().position(new LatLng(myLat,myLon));
					googleMap.addMarker(sta_marker);*/
		
						}
				} catch (Exception e) {
					e.printStackTrace();
					}
						
			
			}
			

						
		/*	// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	         criteria = new Criteria();
	         provider = locationManager.getBestProvider(criteria, true);
	        // Getting Current Location
	     //   Location location = locationManager.getLastKnownLocation(provider);	
	        
	        googleMap.setMyLocationEnabled(true);
	        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
	        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLat,myLon), 15));
	        	        
	        MarkerOptions sta_marker = new MarkerOptions().position(new LatLng(myLat,myLon));
	        googleMap.addMarker(sta_marker);
			
	     //  addMaker();
			
			
		}
	*/
	}
	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}
}