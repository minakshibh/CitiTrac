package com.qrpatrol.modal;

import android.os.Parcel;
import android.os.Parcelable;

public class Officer implements Parcelable{
	private String officerId, firstName, lastName, email, contactInfo, alt_info, per_address, cur_address, state, city, country, zipcode, DOJ, shiftId, isTestInProcess, checkPointBeingTested;
	private String isActive;
	
	public Officer(){
		
	}
	 public Officer(Parcel source) {
		 officerId = source.readString();
		 firstName = source.readString();
		 lastName = source.readString();
		 email = source.readString();
		 contactInfo = source.readString();
		 alt_info = source.readString();
		 per_address = source.readString();
		 cur_address = source.readString();
		 state = source.readString();
		 city = source.readString();
		 country = source.readString();
		 zipcode = source.readString();
		 DOJ = source.readString();
		 shiftId = source.readString();
		 isTestInProcess = source.readString();
		 checkPointBeingTested = source.readString();
		 isActive = source.readString();
    }
	 
	 
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public String getIsTestInProcess() {
		return isTestInProcess;
	}
	public void setIsTestInProcess(String isTestInProcess) {
		this.isTestInProcess = isTestInProcess;
	}
	public String getCheckPointBeingTested() {
		return checkPointBeingTested;
	}
	public void setCheckPointBeingTested(String checkPointBeingTested) {
		this.checkPointBeingTested = checkPointBeingTested;
	}
	public String getOfficerId() {
		return officerId;
	}

	public void setOfficerId(String officerId) {
		this.officerId = officerId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public String getAlt_info() {
		return alt_info;
	}

	public void setAlt_info(String alt_info) {
		this.alt_info = alt_info;
	}

	public String getPer_address() {
		return per_address;
	}

	public void setPer_address(String per_address) {
		this.per_address = per_address;
	}

	public String getCur_address() {
		return cur_address;
	}

	public void setCur_address(String cur_address) {
		this.cur_address = cur_address;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public String getDOJ() {
		return DOJ;
	}

	public void setDOJ(String dOJ) {
		DOJ = dOJ;
	}

	public String getShiftId() {
		return shiftId;
	}

	public void setShiftId(String shiftId) {
		this.shiftId = shiftId;
	}
	
	public static final Parcelable.Creator<Officer> CREATOR
	  = new Parcelable.Creator<Officer>() 
	{
	       public Officer createFromParcel(Parcel in) 
	       {
	           return new Officer(in);
	       }

	       public Officer[] newArray (int size) 
	       {
	           return new Officer[size];
	       }
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {	 
		dest.writeString(officerId);
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeString(email);
		dest.writeString(contactInfo);
		dest.writeString(alt_info);
		dest.writeString(per_address);
		dest.writeString(cur_address);
		dest.writeString(state);
		dest.writeString(city);
		dest.writeString(country);
		dest.writeString(zipcode);
		dest.writeString(DOJ);
		dest.writeString(shiftId);
		dest.writeString(isTestInProcess);
		dest.writeString(checkPointBeingTested);
		dest.writeString(isActive);
	}
}