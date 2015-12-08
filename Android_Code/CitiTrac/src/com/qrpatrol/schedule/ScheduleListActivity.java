package com.qrpatrol.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.qrpatrol.activity.DashboardActivity;
import com.qrpatrol.android.R;
import com.qrpatrol.async_task.AsyncResponseForQRPatrol;
import com.qrpatrol.modal.CheckPoint;
import com.qrpatrol.util.QRParser;
import com.qrpatrol.util.QRPatrolDatabaseHandler;
import com.qrpatrol.util.Util;

public class ScheduleListActivity extends Activity implements AsyncResponseForQRPatrol{
	private ListView listView;
	private TextView tv_title, textList, textMap;
	private ImageView mySchedule, back;
	
	private ScheduleAdapter adapter;
	private QRPatrolDatabaseHandler dbHandler;
	private ArrayList<CheckPoint> schedule;//=new ArrayList<CheckPoint>();
	private CheckPoint checkpoint;
	private LinearLayout showMap, showList;
	private RelativeLayout mapLayout;
	
	private String trigger;
	private int mapFlag =0;
	private GoogleMap googleMap;
	private Location center;
	private LocationManager locationManager;
	private String provider,getnoti;
	private Criteria criteria;
	private HashMap<Marker, CheckPoint> checkPointMarkers;
	int startFlag = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_schedulelist);
		
		
		initializeLayout();
		fetchSchedule();
		setOnClickListener();
	}

	
	public Marker placeMarker(CheckPoint checkPoint) {

		  Marker m  = googleMap.addMarker(new MarkerOptions()
		  .title(checkPoint.getName()).snippet(checkPoint.getAddress())
		  .position(new LatLng(Double.valueOf(checkPoint.getLatitude()),Double.valueOf(checkPoint.getLongitude()))));

		  return m;

	}
	public void initializeLayout(){
		startFlag = 1;
	  /* try{
		          Util.fetchSchedule(ScheduleListActivity.this, DashboardActivity.officer.getOfficerId(), DashboardActivity.officer.getShiftId(), "all", false);
		         }catch(Exception e){
		          e.printStackTrace();
		         }*/
		  
		
//		schedule = (ArrayList<CheckPoint>)getIntent().getParcelableExtra("checkpoint");
		dbHandler = new QRPatrolDatabaseHandler(ScheduleListActivity.this);
		mySchedule = (ImageView)findViewById(R.id.menu);
		
		tv_title=(TextView)findViewById(R.id.txtHeader);
		tv_title.setText("My Schedule");
		trigger = getIntent().getStringExtra("trigger");
		listView = (ListView)findViewById(R.id.scheduleListView);
		back = (ImageView)findViewById(R.id.back);
		showMap = (LinearLayout)findViewById(R.id.viewMap);
		showList = (LinearLayout)findViewById(R.id.viewList);
		mapLayout = (RelativeLayout)findViewById(R.id.mapLayout);
		
		if(trigger.equals("all"))
			mySchedule.setVisibility(View.VISIBLE);
		else
			mySchedule.setVisibility(View.GONE);
		
		textList = (TextView)findViewById(R.id.textList);
		textMap = (TextView)findViewById(R.id.textMap);
		
		textMap.setTextColor(Color.parseColor("#6c6c6c"));
		textList.setTextColor(Color.parseColor("#ffffff"));
	}
		
		private void setOnClickListener() {
				
			back.setOnClickListener(listener);
			
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					checkpoint=new CheckPoint();
					checkpoint = schedule.get(position);
					Intent intent = new Intent(ScheduleListActivity.this, ScheduleDetailsActivity.class);
					intent.putExtra("checkPoint",checkpoint);
					
					startActivity(intent);
				
					
					/*Incident incident = incidentList.get(position);
					if(incident.isChecked())
						incident.setIsChecked(false);
					else
						incident.setIsChecked(true);
					
					incidentList.set(position, incident);
					adapter.notifyDataSetChanged();*/
				}
			});
			
			mySchedule.setOnClickListener(listener);
