/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.asynctasks.cancellation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.jayway.android.advanced.examples.BaseActivity;
import com.jayway.android.advanced.examples.R;

public final class AsyncTaskCancellationActivity extends BaseActivity {
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.asynctasks_cancellation_main);

		spinner = findTypedViewById(R.id.asyntasks_cancellation_spinner);
		startButton = findTypedViewById(R.id.asyntasks_cancellation_button_start);
		cancelButton = findTypedViewById(R.id.asyntasks_cancellation_button_cancel);
		interruptButton = findTypedViewById(R.id.asyntasks_cancellation_button_interrupt);
	}

	public void onStartTaskButton(final View v) {
		task = new SlowCancellableAsyncTask(this, spinner, startButton, cancelButton, interruptButton);
		task.execute((Void) null);
	}

	public void onCancelTaskButton(final View v) {
		task.cancel(false);
	}

	public void onInterruptAndCancelTaskButton(final View v) {
		task.cancel(true);
	}

	private Button startButton;
	private Button cancelButton;
	private Button interruptButton;
	private ProgressBar spinner;
	
	private SlowCancellableAsyncTask task;
}
