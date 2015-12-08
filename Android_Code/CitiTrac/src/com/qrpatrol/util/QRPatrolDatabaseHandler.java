package com.qrpatrol.util;

import java.lang.reflect.Array;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qrpatrol.modal.CheckPoint;
import com.qrpatrol.modal.Event;
import com.qrpatrol.modal.Incident;
import com.qrpatrol.modal.Logs;
import com.qrpatrol.modal.Officer;
import com.qrpatrol.modal.Order;
import com.qrpatrol.patrol.PatrolActivity;

public class QRPatrolDatabaseHandler extends SQLiteOpenHelper{
 
    //The Android's default system path of your application database.
    @SuppressLint("SdCardPath")
	private static String DB_PATH = "/data/data/com.qrpatrol.activity/databases/";
 
    private static String DB_NAME = "QRPatrol_db.sqlite";
 
    private SQLiteDatabase myDataBase; 
 
    private final Context myContext;
    
    private String TABLE_SCHEDULE = "Schedule";
    private String TABLE_INCIDENT = "Incident";
	private String TABLE_OFFICER = "Officer";
	private String TABLE_EVENT = "Events";
	private String TABLE_ORDER = "Orders";
	private String TABLE_LOGS = "Logs";
	
	//field for incident table
	private String Incident_Id = "Inc_id";
	private String Incident_Desc = "Inc_desc";
	private String Incident_isChecked = "isChecked";
	
	//field for schedule table
	private String CheckPoint_Id = "CheckPointId";
	private String PrefferedTime = "PrefferedTime";
	private String Priority = "Priority";
	private String CheckPointName = "CheckPointName";
	private String Address = "Address";
	private String City = "City";
	private String State = "State";
	private String Country = "Country";
	private String ZipCode = "ZipCode";
	private String ContactInfo= "ContactInfo";
	private String AlternateContact = "AlternateContact";
	private String Latitude = "Latitude";
	private String Longitude = "Longitude";
	private String Notes = "Notes";
	private String OpenTimings = "OpenTimings";
	private String CloseTimings= "CloseTimings";
	private String isChecked = "isChecked";
	private String CheckedTime = "checkedTime";
	
	//field for Officer table
	private String officerId = "officerId";
	private String firstName = "firstName";
	private String lastName = "lastName";
	private String email = "email";
	private String contactInfo = "contactInfo";
	private String alt_info = "alt_info";
	private String per_address = "per_address";
	private String cur_address = "cur_address";
	private String DOJ = "DOJ";
	private String shiftId = "shiftId";
	private String isTestInProcess ="isTestInProcess";
	private String checkPointBeingTested = "checkPointBeingTested";

	//field for Event table
	private String eventName = "eventName";
	private String reportedTime = "reportedTime";
	private String isSentViaEmail = "isSentViaEmail";
	private String text = "text";
	private String imageUrl = "imageUrl";
	private String soundUrl = "soundUrl";
	private String eventId ="eventId";
	private String patrolID = "patrolID";
    private String testDesc = "testDesc";
	
	//field for Order table
	//checkpoint_id
	private String orderId = "orderId";
	private String description = "description";
	private String instruction = "instruction";
	//checkpoint_latitude 
	//checkpoint_longitude 
	private String lastupdated ="lastupdated";
	
	
	//field for Log table
		//checkpoint_id"
		private String logid = "logid";
		private String passedbyofficerid = "passedbyofficerid";
		private String passedbyofficername = "passedbyofficername";
		private String passedbyofficercontactdetail = "passedbyofficercontactdetail";
		// description 
		private String notes ="notes";
		private String observation ="observation";
		private String shiftid ="shiftid";
		// last_updated
		
	
	SQLiteCursor cursor;
    
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
	
