/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.asynctasks.uiblocking;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jayway.android.advanced.examples.BaseActivity;
import com.jayway.android.advanced.examples.R;

public final class UiBlockingActivity extends BaseActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.asynctasks_uiblocking_main);

		taskDurationField = findTypedViewById(R.id.asyntasks_uiblocking_taskduration);
	}

	public void onExecSyncButton(final View v) {
		Toast.makeText(this, "Sychronous task starting...", Toast.LENGTH_SHORT)
		.show();
		new SlowTask(getTaskDuration()).execute();
		Toast.makeText(this, "Sychronous task completed!", Toast.LENGTH_SHORT)
				.show();
	}

	public void onExecAsyncButton(final View v) {
		new SlowAsyncTask(this, getTaskDuration()).execute((Void) null);
	}

	private int getTaskDuration() {
		try {
			return Integer.parseInt(taskDurationField.getText().toString());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private EditText taskDurationField;
}
