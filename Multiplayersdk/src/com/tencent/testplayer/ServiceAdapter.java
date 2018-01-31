package com.tencent.testplayer;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tencent.multiplayersdk.Game;

public class ServiceAdapter extends BaseAdapter {
	private List<Game> mGames;
	private Context mContext;
	
	public ServiceAdapter(Context context, List<Game> gameList) {
		// TODO Auto-generated constructor stub
		mGames = gameList;
		mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mGames.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mGames.get(arg0);
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
		Game game = mGames.get(arg0);
		textView.setText(game.getServiceIP());
		return textView;
	}

}
