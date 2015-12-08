package com.qrpatrol.modal;

import android.os.Parcel;
import android.os.Parcelable;


public class Order implements Parcelable{
	String result, message,lastUpdated;
//	ArrayList<String>list_Orders;

	String order_ID,Description, Instructions, CheckPointId, CheckpointLat, CheckPointLon, isActive;
	private CheckPoint checkpoint;
	
	public Order() {
		// TODO Auto-generated constructor stub
	}
	
	 public Order(Parcel source) {
		order_ID = source.readString();
		Description = source.readString();
		Instructions = source.readString();
		CheckPointId = source.readString();
		CheckpointLat = source.readString();
		CheckPointLon = source.readString();
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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}



	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getOrder_ID() {
		return order_ID;
	}

	public void setOrder_ID(String order_ID) {
		this.order_ID = order_ID;
	}
/*
	public ArrayList<String> getList_Orders() {
		return list_Orders;
	}

	public void setList_Orders(ArrayList<String> list_Orders) {
		this.list_Orders = list_Orders;
	}*/

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getInstructions() {
		return Instructions;
	}

	public void setInstructions(String instructions) {
		Instructions = instructions;
	}

	public String getCheckPointId() {
		return CheckPointId;
	}

	public void setCheckPointId(String checkPointId) {
		CheckPointId = checkPointId;
	}

	public String getCheckpointLat() {
		return CheckpointLat;
	}

	public void setCheckpointLat(String checkpointLat) {
		CheckpointLat = checkpointLat;
	}

	public String getCheckPointLon() {
		return CheckPointLon;
	}

	public void setCheckPointLon(String checkPointLon) {
		CheckPointLon = checkPointLon;
	}

	public static final Parcelable.Creator<Order> CREATOR
	  = new Parcelable.Creator<Order>() 
	{
	       public Order createFromParcel(Parcel in) 
	       {
	           return new Order(in);
	       }

	       public Order[] newArray (int size) 
	       {
	           return new Order[size];
	       }
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(order_ID);
		dest.writeString(Description);
		dest.writeString(Instructions);
		dest.writeString(CheckPointId);
		dest.writeString(CheckpointLat);
		dest.writeString(CheckPointLon);
		dest.writeString(isActive);
		dest.writeParcelable(checkpoint, flags);
	}
}
