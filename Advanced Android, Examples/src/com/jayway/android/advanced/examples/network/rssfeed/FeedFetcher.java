package com.jayway.android.advanced.examples.network.rssfeed;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public final class FeedFetcher {

	private static final int HTTP_TIMEOUT_MS = 10000;

	public InputStream fetchFeed(final String feedUrl) throws Exception {
		HttpGet getRequest = new HttpGet(feedUrl);
		HttpResponse response = httpClient.execute(getRequest);
		return response.getEntity().getContent();
	}

	private static DefaultHttpClient getHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT_MS);
		HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT_MS);
		DefaultHttpClient client = new DefaultHttpClient(params);
		ClientConnectionManager mgr = client.getConnectionManager();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(client.getParams(), mgr.getSchemeRegistry()), client.getParams());
		return client;
	}

	private static final HttpClient httpClient = getHttpClient();
}
