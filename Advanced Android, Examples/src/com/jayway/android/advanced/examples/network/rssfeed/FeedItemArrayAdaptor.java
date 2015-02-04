package com.jayway.android.advanced.examples.network.rssfeed;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jayway.android.advanced.examples.R;

public class FeedItemArrayAdaptor extends ArrayAdapter<FeedItem> {

	public FeedItemArrayAdaptor(Context context, List<FeedItem> feedItems) {
		super(context, R.layout.network_rssfeed_item, feedItems);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.network_rssfeed_item, parent, false);
		TextView titleView = (TextView) rowView.findViewById(R.id.title);
		titleView.setText(getItem(position).title());
		TextView dateView = (TextView) rowView.findViewById(R.id.date);
		dateView.setText(getItem(position).publicationDate().toString());

		return rowView;
	}
}
