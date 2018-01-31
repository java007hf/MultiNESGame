package com.tencent.multiplayersdk;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;


public class GameServerManager implements ServerConnections.EventReceivedListener {
	public static final int GAMEPADTYPE_A = 1;
	public static final int GAMEPADTYPE_NES = 2;
	
	private static final String TAG = "GameServerManager";
	
	private static GameServerManager mGameServerManager;
	private GameDevice mGameDevice = GameDevice.getInstance();
	private Game mGame;
	
	public interface OnEventListener {
		public static final int FUNC_ONSENSORCHANGED = 0x01;
		public static final int FUNC_ONACCURACYCHANGED = 0x02;
		public static final int FUNC_ONFLUSHCOMPLETED = 0x03;
		public static final int FUNC_ONTOUCHEVENT = 0x04;
		public static final int FUNC_ONKEYDOWN = 0x05;
		public static final int FUNC_ONKEYLONGPRESS = 0x06;
		public static final int FUNC_ONKEYUP = 0x07;
		public static final int FUNC_ONKEYMULTIPLE = 0x08;
		public static final int FUNC_ONCOMMAND = 0x09;
		
		/**
	     * Called when sensor values have changed.
	     * <p>See {@link android.hardware.SensorManager SensorManager}
	     * for details on possible sensor types.
	     * <p>See also {@link android.hardware.SensorEvent SensorEvent}.
	     * 
	     * <p><b>NOTE:</b> The application doesn't own the
	     * {@link android.hardware.SensorEvent event}
	     * object passed as a parameter and therefore cannot hold on to it.
	     * The object may be part of an internal pool and may be reused by
	     * the framework.
	     *
	     * @param event the {@link android.hardware.SensorEvent SensorEvent}. 
	     */
		public void onSensorChanged(Player player, SensorEvent event);
		
		/**
	     * Called when the accuracy of a sensor has changed.
	     * <p>See {@link android.hardware.SensorManager SensorManager}
	     * for details.
	     *
	     * @param accuracy The new accuracy of this sensor
	     */
		public void onAccuracyChanged(Player player, Sensor sensor, int accuracy);
		
		/**
	     * Called after flush() is completed. This flush() could have been initiated by this application
	     * or some other application. All the events in the batch at the point when the flush was called
	     * have been delivered to the applications registered for those sensor events.
	     * <p>
	     *
	     * @param sensor The {@link android.hardware.Sensor Sensor} on which flush was called.
	     *
	     * @see android.hardware.SensorManager#flush(SensorEventListener)
	     */
		public void onFlushCompleted(Player player, Sensor sensor);
		
		/**
	     * Implement this method to handle touch screen motion events.
	     * <p>
	     * If this method is used to detect click actions, it is recommended that
	     * the actions be performed by implementing and calling
	     * {@link #performClick()}. This will ensure consistent system behavior,
	     * including:
	     * <ul>
	     * <li>obeying click sound preferences
	     * <li>dispatching OnClickListener calls
	     * <li>handling {@link AccessibilityNodeInfo#ACTION_CLICK ACTION_CLICK} when
	     * accessibility features are enabled
	     * </ul>
	     *
	     * @param event The motion event.
	     * @return True if the event was handled, false otherwise.
	     */
	    public boolean onTouchEvent(Player player, MotionEvent event);
	    
	    /**
	     * Called when a key down event has occurred.  If you return true,
	     * you can first call {@link KeyEvent#startTracking()
	     * KeyEvent.startTracking()} to have the framework track the event
	     * through its {@link #onKeyUp(int, KeyEvent)} and also call your
	     * {@link #onKeyLongPress(int, KeyEvent)} if it occurs.
	     *
	     * @param keyCode The value in event.getKeyCode().
	     * @param event Description of the key event.
	     *
	     * @return If you handled the event, return true.  If you want to allow
	     *         the event to be handled by the next receiver, return false.
	     */
	    public boolean onKeyDown(Player player, int keyCode, KeyEvent event);

	    /**
	     * Called when a long press has occurred.  If you return true,
	     * the final key up will have {@link KeyEvent#FLAG_CANCELED} and
	     * {@link KeyEvent#FLAG_CANCELED_LONG_PRESS} set.  Note that in
	     * order to receive this callback, someone in the event change
	     * <em>must</em> return true from {@link #onKeyDown} <em>and</em>
	     * call {@link KeyEvent#startTracking()} on the event.
	     *
	     * @param keyCode The value in event.getKeyCode().
	     * @param event Description of the key event.
	     *
	     * @return If you handled the event, return true.  If you want to allow
	     *         the event to be handled by the next receiver, return false.
	     */
	    public boolean onKeyLongPress(Player player, int keyCode, KeyEvent event);

	    /**
	     * Called when a key up event has occurred.
	     *
	     * @param keyCode The value in event.getKeyCode().
	     * @param event Description of the key event.
	     *
	     * @return If you handled the event, return true.  If you want to allow
	     *         the event to be handled by the next receiver, return false.
	     */
	    public boolean onKeyUp(Player player, int keyCode, KeyEvent event);

	    /**
	     * Called when multiple down/up pairs of the same key have occurred
	     * in a row.
	     *
	     * @param keyCode The value in event.getKeyCode().
	     * @param count Number of pairs as returned by event.getRepeatCount().
	     * @param event Description of the key event.
	     *
	     * @return If you handled the event, return true.  If you want to allow
	     *         the event to be handled by the next receiver, return false.
	     */
	    public boolean onKeyMultiple(Player player, int keyCode, int count, KeyEvent event);
	    
	    public void onCommand(Player player, byte[] b);
	}

	
	public interface OnGameControllerStatusChanged extends ServerConnections.OnGameControllerStatusChanged {
	}
	
	private GameServerManager() {
		Nearby.SConnections.init();
		Nearby.SConnections.setEventReceivedListener(this);
	}
	
	public void setOnEventListener(OnEventListener l) {
		mGameDevice.setOnEventListener(l);
	}
	
	public void setOnGameControllerStatusChangedLister(OnGameControllerStatusChanged l) {
		Nearby.SConnections.setOnGameControllerStatusChangedListener(l);
	}
	
	public static GameServerManager getInstance() {
		if (null == mGameServerManager) {
			mGameServerManager = new GameServerManager(); 
		}
		
		return mGameServerManager;
	}
	
	public void startGameServer(Game game) {
		mGame = game;
		Nearby.SConnections.startBroadcasting(game);
		Nearby.SConnections.startAcceptingConnectionRequest(1, 10);
	}
	
	public void allowConnect(Player player) {
		Nearby.SConnections.sendConnectionInvitation(player, mGame, 300);
	}

	@Override
	public void onEventReceived(Player player, byte[] b) {
		mGameDevice.onEventReceived(player, b);
	}
}
