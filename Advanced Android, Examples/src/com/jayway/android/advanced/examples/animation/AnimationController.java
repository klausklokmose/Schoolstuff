package com.jayway.android.advanced.examples.animation;


public abstract class AnimationController {


	private boolean isRunning = false;

	public void toogleAnimation() {
		if (isRunning) {
			isRunning = false;
			stopAnimation();
		} else {
			isRunning = true;
			startAnimation();
		}
	}

	protected abstract void startAnimation();

	protected abstract void stopAnimation();

	public boolean isRunning() {
		return isRunning;
	}

}
