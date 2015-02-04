/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public final class CrashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean couldGetTitleBarIcon = requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.activity_crash);
		if (couldGetTitleBarIcon)
			setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
					R.drawable.android_sad);

		String msg = getIntent().getStringExtra("MSG");
		String trace = getIntent().getStringExtra("TRACE");
		TextView tv = (TextView) findViewById(R.id.crash_details);
		tv.setText(msg + "\n\n\n" + trace);
	}
}
