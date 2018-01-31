package com.example.multines;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.androidemu.nes.EmulatorActivity;
import com.tencent.multiplayersdk.Game;
import com.tencent.multiplayersdk.GameServerManager;
import com.tencent.multiplayersdk.Player;

public class MainActivity extends Activity implements
		GameServerManager.OnGameControllerStatusChanged {
	private static final String TAG = "MainActivity game";
	public static List<Player> mPlayers = new ArrayList<Player>();
	private Game mGame;
	private GameServerManager mGamePadManager = GameServerManager.getInstance();

	private Button mStartButton;
	private ListView mServiceListView;
	private ServiceAdapter mServiceAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		startBroadcast();
	}

	private void init() {
		mStartButton = (Button) findViewById(R.id.start);
		mStartButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, EmulatorActivity.class);
				startActivity(intent);
			}
		});

		mServiceListView = (ListView) findViewById(R.id.servicelist);
		mServiceAdapter = new ServiceAdapter(this, mPlayers);
		mServiceListView.setAdapter(mServiceAdapter);
	}

	private void startBroadcast() {
		mGame = new Game();
		mGame.setServiceID("service id");
		mGame.setGamePadType(GameServerManager.GAMEPADTYPE_NES);

		mGamePadManager.setOnGameControllerStatusChangedLister(this);
		mGamePadManager.startGameServer(mGame);

		mPlayers.clear();
	}

	@Override
	public void onGameControllerLost(Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGameControllerFound(Player player) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onGameControllerFound " + player);
		mGamePadManager.allowConnect(player);
	}

	@Override
	public void onGameControllerConnected(Player player) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onGameControllerConnected " + player);
		for (Player p : mPlayers) {
			Log.d(TAG, "p = " + p);
			Log.d(TAG, "player = " + player);

			if (player.equals(p)) {
				return;
			}
		}
		mPlayers.add(player);
		mServiceAdapter.notifyDataSetChanged();
	}

	@Override
	public void onGameControllerDisconnected(Player player) {
		// TODO Auto-generated method stub
		
	}
}
