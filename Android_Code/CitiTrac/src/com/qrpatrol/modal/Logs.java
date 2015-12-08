package com.qrpatrol.modal;

import android.os.Parcel;
import android.os.Parcelable;

public class Logs implements Parcelable{
	String LogId, PassedByOfficerName, PassedByOfficerContactInfo,
			PassedByOfficerId, Description, Notes, Observation, ShiftId,
			CheckPointId, last_updated, isActive;

	private CheckPoint checkpoint;

	public Logs() {
		// TODO Auto-generated constructor stub
	}
	
	 public Logs(Parcel source) {
		LogId = source.readString();
		 PassedByOfficerName = source.readString();
		 PassedByOfficerContactInfo = source.readString();
		 PassedByOfficerId = source.readString();
		 Description = source.readString();
		 Notes = source.readString();
		 Observation = source.readString();
		 ShiftId = source.readString();
		 CheckPointId = source.readString();
		 last_updated = source.readString();
		 isActive = source.readString();
		checkpoint = source.readParcelable(CheckPoint.class.getClassLoader());
    }
	 
	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public CheckPoint getCheckpoint() {
		return checkpoint;
	}

	public void setCheckpoint(CheckPoint checkpoint) {
		this.checkpoint = checkpoint;
	}

	public String getLogId() {
		return LogId;
	}

	public String getLast_updated() {
		return last_updated;
	}

	public void setLast_updated(String last_updated) {
		this.last_updated = last_updated;
	}

	public void setLogId(String logId) {
		LogId = logId;
	}

	public String getPassedByOfficerName() {
		return PassedByOfficerName;
	}

	public void setPassedByOfficerName(String passedByOfficerName) {
		PassedByOfficerName = passedByOfficerName;
	}

	public String getPassedByOfficerContactInfo() {
		return PassedByOfficerContactInfo;
	}

	public void setPassedByOfficerContactInfo(String passedByOfficerContactInfo) {
		PassedByOfficerContactInfo = passedByOfficerContactInfo;
	}

	public String getPassedByOfficerId() {
		return PassedByOfficerId;
	}

	public void setPassedByOfficerId(String passedByOfficerId) {
		PassedByOfficerId = passedByOfficerId;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getNotes() {
		return Notes;
	}

	public void setNotes(String notes) {
		Notes = notes;
	}

	public String getObservation() {
		return Observation;
	}

	public void setObservation(String observation) {
		Observation = observation;
	}

	public String getShiftId() {
		return ShiftId;
	}

	public void setShiftId(String shiftId) {
		ShiftId = shiftId;
	}

	public String getCheckPointId() {
		return CheckPointId;
	}

	public void setCheckPointId(String checkPointId) {
		CheckPointId = checkPointId;
	}

	public static final Parcelable.Creator<Logs> CREATOR
	  = new Parcelable.Creator<Logs>() 
	{
	       public Logs createFromParcel(Parcel in) 
	       {
	           return new Logs(in);
	       }

	       public Logs[] newArray (int size) 
	       {
	           return new Logs[size];
	       }
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(LogId);
		dest.writeString(PassedByOfficerName);
		dest.writeString(PassedByOfficerContactInfo);
		dest.writeString(PassedByOfficerId);
		dest.writeString(Description);
		dest.writeString(Notes);
		dest.writeString(Observation);
		dest.writeString(ShiftId);
		dest.writeString(CheckPointId);
		dest.writeString(last_updated);
		dest.writeString(isActive);
		dest.writeParcelable(checkpoint, flags);
	}
}
