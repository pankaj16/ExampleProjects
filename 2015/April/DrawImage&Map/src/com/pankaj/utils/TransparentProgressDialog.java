package com.pankaj.utils;

import com.pankaj.loginexttest.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class TransparentProgressDialog extends Dialog {
		
		private ImageView iv;
		
		public TransparentProgressDialog(Context context, int resourceIdOfImage) {
			super(context, R.style.TransparentProgressDialog);
        	WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        	wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        	getWindow().setAttributes(wlmp);
			setTitle(null);
			setCancelable(false);
			setOnCancelListener(null);
//			LinearLayout layout = new LinearLayout(context);
//			layout.setOrientation(LinearLayout.VERTICAL);
//			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//			iv = new ImageView(context);
//			iv.setImageResource(resourceIdOfImage);
//			layout.addView(iv, params);
//			addContentView(layout, params);
			setContentView(R.layout.progress);
			iv = (ImageView)findViewById(R.id.image);
			iv.setImageResource(resourceIdOfImage);
		}
		
		@Override
		public void show() {
			super.show();
			RotateAnimation anim = new RotateAnimation(0.0f, 360.0f , Animation.RELATIVE_TO_SELF, .490f, Animation.RELATIVE_TO_SELF, 0.579f);
			anim.setInterpolator(new LinearInterpolator());
			anim.setRepeatCount(Animation.INFINITE);
			anim.setDuration(3000);
			iv.setAnimation(anim);
			iv.startAnimation(anim);
		}
	}