package org.orzlabs.android.massage;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Massage extends Activity
implements OnClickListener,
OnSeekBarChangeListener {
	private static final String TAG = Massage.class.getSimpleName();

	private static final int QUIT_ITEM = 0;

	private static final int MASSAGE_NOTIFICATION = 0;
	
	private static int progress = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SeekBar seekBar = (SeekBar) findViewById(R.id.SeekBar01);
		seekBar.setOnSeekBarChangeListener(this);
		seekBar.setProgress(progress);
		Log.d(TAG, "onCreate called.");
	}

	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyDown called:keyCode:" + keyCode + ", event:" + event);

		boolean ret = true;
		SeekBar seekBar = (SeekBar) findViewById(R.id.SeekBar01);
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			seekBar.setProgress(seekBar.getProgress() + 10);
			onProgressChanged(seekBar, seekBar.getProgress(), true);
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			seekBar.setProgress(seekBar.getProgress() - 10);
			onProgressChanged(seekBar, seekBar.getProgress(), true);
		} else {
			ret = super.onKeyDown(keyCode, event);
		}

		return ret;
	}

	public synchronized void onClick(View v) {
		vibStop();
		vibStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem itemQuit = menu.add(0, QUIT_ITEM, 0, "Quit");
		itemQuit.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		vibStop();
		progress = 0;

		finish();
		return true;
	}

	public void onProgressChanged(SeekBar seekBar, int pProgress,
			boolean fromUser) {
		Log.d(TAG, "onProgressChanged called.");
		vibStop();
		if (pProgress == 0) {
			progress = 0;
			return;
		}
		progress = pProgress;
		
		vibStart();
		if (pProgress == seekBar.getMax()) {
			MassageService.vibMode = MassageService.RANDOM;
			return;
		}
		MassageService.vibMode = MassageService.CONTINUOUS;
		MassageService.vibratingTime  = progress * 10 + 15;
	}

	private synchronized void vibStart() {
		Intent service = new Intent(this, MassageService.class);
		startService(service);

		Log.d(TAG, "vibrate start.");
	}

	private synchronized void vibStop() {
		Intent service = new Intent(this, MassageService.class);
		stopService(service);

		Log.d(TAG, "vibrate end.");
	}
	@Override
	public void onPause() {
		super.onPause();
		if (progress != 0) {
			notification();
		}
	}
	private void notification() {
		NotificationManager nm =
			(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification =
			new Notification(R.drawable.icon,
					getText(R.string.NotificationMsg),
					System.currentTimeMillis());
		PendingIntent pi =
			PendingIntent.getActivity(this,
					0,
					new Intent(this, Massage.class),
					0);
		notification.setLatestEventInfo(this,
				"Simple Massager",
				getText(R.string.NotificationMsg),
				pi);
		nm.notify(MASSAGE_NOTIFICATION, notification);
	}

	@Override
	public void onResume() {
		super.onResume();
		NotificationManager nm =
			(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(MASSAGE_NOTIFICATION);
		Log.d(TAG, "onResume called.");
	}
	public void onStartTrackingTouch(SeekBar seekBar) {
		Log.d(TAG, "onStartTrackingTouch called.");
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		Log.d(TAG, "onStopTrackingTouch called.");
	}
}