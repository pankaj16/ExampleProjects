package com.tutorialsface.popupmenu;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button btnShowPopup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnShowPopup = (Button) findViewById(R.id.btnShowPopup);
		btnShowPopup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopupWindow(v);
			}
		});
	}
	
	void showPopupWindow(View view) {
		PopupMenu popup = new PopupMenu(MainActivity.this, view);
		try {
			Field[] fields = popup.getClass().getDeclaredFields();
			for (Field field : fields) {
				if ("mPopup".equals(field.getName())) {
					field.setAccessible(true);
					Object menuPopupHelper = field.get(popup);
					Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
					Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
					setForceIcons.invoke(menuPopupHelper, true);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		popup.getMenuInflater().inflate(R.menu.popupmenu, popup.getMenu());
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
	
			public boolean onMenuItemClick(MenuItem item) {
				Toast.makeText(getApplicationContext(), "You Clicked : " + item.getTitle(),	Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		popup.show();
	}
}
