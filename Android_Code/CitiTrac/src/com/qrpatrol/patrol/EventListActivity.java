package com.qrpatrol.patrol;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qrpatrol.activity.LoginActivity;
import com.qrpatrol.activity.Splash;
import com.qrpatrol.android.R;
import com.qrpatrol.async_task.AsyncResponseForQRPatrol;
import com.qrpatrol.async_task.AsyncTaskForQRPatrol;
import com.qrpatrol.modal.CheckPoint;
import com.qrpatrol.modal.Event;
import com.qrpatrol.patrol.IncidentListActivity.IncidentAdapter;
import com.qrpatrol.util.QRParser;
import com.qrpatrol.util.QRPatrolDatabaseHandler;
import com.qrpatrol.util.Util;

public class EventListActivity extends Activity{
	private ListView listView;
	private QRParser qrParser;
	private SharedPreferences patrolPrefs, appPrefs;
	private String patrolId, trigger;
	private ArrayList<Event> eventList;
	private EventAdapter adapter;
	private Boolean isSentViaEmail = false;
	private QRPatrolDatabaseHandler dbHandler;
	private ImageView  back, menu;
	private LinearLayout showMap, showList;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_schedulelist);
		initializeLayout();
		fetchEvents();
		setClickListeners();
		
		Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
			public void run() {
				fetchEvents();
			}
		}, 2000);
	}

	private void fetchEvents() {
		eventList = dbHandler.getEvents(patrolId);
		adapter = new EventAdapter(EventListActivity.this);
		listView.setAdapter(adapter);
	}

	public void initializeLayout(){
		qrParser = new QRParser(EventListActivity.this);
		dbHandler = new QRPatrolDatabaseHandler(EventListActivity.this);
		showMap = (LinearLayout)findViewById(R.id.viewMap);
		showList = (LinearLayout)findViewById(R.id.viewList);
		menu = (ImageView)findViewById(R.id.menu);
		menu.setVisibility(View.GONE);
		showMap.setVisibility(View.GONE);
		showList.setVisibility(View.GONE);
		
		back = (ImageView)findViewById(R.id.back);
		
		listView = (ListView)findViewById(R.id.scheduleListView);
		
		patrolPrefs = getSharedPreferences("patrol_prefs", MODE_PRIVATE);
		appPrefs = getSharedPreferences("qrpatrol_app_prefs", MODE_PRIVATE);
		
		patrolId = patrolPrefs.getString("patrolId", "0");
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Event event = eventList.get(position);
				
				if(event.getName().equals("TEST") || event.getName().equals("SOS") || event.getName().equals("END") || event.getName().equals("START")){
			    	
			    }else{
					Intent intent = new Intent(EventListActivity.this, EventDetailsActivity.class);
					intent.putExtra("event", event);
					startActivity(intent);
			    }
//				Util.alertMessage(EventListActivity.this, "CHECK POINT NAME : "+event.getCheckPoint().getName());
			}
		});
		
		
	}
	
	public void setClickListeners(){
		back.setOnClickListener(listener);
//		imgConfirmEmail.setOnClickListener(listener);
	}
	
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v == back)
				finish();
		}
	};
	
	
	

	public class EventAdapter extends BaseAdapter
	{			
		private Context context;
		private TextView eventName, checkPointName, time, eventDesc;
		private ImageView checkBox;
		private Event event;
		
		public EventAdapter(Context ctx)
		{
			context = ctx;
		}

//		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return eventList.size();
		}

//		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return eventList.get(position);
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

			event = eventList.get(position);
		    
			eventName = (TextView)convertView.findViewById(R.id.checkpointName);
		    checkPointName = (TextView)convertView.findViewById(R.id.timeOfCheck);
		    eventDesc = (TextView)convertView.findViewById(R.id.eventDesc);
		    time = (TextView)convertView.findViewById(R.id.notes);
		    
		    checkBox = (ImageView)convertView.findViewById(R.id.imgDelete);
		    
		    eventName.setText(event.getName());
		    
		    if(event.getName().equalsIgnoreCase("Test In Process"))
		    {
		    	eventName.setText(event.getName() +" ("+event.getTestDesc()+")");
		    }
		   if(event.getName().equals("TEST") || event.getName().equals("SOS") || event.getName().equals("END") || event.getName().equals("START")){
		    	checkPointName.setVisibility(View.GONE);
		    }else{
		    	checkPointName.setVisibility(View.VISIBLE);
		    	checkPointName.setText("CheckPoint : "+event.getCheckPoint().getName());
		    }
		    
		    if(event.getName().equalsIgnoreCase("incident")){
		    	eventDesc.setVisibility(View.VISIBLE);
		    	eventDesc.setText("Description : "+event.getIncidentDesc());
		    }else{
		    	eventDesc.setVisibility(View.GONE);
		    }
		    
		    time.setText("Reported Time : "+event.getReportedTime());
		    
			return convertView;
		}
	}
	
	/*@Override
	public void processFinish(String output, String methodName) {
		Util.alertMessage(EventListActivity.this,output);
		eventList = qrParser.getEvents(output);
		adapter = new EventAdapter(EventListActivity.this);
		listView.setAdapter(adapter);
		
		
		if(methodName.equals("report-incident")){
			String response = qrParser.getEventResponse(output);
			if(!response.equals("failure")){
				Util.showToast(EventListActivity.this, "Event submitted successfully.");
				finish();
			}
		}else{
			incidentList = qrParser.getIncidents(output);
			adapter = new IncidentAdapter(IncidentListActivity.this);
			listView.setAdapter(adapter);
		}
	}*/
}