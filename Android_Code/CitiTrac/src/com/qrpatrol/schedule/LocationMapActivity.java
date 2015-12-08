package com.qrpatrol.schedule;


import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.qrpatrol.android.R;
import com.qrpatrol.modal.CheckPoint;
import com.qrpatrol.util.QRPatrolDatabaseHandler;





public class LocationMapActivity extends Activity{
	
	private GoogleMap googleMap;
	private Location center;
	private LocationManager locationManager;
	private String provider;
	private static double myLat=0, myLon=0;
	private Criteria criteria;
	ArrayList<CheckPoint> schedule;
	private QRPatrolDatabaseHandler dbHandler;
	private String trigger;
	private ImageView iv_back;
	private TextView tv_tilte;
	//private QRParser parser;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_locationmap);
		initializeLayout();
		fetchSchedule();
		initiliseMap();
		setClickListeners();
		
	}

	private void fetchSchedule() {
		schedule=new ArrayList<CheckPoint>();
		schedule = dbHandler.getSchedule(trigger);
	}

	public void initializeLayout(){
	//	parser = new QRParser(LocationonMapActivity.this);
		//Util.fetchSchedule(LocationonMapActivity.this, "1", "2");
		dbHandler = new QRPatrolDatabaseHandler(LocationMapActivity.this);
		trigger = getIntent().getStringExtra("trigger");
		iv_back=(ImageView)findViewById(R.id.back);
		tv_tilte=(TextView)findViewById(R.id.txtHeader);
		
		
	}
	
	public void setClickListeners(){
		tv_tilte.setText("My Schedule");
		
		iv_back.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			finish();
				
			}
		});
		
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
					Toast.makeText(LocationMapActivity.this, "GPS signal not found",Toast.LENGTH_LONG).show();
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

					for(int i = 0; i<schedule.size(); i++){
						MarkerOptions sta_marker = new MarkerOptions().title(schedule.get(i).getName()).snippet(schedule.get(i).getAddress()).position(new LatLng(Double.valueOf(schedule.get(i).getLatitude()),Double.valueOf(schedule.get(i).getLongitude())));
					
						googleMap.addMarker(sta_marker);
						if(i == 0){
							googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(schedule.get(i).getLatitude()), Double.valueOf(schedule.get(i).getLongitude())), 15));
							
						}
					}
					
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
	}
}