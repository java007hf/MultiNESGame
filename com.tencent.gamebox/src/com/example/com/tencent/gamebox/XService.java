package com.example.com.tencent.gamebox;

import java.io.IOException;

import android.content.Context;

public class XService implements RemoteUtil.ConnectLister {
	private static XService mService = null;
	private Context mContext;
	private RemoteUtil mRemoteUtil;

	private XService(Context context) {
		mContext = context;
		mRemoteUtil = RemoteUtil.getInstance();
		mRemoteUtil.setConnectLister(this);
	}

	public static XService getInstance(Context context) {
		if (null == mService) {
			mService = new XService(context);
		}

		return mService;
	}

	public void create(GameInfo gameInfo) {
		mRemoteUtil.broadcastHost(gameInfo);
	}

	@Override
	public void onBind(ClinetInfo clinetInfo) {

	}

	@Override
	public void onError(String str) {

	}
}
