package com.tencent.multiplayersdk;

import com.tencent.multiplayersdk.GameServerManager.OnEventListener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Parcel;
import android.util.Log;

class GameDevice implements OnPushEventListener {
	private static final String TAG = "GameDevice";
	private static GameDevice mGameDevice;
	private OnEventListener mOnEventListener;

	private GameDevice() {

	}

	public static GameDevice getInstance() {
		if (null == mGameDevice) {
			mGameDevice = new GameDevice();
		}

		return mGameDevice;
	}

	public void setOnEventListener(OnEventListener onEventListener) {
		mOnEventListener = onEventListener;
	}
	
	private void readSensor(Parcel parcel, Sensor sensor) {
		String  mName = parcel.readString();
	    String  mVendor = parcel.readString();
	    int     mVersion=parcel.readInt();
	    int     mHandle=parcel.readInt();
	    int     mType=parcel.readInt();
	    float   mMaxRange=parcel.readFloat();
	    float   mResolution=parcel.readFloat();
	    float   mPower=parcel.readFloat();
	    int     mMinDelay=parcel.readInt();
	    int     mFifoReservedEventCount=parcel.readInt();
	    int     mFifoMaxEventCount=parcel.readInt();
	    
	    //java
	}
	
	public boolean onEventReceived(Player player, byte[] b) {
		boolean getFuncFlag = false;
		if (null == mOnEventListener) {
			Log.e(TAG, "onEventReceived mOnEventListener==NULL!!! ");
		}

		Parcel parcel = Parcel.obtain();
		parcel.unmarshall(b, 0, b.length);
		parcel.setDataPosition(0);
		
		int funcType = parcel.readInt();

		switch (funcType) {
		case OnEventListener.FUNC_ONSENSORCHANGED:
//			getFuncFlag = true;
//			mOnEventListener.onSensorChanged(player, event);
			break;
		case OnEventListener.FUNC_ONACCURACYCHANGED:
//			getFuncFlag = true;
//			mOnEventListener.onAccuracyChanged(player, sensor, accuracy);
			break;
		case OnEventListener.FUNC_ONFLUSHCOMPLETED:
//			getFuncFlag = true;
//			mOnEventListener.onFlushCompleted(player, sensor);
			break;
		case OnEventListener.FUNC_ONTOUCHEVENT:
//			getFuncFlag = true;
//			mOnEventListener.onTouchEvent(player, event);
			break;
		case OnEventListener.FUNC_ONKEYDOWN:
//			getFuncFlag = true;
//			mOnEventListener.onKeyDown(player, keyCode, event);
			break;
		case OnEventListener.FUNC_ONKEYLONGPRESS:
//			getFuncFlag = true;
//			mOnEventListener.onKeyLongPress(player, keyCode, event);
			break;
		case OnEventListener.FUNC_ONKEYUP:
//			getFuncFlag = true;
//			mOnEventListener.onKeyUp(player, keyCode, event);
			break;
		case OnEventListener.FUNC_ONKEYMULTIPLE:
//			getFuncFlag = true;
//			mOnEventListener.onKeyMultiple(player, keyCode, count, event);
			break;
		case OnEventListener.FUNC_ONCOMMAND:
			getFuncFlag = true;
			int lenth = parcel.readInt();
			byte[] bytes = new byte[lenth];
			parcel.readByteArray(bytes);
			mOnEventListener.onCommand(player, bytes);
			break;
		default:
			break;
		}

		return getFuncFlag;
	}
}
