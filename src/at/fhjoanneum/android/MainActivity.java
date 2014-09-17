package at.fhjoanneum.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import at.fhjoanneum.android.calendar.CalendarActivity;

public class MainActivity extends Activity {

	Drawable myCalendar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myCalendar = getResources().getDrawable(R.drawable.calendar_icon);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Defines the different ImageViews for the objects on the mainActivity
	 * and resets the icons after you get into the mainActivity again
	 */
	@Override
	public void onResume() {
		super.onResume();
		ImageView imgChecklist = (ImageView) findViewById(R.id.to_do_list);
		ImageView imgCalendar  = (ImageView) findViewById(R.id.calender);
		ImageView imgSettings  = (ImageView) findViewById(R.id.settings);
		ImageView imgAbout     = (ImageView) findViewById(R.id.about);
		
		imgChecklist.setImageResource(R.drawable.checklist_icon);
		imgCalendar.setImageResource(R.drawable.calendar_icon);
		imgSettings.setImageResource(R.drawable.settings_icon);
		imgAbout.setImageResource(R.drawable.about_icon);
		
	}

	public void calendarActivity(View view){
		myCalendar.setVisible(false, true);
		ImageView img = (ImageView) findViewById(R.id.calender);
		img.setImageResource(R.drawable.calendar_icon_clicked);
		Intent intent = new Intent(this, CalendarActivity.class);
		startActivity(intent);
	}
	
	public void toDoList (View view){
		ImageView img = (ImageView) findViewById(R.id.to_do_list);
		img.setImageResource(R.drawable.checklist_icon_clicked);
		Intent intent = new Intent(this, ToDoList.class);
		startActivity(intent);
	}
	
	public void onSettings (View view){
		ImageView img = (ImageView) findViewById(R.id.settings);
		img.setImageResource(R.drawable.settings_icon_clicked);
		Intent intent = new Intent(this, Settings.class);
		startActivity(intent);
	}
	
	public void onAbout (View view){
		ImageView img = (ImageView) findViewById(R.id.about);
		img.setImageResource(R.drawable.about_icon_clicked);
		Intent intent = new Intent(this, About.class);
		startActivity(intent);
	}
}