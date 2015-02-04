/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public final class MainActivity extends BaseActivity implements
		SampleFragmentSelectedListener {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		sectionsPagerAdapter = new SectionsPagerAdapter(
				Configuration.getSampleConfiguration(this),
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		viewPager = findTypedViewById(R.id.pager);
		viewPager.setAdapter(sectionsPagerAdapter);

		goToDefaultTab();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		final boolean hasDefaultTab = Preferences.hasDefaultTab(this);
		menu.findItem(R.id.action_defaulttab_go).setEnabled(hasDefaultTab);
		menu.findItem(R.id.action_defaulttab_clear).setEnabled(hasDefaultTab);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_defaulttab_set:
			setDefaultTab();
			return true;
		case R.id.action_defaulttab_go:
			goToDefaultTab();
			return true;
		case R.id.action_defaulttab_clear:
			clearDefaultTab();
			return true;
		case R.id.action_about:
			showAboutDialog();
			return true;
		}
		return false;
	}

	@Override
	public void onExampleFragmentSelected(final SampleEntry entry) {
		Toast.makeText(this, entry.toString(), Toast.LENGTH_SHORT).show();
		entry.start(this);
	}

	private void goToDefaultTab() {
		viewPager.setCurrentItem(Preferences.getDefaultTab(this), true);
	}

	private void setDefaultTab() {
		final int tab = viewPager.getCurrentItem();
		Preferences.setDefaultTab(this, tab);
		Toast.makeText(this, "Current tab set as default.", Toast.LENGTH_SHORT)
				.show();
	}

	private void clearDefaultTab() {
		Preferences.clearDefaultTab(this);
		Toast.makeText(this, "Default tab cleared.", Toast.LENGTH_SHORT).show();
	}

	private void showAboutDialog() {
		final Dialog dlg = new Dialog(this);
		dlg.setContentView(R.layout.dialog_about);
		dlg.setTitle(R.string.app_name);
		Button dlgButton = (Button) dlg.findViewById(R.id.about_button_ok);
		dlgButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
		ImageView imgView = (ImageView) dlg.findViewById(R.id.about_logo);
		imgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = getString(R.string.about_link);
				Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT)
						.show();
				launchBrowser(url);
			}
		});
		dlg.show();
	}

	private void launchBrowser(String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter sectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager viewPager;
}
