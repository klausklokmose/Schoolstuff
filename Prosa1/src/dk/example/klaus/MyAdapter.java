package dk.example.klaus;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private String[] str;
	private Context c;

	public MyAdapter(Context c, String[] str) {
		this.c = c;
		this.str = str;
		inflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return str.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return str[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		TextView tv;
		if (convertView == null) {
			view = inflater.inflate(R.layout.list_thing, parent, false);
			tv = (TextView) view.findViewById(R.id.textView1);
			tv.setText(str[position]);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.tv = tv;
			view.setTag(viewHolder);
		} else {
			ViewHolder viewHolder = (ViewHolder) view.getTag();
			tv = viewHolder.tv;
			
		}
		return view;
	}

	private static class ViewHolder {
		public TextView tv;
	}
}
