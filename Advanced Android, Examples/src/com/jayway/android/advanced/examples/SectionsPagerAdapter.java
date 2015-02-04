/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
 * of the sections/tabs/pages.
 */
public final class SectionsPagerAdapter extends FragmentPagerAdapter {

	public SectionsPagerAdapter(final SampleConfiguration sampleConfiguration,
			final FragmentManager fm) {
		super(fm);
		tabNames = getMainNavigationTabs(sampleConfiguration);
		tabSamples = getTabSamples(sampleConfiguration);
	}

	@Override
	public Fragment getItem(final int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Fragment fragment = new DummySectionFragment();
		Fragment fragment = new SampleListFragment();
		Bundle args = new Bundle();
		args.putInt(SampleListFragment.ARG_SECTION_NUMBER, position + 1);
		args.putSerializable(SampleListFragment.ARG_EXAMPLES,
				getExamples(position));
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		return tabNames.size();
	}

	@Override
	public CharSequence getPageTitle(final int position) {
		Locale l = Locale.getDefault();
		return tabNames.get(position).toUpperCase(l);
	}

	private ArrayList<SampleEntry> getExamples(final int position) {
		return tabSamples.get(position);
	}

	private static List<String> getMainNavigationTabs(
			final SampleConfiguration sampleConfiguration) {
		ArrayList<String> sampleCollectionNames = new ArrayList<String>();

		for (SampleCollection collection : sampleConfiguration) {
			sampleCollectionNames.add(collection.toString());
		}

		return sampleCollectionNames;
	}

	private static List<ArrayList<SampleEntry>> getTabSamples(
			final SampleConfiguration sampleConfiguration) {
		ArrayList<ArrayList<SampleEntry>> sampleCollections = new ArrayList<ArrayList<SampleEntry>>();

		for (SampleCollection collection : sampleConfiguration) {
			ArrayList<SampleEntry> sampleEntries = new ArrayList<SampleEntry>();
			for (SampleEntry entry : collection) {
				sampleEntries.add(entry);
			}
			sampleCollections.add(sampleEntries);
		}

		return sampleCollections;
	}

	private final List<String> tabNames;
	private final List<ArrayList<SampleEntry>> tabSamples;
}
