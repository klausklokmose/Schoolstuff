package com.jayway.android.advanced.examples.fragments.component;

public interface SearchBox {

	public interface SearchBoxClient {
		void onSearchInvoked(String searchterm);
	}

	void setSearchHint(String searchHint);
}
