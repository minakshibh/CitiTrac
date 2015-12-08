package com.qrpatrol.activity;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.qrpatrol.android.R;
import com.qrpatrol.async_task.AsyncResponseForQRPatrol;
import com.qrpatrol.async_task.AsyncTaskForQRPatrol;
import com.qrpatrol.modal.CheckPoint;
import com.qrpatrol.modal.Event;
import com.qrpatrol.modal.Incident;
import com.qrpatrol.modal.Logs;
import com.qrpatrol.modal.Officer;
import com.qrpatrol.modal.Order;
import com.qrpatrol.util.QRParser;
import com.qrpatrol.util.QRPatrolDatabaseHandler;
import com.qrpatrol.util.Util;

public class LoginActivity extends Activity implements AsyncResponseForQRPatrol {

	private EditText txtGuardId, txtPIN;
	private Button btnCheckIn;
	private Officer officer;
	private CheckPoint checkpoint;
	// ArrayList<CheckPoint> schedule=new ArrayList<CheckPoint>();
	private SharedPreferences appPrefs;
	private QRParser parser;
	private QRPatrolDatabaseHandler dbHandler;
	private String patrolId, checkPointString = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		initializeLayout();
		setClickListners();
	}

	public void initializeLayout() {
		parser = new QRParser(LoginActivity.this);
		dbHandler = new QRPatrolDatabaseHandler(LoginActivity.this);

		txtGuardId = (EditText) findViewById(R.id.guardId);
		txtPIN = (EditText) findViewById(R.id.pin);
		btnCheckIn = (Button) findViewById(R.id.checkIn);
		appPrefs = getSharedPreferences("qrpatrol_app_prefs", MODE_PRIVATE);
	}

	public String emptyFieldCheck() {

		if (txtGuardId.getText().toString().trim().equals("")
				|| txtPIN.getText().toString().trim().equals("")) {
			return "Please enter the required fields";
		} else
			return "success";
	}

	public void setClickListners() {
		btnCheckIn.setOnClickListener(listener);
	}

	public View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == btnCheckIn) {
				Util.hideKeyboard(LoginActivity.this);
				String result = emptyFieldCheck();
				if (result.equals("success")) {
					if (Util.isNetworkAvailable(LoginActivity.this)) {
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("guardid",
								txtGuardId.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("pin", txtPIN
								.getText().toString().trim()));
						// nameValuePairs.add(new
						// BasicNameValuePair("login","pawan31oct@mailinator.com"));
						// nameValuePairs.add(new
						// BasicNameValuePair("password","pawan123"));
						//
						AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(
								LoginActivity.this, "officer-login",
								nameValuePairs, true, "Please wait...");
						/*
						 * AsyncTaskForQRPatrol mLogin = new
						 * AsyncTaskForQRPatrol( LoginActivity.this,
						 * "IpadAuthenticate", nameValuePairs, true,
						 * "Please wait...");
						 */

						mLogin.delegate = (AsyncResponseForQRPatrol) LoginActivity.this;
						mLogin.execute();

					} else {
						Util.alertMessage(LoginActivity.this,
								"Please check your internet connection");
					}
				} else {
					Util.alertMessage(LoginActivity.this, result);
				}
			}
		}
	};

	@Override
	public void processFinish(String output, String methodName) {
		// Util.alertMessage(LoginActivity.this, "Login Successful : "+output);
		Log.e("Output", output);

		if (methodName.equals("officer-login")) {
			officer = parser.parseOfficerDetails(output);
			dbHandler.deleteOfficer();
			dbHandler.saveOfficerDetails(officer);

			if (officer != null) {
				if (Util.isNetworkAvailable(LoginActivity.this)) {
					Util.fetchIncidents(LoginActivity.this, true);
				} else {
					Util.alertMessage(LoginActivity.this,
							"Please check your internet connection");
				}
			}
		} else if (methodName.equals("officer-schedule")) {
			// Util.alertMessage(LoginActivity.this,
			// "Login Successful : "+output);

			ArrayList<CheckPoint> schedule = parser.getSchedule(output);

			dbHandler.deleteSchedule();
			dbHandler.updateSchedule(schedule);
			
			for(int i = 0; i<schedule.size() ; i++){
				if(i == schedule.size() - 1){
					checkPointString = checkPointString+ schedule.get(i).getCheckPointId();
				}else
					checkPointString = checkPointString+ schedule.get(i).getCheckPointId()+",";
			}
			
			if (Util.isNetworkAvailable(LoginActivity.this)) {
				Util.fetchOrderList(LoginActivity.this, officer.getShiftId(),
						checkPointString, false);// officer.getOfficerId(),
														// officer.getShiftId());
			} else {
				Util.alertMessage(LoginActivity.this,
						"Please check your internet connection");
			}
			


		} else if (methodName.equals("fetch-incident")) {
			ArrayList<Incident> incidentList = parser.getIncidents(output);
			dbHandler.updateIncidents(incidentList);
			
			if (Util.isNetworkAvailable(LoginActivity.this)) {
				Util.fetchSchedule(LoginActivity.this, officer.getOfficerId(),
						officer.getShiftId(), "all", true);
			} else {
				Util.alertMessage(LoginActivity.this,
						"Please check your internet connection");
			}
			
			
		} else if (methodName.equals("fetch-event")) {

			Editor editor = appPrefs.edit();
			editor.putString("guardId", txtGuardId.getText().toString().trim());
			editor.putString("pin", txtPIN.getText().toString().trim());
			editor.commit();

			ArrayList<Event> events = parser.getEvents(output);
			dbHandler.saveEvents(events, patrolId);

			Intent intent = new Intent(LoginActivity.this,
					DashboardActivity.class);
			intent.putExtra("Officer", officer);
			intent.putExtra("checkpoint", checkpoint);
			startActivity(intent);
			finish();
		} else if (methodName.equals("fetch-orders")) {
			ArrayList<Order> array_orderlist = parser.getOrderList(output);
			dbHandler.deleteOrder();
			dbHandler.updateOrder(array_orderlist);

			if (Util.isNetworkAvailable(LoginActivity.this)) {
				Util.fetchLogsList(LoginActivity.this, officer.getShiftId(),
						checkPointString);
			} else {
				Util.alertMessage(LoginActivity.this,
						"Please check your internet connection");
			}
		} else if (methodName.equals("fetch-logs")) {
			ArrayList<Logs> array_logslist = parser.getLogList(output);
			dbHandler.deleteLogs();
			dbHandler.updateLogs(array_logslist);


			SharedPreferences patrolPrefs = getSharedPreferences(
					"patrol_prefs", MODE_PRIVATE);
			patrolId = patrolPrefs.getString("patrolId", "0");

			if (!patrolId.equals("0")) {
				if (Util.isNetworkAvailable(LoginActivity.this)) {
					Util.fetchEvents(LoginActivity.this, patrolId, true);
				} else {
					Util.alertMessage(LoginActivity.this,
							"Please check your internet connection");
				}

			} else {
				Editor editor = appPrefs.edit();
				editor.putString("guardId", txtGuardId.getText().toString()
						.trim());
				editor.putString("pin", txtPIN.getText().toString().trim());
				editor.commit();

				Intent intent = new Intent(LoginActivity.this,
						DashboardActivity.class);
				intent.putExtra("Officer", officer);
				intent.putExtra("checkpoint", checkpoint);
				startActivity(intent);
				finish();
			}
		}
	}
}