package com.qrpatrol.order;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qrpatrol.activity.DashboardActivity;
import com.qrpatrol.android.R;
import com.qrpatrol.async_task.AsyncResponseForQRPatrol;
import com.qrpatrol.async_task.AsyncTaskForQRPatrol;
import com.qrpatrol.modal.CheckPoint;
import com.qrpatrol.modal.Officer;
import com.qrpatrol.util.CheckPointSelection;
import com.qrpatrol.util.CoveredCheckPointDialog;
import com.qrpatrol.util.QRPatrolDatabaseHandler;
import com.qrpatrol.util.Util;

public class PassLogsActivity extends Activity implements OnClickListener,AsyncResponseForQRPatrol, CheckPointSelection{

	/*
	 * Parameters: officer_id description notes observation shift_id
	 * checkpoint_id
	 * 
	 * 
	 * Here is link for the same: http://app.mysecurityguards.com/pass-logs.php
	 */
	EditText desc_Etext, notes_Etext, observation_Etext;
	String desc_str, notes_str, observation_str, en_desc_str, en_notes_str,
			en_observation_str;
	LinearLayout passLog,assignCheckPoint;
	ImageView menu, back;
	private int scannedCheckPoint = 0;
	private QRPatrolDatabaseHandler dbHandler;
	private ArrayList<CheckPoint> schedule;
	private TextView assignedCP;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passlog);
		setUI();
		
	}

	private void setUI() {
		// TODO Auto-generated method stub
		dbHandler = new QRPatrolDatabaseHandler(PassLogsActivity.this);
		schedule = dbHandler.getSchedule("tillNow");
		
		desc_Etext = (EditText) findViewById(R.id.desc);
		notes_Etext = (EditText) findViewById(R.id.notes);
		observation_Etext = (EditText) findViewById(R.id.observation);
		assignCheckPoint=(LinearLayout)findViewById(R.id.assignCheckPoint);
		passLog=(LinearLayout)findViewById(R.id.passLog);
		assignedCP = (TextView)findViewById(R.id.assignedCP);
		back = (ImageView)findViewById(R.id.back);
		menu=(ImageView)findViewById(R.id.menu);
		menu.setVisibility(View.GONE);
	
		passLog.setOnClickListener(this);
		assignCheckPoint.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	private void implementWebservices() {
		// TODO Auto-generated method stub
		desc_str = desc_Etext.getText().toString();
		notes_str = notes_Etext.getText().toString();
		observation_str = observation_Etext.getText().toString();
		if (desc_str.length() > 0) {

			if (notes_str.length() > 0) {

				if (observation_str.length() > 0) {
					try {

						en_desc_str = URLDecoder.decode(desc_str, "UTF-8");
						en_notes_str = URLDecoder.decode(notes_str, "UTF-8");
						en_observation_str = URLDecoder.decode(observation_str,"UTF-8");

						if(scannedCheckPoint == 0){
							Util.alertMessage(PassLogsActivity.this, "Please assign some CheckPoint");
						}else{
							if (Util.isNetworkAvailable(PassLogsActivity.this)) {
	
								ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	
								nameValuePairs.add(new BasicNameValuePair("officer_id", DashboardActivity.officer.getOfficerId()));
								nameValuePairs.add(new BasicNameValuePair("description", en_desc_str));
								nameValuePairs.add(new BasicNameValuePair("notes",en_notes_str));
	
								nameValuePairs.add(new BasicNameValuePair("observation", en_observation_str));
								nameValuePairs.add(new BasicNameValuePair("shift_id", DashboardActivity.officer.getShiftId()));
								nameValuePairs.add(new BasicNameValuePair("checkpoint_id", String.valueOf(scannedCheckPoint)));
	
								AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(PassLogsActivity.this, "pass-logs",
										nameValuePairs, true, "Please wait...");
								mLogin.delegate = (AsyncResponseForQRPatrol) PassLogsActivity.this;
								mLogin.execute();
							} else {
								Util.alertMessage(PassLogsActivity.this,
										"Please check your internet connection");
							}
						}

					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				} else {

					Toast.makeText(PassLogsActivity.this,
							"Please enter the observations", Toast.LENGTH_LONG)
							.show();
				}

			} else {

				Toast.makeText(PassLogsActivity.this, "Please enter the notes",
						Toast.LENGTH_LONG).show();

			}

		} else {

			Toast.makeText(PassLogsActivity.this,
					"Please enter the decription", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.passLog:
			implementWebservices();
			
			break;
		
		case R.id.back:
			finish();
			break;
			
		case R.id.assignCheckPoint:
			CoveredCheckPointDialog dialog = new CoveredCheckPointDialog(PassLogsActivity.this, "Please scan the checkpoint before passing any log");
			dialog.show();
			
			break;
		}
	}

	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnCheckPointSelected(CheckPoint checkPoint) {
		// TODO Auto-generated method stub
		scannedCheckPoint = Integer.parseInt(checkPoint.getCheckPointId());
		assignedCP.setText("Assigned CheckPoint : "+checkPoint.getName());
	}
}
