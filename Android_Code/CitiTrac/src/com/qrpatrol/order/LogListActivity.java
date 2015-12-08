package com.qrpatrol.order;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.qrpatrol.android.R;
import com.qrpatrol.async_task.AsyncResponseForQRPatrol;
import com.qrpatrol.modal.Logs;
import com.qrpatrol.modal.Officer;
import com.qrpatrol.util.QRPatrolDatabaseHandler;

public class LogListActivity extends Activity implements
		AsyncResponseForQRPatrol, OnClickListener {
	private ImageView iv_back, iv_menu ;
	private TextView tv_title;
	private ListView lv_loglist;
	private String fetch_logs = "fetch-logs";
	// private SharedPreferences appPrefs;
	private QRPatrolDatabaseHandler dbHandler;
	// private QRParser parser
	private LogsAdapter adapter;
	private Logs logList;
	private ArrayList<Logs> array_loglist;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_loglist);
		initializeLayout();
		setClickListners();
		fetchLogList();
	}

	private void initializeLayout() {
		dbHandler = new QRPatrolDatabaseHandler(LogListActivity.this);
		// parser=new QRParser(LogListActivity.this);
		// appPrefs = getSharedPreferences("qrpatrol_app_prefs", MODE_PRIVATE);

		iv_menu = (ImageView) findViewById(R.id.menu);
		iv_menu.setImageResource(R.drawable.add);
		lv_loglist = (ListView) findViewById(R.id.log_ListView);
		iv_back = (ImageView) findViewById(R.id.back);
		tv_title = (TextView) findViewById(R.id.txtHeader);
	}

	private void setClickListners() {
		tv_title.setText("Logs");
		iv_back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();

			}
		});

		lv_loglist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				logList = new Logs();
				logList = array_loglist.get(position);

				Intent intent = new Intent(LogListActivity.this,
						LogDetailsActivity.class);
				intent.putExtra("Log", logList);
				startActivity(intent);

			}
		});
		iv_menu.setOnClickListener(this);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try{
			fetchLogList();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void fetchLogList() {

		array_loglist = new ArrayList<Logs>();
		array_loglist = dbHandler.getLogs("all");
		adapter = new LogsAdapter(LogListActivity.this);
		lv_loglist.setAdapter(adapter);

		/*
		 * if (Util.isNetworkAvailable(LogListActivity.this)){
		 * 
		 * //String lastUpdated = appPrefs.getString("scheduleTimeStamp", "");
		 * 
		 * ArrayList<NameValuePair> nameValuePairs = new
		 * ArrayList<NameValuePair>(); nameValuePairs.add(new
		 * BasicNameValuePair("shift_id", "10"));//officer.getShiftId()));
		 * nameValuePairs.add(new BasicNameValuePair("checkpoint_id","2"));
		 * nameValuePairs.add(new BasicNameValuePair("last_updated", ""));
		 * 
		 * 
		 * Log.e("logs", nameValuePairs.toString()); AsyncTaskForQRPatrol mLogin
		 * = new AsyncTaskForQRPatrol( LogListActivity.this, fetch_logs,
		 * nameValuePairs, true, "Please wait..."); mLogin.delegate =
		 * (AsyncResponseForQRPatrol) LogListActivity.this; mLogin.execute(); }
		 * else { Util.alertMessage(LogListActivity.this,
		 * "Please check your internet connection"); }
		 */
	}

	public void processFinish(String output, String methodName) {
		if (methodName.equals(fetch_logs)) {
			/*
			 * Log.e("logslist", output); array_loglist =
			 * parser.getLogList(output);
			 * 
			 * 
			 * System.err.println(array_loglist.toString());
			 * 
			 * 
			 * // orderlist = dbHandler.getSchedule(trigger); adapter = new
			 * LogsAdapter(LogListActivity.this);
			 * lv_loglist.setAdapter(adapter);
			 */
		}

	}

	public class LogsAdapter extends BaseAdapter {
		private Context context;
		private TextView checkPointName, description, date;
		// private ImageView delete;
		private Logs logList;

		public LogsAdapter(Context ctx) {
			context = ctx;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return array_loglist.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return array_loglist.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.logs_row, parent, false);
			}

			logList = array_loglist.get(position);

			checkPointName = (TextView) convertView
					.findViewById(R.id.checkpointName);
			description = (TextView) convertView.findViewById(R.id.desription);
			date = (TextView) convertView.findViewById(R.id.date);

			// delete = (ImageView)convertView.findViewById(R.id.imgDelete);
			// delete.setVisibility(View.GONE);

			checkPointName.setText(logList.getDescription());
			date.setText("Date : " + logList.getLast_updated());
			description.setText("Associated CheckPoint : " + logList.getCheckpoint().getName());
			/*
			 * if(trigger.equals("all"))
			 * timeOfCheck.setText("Preffered Time: "+checkPoint
			 * .getPrefferedTime()); else 
			 * timeOfCheck.setText("Checked Time: "+checkPoint
			 * .getCheckedTime());
			 */

			return convertView;
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.menu:
			// record video
			Intent intent=new Intent(LogListActivity.this,PassLogsActivity.class);
			startActivity(intent);
			
			break;
		}
	}
}