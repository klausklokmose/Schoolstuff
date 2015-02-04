/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public final class SampleListFragment extends ListFragment {
	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final String ARG_EXAMPLES = "examples";

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		try {
			fragmentSelectedListener = (SampleFragmentSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ExampleFragmentSelectedListener");
		}
	}

	@SuppressWarnings("unchecked")
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		arguments = getArguments();
		List<SampleEntry> entries = (List<SampleEntry>) arguments
				.get(ARG_EXAMPLES);
		if (entries == null)
			entries = new ArrayList<SampleEntry>();

		ArrayAdapter<SampleEntry> adapter = new ArrayAdapter<SampleEntry>(
				activity, android.R.layout.simple_list_item_1, entries);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Object o = getListView().getItemAtPosition(position);
		if (o instanceof SampleEntry) {
			SampleEntry e = (SampleEntry) o;
			if (fragmentSelectedListener != null)
				fragmentSelectedListener.onExampleFragmentSelected(e);
		}
	}

	private Activity activity;
	private SampleFragmentSelectedListener fragmentSelectedListener;
	private Bundle arguments;
}
