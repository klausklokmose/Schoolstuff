/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

@SuppressWarnings("serial")
public final class SampleEntry implements Serializable {

	public SampleEntry(final String name, final Class<? extends Activity> intentActivity) {
		this.name = name;
		this.intentActivity = intentActivity;
	}

	@Override
	public String toString() {
		return name != null ? name : "";
	}

	public void start(final Context context) {
		if (intentActivity != null) {
			Intent explicitIntent = new Intent(context, intentActivity);
			context.startActivity(explicitIntent);
		}
	}

	private final String name;
	private Class<? extends Activity> intentActivity;
}
