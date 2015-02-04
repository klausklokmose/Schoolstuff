package com.jayway.android.advanced.examples.fragments.xml;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jayway.android.advanced.examples.R;

public final class BookmarkListFragment extends ListFragment {
	public interface BookmarkListFragmentController {
	}

	public interface OnBookmarkSelectedListener {
		public void OnBookmarkSelected(String url);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, getResources()
						.getStringArray(R.array.fragments_xml_bookmarks));
		setListAdapter(listAdapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			bookmarkSelectedListener = (OnBookmarkSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnBookmarkSelectedListener");
		}
	}

	@Override
	public void onListItemClick(final ListView l, final View v,
			final int position, final long id) {
		String url = getListView().getItemAtPosition(position).toString();
		bookmarkSelectedListener.OnBookmarkSelected(url);
	}

	private OnBookmarkSelectedListener bookmarkSelectedListener;
}
