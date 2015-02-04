/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.asynctasks.cancellation;

public final class SlowInterruptableTask {
	public SlowInterruptableTask(final int duration) {
		this.duration = duration;
	}

	public boolean execute() {
		try {
			Thread.sleep(duration * 1000);
			return false;
		} catch (InterruptedException e) {
			return true;
		}
	}

	private final int duration;
}
