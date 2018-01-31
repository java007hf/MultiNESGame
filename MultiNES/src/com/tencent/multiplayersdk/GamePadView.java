package com.tencent.multiplayersdk;

import com.tencent.multiplayersdk.GameServerManager.OnEventListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public abstract class GamePadView extends View {
	public GamePadView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected OnEventListener mOnEventLister;
	protected Player mPlayer;
	
	public void setOnEventLister(OnEventListener onEventLister) {
		mOnEventLister = onEventLister;
	}
	
	public void setPlayer(Player player) {
		mPlayer = player;
	}
	
	abstract public int getKeyBoardType();
}
