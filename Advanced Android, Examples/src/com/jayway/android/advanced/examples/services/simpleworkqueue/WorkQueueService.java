/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.services.simpleworkqueue;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

public final class WorkQueueService extends IntentService {

	public static final String PARAM_MESSAGE = "IN_MESSAGE";

	private static final int TASK_DURATION = 10;

	public WorkQueueService() {
		super("WorkQueueService");
		// Set Sticky mode:
		// If process dies before onHandleIntent returns, the process will be
		// restarted (at an undefined point in the future) and the intent redelivered.
		setIntentRedelivery(true);
	}

	public void onCreate() {
		super.onCreate();
		handler = new Handler();
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		String message = intent.getStringExtra(PARAM_MESSAGE);

		makeToastOnHandler("Starting background work: " + message);
		new SlowTask(TASK_DURATION).execute();
		makeToastOnHandler("Background work completed: " + message);
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
}
