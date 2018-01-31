package com.tencent.multiplayersdk.gamepad;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.tencent.multiplayersdk.GamePadView;
import com.tencent.multiplayersdk.GameServerManager;
import com.tencent.multiplayersdk.Player;
import com.tencent.multiplayersdk.GameServerManager.OnEventListener;

public class GamePadNes extends GamePadView {
	private VirtualKeypad vkeypad;
	public GamePadNes(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		vkeypad = new VirtualKeypad(this);
	}

	@Override
	public void setPlayer(Player player) {
		vkeypad.setPlayer(player);
		super.setPlayer(player);
	}
	
	public void setOnEventLister(OnEventListener onEventLister) {
		mOnEventLister = onEventLister;
		vkeypad.setOnEventLister(onEventLister);
	}
	
	@Override
	public int getKeyBoardType() {
		// TODO Auto-generated method stub
		return GameServerManager.GAMEPADTYPE_NES;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return vkeypad.onTouch(arg0, false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		vkeypad.draw(canvas);
		super.onDraw(canvas);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stub
		vkeypad.reset();
		super.onWindowFocusChanged(hasWindowFocus);
	}
	
	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) {
		// TODO Auto-generated method stub
		vkeypad.resize(width, height);
		super.onSizeChanged(width, height, oldw, oldh);
	}
}
