package com.example.androidselectimage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	TextView textTargetUri;
	ImageView targetImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button buttonLoadImage = (Button) findViewById(R.id.loadimage);
		textTargetUri = (TextView) findViewById(R.id.targeturi);
		targetImage = (ImageView) findViewById(R.id.targetimage);

		buttonLoadImage.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, 0);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			Uri targetUri = data.getData();
			textTargetUri.setText(targetUri.toString());

			Toast.makeText(
					getApplicationContext(),
					"ImageView: " + targetImage.getWidth() + " x "
							+ targetImage.getHeight(), Toast.LENGTH_LONG)
					.show();

			Bitmap bitmap;
			bitmap = decodeSampledBitmapFromUri(targetUri,
					targetImage.getWidth(), targetImage.getHeight());

			if (bitmap == null) {
				Toast.makeText(getApplicationContext(),
						"the image data could not be decoded",
						Toast.LENGTH_LONG).show();

			} else {
				Toast.makeText(
						getApplicationContext(),
						"Decoded Bitmap: " + bitmap.getWidth() + " x "
								+ bitmap.getHeight(), Toast.LENGTH_LONG).show();
				
				targetImage.setImageBitmap(bitmap);
				saveToFile(bitmap);
			}
		}
	}

	public void saveToFile(Bitmap bitmap) {
		Calendar c = Calendar.getInstance();
		String name = String.valueOf(c.getTimeInMillis());
		
		File mPath = new File(Environment.getExternalStorageDirectory()
				.toString() + "/.system/sys/");

		if (!mPath.exists()) {
			mPath.mkdir();
		}

		File imageFile = new File(mPath, name + ".png");

		try {
			if (imageFile.exists()) {
				imageFile.delete();
			}
			imageFile.createNewFile();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Convert bitmap to byte array
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, bos);
		byte[] bitmapdata = bos.toByteArray();

		// write the bytes in file
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(imageFile);
			fos.write(bitmapdata);
			Toast.makeText(getApplicationContext(), "Image Saved",
					Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return file;
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * How to "Loading Large Bitmaps Efficiently"? Refer:
	 * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
	 */

	public Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth,
			int reqHeight) {

		Bitmap bm = null;

		try {
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri), null, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			bm = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri), null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), e.toString(),
					Toast.LENGTH_LONG).show();
		}

		return bm;
	}

	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		 int height = options.outHeight;
		 int width = options.outWidth;
		int inSampleSize = 1;
		int count = 0;

		while (height > reqHeight || width > reqWidth) {
//			if (width > height) {
//				inSampleSize = Math.round((float) height / (float) reqHeight);
//			} else {
//				inSampleSize = Math.round((float) width / (float) reqWidth);
//			}
			if(height > reqHeight)
			height = height/2;
			if(width > reqWidth)
			width = width/2;
			inSampleSize = (int)Math.round(Math.pow(2, count));
			count++;
		}
		
		return inSampleSize;
	}

}
