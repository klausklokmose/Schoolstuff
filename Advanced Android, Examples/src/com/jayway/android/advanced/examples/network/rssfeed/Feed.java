/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.network.rssfeed;

import java.util.Date;
import java.util.List;

public class Feed {

	private final String title;
	private final String description;
	private final Date lastBuildDate;
	private final List<FeedItem> feedItems;

	public Feed(final String title, final String description,
			final Date lastBuildDate, final List<FeedItem> feedItems) {
		this.title = title;
		this.description = description;
		this.lastBuildDate = lastBuildDate;
		this.feedItems = feedItems;
	}

	public String title() {
		return this.title;
	}

	public String description() {
		return this.description;
	}

	public Date lastBuildDate() {
		return this.lastBuildDate;
	}
	
	public List<FeedItem> items()
	{
		return this.feedItems;
	}
}
