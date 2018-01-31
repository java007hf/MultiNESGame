package com.tencent.multiplayersdk;

//import com.tencent.multiplayersdk.gamepad.GamePadNes;
//import com.tencent.multiplayersdk.gamepad.GamePadViewA;

import android.content.Context;

public class GameClinetManager {
	private static final String TAG = "GameClinetManager";
	private static GameClinetManager mGameClinetManager;
	private PlayerDevice mPlayerDevice = PlayerDevice.getInstance();
	private Player mPlayer;
	private Game mGame;
	
	public interface OnGameConsoleStatusChanged extends ClientConnections.OnGameConsoleStatusChanged {
		
	}
	
	private GameClinetManager() {
		Nearby.CConnections.init();
	}
	
	public static GameClinetManager getInstance() {
		if (null == mGameClinetManager) {
			mGameClinetManager = new GameClinetManager(); 
		}
		
		return mGameClinetManager;
	}
	
	public void startGameClinet(Player play) {
		mPlayer = play;
		Nearby.CConnections.startDiscovering(play.getServiceID(), null);
	}
	
	public void stopScan() {
		Nearby.CConnections.stopDiscovering();
	}
	
	public void sendConnectionRequest(Game game) {
		Nearby.CConnections.sendConnectionRequest(game, mPlayer, 300);
	}
	
	public void connectSucess(Game game) {
		mGame = game;
		Nearby.CConnections.connectSucess(game, mPlayer, 300);
		mPlayerDevice.setGame(game);
	}
	
	public void setOnGameConsoleStatusChangedLister(OnGameConsoleStatusChanged l){
		Nearby.CConnections.setOnGameConsoleStatusChanged(l);
	}
	
	public GamePadView getGamePadView(Context context) {
		GamePadView gamePadView = null;
//		int gamepadType = mGame.getGamePadType();
//		
//		switch (gamepadType) {
//		case GameServerManager.GAMEPADTYPE_A:
//			gamePadView = new GamePadViewA(context, null);
//			break;
//		case GameServerManager.GAMEPADTYPE_NES:
//			gamePadView = new GamePadNes(context, null);
//			break;
//		default:
//			break;
//		}
//		
//		if (null != gamePadView) {
//			gamePadView.setOnEventLister(mPlayerDevice);
//		}
		return gamePadView;
	}
}
