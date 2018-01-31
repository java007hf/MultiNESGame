package com.tencent.testplayer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.tencent.multiplayersdk.Game;
import com.tencent.multiplayersdk.GameClinetManager;
import com.tencent.multiplayersdk.Player;
import com.tencent.multiplayersdk.R;

public class MainClient extends Activity implements
		GameClinetManager.OnGameConsoleStatusChanged {
	private static final String TAG = "MainClient";
	private GameClinetManager mGamePadManager = GameClinetManager.getInstance();
	private Button mStartDiscoverButton;
	private ListView mServiceListView;
	private List<Game> mGameList = new ArrayList<Game>();
	private ServiceAdapter mServiceAdapter;
	private Game mJoinGame;
	private Player mPlayer;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		init();
	}

	private void init() {
		setContentView(R.layout.activity_main);
		mPlayer = new Player();
		mPlayer.setServiceID("service id");
		mGamePadManager.setOnGameConsoleStatusChangedLister(this);

		mStartDiscoverButton = (Button) findViewById(R.id.startdiscovering);
		mStartDiscoverButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mGamePadManager.startGameClinet(mPlayer);
				mGameList.clear();
			}
		});

		mServiceListView = (ListView) findViewById(R.id.servicelist);
		mServiceAdapter = new ServiceAdapter(this, mGameList);
		mServiceListView.setAdapter(mServiceAdapter);
		mServiceListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mGamePadManager.stopScan();

				Log.d(TAG, "onItemClick = " + arg2);
				Game game = mGameList.get(arg2);
				mGamePadManager.sendConnectionRequest(game);

				mJoinGame = game;
			}
		});
	}

	@Override
	public void onGameConsoleFound(Game game) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onGameConsoleFound = " + game);

		for (Game g : mGameList) {
			Log.d(TAG, "g = " + g);
			Log.d(TAG, "game = " + game);

			if (game.equals(g)) {
				return;
			}
		}

		mGameList.add(game);
		mServiceAdapter.notifyDataSetChanged();
	}

	@Override
	public void onGameConsoleLost(Game game) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGameConsoleConnected(Game game) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onConnectionInvitationReceived = " + game);
		Log.d(TAG, "onConnectionInvitationReceived = " + mJoinGame);
		if (game.equals(mJoinGame)) {
			mGamePadManager.connectSucess(game);

			Intent intent = new Intent(this, GamePadActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onGameConsoleDisconnected(Game game) {
		// TODO Auto-generated method stub

	}

}
