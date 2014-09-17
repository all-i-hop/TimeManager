package at.fhjoanneum.android.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.fhjoanneum.android.MainActivity;
import at.fhjoanneum.android.R;
import at.fhjoanneum.android.WorkSession;
import at.fhjoanneum.android.calendar.CalendarView.OnDispatchDateSelectListener;
import at.fhjoanneum.android.data.DatabaseHandler;

public class CalendarActivity extends Activity implements  OnDispatchDateSelectListener {
	private TextView 					mTextDate;
	private SimpleDateFormat 			mFormat;
	private SimpleDateFormat 			exerciseTimeFormat;
	private CalendarData 				calendarData;
	private ListView					mList;
	private long						millisOfDay = 60 * 60 * 24 * 1000;
	long 								currentTime = new Date().getTime();
	private DatabaseHandler 			db  = new DatabaseHandler(this);
	private static Context				context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ((CalendarView) findViewById(R.id.calendar)).setOnDispatchDateSelectListener(this);
        db = new DatabaseHandler(this);
        context = this;
		db.getWritableDatabase();
        calendarData = new CalendarData(this);
        mList = (ListView) findViewById(R.id.events);
        mTextDate= (TextView) findViewById(R.id.display_date);
        mFormat = new SimpleDateFormat("dd.MM.yyyy");
        exerciseTimeFormat = new SimpleDateFormat("HH:mm");
        onDispatchDateSelect(new Date());
    }
    
    public static Context getContext(){
    	return context;
    }
  
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() != R.id.calendar_back && item.getItemId() != R.id.add_Worksessions){
			return super.onOptionsItemSelected(item);
		}
		switch (item.getItemId()) {
		case R.id.add_Worksessions:
			boolean c = true;
			LoadExercises loadExercises = new LoadExercises();
			loadExercises.execute(this);
			try {
				 c = loadExercises.get(); // checks if adding Worksession was successfull
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			if (!c) Toast.makeText(this, getResources().getText(R.string.conflict_calendar), Toast.LENGTH_LONG).show();
			break;
		case R.id.calendar_back:
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			break;
	}
		return true;
}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.calendar, menu);
		return true;
	}
	
 
	private static class eventComparator implements Comparator<Pair<Pair<Long, Long>, String>> {
		@Override
		public int compare(Pair<Pair<Long, Long>, String> a, Pair<Pair<Long, Long>, String> b) {
			int f = a.first.first.compareTo(b.first.first);
			return f;
		}
	}
    
    
	@Override
	public void onDispatchDateSelect(Date date) {
		db = new DatabaseHandler(this);
		Long start = (date.getTime() / millisOfDay) * millisOfDay;
		Long end = start + millisOfDay;
		ArrayList<Pair<Pair<Long, Long>, String>> workSessions = db.getWorkSessionOfDate(start, end);
		ArrayList<Pair<Pair<Long, Long>, String>> rows = calendarData.getEvent(start, end); // return every event of the day in Pair <Title, TimeRange>
		if (!workSessions.isEmpty()) {
			for (int i = 0; i < workSessions.size(); i++) {
				rows.add(workSessions.get(i));
			}
			Collections.sort(rows, new eventComparator());
		}
		ArrayList<Pair<String, String>> newRowType = changeType(rows);
		createList(createRows(newRowType));
	}

	/**
	 * method takes start and end time and changes it to String which will be shown in List
	 * @return - ArrayList with (Starttime - Endtime)
	 */
	private ArrayList<Pair<String, String>> changeType(ArrayList<Pair<Pair<Long, Long>, String>> rows) {
		ArrayList<Pair<String, String>> rows_Changed = new ArrayList<Pair<String,String>>();
		for (int i = 0; i < rows.size(); i++){
			String startTime = exerciseTimeFormat.format(new Date (rows.get(i).first.first));
            String endTime = exerciseTimeFormat.format(new Date (rows.get(i).first.second));
			Pair<String, String> rowEntry = new Pair<String, String>(rows.get(i).second, startTime + "-" + endTime);
			rows_Changed.add(rowEntry);
		}
		return rows_Changed;	
	}
	
	private void createList(ArrayList<HashMap<String, String>> rows){
		String[] columns = new String[] {"event_name", "event_start"};
		int[] listEntryFields = new int[]{R.id.event_name , R.id.event_start};
		CalendarListAdapter listAdapter = new CalendarListAdapter(this, rows,  R.layout.event_list_calendar, columns, listEntryFields );
		mList.setAdapter(listAdapter);
	}
	

	private ArrayList<HashMap<String, String>> createRows(ArrayList<Pair<String, String>> eventList){ // creating each row for the Listview
		ArrayList<HashMap<String, String>> rowData = new ArrayList<HashMap<String,String>>();
		String[] columns = new String[] {"event_name", "event_start"};
		for (int i = 0 ; i < eventList.size(); i ++){
			HashMap<String, String> row = new HashMap<String, String>();
			row.put(columns[0], eventList.get(i).first);
			row.put(columns[1], eventList.get(i).second);
			rowData.add(row);
		}
		return rowData;
	}
	
}

