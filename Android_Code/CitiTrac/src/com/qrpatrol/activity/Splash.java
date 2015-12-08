package com.qrpatrol.activity;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.qrpatrol.android.R;
import com.qrpatrol.async_task.AsyncResponseForQRPatrol;
import com.qrpatrol.async_task.AsyncTaskForQRPatrol;
import com.qrpatrol.modal.CheckPoint;
import com.qrpatrol.modal.Event;
import com.qrpatrol.modal.Incident;
import com.qrpatrol.modal.Logs;
import com.qrpatrol.modal.Officer;
import com.qrpatrol.modal.Order;
import com.qrpatrol.patrol.PatrolActivity;
import com.qrpatrol.patrol.ScanPatroluserActivity;
import com.qrpatrol.util.QRParser;
import com.qrpatrol.util.QRPatrolDatabaseHandler;
import com.qrpatrol.util.Util;


public class Splash extends Activity implements AsyncResponseForQRPatrol{

	private QRPatrolDatabaseHandler dbHandler;
	private SharedPreferences appPrefs, patrolPrefs;
	private Officer officer;
	private CheckPoint checkpoint;
	private String patrolId, checkPointString="";
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        
        appPrefs = getSharedPreferences("qrpatrol_app_prefs", MODE_PRIVATE);
        
        dbHandler = new QRPatrolDatabaseHandler(this);
        patrolPrefs = getSharedPreferences("patrol_prefs", MODE_PRIVATE);
		patrolId = patrolPrefs.getString("patrolId", "0");
		String guardId, pin;
		guardId = appPrefs.getString("guardId", "");
		pin = appPrefs.getString("pin", "");
		if(guardId.equals("") || pin.equals("")){
			Handler handler = new Handler();
	        handler.postDelayed(new Runnable() {
				public void run() {
					
					Intent mIntent=new Intent(Splash.this,LoginActivity.class);
					Splash.this.finish();
					startActivity(mIntent);
				}
			}, 2000);
		}else{
			/*		mSharedPreferences=Splash.this.getSharedPreferences("MyPref", android.content.Context.MODE_PRIVATE);
					String str=mSharedPreferences.getString("user_email", null);
					if(str==null||str.equals("")){
						*/
			
						officer = dbHandler.getOfficerDetails();
						
						
						Util.fetchIncidents(Splash.this, true);
			
						//						gettingCheckPoint("7");
						
						
						
					/*	
					}
					else {				
					Intent in=new Intent(SplashScreen.this,AvailableSong.class);
					SplashScreen.this.finish();
					startActivity(in);
					}*/
    	}
    }

    private void gettingCheckPoint(String contents) {
		// TODO Auto-generated method stub
		CheckPoint checkPoint = dbHandler.getCheckPoint(contents, null);
		
		if (checkPoint==null) {
			Util.alertMessage(Splash.this, "Invalid CheckPoint. Please try again...");
			
		}else{
			
			Intent intent=new Intent(Splash.this,ScanPatroluserActivity.class);
			intent.putExtra("checkpoint", checkPoint);
			intent.putExtra("Officer", officer);
			intent.putExtra("patrolID", patrolId);
			
			startActivity(intent);
		}
	}
    
    public void goToNextScreen(){
    	Intent intent = new Intent(Splash.this, DashboardActivity.class);
		intent.putExtra("Officer", officer);
		startActivity(intent);
		finish();
    }
    
	@Override
	public void processFinish(String output, String methodName) {
		System.err.println(output);
			QRParser parser = new QRParser(Splash.this);
			if(methodName.equals("officer-schedule")){
			
				ArrayList<CheckPoint> schedule = parser.getSchedule(output);
				dbHandler.updateSchedule(schedule);
				
				ArrayList<CheckPoint> scheduleList = dbHandler.getSchedule("all");
				
				for(int i = 0; i<scheduleList.size() ; i++){
					if(i == scheduleList.size() - 1){
						checkPointString = checkPointString+ scheduleList.get(i).getCheckPointId();
					}else
						checkPointString = checkPointString+ scheduleList.get(i).getCheckPointId()+",";
				}
				
				if (Util.isNetworkAvailable(Splash.this)){
					Util.fetchOrderList(Splash.this,officer.getOfficerId(), officer.getShiftId(),true );//officer.getOfficerId(), officer.getShiftId());
				}else{
					goToNextScreen();
				}
				
				
				
			}else if(methodName.equals("fetch-incident")){
				ArrayList<Incident> incidentList = parser.getIncidents(output);
				dbHandler.updateIncidents(incidentList);
				
				if (Util.isNetworkAvailable(Splash.this)){
					Util.fetchSchedule(Splash.this, officer.getOfficerId(), officer.getShiftId(), "all", true);
				}else{
					goToNextScreen();
				}
			}else if(methodName.equals("fetch-event")){
				
				ArrayList<Event> events = parser.getEvents(output);
				dbHandler.saveEvents(events, patrolId);
				
				goToNextScreen();
				
			}else if(methodName.equals("fetch-orders")){
				ArrayList<Order> array_orderlist = parser.getOrderList(output);
//				dbHandler.deleteOrder();
				dbHandler.updateOrder(array_orderlist);
				
				if (Util.isNetworkAvailable(Splash.this)){
					Util.fetchLogsList(Splash.this,officer.getOfficerId(), officer.getShiftId() );//officer.getOfficerId(), officer.getShiftId());
				}else{
					goToNextScreen();
				}
			}
			else if(methodName.equals("fetch-logs")){
				ArrayList<Logs> array_logslist = parser.getLogList(output);
//					dbHandler.deleteLogs();
					dbHandler.updateLogs(array_logslist);
					
					if(!patrolId.equals("0")){
						if (Util.isNetworkAvailable(Splash.this)){
							Util.fetchEvents(Splash.this, patrolId, true);
						}else{
							goToNextScreen();
						}
					}else{
						goToNextScreen();
					}
					
					
			}
	}
}
