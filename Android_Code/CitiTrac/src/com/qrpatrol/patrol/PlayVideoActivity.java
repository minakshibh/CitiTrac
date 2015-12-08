package com.qrpatrol.patrol;

import com.qrpatrol.android.R;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class PlayVideoActivity extends Activity {
	String video_url;
	VideoView mVideoView;
	ImageView iv_back, iv_menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playvideo);
		setUI();

		video_url = getIntent().getStringExtra("video_url");
		Log.d("", "video_urlvideo_url" + video_url);
		Uri uri = Uri.parse(video_url);
		MediaController mc= new MediaController(PlayVideoActivity.this);
		mVideoView.setMediaController(mc);
		mVideoView.setVideoURI(uri);
		mVideoView.requestFocus();
		mVideoView.start();
		
		
	}

	private void setUI() {
		// TODO Auto-generated method stub
		iv_back = (ImageView) findViewById(R.id.back);
		iv_menu = (ImageView) findViewById(R.id.menu);
		iv_menu.setVisibility(View.GONE);
		
		mVideoView = (VideoView) findViewById(R.id.videoView1);
		iv_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