//			mapview.setOnClickListener(listener);
			showMap.setOnClickListener(listener);
			showList.setOnClickListener(listener);
		}
		
	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			/*if(v == mapview){
				Intent intent = new Intent(ScheduleListActivity.this, LocationMapActivity.class);
				intent.putExtra("trigger", "all");
				intent.putExtra("schedule", schedule);
				startActivity(intent);
			}else */if( v == mySchedule){
				Intent intent = new Intent(ScheduleListActivity.this, ScheduleListActivity.class);
				intent.putExtra("trigger", "tillNow");
				startActivity(intent);
			}else if(v == back){
				finish();
			}else if(v == showMap){
				
				if(mapFlag == 0){
					initiliseMap();
				}
				listView.setVisibility(View.GONE);
				mapLayout.setVisibility(View.VISIBLE);
				textList.setTextColor(Color.parseColor("#6c6c6c"));
				textMap.setTextColor(Color.parseColor("#ffffff"));
				
			}else if(v == showList){
				listView.setVisibility(View.VISIBLE);
				mapLayout.setVisibility(View.GONE);
				textMap.setTextColor(Color.parseColor("#6c6c6c"));
				textList.setTextColor(Color.parseColor("#ffffff"));
			}
		}
	};
	
	public void fetchSchedule(){
		schedule=new ArrayList<CheckPoint>();
		schedule = dbHandler.getSchedule(trigger);
		adapter = new ScheduleAdapter(ScheduleListActivity.this,schedule);
		listView.setAdapter(adapter);
		
		/*if (Util.isNetworkAvailable(ScheduleListActivity.this)){
			String lastUpdated = appPrefs.getString("scheduleTimeStamp", "");
			
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			nameValuePairs.add(new BasicNameValuePair("officer_id", officer.getOfficerId()));
			nameValuePairs.add(new BasicNameValuePair("shift_id", officer.getShiftId()));
			nameValuePairs.add(new BasicNameValuePair("last_updated_date", lastUpdated));
			
			nameValuePairs.add(new BasicNameValuePair("officer_id", "1"));
			nameValuePairs.add(new BasicNameValuePair("shift_id", "2"));
			nameValuePairs.add(new BasicNameValuePair("last_updated_date", lastUpdated));
			
			AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(
					ScheduleListActivity.this, "officer-schedule", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForQRPatrol) ScheduleListActivity.this;
			mLogin.execute();
		}
		else {
			Util.alertMessage(ScheduleListActivity.this,
					"Please check your internet connection");
		}*/
	}

	public class ScheduleAdapter extends BaseAdapter
	{			
		private Context context;
		private TextView checkPointName, timeOfCheck, notes;
		private ImageView delete;
		private CheckPoint checkPoint;
		ArrayList<CheckPoint> schedule;
		public ScheduleAdapter(Context ctx, ArrayList<CheckPoint> schedule)
		{
			this.schedule=schedule;
			context = ctx;
		}

//		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return schedule.size();
		}

//		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return schedule.get(position);
		}

//		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		
//		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if(convertView == null){
			    convertView = inflater.inflate(R.layout.schedule_row, parent, false);
			}

			checkPoint = schedule.get(position);
			
			checkPointName = (TextView)convertView.findViewById(R.id.checkpointName);
			timeOfCheck = (TextView)convertView.findViewById(R.id.timeOfCheck);
			notes = (TextView)convertView.findViewById(R.id.notes);
			
		    delete = (ImageView)convertView.findViewById(R.id.imgDelete);
		    delete.setVisibility(View.GONE);
		    
		    checkPointName.setText(checkPoint.getName());
		    
		    if(trigger.equals("all"))
		    	timeOfCheck.setText("Preffered Time: "+checkPoint.getPrefferedTime());
		    else
		    	timeOfCheck.setText("Checked Time: "+checkPoint.getCheckedTime());
		    
		    notes.setText("Notes : "+checkPoint.getNotes());
			
			return convertView;
		}
	}
	/*
	@Override
	public void processFinish(String output, String methodName) {
		Log.e("response",output);
		
		Util.alertMessage(ScheduleListActivity.this,output);
		schedule = qrParser.getSchedule(output);
		adapter = new ScheduleAdapter(ScheduleListActivity.this);
		listView.setAdapter(adapter);
	}*/
	
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
					Toast.makeText(ScheduleListActivity.this, "GPS signal not found",Toast.LENGTH_LONG).show();
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

					googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
						
						@Override
						public void onInfoWindowClick(Marker marker) {
							// TODO Auto-generated method stub
							CheckPoint checkPoint = checkPointMarkers.get(marker);
							
							Intent intent = new Intent(ScheduleListActivity.this, ScheduleDetailsActivity.class);
							intent.putExtra("checkPoint",checkPoint);
							
							startActivity(intent);
						}
					});
					checkPointMarkers = new HashMap<Marker, CheckPoint>();

					for(int i = 0; i<schedule.size(); i++){
						
						checkPointMarkers.put(placeMarker(schedule.get(i)), schedule.get(i));
						  
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


	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
//		ArrayList<CheckPoint> schedule1=new ArrayList<CheckPoint>();
		Log.e("fetch-schedule", output);
		QRParser parser = new QRParser(ScheduleListActivity.this);
//		listView.setAdapter(null);
		ArrayList<CheckPoint> schedule = parser.getSchedule(output);
	/*	Iterator<CheckPoint> iterator11 = schedule.iterator();
        while (iterator11.hasNext()) {
         
        	schedule1.add(iterator11.next());
        }
        schedule.clear();
        schedule.addAll(schedule1);
       
       listView.setAdapter(null);
       adapter = new ScheduleAdapter(ScheduleListActivity.this,schedule);
       listView.invalidateViews();
       listView.setAdapter(adapter);*/
       dbHandler.updateSchedule(schedule);
		fetchSchedule();
//		listView.setAdapter(adapter);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("on Resume","on Resume");
		
		if(startFlag == 1){
		
		   try{
		          Util.fetchSchedule(ScheduleListActivity.this, DashboardActivity.officer.getOfficerId(), DashboardActivity.officer.getShiftId(), "all", true);
		      }catch(Exception e){
		          e.printStackTrace();
		      }
		}
	}
	
	/*
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		System.out.print(".....................onstart................................");
		System.out.print("......................onstart................................");
		
		   try{
		          Util.fetchSchedule(ScheduleListActivity.this, DashboardActivity.officer.getOfficerId(), DashboardActivity.officer.getShiftId(), "all", false);
		         }catch(Exception e){
		          e.printStackTrace();
		         }
		  
	}*/
}