/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.services.simpleworkqueue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jayway.android.advanced.examples.BaseActivity;
import com.jayway.android.advanced.examples.R;

public final class IntentServiceActivity extends BaseActivity {
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.services_simpleworkqueue_main);

		parameterField = findTypedViewById(R.id.services_simpleworkqueue_taskparameter);
	}

	public void onStartTaskButton(final View v) {
		if (hasMessage()) {
			startBackgroundTask(getMessage());
			clearMessage();
		}
	}

	private boolean hasMessage() {
		final String msg = getMessage();
		return msg != null && !msg.isEmpty();
	}

	private String getMessage() {
		return parameterField.getText().toString().trim();
	}

	private void clearMessage() {
		parameterField.setText("");
	}

	private void startBackgroundTask(final String message) {
		final Intent msgIntent = new Intent(this, WorkQueueService.class);
		msgIntent.putExtra(WorkQueueService.PARAM_MESSAGE, message);
		startService(msgIntent);
	}

	private EditText parameterField;
}
