/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.services.simpleworkqueue;

public final class SlowTask {
	public SlowTask(final int duration) {
		this.duration = duration;
	}

	public void execute() {
		try {
			Thread.sleep(duration * 1000);
		} catch (InterruptedException e) {
		}
	}

	private final int duration;
}
