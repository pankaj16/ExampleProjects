package com.pankaj716.imagecompression;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;

public class ImageCompressingUtils {
	private Context context;
	
	float height = 816.0f;
	float width = 612.0f;
	
	public ImageCompressingUtils(Context context){
		this.context = context;
	}
	
	public int convertDipToPixels(float dips){
		 Resources r = context.getResources();
		 return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, r.getDisplayMetrics());
	}
	
	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    final float totalPixels = width * height;
	    final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

	    return inSampleSize;
	}
	
	public Bitmap decodeBitmapFromPath(String filePath){
		Bitmap scaledBitmap = null;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;						
		scaledBitmap = BitmapFactory.decodeFile(filePath,options);
		
		options.inSampleSize = calculateInSampleSize(options, convertDipToPixels(150), convertDipToPixels(200));
		options.inDither = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inJustDecodeBounds = false;
		
		scaledBitmap = BitmapFactory.decodeFile(filePath, options);		
		return scaledBitmap;
	}
	
	
	
	protected String getRealPathFromURI(String contentURI) {
		Uri contentUri = Uri.parse(contentURI);
		Cursor cursor = context.getContentResolver().query(contentUri, null, null,
				null, null);
		if (cursor == null) {
			return contentUri.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			return cursor.getString(idx);
		}
	}

	protected String decodedImageFilePath(String filePath) {
		long length = 0;
		try {
			File file = new File(filePath);
			length = file.length();
			length = length / 1024; // gives length in kb
		} catch (Exception e) {
			System.out.println("File not found : " + e.getMessage() + e);
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		
		/***if image length > 250 kb then decode else save/send same imageBitmap***/
		if (length > 250) {
			Bitmap scaledBitmap = decodeImage(filePath,options);
			// call Method to rotate image if exif orientation is not normal
			scaledBitmap = checkRotation(filePath, scaledBitmap);

			return saveImageFile(scaledBitmap);
		}else{
			options.inJustDecodeBounds = false;
			options.inSampleSize = 1;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			return saveImageFile(BitmapFactory.decodeFile(filePath, options));
		}
	}

	private Bitmap decodeImage(String filePath, BitmapFactory.Options options) {
		Bitmap scaledBitmap = null;

		Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
		int actualHeight = options.outHeight;
		int actualWidth = options.outWidth;
		float maxHeight = height;
		float maxWidth = width;
		float imgRatio = actualWidth / actualHeight;
		float maxRatio = maxWidth / maxHeight;

		if (actualHeight > maxHeight || actualWidth > maxWidth) {
			if (imgRatio < maxRatio) {
				imgRatio = maxHeight / actualHeight;
				actualWidth = (int) (imgRatio * actualWidth);
				actualHeight = (int) maxHeight;
			} else if (imgRatio > maxRatio) {
				imgRatio = maxWidth / actualWidth;
				actualHeight = (int) (imgRatio * actualHeight);
				actualWidth = (int) maxWidth;
			} else {
				actualHeight = (int) maxHeight;
				actualWidth = (int) maxWidth;

			}
		}

		options.inSampleSize = calculateInSampleSize(options,
				actualWidth, actualHeight);
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[16 * 1024];

		try {
			bmp = BitmapFactory.decodeFile(filePath, options);
		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();

		}
		try {
			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,
					Bitmap.Config.ARGB_8888);
		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();
		}

		float ratioX = actualWidth / (float) options.outWidth;
		float ratioY = actualHeight / (float) options.outHeight;
		float middleX = actualWidth / 2.0f;
		float middleY = actualHeight / 2.0f;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
				middleY - bmp.getHeight() / 2, new Paint(
						Paint.FILTER_BITMAP_FLAG));

		return scaledBitmap;
	}

	private Bitmap checkRotation(String filePath, Bitmap scaledBitmap) {
		ExifInterface exif;
		try {
			exif = new ExifInterface(filePath);

			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, 0);
			Log.d("EXIF", "Exif: " + orientation);
			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 3) {
				matrix.postRotate(180);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 8) {
				matrix.postRotate(270);
				Log.d("EXIF", "Exif: " + orientation);
			}
			scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
					scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
					true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scaledBitmap;
	}

	private String getFilename() {
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath(), "ImageCompression");
		if (!file.exists()) {
			file.mkdirs();
		}
		String uriSting = (file.getAbsolutePath() + "/"
				+ System.currentTimeMillis() + ".jpg");
		return uriSting;

	}

	private String saveImageFile(Bitmap scaledBitmap) {
		FileOutputStream out = null;
		String filename = getFilename();
		try {
			out = new FileOutputStream(filename);
			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return filename;
	}

}
