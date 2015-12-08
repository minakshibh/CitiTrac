package com.qrpatrol.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qrpatrol.android.R;
import com.qrpatrol.async_task.AsyncResponseForQRPatrol;
import com.qrpatrol.async_task.AsyncTaskForQRPatrol;
import com.qrpatrol.modal.CheckPoint;
import com.qrpatrol.patrol.IncidentListActivity;


public class Util {

	static 	Context context;
	
	static public void alertMessage(Context ctx,String str)
	{
		context=ctx;
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("CitiTrac");
		alert.setMessage(str);
		alert.setPositiveButton("OK", null);
		alert.show();
	}
	static public void showToast(Context ctx,String str)
	{
		context=ctx;
		Toast.makeText(context, str, Toast.LENGTH_LONG).show();
	}
	public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
	static public void actionAlertMessage(Context ctx,String str)
	{
		context=ctx;
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("CitiTrac");
		alert.setMessage(str);
		alert.setPositiveButton("OK", null);
		alert.show();
	}
	
	static public void fetchSchedule(Context ctx, String officerId, String shiftId, String trigger, Boolean progressNeeded){
		
			SharedPreferences patrolPrefs = ctx.getSharedPreferences("patrol_prefs", ctx.MODE_PRIVATE);
			String lastUpdated = patrolPrefs.getString("scheduleTimeStamp", "");
			Log.e("lastUpdated schedule", ":::: "+lastUpdated);
			if(lastUpdated == null || lastUpdated.equalsIgnoreCase("null"))
				lastUpdated = "";
			
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			/*nameValuePairs.add(new BasicNameValuePair("officer_id", officer.getOfficerId()));
			nameValuePairs.add(new BasicNameValuePair("shift_id", officer.getShiftId()));
			nameValuePairs.add(new BasicNameValuePair("last_updated_date", lastUpdated));
			*/
			nameValuePairs.add(new BasicNameValuePair("officer_id", officerId));
			nameValuePairs.add(new BasicNameValuePair("shift_id", shiftId));
			nameValuePairs.add(new BasicNameValuePair("last_updated_date",lastUpdated));
			nameValuePairs.add(new BasicNameValuePair("trigger", trigger));
			AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol((Activity) ctx, "officer-schedule", nameValuePairs, progressNeeded, "Fetching Schedule...");
			mLogin.delegate = (AsyncResponseForQRPatrol) ctx;
			mLogin.execute();
		
	}
	
	static public void fetchEvents(Context ctx, String patrolId, Boolean progressNeeded){
		SharedPreferences patrolPrefs = ctx.getSharedPreferences("patrol_prefs", ctx.MODE_PRIVATE);
		String lastUpdated = patrolPrefs.getString("eventTimeStamp", "");
		
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("patrol_id", patrolId));
			nameValuePairs.add(new BasicNameValuePair("last_updated_date", lastUpdated));
			
			AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(
					(Activity) ctx, "fetch-event", nameValuePairs, progressNeeded, "Fetching Events...");
			mLogin.delegate = (AsyncResponseForQRPatrol) ctx;
			mLogin.execute();
	}
	
	static public void fetchIncidents(Context ctx, Boolean progressNeeded){
		
			SharedPreferences appPrefs = ctx.getSharedPreferences("qrpatrol_app_prefs", ctx.MODE_PRIVATE);
			String lastUpdated = appPrefs.getString("incidentTimeStamp", "");
			
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("last_updated_date", lastUpdated));
			
			
			AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(
					(Activity) ctx, "fetch-incident", nameValuePairs, progressNeeded, "Fetching Incidents...");
			mLogin.delegate = (AsyncResponseForQRPatrol) ctx;
			mLogin.execute();
	}
	static public void  fetchOrderList(Context ctx, String shiftid,String checkpointid,Boolean progressNeeded) {
		
		SharedPreferences appPrefs = ctx.getSharedPreferences("qrpatrol_app_prefs", ctx.MODE_PRIVATE);
	
			
		String lastUpdated = appPrefs.getString("orderTimeStamp", "");
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("shift_id", shiftid));//officer.getShiftId()));
		nameValuePairs.add(new BasicNameValuePair("checkpoint_id",checkpointid));
		nameValuePairs.add(new BasicNameValuePair("last_updated", ""));
		
	
	
		Log.e("order", nameValuePairs.toString());
		AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(
				(Activity) ctx, "fetch-orders", nameValuePairs, progressNeeded, "Please wait...");
		mLogin.delegate = (AsyncResponseForQRPatrol) ctx;
		mLogin.execute();
	
	}
	
static public void  fetchLogsList(Context ctx, String shiftid,String checkpointid) {
		
		SharedPreferences appPrefs = ctx.getSharedPreferences("qrpatrol_app_prefs", ctx.MODE_PRIVATE);
	
			
		String lastUpdated = appPrefs.getString("logsTimeStamp", "");
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("shift_id", shiftid));//officer.getShiftId()));
		nameValuePairs.add(new BasicNameValuePair("checkpoint_id",checkpointid));
		nameValuePairs.add(new BasicNameValuePair("last_updated", ""));
		
	
		Log.e("logs", nameValuePairs.toString());
		AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(
				(Activity) ctx, "fetch-logs", nameValuePairs, true, "Please wait...");
		mLogin.delegate = (AsyncResponseForQRPatrol) ctx;
		mLogin.execute();
	
	}
	
	static public boolean isNetworkAvailable(Context mCtx) {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) mCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	public static String showImage(String path, ImageView imageView){
		int targetW = 500;
		 int targetH = 500;

		    // Get the dimensions of the bitmap
		    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		    bmOptions.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(path, bmOptions);
		    int photoW = bmOptions.outWidth;
		    int photoH = bmOptions.outHeight;

		    // Determine how much to scale down the image
		    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

		    // Decode the image file into a Bitmap sized to fill the View
		    bmOptions.inJustDecodeBounds = false;
		    bmOptions.inSampleSize = scaleFactor;
		    bmOptions.inPurgeable = true;

		   Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
		    
//	        bitmap = (Bitmap) data.getExtras().get("data"); 
		   imageView.setImageBitmap(bitmap);
	   	
	        ByteArrayOutputStream bao = new ByteArrayOutputStream(); //convert images into base64
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
	        byte[] ba = bao.toByteArray();
	        String encodedImage = Base64.encodeToString(ba, Base64.DEFAULT);
	        
	        return encodedImage;
	}
	
	static public  void hideKeyboard(Context cxt) {
		context=cxt;
	    InputMethodManager inputManager = (InputMethodManager) cxt.getSystemService(Context.INPUT_METHOD_SERVICE);
	    
	    // check if no view has focus:
	    View view = ((Activity) cxt).getCurrentFocus();
	    if (view != null) {
	        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}
	
	
	public static String createImageFile() {
		try{
		    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		    String imageFileName = "Zira_" + timeStamp;
	
		    String mCurrentPhotoPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+imageFileName+".jpg";
		    return mCurrentPhotoPath;
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	public static String getResponseFromUrl(String functionName, List<NameValuePair> param, Context context){
		String responseString = "";
		
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(context.getResources().getString(R.string.baseUrl)+functionName+".php");
			//HttpPost request = new HttpPost("http://ucoservice.vishalshahi.com/SecureService.svc");
	        request.setEntity(new UrlEncodedFormEntity(param));
	        HttpResponse response = httpClient.execute(request);
	       
	         HttpEntity httpEntity = response.getEntity();
	         responseString = EntityUtils.toString(httpEntity);
	         Log.e(functionName, responseString);
	         
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return responseString;
	}
}
