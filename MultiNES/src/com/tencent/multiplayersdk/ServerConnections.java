package com.tencent.multiplayersdk;

interface ServerConnections {
	public void init();

	/**
	 * [Server] Start broadcasting service
	 */
	public void startBroadcasting(Game game);

	/**
	 * [Server] Stop broadcasting service
	 */
	public void stopBroadcasting();

	/**
	 * [Server] Start accepting connection request from client
	 */
	public void startAcceptingConnectionRequest(int minimum, int maximum);

	/**
	 * [Server] Stop accepting connection request from client
	 */
	public void stopAcceptingConnectionRequest();

	/**
	 * [Server] Start handling incoming message, all kinds of events, game start
	 */
	public void startHandlingMessage();

	/**
	 * [Server] Pause handling incoming message, game pause
	 */
	public void pauseHandlingMessage();

	/**
	 * [Server] Resume handling incoming message, game resume
	 */
	public void resumeHandlingMessage();

	/**
	 * [Server] Stop handling incoming message, forced game stop or game over
	 */
	public void stopHandlingMessage();

	/**
	 * [Server] Send connection invitation to client Server should create
	 * ServerSocketChannel, wait for incoming connections from clients
	 */
	public void sendConnectionInvitation(Player player, Game game,
			long millisecond);

	/**
	 * [Server] How to check exceptional disconnection
	 */
	public void closeGameConsoleChannel();

	/**
	 * [Client, Server] Send message from one device to another using both TCP
	 * and UDP protocols
	 */
	public void sendReliableMessage(String deviceId, String endpointId,
			byte[] payload, int length);

	/**
	 * [Client, Server] Send message from one device to another using only UDP
	 * protocol
	 */
	public void sendUnreliableMessage(String deviceId, String endpointId,
			byte[] payload, int length);

	public void setOnGameControllerStatusChangedListener(
			OnGameControllerStatusChanged listener);

	public void setEventReceivedListener(EventReceivedListener listener);

	public static interface OnGameControllerStatusChanged {

		public void onGameControllerFound(Player player);

		public void onGameControllerLost(Player player);

		public void onGameControllerConnected(Player player);

		public void onGameControllerDisconnected(Player player);
	}

	public static interface EventReceivedListener {

		public void onEventReceived(Player player, byte[] b);
	}
}
