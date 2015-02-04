package com.example.smartcoffee;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuAdapter extends ArrayAdapter<String> {

	private LayoutInflater inflator;
	private static ArrayList<String> menuList;
	private Vibrator vi;
	private Context c;
	private Activity activ;
	private ArrayList<Integer> icons;
	private User user;

	public MenuAdapter(Activity activity, int textViewResourceId, List<String> list, User user) {
		super(activity, textViewResourceId, list);
		this.activ = activity;
		this.user = user;
		
		icons = new ArrayList<Integer>();
		icons.add(R.drawable.user_ic);
		icons.add(R.drawable.search_ic);
		icons.add(R.drawable.friends_ic);
//		icons.add(R.drawable.ic_launcher);
		icons.add(R.drawable.question_ic);
		icons.add(R.drawable.settings_ic);
		
		menuList = new ArrayList<String>();
		menuList.addAll(list);

		c = activity.getBaseContext();
		vi = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
//		if (convertView == null) {
			inflator = activ.getLayoutInflater();
			convertView = inflator.inflate(R.layout.row, parent, false);

			final String s = menuList.get(position);

			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.smallText = (TextView) convertView.findViewById(R.id.smallText);
			
			convertView.setTag(holder);
			convertView.setTag(R.id.title, holder.title);
			convertView.setTag(R.id.img, holder.img);
			convertView.setTag(R.id.smallText, holder.smallText);
			
			holder.img.setImageResource(icons.get(position));
			//If this is the balance view
//			if(position==3){
//				Log.d("SET", "Invisible");
//				holder.img.setVisibility(View.INVISIBLE);
//			}
//			
			
			if(position != 0){
				holder.smallText.setVisibility(View.INVISIBLE);
			}else{
				holder.smallText.setText(user.getEmailAddress());
				
				DisplayMetrics dm = new DisplayMetrics();
				activ.getWindowManager().getDefaultDisplay().getMetrics(dm);
				int pixelSize = (int) ((int) 6 * dm.scaledDensity); 
				holder.title.setTextSize(pixelSize);
			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(c.getApplicationContext(), "Pressed: " + s, Toast.LENGTH_LONG).show();
					vi.vibrate(40);
					if(s.equals("Preferences")){
						SharedPreferences pref = getContext().getSharedPreferences("IP", Activity.MODE_PRIVATE);
						final Editor editor = pref.edit();
						
						LayoutInflater linfl = LayoutInflater.from(getContext());
						final View inflator = linfl.inflate(R.layout.edit_name, null);

						AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//						alert.setTitle(R.string.edit_name);
						alert.setView(inflator);
						
						final EditText editText = (EditText)inflator.findViewById(R.id.name1);
						editText.setText(pref.getString("IP", "10.0.2.2"));
						
								alert.setPositiveButton("Save name",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int id) {
												// User clicked OK button
												String s = editText.getText().toString();
												if(!s.isEmpty()){
													editor.putString("IP", s);
													editor.commit();
												}
											}
										});
								alert.setNegativeButton("cancel",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int id) {
												// User cancelled the dialog
											}
										});

						alert.show();

					}
				}
			});
			
			
			// Set the text in the row
			holder.title.setText(s);
		return convertView;
	}

	static class ViewHolder {
		protected TextView title;
		protected ImageView img;
		protected TextView smallText;
	}
}