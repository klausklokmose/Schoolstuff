/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.asynctasks.uiblocking;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public final class SlowAsyncTask extends AsyncTask<Void, Void, Void> {

	public SlowAsyncTask(final Context context, final int duration) {
		this.context = context;
		slowTask = new SlowTask(duration);
	}

	@Override
	protected void onPreExecute() {
		Toast.makeText(context, "Asychronous task starting...",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected Void doInBackground(final Void... params) {
		slowTask.execute();
		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		Toast.makeText(context, "Asychronous task completed!",
				Toast.LENGTH_SHORT).show();
	}

	private final Context context;
	private final SlowTask slowTask;
}
