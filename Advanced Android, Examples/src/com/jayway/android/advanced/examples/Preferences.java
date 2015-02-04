/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples;

import android.content.Context;
import android.preference.PreferenceManager;

public final class Preferences {
	public static final String PREFS_DEFAULT_TAB = "tab_default";

	public static void setDefaultTab(Context context, int tab) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putInt(PREFS_DEFAULT_TAB, tab).commit();
	}

	public static Integer getDefaultTab(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(
				PREFS_DEFAULT_TAB, 0);
	}

	public static boolean hasDefaultTab(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).contains(
				PREFS_DEFAULT_TAB);
	}

	public static void clearDefaultTab(Context context) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.remove(PREFS_DEFAULT_TAB).commit();
	}
}
