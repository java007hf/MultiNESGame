package com.tencent.multiplayersdk;

interface ClientConnections {
	public void init();

	/**
	 * [Client] Start discovering server
	 */
	public void startDiscovering(String serviceId, String deviceId);

	/**
	 * [Client] Stop discovering server
	 */
	public void stopDiscovering();

	/**
	 * [Client] Send connection application to server
	 * 
	 * @millisecond timeout
	 */
	public void sendConnectionRequest(Game game, Player player, long millisecond);

	/**
	 * [Client] Send connection request to server
	 * 
	 * @millisecond timeout
	 */
	public void connectSucess(Game game, Player player, long millisecond);

	/**
	 * Two Channels, event channel and generic message channel
	 */
	// public void createConnections();

	/**
	 * Called in sendConnectionRequest
	 */
	// public void createGenericMessageChannel();

	/**
	 * Called in sendConnectionRequest
	 */
	// public void createEventChannel();

	/**
	 * [Client] How to check exceptional disconnection
	 */
	public void closeGameControllerChannel();

	/**
	 * [Client, Server] Send message from one device to another using both TCP
	 * and UDP protocols
	 */
	public void sendReliableMessage(Game game, Player player, byte[] payload);

	/**
	 * [Client, Server] Send message from one device to another using only UDP
	 * protocol
	 */
	public void sendUnreliableMessage(Game game, Player player, byte[] payload);

	/**
	 * [Client]
	 */
	public void sendReliableEvent(Game game, Player player, byte[] payload);

	public void setOnGameConsoleStatusChanged(
			OnGameConsoleStatusChanged listener);

	public static interface OnGameConsoleStatusChanged {

		public void onGameConsoleFound(Game game);

		public void onGameConsoleLost(Game game);

		public void onGameConsoleConnected(Game game);

		public void onGameConsoleDisconnected(Game game);
	}

}
