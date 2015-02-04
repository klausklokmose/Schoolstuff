/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples.network.rssfeed;

import java.util.Date;

public class FeedItem {

	public FeedItem(final String guid, final String link, final String title, final Date publicationDate, final String descriptionText, final String contentMarkup) {
		this.guid = guid;
		this.link = link;
		this.title = title;
		this.publicationDate = publicationDate;
		this.descriptionText = descriptionText;
		this.contentMarkup = contentMarkup;
	}

	public String guid() {
		return this.guid;
	}

	public String link() {
		return this.link;
	}

	public String title() {
		return this.title;
	}

	public Date publicationDate() {
		return this.publicationDate;
	}

	public String descriptionText() {
		return this.descriptionText;
	}

	public String contentMarkup() {
		return this.contentMarkup;
	}

	private final String guid;
	private final String title;
	private final String link;
	private final Date publicationDate;
	private final String descriptionText;
	private final String contentMarkup;
}
