package com.jayway.android.advanced.examples.customview;

import android.os.Bundle;

import com.jayway.android.advanced.examples.BaseActivity;
import com.jayway.android.advanced.examples.R;

public class CustomViewActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_view);
		init();
	}

	private void init() {
		final Circle circle = findTypedViewById(R.id.custom_view_circle);
		circle.setText("Hello there");
		circle.setTextSize(90);
	}
}
