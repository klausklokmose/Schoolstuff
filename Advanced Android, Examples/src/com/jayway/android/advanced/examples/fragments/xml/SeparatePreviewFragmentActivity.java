package com.jayway.android.advanced.examples.fragments.xml;

import android.os.Bundle;

import com.jayway.android.advanced.examples.BaseActivity;
import com.jayway.android.advanced.examples.R;

public final class SeparatePreviewFragmentActivity extends BaseActivity {
	
	public static final String URL_PARAMETER = "URL";
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragments_xml_preview);

		previewUrl = getIntent().getExtras().getString(URL_PARAMETER, "about:blank");
		
		getBookmarkPreviewFragment().showBookmark(previewUrl);
	}

	private BookmarkPreviewFragment getBookmarkPreviewFragment() {
		return (BookmarkPreviewFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragments_xml_bookmark_preview_fragment);
	}

	private String previewUrl;
}
