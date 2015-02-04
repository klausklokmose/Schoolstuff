package com.example.framework;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private ImageView image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avatar_example);
		 image = (ImageView) findViewById(R.id.imageView1);
		 setRoundedImage(image, R.drawable.ic_contact_picture);
	}

	public void setRoundedImage(ImageView image, int imageResource) {
		BitmapFactory.Options myOptions = new BitmapFactory.Options();
		myOptions.inDither = true;
		myOptions.inScaled = false;
		myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important
		myOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				imageResource, myOptions);
		Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);

		BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,
				TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShader(shader);
		Canvas c = new Canvas(circleBitmap);
		c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
				bitmap.getHeight() / 2, paint);
		image.setImageBitmap(circleBitmap);
		
	}
}
