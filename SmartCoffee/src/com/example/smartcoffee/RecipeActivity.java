package com.example.smartcoffee;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class RecipeActivity extends Activity {

	private static Recipe recipe;
	private ActionBar actionBar;
	private CheckBox strMild;
	private CheckBox strStrong;
	private CheckBox strMed;
	private CheckBox grBlonde;
	private CheckBox grMed;
	private CheckBox grDark;

	private CheckBox foam;
	private CheckBox decaf;

	private SeekBar sugar;
	private TextView sugarText;
	private TextView sweetText;
	private SeekBar sweet;
	private TextView creamText;
	private SeekBar cream;
	private ProgressBar progressBar;

	public static Recipe getRecipe() {
		return recipe;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);

		// Getting the Recipe object to populate this activity
		recipe = MainActivity.currentRecipe;
		if (recipe == null) {
			recipe = new Recipe(0, 0, false, "New Recipe", 1, 2, 2, false,
					false, 0, 3, 5, 3);
		}

		// setting up the Actionbar
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#9C7444")));
		// Setting the title corresponding to the recipe
		actionBar.setTitle(recipe.getName());
		// Add arrow to the left of icon
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		new SetUpViews().execute();
		
	
	}

	class SetUpViews extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... voids) {

			setUpStrengthOfCoffee();
			setUpSelectGrind();
			setUpSugarSweetCream();
			foam = (CheckBox) findViewById(R.id.foam);
			foam.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						recipe.setFoam(true);
					} else {
						recipe.setFoam(false);
					}
				}
			});
			decaf = (CheckBox) findViewById(R.id.decaf);
			decaf.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						recipe.setDecaf(true);
					} else {
						recipe.setDecaf(false);
					}
				}
			});
			setUpRecipeParams();
			return null;
		}//

		@Override
		protected void onPostExecute(Void v) {

			progressBar.setVisibility(View.GONE);
			Log.d("FINISH", "FINISH");
			
			sugarText.setText("Sugar (" + sugar.getProgress() + ")");
			sweetText.setText("Sweetener (" + sweet.getProgress() + ")");
			creamText.setText("Cream (" + cream.getProgress() + ")");
		}

	}

	private void setUpRecipeParams() {
		// Setting up the strength
		int strength = recipe.getStrength();
		if (strength == 1) {
			strMild.setChecked(true);
		} else if (strength == 2) {
			strMed.setChecked(true);
		} else {
			strStrong.setChecked(true);
		}
		// Setting up the grind
		int grind = recipe.getGrind();
		if (grind == 1) {
			grBlonde.setChecked(true);
		} else if (grind == 2) {
			grMed.setChecked(true);
		} else {
			grDark.setChecked(true);
		}
		// Setting up decaf
		if (recipe.isDecaf()) {
			decaf.setChecked(true);
		} else {
			decaf.setChecked(false);
		}
		// Setting up foam
		if (recipe.isFoam()) {
			foam.setChecked(true);
		} else {
			foam.setChecked(false);
		}
		// Setting up sugar slider
		sugar.setProgress(recipe.getSugar());
		// Setting up sweet slider
		sweet.setProgress(recipe.getSweetener());
		// Setting up cream slider
		cream.setProgress(recipe.getCream());
	}

	private void setUpStrengthOfCoffee() {
		strMild = (CheckBox) findViewById(R.id.strMild);
		strMild.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					recipe.setStrength(1);
					strMed.setChecked(false);
					strStrong.setChecked(false);
				}
			}
		});
		strMed = (CheckBox) findViewById(R.id.strMed);
		strMed.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					recipe.setStrength(2);
					strMild.setChecked(false);
					strStrong.setChecked(false);
				}
			}
		});
		strStrong = (CheckBox) findViewById(R.id.strStrong);
		strStrong.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					recipe.setStrength(3);
					strMild.setChecked(false);
					strMed.setChecked(false);
				}
			}
		});

	}

	private void setUpSelectGrind() {
		grBlonde = (CheckBox) findViewById(R.id.grBlon);
		grBlonde.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					recipe.setGrind(1);
					grMed.setChecked(false);
					grDark.setChecked(false);
				}
			}
		});
		grMed = (CheckBox) findViewById(R.id.grMed);
		grMed.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					recipe.setGrind(2);
					grBlonde.setChecked(false);
					grDark.setChecked(false);
				}
			}
		});
		grDark = (CheckBox) findViewById(R.id.grDar);
		grDark.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					recipe.setGrind(3);
					grBlonde.setChecked(false);
					grMed.setChecked(false);
				}
			}
		});
	}

	private void setUpSugarSweetCream() {
		sugarText = (TextView) findViewById(R.id.sugar);
		sugar = (SeekBar) findViewById(R.id.sugarBar);
		setSeekBarListener(sugar);
		sweetText = (TextView) findViewById(R.id.sweet);
		sweet = (SeekBar) findViewById(R.id.sweetBar);
		setSeekBarListener(sweet);
		creamText = (TextView) findViewById(R.id.cream);
		cream = (SeekBar) findViewById(R.id.creamBar);
		setSeekBarListener(cream);
	}

	private void setSeekBarListener(SeekBar seekBar) {
		// seekBar.setProgressDrawable(new
		// ColorDrawable(Color.parseColor("#9C7444")));
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (seekBar == sugar) {
					sugarText.setText("Sugar (" + sugar.getProgress() + ")");
				} else if (seekBar == sweet) {
					sweetText.setText("Sweetener (" + sweet.getProgress() + ")");
				} else {
					creamText.setText("Cream (" + cream.getProgress() + ")");
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_name, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_edit_name:
			// Toast.makeText(getBaseContext(), "EDIT NAME", Toast.LENGTH_SHORT)
			// .show();
			// 1. Instantiate an AlertDialog.Builder with its constructor
			LayoutInflater linfl = LayoutInflater.from(this);
			final View inflator = linfl.inflate(R.layout.edit_name, null);

			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			// alert.setTitle(R.string.edit_name);
			alert.setView(inflator);

			final EditText editText = (EditText) inflator
					.findViewById(R.id.name1);
			editText.setText(recipe.getName());

			alert.setPositiveButton("Save name",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User clicked OK button
							String s = editText.getText().toString();
							if (!s.isEmpty()) {
								recipe.setName(s);
								actionBar.setTitle(s);
							}
						}
					});
			alert.setNegativeButton("cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
						}
					});

			alert.show();
			// dialog.show();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
