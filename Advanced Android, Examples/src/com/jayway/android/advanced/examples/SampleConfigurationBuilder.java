/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public final class SampleConfigurationBuilder {

	public final class SampleCollectionBuilder {

		public SampleCollectionBuilder(final String name) {
			this.name = name;
		}

		public SampleCollectionBuilder addSample(final String name,
				Class<? extends Activity> activity) {
			entries.add(new SampleEntry(name, activity));
			return this;
		}

		public SampleConfigurationBuilder endCollection() {
			collections.add(new SampleCollection(name, entries));
			return SampleConfigurationBuilder.this;
		}

		private final String name;
		private final List<SampleEntry> entries = new ArrayList<SampleEntry>();
	}

	public SampleCollectionBuilder beginCollection(final String name) {
		return new SampleCollectionBuilder(name);
	}

	public SampleConfiguration build() {
		return new SampleConfiguration(collections);
	}

	private final List<SampleCollection> collections = new ArrayList<SampleCollection>();
}
