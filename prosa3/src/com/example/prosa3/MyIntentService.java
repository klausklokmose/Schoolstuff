package com.example.prosa3;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

public class MyIntentService extends IntentService {

	private Handler handler;
	public final static String MESSAGE = "MSG";

	public MyIntentService() {
		super("MyIntentService"); // Display name
		// TODO Auto-generated constructor stub
		handler = new Handler();

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO
		Bundle bundle = intent.getExtras();
		final String str = (String) bundle.get(MESSAGE);

		if (str != null) {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this).setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("My service message")
					.setContentText(str);
			NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			// Builds the notification and issues it.
			mNotifyMgr.notify(1, mBuilder.build());
		}
		// if(str != null){
		// handler.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// Toast.makeText(getApplicationContext(), str,
		// Toast.LENGTH_SHORT).show();
		// }
		// });
		// }
	}

}
