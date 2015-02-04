package nfc.simple.app2;

/*
 * 
 * NFC tag reader example
 * Tags must be "plain text" formatted
 * 
 * 2012 -Per Lynggaard
 * 
 */

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class NFC_simpleActivity extends Activity {

	WebView webView;
	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		setContentView(R.layout.main2);
		webView = (WebView) findViewById(R.id.webView1);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Log.i("WEB_VIEW_TEST", "error code:" + errorCode);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		});
		webView.getSettings().setJavaScriptEnabled(false);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
		webView.getSettings().setPluginsEnabled(false);
		webView.getSettings().setSupportMultipleWindows(false);
		webView.getSettings().setSupportZoom(false);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);

		resolveIntent(getIntent());
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
				mTechLists);
	}

	@Override
	public void onPause() {
		super.onPause();
		mAdapter.disableForegroundDispatch(this);
	}

	void resolveIntent(Intent intent) {
		// Parse the intent
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			// When a tag is discovered we send it to the service to be save. We
			// include a PendingIntent for the service to call back onto. This
			// will cause this activity to be restarted with onNewIntent(). At
			// that time we read it from the database and view it.
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] msgs;
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
			// Setup the web-view
			setUpWebView(msgs);

		}
	}

	void setUpWebView(NdefMessage[] msgs) {
		if (msgs == null || msgs.length == 0)
			return;
		byte[] msgsb = msgs[0].getRecords()[0].getPayload();
		// First 3 chars are card info and needs to be removed
		String urlToLoad = EncodingUtils.getAsciiString(msgsb).substring(3);
		if (!urlToLoad.matches("")) {
			if (!urlToLoad.contains("http://")) {
				urlToLoad = "http://" + urlToLoad;
			}
			webView.loadUrl(urlToLoad);
		}
		Toast.makeText(getBaseContext(), urlToLoad, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);
		resolveIntent(intent);
		Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
	}
}
