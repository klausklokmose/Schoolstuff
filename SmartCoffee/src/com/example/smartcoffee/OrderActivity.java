package com.example.smartcoffee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderActivity extends Activity {

	private ImageView img;
	private Button cancelB;
	private TextView name;
	private final String WAIT = "Setting up connection";
	private final String SCAN = "Please scan a NFC tag at the coffee machine";
	private final String BEING_PROCESSED = "Order is being processed";
	private final String READY = "Done\nBe carefull when removing the cup";
	private TextView messages;
	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private NdefMessage[] msgs;
	private String HOST_ADDRESS = "10.0.2.2";
	private final String HOST_PORT = "1337";
	private float price = 0;
	private Recipe recipe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		// Get the recipe that should be ordered
		recipe = MainActivity.currentRecipe;
		if (recipe == null) {
			finish();
		}
		setUpView(recipe);
		SharedPreferences pref = getBaseContext().getSharedPreferences("IP",
				Activity.MODE_PRIVATE);
		// final Editor editor = pref.edit();
		HOST_ADDRESS = pref.getString("IP", "10.0.2.2");

		mAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndef.addDataType("*/*"); /*
									 * Handles all MIME based dispatches. You
									 * should specify only the ones that you
									 * need.
									 */
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		mFilters = new IntentFilter[] { ndef, };
		mTechLists = new String[][] { new String[] { NfcF.class.getName() } };
		resolveIntent(getIntent());

	}

	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);
		resolveIntent(intent);
		// Display content -Your code
		String t = "";
		byte[] b = msgs[0].getRecords()[0].getPayload();
		try {
			t = new String(b, "UTF-8").substring(3);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
		Log.i("Message", "Discovered tag: " + t);
		// TODO
		try {
			messages.setText(WAIT);
			setUpConnection("message=TAG");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class CoffeeMachineRequest extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			Log.d("Connection info", params[0]);
			HttpGet getRequest = new HttpGet("http://" + HOST_ADDRESS + ":"
					+ HOST_PORT + "/?" + params[0]);
			InputStream stream = null;
			try {
				HttpParams param = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(param, 5000);
				HttpConnectionParams.setSoTimeout(param, 5000);

				HttpResponse response = new DefaultHttpClient(param)
						.execute(getRequest);
				// int responceCode = response.getStatusLine().getStatusCode();

				stream = response.getEntity().getContent();

			} catch (ClientProtocolException e) {
				e.printStackTrace();
				showErrorToast("ClientProtocol Exception");
			} catch (IOException e) {
				e.printStackTrace();
				showErrorToast("IO Exception - check server\nIP: "
						+ HOST_ADDRESS);
			} finally {
				if (stream != null) {
					if (!stream.equals("")) {
						return consumeStream(stream);
					}
				}
			}
			return "";

		}//

		@Override
		protected void onPostExecute(String str) {
			if (str != null) {
				String[] str1 = str.split("#");
				Log.i("Response", str);
				if (str1[0].equalsIgnoreCase("setup ok")) {
					price = Float.parseFloat(str1[1]);
					if (price != -1) {
						Log.i("SETUP", "OK");
						setUpdatedText("Connection good");
						try {
							requestOrder(recipe);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						finish();
					}
				} else if (str.equalsIgnoreCase("Recipe ok")) {
					Log.i("ORDER", "OK");
					setUpdatedText("Recipe ok");
					try {
						verifyPayment();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (str.equalsIgnoreCase("Recipe fail")) {
					Log.i("ORDER", "FAIL");
					setUpdatedText("Recipe FAIL");
					finish();
				} else if (str.contains("token1")) {
					setUpdatedText("Payment ok");
					Log.i("TOKEN1", str.substring(5));
					// price = Float.parseFloat(str.substring(5));
					try {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								cancelB.setVisibility(View.INVISIBLE);
							}
						});
						orderCoffee();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (str.equalsIgnoreCase("Balance ok")) {
					setUpdatedText(BEING_PROCESSED);
					Log.i("Balance", "OK");
				} else if (str.equalsIgnoreCase("balance too low")) {
					setUpdatedText("BALANCE LOW!");
					finish();
				} else if (str.equalsIgnoreCase("executing order")) {
					setUpdatedText(READY);
					Log.i("Executing", "OK");
					finish();
				}
			} else {
				Log.i("ERROR", "NOT WORKING!");
				showErrorToast("Message received was not recognized");
			}
			Log.d("FINISH", "FINISH");
		}

		private String consumeStream(InputStream is) {
			try {
				BufferedReader read = new BufferedReader(new InputStreamReader(
						is, "UTF-8"));
				StringBuilder responseBuilder = new StringBuilder();
				String line;
				while ((line = read.readLine()) != null) {
					responseBuilder.append(line);
				}
				return responseBuilder.toString();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;

		}
	}

	void resolveIntent(Intent intent) {
		// Parse the intent
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
		}
	}

	public void showErrorToast(final String errorMessage) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getBaseContext(), errorMessage,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	public void setUpConnection(String connectionInfo) throws Exception {
		// Setup connection
		new CoffeeMachineRequest().execute(connectionInfo);
	}

	public void orderCoffee() throws Exception {
		// order coffee command
		new CoffeeMachineRequest().execute("message=orderCoffee&recipe="
				+ recipe.toString() + "&token1=123&token2=456");
	}

	public void verifyPayment() throws Exception {
		// verify payment command
		new CoffeeMachineRequest().execute("message=payment&price=" + price);
	}

	public void requestOrder(Recipe recipe2) throws Exception {
		// request order command
		new CoffeeMachineRequest().execute("message=recipe&recipe="
				+ recipe.toString());
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
					mTechLists);
			// TODO only for testing
			messages.setText(WAIT);
			setUpConnection("message=TAG");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
		setUpConnection("message=TAG");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if ((keyCode == KeyEvent.KEYCODE_MENU)) { // Back key pressed
//			Log.d("MENU", "Menu pressed");
//			try {
//				setUpConnection("message=TAG");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return super.onKeyDown(keyCode, event);
//	}

	private void setUpView(Recipe recipe) {
		getActionBar().hide();

		// Show the icon from the recipe
		img = (ImageView) findViewById(R.id.loadingImg);
		img.setImageResource(RecipeAdapter.icons[recipe.getIconReference()]);

		// Show the recipe name
		name = (TextView) findViewById(R.id.recipe_name);
		name.setText(recipe.getName());

		// Show the current process to the user
		messages = (TextView) findViewById(R.id.orderMessages);
		messages.setText(SCAN);

		// Allow the user to cancel
		cancelB = (Button) findViewById(R.id.cancelButton);
		cancelB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Cancel the order (rollback)

				// Then go back
				onBackPressed();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.order, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		// do something on back.
		// Show dialog to confirm cancel order
		AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(this);
		// Setting Dialog Title
		alertDialog2.setTitle("Confirm cancel...");
		// Setting Dialog Message
		alertDialog2.setMessage("Are you sure you want to cancel?");
		// Setting Icon to Dialog
		alertDialog2.setIcon(android.R.drawable.ic_media_pause);
		// Setting Positive "Yes" Btn
		alertDialog2.setPositiveButton("Yes, cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to execute after dialog
						Toast.makeText(getApplicationContext(),
								"You clicked on YES", Toast.LENGTH_SHORT)
								.show();
						finish();
					}
				});
		// Setting Negative "NO" Btn
		alertDialog2.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to execute after dialog
						Toast.makeText(getApplicationContext(),
								"You clicked on NO", Toast.LENGTH_SHORT).show();
						dialog.cancel();
					}
				});

		// Showing Alert Dialog
		alertDialog2.show();
	}

	private void setUpdatedText(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				messages.setText(text);
			}
		});
	}

}
