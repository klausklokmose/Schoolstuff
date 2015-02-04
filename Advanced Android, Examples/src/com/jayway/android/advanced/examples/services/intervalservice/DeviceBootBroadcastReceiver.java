package com.jayway.android.advanced.examples.services.intervalservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public final class DeviceBootBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		if (AutoStartServicePreferences.getAutoStartService(context))
			scheduleServiceAlarm(context);
	}

	private void scheduleServiceAlarm(Context context) {
		final int millisecondInterval = 5 * 1000;
		Intent i = new Intent(context, IntervalService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
		getAlarmManager(context).setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME, // ELAPSED_REALTIME_WAKEUP
				SystemClock.elapsedRealtime() + millisecondInterval,
				millisecondInterval, pi);
	}

	private AlarmManager getAlarmManager(Context context) {
		return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}
}
