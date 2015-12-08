package com.qrpatrol.patrol;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qrpatrol.activity.DashboardActivity;
import com.qrpatrol.android.R;
import com.qrpatrol.async_task.AsyncResponseForQRPatrol;
import com.qrpatrol.async_task.AsyncTaskForQRPatrol;
import com.qrpatrol.modal.CheckPoint;
import com.qrpatrol.modal.Incident;
import com.qrpatrol.util.CheckPointSelection;
import com.qrpatrol.util.CoveredCheckPointDialog;
import com.qrpatrol.util.QRParser;
import com.qrpatrol.util.QRPatrolDatabaseHandler;
import com.qrpatrol.util.Util;

public class IncidentListActivity extends Activity implements AsyncResponseForQRPatrol, CheckPointSelection{
	private ListView listView;
	private LinearLayout assignCheckPoint, sendIncident;
	private QRParser qrParser;
	private SharedPreferences patrolPrefs, appPrefs;
	private String patrolId, officerId;
	private ArrayList<Incident> incidentList;
	private IncidentAdapter adapter;
	private String incidentId, shiftId, urgency = "";
	private Boolean isSentViaEmail = false, isMaintenance = false;
	private LinearLayout sendEmailLayout, urgencyLevelLayout;
	private ImageView imgConfirmEmail, imgIsMaintainance, back, imgLow, imgNormal, imgCritical, menu;
	private QRPatrolDatabaseHandler dbHandler;
	private int scannedCheckPoint = 0;
	private TextView assignedCP;
	private ArrayList<CheckPoint> schedule;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_incidentlist);
		initializeLayout();
		fetchIncidents();
		setClickListeners();
	}

	public void initializeLayout(){
		qrParser = new QRParser(IncidentListActivity.this);
		dbHandler = new QRPatrolDatabaseHandler(IncidentListActivity.this);
		
		schedule = dbHandler.getSchedule("tillNow");
		
		menu = (ImageView)findViewById(R.id.menu);
		menu.setVisibility(View.GONE);
		
		assignedCP =(TextView)findViewById(R.id.assignedCP);
		assignedCP.setText("Assigned CheckPoint : ");
		
		listView = (ListView)findViewById(R.id.incidentListView);
		assignCheckPoint = (LinearLayout)findViewById(R.id.assignCheckPoint);
		sendIncident = (LinearLayout)findViewById(R.id.sendIncident);
		sendEmailLayout = (LinearLayout)findViewById(R.id.sentEmailLayout);
		imgConfirmEmail = (ImageView)findViewById(R.id.imgEmailConfirmation);
		imgIsMaintainance = (ImageView)findViewById(R.id.isMaintenance);
		urgencyLevelLayout = (LinearLayout)findViewById(R.id.urgencyLevelLayout);
		
		imgLow = (ImageView)findViewById(R.id.imgLow);
		imgNormal = (ImageView)findViewById(R.id.imgNormal);
		imgCritical = (ImageView)findViewById(R.id.imgCritical);
		
		imgLow.setImageResource(R.drawable.checked_checkbox);
		urgency = "low";
		
		back = (ImageView)findViewById(R.id.back);
		
		sendEmailLayout.setVisibility(View.VISIBLE);
		
		patrolPrefs = getSharedPreferences("patrol_prefs", MODE_PRIVATE);
		appPrefs = getSharedPreferences("qrpatrol_app_prefs", MODE_PRIVATE);
		
		patrolId = patrolPrefs.getString("patrolId", "0");
		officerId = getIntent().getStringExtra("OfficerId");
		shiftId = getIntent().getStringExtra("ShiftId");
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Incident incident = incidentList.get(position);
				if(incident.isChecked())
					incident.setIsChecked(false);
				else
					incident.setIsChecked(true);
				
				incidentList.set(position, incident);
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	public void setClickListeners(){
		sendIncident.setOnClickListener(listener);
		imgConfirmEmail.setOnClickListener(listener);
		imgIsMaintainance.setOnClickListener(listener);
		back.setOnClickListener(listener);
		imgLow.setOnClickListener(listener);
		imgNormal.setOnClickListener(listener);
		imgCritical.setOnClickListener(listener);
		assignCheckPoint.setOnClickListener(listener);
		
	}
	
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v== sendIncident){
				incidentId = "";
				for (int i = 0; i<incidentList.size(); i++){
					if(incidentList.get(i).isChecked()){
						if(incidentId.equals(""))
							incidentId = incidentId+incidentList.get(i).getId();
						else
							incidentId = incidentId+","+incidentList.get(i).getId();
					}
				}
				if(incidentId.equals("")){
					Util.alertMessage(IncidentListActivity.this, "Please select at least one incident");
				}else if(scannedCheckPoint == 0){
					Util.alertMessage(IncidentListActivity.this, "Please assign some CheckPoint");
				}else
					reportIncident();
				
			}else if(v == imgConfirmEmail){
				if(isSentViaEmail)
				{
					isSentViaEmail = false;
					imgConfirmEmail.setImageResource(R.drawable.unchecked_checkbox);
				}else{
					isSentViaEmail = true;
					imgConfirmEmail.setImageResource(R.drawable.checked_checkbox);
				}
			}else if(v == imgIsMaintainance){
				if(isMaintenance)
				{
					isMaintenance = false;
					imgIsMaintainance.setImageResource(R.drawable.unchecked_checkbox);
					urgencyLevelLayout.setVisibility(View.GONE);
					urgency = "";
				}else{
					isMaintenance = true;
					imgIsMaintainance.setImageResource(R.drawable.checked_checkbox);
					urgencyLevelLayout.setVisibility(View.VISIBLE);
				}
			}else if(v == imgLow){
				imgLow.setImageResource(R.drawable.checked_checkbox);
				imgNormal.setImageResource(R.drawable.unchecked_checkbox);
				imgCritical.setImageResource(R.drawable.unchecked_checkbox);
				urgency = "low";
			}else if(v == imgCritical){
				imgLow.setImageResource(R.drawable.unchecked_checkbox);
				imgNormal.setImageResource(R.drawable.unchecked_checkbox);
				imgCritical.setImageResource(R.drawable.checked_checkbox);
				urgency = "critical";
			}else if(v == imgNormal){
				imgLow.setImageResource(R.drawable.unchecked_checkbox);
				imgNormal.setImageResource(R.drawable.checked_checkbox);
				imgCritical.setImageResource(R.drawable.unchecked_checkbox);
				urgency = "normal";
			}else if(v == back){
				finish();
			}else if(v == assignCheckPoint){
				CoveredCheckPointDialog dialog = new CoveredCheckPointDialog(IncidentListActivity.this, "Please scan some CheckPoint before reporting some incident.");
				dialog.show();
			}
		}
	};
	
	public void reportIncident(){
		if (Util.isNetworkAvailable(IncidentListActivity.this)){
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("patrol_id", patrolId));
			nameValuePairs.add(new BasicNameValuePair("officer_id",officerId));
			nameValuePairs.add(new BasicNameValuePair("shift_id", shiftId));
			nameValuePairs.add(new BasicNameValuePair("event_name","INCIDENT"));
			nameValuePairs.add(new BasicNameValuePair("latitude", ""+DashboardActivity.myLat));
			nameValuePairs.add(new BasicNameValuePair("longitude",""+DashboardActivity.myLon));
			nameValuePairs.add(new BasicNameValuePair("checkpoint_id", String.valueOf(scannedCheckPoint)));
			nameValuePairs.add(new BasicNameValuePair("incident_id",incidentId));
			nameValuePairs.add(new BasicNameValuePair("isSentViaEmail", isSentViaEmail.toString()));
			nameValuePairs.add(new BasicNameValuePair("isMaintenance", isMaintenance.toString()));
			nameValuePairs.add(new BasicNameValuePair("urgency", urgency));
			
			AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(
					IncidentListActivity.this, "report-incident", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForQRPatrol) IncidentListActivity.this;
			mLogin.execute();
		}
		else {
			Util.alertMessage(IncidentListActivity.this,
					"Please check your internet connection");
		}
	}
	
	public void fetchIncidents(){
		incidentList = dbHandler.getAllIncidents();
		adapter = new IncidentAdapter(IncidentListActivity.this);
		listView.setAdapter(adapter);
		/*if (Util.isNetworkAvailable(IncidentListActivity.this)){
			String lastUpdated = appPrefs.getString("incidentTimeStamp", "");
			
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("last_updated_date", lastUpdated));
			
			
			AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(
					IncidentListActivity.this, "fetch-incident", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForQRPatrol) IncidentListActivity.this;
			mLogin.execute();
		}
		else {
			Util.alertMessage(IncidentListActivity.this,
					"Please check your internet connection");
		}*/
	}

	public class IncidentAdapter extends BaseAdapter
	{			
		private Context context;
		private TextView incidentDesc;
		private ImageView checkBox;
		private Incident incident;
		
		public IncidentAdapter(Context ctx)
		{
			context = ctx;
		}

//		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return incidentList.size();
		}

