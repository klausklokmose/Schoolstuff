/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public abstract class BaseActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, final Throwable ex) {
				Intent crashedIntent = new Intent(BaseActivity.this,
						CrashActivity.class);
				StringWriter errors = new StringWriter();
				ex.printStackTrace(new PrintWriter(errors));
				crashedIntent.putExtra("MSG", ex.toString());
				crashedIntent.putExtra("TRACE", errors.toString());
				crashedIntent
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(crashedIntent);

				System.exit(0);
				// If you don't kill the VM here the app goes into limbo
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected <TView extends View> TView findTypedViewById(int id) {
		return (TView) findViewById(id);
	}

	@SuppressWarnings("unchecked")
	protected <TFragment extends Fragment> TFragment findTypedFragmentById(int id) {
		return (TFragment) getSupportFragmentManager().findFragmentById(id);
	}
}
