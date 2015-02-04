/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.network.rssfeed;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public final class FeedReaderTask extends AsyncTask<Void, Void, Feed> {

	public FeedReaderTask(final Context context, final FeedFetcher fetcher, final String feedUrl, final FeedReaderResultHandler resultHandler) {
		this.context = context;
		this.fetcher = fetcher;
		this.feedUrl = feedUrl;
		this.resultHandler = resultHandler;
	}

	@Override
	protected void onPreExecute() {
		Toast.makeText(context, "Fetching RSS feed:\n" + feedUrl, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected Feed doInBackground(final Void... params) {
		FeedParser parser = new FeedParser();
		try {
			return parser.parseFeed(fetcher.fetchFeed(feedUrl));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(final Feed result) {
		String feedItemsRead = new Integer(result != null ? result.items().size() : 0).toString();
		Toast.makeText(context, "Fetched " + feedItemsRead + " feed items.", Toast.LENGTH_SHORT).show();
		resultHandler.onFeedRead(result);
	}

	private final Context context;
	private final FeedFetcher fetcher;
	private final String feedUrl;
	private final FeedReaderResultHandler resultHandler;
}
