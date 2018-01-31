package com.tencent.multiplayersdk;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;

final class Nearby {

	public static final String TAG = "Kevin";
	public static final int SOCKET_BROAD_PORT = 4445;
	public static final int SOCKET_MSG_PORT = 4448;
	public static final String BROADCAST_IP = "239.255.255.250";

	public static final int SOCKET_MSG_ID_SERVER_BROADCAST = 0X1001;
	public static final int SOCKET_MSG_ID_SERVER_ACCPET = 0X1002;
	public static final int SOCKET_MSG_ID_CLINE_CONNECT_REQUEST = 0X1003;
	public static final int SOCKET_MSG_ID_CLINE_CONNECT_SUCESS = 0X1004;
	
	public static final int SOCKET_MSG_ID_CLINE_EVENT = 0X1005;

	public static ServerConnections SConnections = new ServerConnectionsImpl();

	static class ServerConnectionsImpl implements ServerConnections {

		private ServerSocketChannel mSSChannel = null;
		private Selector mSelector = null;

		private InetAddress mBroadcastAddr;
		private MulticastSocket mBroadSocket;
		private DatagramSocket mSocket;
		private DatagramPacket mDatagram;
		private Thread mThread = null;
		private boolean mBroadcatFlag = false;
		private boolean mAcceptFlag = false;
		private boolean mRecvEventFlag = false;
		private EventReceivedListener mEventReceivedListener;
		private OnGameControllerStatusChanged mOnGameControllerStatusChanged;

		private AcceptingConnectionRequestThread mACRThread = null;

		private static final int MSG_ONACCPTINGREQUEST = 0x01;
		private static final int MSG_ONCONNECT = 0x02;
		private static final int MSG_ONEVENT = 0x03;

		private Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_ONACCPTINGREQUEST:
					if (null == mOnGameControllerStatusChanged) {
						Log.e(TAG,
								"mConnectionApplicationReceivedListener == null");
						return;
					}
					mOnGameControllerStatusChanged
							.onGameControllerFound((Player) msg.obj);
					break;

				case MSG_ONCONNECT:
					if (null == mOnGameControllerStatusChanged) {
						Log.e(TAG, "mConnectionRequestReceivedListener == null");
						return;
					}

					mOnGameControllerStatusChanged
							.onGameControllerConnected((Player) msg.obj);
					break;
					
				case MSG_ONEVENT:
					if (null == mEventReceivedListener) {
						Log.e(TAG, "mEventReceivedListener == null");
						return;
					}
					
					EventObj eventObj = (EventObj)msg.obj;
					
