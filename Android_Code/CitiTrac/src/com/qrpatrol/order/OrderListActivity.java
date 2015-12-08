package com.qrpatrol.order;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.android.gms.internal.nu;
import com.qrpatrol.android.R;
import com.qrpatrol.async_task.AsyncResponseForQRPatrol;
import com.qrpatrol.modal.Officer;
import com.qrpatrol.modal.Order;
import com.qrpatrol.util.QRPatrolDatabaseHandler;


public class OrderListActivity extends Activity implements AsyncResponseForQRPatrol{
private ListView lv_order;
private ImageView iv_back,iv_menu;
private TextView tv_tilte;
	private String fetch_orders = "fetch-orders";
//private SharedPreferences appPrefs;
//private QRParser parser;
private Order orderlist;
private OrderAdapter adapter;
private QRPatrolDatabaseHandler dbHandler;
private ArrayList<Order> array_orderlist;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_orderlist);
		
		initializeLayout();
		setClickListners();
		fetchOrderList();
	}
	
	
	private void initializeLayout() {
		
		dbHandler = new QRPatrolDatabaseHandler(OrderListActivity.this);
		//parser=new QRParser(OrderListActivity.this);
		//appPrefs = getSharedPreferences("qrpatrol_app_prefs", MODE_PRIVATE);
		
		lv_order=(ListView)findViewById(R.id.orderListView);
		iv_back=(ImageView)findViewById(R.id.back);
	
		iv_menu=(ImageView)findViewById(R.id.menu);
		iv_menu.setVisibility(View.GONE);
		tv_tilte=(TextView)findViewById(R.id.txtHeader);
		
	}
	private void setClickListners() {
		tv_tilte.setText("Orders");
		iv_back.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
				finish();
				
			}
		});
	
		lv_order.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				orderlist=new Order();
				orderlist = array_orderlist.get(position);
				
				Intent intent = new Intent(OrderListActivity.this,OrderDetailsActivity.class);
				intent.putExtra("order", orderlist);/*
				intent.putExtra("orderid",orderlist.getOrder_ID());
				intent.putExtra("description",orderlist.getDescription());
				intent.putExtra("instruction",orderlist.getInstructions());
				intent.putExtra("checkpointlatitude",orderlist.getCheckpointLat());
				intent.putExtra("checkpointlongitude",orderlist.getCheckPointLon());
				intent.putExtra("lastupdated",orderlist.getLastUpdated());*/
				startActivity(intent);
				}
			});
		
	}
	
	private void fetchOrderList() {
	
		array_orderlist=new ArrayList<Order>();
		array_orderlist = dbHandler.getOrder("all");
		adapter = new OrderAdapter(OrderListActivity.this);
		lv_order.setAdapter(adapter);
	
		/*if (Util.isNetworkAvailable(OrderListActivity.this)){
			
		String lastUpdated = appPrefs.getString("scheduleTimeStamp", "");
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("shift_id", "10"));//officer.getShiftId()));
		nameValuePairs.add(new BasicNameValuePair("checkpoint_id","2"));
		nameValuePairs.add(new BasicNameValuePair("last_updated", ""));
		
		nameValuePairs.add(new BasicNameValuePair("Functionality", "1"));
		nameValuePairs.add(new BasicNameValuePair("shift_id", "2"));
	
		Log.e("order", nameValuePairs.toString());
		AsyncTaskForQRPatrol mLogin = new AsyncTaskForQRPatrol(
				OrderListActivity.this, fetch_orders, nameValuePairs, true, "Please wait...");
		mLogin.delegate = (AsyncResponseForQRPatrol) OrderListActivity.this;
		mLogin.execute();
	}
	else {
			Util.alertMessage(OrderListActivity.this,"Please check your internet connection");
			}*/
	
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try{
			fetchOrderList();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void processFinish(String output, String methodName) {
		if(methodName.equals(fetch_orders)){

			/*System.err.println(output);
			array_orderlist = parser.getOrderList(output);
			System.err.println("list size="+array_orderlist.size());
			//	orderlist = dbHandler.getSchedule(trigger);
			adapter = new OrderAdapter(OrderListActivity.this);
			lv_order.setAdapter(adapter);*/
			}
		
		
	}
	
	
	public class OrderAdapter extends BaseAdapter
	{			
		private Context context;
		private TextView checkPointName, description, date;
	//	private ImageView delete;
		private Order orderList;
		
		public OrderAdapter(Context ctx)
		{
			context = ctx;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return array_orderlist.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return array_orderlist.get(position);
		}
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if(convertView == null){
			    convertView = inflater.inflate(R.layout.order_row, parent, false);
			}

			orderList = array_orderlist.get(position);
			
			checkPointName = (TextView)convertView.findViewById(R.id.checkpointId);
			date = (TextView)convertView.findViewById(R.id.last_updated);
			description = (TextView)convertView.findViewById(R.id.description);
		

			// delete = (ImageView)convertView.findViewById(R.id.imgDelete);
			//  delete.setVisibility(View.GONE);
		    
		    checkPointName.setText(orderList.getDescription());
		    date.setText("Date : "+orderList.getLastUpdated());
		    description.setText("Associated CheckPoint : "+orderList.getCheckpoint().getName());
		    description.setTypeface(null, Typeface.BOLD);
		    
		  /*  if(trigger.equals("all"))
		    	timeOfCheck.setText("Preffered Time: "+checkPoint.getPrefferedTime());
		    else
		    	timeOfCheck.setText("Checked Time: "+checkPoint.getCheckedTime());*/
		    
		    
			return convertView;
		}
	}
}