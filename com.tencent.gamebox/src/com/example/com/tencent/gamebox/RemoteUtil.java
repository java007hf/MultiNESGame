package com.example.com.tencent.gamebox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

public class RemoteUtil {
	private static RemoteUtil mRemoteUtil;
	private ConnectLister mConnectLister;
	private MulticastSocket mBroadcastSocket;
	private DatagramPacket mBroadcastDatagramPacket;
	private boolean mSendFlag = true;

	private static final String TAG = "RemoteUtil";

	class BroadcastThread extends Thread {
		@Override
		public void run() {
			while (mSendFlag) {
				try {
					mBroadcastSocket.send(mBroadcastDatagramPacket);
					Thread.sleep(1000);
				} catch (Exception e) {
					stopBroadcast();
					if (null != mConnectLister) {
						mConnectLister.onError(e.toString());
					}
				}
			}
			super.run();
		}
	}

	private RemoteUtil() {

	}

	public static RemoteUtil getInstance() {
		if (null == mRemoteUtil) {
			mRemoteUtil = new RemoteUtil();
		}

		return mRemoteUtil;
	}

	public interface ConnectLister {
		public void onBind(ClinetInfo clinetInfo);

		public void onError(String str);
	}

	public static String getLocalHostIp() throws SocketException {
		String ipaddress = "";
		Enumeration<NetworkInterface> en = NetworkInterface
				.getNetworkInterfaces();
		// 遍历所用的网络接口
		while (en.hasMoreElements()) {
			NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
			Enumeration<InetAddress> inet = nif.getInetAddresses();
			// 遍历每一个接口绑定的所有ip
			while (inet.hasMoreElements()) {
				InetAddress ip = inet.nextElement();
				if (!ip.isLoopbackAddress()
						&& InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
					return ip.getHostAddress();
				}
			}

		}
		return ipaddress;

	}

	public static byte[] getBytes(Serializable obj) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.writeObject(obj);
		out.flush();
		byte[] bytes = bout.toByteArray();
		bout.close();
		out.close();
		return bytes;
	}

	public static Object getObject(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
		ObjectInputStream oi = new ObjectInputStream(bi);
		Object obj = oi.readObject();
		bi.close();
		oi.close();
		return obj;
	}

	public void stopBroadcast() {
		mSendFlag = false;
		mBroadcastSocket.close();
		mBroadcastSocket = null;
	}
	
	public void broadcastHost(GameInfo gameInfo) {
		try {
			mBroadcastSocket = new MulticastSocket(gameInfo.broadcastPort);
			mBroadcastSocket.setBroadcast(true);
			mBroadcastSocket.setTimeToLive(1);

			InetAddress broadcastAddr = InetAddress
					.getByName(gameInfo.broadcastIp);

			byte[] buffer = getBytes(gameInfo);

			mBroadcastDatagramPacket = new DatagramPacket(buffer,
					buffer.length, broadcastAddr, gameInfo.broadcastPort);
			
			Thread sendThread = new BroadcastThread();
			sendThread.start();
		} catch (Exception e) {
			if (mConnectLister != null) {
				mConnectLister.onError(e.toString());
			}
		}
	}

	public void setConnectLister(ConnectLister connectLister) {
		mConnectLister = connectLister;
	}

}
