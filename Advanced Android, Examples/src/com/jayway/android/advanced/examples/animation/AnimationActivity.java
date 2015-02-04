package com.jayway.android.advanced.examples.animation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jayway.android.advanced.examples.BaseActivity;
import com.jayway.android.advanced.examples.R;

public abstract class AnimationActivity extends BaseActivity {

	protected AnimationController animationController;
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		init();
	}

	protected int getLayout() {
		return R.layout.animations;
	}

	protected int getIdOfViewToAnimate() {
		return R.id.button;
	}

	private void init() {
		animationController = createAnimationController(findViewById(getIdOfViewToAnimate()));
	}

	protected abstract AnimationController createAnimationController(View view);

	public void toggleAnimation(View view) {
		if (animationController != null) {
			animationController.toogleAnimation();
		}
	}

	public void increment(View view) {
		Button button = (Button) view;
		button.setText(String.valueOf(++count));
	}

}
