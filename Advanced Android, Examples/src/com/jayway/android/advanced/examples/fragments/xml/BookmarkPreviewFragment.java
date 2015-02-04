package com.jayway.android.advanced.examples.fragments.xml;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.jayway.android.advanced.examples.R;

public final class BookmarkPreviewFragment extends Fragment {

	public void showBookmark(String url) {
		urlTextView.setText(url);
		webView.loadUrl(url);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragments_xml_preview_fragment,
				container, false);

		urlTextView = (TextView) view
				.findViewById(R.id.fragments_xml_preview_url);
		webView = (WebView) view
				.findViewById(R.id.fragments_xml_preview_webpage);
		configureWebView(webView);

		return view;
	}

	private void configureWebView(final WebView webView) {
		webView.setWebViewClient(new WebViewClient() {

			// Capture redirects and links so that they don't open an external browser:
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webView.loadUrl(url);
				return true;
			}

		});

		WebSettings webSettings = webView.getSettings();
		webSettings.setBuiltInZoomControls(true);
	}

	private TextView urlTextView;
	private WebView webView;
}
