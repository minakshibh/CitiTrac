package com.qrpatrol.android;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog.Builder;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.qrpatrol.activity.DashboardActivity;
import com.qrpatrol.activity.Splash;
import com.qrpatrol.schedule.ScheduleListActivity;
import com.qrpatrol.util.Util;
 
public class GCMIntentService extends GCMBaseIntentService {
 
    private static final String TAG = "GCMIntentService";
     
    private Controller aController = null;
    SharedPreferences tutorPrefs;
    String userid,uidi,trigger;
    public GCMIntentService() {
        // Call extended class Constructor GCMBaseIntentService
        super(CommonUtilities.SENDER_ID);
       
         }
 
    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
         
        //Get Global Controller Class object (see application tag in AndroidManifest.xml)
    	tutorPrefs= context.getSharedPreferences("tutor", 0);
    	trigger=tutorPrefs.getString("triger", "");
    	userid=tutorPrefs.getString("userid", "");
    	  Log.d("","GCM calsssod classs userid"+userid);
    	uidi=tutorPrefs.getString("udid", "");
        if(aController == null)
          aController = new Controller();
         
        Log.i(TAG, "Device registered: regId = " + registrationId);
        aController.displayMessageOnScreen(context, 
                                           "Your device registred with GCM");
        
        aController.register(context,"android",registrationId,userid,uidi);
    }
 
    /**
     * Method called on device unregistred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        if(aController == null)
        	aController = new Controller();
        Log.i(TAG, "Device unregistered");
        aController.displayMessageOnScreen(context, 
                                            getString(R.string.gcm_unregistered));
        aController.unregister(context, registrationId);
    }
 
    /**
     * Method called on Receiving a new message from GCM server
     * */
    @Override
    protected void onMessage(final Context context, Intent intent) {
         
        if(aController == null)
            aController = new Controller();
         
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("message");
         
        aController.displayMessageOnScreen(context, message);
        // notifies user
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        Log.d("current task :", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClass().getSimpleName());
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        
         if(componentInfo.getPackageName().equalsIgnoreCase("com.qrpatrol.android")) {
        	SharedPreferences appPrefs = context.getSharedPreferences("qrpatrol_app_prefs", MODE_PRIVATE);
             
            String guardId = appPrefs.getString("guardId", "");
     		String pin = appPrefs.getString("pin", "");
     		if(!(guardId.equals("") || pin.equals(""))){
     		 
     		
//     			AlertDialog.Builder alert = new AlertDialog.Builder(context);
//     			alert.setTitle("CitiTrac");
//     			alert.setMessage(message);
//     			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent scheduleIntent = new Intent(context, ScheduleListActivity.class);
			        	 scheduleIntent.putExtra("trigger", "all");
			        	  scheduleIntent.putExtra("puchNoti", "noti");
			        	 scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			        	 context.startActivity(scheduleIntent);
//					}
//				});
//     			alert.show();
     		 
//        	 if(componentInfo.getClassName().contains("ScheduleListActivity")){
//        	 }
        	
        	 
     		}
//        	 ((Activity) context).finish();
        	 //foreground app
		}else{
			//background app
			 generateNotification(context, message);
			
		}
       
    }
 
    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
         
        if(aController == null)
        	aController = new Controller();
         
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        aController.displayMessageOnScreen(context, message);
        // notifies user
        generateNotification(context, message);
    }
 
    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
         
        if(aController == null)
            aController = new Controller();
         
        Log.i(TAG, "Received error: " + errorId);
        aController.displayMessageOnScreen(context, 
                                   getString(R.string.gcm_error, errorId));
    }
 
    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
         
        if(aController == null)
        	aController = new Controller();
         
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        aController.displayMessageOnScreen(context, 
                        getString(R.string.gcm_recoverable_error,
                        errorId));
        return super.onRecoverableError(context, errorId);
    }
 
    /**
     * Create a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
     
        int icon = R.drawable.app_icon;
        long when = System.currentTimeMillis();
         
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
         
        String title = context.getString(R.string.app_name);
         
        Intent notificationIntent = new Intent(context, Splash.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
         
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
         
        //notification.sound = Uri.parse(
                              // "android.resource://"
                              // + context.getPackageName() 
                              // + "your_sound_file_name.mp3");
         
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);      
 
    }
 
}