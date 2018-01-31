package com.tencent.multiplayersdk;

import com.tencent.multiplayersdk.GameServerManager.OnEventListener;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Parcel;
import android.view.KeyEvent;
import android.view.MotionEvent;

class PlayerDevice implements OnEventListener {
	private static PlayerDevice mPlayerDevice;
	private Game mGame;

	private PlayerDevice() {

	}

	public static PlayerDevice getInstance() {
		if (null == mPlayerDevice) {
			mPlayerDevice = new PlayerDevice();
		}

		return mPlayerDevice;
	}

	public void setGame(Game game) {
		mGame = game;
	}
	
	@Override
	public void onSensorChanged(Player player, SensorEvent event) {
		// TODO Auto-generated method stub
		Parcel parcel = Parcel.obtain();
		parcel.writeInt(FUNC_ONSENSORCHANGED);
		parcel.writeFloatArray(event.values);
		writeSensor(parcel, event.sensor);
		parcel.writeInt(event.accuracy);
		parcel.writeLong(event.timestamp);
		byte[] b = parcel.marshall();
		parcel.recycle();
		Nearby.CConnections.sendReliableEvent(mGame, player, b);
	}

	@Override
	public void onAccuracyChanged(Player player, Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		Parcel parcel = Parcel.obtain();
		parcel.writeInt(FUNC_ONACCURACYCHANGED);
		writeSensor(parcel, sensor);
		parcel.writeInt(accuracy);
		byte[] b = parcel.marshall();
		parcel.recycle();
		Nearby.CConnections.sendReliableEvent(mGame, player, b);
	}

	@Override
	public void onFlushCompleted(Player player, Sensor sensor) {
		// TODO Auto-generated method stub
		Parcel parcel = Parcel.obtain();
		parcel.writeInt(FUNC_ONFLUSHCOMPLETED);
		writeSensor(parcel, sensor);
		byte[] b = parcel.marshall();
		parcel.recycle();
		Nearby.CConnections.sendReliableEvent(mGame, player, b);
	}

	@SuppressLint("NewApi")
	private void writeSensor(Parcel parcel, Sensor sensor) {
		parcel.writeString(sensor.getName());
		parcel.writeString(sensor.getVendor());
		parcel.writeInt(sensor.getVersion());
		parcel.writeInt(sensor.getHandle());
		parcel.writeInt(sensor.getType());
		parcel.writeFloat(sensor.getMaximumRange());
		parcel.writeFloat(sensor.getResolution());
		parcel.writeFloat(sensor.getPower());
		parcel.writeInt(sensor.getMinDelay());
		parcel.writeInt(sensor.getFifoReservedEventCount());
		parcel.writeInt(sensor.getFifoMaxEventCount());
	}

	@Override
	public boolean onTouchEvent(Player player, MotionEvent event) {
		// TODO Auto-generated method stub
		Parcel parcel = Parcel.obtain();
		parcel.writeInt(FUNC_ONTOUCHEVENT);
		event.writeToParcel(parcel, 0);

		byte[] b = parcel.marshall();
		parcel.recycle();
		Nearby.CConnections.sendReliableEvent(mGame, player, b);
		return true;
	}

	@Override
	public boolean onKeyDown(Player player, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Parcel parcel = Parcel.obtain();
		parcel.writeInt(FUNC_ONKEYDOWN);
		parcel.writeInt(keyCode);
		event.writeToParcel(parcel, 0);
		byte[] b = parcel.marshall();
		parcel.recycle();
		Nearby.CConnections.sendReliableEvent(mGame, player, b);
		return true;
	}

	@Override
	public boolean onKeyLongPress(Player player, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Parcel parcel = Parcel.obtain();
		parcel.writeInt(FUNC_ONKEYLONGPRESS);
		parcel.writeInt(keyCode);
		event.writeToParcel(parcel, 0);
		byte[] b = parcel.marshall();
		parcel.recycle();
		Nearby.CConnections.sendReliableEvent(mGame, player, b);
		return true;
	}

	@Override
	public boolean onKeyUp(Player player, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Parcel parcel = Parcel.obtain();
		parcel.writeInt(FUNC_ONKEYUP);
		parcel.writeInt(keyCode);
		event.writeToParcel(parcel, 0);
		byte[] b = parcel.marshall();
		parcel.recycle();
		Nearby.CConnections.sendReliableEvent(mGame, player, b);
		return true;
	}

	@Override
	public boolean onKeyMultiple(Player player, int keyCode, int count, KeyEvent event) {
		// TODO Auto-generated method stub
		Parcel parcel = Parcel.obtain();
		parcel.writeInt(FUNC_ONKEYMULTIPLE);
		parcel.writeInt(keyCode);
		parcel.writeInt(count);
		event.writeToParcel(parcel, 0);
		byte[] b = parcel.marshall();
		parcel.recycle();
		Nearby.CConnections.sendReliableEvent(mGame, player, b);
		return true;
	}

	@Override
	public void onCommand(Player player, byte[] bts) {
		// TODO Auto-generated method stub
		Parcel parcel = Parcel.obtain();
		parcel.writeInt(FUNC_ONCOMMAND);
		parcel.writeInt(bts.length);
		parcel.writeByteArray(bts);
		byte[] b = parcel.marshall();
		parcel.recycle();
		Nearby.CConnections.sendReliableEvent(mGame, player, b);
	}

}
