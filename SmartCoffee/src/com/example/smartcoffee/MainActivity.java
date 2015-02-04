package com.example.smartcoffee;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {

	private String[] mPlanetTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	private ActionBarDrawerToggle mDrawerToggle;

	private List<String> menuPoints;
	private ArrayList<Recipe> strs;
	private ListView listV;
	private ActionBar actionBar;
	private ProgressBar loadingBar;
	public static ArrayList<Recipe[]> finalList;
	public static Recipe currentRecipe;
	
	private User user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		user = new User(true, 1, "Klaus Nielsen", "kkni11@student.aau.dk");
		
		// Actionbar
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#9C7444")));

		// Add arrow to the left of icon
		// actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
		createDrawer();
		setupView();

		// Starting the AsyncTask for setting up the list etc.
		new SetUpView().execute();
	}

	class SetUpView extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			loadingBar.setVisibility(View.GONE);
			Log.d("FINISH", "FINISH");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_new_recipe:
			// TODO
			currentRecipe = new Recipe(0, 0, false, "New recipe", 1, 2, 2,
					false, false, 0, 0, 0, 3);
			Log.d("ACTION", "New recipe button pressed");
			// Start RecipeActivity with no content
			Intent intent = new Intent(getBaseContext(), RecipeActivity.class);
			startActivity(intent);
			return true;
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			} else {
				mDrawerLayout.openDrawer(Gravity.LEFT);
			}
			Log.d("HOME", "pressed");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setupView() {

		strs = user.getRecipes();

		listV = (ListView) findViewById(R.id.listView1);

		finalList = new ArrayList<Recipe[]>();

		int k = 0;
		Recipe recipe1 = null;
		Recipe recipe2 = null;

		for (int i = 0; i < strs.size(); i++) {
			if (k == 0) {
				recipe1 = strs.get(i);
				Log.d("STRING", recipe1.getName());
				k = 1;
			} else {
				recipe2 = strs.get(i);
				Log.d("STRING", recipe2.getName());
				finalList.add(new Recipe[] { recipe1, recipe2 });
				k = 0;
			}
		}// END of for loop

		// if the number of recipes are odd
		if (strs.size() % 2 == 1) {
			finalList.add(new Recipe[] { strs.get(strs.size() - 1), null });
		}

		// empty list because ArrayAdapter takes an input only List<String>
		ArrayList<String> emptyList = new ArrayList<String>();
		for (int i = 0; i < finalList.size(); i++) {
			emptyList.add("" + i);
		}

		RecipeAdapter recipeAdapter = new RecipeAdapter(this,
				R.layout.recipe_item, emptyList);

		listV.setAdapter(recipeAdapter);

	}

	private void createDrawer() {
		menuPoints = new ArrayList<String>();
		menuPoints.add(user.getUserName());
		menuPoints.add("Discover");
		menuPoints.add("Friends");
//		menuPoints.add("Balance");
		menuPoints.add("About");
		menuPoints.add("Preferences");

		mPlanetTitles = getResources().getStringArray(R.array.planets_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
		// R.layout.common_row,
		// mPlanetTitles);
		MenuAdapter aa = new MenuAdapter(this, R.layout.row, menuPoints, user);
		// Set the adapter for the list view
		// mDrawerList.setAdapter(aa);
		// mDrawerList.setAdapter(new ArrayAdapter<String>(this,
		// R.layout.drawer_list_item, mPlanetTitles));
		mDrawerList.setAdapter(aa);
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_launcher, R.string.app_name, R.string.hello_world) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				// getActionBar().setTitle(mDrawerTitle);
				actionBar.setDisplayHomeAsUpEnabled(false);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				// getActionBar().setTitle("Menu");
				actionBar.setDisplayHomeAsUpEnabled(true);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_MENU)) { // Back key pressed
			Log.d("MENU", "Menu pressed");
			if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			} else {
				mDrawerLayout.openDrawer(Gravity.LEFT);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Toast.makeText(getBaseContext(),
					"Pressed: " + mPlanetTitles[position], Toast.LENGTH_LONG)
					.show();
		}

	}

	public static Recipe getCurrentRecipe() {
		return currentRecipe;
	}

	public static void setCurrentRecipe(Recipe currentRecipe) {
		MainActivity.currentRecipe = currentRecipe;
	}

}
