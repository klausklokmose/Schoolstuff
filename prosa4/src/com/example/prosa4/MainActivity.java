package com.example.prosa4;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	String url;
	private TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		url = "http://www.jayway.com/feed/";
		text = (TextView) findViewById(R.id.text);
		new GetRSSfeed().execute(url);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onPause() {
	    super.onPause();  // Always call the superclass method first
	    
	}
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    
	}

	class GetRSSfeed extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO download things
			Log.d("URL", params[0]);
			HttpGet getRequest = new HttpGet(params[0]);
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
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				return consumeStream(stream);
			}

		}//

		@Override
		protected void onPostExecute(String str) {
			if (str != null) {
				text.setText(str);
			} else {
				text.setText("NOT WORKING");
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

}
