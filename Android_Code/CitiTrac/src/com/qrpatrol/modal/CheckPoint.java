package com.qrpatrol.modal;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class CheckPoint implements Parcelable{
	

	private String checkPointId, isChecked, checkedTime, prefferedTime, priority, name, address, city, state, country, zipcode, contactInfo, altContact, latitude, longitude, notes, openTimings, closeTimings;
	private String isActive;
	
	public CheckPoint(){
		
	}
	 public CheckPoint(Parcel source) {
		checkPointId = source.readString();
		isChecked = source.readString();
		checkedTime = source.readString();
		prefferedTime = source.readString();
		priority = source.readString();
		name = source.readString();
		address = source.readString();
		city = source.readString();
		state = source.readString();
		country = source.readString();
		zipcode = source.readString();
		contactInfo = source.readString();
		altContact = source.readString();
		latitude = source.readString();
		longitude = source.readString();
		notes = source.readString();
		openTimings = source.readString();
		closeTimings = source.readString();
		isActive = source.readString();
    }
	 
	 
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public String getIsChecked() {
		return isChecked;
	}

	public void setIsChecked(String isChecked) {
		this.isChecked = isChecked;
	}

	public String getCheckedTime() {
		return checkedTime;
	}

	public void setCheckedTime(String checkedTime) {
		this.checkedTime = checkedTime;
	}

	public String getCheckPointId() {
		return checkPointId;
	}

	public void setCheckPointId(String checkPointId) {
		this.checkPointId = checkPointId;
	}

	public String getPrefferedTime() {
		return prefferedTime;
	}

	public void setPrefferedTime(String prefferedTime) {
		this.prefferedTime = prefferedTime;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public String getAltContact() {
		return altContact;
	}

	public void setAltContact(String altContact) {
		this.altContact = altContact;
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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getOpenTimings() {
		return openTimings;
	}

	public void setOpenTimings(String openTimings) {
		this.openTimings = openTimings;
	}

	public String getCloseTimings() {
		return closeTimings;
	}

	public void setCloseTimings(String closeTimings) {
		this.closeTimings = closeTimings;
	}

	public static final Parcelable.Creator<CheckPoint> CREATOR
	  = new Parcelable.Creator<CheckPoint>() 
	{
	       public CheckPoint createFromParcel(Parcel in) 
	       {
	           return new CheckPoint(in);
	       }

	       public CheckPoint[] newArray (int size) 
	       {
	           return new CheckPoint[size];
	       }
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(checkPointId);
		dest.writeString(isChecked);
		dest.writeString(checkedTime);
		dest.writeString(prefferedTime);
		dest.writeString(priority);
		dest.writeString(name);
		dest.writeString(address);
		dest.writeString(city);
		dest.writeString(state);
		dest.writeString(country);
		dest.writeString(zipcode);
		dest.writeString(contactInfo);
		dest.writeString(altContact);
		dest.writeString(latitude);
		dest.writeString(longitude);
		dest.writeString(notes);
		dest.writeString(openTimings);
		dest.writeString(closeTimings);
		dest.writeString(isActive);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}
}
