package dk.example.klaus;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String[] fruits = {"Orange", "Apple", "Lemon", "KittyKat", "Orange", "Apple", "Lemon", "KittyKat", "Orange", "Apple", "Lemon", "KittyKat", "Orange", "Apple", "Lemon", "KittyKat", "Orange", "Apple", "Lemon", "KittyKat", "Orange", "Apple", "Lemon", "KittyKat", "Orange", "Apple", "Lemon", "KittyKat", "Orange", "Apple", "Lemon", "KittyKat", "Orange", "Apple", "Lemon", "KittyKat"};
		
		ListView listV = (ListView)findViewById(R.id.listView1);
//		ArrayAdapter adap = new ArrayAdapter(this, android.R.layout.simple_list_item_1, fruits);
		MyAdapter adap = new MyAdapter(getApplicationContext(), fruits);
		listV.setAdapter(adap);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
