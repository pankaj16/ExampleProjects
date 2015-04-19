package com.pankaj.utils;

import android.content.Context;
import android.os.AsyncTask;

public class BackgroundNetwork extends AsyncTask<Void, String, Void> {

	Context context;

	public BackgroundNetwork(Context activity) {
		context = activity;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
	}

	public void onPostExecuteDeveloperMethodForPublicAccess(Void result) {
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		return null;
	}

}
