package com.qrpatrol.util;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qrpatrol.android.R;
import com.qrpatrol.modal.CheckPoint;

public class CoveredCheckPointDialog {

	private Context ctx;
	private String messasge;
	private QRPatrolDatabaseHandler dbHandler;
	private ArrayList<CheckPoint> schedule;
	private CheckPointSelection delegate = null;
	
	public CoveredCheckPointDialog(Context context, String message){
		this.ctx = context;
		this.messasge = message;
		this.delegate = (CheckPointSelection) context;
	}
	
	public void show(){
		dbHandler = new QRPatrolDatabaseHandler(ctx);
		schedule = dbHandler.getSchedule("tillNow");
		
		if(schedule.size() > 0){
			final Dialog alert = new Dialog(ctx);
			alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alert.setContentView(R.layout.list_dialog_layout);
			
			final ListView listView = (ListView)alert.findViewById(R.id.checkPointList);
		    
			TextView txtHeader = (TextView)alert.findViewById(R.id.txtHeader);
			txtHeader.setText("Select CheckPoint...");
		    listView.setAdapter(new ArrayAdapter<CheckPoint>(ctx, R.layout.listitem_layout, schedule));
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					alert.dismiss();
					
					try{
//						scannedCheckPoint = Integer.parseInt(schedule.get(arg2).getCheckPointId());
//						assignedCP.setText("Assigned CheckPoint : "+schedule.get(arg2).getName());
						
						delegate.OnCheckPointSelected(schedule.get(arg2));
					}catch(Exception e){
						e.printStackTrace();
						Util.alertMessage(ctx, e.getMessage());
					}
					
//					Toast.makeText(ctx, "Option selected "+schedule.get(arg2).getName(), Toast.LENGTH_LONG).show();
				}
			});
			
			alert.setCancelable(true);
			alert.show();
		}else{
			/*try {
				Intent intent = new Intent(
						"com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE
				// for bar// codes
				startActivityForResult(intent, 0);
			} catch (Exception e) {
				Uri marketUri = Uri
						.parse("market://details?id=com.google.zxing.client.android");
				Intent marketIntent = new Intent(Intent.ACTION_VIEW,
						marketUri);
				startActivity(marketIntent);
			}*/
			Util.alertMessage(ctx, messasge);
		}
	}
}
