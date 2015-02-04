package com.jayway.android.advanced.examples.services.intervalservice;

import android.content.Context;
import android.preference.PreferenceManager;

public final class AutoStartServicePreferences {
	public static final String PREFS_AUTOSTART_SERVICE = "services_intervalservice_autostart";

	public static void setAutoStartService(Context context, boolean autoStart) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putBoolean(PREFS_AUTOSTART_SERVICE, autoStart).commit();
	}

	public static boolean getAutoStartService(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(PREFS_AUTOSTART_SERVICE, false);
	}
}
