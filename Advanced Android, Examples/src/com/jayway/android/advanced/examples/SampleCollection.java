/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class SampleCollection implements Iterable<SampleEntry> {

	public SampleCollection(final String name, final List<SampleEntry> entries) {
		this.name = name;
		this.entries = entries;
	}

	public SampleCollection(final String name) {
		this(name, new ArrayList<SampleEntry>());
	}

	@Override
	public String toString() {
		return name != null ? name : "";
	}

	@Override
	public Iterator<SampleEntry> iterator() {
		return entries.iterator();
	}

	private final String name;
	private final List<SampleEntry> entries;
}
