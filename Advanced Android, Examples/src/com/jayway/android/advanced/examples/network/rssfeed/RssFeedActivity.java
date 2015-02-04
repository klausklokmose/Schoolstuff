package com.jayway.android.advanced.examples.network.rssfeed;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.jayway.android.advanced.examples.BaseActivity;
import com.jayway.android.advanced.examples.R;

public class RssFeedActivity extends BaseActivity implements FeedReaderResultHandler {
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.network_rssfeed_main);

		feedUrlField = findTypedViewById(R.id.network_rssfeed_main_feedurl);
		feedUrlField.setSelection(feedUrlField.getText().length());
		fetchButton = findTypedViewById(R.id.network_rssfeed_main_fetchbutton);
		clearButton = findTypedViewById(R.id.network_rssfeed_main_clearbutton);
		feedItemList = findTypedViewById(R.id.network_rssfeed_main_feeditems);
		feedItemsAdaptor = new FeedItemArrayAdaptor(this, feedItems);
		feedItemList.setAdapter(feedItemsAdaptor);
		feedItemList.setOnItemClickListener(getFeedItemClickListener());
	}

	public void onFetchFeedButton(final View v) {
		setBusyState();
		String feedUrl = getFeedUrl();
		new FeedReaderTask(this, feedFetcher, feedUrl, this).execute((Void) null);
	}

	public void onClearFeedButton(final View v) {
		feedItems.clear();
		feedItemsAdaptor.notifyDataSetChanged();
	}

	private OnItemClickListener getFeedItemClickListener() {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> item, View view, int position, long id) {
				String itemUrl = feedItems.get(position).link();
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(android.net.Uri.parse(itemUrl));
				startActivity(intent);
			}
		};
	}

	private void setBusyState()
	{
		fetchButton.setEnabled(false);
		clearButton.setEnabled(false);
	}

	private void clearBusyState()
	{
		fetchButton.setEnabled(true);
		clearButton.setEnabled(true);
	}
	
	private String getFeedUrl() {
		return feedUrlField.getText().toString().trim();
	}

	@Override
	public void onFeedRead(Feed feed) {
		if (feed != null) {
			feedItems.clear();
			feedItems.addAll(feed.items());
		}
		feedItemsAdaptor.notifyDataSetChanged();
		clearBusyState();
	}

	private EditText feedUrlField;
	private Button fetchButton;
	private Button clearButton;
	private ListView feedItemList;
	private List<FeedItem> feedItems = new ArrayList<FeedItem>();
	private FeedItemArrayAdaptor feedItemsAdaptor;
	private final FeedFetcher feedFetcher = new FeedFetcher();
}
