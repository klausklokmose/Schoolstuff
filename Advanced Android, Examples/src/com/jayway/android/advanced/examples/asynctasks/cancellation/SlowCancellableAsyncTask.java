/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.asynctasks.cancellation;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public final class SlowCancellableAsyncTask extends AsyncTask<Void, Void, Boolean> {

	private static final int TASK_DURATION = 10;

	public SlowCancellableAsyncTask(final Context context,
			final ProgressBar spinner, final Button startButton,
			final Button cancelButton, final Button interruptButton) {
		this.context = context;
		this.spinner = spinner;
		this.startButton = startButton;
		this.cancelButton = cancelButton;
		this.interruptButton = interruptButton;
		slowTask = new SlowInterruptableTask(TASK_DURATION);
	}

	@Override
	protected void onPreExecute() {
		Toast.makeText(context, "Asychronous task starting...",
				Toast.LENGTH_SHORT).show();
		
		setRunningState();
	}

	@Override
	protected Boolean doInBackground(final Void... params) {
		return slowTask.execute();
	}

	@Override
	protected void onPostExecute(final Boolean interrupted) {
		clearRunningState();
		
		Toast.makeText(context, "Asychronous task completed.",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onCancelled(final Boolean interrupted) {
		clearRunningState();
		
		String result = interrupted ? "(interrupted)" : "(non-interrupt)";
		
		Toast.makeText(context, "Asychronous task CANCELLED " + result,
				Toast.LENGTH_SHORT).show();
	}

	private void setRunningState()
	{
		showSpinner();
		disableStartButton();
		enableCancelButtons();
	}
	
	private void clearRunningState()
	{
		hideSpinner();
		enableStartButton();
		disableCancelButtons();
	}
	
	private void showSpinner() {
		spinner.setVisibility(View.VISIBLE);
	}

	private void hideSpinner() {
		spinner.setVisibility(View.GONE);
	}

	private void enableStartButton() {
		startButton.setEnabled(true);
	}

	private void disableStartButton() {
		startButton.setEnabled(false);
	}

	private void enableCancelButtons() {
		cancelButton.setEnabled(true);
		interruptButton.setEnabled(true);
	}

	private void disableCancelButtons() {
		cancelButton.setEnabled(false);
		interruptButton.setEnabled(false);
	}

	private final Context context;
	private final ProgressBar spinner;
	private final Button startButton;
	private final Button cancelButton;
	private final Button interruptButton;
	private final SlowInterruptableTask slowTask;
}
