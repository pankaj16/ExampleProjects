package com.pankaj.actionbarfadingparallax;

import com.pankaj.actionbarfadingparallax.ParallaxScrollView.OnScrollCallBack;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnScrollCallBack{

	ActionBar actionBar;
	Drawable mDrawable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Dont forget to put this one line
		supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		
		setContentView(R.layout.activity_main);
		
		// Inflate your custom layout
	    final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
	            R.layout.actionbar_layout,
	            null);
	    //Set Custom ActionBar
		actionBar = getSupportActionBar();
	    actionBar.setDisplayShowHomeEnabled(false);
	    actionBar.setDisplayShowTitleEnabled(false);
	    actionBar.setDisplayShowCustomEnabled(true);
	    actionBar.setCustomView(actionBarLayout);
	    
	    // You customization
	    mDrawable = getResources().getDrawable(R.drawable.ab_background_light);
	    actionBar.setBackgroundDrawable(mDrawable);
	    mDrawable.setAlpha(0);
	    
	    TextView titleTextView = (TextView)findViewById(R.id.textView_actionBar_title);
	    
	    titleTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "ActionBar Title clicked", Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onScrollValues(int scrollPosition) {
		 int headerHeight = 100+actionBar.getHeight();
	        float ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;
	        int newAlpha = (int) (ratio * 255);
	        mDrawable.setAlpha(newAlpha);
		
	}
}
