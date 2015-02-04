package com.example.smartcoffee;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeAdapter extends ArrayAdapter<String> {

	private LayoutInflater inflator;
	// private static ArrayList<String> recipeList;
	private Vibrator vi;
	private Context c;
	private Activity activ;
	private List<Recipe[]> list;
	public static final Integer[] icons = new Integer[] { R.drawable.one,
			R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five,
			R.drawable.six, R.drawable.seven, R.drawable.eight, R.drawable.nine };;

	public RecipeAdapter(Activity activity, int textViewResourceId,
			List<String> list) {
		super(activity, textViewResourceId, list);
		this.activ = activity;
		this.list = MainActivity.finalList;

		c = activity.getApplicationContext();

		vi = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		// if (convertView == null) {
		inflator = activ.getLayoutInflater();
		convertView = inflator.inflate(R.layout.recipe_item, parent, false);

		final Recipe[] two_recipes = list.get(position);
		// Log.d("STR", "---" + str[0].getName());

		holder = new ViewHolder();
		holder.bg = (RelativeLayout) convertView
				.findViewById(R.id.recipeLayout);
		holder.rec1 = (LinearLayout) convertView
				.findViewById(R.id.linearLayout1);
		holder.title1 = (TextView) convertView.findViewById(R.id.recip1);
		holder.img1 = (ImageView) convertView.findViewById(R.id.img1);
		holder.rec2 = (LinearLayout) convertView
				.findViewById(R.id.linearLayout2);
		holder.title2 = (TextView) convertView.findViewById(R.id.recip2);
		holder.img2 = (ImageView) convertView.findViewById(R.id.img2);

		convertView.setTag(holder);
		convertView.setTag(R.id.recipeLayout, holder.bg);
		convertView.setTag(R.id.linearLayout1, holder.rec1);
		convertView.setTag(R.id.recip1, holder.title1);
		convertView.setTag(R.id.img1, holder.img1);
		convertView.setTag(R.id.linearLayout2, holder.rec2);
		convertView.setTag(R.id.recip2, holder.title2);
		convertView.setTag(R.id.img2, holder.img2);
		// holder.img.setImageResource();

		holder.img1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// If this is a saved recipe
				Toast.makeText(c.getApplicationContext(),
						"Pressed: " + two_recipes[0].getName(),
						Toast.LENGTH_LONG).show();
				MainActivity.currentRecipe = two_recipes[0];
				v.getContext()
						.startActivity(new Intent(c, OrderActivity.class));

				vi.vibrate(40);
			}
		});

		holder.img1.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				MainActivity.currentRecipe = two_recipes[0];
				v.getContext().startActivity(
						new Intent(c, RecipeActivity.class));

				vi.vibrate(40);
				return false;
			}
		});
		holder.img2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(c.getApplicationContext(),
						"Pressed: " + two_recipes[1].getName(),
						Toast.LENGTH_LONG).show();
				MainActivity.currentRecipe = two_recipes[1];
				v.getContext()
						.startActivity(new Intent(c, OrderActivity.class));

				vi.vibrate(40);
			}
		});
		holder.img2.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				MainActivity.currentRecipe = two_recipes[1];
				v.getContext().startActivity(
						new Intent(c, RecipeActivity.class));

				vi.vibrate(40);
				return false;
			}
		});
		if (two_recipes[0] != null) {
			holder.img1.setImageResource(icons[two_recipes[0]
					.getIconReference()]);
			holder.title1.setText(two_recipes[0].getName());
			if (two_recipes[1] != null) {
				holder.title2.setText(two_recipes[1].getName());
				holder.img2.setImageResource(icons[two_recipes[1]
						.getIconReference()]);
			} else {
				holder.rec2.setAlpha((float) 0);
				holder.title2.setText("");
				// holder.img2.setImageResource(R.drawable.about);
				holder.img2.setVisibility(View.INVISIBLE);
			}
		} // END if str[0] not null
			// if (str[0] == null && str[1] == null) {
			// holder.rec1.setAlpha((float) 0.5);
			// holder.title1.setText("New Recipe");
			// holder.img1.setImageResource(R.drawable.about);
			//
			// holder.rec2.setVisibility(View.INVISIBLE);
			// holder.title2.setVisibility(View.INVISIBLE);
			// holder.img2.setVisibility(View.INVISIBLE);
			//
			// }
		holder.bg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// DO nothing, just to remove the listview animation when the
				// user clicks outside the buttons
			}
		});
		return convertView;
	}

	static class ViewHolder {
		protected RelativeLayout bg;
		protected LinearLayout rec1;
		protected TextView title1;
		protected ImageView img1;
		protected LinearLayout rec2;
		protected TextView title2;
		protected ImageView img2;
	}
}