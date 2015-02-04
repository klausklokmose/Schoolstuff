/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples;

import java.util.Iterator;
import java.util.List;

public final class SampleConfiguration implements Iterable<SampleCollection> {

	public SampleConfiguration(List<SampleCollection> samples) {
		this.samples = samples;
	}

	@Override
	public Iterator<SampleCollection> iterator() {
		return samples.iterator();
	}

	private final List<SampleCollection> samples;
}
