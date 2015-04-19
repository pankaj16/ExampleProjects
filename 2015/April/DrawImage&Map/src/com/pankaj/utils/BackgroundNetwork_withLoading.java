package com.pankaj.utils;


import com.pankaj.loginexttest.R;

import android.content.Context;
import android.os.AsyncTask;

public class BackgroundNetwork_withLoading extends AsyncTask<Void, String, Void> {

	private TransparentProgressDialog transparentProgressDialog;
	Context context;
	int value;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public BackgroundNetwork_withLoading(Context activity) {
		context = activity;
		transparentProgressDialog = new TransparentProgressDialog(context, R.drawable.load_2arrow);
	}
	
	public BackgroundNetwork_withLoading(Context activity, int value) {
		context = activity;
		this.value = value;
		transparentProgressDialog = new TransparentProgressDialog(context, R.drawable.load_2arrow);
	}
	
	

	protected void doActionOnPostExecuteBeforeProgressDismiss() {

	}

	protected void doActionOnPostExecuteAfterProgressDismiss() {

	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		doActionOnPostExecuteBeforeProgressDismiss();
		if(transparentProgressDialog.isShowing()){
			transparentProgressDialog.dismiss();
		}
		doActionOnPostExecuteAfterProgressDismiss();
	}

	public void onPostExecuteDeveloperMethodForPublicAccess(Void result) {
		super.onPostExecute(result);
		doActionOnPostExecuteBeforeProgressDismiss();
		if(transparentProgressDialog.isShowing()){
			transparentProgressDialog.dismiss();
		}
		doActionOnPostExecuteAfterProgressDismiss();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.transparentProgressDialog.show();
		this.transparentProgressDialog.setCancelable(false);
	}

	@Override
	protected Void doInBackground(Void... params) {
		return null;
	}
	
	

}
