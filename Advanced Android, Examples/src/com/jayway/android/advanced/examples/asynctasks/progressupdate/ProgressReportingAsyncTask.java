/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.asynctasks.progressupdate;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public final class ProgressReportingAsyncTask extends AsyncTask<Void, Integer, Void> {

	private static final int TASK_STEPS = 10;
	private static final int TASK_STEP_DURATION = 1;

	public ProgressReportingAsyncTask(final Context context,
			Button startButton, ProgressBar progressBar) {
		this.context = context;
		this.startButton = startButton;
		this.progressBar = progressBar;
	}

	@Override
	protected void onPreExecute() {
		Toast.makeText(context, "Asychronous task starting...",
				Toast.LENGTH_SHORT).show();
		setRunningState();
	}

	@Override
	protected Void doInBackground(final Void... params) {
		for (int i = 0; i < TASK_STEPS; i++) {
			try {
				int progress = calculateProgresPercentage(i, TASK_STEPS);
				// Request a progress update - the update itself will happen on
				// the UI thread an undefined time after this:
				publishProgress(progress);
				
				Thread.sleep(TASK_STEP_DURATION * 1000);
			} catch (InterruptedException e) {
			}
		}
		publishProgress(100);

		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		progressBar.setProgress(progress[0]);
	}

	@Override
	protected void onPostExecute(final Void result) {
		Toast.makeText(context, "Asychronous task completed.",
				Toast.LENGTH_SHORT).show();

		clearRunningState();
	}

	private int calculateProgresPercentage(int step, int steps)
	{
		return (int) ((step / (float) steps) * 100);
	}

	private void setRunningState() {
		startButton.setEnabled(false);
	}

	private void clearRunningState() {
		startButton.setEnabled(true);
	}

	private final Context context;
	private final Button startButton;
	private final ProgressBar progressBar;
}
