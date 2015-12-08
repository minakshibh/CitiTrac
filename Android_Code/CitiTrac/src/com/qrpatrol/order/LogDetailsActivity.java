package com.qrpatrol.order;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qrpatrol.activity.DashboardActivity;
import com.qrpatrol.android.R;
import com.qrpatrol.async_task.AsyncResponseForQRPatrol;
import com.qrpatrol.async_task.AsyncTaskForQRPatrol;
import com.qrpatrol.modal.CheckPoint;
import com.qrpatrol.modal.Logs;
import com.qrpatrol.modal.Officer;
import com.qrpatrol.modal.Order;
import com.qrpatrol.order.LogListActivity.LogsAdapter;
import com.qrpatrol.schedule.ScheduleDetailsActivity;
import com.qrpatrol.util.QRParser;
import com.qrpatrol.util.QRPatrolDatabaseHandler;
import com.qrpatrol.util.Util;

public class LogDetailsActivity extends Activity implements
		AsyncResponseForQRPatrol {

	private ImageView iv_back, iv_list;
	private TextView tv_title, tv_ordername, tv_description, tv_date,
			tv_instruction, tv_latlog, tv_notes, tv_passedBy;
	private String mark_log_done = "mark-log-done";
	
	private QRPatrolDatabaseHandler dbHandler;
	private QRParser parser;
	private Button iv_markdone, viewCheckPoint, contactOfficer;
	private Logs log;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_logdetails);
		initializeLayout();
		setClickListners();

	}

	private void initializeLayout() {
		dbHandler = new QRPatrolDatabaseHandler(LogDetailsActivity.this);
		parser = new QRParser(LogDetailsActivity.this);
		
		iv_back = (ImageView) findViewById(R.id.back);
		iv_markdone = (Button) findViewById(R.id.imageView_checked);
		tv_title = (TextView) findViewById(R.id.txtHeader);
		iv_list = (ImageView) findViewById(R.id.menu);
		iv_list.setVisibility(View.GONE);

		tv_ordername = (TextView) findViewById(R.id.textView_ordername);
		tv_date = (TextView) findViewById(R.id.textView_date);
		tv_description = (TextView) findViewById(R.id.textView_description);
		tv_instruction = (TextView) findViewById(R.id.instruction);
		tv_latlog = (TextView) findViewById(R.id.textView_direction);
		viewCheckPoint = (Button)findViewById(R.id.viewCheckpoint);
		tv_notes = (TextView)findViewById(R.id.notes);
		tv_passedBy = (TextView)findViewById(R.id.passedBy);
		contactOfficer = (Button)findViewById(R.id.contactOfficer);
		log = (Logs)getIntent().getParcelableExtra("Log");
		if (log.getCheckpoint() == null) {
			viewCheckPoint.setVisibility(View.GONE);
			tv_latlog.setVisibility(View.GONE);
		}else{
			viewCheckPoint.setVisibility(View.VISIBLE);
			tv_latlog.setText("CheckPoint : "
					+ log.getCheckpoint().getName());
		}
		
		tv_ordername.setText("LogId : "
				+ log.getLogId());
		tv_description.setText(log.getDescription());
		tv_instruction.setText(log.getObservation());
		tv_date.setText(log.getLast_updated());
		tv_notes.setText(log.getNotes());
		tv_passedBy.setText(log.getPassedByOfficerName());
	}

	private void markDone() {
		if (Util.isNetworkAvailable(LogDetailsActivity.this)) {

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("officer_id", DashboardActivity.officer
					.getOfficerId()));// officer.getOfficerId()));
			nameValuePairs.add(new BasicNameValuePair("id", log.getLogId()));
			nameValuePairs.add(new BasicNameValuePair("trigger", "log"));

			Log.e("mark_log_done", nameValuePairs.toString());
			AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(
					LogDetailsActivity.this, mark_log_done, nameValuePairs,
					true, "Please wait...");
			mLogin.delegate = (AsyncResponseForQRPatrol) LogDetailsActivity.this;
			mLogin.execute();
		} else {
			Util.alertMessage(LogDetailsActivity.this,
					"Please check your internet connection");
		}

	}

	private void setClickListners() {
		tv_title.setText("Log Details");
		iv_back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();

			}
		});
		iv_markdone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				markDone();

			}
		});

		viewCheckPoint.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LogDetailsActivity.this, ScheduleDetailsActivity.class);
				intent.putExtra("checkPoint",log.getCheckpoint());
				startActivity(intent);
			}
		});
		
		contactOfficer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String uri = "tel:" + log.getPassedByOfficerContactInfo();
				 Intent intent = new Intent(Intent.ACTION_CALL);
				 intent.setData(Uri.parse(uri));
				 startActivity(intent);
			}
		});
	}

	public void processFinish(String output, String methodName) {
		if (methodName.equals(mark_log_done)) {
			try {
				JSONObject json = new JSONObject(output);

				String result = json.getString("result");
				String message = json.getString("message");

				if (result.equals("0")) {
					if (Util.isNetworkAvailable(LogDetailsActivity.this)) {
						ArrayList<CheckPoint> scheduleList = dbHandler.getSchedule("all");
						String checkPointString = "";
						
						for(int i = 0; i<scheduleList.size() ; i++){
							if(i == scheduleList.size() - 1){
								checkPointString = checkPointString+ scheduleList.get(i).getCheckPointId();
							}else
								checkPointString = checkPointString+ scheduleList.get(i).getCheckPointId()+",";
						}
						
						Util.fetchLogsList(LogDetailsActivity.this,
								DashboardActivity.officer.getShiftId(),checkPointString);// );

					} else {
						Util.alertMessage(LogDetailsActivity.this,
								"Please check your internet connection");
					}
				} else
					Util.alertMessage(LogDetailsActivity.this, message);
			} catch (Exception e) {
			}

		} else if (methodName.equals("fetch-logs")) {
			ArrayList<Logs> logList = parser.getLogList(output);
			dbHandler.deleteLogs();
			dbHandler.updateLogs(logList);
			
			finish();
		}
	}

}
