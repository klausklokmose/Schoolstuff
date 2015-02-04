/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.asynctasks.baduiupdate;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public final class BadAsyncTask extends AsyncTask<Void, Void, Void> {

	private static final int TASK_DURATION = 10;

	public BadAsyncTask(final Context context) {
		this.context = context;
		slowTask = new SlowTask(TASK_DURATION);
	}

	@Override
	protected void onPreExecute() {
		Toast.makeText(context, "Asychronous task starting...",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected Void doInBackground(final Void... params) {
		Toast.makeText(context, "CRASH - Can't update UI from background!",
				Toast.LENGTH_SHORT).show();

		slowTask.execute();
		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		Toast.makeText(context, "Asychronous task completed.",
				Toast.LENGTH_SHORT).show();
	}

	private final Context context;
	private final SlowTask slowTask;
}
