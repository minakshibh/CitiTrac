package com.qrpatrol.patrol;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import com.qrpatrol.activity.DashboardActivity;
import com.qrpatrol.android.R;
import com.qrpatrol.android.R.color;
import com.qrpatrol.async_task.AsyncResponseForQRPatrol;
import com.qrpatrol.async_task.AsyncTaskForQRPatrol;
import com.qrpatrol.modal.CheckPoint;
import com.qrpatrol.modal.Officer;
import com.qrpatrol.modal.TestCase;
import com.qrpatrol.util.QRParser;
import com.qrpatrol.util.QRPatrolDatabaseHandler;
import com.qrpatrol.util.Util;

public class TestInProcessActivity extends Activity implements AsyncResponseForQRPatrol{

	private TextView checkPointName;
	private Button btnEndTest, btnSubmit;
	private ListView listView;
	private ArrayList<TestCase> testList;
	private QRParser parser;
	private TestListAdapter adapter;
	private TestCase selectedTestCase = null;
	private QRPatrolDatabaseHandler dbHandler;
	private CheckPoint checkPoint;
	private String patrolId;
	private SharedPreferences patrolPrefs;
	private String eventName="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_testinprocess);
		initializeLayout();
		setClickListeners();
		fetchTestCases();
	}
	
	private void fetchTestCases() {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("checkpoint_id", DashboardActivity.officer.getCheckPointBeingTested()));
		
		AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(
				TestInProcessActivity.this, "fetch-testlist", nameValuePairs,
				true, "Please wait...");
		mLogin.delegate = (AsyncResponseForQRPatrol) TestInProcessActivity.this;
		mLogin.execute();
	}

	private void initializeLayout() {
		parser = new QRParser(TestInProcessActivity.this);
		adapter = new TestListAdapter(TestInProcessActivity.this);
		dbHandler = new QRPatrolDatabaseHandler(TestInProcessActivity.this);
		
		patrolId = getIntent().getStringExtra("patrolId");
		patrolPrefs = getSharedPreferences("patrol_prefs", MODE_PRIVATE);
		checkPoint = dbHandler.getCheckPoint(DashboardActivity.officer.getCheckPointBeingTested(), null);
		
		checkPointName = (TextView)findViewById(R.id.checkpointName);
		btnEndTest = (Button)findViewById(R.id.btnEndTest);
		btnSubmit = (Button)findViewById(R.id.btnSubmit);
		listView = (ListView)findViewById(R.id.textCaseListView);
		
		checkPointName.setText(checkPoint.getName());
	}
	
	private void setClickListeners(){
		btnSubmit.setOnClickListener(listener);
		btnEndTest.setOnClickListener(listener);
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				for(int i = 0; i<testList.size(); i++){
					testList.get(i).setIsSelected("false");
				}
				testList.get(arg2).setIsSelected("true");
				selectedTestCase = testList.get(arg2);
				
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v == btnSubmit){
				if(selectedTestCase == null){
					Util.alertMessage(TestInProcessActivity.this, "Please select some test case");
				}else{
					eventName = "Test In Process";
					reportEvent();
				}
			}else if(v == btnEndTest){
				eventName = "End Test";
				reportEvent();
			}
			
		}

	};

	public void reportEvent(){
	
		if (Util.isNetworkAvailable(TestInProcessActivity.this)) {
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
							.add(new BasicNameValuePair("checkpoint_id", checkPoint.getCheckPointId()));
					nameValuePairs.add(new BasicNameValuePair("img", ""));
					nameValuePairs.add(new BasicNameValuePair("notes", ""));
					if(eventName.equalsIgnoreCase("End Test"))
						nameValuePairs.add(new BasicNameValuePair("desc", ""));
					else
						nameValuePairs.add(new BasicNameValuePair("desc", selectedTestCase.getTest_description()));
					AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(
							TestInProcessActivity.this, "report-event", nameValuePairs,
							true, "Please wait...");
					mLogin.delegate = (AsyncResponseForQRPatrol) TestInProcessActivity.this;
					mLogin.execute();
			
		} else {
			Util.alertMessage(TestInProcessActivity.this,
					"Please check your internet connection");
		}
	}
	
	public class TestListAdapter extends BaseAdapter
	{			
		private Context context;
		private TextView txtTestCase;
		
		public TestListAdapter(Context ctx)
		{
			context = ctx;
		}

//		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return testList.size();
		}

//		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return testList.get(position);
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
			    convertView = inflater.inflate(R.layout.listitem_layout, parent, false);
			}

			txtTestCase = (TextView)convertView.findViewById(R.id.listText);
			txtTestCase.setText(testList.get(position).getTest_description());
			
			if(testList.get(position).getIsSelected().equalsIgnoreCase("true")){
				txtTestCase.setBackgroundColor(color.black_start_grd);
				txtTestCase.setTextColor(Color.WHITE);
			}else{
				txtTestCase.setBackgroundColor(Color.WHITE);
				txtTestCase.setTextColor(Color.BLACK);
			}
			
			return convertView;
		}
	}
	
	@Override
	public void processFinish(String output, String methodName) {
		if(methodName.equals("fetch-testlist")){
			testList = parser.fetchTestList(output);
			listView.setAdapter(adapter);
		}else if(methodName.equals("report-event")){
			String response = parser.getEventResponse(output);
			if(!response.equals("failure")){
				if(eventName.equalsIgnoreCase("End Test")){
					DashboardActivity.officer.setIsTestInProcess("false");
					DashboardActivity.officer.setCheckPointBeingTested("0");
					
					Editor editor = patrolPrefs.edit();
					editor.putString("eventName", "End Test");
					editor.commit();
				}else{
					Editor editor = patrolPrefs.edit();
					editor.putString("eventName", "Reported "+selectedTestCase.getTest_description()+" for "+checkPoint.getName());
					editor.commit();
				}
				
				Util.showToast(TestInProcessActivity.this, "Event submitted successfully.");
				finish();
			}
		}
	}
}
