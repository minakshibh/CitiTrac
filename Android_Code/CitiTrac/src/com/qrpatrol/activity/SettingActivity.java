package com.qrpatrol.activity;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.qrpatrol.android.R;
import com.qrpatrol.modal.Officer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingActivity  extends Activity {
	ImageView back,menu;
	TextView txtHeader,textView_changepass;
	static ArrayList<Activity> settingACtivities;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_settings);
		
		initializeLayout();
		setClickListeners();
}

	private void setClickListeners() {
		back.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			finish();
				
			}
		});
		textView_changepass.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			
			
			Intent intent = new Intent(SettingActivity.this, ForgotPassword.class);
			startActivity(intent);
							
			}
		});
	}

	private void initializeLayout() {
		settingACtivities = new ArrayList<Activity>();
		settingACtivities.add(SettingActivity.this);
		
		back = (ImageView)findViewById(R.id.back);
		menu= (ImageView)findViewById(R.id.menu);
		menu.setVisibility(View.GONE);
		txtHeader=(TextView)findViewById(R.id.txtHeader);
		txtHeader.setText("Settings");
		textView_changepass=(TextView)findViewById(R.id.textView_changepass);
	}
}