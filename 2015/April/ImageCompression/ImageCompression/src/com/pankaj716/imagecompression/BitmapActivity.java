package com.pankaj716.imagecompression;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BitmapActivity extends Activity implements OnClickListener {

	private Button btnFromGallery;
	private ImageView imageView;
	private TextView pathTextView;

	private ImageCompressingUtils utils = new ImageCompressingUtils(this);

	private LruCache<String, Bitmap> memoryCache;

	public final static int REQUEST_CODE_FROM_GALLERY = 1;
	public final static int REQUEST_CODE_CLICK_IMAGE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bitmap);

		int cachesize = 60 * 1024 * 1024;

		memoryCache = new LruCache<String, Bitmap>(cachesize) {
			@SuppressLint("NewApi")
			@Override
			protected int sizeOf(String key, Bitmap value) {
				if (android.os.Build.VERSION.SDK_INT >= 12) {
					return value.getByteCount();
				} else {
					return value.getRowBytes() * value.getHeight();
				}
			}
		};

		initViews();
	}

	private void initViews() {
		btnFromGallery = (Button) findViewById(R.id.btnFromGallery);

		btnFromGallery.setOnClickListener(this);

		imageView = (ImageView) findViewById(R.id.image);
		pathTextView = (TextView) findViewById(R.id.path);
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			memoryCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return memoryCache.get(key);
	}

	@Override
	public void onClick(View v) {
		new ImagePicker(this, BitmapActivity.this).createLayout();
	}

	@Override
	protected void onDestroy() {
		memoryCache.evictAll();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_FROM_GALLERY:
				new ImageCompressionAsyncTask(true).execute(data
						.getDataString());

				break;
			case REQUEST_CODE_CLICK_IMAGE:
				String imagePathString = new DevicePreferences().getString(
						BitmapActivity.this, ImagePicker.IMAGE_URI, "");
				Uri imageUri = Uri.parse(imagePathString);
				String imageId = new ImagePicker(this, BitmapActivity.this)
						.convertImageUriToFile(imageUri, BitmapActivity.this);
				String path = ImagePath.getPath(BitmapActivity.this, imageUri);
				new ImageCompressionAsyncTask(false).execute(path);
				break;
			}
		}
	}

	private class ImageCompressionAsyncTask extends
			AsyncTask<String, Void, String> {
		private boolean fromGallery;
		private ProgressDialog dialog;

		public ImageCompressionAsyncTask(boolean fromGallery) {
			this.fromGallery = fromGallery;
		}

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(BitmapActivity.this);
			dialog.setMessage("Please Wait");
			dialog.show();
			dialog.setCancelable(false);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			String filePath = utils.getRealPathFromURI(params[0]);
			return utils.decodedImageFilePath(filePath);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(dialog.isShowing()){
				dialog.hide();
			}
			
			if (result != null) {
				pathTextView.setText(result);
				File file = new File(result);
				Bitmap bitmap = BitmapFactory
						.decodeFile(file.getAbsolutePath());
				imageView.setImageBitmap(bitmap);
				Toast.makeText(BitmapActivity.this, "Image Saved: " + result,
						Toast.LENGTH_LONG).show();
			} else {
				pathTextView.setText("Some error occur");
				imageView.setImageResource(R.drawable.ic_launcher);
			}
		}

	}

}
