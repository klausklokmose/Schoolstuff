package com.example.prosa2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MainActivity extends Activity {

	private boolean on;
	private Animation anim;
	private Button b2;
	private Button b1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		b1 = (Button)findViewById(R.id.button1);
		
		b2 = (Button)findViewById(R.id.button2);
		anim = AnimationUtils.loadAnimation(b2.getContext(), R.anim.animation1);
		on = false;
		b1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(on){
					anim.cancel();
//					anim.setRepeatMode(1);
					b2.setText("Stopped");
					on = false;
				}else{
					b2.startAnimation(anim);
					b2.setText("Bouncing");
					on = true;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
