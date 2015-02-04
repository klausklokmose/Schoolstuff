package com.example.asynctemplate;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
	
	private static final String URL = "http://www.jayway.com/wordpress/wp-content/themes/dev/images/jayway-logo.png";
	private ImageView imageView;
	private ProgressBar pb;
	private ProgressBar pb2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageView = (ImageView) findViewById(R.id.imageView);
		pb = (ProgressBar)findViewById(R.id.progressBar1);
		pb.setVisibility(View.INVISIBLE);
		pb2 = (ProgressBar)findViewById(R.id.progressBar2);
		
	}

	public void showImage(View view){
		new ImageDownloaderTask(imageView).execute(URL);
	}

	class ImageDownloaderTask extends AsyncTask<String, Integer, Bitmap>{
		ImageView imageView;
		public ImageDownloaderTask(ImageView imageView) {
			this.imageView = imageView;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			
			return Downloader.loadBitmap(params[0]);
		}
		@Override
		protected void onProgressUpdate(Integer...progress){
			pb2.setProgress(progress[0]);
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(result);
			pb.setVisibility(View.INVISIBLE);
			
		}
		
		@Override
		protected void onPreExecute(){
			pb.setVisibility(View.VISIBLE);
		}
	}
}
