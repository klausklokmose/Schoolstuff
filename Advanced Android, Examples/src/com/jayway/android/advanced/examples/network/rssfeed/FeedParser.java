/*
 * (C)opyright 2013 : Jayway ApS
 */

 package com.jayway.android.advanced.examples.network.rssfeed;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FeedParser {

	// In real life, we'd probably use an XML Pull Parser for efficiency and
	// reduced memory footprint. This is not about optimized XML parsing,
	// however, so we use a DOM parser for convenience.

	public Feed parseFeed(final InputStream feedXmlStream) throws Exception {
		Document doc = parseFeedXml(feedXmlStream);

		Element channel = tryGetFirstChildElement(doc.getDocumentElement(),
				"channel");

		String title = tryGetFirstChildElementText(channel, "title");
		String description = tryGetFirstChildElementText(channel, "description");
		Date lastBuildDate = tryGetFirstChildTimeStamp(channel, "lastBuildDate");
		List<FeedItem> items = tryParseFeedItems(channel);

		return new Feed(title, description, lastBuildDate, items);
	}

	private List<FeedItem> tryParseFeedItems(Element channel) throws Exception {
		List<FeedItem> feedItems = new ArrayList<FeedItem>();

		NodeList items = channel.getElementsByTagName("item");
		for (int i = 0; i < items.getLength(); i++) {
			Element element = (Element) items.item(i);

			String guid = tryGetFirstChildElementText(element, "guid");
			String link = tryGetFirstChildElementText(element, "link");
			String title = tryGetFirstChildElementText(element, "title");
			Date publicationDate = tryGetFirstChildTimeStamp(element, "pubDate");
			String descriptionText = tryGetFirstChildElementText(element,
					"description");
			String contentMarkup = tryGetFirstChildElementText(element,
					"content:encoded");

			feedItems.add(new FeedItem(guid, link, title, publicationDate, descriptionText,
					contentMarkup));
		}
		
		return feedItems;
	}

	private Document parseFeedXml(InputStream xmlStream) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.parse(xmlStream);
	}

	private static Element tryGetFirstChildElement(final Element element,
			final String childElement) {
		NodeList nl = element.getElementsByTagName(childElement);
		return nl.getLength() > 0 ? (Element) nl.item(0) : null;
	}

	private static String tryGetFirstChildElementText(final Element element,
			final String childElement) {
		Element child = tryGetFirstChildElement(element, childElement);
		return child != null ? child.getTextContent().trim() : null;
	}

	private static Date tryGetFirstChildTimeStamp(final Element element,
			final String childElement) throws Exception {
		String lastBuildDate = tryGetFirstChildElementText(element,
				childElement);
		return lastBuildDate != null ? parseDate(lastBuildDate) : null;
	}

	private static Date parseDate(String rssDate) throws Exception {
		return rssDateFormatter.parse(rssDate);
	}

	private static final DateFormat rssDateFormatter = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
}
