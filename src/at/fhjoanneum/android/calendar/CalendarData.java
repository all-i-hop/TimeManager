package at.fhjoanneum.android.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.util.Pair;
import at.fhjoanneum.android.R;

@SuppressLint({ "InlinedApi", "NewApi" })
public class CalendarData implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	private Context 					context;
	private static final Uri 			CALENDAR_URI = Uri.parse("content://com.android.calendar/calendars");
	private static final String			CALENDAR_NAME = "calendarName";
	private SimpleDateFormat			formatter = new SimpleDateFormat("HH:mm");
	private SharedPreferences 			prefs;
	private Resources 					res;
	private String						calendar;

	public CalendarData(Context context) {
		this.context = context;
		res = context.getResources();
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.registerOnSharedPreferenceChangeListener(this);
		getCalendarPreference();
	}
	
	private void getCalendarPreference(){
		calendar = prefs.getString(CALENDAR_NAME, res.getString(R.string.default_calendarName));
	}
	
	
	// get all calendars of our phone - just to see what calendars you have
	public ArrayList<String> getCalendarName( ) {
		ArrayList<String> listOfNames = new ArrayList<String>();
		String [] projection = new String[] {Calendars._ID ,
							Calendars.NAME,
							Calendars.ACCOUNT_NAME,
							Calendars.ACCOUNT_TYPE}; // columns of cursor
		Cursor calCursor = context.getContentResolver().query(CALENDAR_URI , projection, 
							Calendars.VISIBLE + " = 1 ",
							null, Calendars._ID + " ASC "); // getting a cursor from the calendar with the URI, that looks like projection (columsn)
															// is visible and has ascending order
		int mIndex = calCursor.getColumnIndex(Calendars.ACCOUNT_TYPE);
		if (calCursor.moveToFirst()){
			long id = calCursor.getLong(0);
			String displayName = calCursor.getString(0);
			listOfNames.add(displayName);
		}
		calCursor.close();
		return listOfNames;
	}

	
	//to get the id of the calendar we want to work with
	private long getCalendarId() { 
		   String[] projection = new String[]{Calendars._ID};
		   String selection = 
		         Calendars.CALENDAR_DISPLAY_NAME + " = " + "'" + calendar + "'";		// Name of the calendar (different on every phone) 
		   Cursor cursor = 
		         context.getContentResolver().
		               query(
		            	  CALENDAR_URI, 
		                  projection, 
		                  selection, 
		                  null, 
		                  null); 
		   if (cursor.moveToFirst()) { 
		      return cursor.getLong(0); 
		   } 
		   cursor.close();
		   return -1; 
		} 
	
	public ArrayList<Pair<Pair<Long, Long>, String>> getEvent(long start, long end){   // returngin a Pair out of  a Pair with start and endTime in mIllis and the name of the event
		ArrayList<Pair<Pair<Long, Long>, String>> eventList = new ArrayList<Pair<Pair<Long, Long>, String>>(); // List of events - each Pair contains name and timerange of event
        String[] projection = new String[] {Events._ID, Events.TITLE, Events.DTSTART, Events.DTEND}; // columns of the Database 
        String selection = Events.CALENDAR_ID + " = " + getCalendarId() + " and " + Events.DTSTART + " > " + start + " and " 
        + Events.DTEND + " < " + end; // SQL selection 
        Cursor eventCursor = context.getContentResolver().query(Events.CONTENT_URI,
        		projection, selection, null, Events.DTSTART);
        while (eventCursor.moveToNext()) {
        	Pair<Pair<Long, Long>, String> event;
            String startTime = formatter.format(new Date (Long.parseLong(eventCursor.getString(2))));
            String endTime = formatter.format(new Date (Long.parseLong(eventCursor.getString(3))));
            Pair<Long, Long> eventInfo = new Pair<Long, Long>(eventCursor.getLong(2), eventCursor.getLong(3));
            if (eventCursor.getString(1).contains(",")) { 
            	String[] titleLong = eventCursor.getString(1).split(", ");
                event = new Pair<Pair<Long ,Long>, String>(eventInfo, titleLong[0] + ", " + titleLong[2]);
            }
            else {
            	String title = eventCursor.getString(1);
            	event = new Pair<Pair<Long,Long>, String>(eventInfo, title);
            }
            eventList.add(event);
        }
        eventCursor.close();
        return eventList;
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		prefs = sharedPreferences;
		if (key.equals(CALENDAR_NAME)) {
			getCalendarPreference();
		}
	}
	


}
	
