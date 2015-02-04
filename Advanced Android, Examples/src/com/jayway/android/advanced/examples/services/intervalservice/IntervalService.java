/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.services.intervalservice;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

public final class IntervalService extends IntentService {

	public IntervalService() {
		super("IntervalService");
	}

	public void onCreate() {
		super.onCreate();
		handler = new Handler();
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		invocationCount++;
		makeToastOnHandler("Interval Service doing background work... ("
				+ invocationCount + ")");
	}

	private void makeToastOnHandler(final String message) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private Handler handler;
	private static int invocationCount = 0;
}
