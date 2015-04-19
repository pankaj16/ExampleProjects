package com.pankaj.loginexttest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends ActionBarActivity {

	Button signatureButton;
	GoogleMap googleMap;

	public static int statusGooglePlayService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_layout);
		
		init();
		findViews();
		listeners();
	}
	
	private void init(){
		// Getting reference to the SupportMapFragment
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);

		// Getting reference to the Google Map
		googleMap = mapFragment.getMap();

		statusGooglePlayService = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(HomeActivity.this);
		if (statusGooglePlayService == ConnectionResult.SUCCESS) {
			if (googleMap != null) {
				googleMap.setMyLocationEnabled(true);
				googleMap.setOnMyLocationChangeListener(onlocationChangeListener);
			}
		} else {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
					statusGooglePlayService, HomeActivity.this, -1);
			dialog.show();
		}
	}
	
	private void findViews(){
		signatureButton = (Button)findViewById(R.id.home_button_signature);
	}
	
	private void listeners(){
		signatureButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, CaptureSignature.class);
				startActivity(intent);
			}
		});
	}
	
	OnMyLocationChangeListener onlocationChangeListener = new OnMyLocationChangeListener() {

		@Override
		public void onMyLocationChange(Location location) {
			try {
				// For preventing callback on this method again
				googleMap.setOnMyLocationChangeListener(null);
				 double latitude = location.getLatitude();
				 double longitude = location.getLongitude();
				 LatLng latlng = new LatLng(latitude, longitude);
				
				 googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
				 googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
