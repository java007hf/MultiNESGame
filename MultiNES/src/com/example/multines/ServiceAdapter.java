package com.example.multines;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tencent.multiplayersdk.Game;
import com.tencent.multiplayersdk.Player;

public class ServiceAdapter extends BaseAdapter {
	private List<Player> mPlayers;
	private Context mContext;
	
	public ServiceAdapter(Context context, List<Player> playerList) {
		// TODO Auto-generated constructor stub
		mPlayers = playerList;
		mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mPlayers.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mPlayers.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		TextView textView;
		if (null == arg1) {
			textView = new TextView(mContext);
		} else {
			textView = (TextView)arg1;
		}
		Player player = mPlayers.get(arg0);
		textView.setText(player.getPlayerIP());
		return textView;
	}

}
