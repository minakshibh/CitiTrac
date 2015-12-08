package com.qrpatrol.activity;

import com.qrpatrol.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutUsActivity extends Activity{

	private ImageView menu, back;
	private TextView txtDesc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_aboutus);
		
		initializeLayout();
		setClickListeners();
	}
	
	private void initializeLayout() {
		back = (ImageView)findViewById(R.id.back);
		menu = (ImageView)findViewById(R.id.menu);
		txtDesc = (TextView)findViewById(R.id.aboutUsDesc);
		
		txtDesc.setText("At Tracktik we believe that managing a security workforce should be simple and stress-free" +
				" while being efficient and profitable." +
				" We will help you improve the services you offer as well as your workforce management " +
				"and bring them to an entire new level. " +
				"The accountability provided by our system will allow you to compete with demonstrated value.");
		
		menu.setVisibility(View.GONE);
	}
	
	private void setClickListeners() {
		back.setOnClickListener(listener);
	}
	
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if(arg0 == back){
				finish();
			}
		}
	};
	
	
}
