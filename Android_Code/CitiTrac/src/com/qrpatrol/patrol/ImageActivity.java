package com.qrpatrol.patrol;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.qrpatrol.android.R;


public class ImageActivity extends Activity{

	private ZoomableImageView imageView;
	private String url;
	public ImageLoader imageLoader; 
	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image);
		
		imageView = (ZoomableImageView)findViewById(R.id.imgView);
		
		url = getIntent().getStringExtra("url");
		imageLoader=new ImageLoader(ImageActivity.this);
		 
		imageLoader.DisplayImage(url, imageView, "", null);
	}
}
