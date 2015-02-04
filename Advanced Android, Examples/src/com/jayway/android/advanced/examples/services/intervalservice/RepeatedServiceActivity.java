/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.services.intervalservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.jayway.android.advanced.examples.BaseActivity;
import com.jayway.android.advanced.examples.R;

public final class RepeatedServiceActivity extends BaseActivity {
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.services_intervalservice_main);

		serviceIntervalField = findTypedViewById(R.id.services_intervalservice_repeatinterval);
		startButton = findTypedViewById(R.id.services_intervalservice_button_start);
		stopButton = findTypedViewById(R.id.services_intervalservice_button_stop);
		autoStartCheckBox = findTypedViewById(R.id.services_intervalservice_checkbox_autostart);

		setUiState();
	}

	public void onStartServiceButton(View button) {
		Toast.makeText(this, "Starting Interval Service...", Toast.LENGTH_SHORT)
				.show();
		scheduleServiceAlarm();
		setServiceRunningState();
	}

	public void onStopServiceButton(View button) {
		Toast.makeText(this, "Stopping Interval Service...", Toast.LENGTH_SHORT)
				.show();
		unscheduleServiceAlarm();
		setServiceNotRunningState();
	}

	private void setUiState() {
		if (!isAlarmSet()) {
			setServiceNotRunningState();
		} else {
			setServiceRunningState();
		}
		updateAutoStartCheckbox();
	}

	private void setServiceRunningState() {
		disableIntervalField();
		disableStartButton();
		enableStopButton();
	}

	private void setServiceNotRunningState() {
		enableIntervalField();
		enableStartButton();
		disableStopButton();
	}

	private void enableIntervalField() {
		serviceIntervalField.setEnabled(true);
	}

	private void disableIntervalField() {
		serviceIntervalField.setEnabled(false);
	}

	private void enableStartButton() {
		startButton.setEnabled(true);
	}

	private void disableStartButton() {
		startButton.setEnabled(false);
	}

	private void enableStopButton() {
		stopButton.setEnabled(true);
	}

	private void disableStopButton() {
		stopButton.setEnabled(false);
	}

	private void updateAutoStartCheckbox() {
		boolean autoStart = AutoStartServicePreferences
				.getAutoStartService(this);
		autoStartCheckBox.setChecked(autoStart);
		autoStartCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean isChecked) {
						AutoStartServicePreferences.setAutoStartService(
								RepeatedServiceActivity.this, isChecked);
					}
				});
	}

	private int getServiceIntervalMilliseonds() {
		int seconds = 0;
		try {
			seconds = Integer.parseInt(serviceIntervalField.getText()
					.toString().trim());
		} catch (NumberFormatException e) {
		}

		return seconds * 1000;
	}

	private boolean isAlarmSet() {
		Intent i = new Intent(this, IntervalService.class);
		PendingIntent pi = PendingIntent.getService(this, 0, i,
				PendingIntent.FLAG_NO_CREATE);
		return pi != null;
	}

	private void scheduleServiceAlarm() {
		final int millisecondInterval = getServiceIntervalMilliseonds();
		Intent i = new Intent(this, IntervalService.class);
		PendingIntent pi = PendingIntent.getService(this, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
		getAlarmManager().setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME, // ELAPSED_REALTIME_WAKEUP
				SystemClock.elapsedRealtime() + millisecondInterval,
				millisecondInterval, pi);
	}

	private void unscheduleServiceAlarm() {
		Intent i = new Intent(this, IntervalService.class);
		PendingIntent pi = PendingIntent.getService(this, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		getAlarmManager().cancel(pi);
	}

	private AlarmManager getAlarmManager() {
		return (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	}

	private EditText serviceIntervalField;
	private Button startButton;
	private Button stopButton;
	private CheckBox autoStartCheckBox;
}
