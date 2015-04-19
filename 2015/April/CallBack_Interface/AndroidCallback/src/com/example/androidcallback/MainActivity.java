package com.example.androidcallback;

import com.example.androidcallback.MyAsyncTask.DoSomething;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.app.Activity;

public class MainActivity extends Activity implements DoSomething{
	
	ProgressBar myProgressBar;
	MyAsyncTask myAsyncTask;
	int myProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		myProgressBar = (ProgressBar)findViewById(R.id.myprogressbar);
		
		myProgress = 0;
		myAsyncTask = new MyAsyncTask(this, 100);
		myAsyncTask.execute();
	}

	@Override
	public void doInBackground(int i) {
		myProgressBar.setProgress(i);
	}

	@Override
	public void doPostExecute() {
		Toast.makeText(MainActivity.this, 
				"Finish", Toast.LENGTH_LONG).show();
	}

}
