package com.pankaj716.imagecompression;
import java.io.File;
import java.util.Calendar;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class ImagePicker {

	Activity activity;
	Context context;
	Button cameraButton;
	Button galleryButton;

	public final static String IMAGE_URI = "image_uri";
	public final static String IMAGE_PATH_NAME = "image_path_name";
	
	boolean isButtonClicked = false; // if any of the button clicked then on
										// dismiss method callback will be fired

	public ImagePicker(Activity activity, Context context) {
		this.activity = activity;
		this.context = context;
	}

	public void createLayout() {
		Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.image_picker_layout);
		dialog.setCancelable(true);

		dialog.show();
		findViews(dialog);
		listners(dialog);
	}

	private void findViews(Dialog dialog) {
		cameraButton = (Button) dialog
				.findViewById(R.id.button_imagePicker_camera);
		galleryButton = (Button) dialog
				.findViewById(R.id.button_imagePicker_gallery);
	}

	private void listners(final Dialog dialog) {
		cameraButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isButtonClicked = true;
				Calendar calendar = Calendar.getInstance();
				String fileName = String.valueOf(calendar.getTimeInMillis());
				fileName += ".jpg";

				ContentValues values = new ContentValues();

				values.put(MediaStore.Images.Media.TITLE, fileName);

				values.put(MediaStore.Images.Media.DESCRIPTION,
						"Image capture by camera");

				Uri imageUri = activity.getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

				new DevicePreferences().addKey(context, IMAGE_URI,
						imageUri.toString());

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

				activity.startActivityForResult(intent,
						BitmapActivity.REQUEST_CODE_CLICK_IMAGE);
				dialog.dismiss();
			}
		});

		galleryButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isButtonClicked = true;
				try {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					activity.startActivityForResult(
							Intent.createChooser(intent, "Select Picture"), BitmapActivity.REQUEST_CODE_FROM_GALLERY);

				} catch (Exception e) {
					Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
							.show();
					Log.e(e.getClass().getName(), e.getMessage(), e);
				}
				dialog.dismiss();
			}
		});

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (!isButtonClicked) {
					dialogOnDismiss();
				}
			}
		});
	}

	//for callback
	public void dialogOnDismiss() {

	}

	public String convertImageUriToFile(Uri imageUri, Activity activity) {
		Cursor cursor = null;
		int imageID = 0;

		try {
			/*********** Which columns values want to get *******/
			String[] proj = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID,
					MediaStore.Images.Thumbnails._ID,
					MediaStore.Images.ImageColumns.ORIENTATION };

			cursor = context.getContentResolver().query(

			imageUri, // Get data for specific image URI
					proj, // Which columns to return
					null, // WHERE clause; which rows to return (all rows)
					null, // WHERE clause selection arguments (none)
					null // Order-by clause (ascending by name)

					);

			int columnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
			int columnIndexThumb = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
			int file_ColumnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			int orientation_ColumnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);

			int size = cursor.getCount();

			/******* If size is 0, there are no images on the SD Card. *****/

			if (size == 0) {
				Toast.makeText(activity, "No Image found.", Toast.LENGTH_LONG)
						.show();
			} else {

				int thumbID = 0;
				if (cursor.moveToFirst()) {

					/**************** Captured image details ************/

					imageID = cursor.getInt(columnIndex);

					// String orientation =
					// cursor.getString(orientation_ColumnIndex);
					// thumbID = cursor.getInt(columnIndexThumb);

					String Path = cursor.getString(file_ColumnIndex);
					String name = Path.substring(Path.indexOf("/DCIM") + 0,
							Path.length());
					String fullPath = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + name;
					new DevicePreferences().addKey(context,
							IMAGE_PATH_NAME, fullPath);
					// try{
					// int rotate = getRoatation(fullPath);
					// new DevicePreferences().addKey(activity,
					// Constants.ORIENTATION, rotate);
					// }catch(Exception e){
					// e.printStackTrace();
					// }

				}
			}
		} finally {
			if (cursor != null) {
				cursor = null;
			}
		}

		return "" + imageID;
	}

	public int getRoatation(String imagePath) {
		try {
			File imageFile = new File(imagePath);
			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				return 270;
			case ExifInterface.ORIENTATION_ROTATE_180:
				return 180;
			case ExifInterface.ORIENTATION_ROTATE_90:
				return 90;
			}
			Log.v("ORIENTATION", "Exif orientation: " + orientation);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;

	}

	public boolean getExternalStorageState() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}
}
