package com.tencent.multiplayersdk.gamepad;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.tencent.multiplayersdk.GameClinetManager;
import com.tencent.multiplayersdk.GamePadView;
import com.tencent.multiplayersdk.GameServerManager;

public class GamePadViewA extends GamePadView {
	private static final String TAG = "GamePadViewA";
	
	public GamePadViewA(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public int getKeyBoardType() {
		// TODO Auto-generated method stub
		return GameServerManager.GAMEPADTYPE_A;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawARGB(255, 255, 0, 0);
		super.onDraw(canvas);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if (null == mOnEventLister) {
			Log.e(TAG, "Have not connect to game!");
			return false;
		}
		byte[] b = new byte[1];
		b[0] = 5;
		mOnEventLister.onCommand(mPlayer, b);
		return mOnEventLister.onTouchEvent(mPlayer, arg0);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (null == mOnEventLister) {
			Log.e(TAG, "Have not connect to game!");
			return false;
		}
		return mOnEventLister.onKeyDown(mPlayer, keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (null == mOnEventLister) {
			Log.e(TAG, "Have not connect to game!");
			return false;
		}
		return mOnEventLister.onKeyUp(mPlayer, keyCode, event);
	}
	
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (null == mOnEventLister) {
			Log.e(TAG, "Have not connect to game!");
			return false;
		}
		return mOnEventLister.onKeyLongPress(mPlayer, keyCode, event);
	}
	
	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		// TODO Auto-generated method stub
		if (null == mOnEventLister) {
			Log.e(TAG, "Have not connect to game!");
			return false;
		}
		return mOnEventLister.onKeyMultiple(mPlayer, keyCode, repeatCount, event);
	}
}
