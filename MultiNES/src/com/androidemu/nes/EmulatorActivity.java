package com.androidemu.nes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.Window;

import com.androidemu.EmuMedia;
import com.androidemu.Emulator;
import com.androidemu.Emulator.FrameUpdateListener;
import com.androidemu.EmulatorView;
import com.example.multines.MainActivity;
import com.tencent.multiplayersdk.GameServerManager;
import com.tencent.multiplayersdk.Player;

public class EmulatorActivity extends Activity implements
		SurfaceHolder.Callback, Emulator.OnFrameDrawnListener,
		GameServerManager.OnEventListener, FrameUpdateListener {

	private static final String LOG_TAG = "Nesoid";

	private static final int GAMEPAD_LEFT_RIGHT = (Emulator.GAMEPAD_LEFT | Emulator.GAMEPAD_RIGHT);
	private static final int GAMEPAD_UP_DOWN = (Emulator.GAMEPAD_UP | Emulator.GAMEPAD_DOWN);
	private static final int GAMEPAD_DIRECTION = (GAMEPAD_UP_DOWN | GAMEPAD_LEFT_RIGHT);

	private Emulator emulator;
	private EmulatorView emulatorView;
	private Rect surfaceRegion = new Rect();

	private boolean flipScreen;
	private boolean inFastForward;
	private int trackballSensitivity;

	private int quickLoadKey;
	private int quickSaveKey;
	private int fastForwardKey;
	private int screenshotKey;
	private GameServerManager mGamePadManager = GameServerManager.getInstance();
	private SharedPreferences sharedPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		final SharedPreferences prefs = sharedPrefs;

		emulator = Emulator.createInstance(getApplicationContext(),
				getEmulatorEngine(prefs));
		EmuMedia.setOnFrameDrawnListener(this);

		emulatorView = new EmulatorView(this, null);
		setContentView(emulatorView);
		emulatorView.getHolder().addCallback(this);
		emulatorView.requestFocus();

		emulator.setOption("soundEnabled", true);
		
		if (MainActivity.mPlayers.size() > 1) {
			emulator.setFrameUpdateListener(this);
			emulator.setOption("secondController", "gamepad");
		}
		
		mGamePadManager.setOnEventListener(this);
		// keyboard is always present
		if (!loadROM()) {
			finish();
			return;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (emulator != null)
			emulator.unloadROM();
	}

	@Override
	protected void onPause() {
		super.onPause();

		pauseEmulator();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		setFlipScreen(sharedPrefs, newConfig);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus) {
			// reset keys
			emulator.setKeyStates(0);
			emulator.resume();
		} else
			emulator.pause();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (!Intent.ACTION_VIEW.equals(intent.getAction()))
			return;

		pauseEmulator();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == quickLoadKey) {
			return true;
		}
		if (keyCode == quickSaveKey) {
			return true;
		}
		if (keyCode == fastForwardKey) {
			return true;
		}
		if (keyCode == screenshotKey) {
			onScreenshot();
			return true;
		}
		// ignore keys that would annoy the user
		if (keyCode == KeyEvent.KEYCODE_CAMERA
				|| keyCode == KeyEvent.KEYCODE_SEARCH)
			return true;

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			pauseEmulator();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private static int makeKeyStates(int p1, int p2) {
		return (p2 << 16) | (p1 & 0xffff);
	}

	int p1states = 0;
	int p2states = 0;

	public void onGameKeyChanged(Player player, int states) {
		Log.d(LOG_TAG, "player = " + player);
		Log.d(LOG_TAG, "MainActivity.mPlayers.get(0) = "
				+ MainActivity.mPlayers.get(0));

		// resolve conflict keys
		if ((states & GAMEPAD_LEFT_RIGHT) == GAMEPAD_LEFT_RIGHT)
			states &= ~GAMEPAD_LEFT_RIGHT;
		if ((states & GAMEPAD_UP_DOWN) == GAMEPAD_UP_DOWN)
			states &= ~GAMEPAD_UP_DOWN;

		if (MainActivity.mPlayers.size() > 1) {
			if (player.equals(MainActivity.mPlayers.get(0))) {
				p1states = states;
//				states = makeKeyStates(states, p2states);
				Log.d(LOG_TAG, "states1 " + states);
			} else {
				p2states = states;
//				states = makeKeyStates(p1states, states);
				Log.d(LOG_TAG, "states2 " + states);
			}
		}
		emulator.setKeyStates(states);
	}
	
	@Override
	public int onFrameUpdate(int keys) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return makeKeyStates(p1states, p2states);
	}

	public boolean onTrackball(MotionEvent event) {
		float dx = event.getX();
		float dy = event.getY();
		if (flipScreen) {
			dx = -dx;
			dy = -dy;
		}

		int duration1 = (int) (dx * trackballSensitivity);
		int duration2 = (int) (dy * trackballSensitivity);
		int key1 = 0;
		int key2 = 0;

		if (duration1 < 0)
			key1 = Emulator.GAMEPAD_LEFT;
		else if (duration1 > 0)
			key1 = Emulator.GAMEPAD_RIGHT;

		if (duration2 < 0)
			key2 = Emulator.GAMEPAD_UP;
		else if (duration2 > 0)
			key2 = Emulator.GAMEPAD_DOWN;

		if (key1 == 0 && key2 == 0)
			return false;

		emulator.processTrackball(key1, Math.abs(duration1), key2,
				Math.abs(duration2));
		return true;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		emulator.setSurface(holder);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		emulator.setSurface(null);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		final int w = emulator.getVideoWidth();
		final int h = emulator.getVideoHeight();
		surfaceRegion.left = (width - w) / 2;
		surfaceRegion.top = (height - h) / 2;
		surfaceRegion.right = surfaceRegion.left + w;
		surfaceRegion.bottom = surfaceRegion.top + h;

		emulator.setSurfaceRegion(surfaceRegion.left, surfaceRegion.top, w, h);
	}

	public void onFrameDrawn(Canvas canvas) {
	}

	private void pauseEmulator() {
		emulator.pause();
	}

	private void resumeEmulator() {
		if (hasWindowFocus())
			emulator.resume();
	}

	private void setFlipScreen(SharedPreferences prefs, Configuration config) {
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
			flipScreen = prefs.getBoolean("flipScreen", false);
		else
			flipScreen = false;

		emulator.setOption("flipScreen", flipScreen);
	}

	private String getEmulatorEngine(SharedPreferences prefs) {
		return "nes";
	}

	private String getROMFilePath() {
		return "/sdcard/fsb.zip";// getIntent().getData().getPath();
	}

	private boolean isROMSupported(String file) {
		return true;
	}

	private boolean loadROM() {
		String path = getROMFilePath();
		Log.d("BEN", "loadROM path = " + path);

		if (!isROMSupported(path)) {
			finish();
			return false;
		}
		if (!emulator.loadROM(path)) {
			finish();
			return false;
		}
		// reset fast-forward on ROM load
		inFastForward = false;

		emulatorView.setActualSize(emulator.getVideoWidth(),
				emulator.getVideoHeight());
		return true;
	}

	private void onScreenshot() {
		File dir = new File("/sdcard/screenshot");
		if (!dir.exists() && !dir.mkdir()) {
			Log.w(LOG_TAG, "Could not create directory for screenshots");
			return;
		}
		String name = Long.toString(System.currentTimeMillis()) + ".png";
		File file = new File(dir, name);

		pauseEmulator();

		FileOutputStream out = null;
		try {
			try {
				out = new FileOutputStream(file);
				Bitmap bitmap = getScreenshot();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				bitmap.recycle();

			} finally {
				if (out != null)
					out.close();
			}
		} catch (IOException e) {
		}

		resumeEmulator();
	}

	private File getTempStateFile() {
		return new File(getCacheDir(), "saved_state");
	}

	private static byte[] readFile(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		byte[] buffer = new byte[(int) file.length()];
		try {
			if (in.read(buffer) == -1)
				throw new IOException();
		} finally {
			in.close();
		}
		return buffer;
	}

	private static void writeFile(File file, byte[] buffer) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(buffer);
		} finally {
			out.close();
		}
	}

	private void saveState(String fileName) {
		pauseEmulator();

		ZipOutputStream out = null;
		try {
			try {
				out = new ZipOutputStream(new BufferedOutputStream(
						new FileOutputStream(fileName)));
				out.putNextEntry(new ZipEntry("screenshot.png"));

				Bitmap bitmap = getScreenshot();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				bitmap.recycle();
			} finally {
				if (out != null)
					out.close();
			}
		} catch (Exception e) {
		}

		emulator.saveState(fileName);
		resumeEmulator();
	}

	private void loadState(String fileName) {
		File file = new File(fileName);
		if (!file.exists())
			return;

		pauseEmulator();
		emulator.loadState(fileName);

		resumeEmulator();
	}

	private Bitmap getScreenshot() {
		final int w = emulator.getVideoWidth();
		final int h = emulator.getVideoHeight();

		ByteBuffer buffer = ByteBuffer.allocateDirect(w * h * 2);
		emulator.getScreenshot(buffer);

		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
		bitmap.copyPixelsFromBuffer(buffer);
		return bitmap;
	}

	@Override
	public void onSensorChanged(Player player, SensorEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAccuracyChanged(Player player, Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFlushCompleted(Player player, Sensor sensor) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouchEvent(Player player, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyDown(Player player, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyLongPress(Player player, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyUp(Player player, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyMultiple(Player player, int keyCode, int count,
			KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCommand(Player player, byte[] b) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "onCommand " + b[0]);
		onGameKeyChanged(player, bytesToInt(b));
	}

	public byte[] intToByte(int i) {
		byte[] abyte0 = new byte[4];
		abyte0[0] = (byte) (0xff & i);
		abyte0[1] = (byte) ((0xff00 & i) >> 8);
		abyte0[2] = (byte) ((0xff0000 & i) >> 16);
		abyte0[3] = (byte) ((0xff000000 & i) >> 24);
		return abyte0;
	}

	public static int bytesToInt(byte[] bytes) {
		int addr = bytes[0] & 0xFF;
		addr |= ((bytes[1] << 8) & 0xFF00);
		addr |= ((bytes[2] << 16) & 0xFF0000);
		addr |= ((bytes[3] << 24) & 0xFF000000);
		return addr;
	}
}
