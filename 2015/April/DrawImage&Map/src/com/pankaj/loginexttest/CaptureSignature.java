package com.pankaj.loginexttest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.cloudinary.Cloudinary;
import com.pankaj.utils.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CaptureSignature extends Activity {
	LinearLayout mContent;
	signature mSignature;
	Button mClear, mGetSign, mCancel;
	public int count = 1;
	public String current = null;
	private Bitmap mBitmap;
	View mView;
	Intent intent;
	byte[] byteArray;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.signature);

		findViews();
		listners();
		
	}

	
	private void findViews(){
		mContent = (LinearLayout) findViewById(R.id.signature_base_layout);
		mSignature = new signature(this, null);
		mSignature.setBackgroundColor(Color.WHITE);
		mContent.addView(mSignature, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mClear = (Button) findViewById(R.id.clear);
		mGetSign = (Button) findViewById(R.id.use_btn);
		// mGetSign.setEnabled(false);
		mCancel = (Button) findViewById(R.id.cancel_btn);
		mView = mContent;
	}
	
	private void listners(){
		mClear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.v("log_tag", "Panel Cleared");
				mSignature.clear();
			}
		});
		mGetSign.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				try {
					Log.v("log_tag", "Panel Saved");

					mView.setDrawingCacheEnabled(true);

					Bitmap mBitmap = mView.getDrawingCache();
					Canvas canvas = new Canvas(mBitmap);
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byteArray = stream.toByteArray();

					mView.draw(canvas);
					mSignature.save(mView);

				} catch (Exception e) {
					System.out.println(">>>Exception " + e + "  >>> Message : "
							+ e.getMessage());
				}
			}
		});
		mCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		Log.w("GetSignature", "onDestory");
		super.onDestroy();
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	public class signature extends View {
		private static final float STROKE_WIDTH = 5f;
		private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
		private Paint paint = new Paint();
		private Path path = new Path();
		private float lastTouchX;
		private float lastTouchY;
		private final RectF dirtyRect = new RectF();

		public signature(Context context, AttributeSet attrs) {

			super(context, attrs);
			paint.setAntiAlias(true);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeWidth(STROKE_WIDTH);
		}

		public Bitmap getImage() {
			return mBitmap;
		}

		public void save(View v) {

			Log.v("log_tag", "Width: " + v.getWidth());
			Log.v("log_tag", "Height: " + v.getHeight());
			if (mBitmap == null) {
				mBitmap = Bitmap.createBitmap(mContent.getWidth(),
						mContent.getHeight(), Bitmap.Config.RGB_565);
			}
			Canvas canvas = new Canvas(mBitmap);
			try {
				Bitmap b = v.getDrawingCache();
				File sdCard = Environment.getExternalStorageDirectory();
				File file = new File(sdCard, "image.jpg");
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(file);
					b.compress(CompressFormat.JPEG, 95, fos);
					new DevicePreferences().addKey(CaptureSignature.this, "file", file.toString());
//					Toast.makeText(CaptureSignature.this, file.toString(), Toast.LENGTH_LONG).show();
					Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
					uploadImage(bitmap);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				String url = Images.Media.insertImage(getContentResolver(),
						mBitmap, "title", null);
				Log.d("log_tag", "url: " + url);

			} catch (Exception e) {
				Log.v("log_tag", e.toString());
			}
		}

		public void clear() {
			path.reset();
			invalidate();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawPath(path, paint);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float eventX = event.getX();
			float eventY = event.getY();
			// mGetSign.setEnabled(true);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				path.moveTo(eventX, eventY);
				lastTouchX = eventX;
				lastTouchY = eventY;
				return true;
			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_UP:
				resetDirtyRect(eventX, eventY);
				int historySize = event.getHistorySize();
				for (int i = 0; i < historySize; i++) {
					float historicalX = event.getHistoricalX(i);
					float historicalY = event.getHistoricalY(i);
					expandDirtyRect(historicalX, historicalY);
					path.lineTo(historicalX, historicalY);
				}
				path.lineTo(eventX, eventY);
				break;
			default:
				debug("Ignored touch event: " + event.toString());
				return false;
			}
			invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
					(int) (dirtyRect.top - HALF_STROKE_WIDTH),
					(int) (dirtyRect.right + HALF_STROKE_WIDTH),
					(int) (dirtyRect.bottom + HALF_STROKE_WIDTH));
			lastTouchX = eventX;
			lastTouchY = eventY;
			return true;
		}

		private void debug(String string) {
		}

		private void expandDirtyRect(float historicalX, float historicalY) {
			if (historicalX < dirtyRect.left) {
				dirtyRect.left = historicalX;
			} else if (historicalX > dirtyRect.right) {
				dirtyRect.right = historicalX;
			}
			if (historicalY < dirtyRect.top) {
				dirtyRect.top = historicalY;
			} else if (historicalY > dirtyRect.bottom) {
				dirtyRect.bottom = historicalY;
			}
		}

		private void resetDirtyRect(float eventX, float eventY) {
			dirtyRect.left = Math.min(lastTouchX, eventX);
			dirtyRect.right = Math.max(lastTouchX, eventX);
			dirtyRect.top = Math.min(lastTouchY, eventY);
			dirtyRect.bottom = Math.max(lastTouchY, eventY);
		}
	}
	
	private void uploadImage(final Bitmap bitmap) {
		//www.cloudinary.com
		//Credential:-
		//email: xdgunj@imgof.com
		//password: android123
		
		new BackgroundNetwork_withLoading(CaptureSignature.this) {
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
				// new BackgroundNetwork(context) {
				// protected Void doInBackground(Void[] params) {
				try {
					if (!data.equals("") || data != null) {
						JSONObject objJsonObject = new JSONObject(data);
						// for displaying and removing from list
//						publicIdArrayList.add(objJsonObject.getString(
//								"public_id").toString());
						// for checking against deleted images id
						Toast.makeText(CaptureSignature.this, "Image Uploaded", Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(CaptureSignature.this, "Error Occur while uploading",
							Toast.LENGTH_SHORT).show();
				}
			};

		}.execute();

	}
}
