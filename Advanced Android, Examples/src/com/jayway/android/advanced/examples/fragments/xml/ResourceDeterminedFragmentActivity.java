package com.jayway.android.advanced.examples.fragments.xml;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.jayway.android.advanced.examples.BaseActivity;
import com.jayway.android.advanced.examples.R;
import com.jayway.android.advanced.examples.fragments.xml.BookmarkListFragment.OnBookmarkSelectedListener;

public final class ResourceDeterminedFragmentActivity extends BaseActivity
		implements OnBookmarkSelectedListener {
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragments_xml_main);
		bookmarkPreviewFragment = findTypedFragmentById(R.id.fragments_xml_bookmark_preview_fragment);
	}

	@Override
	public void OnBookmarkSelected(String url) {
		Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
		showBookmarkDetails(url);
	}

	private void showBookmarkDetails(String url) {
		if (hasPreviewFragment())
			showBookmarkDetailsInPreviewFragment(url);
		else
			showBookmarkDetailsInNewActivity(url);
	}

	private boolean hasPreviewFragment() {
		return bookmarkPreviewFragment != null;
	}

	private void showBookmarkDetailsInPreviewFragment(String url) {
		bookmarkPreviewFragment.showBookmark(url);
	}

	private void showBookmarkDetailsInNewActivity(String url) {
		Intent explicitIntent = new Intent(this,
				SeparatePreviewFragmentActivity.class);
		explicitIntent.putExtra(SeparatePreviewFragmentActivity.URL_PARAMETER,
				url);
		startActivity(explicitIntent);
	}

	private BookmarkPreviewFragment bookmarkPreviewFragment;
}