    public QRPatrolDatabaseHandler(Context context) {
 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
   
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String CREATE_INCIDENT_TABLE = "CREATE TABLE if NOT Exists " + TABLE_INCIDENT + "("
				+ Incident_Id + " TEXT," + Incident_Desc + " TEXT,"+ Incident_isChecked+" TEXT)";
		
		String CREATE_SCHEDULE_TABLE = "CREATE TABLE if NOT Exists " + TABLE_SCHEDULE + "("
				+ CheckPoint_Id + " TEXT," + PrefferedTime + " TEXT," + Priority + " TEXT,"
				+ CheckPointName + " TEXT,"+ Address + " TEXT,"+ City + " TEXT,"+ State + " TEXT,"+ Country + " TEXT,"
				+ ZipCode + " TEXT ,"+ ContactInfo + " TEXT," + AlternateContact + " TEXT,"+ Latitude + " TEXT,"+ Longitude 
				+ " TEXT,"+ Notes + " TEXT,"+ OpenTimings + " TEXT,"+ CloseTimings + " TEXT,"+ isChecked + " TEXT,"+ CheckedTime + " TEXT)";

		String CREATE_OFFICER_TABLE = "CREATE TABLE if NOT Exists " + TABLE_OFFICER + "("
				+ officerId + " TEXT," + firstName + " TEXT," + lastName + " TEXT,"
				+ email + " TEXT,"+ contactInfo + " TEXT,"+ alt_info + " TEXT,"+ per_address + " TEXT,"+ cur_address + " TEXT,"
				+ State + " TEXT ,"+ City + " TEXT," + Country + " TEXT,"+ ZipCode + " TEXT,"+ DOJ 
				+ " TEXT,"+ shiftId + " TEXT," + isTestInProcess +" TEXT,"+ checkPointBeingTested+" TEXT)";
		
		String CREATE_EVENT_TABLE = "CREATE TABLE if NOT Exists " + TABLE_EVENT + "("
				+ eventId + " TEXT,"+ patrolID + " TEXT,"+ eventName + " TEXT," + Latitude + " TEXT," + Longitude + " TEXT,"
				+ CheckPoint_Id + " TEXT,"+ reportedTime + " TEXT,"+ Incident_Desc + " TEXT,"+ isSentViaEmail + " TEXT,"
				+ text + " TEXT," + imageUrl + " TEXT ,"+ soundUrl + " TEXT," +testDesc+" TEXT,"+notes+" TEXT)";
		
		String CREATE_ORDER_TABLE = "CREATE TABLE if NOT Exists " + TABLE_ORDER + "("
				+ CheckPoint_Id + " TEXT,"+ orderId + " TEXT,"+ description + " TEXT," + instruction + " TEXT," + Latitude + " TEXT,"
				+ Longitude + " TEXT,"+ lastupdated + " TEXT)";
		
		String CREATE_LOGS_TABLE = "CREATE TABLE if NOT Exists " + TABLE_LOGS + "("
				+ CheckPoint_Id + " TEXT,"+ logid + " TEXT,"+ passedbyofficerid + " TEXT," + passedbyofficername + " TEXT," + passedbyofficercontactdetail + " TEXT,"
				+ description + " TEXT,"+ notes + " TEXT,"+ observation + " TEXT,"+ shiftid + " TEXT,"+ lastupdated + " TEXT)";
		
		db.execSQL(CREATE_INCIDENT_TABLE);
		db.execSQL(CREATE_SCHEDULE_TABLE);
		db.execSQL(CREATE_OFFICER_TABLE);
		db.execSQL(CREATE_EVENT_TABLE);
		db.execSQL(CREATE_ORDER_TABLE);
		db.execSQL(CREATE_LOGS_TABLE);
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
 
	}
	
	public void deleteSchedule(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SCHEDULE, null, null);
		db.close();
	}

