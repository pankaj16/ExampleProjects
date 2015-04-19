package com.example.androidcallback;

import android.os.AsyncTask;
import android.os.SystemClock;

public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

	interface DoSomething {
		void doInBackground(int progress);
		void doPostExecute();
	}

	DoSomething myDoSomethingCallBack;
	int myMax;
	
	MyAsyncTask(DoSomething callback, int max){
		myDoSomethingCallBack = callback;
		myMax = max;
	}

	@Override
	protected Void doInBackground(Void... params) {
		for (int i = 0; i <= myMax; i++) {
			SystemClock.sleep(100);
			myDoSomethingCallBack.doInBackground(i);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		myDoSomethingCallBack.doPostExecute();
	}

}
