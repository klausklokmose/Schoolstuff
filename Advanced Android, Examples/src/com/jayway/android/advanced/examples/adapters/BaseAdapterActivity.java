package com.jayway.android.advanced.examples.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jayway.android.advanced.examples.BaseActivity;
import com.jayway.android.advanced.examples.R;

public class BaseAdapterActivity extends BaseActivity {

	private ListView listView;
	private static final int LIST_ITEM = 1;
	private static final int LIST_SEPERATOR = 2;
	private static final int SEPERATOR_POSITION = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.array_adapter);
		listView = findTypedViewById(R.id.array_adapter_list);
		MyAdapter myAdapter = new MyAdapter();
		listView.setAdapter(myAdapter);
		List<String> fruits = Arrays.asList(ArrayAdapterActivity.fruits);
		myAdapter.updateFruits(fruits);

	}

	private class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater = BaseAdapterActivity.this.getLayoutInflater();
		private List<String> fruits = new ArrayList<String>();

		public void updateFruits(List<String> fruits) {
			this.fruits = fruits;
			notifyDataSetChanged();
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getCount() {
			return fruits.size();
		}

		@Override
		public String getItem(int position) {
			return fruits.get(position);
		}

		@Override
		public long getItemId(int position) {
			// use to differentiate the list items if you have different type
			// item in your list
			if (position % SEPERATOR_POSITION == 0) {
				return LIST_SEPERATOR;
			} else {
				return LIST_ITEM;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
//			View view = inflater.inflate(R.layout.array_adapter_item_seperator, parent, false);
//			view = inflater.inflate(R.layout.array_adapter_item, parent, false);
//			TextView index = (TextView) view.findViewById(R.id.array_adapter_item_index);
//			TextView description = (TextView) view.findViewById(R.id.array_adapter_item_description);
//			index.setText(String.valueOf(position));
//			description.setText(getItem(position));
			
			
			View view = convertView;

			if (getItemId(position) == LIST_SEPERATOR) {
				view = inflater.inflate(R.layout.array_adapter_item_seperator, parent, false);
				return view;

			}

			TextView index, description;

			if (view == null || view.getTag() == null) {
				view = inflater.inflate(R.layout.array_adapter_item, parent, false);
				index = (TextView) view.findViewById(R.id.array_adapter_item_index);
				description = (TextView) view.findViewById(R.id.array_adapter_item_description);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.index = index;
				viewHolder.description = description;
				view.setTag(viewHolder);
			} else {
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				index = viewHolder.index;
				description = viewHolder.description;
			}

			index.setText(String.valueOf(position));
			description.setText(getItem(position));
			return view;
		}

	}

	private static class ViewHolder {
		TextView index, description;
	}
}
