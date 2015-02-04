package barcode.simple.app2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/* Remember to install BarcodeScanner4.0.apk on phone (use. adb install BarcodeScanner4.0.apk) available from:
 * http://code.google.com/p/zxing/downloads/detail?name=BarcodeScanner4.0.apk&can=2&q= 
 */

public class Barcode_simpleActivity extends Activity {

	private TextView outContent;
	private TextView outFormat;
	private String barContent;
	private String barFormat;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button scanBut = (Button) findViewById(R.id.button1);
		outContent = (TextView) findViewById(R.id.outContent);
		outFormat = (TextView) findViewById(R.id.textView1);

		scanBut.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(
						"com.google.zxing.client.android.SCAN");
				intent.putExtra(
						"com.google.zxing.client.android.SCAN.SCAN_MODE",
						"QR_CODE_MODE");
				startActivityForResult(intent, 0);
			}
		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				barContent = intent.getStringExtra("SCAN_RESULT");
				barFormat = intent.getStringExtra("SCAN_RESULT_FORMAT");
				// Print out bar content and barformat
				outContent.setText("Content of TAG: " + barContent);
				outFormat.setText("Type of TAG: " + barFormat);

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}
}