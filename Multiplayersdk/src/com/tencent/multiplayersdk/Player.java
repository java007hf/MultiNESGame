package com.tencent.multiplayersdk;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Player implements Parcelable {
	private String mServiceID = "";
	private String mDeviceID;
	private String mEndpointID;
	private String mPlayerIP;
	
	public Player() {
	}
	
	public Player(Parcel in) {
		mServiceID = in.readString();
		mDeviceID = in.readString();
		mEndpointID = in.readString();
		mPlayerIP = in.readString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		// TODO Auto-generated method stub
		out.writeString(mServiceID);
		out.writeString(mDeviceID);
		out.writeString(mEndpointID);
		out.writeString(mPlayerIP);
	}
	
	public void setServiceID(String serviceID) {
		mServiceID = serviceID;
	}

	public String getServiceID() {
		return mServiceID;
	}
	
	public void setDeviceID(String deviceid) {
		mDeviceID = deviceid;
	}

	public String getDeviceID() {
		return mDeviceID;
	}

	public void setEndpointID(String endpointId) {
		mEndpointID = endpointId;
	}

	public String getEndpointID() {
		return mEndpointID;
	}

	public void setPlayerIP(String playerIP) {
		mPlayerIP = playerIP;
	}

	public String getPlayerIP() {
		return mPlayerIP;
	}
	
	public static final Parcelable.Creator<Player> CREATOR = new Creator<Player>() {

		@Override
		public Player createFromParcel(Parcel arg0) {
			// TODO Auto-generated method stub
			return new Player(arg0);
		}

		@Override
		public Player[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return new Player[arg0];
		}
	};
	
	public boolean equals(Player o) {
		if (null == o)
			return true;
		if ((mServiceID != null && mServiceID.equals(o.mServiceID) || (mServiceID == null && mServiceID == o.mServiceID))
				&& (mDeviceID != null && mDeviceID.equals(o.mDeviceID) || (mDeviceID == null && mDeviceID == o.mDeviceID))
				&& (mEndpointID != null && mEndpointID.equals(o.mEndpointID) || (mEndpointID == null && mEndpointID == o.mEndpointID))
				&& (mPlayerIP != null && mPlayerIP.equals(o.mPlayerIP) || (mPlayerIP == null && mPlayerIP == o.mPlayerIP))) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("mDeviceID = " + mDeviceID);
		sBuilder.append(",mServiceID = " + mServiceID);
		sBuilder.append(",mEndpointID = " + mEndpointID);
		sBuilder.append(",mPlayerIP = " + mPlayerIP);
		return sBuilder.toString();
	}
}