//		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return incidentList.get(position);
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
			    convertView = inflater.inflate(R.layout.incident_row, parent, false);
			}

			incident = incidentList.get(position);
		    incidentDesc = (TextView)convertView.findViewById(R.id.incidentDesc);
		    checkBox = (ImageView)convertView.findViewById(R.id.imgCheck);
		    
			incidentDesc.setText(incident.getDesc());
			
			if(incident.isChecked()){
				checkBox.setImageResource(R.drawable.checked_checkbox);
			}else
				checkBox.setImageResource(R.drawable.unchecked_checkbox);
			
			return convertView;
		}
	}
	/*
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
					scannedCheckPoint = 0;
					e.printStackTrace();
				}
				
				gettingCheckPoint(String.valueOf(scannedCheckPoint));
				
				
				
				
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
			Util.alertMessage(IncidentListActivity.this, "Invalid CheckPoint. Please try again...");
			scannedCheckPoint = 0;
			
		}else{
			
			assignedCP.setText("Assigned CheckPoint : "+checkPoint.getName());
		}
		
	}*/
	
	@Override
	public void processFinish(String output, String methodName) {
//		Util.alertMessage(ScheduleListActivity.this,output);
		if(methodName.equals("report-incident")){
			String response = qrParser.getEventResponse(output);
			if(!response.equals("failure")){
				
				Editor editor = patrolPrefs.edit();
				editor.putString("eventName", "INCIDENT");
				editor.commit();
				
				Util.showToast(IncidentListActivity.this, "Event submitted successfully.");
				finish();
			}
		}/*else{
			incidentList = qrParser.getIncidents(output);
			adapter = new IncidentAdapter(IncidentListActivity.this);
			listView.setAdapter(adapter);
		}*/
	}

	@Override
	public void OnCheckPointSelected(CheckPoint checkPoint) {
		// TODO Auto-generated method stub
		scannedCheckPoint = Integer.parseInt(checkPoint.getCheckPointId());
		assignedCP.setText("Assigned CheckPoint : "+checkPoint.getName());
	}
}