//edit schedule
	public void updateSchedule(ArrayList<CheckPoint> schedule) {
		SQLiteDatabase db = this.getWritableDatabase();
		for(int i = 0; i<schedule.size(); i++){
			CheckPoint checkPoint = schedule.get(i);
			String selectQuery = "SELECT  * FROM "+TABLE_SCHEDULE+" where "+CheckPoint_Id+"="+ checkPoint.getCheckPointId();  
			
			try{
				ContentValues values = new ContentValues();
				values.put(CheckPoint_Id, checkPoint.getCheckPointId()); 
				values.put(PrefferedTime, checkPoint.getPrefferedTime());
				values.put(Priority, checkPoint.getPriority());
				values.put(CheckPointName, checkPoint.getName());
				values.put(Address, checkPoint.getAddress());
				values.put(City, checkPoint.getCity());
				values.put(State, checkPoint.getState());
				values.put(Country, checkPoint.getCountry());
				values.put(ZipCode, checkPoint.getZipcode());
				values.put(ContactInfo, checkPoint.getContactInfo());
				values.put(AlternateContact, checkPoint.getAltContact());
				values.put(Latitude, checkPoint.getLatitude());
				values.put(Longitude, checkPoint.getLongitude());
				values.put(Notes, checkPoint.getNotes());
				values.put(OpenTimings, checkPoint.getOpenTimings());
				values.put(CloseTimings, checkPoint.getCloseTimings());
				
				cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
				if (cursor.moveToFirst()) {
					
					if (!checkPoint.getIsActive().equalsIgnoreCase("true")) 
					{
//								db = this.getWritableDatabase();
							int a =db.delete(TABLE_SCHEDULE, CheckPoint_Id + " = ?",
										new String[] { String
												.valueOf(checkPoint
														.getCheckPointId()) });
					}else {

					// updating row
					int a=db.update(TABLE_SCHEDULE, values, CheckPoint_Id + " = ?",
							new String[] { String.valueOf(checkPoint.getCheckPointId()) });
					}
				}else if(checkPoint.getIsActive().equalsIgnoreCase("true")){
					db.insert(TABLE_SCHEDULE, null, values);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		db.close();
    }
	
	
	
	//edit order
		public void updateOrder(ArrayList<Order> order) {
			SQLiteDatabase db = this.getWritableDatabase();
			for(int i = 0; i<order.size(); i++){
				Order orderlist = order.get(i);
				String selectQuery = "SELECT  * FROM "+TABLE_ORDER+" where "+orderId+"="+ orderlist.getOrder_ID();  
				
				try{
					ContentValues values = new ContentValues();
					values.put(CheckPoint_Id, orderlist.getCheckPointId()); 
					values.put(orderId, orderlist.getOrder_ID());
					values.put(description, orderlist.getDescription());
					values.put(instruction, orderlist.getInstructions());
					values.put(Longitude, orderlist.getCheckpointLat());
					values.put(Latitude, orderlist.getCheckPointLon());
					values.put(lastupdated, orderlist.getLastUpdated());
				
				
					
					cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
					if (cursor.moveToFirst()) {
						// updating row
						if (!orderlist.getIsActive().equalsIgnoreCase("true")) {
							int a =	db.delete(TABLE_ORDER, orderId + " = ?",
										new String[] { String.valueOf(orderlist
												.getOrder_ID()) });
					} else {

							int a=db.update(TABLE_ORDER, values, orderId + " = ?",
								new String[] { String.valueOf(orderlist.getOrder_ID()) });
						}
					}else if(orderlist.getIsActive().equalsIgnoreCase("true")){
						db.insert(TABLE_ORDER, null, values);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			db.close();
	    }
		//delete order
		public void deleteOrder(){
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_ORDER, null, null);
			db.close();
		}
		//get order list
		public ArrayList<Order> getOrder(String trigger) {
		
				ArrayList<Order> array_orderlist = new ArrayList<Order>();
			
				String selectQuery;
				if(trigger.equals("all"))
					selectQuery = "SELECT  * FROM "+TABLE_ORDER;              
				else
					selectQuery = "SELECT  * FROM "+TABLE_ORDER+" where "+isChecked+" = "+"'true'";
				
				SQLiteDatabase db = this.getReadableDatabase();
				
				try{
					cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
					if (cursor.moveToFirst()) {
						do {
							
									
							Order order = new Order();
							order.setCheckPointId(cursor.getString(cursor.getColumnIndex(CheckPoint_Id)));
							order.setOrder_ID(cursor.getString(cursor.getColumnIndex(orderId)));
							order.setDescription(cursor.getString(cursor.getColumnIndex(description)));
							order.setInstructions(cursor.getString(cursor.getColumnIndex(instruction)));
							order.setCheckpointLat(cursor.getString(cursor.getColumnIndex(Longitude)));
							order.setCheckPointLon(cursor.getString(cursor.getColumnIndex(Latitude)));
							order.setLastUpdated(cursor.getString(cursor.getColumnIndex(lastupdated)));
						
							String checkPointID = cursor.getString(cursor.getColumnIndex(CheckPoint_Id));
							order.setCheckpoint(getCheckPoint(checkPointID, db));
							
							array_orderlist.add(order);
						} while (cursor.moveToNext());
					}
					
					cursor.getWindow().clear();
					cursor.close();
					// close inserting data from database
					db.close();
					// return city list
					return array_orderlist;
					
				}
				catch (Exception e)
				{
					e.printStackTrace();
					if (cursor != null)
					{
						cursor.getWindow().clear();
						cursor.close();
					}
					
					db.close();
					return array_orderlist;
				}
			}

		//edit logs
				public void updateLogs(ArrayList<Logs> arraylogs) {
					SQLiteDatabase db = this.getWritableDatabase();
					for(int i = 0; i<arraylogs.size(); i++){
						Logs logslist = arraylogs.get(i);
						String selectQuery = "SELECT  * FROM "+TABLE_LOGS+" where "+logid+"="+ logslist.getLogId();  
						
						try{
							
							ContentValues values = new ContentValues();
							values.put(CheckPoint_Id, logslist.getCheckPointId()); 
							values.put(logid, logslist.getLogId());
							values.put(passedbyofficerid, logslist.getPassedByOfficerId());
							values.put(passedbyofficername, logslist.getPassedByOfficerName());
							values.put(passedbyofficercontactdetail, logslist.getPassedByOfficerContactInfo());
							values.put(description, logslist.getDescription());
							values.put(notes, logslist.getNotes());
							values.put(observation, logslist.getObservation());
							values.put(shiftid, logslist.getShiftId());
							values.put(lastupdated, logslist.getLast_updated());
						
							
							cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
							if (cursor.moveToFirst()) {
								if (!logslist.getIsActive().equalsIgnoreCase("true")) {
									int a =	db.delete(TABLE_LOGS, logid + " = ?",
													new String[] { String.valueOf(logslist
															.getLogId()) });
										
								} else {

								int a=db.update(TABLE_LOGS, values, logid + " = ?",
										new String[] { String.valueOf(logslist.getLogId()) });
								}
							}else if(logslist.getIsActive().equalsIgnoreCase("true")){
								db.insert(TABLE_LOGS, null, values);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					db.close();
			    }
				
		//delete logs
		public void deleteLogs(){
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_LOGS, null, null);
			db.close();
		}
		
		//get logs
				public ArrayList<Logs> getLogs(String trigger) {
				
						ArrayList<Logs> array_logList = new ArrayList<Logs>();
					
						String selectQuery;
						if(trigger.equals("all"))
							selectQuery = "SELECT  * FROM "+TABLE_LOGS;              
						else
							selectQuery = "SELECT  * FROM "+TABLE_LOGS+" where "+isChecked+" = "+"'true'";
						
						SQLiteDatabase db = this.getReadableDatabase();
						
						try{
							cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
							if (cursor.moveToFirst()) {
								do {
									
											
									Logs log = new Logs();
									log.setCheckPointId(cursor.getString(cursor.getColumnIndex(CheckPoint_Id)));
									log.setLogId(cursor.getString(cursor.getColumnIndex(logid)));
									log.setPassedByOfficerId(cursor.getString(cursor.getColumnIndex(passedbyofficerid)));
									log.setPassedByOfficerName(cursor.getString(cursor.getColumnIndex(passedbyofficername)));
									log.setPassedByOfficerContactInfo(cursor.getString(cursor.getColumnIndex(passedbyofficercontactdetail)));
									log.setDescription(cursor.getString(cursor.getColumnIndex(description)));
									log.setNotes(cursor.getString(cursor.getColumnIndex(notes)));
									log.setObservation(cursor.getString(cursor.getColumnIndex(observation)));
									log.setShiftId(cursor.getString(cursor.getColumnIndex(shiftid)));
									log.setLast_updated(cursor.getString(cursor.getColumnIndex(lastupdated)));							
								
									
									String checkPointID = log.getCheckPointId();
									log.setCheckpoint(getCheckPoint(checkPointID, db));
									
									
									array_logList.add(log);
								} while (cursor.moveToNext());
							}
							
							cursor.getWindow().clear();
							cursor.close();
							// close inserting data from database
							db.close();
							// return city list
							return array_logList;
							
						}
						catch (Exception e)
						{
							e.printStackTrace();
							if (cursor != null)
							{
								cursor.getWindow().clear();
								cursor.close();
							}
							
							db.close();
							return array_logList;
						}
					}


	public void updateIncidents(ArrayList<Incident> incidents) {
		SQLiteDatabase db = this.getWritableDatabase();
		for(int i = 0; i<incidents.size(); i++){
			Incident incident = incidents.get(i);
			String selectQuery = "SELECT  * FROM "+TABLE_INCIDENT+" where "+Incident_Id+"="+ incident.getId();  
			
			try{
				ContentValues values = new ContentValues();
				values.put(Incident_Id, incident.getId()); 
				values.put(Incident_Desc, incident.getDesc());
				values.put(Incident_isChecked, incident.isChecked().toString());
				
				cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
				if (cursor.moveToFirst()) {
					// updating row
					if (!incident.getIsActive().equalsIgnoreCase("true")) {
						int a =	db.delete(TABLE_INCIDENT, Incident_Id
										+ " = ?",new String[] { String.valueOf(incident
												.getId()) });
					} else {
						int a=db.update(TABLE_INCIDENT, values, Incident_Id + " = ?",
							new String[] { String.valueOf(incident.getId()) });
						
					}
				}else if(incident.getIsActive().equalsIgnoreCase("true")){
					db.insert(TABLE_INCIDENT, null, values);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		db.close();
    }
	
	public void markCheckPointAsChecked(String checkPointId, String reportedTime, SQLiteDatabase db) {
//		SQLiteDatabase db = this.getWritableDatabase();
		
			try{
				ContentValues values = new ContentValues();
				values.put(CheckedTime, reportedTime); 
				values.put(isChecked, "true");
				
			
				int a=db.update(TABLE_SCHEDULE, values, CheckPoint_Id + " = ?",
							new String[] { String.valueOf(checkPointId) });
				
			}catch(Exception e){
				e.printStackTrace();
			}
//		db.close();
    }
	public void markCheckPointsAsUnChecked() {
		SQLiteDatabase db = this.getWritableDatabase();
		
			try{
				ContentValues values = new ContentValues();
				values.put(CheckedTime, ""); 
				values.put(isChecked, "false");
				
			
				int a=db.update(TABLE_SCHEDULE, values, "", null);
				
			}catch(Exception e){
				e.printStackTrace();
			}
		db.close();
    }
	public void deleteOfficer(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_OFFICER, null, null);
		db.close();
	}
	
	public void deleteEvents(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_EVENT, null, null);
		db.close();
	}
	
	public void saveOfficerDetails(Officer officer){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			ContentValues values = new ContentValues();
			values.put(officerId, officer.getOfficerId()); 
			values.put(firstName, officer.getFirstName()); 
			values.put(lastName, officer.getLastName()); 
			values.put(email, officer.getEmail()); 
			values.put(contactInfo, officer.getContactInfo()); 
			values.put(alt_info, officer.getAlt_info()); 
			values.put(per_address, officer.getPer_address()); 
			values.put(cur_address, officer.getCur_address()); 
			values.put(State, officer.getState()); 
			values.put(City, officer.getCity()); 
			values.put(Country, officer.getCountry()); 
			values.put(ZipCode, officer.getZipcode()); 
			values.put(DOJ, officer.getDOJ()); 
			values.put(shiftId, officer.getShiftId()); 
			values.put(isTestInProcess, officer.getIsTestInProcess());
			values.put(checkPointBeingTested, officer.getCheckPointBeingTested());
			db.insert(TABLE_OFFICER, null, values);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		db.close();
	}
	
	public Officer getOfficerDetails(){
		Officer officer = new Officer();
		
		// Select All Query
		String selectQuery = "SELECT  * FROM "+TABLE_OFFICER;              
		SQLiteDatabase db = this.getReadableDatabase();
		
		try{
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					officer.setOfficerId(cursor.getString(cursor.getColumnIndex(officerId)));
					officer.setFirstName(cursor.getString(cursor.getColumnIndex(firstName))); 
					officer.setLastName(cursor.getString(cursor.getColumnIndex(lastName)));
					officer.setEmail(cursor.getString(cursor.getColumnIndex(email)));
					officer.setContactInfo(cursor.getString(cursor.getColumnIndex(contactInfo))); 
					officer.setAlt_info(cursor.getString(cursor.getColumnIndex(alt_info)));
					officer.setPer_address(cursor.getString(cursor.getColumnIndex(per_address)));
					officer.setCur_address(cursor.getString(cursor.getColumnIndex(cur_address)));
					officer.setState(cursor.getString(cursor.getColumnIndex(State)));
					officer.setCity(cursor.getString(cursor.getColumnIndex(City)));
					officer.setCountry(cursor.getString(cursor.getColumnIndex(Country)));
					officer.setZipcode(cursor.getString(cursor.getColumnIndex(ZipCode)));
					officer.setDOJ(cursor.getString(cursor.getColumnIndex(DOJ)));
					officer.setShiftId(cursor.getString(cursor.getColumnIndex(shiftId)));
					officer.setIsTestInProcess(cursor.getString(cursor.getColumnIndex(isTestInProcess)));
					officer.setCheckPointBeingTested(cursor.getString(cursor.getColumnIndex(checkPointBeingTested)));
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return officer;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (cursor != null)
			{
				cursor.getWindow().clear();
				cursor.close();
			}
			
			db.close();
			return officer;
		}
	}
	public ArrayList<Incident> getAllIncidents() {
		ArrayList<Incident> incidentList = new ArrayList<Incident>();
		// Select All Query
		String selectQuery = "SELECT  * FROM "+TABLE_INCIDENT;              
		SQLiteDatabase db = this.getReadableDatabase();
		
		try{
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					Incident incident = new Incident();
					incident.setId(cursor.getString(cursor.getColumnIndex(Incident_Id)));
					incident.setDesc(cursor.getString(cursor.getColumnIndex(Incident_Desc)));
					incident.setIsChecked(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(Incident_isChecked))));
					
					incidentList.add(incident);
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return incidentList;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (cursor != null)
			{
				cursor.getWindow().clear();
				cursor.close();
			}
			
			db.close();
			return incidentList;
		}
	}
	//edit amrik
	public ArrayList<CheckPoint> getSchedule(String trigger) {
	//public CheckPoint getSchedule(String trigger) {
		ArrayList<CheckPoint> schedule = new ArrayList<CheckPoint>();
		//CheckPoint schedule = new CheckPoint();
		// Select All Query
		String selectQuery;
		if(trigger.equals("all"))
			selectQuery = "SELECT  * FROM "+TABLE_SCHEDULE;              
		else
			selectQuery = "SELECT  * FROM "+TABLE_SCHEDULE+" where "+isChecked+" = "+"'true'";
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		try{
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					
					CheckPoint checkPoint = new CheckPoint();
					checkPoint.setCheckPointId(cursor.getString(cursor.getColumnIndex(CheckPoint_Id)));
					checkPoint.setPrefferedTime(cursor.getString(cursor.getColumnIndex(PrefferedTime)));
					checkPoint.setPriority(cursor.getString(cursor.getColumnIndex(Priority)));
					checkPoint.setName(cursor.getString(cursor.getColumnIndex(CheckPointName)));
					checkPoint.setAddress(cursor.getString(cursor.getColumnIndex(Address)));
					checkPoint.setCity(cursor.getString(cursor.getColumnIndex(City)));
					checkPoint.setState(cursor.getString(cursor.getColumnIndex(State)));
					checkPoint.setCountry(cursor.getString(cursor.getColumnIndex(Country)));
					checkPoint.setZipcode(cursor.getString(cursor.getColumnIndex(ZipCode)));
					checkPoint.setContactInfo(cursor.getString(cursor.getColumnIndex(ContactInfo)));
					checkPoint.setAltContact(cursor.getString(cursor.getColumnIndex(AlternateContact)));
					checkPoint.setLatitude(cursor.getString(cursor.getColumnIndex(Latitude)));
					checkPoint.setLongitude(cursor.getString(cursor.getColumnIndex(Longitude)));
					checkPoint.setNotes(cursor.getString(cursor.getColumnIndex(Notes)));
					checkPoint.setOpenTimings(cursor.getString(cursor.getColumnIndex(OpenTimings)));
					checkPoint.setCloseTimings(cursor.getString(cursor.getColumnIndex(CloseTimings)));
					checkPoint.setIsChecked(cursor.getString(cursor.getColumnIndex(isChecked)));
					checkPoint.setCheckedTime(cursor.getString(cursor.getColumnIndex(CheckedTime)));
					
					schedule.add(checkPoint);
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return schedule;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (cursor != null)
			{
				cursor.getWindow().clear();
				cursor.close();
			}
			
			db.close();
			return schedule;
		}
	}
	
	public CheckPoint getCheckPoint(String id, SQLiteDatabase db) {
		// Select All Query
		String selectQuery = "SELECT  * FROM "+TABLE_SCHEDULE +" where "+CheckPoint_Id +"="+id;              
		SQLiteCursor cpCursor = null;
		CheckPoint checkPoint = null;
		
		if(db == null)
			db = this.getReadableDatabase();
		
		
		try{
			cpCursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			if (cpCursor.moveToFirst()) {
				do {
					
					checkPoint = new CheckPoint();
					checkPoint.setCheckPointId(cpCursor.getString(cpCursor.getColumnIndex(CheckPoint_Id)));
					checkPoint.setPrefferedTime(cpCursor.getString(cpCursor.getColumnIndex(PrefferedTime)));
					checkPoint.setPriority(cpCursor.getString(cpCursor.getColumnIndex(Priority)));
					checkPoint.setName(cpCursor.getString(cpCursor.getColumnIndex(CheckPointName)));
					checkPoint.setAddress(cpCursor.getString(cpCursor.getColumnIndex(Address)));
					checkPoint.setCity(cpCursor.getString(cpCursor.getColumnIndex(City)));
					checkPoint.setState(cpCursor.getString(cpCursor.getColumnIndex(State)));
					checkPoint.setCountry(cpCursor.getString(cpCursor.getColumnIndex(Country)));
					checkPoint.setZipcode(cpCursor.getString(cpCursor.getColumnIndex(ZipCode)));
					checkPoint.setContactInfo(cpCursor.getString(cpCursor.getColumnIndex(ContactInfo)));
					checkPoint.setAltContact(cpCursor.getString(cpCursor.getColumnIndex(AlternateContact)));
					checkPoint.setLatitude(cpCursor.getString(cpCursor.getColumnIndex(Latitude)));
					checkPoint.setLongitude(cpCursor.getString(cpCursor.getColumnIndex(Longitude)));
					checkPoint.setNotes(cpCursor.getString(cpCursor.getColumnIndex(Notes)));
					checkPoint.setOpenTimings(cpCursor.getString(cpCursor.getColumnIndex(OpenTimings)));
					checkPoint.setCloseTimings(cpCursor.getString(cpCursor.getColumnIndex(CloseTimings)));
				
				} while (cpCursor.moveToNext());
			}
			
			cpCursor.getWindow().clear();
			cpCursor.close();
			// close inserting data from database
			
			// return city list
			return checkPoint;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (cpCursor != null)
			{
				cpCursor.getWindow().clear();
				cpCursor.close();
			}
			
			return null;
		}
	}
	
	public void saveEvents(ArrayList<Event> events, String patrolId){
		SQLiteDatabase db = this.getWritableDatabase();
		for(int i = 0; i<events.size(); i++){
			Event event = events.get(i);
			
			String selectQuery = "SELECT  * FROM "+TABLE_EVENT+" where "+eventId+"="+ event.getId();  
			try{
				ContentValues values = new ContentValues();
				values.put(patrolID, patrolId); 
				values.put(eventId, event.getId()); 
				values.put(eventName, event.getName()); 
				values.put(Latitude, event.getLatitude());
				values.put(Longitude, event.getLongitude());
				values.put(CheckPoint_Id, event.getCheckPoint().getCheckPointId());
				values.put(reportedTime, event.getReportedTime());
				values.put(Incident_Desc, event.getIncidentDesc());
				values.put(isSentViaEmail, event.getIsSentViaEmail());
				values.put(text, event.getText());
				values.put(imageUrl, event.getImageUrl());
				values.put(soundUrl,  event.getSoundUrl());
				values.put(testDesc, event.getTestDesc());
				values.put(notes, event.getNotes());
				
				cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
				if (cursor.moveToFirst()) {
					if (!event.getIsActive().equalsIgnoreCase("true")) {
						int a = db.delete(TABLE_EVENT, eventId + " = ?",
										new String[] { String.valueOf(event
												.getId()) });
					} else {
						// updating row
						int a=db.update(TABLE_EVENT, values, eventId + " = ?",
							new String[] { String.valueOf(event.getId()) });
					}
				}else if(event.getIsActive().equalsIgnoreCase("true")){
					db.insert(TABLE_EVENT, null, values);
					
					if(event.getName().equalsIgnoreCase("scan"))
						markCheckPointAsChecked(event.getCheckPoint().getCheckPointId(), event.getReportedTime(), db);
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		db.close();
	}
	
	public ArrayList<Event> getEvents(String id) {
		// Select All Query
		String selectQuery = "SELECT  * FROM "+TABLE_EVENT +" where "+patrolID +"="+id+" ORDER BY "+reportedTime +" DESC";              
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<Event> eventList = new ArrayList<Event>();
		
		try{
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					Event event = new Event();
					event.setName(cursor.getString(cursor.getColumnIndex(eventName)));
					event.setLatitude(cursor.getString(cursor.getColumnIndex(Latitude)));
					event.setLongitude(cursor.getString(cursor.getColumnIndex(Longitude)));
					
					String checkPointID = cursor.getString(cursor.getColumnIndex(CheckPoint_Id));
					event.setCheckPoint(getCheckPoint(checkPointID, db));
					
					event.setReportedTime(cursor.getString(cursor.getColumnIndex(reportedTime)));
					event.setIncidentDesc(cursor.getString(cursor.getColumnIndex(Incident_Desc)));
					event.setIsSentViaEmail(cursor.getString(cursor.getColumnIndex(isSentViaEmail)));
					event.setText(cursor.getString(cursor.getColumnIndex(text)));
					event.setImageUrl(cursor.getString(cursor.getColumnIndex(imageUrl)));
					event.setSoundUrl(cursor.getString(cursor.getColumnIndex(soundUrl)));
					event.setTestDesc(cursor.getString(cursor.getColumnIndex(testDesc)));
					event.setNotes(cursor.getString(cursor.getColumnIndex(notes)));
					eventList.add(event);
					
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return eventList;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (cursor != null)
			{
				cursor.getWindow().clear();
				cursor.close();
			}
			
			db.close();
			return eventList;
		}
	}
	
	/*
	 public void createDataBase() throws IOException{
		 
	    	boolean dbExist = checkDataBase();
	 
	    	if(dbExist){
	    		//do nothing - database already exist
	    	}else{
	 
	    		//By calling this method and empty database will be created into the default system path
	               //of your application so we are gonna be able to overwrite that database with our database.
	        	this.getReadableDatabase();
	 
	        	try {
	 
	    			copyDataBase();
	 
	    		} catch (IOException e) {
	    			e.printStackTrace();
	        		throw new Error("Error copying database");
	 
	        	}
	    	}
	 
	    }
	 
	    *//**
	     * Check if the database already exist to avoid re-copying the file each time you open the application.
	     * @return true if it exists, false if it doesn't
	     *//*
	    private boolean checkDataBase(){
	 
	    	SQLiteDatabase checkDB = null;
	 
	    	try{
	    		String myPath = DB_PATH + DB_NAME;
	    		File f = new File(myPath);
				if (f.exists())
					return true;
				else
					return false;
	    		
	 
	    	}catch(SQLiteException e){
	 
	    		e.printStackTrace();
	    		return false;
	 
	    	}
	 
	    	
	 
//	    	return checkDB != null ? true : false;
	    }
	 
	    *//**
	     * Copies your database from your local assets-folder to the just created empty database in the
	     * system folder, from where it can be accessed and handled.
	     * This is done by transfering bytestream.
	     * *//*
	    private void copyDataBase() throws IOException{
	 
	    	//Open your local db as the input stream
	    	InputStream myInput = myContext.getAssets().open(DB_NAME);
	 
	    	// Path to the just created empty db
	    	String outFileName = DB_PATH + DB_NAME;
	 
	    	//Open the empty db as the output stream
	    	OutputStream myOutput = new FileOutputStream(outFileName);
	 
	    	//transfer bytes from the inputfile to the outputfile
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = myInput.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
	 
	    	//Close the streams
	    	myOutput.flush();
	    	myOutput.close();
	    	myInput.close();
	 
	    }
	 */
	    public void openDataBase() throws SQLException{
	 
	    	//Open the database
	        String myPath = DB_PATH + DB_NAME;
	    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
	 
	    }
	 
	    @Override
		public synchronized void close() 
	    {
	 
	    	    if(myDataBase != null)
	    		    myDataBase.close();
	 
	    	    super.close();
	 
		}
	 
}