/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.asynctasks.progressupdate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.jayway.android.advanced.examples.BaseActivity;
import com.jayway.android.advanced.examples.R;

public final class ProgressReportingActivity extends BaseActivity {
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.asynctasks_progressupdate_main);

		startButton = findTypedViewById(R.id.asyntasks_progressupdate_button_start);
		progressBar = findTypedViewById(R.id.asyntasks_progressupdate_progressbar);
	}

	public void onStartTaskButton(final View v) {
		task = new ProgressReportingAsyncTask(this, startButton, progressBar);
		task.execute((Void) null);
	}

	private Button startButton;
	private ProgressBar progressBar;
	private ProgressReportingAsyncTask task;
}
