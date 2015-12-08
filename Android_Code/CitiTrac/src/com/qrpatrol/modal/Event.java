package com.qrpatrol.modal;

import android.os.Parcel;
import android.os.Parcelable;


public class Event implements Parcelable{
	
	private String id, name, latitude, longitude, reportedTime, incidentDesc, text, imageUrl, soundUrl, testDesc;
	private String isSentViaEmail, officerPic, notes, isActive;
	private CheckPoint checkPoint;
	
	public Event() {
		// TODO Auto-generated constructor stub
	}
	
	 public Event(Parcel source) {
		 id = source.readString();
		 name = source.readString();
		 latitude = source.readString();
		 longitude = source.readString();
		 reportedTime = source.readString();
		 incidentDesc = source.readString();
		 text = source.readString();
		 imageUrl = source.readString();
		 soundUrl = source.readString();
		 isSentViaEmail = source.readString();
		 officerPic = source.readString();
		 notes = source.readString();
		 testDesc = source.readString();
		 isActive = source.readString();
		checkPoint = source.readParcelable(CheckPoint.class.getClassLoader());
    }
	 
	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getTestDesc() {
		return testDesc;
	}

	public void setTestDesc(String testDesc) {
		this.testDesc = testDesc;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getReportedTime() {
		return reportedTime;
	}
	public void setReportedTime(String reportedTime) {
		this.reportedTime = reportedTime;
	}
	public String getIncidentDesc() {
		return incidentDesc;
	}
	public void setIncidentDesc(String incidentDesc) {
		this.incidentDesc = incidentDesc;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getSoundUrl() {
		return soundUrl;
	}
	public void setSoundUrl(String soundUrl) {
		this.soundUrl = soundUrl;
	}
	public String getIsSentViaEmail() {
		return isSentViaEmail;
	}
	public void setIsSentViaEmail(String isSentViaEmail) {
		this.isSentViaEmail = isSentViaEmail;
	}
	public CheckPoint getCheckPoint() {
		return checkPoint;
	}
	public void setCheckPoint(CheckPoint checkPoint) {
		this.checkPoint = checkPoint;
	}
	
	public static final Parcelable.Creator<Event> CREATOR
	  = new Parcelable.Creator<Event>() 
	{
	       public Event createFromParcel(Parcel in) 
	       {
	           return new Event(in);
	       }

	       public Event[] newArray (int size) 
	       {
	           return new Event[size];
	       }
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(latitude);
		dest.writeString(longitude);
		dest.writeString(reportedTime);
		dest.writeString(incidentDesc);
		dest.writeString(text);
		dest.writeString(imageUrl);
		dest.writeString(soundUrl);
		dest.writeString(isSentViaEmail);
		dest.writeString(officerPic);
		dest.writeString(notes);
		dest.writeString(testDesc);
		dest.writeString(isActive);
		dest.writeParcelable(checkPoint, flags);
	}

	public String getOfficerPic() {
		return officerPic;
	}

	public void setOfficerPic(String officerPic) {
		this.officerPic = officerPic;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
