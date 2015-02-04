package com.jayway.android.advanced.examples.fragments.component;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jayway.android.advanced.examples.R;

public class SearchBoxFragment extends Fragment implements SearchBox {

	private SearchBoxClient clientActivity;
	private EditText searchTerm;
	private Button searchButton;

	/**
	 * Called when the Fragment instance is attached to a hosting Activity. The
	 * Fragment's UI has not been created at this stage, not that of the Activity.
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof SearchBoxClient))
			throw new ClassCastException("Attached activity must implement the SearchBoxClient interface!");
		this.clientActivity = (SearchBoxClient) activity;
	}

	/**
	 * Called when Fragment is created. Don't create UI yet, only base
	 * initialization.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * Called after creation, to make the Fragment create its UI.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragments_component_searchbox_fragment, container);
		searchTerm = (EditText) fragmentView.findViewById(R.id.fragments_component_searchbox_fragment_searchterm);
		searchButton = (Button) fragmentView.findViewById(R.id.fragments_component_searchbox_fragment_searchbutton);
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dispatchSearch();
			}
		});
		return fragmentView;
	}

	/**
	 * Called after Fragment UI AND the hosting Activity's UI has has been
	 * created.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void setSearchHint(String searchHint) {
		searchTerm.setHint(searchHint);
	}

	private void dispatchSearch() {
		String term = searchTerm.getText().toString().trim();
		if (term.length() > 0) {
			if (clientActivity != null)
				clientActivity.onSearchInvoked(term);

		}
	}
}
