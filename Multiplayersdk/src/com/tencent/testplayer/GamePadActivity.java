package com.tencent.testplayer;

import android.app.Activity;
import android.os.Bundle;

import com.tencent.multiplayersdk.GameClinetManager;
import com.tencent.multiplayersdk.GamePadView;

public class GamePadActivity extends Activity {
	private GameClinetManager mGamePadManager;
	private GamePadView mGamePadView;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		mGamePadManager = GameClinetManager.getInstance();
		mGamePadView = mGamePadManager.getGamePadView(this);
		setContentView(mGamePadView);
	}
}
