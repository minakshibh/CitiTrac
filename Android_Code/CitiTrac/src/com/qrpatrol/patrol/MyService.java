package com.qrpatrol.patrol;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.qrpatrol.android.R;
import com.qrpatrol.async_task.AsyncResponseForQRPatrol;
import com.qrpatrol.util.Util;

public class MyService extends Service implements AsyncResponseForQRPatrol{

	private GPSTracker gpsTracker;
	private Handler handler = new Handler();
	private Timer timer = new Timer();
	private Distance pastDistance = new Distance();
	private Distance currentDistance = new Distance();
	public static double DISTANCE;
	boolean flag = true;
	private double totalDistance;
	Context mContext=this;
	TimerTask timerTask;
	String getOfficerId,response;
	String function="updateOfficerLocation";
	Activity aciActivity;
	String responseString = "";
	ArrayList<NameValuePair> nameValuePairs;
	
	
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		gpsTracker = new GPSTracker(this);
		
		 timerTask = new TimerTask() {

			@Override
			public void run() {
				handler.post(new Runnable() {

					@Override
					public void run() {
						
						SharedPreferences prefs =mContext.getSharedPreferences("tutor", MODE_PRIVATE); 
						getOfficerId = prefs.getString("getOfficerId", null);
						if (flag) {
							pastDistance.setLatitude(gpsTracker.getLocation().getLatitude());
							pastDistance.setLongitude(gpsTracker.getLocation()
									.getLongitude());
							Log.d("", "gpsTracker.getLocation().getLatitude()"
									+ gpsTracker.getLocation().getLatitude());
							Log.d("", "gpsTracker.getLocation().getLongitude()"
									+ gpsTracker.getLocation().getLongitude());
							flag = false;
							
							
						} else {
							currentDistance.setLatitude(gpsTracker
									.getLocation().getLatitude());
							currentDistance.setLongitude(gpsTracker
									.getLocation().getLongitude());
							Log.d("", "getLatitude"
									+ gpsTracker.getLocation().getLatitude());
							Log.d("", "getLongitude"
									+ gpsTracker.getLocation().getLongitude());
							flag = comapre_LatitudeLongitude();
							
                           
						}
						 if (Util.isNetworkAvailable(MyService.this)) {
								
							
								
								nameValuePairs = new ArrayList<NameValuePair>();
								
								nameValuePairs.add(new BasicNameValuePair("officer_id", getOfficerId));
								nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(gpsTracker.getLocation().getLatitude())));
								nameValuePairs.add(new BasicNameValuePair("longitude",	String.valueOf(gpsTracker.getLocation().getLongitude())));
								
				
//								AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(aciActivity, "updateOfficerLocation", nameValuePairs,
//										, "Please wait...");
//								mLogin.delegate = (AsyncResponseForQRPatrol) MyService.this;
//								mLogin.execute();
								new GetData().execute();
							}
						// Toast.makeText(mContext,
						// "latitude:"+gpsTracker.getLocation().getLatitude(),
						// 4000).show();

					}
				});

			}
		};

		timer.schedule(timerTask, 0, 5000);
		
		
		
		

	}

	private double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		return (dist);
	}

	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onDestroy() {
		stopSelf();
		Intent intent=new Intent();
		stopService(intent);
		
		
		//
		
		System.out
				.println("--------------------------------onDestroy -stop service ");
		timer.cancel();
		DISTANCE = totalDistance;
		stopService(new Intent(this, MyService.class));
		super.onDestroy();
	}

	
	@Override
	public boolean stopService(Intent name) {
		// TODO Auto-generated method stub
		timer.cancel();
		stopSelf();
		return super.stopService(name);

	}

	public boolean comapre_LatitudeLongitude() {

		if (pastDistance.getLatitude() == currentDistance.getLatitude()
				&& pastDistance.getLongitude() == currentDistance
						.getLongitude()) {
			return false;
		} else {

			final double distance = distance(pastDistance.getLatitude(),
					pastDistance.getLongitude(), currentDistance.getLatitude(),
					currentDistance.getLongitude());
			System.out.println("Distance in mile :" + distance);
			handler.post(new Runnable() {

				@Override
				public void run() {
					float kilometer = 1.609344f;

					totalDistance = totalDistance + distance * kilometer;
					DISTANCE = totalDistance;
					// Toast.makeText(HomeFragment.HOMECONTEXT,
					// "distance in km:"+DISTANCE, 4000).show();

				}
			});

			return true;
		}

	}
	private class GetData extends AsyncTask<Void, Void, Void> {
         
		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
		}

		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(Void... arg0) {
		
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
               
				HttpPost request = new HttpPost(MyService.this.getResources().getString(R.string.baseUrl)+"/"+function+".php");
				
				
					//HttpPost request = new HttpPost("http://ucoservice.vishalshahi.com/SecureService.svc");
			        request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			        HttpResponse response = httpClient.execute(request);
			       
			         HttpEntity httpEntity = response.getEntity();
			         responseString = EntityUtils.toString(httpEntity);
			         Log.e("responseString","responseString"+ responseString);
			         
				} catch (Exception e) {
					e.printStackTrace();
				} 
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
		

		}

	}
	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		Log.e("Output", response);
		Log.e("Output", response);
	}
	
	@SuppressWarnings("deprecation")
	public String uploadFileToserver() {
		
		String serverResponse = "";
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost request = new HttpPost(aciActivity.getResources().getString(R.string.baseUrl)+"/"+function+".php");
			
			
				//HttpPost request = new HttpPost("http://ucoservice.vishalshahi.com/SecureService.svc");
		        request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        HttpResponse response = httpClient.execute(request);
		       
		         HttpEntity httpEntity = response.getEntity();
		         responseString = EntityUtils.toString(httpEntity);
		         Log.e("responseString","responseString"+ responseString);
		         
			} catch (Exception e) {
				e.printStackTrace();
			} 
		
		return serverResponse;
	}
}