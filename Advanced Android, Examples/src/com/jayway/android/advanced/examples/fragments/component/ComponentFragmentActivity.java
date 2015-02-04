package com.jayway.android.advanced.examples.fragments.component;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.Toast;

import com.jayway.android.advanced.examples.R;
import com.jayway.android.advanced.examples.fragments.component.SearchBox.SearchBoxClient;

public class ComponentFragmentActivity extends Activity implements SearchBoxClient {

	private FragmentManager fragmentManager;
	private SearchBox searchBox;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.fragmentManager = getFragmentManager();
		setContentView(R.layout.fragments_component_main);
	}

	@Override
	public void onSearchInvoked(String searchTerm) {
		Toast.makeText(this, "Searching for: " + searchTerm, Toast.LENGTH_SHORT).show();
		// Use latest search term as hint for next search:
		searchBox = (SearchBox)fragmentManager.findFragmentById(R.id.fragments_component_main_searchbox);
		searchBox.setSearchHint(searchTerm);
	}
}
