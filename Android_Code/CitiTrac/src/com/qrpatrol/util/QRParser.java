package com.qrpatrol.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.qrpatrol.modal.CheckPoint;
import com.qrpatrol.modal.Event;
import com.qrpatrol.modal.Incident;
import com.qrpatrol.modal.Logs;
import com.qrpatrol.modal.Officer;
import com.qrpatrol.modal.Order;
import com.qrpatrol.modal.TestCase;
import com.qrpatrol.order.LogListActivity.LogsAdapter;

public class QRParser {
	private Context context;
	private SharedPreferences appPrefs, patrolPrefs;
	
	public QRParser(Context ctx){
		context = ctx;
		appPrefs = context.getSharedPreferences("qrpatrol_app_prefs", context.MODE_PRIVATE);
		patrolPrefs = ctx.getSharedPreferences("patrol_prefs", ctx.MODE_PRIVATE);
	}
	

	public Officer parseOfficerDetails(String response){
	
		Officer officer = new Officer();
		String result, message;
		
		try {
			
			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			
			if(result.equals("0")){
				officer.setOfficerId(jsonChildNode.getString("Officer_Id").toString());
				officer.setFirstName(jsonChildNode.getString("firstname").toString());
				officer.setLastName(jsonChildNode.getString("lastname").toString());
				officer.setEmail(jsonChildNode.getString("email").toString());
				officer.setContactInfo(jsonChildNode.getString("contact_number").toString());
				officer.setAlt_info(jsonChildNode.getString("alt_c_number").toString());
				officer.setPer_address(jsonChildNode.getString("permanent_address").toString());
				officer.setCur_address(jsonChildNode.getString("current_address").toString());
				officer.setState(jsonChildNode.getString("state").toString());
				officer.setCity(jsonChildNode.getString("city").toString());
				officer.setCountry(jsonChildNode.getString("country").toString());
				officer.setZipcode(jsonChildNode.getString("zipcode").toString());
				officer.setDOJ(jsonChildNode.getString("DOJ").toString());
				officer.setShiftId(jsonChildNode.getString("shift_id").toString());
				officer.setIsTestInProcess(jsonChildNode.getString("IsTestInProcess").toString());
				officer.setCheckPointBeingTested(jsonChildNode.getString("CheckPointBeingTested").toString());
				
				String patrolId = jsonChildNode.getString("patrol_id").toString();
				String eventName = jsonChildNode.getString("event_name").toString();
				
				Editor editor = patrolPrefs.edit();
				editor.putString("patrolId", patrolId);
				editor.putString("eventName", eventName);
				
				editor.commit();
				
			}else{
				officer = null;
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return officer;
	}
	
	public String getEventResponse(String response){

		String result, message, patrolId="";
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			try{
			patrolId = jsonChildNode.getString("patrol_id").toString();
			}catch(Exception e){
				e.printStackTrace();
			}
			if(result.equals("1")){
				patrolId = "failure";
				showAlert(message);
			}
//			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return patrolId;
	}
	
	public void showAlert(String message){
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("CitiTrac");
		alert.setMessage(message);
		alert.setPositiveButton("Okay", null);
		alert.show();
	}
	
	public ArrayList<Incident> getIncidents(String response){
		String result, message, lastUpdated;
		ArrayList<Incident> incidentList = new ArrayList<Incident>();
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			lastUpdated = jsonChildNode.getString("greatest_last_updated").toString();
			
			if(result.equals("0")){
				Editor editor = appPrefs.edit();
				editor.putString("incidentTimeStamp", lastUpdated);
				editor.commit();
				
				JSONArray jsonArray = new JSONArray(jsonChildNode.getString("incident_list"));
				
				for(int i=0; i < jsonArray.length(); i++)
		        {
		            Incident incident = new Incident();
		            JSONObject jsonObj = jsonArray.getJSONObject(i);
		             
		            incident.setId(jsonObj.optString("inc_id").toString());
		            incident.setDesc(jsonObj.optString("inc_desc").toString());
		            incident.setIsActive(jsonObj.getString("isactive")
							.toString());

		            incident.setIsChecked(false);
		            incidentList.add(incident);
		       }
			}else{
				incidentList = new ArrayList<Incident>();
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return incidentList;
	}
	
	
	public ArrayList<TestCase> fetchTestList(String response){
		String result, message;
		ArrayList<TestCase> testList = new ArrayList<TestCase>();
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			
			if(result.equals("0")){
				
				JSONArray jsonArray = new JSONArray(jsonChildNode.getString("testlist"));
				
				for(int i=0; i < jsonArray.length(); i++)
		        {
		            TestCase testCase = new TestCase();
		            JSONObject jsonObj = jsonArray.getJSONObject(i);
		             
		            testCase.setTestpoint_id(jsonObj.optString("testpoint_id").toString());
		            testCase.setTest_description(jsonObj.optString("test_description").toString());
		            
		            
		            testList.add(testCase);
		       }
			}else{
				testList = new ArrayList<TestCase>();
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return testList;
	}
	
	
	public ArrayList<Event> getEvents(String response){
		String result, message, lastUpdated;
		ArrayList<Event> eventList = new ArrayList<Event>();
		try {
			QRPatrolDatabaseHandler dbHandler = new QRPatrolDatabaseHandler(context);
			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			lastUpdated = jsonChildNode.getString("MaxLastUpdatedValue").toString();
			
			if(result.equals("0")){
				
				Editor editor = patrolPrefs.edit();
				editor.putString("eventTimeStamp", lastUpdated);
				editor.commit();
				
				JSONArray jsonArray = new JSONArray(jsonChildNode.getString("event_list"));
				
				for(int i=0; i < jsonArray.length(); i++)
		        {
		            Event event = new Event();
		            JSONObject jsonObj = jsonArray.getJSONObject(i);
		            event.setId(jsonObj.optString("event_ID").toString());
		            event.setName(jsonObj.optString("event_name").toString());
		            event.setLatitude(jsonObj.optString("latitude").toString());
		            event.setLongitude(jsonObj.optString("longitude").toString());
		            String checkPointId = jsonObj.optString("checkpoint_ID").toString();
		            
//		            CheckPoint checkPoint = dbHandler.getCheckPoint(checkPointId);
		            CheckPoint checkPoint = new CheckPoint();
		            checkPoint.setCheckPointId(checkPointId);
		            
		            event.setCheckPoint(checkPoint);
		            event.setReportedTime(jsonObj.optString("reported_time").toString());
		            event.setIncidentDesc(jsonObj.optString("incident_description").toString());
		            event.setIsSentViaEmail(jsonObj.optString("isSentViaEmail").toString());
		            event.setText(jsonObj.optString("text").toString());
		            event.setImageUrl(jsonObj.optString("imageurl").toString());
		            event.setSoundUrl(jsonObj.optString("soundurl").toString());
		            event.setOfficerPic(jsonObj.optString("officer_pic").toString());
		            event.setNotes(jsonObj.optString("notes").toString());
		            event.setTestDesc(jsonObj.optString("test_desc").toString());
		            event.setIsActive(jsonObj.getString("is_active").toString());

		            eventList.add(event);
		       }
			}else{
				eventList = new ArrayList<Event>();
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return eventList;
	}
	
	public ArrayList<CheckPoint> getSchedule(String response){
//		"checkpoint_id":"1","preffered_time":"10:30:00","priority":"1","checkpoint_name":"acora","address":"123456",
//		"city":"mohali","state":"punjab","country":"india","zipcode":"123654",
//		"contact_info":"12369756","alternate_contact":"12369756","latitude":"12.5","longitude":"45.5",
//		"notes":"inprogress","open_timings":"00:00:10","close_timing":"00:00:12"},
		
		ArrayList<CheckPoint> schedule = new ArrayList<CheckPoint>();
	//	CheckPoint checkpoint = new CheckPoint();
		String result, message, lastUpdated;
		
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			 if (jsonChildNode.getString("greatest_last_updated").equals("null")){
			        return null;
			  }else{
				 lastUpdated = jsonChildNode.getString("greatest_last_updated").toString();
			  }
			
			
			if(result.equals("0")){
				Editor editor = patrolPrefs.edit();
				editor.putString("scheduleTimeStamp", lastUpdated);
				editor.commit();
				
				JSONArray jsonArray = new JSONArray(jsonChildNode.getString("checkpoint"));
				
				for(int i=0; i < jsonArray.length(); i++)
		        {
		            CheckPoint checkPoint = new CheckPoint();
		            JSONObject jsonObj = jsonArray.getJSONObject(i);
		             
		            checkPoint.setCheckPointId(jsonObj.optString("checkpoint_id").toString());
		            checkPoint.setPrefferedTime(jsonObj.optString("preffered_time").toString());
		            checkPoint.setPriority(jsonObj.optString("priority").toString());
		            checkPoint.setName(jsonObj.optString("checkpoint_name").toString());
		            checkPoint.setAddress(jsonObj.optString("address").toString());
		            checkPoint.setCity(jsonObj.optString("city").toString());
		            checkPoint.setState(jsonObj.optString("state").toString());
		            checkPoint.setCountry(jsonObj.optString("country").toString());
		            checkPoint.setZipcode(jsonObj.optString("zipcode").toString());
		            checkPoint.setContactInfo(jsonObj.optString("contact_info").toString());
		            checkPoint.setAltContact(jsonObj.optString("alternate_contact").toString());
		            checkPoint.setLatitude(jsonObj.optString("latitude").toString());
		            checkPoint.setLongitude(jsonObj.optString("longitude").toString());
		            checkPoint.setNotes(jsonObj.optString("notes").toString());
		            checkPoint.setOpenTimings(jsonObj.optString("open_timings").toString());
		            checkPoint.setCloseTimings(jsonObj.optString("close_timing").toString());
		            checkPoint.setIsActive(jsonObj.getString("is_active").toString());

		            schedule.add(checkPoint);
		   
		       }
			}else{
				schedule = new ArrayList<CheckPoint>();
				//checkpoint=null;
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return schedule;
	}
	
	public ArrayList<Order> getOrderList(String response){
		String result, message, checkpoints;
		ArrayList<Order> orderList = new ArrayList<Order>();
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			checkpoints = jsonChildNode.getString("checkpoints").toString();
			
			if(result.equals("0")){
			/*	Editor editor = appPrefs.edit();
				editor.putString("incidentTimeStamp", lastUpdated);
				editor.commit();*/
				JSONObject obj1 = new JSONObject(checkpoints);
				JSONObject obj2 = new JSONObject(obj1.getString("order"));
				JSONArray jsonArray = new JSONArray(obj2.getString("checkpoints"));
				
				for(int i=0; i < jsonArray.length(); i++)
		        {
					Order order = new Order();
		            JSONObject jsonObj = jsonArray.getJSONObject(i);
		             
		            order.setCheckPointId((jsonObj.optString("checkpoint_id").toString()));
		            order.setOrder_ID((jsonObj.optString("order_ID").toString()));
		            order.setDescription(jsonObj.optString("description").toString());
		            order.setInstructions(jsonObj.optString("instruction").toString());
		            order.setCheckpointLat(jsonObj.optString("checkpoint_latitude").toString());
		            order.setCheckPointLon(jsonObj.optString("checkpoint_longitude").toString());
		            order.setLastUpdated(jsonObj.optString("last_updated").toString());
		            order.setIsActive(jsonObj.getString("is_active").toString());

		            CheckPoint checkPoint = new CheckPoint();
		            checkPoint.setCheckPointId(order.getCheckPointId());
		            
		            order.setCheckpoint(checkPoint);
		            
		            orderList.add(order);
		       }
			}else{
				orderList = new ArrayList<Order>();
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return orderList;
	}
	
	public ArrayList<Logs> getLogList(String response){
		String result, message, checkpoints;
		ArrayList<Logs> logsList = new ArrayList<Logs>();
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			checkpoints = jsonChildNode.getString("checkpoints").toString();
			
			if(result.equals("0")){
			/*	Editor editor = appPrefs.edit();
				editor.putString("incidentTimeStamp", lastUpdated);
				editor.commit();*/
				JSONObject obj1 = new JSONObject(checkpoints);
				JSONObject obj2 = new JSONObject(obj1.getString("logs"));
				JSONArray jsonArray = new JSONArray(obj2.getString("checkpoints"));
				
				for(int i=0; i < jsonArray.length(); i++)
		        {
					Logs obj_logList = new Logs();
		            JSONObject jsonObj = jsonArray.getJSONObject(i);
		             
		            obj_logList.setCheckPointId((jsonObj.optString("checkpoint_id").toString()));
		            obj_logList.setLogId((jsonObj.optString("log_ID").toString()));
		            obj_logList.setPassedByOfficerId(jsonObj.optString("passedby_officer_id").toString());
		            obj_logList.setPassedByOfficerName(jsonObj.optString("passedby_officer_name").toString());
		            obj_logList.setPassedByOfficerContactInfo(jsonObj.optString("passedby_officer_contact_detail").toString());
		            obj_logList.setDescription(jsonObj.optString("description").toString());
		            obj_logList.setNotes(jsonObj.optString("notes").toString());
		            obj_logList.setObservation(jsonObj.optString("observation").toString());
		            obj_logList.setShiftId(jsonObj.optString("shift_id").toString());
		            obj_logList.setLast_updated(jsonObj.optString("last_updated").toString());
		            obj_logList.setIsActive(jsonObj.getString("is_active").toString());
		            logsList.add(obj_logList);
		           
		       }
			}else{
				logsList = new ArrayList<Logs>();
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return logsList;
	}
}

