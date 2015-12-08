package com.qrpatrol.modal;

import android.os.Parcel;
import android.os.Parcelable;

public class TestCase  implements Parcelable{
	private String testpoint_id, test_description;
	
	private String isSelected = "false";
	
	public String getIsSelected() {
		return isSelected;
	}
	public void setIsSelected(String isSelected) {
		this.isSelected = isSelected;
	}
	public TestCase(){
		
	}
	 public TestCase(Parcel source) {
		 testpoint_id = source.readString();
		 test_description = source.readString();
		 isSelected  = source.readString();
    }
	 
	 
	public String getTestpoint_id() {
		return testpoint_id;
	}

	public void setTestpoint_id(String testpoint_id) {
		this.testpoint_id = testpoint_id;
	}

	public String getTest_description() {
		return test_description;
	}

	public void setTest_description(String test_description) {
		this.test_description = test_description;
	}
	
	public static final Parcelable.Creator<TestCase> CREATOR
	  = new Parcelable.Creator<TestCase>() 
	{
	       public TestCase createFromParcel(Parcel in) 
	       {
	           return new TestCase(in);
	       }

	       public TestCase[] newArray (int size) 
	       {
	           return new TestCase[size];
	       }
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(testpoint_id);
		dest.writeString(test_description);
		dest.writeString(isSelected);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return test_description;
	}
	
}
