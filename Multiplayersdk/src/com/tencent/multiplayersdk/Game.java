package com.tencent.multiplayersdk;

import android.os.Parcel;
import android.os.Parcelable;

public class Game implements Parcelable {
	private int mGamePadType = 0;
	private String mServiceID = "";
	private String mDeviceID;
	private String mEndpointID;
	private String mServiceIP;

	public Game() {

	}

	public Game(Parcel in) {
		mGamePadType = in.readInt();
		mServiceID = in.readString();
		mDeviceID = in.readString();
		mEndpointID = in.readString();
		mServiceIP = in.readString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeInt(mGamePadType);
		out.writeString(mServiceID);
		out.writeString(mDeviceID);
		out.writeString(mEndpointID);
		out.writeString(mServiceIP);
	}

	public void setGamePadType(int type) {
		mGamePadType = type;
	}

	public int getGamePadType() {
		return mGamePadType;
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

	public void setServiceIP(String serviceIP) {
		mServiceIP = serviceIP;
	}

	public String getServiceIP() {
		return mServiceIP;
	}

	public static final Parcelable.Creator<Game> CREATOR = new Creator<Game>() {

		@Override
		public Game createFromParcel(Parcel arg0) {
			// TODO Auto-generated method stub
			return new Game(arg0);
		}

		@Override
		public Game[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return new Game[arg0];
		}
	};

	public boolean equals(Game o) {
		if (null == o)
			return true;
		if ((mDeviceID != null && mDeviceID.equals(o.mDeviceID) || (mDeviceID == null && mDeviceID == o.mDeviceID))
				&& (mEndpointID != null && mEndpointID.equals(o.mEndpointID) || (mEndpointID == null && mEndpointID == o.mEndpointID))
				&& mGamePadType == o.mGamePadType
				&& (mServiceID != null && mServiceID.equals(o.mServiceID) || (mServiceID == null && mServiceID == o.mServiceID))
				&& (mServiceIP != null && mServiceIP.equals(o.mServiceIP) || (mServiceIP == null && mServiceIP == o.mServiceIP))) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("mGamePadType = " + mGamePadType);
		sBuilder.append(",mServiceID = " + mServiceID);
		sBuilder.append(",mDeviceID = " + mDeviceID);
		sBuilder.append(",mEndpointID = " + mEndpointID);
		sBuilder.append(",mServiceIP = " + mServiceIP);
		return sBuilder.toString();
	}
}
