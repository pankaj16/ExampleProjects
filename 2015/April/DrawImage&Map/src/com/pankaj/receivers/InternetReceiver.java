package com.pankaj.receivers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.cloudinary.Cloudinary;
import com.pankaj.utils.BackgroundNetwork;
import com.pankaj.utils.ConnectionDetector;
import com.pankaj.utils.Constants;
import com.pankaj.utils.DevicePreferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.widget.Toast;

public class InternetReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		boolean isConnected = new ConnectionDetector(context)
				.isConnectedToInternet();
		if (isConnected) {
			String path = new DevicePreferences()
					.getString(context, "file", "");
			if (!path.equalsIgnoreCase("")) {
				File file = new File(path);
				try {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.ARGB_8888;
					Bitmap bitmap = BitmapFactory.decodeFile(file.toString(),
							options);
					uploadImage(bitmap,context);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void uploadImage(final Bitmap bitmap, final Context context) {
		new BackgroundNetwork(context) {
			Cloudinary cloudinary;
			String data = "";

			protected Void doInBackground(Void... params) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.PNG, 100, bos);
				byte[] bitmapdata = bos.toByteArray();
				ByteArrayInputStream inputStream = new ByteArrayInputStream(
						bitmapdata);

				Map<String, String> config = new HashMap<String, String>();
				config.put("cloud_name", Constants.CLOUDINARY_NAME);
				config.put("api_key", Constants.CLOUDINARY_API_KEY);
				config.put("api_secret", Constants.CLOUDINARY_API_SECRET);

				try {
					cloudinary = new Cloudinary(config);
					data = cloudinary.uploader()
							.upload(inputStream, Cloudinary.emptyMap())
							.toString();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;

			};

			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				try {
					if (!data.equals("") || data != null) {
						JSONObject objJsonObject = new JSONObject(data);
						//making file preference value to empty coz its uploaded
						new DevicePreferences().addKey(context, "file", "");
						Toast.makeText(context, "Image Uploaded", Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			};

		}.execute();

	}
}