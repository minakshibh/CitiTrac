package com.qrpatrol.schedule;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.qrpatrol.activity.DashboardActivity;
import com.qrpatrol.android.R;
import com.qrpatrol.modal.CheckPoint;


public class ScheduleDetailsActivity extends Activity{
	private ImageView iv_back,iv_list, menu;
	private TextView tv_title,tv_address,tv_date,tv_note,textView_contactno,tv_altph;
	private TextView tv_city,tv_country,tv_state,tv_zipcode,tv_opentime,tv_closetime;
	private GoogleMap googleMap;
	private LocationManager locationManager;
	private String provider;
	private static double myLat=0, myLon=0;
	private Criteria criteria;
	private CheckPoint checkPoint;
	private Button getDirections;
	
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
	
		iv_list=(ImageView)findViewById(R.id.menu);
		iv_list.setVisibility(View.GONE);
		//iv_list.setImageResource(R.drawable.list_dot);
		
		tv_date=(TextView)findViewById(R.id.prefferedTime);
		tv_address=(TextView)findViewById(R.id.address);
		tv_note=(TextView)findViewById(R.id.notes);
		textView_contactno=(TextView)findViewById(R.id.contact);
		
		tv_city=(TextView)findViewById(R.id.city);
		tv_state=(TextView)findViewById(R.id.state);
		tv_country=(TextView)findViewById(R.id.country);
		tv_zipcode=(TextView)findViewById(R.id.zipcode);
		
		tv_altph=(TextView)findViewById(R.id.altContact);
		tv_opentime=(TextView)findViewById(R.id.open_timings);
		tv_closetime=(TextView)findViewById(R.id.close_timings);
		getDirections = (Button)findViewById(R.id.getDirections);

		checkPoint = (CheckPoint)getIntent().getParcelableExtra("checkPoint");
		tv_date.setText("Preffered Time : "+checkPoint.getPrefferedTime());
		tv_title.setText(checkPoint.getName());
		tv_address.setText(checkPoint.getAddress());
		tv_city.setText(checkPoint.getCity());
		tv_state.setText(checkPoint.getState());
		tv_country.setText(checkPoint.getCountry());
		tv_zipcode.setText(checkPoint.getZipcode());
		tv_opentime.setText(checkPoint.getOpenTimings());
		tv_altph.setText(checkPoint.getAltContact());
		textView_contactno.setText(checkPoint.getContactInfo());
	
		tv_note.setText(checkPoint.getNotes());
		tv_closetime.setText(checkPoint.getCloseTimings());
		
	
		
		//getIntent().getStringExtra("shiftid");
	
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
				if(isGoogleMapsInstalled())
				{	
					
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("google.navigation:q="+checkPoint.getLatitude()+","+checkPoint.getLongitude()));
					//intent.setPackage("com.google.android.apps.maps");
					startActivity(intent);
					}
				else
				{
					
				
				String url="http://maps.google.com/maps?daddr="+checkPoint.getAddress()+"&x-success=sourceapp://?resume=true&x-source=AirApp";
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(url));
				startActivity(intent);
				
				}
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
					Toast.makeText(ScheduleDetailsActivity.this, "GPS signal not found",Toast.LENGTH_LONG).show();
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

					MarkerOptions sta_marker = new MarkerOptions().position(new LatLng(Double.valueOf(checkPoint.getLatitude()),Double.valueOf(checkPoint.getLongitude())));
					googleMap.addMarker(sta_marker);
					
					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(checkPoint.getLatitude()), Double.valueOf(checkPoint.getLongitude())), 15));
					
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