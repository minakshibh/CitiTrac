package com.qrpatrol.activity;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.qrpatrol.android.R;
import com.qrpatrol.async_task.AsyncResponseForQRPatrol;
import com.qrpatrol.async_task.AsyncTaskForQRPatrol;
import com.qrpatrol.modal.Officer;
import com.qrpatrol.util.Util;

public class ForgotPassword extends Activity  implements AsyncResponseForQRPatrol{
	private ImageView back,menu;
	private Button btn_change;
	private TextView txtHeader;
	private EditText oldpass,newpass,confirmpass;
	private SharedPreferences appPrefs;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_forgotpassword);
		
		initializeLayout();
		setClickListeners();
}
	private void initializeLayout() {
		
		SettingActivity.settingACtivities.add(ForgotPassword.this);
		
		back = (ImageView)findViewById(R.id.back);
		menu= (ImageView)findViewById(R.id.menu);
		menu.setVisibility(View.GONE);
		oldpass=(EditText)findViewById(R.id.et_oldpass);
		newpass=(EditText)findViewById(R.id.et_newpass);
		confirmpass=(EditText)findViewById(R.id.et_confirmpass);
		txtHeader=(TextView)findViewById(R.id.txtHeader);
		txtHeader.setText("Forgot Password");
		btn_change=(Button)findViewById(R.id.button_change);
		appPrefs = getSharedPreferences("qrpatrol_app_prefs", MODE_PRIVATE);
	}
	private void setClickListeners() {
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
					
				}
			});
		
		btn_change.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			String check=emptyFieldCheck();
			if(check.equalsIgnoreCase("success"))
			{
				
				if (Util.isNetworkAvailable(ForgotPassword.this)) {
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("guardid",appPrefs.getString("guardId", "")));
					nameValuePairs.add(new BasicNameValuePair("pass", newpass.getText().toString().trim()));
				
					AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(ForgotPassword.this, "change-pass",//change-pass.php
							nameValuePairs, true, "Please wait...");
					Log.e("forgotpass", nameValuePairs.toString());
					mLogin.delegate = (AsyncResponseForQRPatrol) ForgotPassword.this;
					mLogin.execute();

				} else {
					Util.alertMessage(ForgotPassword.this,
							"Please check your internet connection");
				}
			}
			else
			{
				Util.alertMessage(ForgotPassword.this, check);
			}
				
			}
		});
	}

	public String emptyFieldCheck() {

		if (oldpass.getText().toString().trim().equals("")
				|| newpass.getText().toString().trim().equals("")|| confirmpass.getText().toString().trim().equals("")) {
			return "Please enter the required fields";
		} 
		else if(!newpass.getText().toString().trim().equals(confirmpass.getText().toString().trim()))
		{
			return "Password not match";
		}
		else if(!oldpass.getText().toString().trim().equals(appPrefs.getString("pin", "").trim()))
		{
			return "Please enter valid Old Password";
		}
		else
		{
			return "success";
		}
	}
	@Override
	public void processFinish(String output, String methodName) {
	if(methodName.equalsIgnoreCase("change-pass"))
	{
		
		Log.e("forgotpass res=", output);
		String result, message;
		
		try {
			
				JSONObject jsonChildNode = new JSONObject(output);	
				
				result = jsonChildNode.getString("result").toString();
				message = jsonChildNode.getString("message").toString();
				
			if(result.equals("0")){
					
			AlertDialog.Builder alert = new AlertDialog.Builder(ForgotPassword.this);
			alert.setTitle("CitiTrac");
			alert.setMessage("Your password has been changed successfully!!");
			alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				Editor ed=appPrefs.edit();
				ed.putString("pin", newpass.getText().toString().trim());
				ed.commit();
				
				
				for(int i =0; i<SettingActivity.settingACtivities.size(); i++){
					SettingActivity.settingACtivities.get(i).finish();
				}
				SettingActivity.settingACtivities = new ArrayList<Activity>();
				}
			});	
			alert.show();
			}
			else
			{
				Util.alertMessage(ForgotPassword.this, message);	
				}
			}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
	}
		
	}	}