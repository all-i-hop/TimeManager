package at.fhjoanneum.android;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import at.fhjoanneum.android.data.CreateTask;
import at.fhjoanneum.android.data.DatabaseHandler;

public class ToDoList extends ListActivity   {
	
	private DatabaseHandler 			db  = new DatabaseHandler(this);
	private String[] 					columns = new String[]{"input_task", "input_deadline"};
	private String 						selectQuery = "SELECT "+ DatabaseHandler.EXERCISE_NAME + ", " + 
										DatabaseHandler.DEADLINE + " FROM " + DatabaseHandler.TABLE_NAME + " ORDER BY _deadline ASC";
	public final static String 			EXERCISE = "EXERCISE";
	public ListView 					list;
	public int 							REL_SWIPE_MIN_DISTANCE;
	public int 							REL_SWIPE_MAX_OFF_PATH;
	public int 							REL_SWIPE_THRESHOLD_VELOCITY;
	private Resources 					res;
	private SimpleDateFormat 			dateFormat = new SimpleDateFormat("dd.MM.yyy");
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_a_task);
		res = getResources();
		createList(createEntries(getCursor(), columns));
		DisplayMetrics dm = getResources().getDisplayMetrics();
		REL_SWIPE_MIN_DISTANCE = (int) (120.0f * dm.densityDpi / 160.0f + 0.5);
		REL_SWIPE_MAX_OFF_PATH = (int) (250.0f * dm.densityDpi / 160.0f + 0.5);
		REL_SWIPE_THRESHOLD_VELOCITY = (int) (200.0f * dm.densityDpi / 160.0f + 0.5);
	}
	
	private Cursor getCursor() {
		SQLiteDatabase data = db.getReadableDatabase();
		return data.rawQuery(selectQuery, null);
	}

	public void createList(ArrayList<HashMap<String, String>> names) {
		int[] listEntryFields = new int[]{R.id.input_task , R.id.input_deadline};
		ListViewAdapter nameAdapter = new ListViewAdapter(this, names, R.layout.twotext_in_listentry, columns, listEntryFields);
		setListAdapter(nameAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.to_do_list, menu);
		return true;
	}
	
	
	private ArrayList<HashMap<String, String>> createEntries(Cursor cursor, String[] columns ) { // getting Data out of Cursor!
		ArrayList<HashMap<String, String>> names = new ArrayList<HashMap<String, String>>();
		int mIndex1 = cursor.getColumnIndex(DatabaseHandler.EXERCISE_NAME);
		int mIndex2 = cursor.getColumnIndex(DatabaseHandler.DEADLINE);
		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> row = new HashMap<String, String>();
				row.put(columns[0], cursor.getString(mIndex1));
				row.put(columns[1], dateFormat.format(cursor.getLong(mIndex2)));
				names.add(row);
			} while (cursor.moveToNext());
		}
		return names;
	}
	
	public void createTask (View view){
		Intent intent = new Intent(this, CreateTask.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() != R.id.add_exercise && item.getItemId() != R.id.back){
			return super.onOptionsItemSelected(item);
		}
		
		Intent intent = new Intent();
		
		switch (item.getItemId()) {
			case R.id.add_exercise:
				intent = new Intent(this, CreateTask.class);
				break;
			case R.id.back:
				intent = new Intent(this, MainActivity.class);
				break;
		}
	 
		startActivity(intent);
		return true;
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		list = getListView();
		if (list == null) {
			new Throwable("Listview not set exception");
		}
	 
	@SuppressWarnings("deprecation")
	final GestureDetector gestureDetector = new GestureDetector(
	new MyGestureDetector());
	 
		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
	list.setOnTouchListener(gestureListener);
	 
}
	 
	private void myOnItemClick(int position) {
		if (position < 0)
		return;
		onItemClickListener(list.getAdapter(), position);
	}
	
	public void onItemClickListener(ListAdapter adapter, int position) {
		 HashMap<String, String> exercise = (HashMap<String, String>) getListAdapter().getItem(position);
			String exerciseName = exercise.get("input_task");
			Exercise exerciseInstance = db.getExercise(exerciseName);
			Intent intent = new Intent(this, TaskDetails.class);
			intent.putExtra(EXERCISE, (Serializable) exerciseInstance);
			startActivity(intent);
		}

	 
	 public void getSwipeItem(boolean isRight, int position) {
		 SQLiteDatabase data = db.getReadableDatabase();
		 HashMap<String, String> exercise = (HashMap<String, String>) getListAdapter().getItem(position);
			String exerciseName = exercise.get("input_task");
			db.deleteWorksessions(exerciseName);
			db.deleteExercise(exerciseName);
			db.close();
			createList(createEntries(getCursor(), columns));
		 }
	 
	 class MyGestureDetector extends SimpleOnGestureListener {
		 
			private int temp_position = -1;
			 
			// Detect a single-click and call my own handler.
		@Override
		public boolean onSingleTapUp(MotionEvent e) {

			int pos = list.pointToPosition((int) e.getX(), (int) e.getY());
			myOnItemClick(pos);
			return true;
		}
			 
		@Override
		public boolean onDown(MotionEvent e) {

			temp_position = list
					.pointToPosition((int) e.getX(), (int) e.getY());
			return super.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH)
				return false;
			if (e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {

				int pos = list
						.pointToPosition((int) e1.getX(), (int) e2.getY());

				if (pos >= 0 && temp_position == pos)
					getSwipeItem(false, pos);
			} else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {

				int pos = list
						.pointToPosition((int) e1.getX(), (int) e2.getY());
				if (pos >= 0 && temp_position == pos)
					getSwipeItem(true, pos);

			}
			return false;
		}
			 
		}
	 
}