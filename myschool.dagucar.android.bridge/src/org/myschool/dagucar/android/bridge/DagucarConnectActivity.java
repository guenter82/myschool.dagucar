package org.myschool.dagucar.android.bridge;

import org.myschool.dagucar.android.bridge.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class DagucarConnectActivity extends Activity implements CallbackInterface {
	
	private TextView userText;
	private final StringBuffer buffer=new StringBuffer();
	private final BluetoothAdapter bAdapter=BluetoothAdapter.getDefaultAdapter();
	private final TcpBluetoothBridgeAndroid bridge=new TcpBluetoothBridgeAndroid(this);
	private final Thread server=new Thread(bridge);
	private int ACTION_REQUEST_ENABLE = 99;
	
	public final BroadcastReceiver finishedBluetoothReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	        	if (server!=null && !server.isAlive()) server.start();
	        }
		}
	};
	
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_dagucar_connect);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.text_message);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.button_refresh).setOnTouchListener(
				mDelayHideTouchListener);
		findViewById(R.id.button_start).setOnTouchListener(
				mDelayHideTouchListener);
		findViewById(R.id.button_stop).setOnTouchListener(
				mDelayHideTouchListener);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	public void startBridging(View source) {
		userText = (TextView) findViewById(R.id.text_message);
		userText.setText("TCP Android Server / Bluetooth DaguCar: ");

		if (bAdapter == null) {
			userText.append("\r\nDevice does not support Bluetooth");
			return;
		}
		if (!bAdapter.isEnabled()) {
			Intent enableBluetooth=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBluetooth, ACTION_REQUEST_ENABLE);
		} else {
			onActivityResult(ACTION_REQUEST_ENABLE, RESULT_OK, null);
		}
		


	}
	
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == ACTION_REQUEST_ENABLE) {
            if (resultCode == RESULT_OK) {
            	userText.append("\r\nBluetooth enabled!");      
    			if ( !bridge.isDaguCarPaired() ) {
    				IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    				IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    				registerReceiver(bridge.mReceiver, filter); // Don't forget to unregister during onDestroy
    				registerReceiver(finishedBluetoothReceiver, filter2); 
    				bAdapter.startDiscovery();
    			} else {
    				if (server!=null && !server.isAlive()) server.start();
    			}
            } else {
            	userText.append("\r\nBluetooth was disabled and could not be enabled!");
            }
        }
        
    }



	
	public void stopBridging(View source) {
		userText = (TextView) findViewById(R.id.text_message);
		userText.setText("Stopped TCP Server!");
		if (server!=null) {
			server.interrupt();
		}

	}
	
	public void refreshTextView(View source) {
		userText = (TextView) findViewById(R.id.text_message);
		userText.setText(buffer.toString());
	}

	@Override
	public Object callback(Object param) {
		buffer.append(String.valueOf(param));
		return null;
	}	
	
	
	@Override
	public void onDestroy() {
	    super.onDestroy();  // Always call the superclass
	    if (bridge!=null) {
	    	unregisterReceiver(bridge.mReceiver);
	    	unregisterReceiver(finishedBluetoothReceiver);
	    }
	    if (server!=null) {
			server.interrupt();
		}
	    
	}
	

}

