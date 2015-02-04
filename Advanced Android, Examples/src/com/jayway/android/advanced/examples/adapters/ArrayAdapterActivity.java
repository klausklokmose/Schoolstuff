package com.jayway.android.advanced.examples.adapters;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jayway.android.advanced.examples.BaseActivity;
import com.jayway.android.advanced.examples.R;

public class ArrayAdapterActivity extends BaseActivity implements OnItemClickListener {

	public static final String[] fruits = new String[] { "banana", "apple", "orange", "lemon", "pinaple", "melon", "another fruit", "sour fruit", "sweet fruit", "a fruit", "tasty fruit",
			" blue fruit", "yellow fruit", "banana2", "apple2", "orange2", "lemon2", "pinaple2", "melon2", "another fruit2", "sour fruit2", "sweet fruit2", "a fruit2", "tasty fruit2", " blue fruit2",
			"yellow fruit2" };
	private ListView listView;
	private ArrayAdapter<String> arrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.array_adapter);
		listView = findTypedViewById(R.id.array_adapter_list);
		listView.setOnItemClickListener(this);
		populateList();
	}

	private void populateList() {
		arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fruits);
		listView.setAdapter(arrayAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View selectedView, int positionOfTheViewInTheAdapter, long rowId) {
		TextView textView = (TextView) selectedView;
		Toast.makeText(ArrayAdapterActivity.this, textView.getText(), Toast.LENGTH_SHORT).show();
	}
}
