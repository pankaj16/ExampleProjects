package com.zipp;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		findViewById(R.id.zip).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String[] s = new String[2]; // declare an array for storing the
											// files i.e the path of your source
											// files

				s[0] = Environment.getExternalStorageDirectory()
						+ "/BT/crazy_answers.txt"; // Type the path of the files
													// in here
				s[1] = Environment.getExternalStorageDirectory()
						+ "/BT/pkg_1.0.apk"; // path of the second file

				Compress c = new Compress(s, Environment
						.getExternalStorageDirectory() + "/BT/new.zip"); // first
				c.zip(); // call the zip function

			}
		});
		
		findViewById(R.id.unzip).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String zipFile = Environment.getExternalStorageDirectory() + "/BT/new.zip"; 
				String unzipLocation = Environment.getExternalStorageDirectory() + "/BT/"; 
				 
				Decompress d = new Decompress(zipFile, unzipLocation); 
				d.unzip();
				
			}
		});
	}
}