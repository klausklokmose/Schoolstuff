/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.asynctasks.baduiupdate;

import android.os.Bundle;
import android.view.View;

import com.jayway.android.advanced.examples.BaseActivity;
import com.jayway.android.advanced.examples.R;

public final class BadUiUpdateActivity extends BaseActivity {
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.asynctasks_baduiupdate_main);
	}

	public void onStartTaskButton(final View v) {
		task = new BadAsyncTask(this);
		task.execute((Void) null);
	}

	private BadAsyncTask task;
}