					mEventReceivedListener
							.onEventReceived((Player)eventObj.obj, eventObj.bytes);
					break;
				default:
					break;
				}
			}
		};

		/**
		 * Start ServerSocketChannel to listen incoming connection request
		 * 
		 * @author kevinweiliu
		 * 
		 */
		class AcceptingConnectionRequestThread extends Thread {

			@Override
			public void run() {
				mAcceptFlag = true;
				byte[] buffer = new byte[1024];
				DatagramPacket acceptionDatagramPacket = new DatagramPacket(
						buffer, buffer.length);
				while (mAcceptFlag) {
					try {
						Log.d(TAG, "receive ACCEPT");
						mSocket.receive(acceptionDatagramPacket);
						Log.d(TAG, "receive ACCEPT END "
								+ acceptionDatagramPacket.getAddress());

						byte[] b = acceptionDatagramPacket.getData();
						Parcel parcel = Parcel.obtain();
						parcel.unmarshall(b, 0, b.length);
						parcel.setDataPosition(0);

						int socket_msg = parcel.readInt();
						Log.d(TAG, "receive ACCEPT " + socket_msg);

						if (socket_msg == SOCKET_MSG_ID_CLINE_CONNECT_REQUEST) {
							Player player = new Player(parcel);
							player.setPlayerIP(acceptionDatagramPacket
									.getAddress().getHostAddress());
							player.setDeviceID(acceptionDatagramPacket
									.getAddress().getHostAddress());

							Message message = Message.obtain();
							message.what = MSG_ONACCPTINGREQUEST;
							message.obj = player;
							mHandler.sendMessage(message);
						} else if (socket_msg == SOCKET_MSG_ID_CLINE_CONNECT_SUCESS) {
							Player player = new Player(parcel);
							player.setPlayerIP(acceptionDatagramPacket
									.getAddress().getHostAddress());
							player.setDeviceID(acceptionDatagramPacket
									.getAddress().getHostAddress());

							Message message = Message.obtain();
							message.what = MSG_ONCONNECT;
							message.obj = player;
							mHandler.sendMessage(message);
						} else if (socket_msg == SOCKET_MSG_ID_CLINE_EVENT) {
							Player player = new Player(parcel);
							int lenth = parcel.readInt();
							byte[] event = new byte[lenth];
							parcel.readByteArray(event);
							player.setPlayerIP(acceptionDatagramPacket
									.getAddress().getHostAddress());
							player.setDeviceID(acceptionDatagramPacket
									.getAddress().getHostAddress());
							
							Message message = Message.obtain();
							message.what = MSG_ONEVENT;
							message.obj = new EventObj(player, event);
							mHandler.sendMessage(message);
						} else {
							parcel.recycle();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		class BroadCastThread extends Thread {
			public void run() {
				mBroadcatFlag = true;

				while (mBroadcatFlag) {
					try {
						mBroadSocket.send(mDatagram);
						Thread.sleep(1000);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		class RecvEventThread extends Thread {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mRecvEventFlag = true;
				byte[] buffer = new byte[1024];
				DatagramPacket datagramPacket = new DatagramPacket(buffer,
						buffer.length);
				while (mRecvEventFlag) {
					try {
						mSocket.receive(datagramPacket);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				super.run();
			}
		}

		class ConnectionInvitationThread extends Thread {
			private Player player;
			private Game game;

			public ConnectionInvitationThread(Player player, Game game,
					long millisecond) {
				this.player = player;
				this.game = game;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Parcel parcel = Parcel.obtain();
				parcel.writeInt(SOCKET_MSG_ID_SERVER_ACCPET);
				game.writeToParcel(parcel, 0);
				byte[] b = parcel.marshall();
				parcel.recycle();
				InetAddress inetAddress;
				try {
					inetAddress = InetAddress.getByName(player.getPlayerIP());

					DatagramPacket datagramPacket = new DatagramPacket(b,
							b.length, inetAddress, SOCKET_MSG_PORT);
					mSocket.send(datagramPacket);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				super.run();
			}
		}

		public void init() {
			try {
				mBroadSocket = new MulticastSocket(SOCKET_BROAD_PORT);
				mBroadcastAddr = InetAddress.getByName(BROADCAST_IP);
				mBroadSocket.setBroadcast(true);
				mBroadSocket.setTimeToLive(1);

				mSocket = new DatagramSocket(SOCKET_MSG_PORT);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void startBroadcasting(Game game) {
			// TODO Auto-generated method stub
			Parcel parcel = Parcel.obtain();
			parcel.writeInt(SOCKET_MSG_ID_SERVER_BROADCAST);
			game.writeToParcel(parcel, 0);
			byte[] b = parcel.marshall();
			parcel.recycle();
			mDatagram = new DatagramPacket(b, b.length, mBroadcastAddr,
					SOCKET_BROAD_PORT);

			if (mThread != null) {
				mBroadcatFlag = false;
			}

			mThread = new BroadCastThread();
			mThread.start();
		}

		@Override
		public void stopBroadcasting() {
			// TODO Auto-generated method stub
			mBroadcatFlag = false;
		}

		/**
		 * Start accepting connection request from game controller
		 */
		@Override
		public void startAcceptingConnectionRequest(int minimum, int maximum) {
			// TODO Auto-generated method stub

			mACRThread = new AcceptingConnectionRequestThread();
			mACRThread.start();

			// try {
			// mSSChannel = ServerSocketChannel.open();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// try {
			// mSelector = Selector.open();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// try {
			// mSSChannel.register(mSelector, SelectionKey.OP_ACCEPT);
			// } catch (ClosedChannelException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// while(true) {
			// try {
			// mSelector.select();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// Set<SelectionKey> keys = mSelector.selectedKeys();
			// Iterator<SelectionKey> iterator = keys.iterator();
			//
			// while(iterator.hasNext()) {
			// SelectionKey key = iterator.next();
			// iterator.remove();
			//
			// if(key.isAcceptable()) {
			// ServerSocketChannel server = (ServerSocketChannel)key.channel();
			// SocketChannel client = null;
			//
			// Log.d(TAG, "server is: " + server);
			// Log.d(TAG, "mSSChannel is: " + mSSChannel);
			//
			// try {
			// client = server.accept();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// try {
			// client.configureBlocking(false);
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// Log.d(TAG, "Accept connection from " + client);
			// }
			// }
			// }

		}

		@Override
		public void stopAcceptingConnectionRequest() {
			// TODO Auto-generated method stub
			mAcceptFlag = false;
			if (mACRThread != null) {
				mACRThread.interrupt();
			}

		}

		@Override
		public void sendConnectionInvitation(Player player, Game game,
				long millisecond) {
			// TODO Auto-generated method stub
			ConnectionInvitationThread connectionInvitationThread = new ConnectionInvitationThread(
					player, game, millisecond);
			connectionInvitationThread.start();
		}

		@Override
		public void closeGameConsoleChannel() {
			// TODO Auto-generated method stub

		}

		@Override
		public void sendReliableMessage(String deviceId, String endpointId,
				byte[] payload, int length) {
			// TODO Auto-generated method stub

		}

		@Override
		public void sendUnreliableMessage(String deviceId, String endpointId,
				byte[] payload, int length) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setEventReceivedListener(EventReceivedListener listener) {
			// TODO Auto-generated method stub
			mEventReceivedListener = listener;
		}

		@Override
		public void startHandlingMessage() {
			// TODO Auto-generated method stub
			Thread thread = new RecvEventThread();
			thread.start();
		}

		@Override
		public void pauseHandlingMessage() {
			// TODO Auto-generated method stub

		}

		@Override
		public void resumeHandlingMessage() {
			// TODO Auto-generated method stub

		}

		@Override
		public void stopHandlingMessage() {
			// TODO Auto-generated method stub
			mRecvEventFlag = false;
		}

		@Override
		public void setOnGameControllerStatusChangedListener(
				OnGameControllerStatusChanged listener) {
			// TODO Auto-generated method stub
			mOnGameControllerStatusChanged = listener;
		}

	};

	public static ClientConnections CConnections = new ClientConnectionsImpl();

	static class ClientConnectionsImpl implements ClientConnections {
		private String mLocalAddress = null;
		private String mRemoteAddress = null;
		private Socket mGenericSocket = null;
		private SocketChannel mSocketChannel = null;

		private MulticastSocket mBroadSocket;
		private DatagramSocket mSocket;
		private InetAddress mAddress;

		private OnGameConsoleStatusChanged mOnGameConsoleStatusChangedListener;

		private static final int MSG_ONDISCOVER = 0x1001;
		private static final int MSG_ONCONNECT = 0x1002;

		private boolean mDiscoverFlag = false;
		private Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_ONDISCOVER:
					if (null == mOnGameConsoleStatusChangedListener) {
						Log.e(TAG, "GameConsoleFoundListener == null");
						return;
					}
					mOnGameConsoleStatusChangedListener
							.onGameConsoleFound((Game) msg.obj);
					break;
				case MSG_ONCONNECT:
					if (null == mOnGameConsoleStatusChangedListener) {
						Log.e(TAG,
								"mConnectionInvitationReceivedListener == null");
						return;
					}
					mOnGameConsoleStatusChangedListener.onGameConsoleConnected((Game) msg.obj);
					break;
				default:
					break;
				}
			}
		};

		class SendConnectionApplicationThread extends Thread {
			private Player player;
			private Game game;

			public SendConnectionApplicationThread(Game game, Player player) {
				this.player = player;
				this.game = game;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Parcel parcel = Parcel.obtain();
				parcel.writeInt(SOCKET_MSG_ID_CLINE_CONNECT_REQUEST);
				player.writeToParcel(parcel, 0);
				byte[] b = parcel.marshall();
				parcel.recycle();
				InetAddress inetAddress;
				try {
					Log.d(TAG, "sendConnectionRequest");
					inetAddress = InetAddress.getByName(game.getServiceIP());
					DatagramPacket datagramPacket = new DatagramPacket(b,
							b.length, inetAddress, SOCKET_MSG_PORT);
					mSocket.send(datagramPacket);

					while (true) {
						byte[] buffer = new byte[256];
						DatagramPacket connectDatagramPacket = new DatagramPacket(
								buffer, buffer.length);
						mSocket.receive(connectDatagramPacket);
						Log.d(TAG, "sendConnectionRequest end"
								+ connectDatagramPacket.getAddress()
										.getHostAddress());
						Log.d(TAG, "sendConnectionRequest game.getServiceIP()"
								+ game.getServiceIP());

						if (connectDatagramPacket.getAddress().getHostAddress()
								.equals(game.getServiceIP())) {
							byte[] connectBuffer = connectDatagramPacket
									.getData();
							Parcel connectParcel = Parcel.obtain();
							connectParcel.unmarshall(connectBuffer, 0,
									connectBuffer.length);
							connectParcel.setDataPosition(0);

							if (connectParcel.readInt() == SOCKET_MSG_ID_SERVER_ACCPET) {
								Game game = new Game(connectParcel);
								game.setServiceIP(connectDatagramPacket
										.getAddress().getHostAddress());
								game.setDeviceID(connectDatagramPacket
										.getAddress().getHostAddress());

								Message message = Message.obtain();
								message.what = MSG_ONCONNECT;
								message.obj = game;
								mHandler.sendMessage(message);
								break;
							}
						} else {
							continue;
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.run();
			}
		}

		class DiscoverThread extends Thread {
			private String serviceId;

			public DiscoverThread(String serviceId) {
				this.serviceId = serviceId;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mDiscoverFlag = true;
				while (mDiscoverFlag) {
					try {
						byte[] buffer = new byte[1024];
						DatagramPacket datagram = new DatagramPacket(buffer,
								buffer.length);
						mBroadSocket.receive(datagram);
						byte[] b = datagram.getData();
						Parcel parcel = Parcel.obtain();
						parcel.unmarshall(b, 0, b.length);
						parcel.setDataPosition(0);

						if (parcel.readInt() == SOCKET_MSG_ID_SERVER_BROADCAST) {
							Game game = new Game(parcel);

							if (serviceId != null
									&& serviceId.equals(game.getServiceID())) {
								game.setServiceIP(datagram.getAddress()
										.getHostAddress());
								game.setDeviceID(datagram.getAddress()
										.getHostAddress());

								Message message = Message.obtain();
								message.what = MSG_ONDISCOVER;
								message.obj = game;
								mHandler.sendMessage(message);
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				super.run();
			}
		}

		class SendSucessThread extends Thread {
			private Player player;
			private Game game;

			public SendSucessThread(Game game, Player player) {
				this.player = player;
				this.game = game;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Parcel parcel = Parcel.obtain();
				parcel.writeInt(SOCKET_MSG_ID_CLINE_CONNECT_SUCESS);
				player.writeToParcel(parcel, 0);
				byte[] b = parcel.marshall();
				parcel.recycle();
				InetAddress inetAddress;
				try {
					inetAddress = InetAddress.getByName(game.getServiceIP());

					DatagramPacket datagramPacket = new DatagramPacket(b,
							b.length, inetAddress, SOCKET_MSG_PORT);
					mSocket.send(datagramPacket);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.run();
			}
		}
		
		class SendEventThread extends Thread {
			private Player player;
			private Game game;
			private byte[] bytes;

			public SendEventThread(Game game, Player player, byte[] payload) {
				this.player = player;
				this.game = game;
				this.bytes = payload;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Parcel parcel = Parcel.obtain();
				parcel.writeInt(SOCKET_MSG_ID_CLINE_EVENT);
				player.writeToParcel(parcel, 0);
				parcel.writeInt(bytes.length);
				parcel.writeByteArray(bytes);
				byte[] b = parcel.marshall();
				parcel.recycle();
				InetAddress inetAddress;
				try {
					inetAddress = InetAddress.getByName(game.getServiceIP());

					DatagramPacket datagramPacket = new DatagramPacket(b,
							b.length, inetAddress, SOCKET_MSG_PORT);
					mSocket.send(datagramPacket);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.run();
			}
		}

		public void init() {
			try {
				mAddress = InetAddress.getByName(BROADCAST_IP);
				mBroadSocket = new MulticastSocket(SOCKET_BROAD_PORT);
				mBroadSocket.joinGroup(mAddress);

				mSocket = new DatagramSocket(SOCKET_MSG_PORT);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void startDiscovering(String serviceId, String deviceId) {
			// TODO Auto-generated method stub
			Thread thread = new DiscoverThread(serviceId);
			thread.start();
		}

		@Override
		public void stopDiscovering() {
			// TODO Auto-generated method stub
			mDiscoverFlag = false;
		}

		@Override
		public void sendConnectionRequest(Game game, Player player,
				long millisecond) {
			// TODO Auto-generated method stub
			Thread thread = new SendConnectionApplicationThread(game, player);
			thread.start();
		}

		// @Override
		public void createGenericMessageChannel() {
			// TODO Auto-generated method stub
			mGenericSocket = new Socket();
			mGenericSocket.getLocalPort();
		}

		// @Override
		public void createEventChannel() {
			// TODO Auto-generated method stub

		}

		@Override
		public void connectSucess(Game game, Player player, long millisecond) {
			// TODO Auto-generated method stub
			Thread thread = new SendSucessThread(game, player);
			thread.start();
		}

		@Override
		public void closeGameControllerChannel() {
			// TODO Auto-generated method stub

		}

		@Override
		public void sendReliableMessage(Game game, Player player, byte[] payload) {
			// TODO Auto-generated method stub

		}

		@Override
		public void sendUnreliableMessage(Game game, Player player, byte[] payload) {
			// TODO Auto-generated method stub

		}

		@Override
		public void sendReliableEvent(Game game, Player player, byte[] payload) {
			// TODO Auto-generated method stub
			SendEventThread thread = new SendEventThread(game, player, payload);
			thread.start();
		}

		@Override
		public void setOnGameConsoleStatusChanged(
				OnGameConsoleStatusChanged listener) {
			// TODO Auto-generated method stub
			mOnGameConsoleStatusChangedListener = listener;
		}

	};
}
