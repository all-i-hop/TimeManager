package at.fhjoanneum.android.calendar;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import at.fhjoanneum.android.R;
import at.fhjoanneum.android.WorkSession;
import at.fhjoanneum.android.data.DatabaseHandler;

public class LoadExercises extends AsyncTask<Context, Integer, Boolean> implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	private Context 					context = CalendarActivity.getContext();
	private DatabaseHandler 			db = new DatabaseHandler(context);
	private SharedPreferences 			prefs =PreferenceManager.getDefaultSharedPreferences(context);;
	private Resources 					res = context.getResources();;
	private CalendarData 				calendarData = new CalendarData(context);
	private long						millisOfDay = 24 *60 * 60 * 1000;
	private long 						currentDateMillis = ((new Date().getTime() / millisOfDay ) * millisOfDay) - (60 * 60 * 1000) ;  //Current date in Milliseconds at midnight since
	private int 						weekStart;
	private int							workinHoursOfDay;
	private int 						weekendStarttime;
	private int 						weekendWorktime;
	private int 						pause;
	private boolean						weekndsAvailable;
	private int							sameSessionsDay;
	private static final String			WORKING_HOURS_KEY = "workingHoursPerDay";
	private static final String			PAUSE = "pause";
	private static final String 		WEEKSTART = "weekStartTime";
	private static final String 		WEEKENDS_AVAILABLE_KEY = "weekendAvailable";
	private static final String 		WEEKEND_STARTTIME = "weekendStart";
	private static final String 		WEEKEND_WORKTIME = "weekendWorktime";
	private static final String 		SESSIONS_A_DAY_KEY = "numberOfSameSessions";
	private ProgressDialog 				Asycdialog = new ProgressDialog(context);
	

	@Override
	protected void onPreExecute() {
		 Asycdialog.setMessage(context.getResources().getText(R.string.loading_message));
         Asycdialog.show();
		super.onPreExecute();
	}
	
	@Override
	protected Boolean doInBackground(Context... params) {
		prefs.registerOnSharedPreferenceChangeListener(this);
		getPreferences();
		boolean isSuccessfull = true;
		boolean endResult = true;
		ArrayList<WorkSession> notSetWorkSessions = db.getNotSetWorkSessions();
		if (!notSetWorkSessions.isEmpty()){
			ContentValues exerciseStatus = new ContentValues();
			exerciseStatus.put(DatabaseHandler.STATUS, 2);
			for (int i = 0; i < notSetWorkSessions.size(); i++){
				try {
					isSuccessfull = addEvent(notSetWorkSessions.get(i));
					if (!isSuccessfull) {
						endResult = false;
						String[] name = notSetWorkSessions.get(i).getNameOfWorkSession().split(" ");
						for (int e = 0; e < notSetWorkSessions.size(); e++){
							if (notSetWorkSessions.get(e).getNameOfWorkSession().split(" ")[0].equals(name[0])){
								notSetWorkSessions.remove(e);
								e--;
							}
						}
					 i = -1;
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			for (int i = 0; i < notSetWorkSessions.size(); i++ ){		//setting exercise status 2 -> assigned
				String[] workSessionName = notSetWorkSessions.get(i).getNameOfWorkSession().split(" ");
				db.updateTable(DatabaseHandler.TABLE_NAME, exerciseStatus,  DatabaseHandler.EXERCISE_NAME + " LIKE '%"+ workSessionName[0] +"%';");	
		}
	}
		return endResult;
}
	
	@Override
	protected void onPostExecute(Boolean result) {
		Asycdialog.dismiss();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	
	// sorts list of Worksessions
	private static class eventComparator implements Comparator<Pair<Pair<Long, Long>, String>> {
		@Override
		public int compare(Pair<Pair<Long, Long>, String> a, Pair<Pair<Long, Long>, String> b) {
			int f = a.first.first.compareTo(b.first.first);
			return f;
		}
	}
	
	//Getting preferences 
	private void getPreferences(){
		pause = Integer.parseInt(prefs.getString(PAUSE, res.getString(R.string.pause_default))) * 60 * 1000;
		workinHoursOfDay = Integer.parseInt(prefs.getString(WORKING_HOURS_KEY, res.getString(R.string.default_workingHours))) * 60  * 60 * 1000;
		weekStart = Integer.parseInt(prefs.getString(WEEKSTART, res.getString(R.string.default_weekStart))) * 60  * 60 * 1000;
		weekendStarttime = Integer.parseInt(prefs.getString(WEEKEND_STARTTIME, res.getString(R.string.weekendStart_default))) * 60  * 60 * 1000;
		weekendWorktime = Integer.parseInt(prefs.getString(WEEKEND_WORKTIME, res.getString(R.string.weekendWorktime_default))) * 60  * 60 * 1000;
		weekndsAvailable = prefs.getBoolean(WEEKENDS_AVAILABLE_KEY, Boolean.parseBoolean(res.getString(R.string.default_weekendAvailable)));
		sameSessionsDay = Integer.parseInt(prefs.getString(SESSIONS_A_DAY_KEY, res.getString(R.string.numberOfSessions_default)));
	}
	
	/**
	 * Called to add each single Event and update it in database
	 * @return - if successful or not
	 */
	public boolean addEvent(WorkSession workSession) throws ParseException{
		db  = new DatabaseHandler(context);
    	SQLiteDatabase data = db.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	long deadLineMillis = workSession.getDeadLineOfWorkSession();
    	long duration = workSession.getDuration() * 60 * 60 * 1000;
    	Long possibleTime = checkTime_new(deadLineMillis, duration, workSession.getNameOfWorkSession());
    	if (possibleTime == 0){ // not all worksessions of this exercise fit in calendar until deadline -> delete start + end time of the already 
    		resetWorksession(workSession.getNameOfWorkSession());					//set worksession of the exercises and mark it as conflicting in todolis
    		return false;
    	}
    	Long endOfExercise = possibleTime + duration;
    	values.put("_start", possibleTime);
    	values.put("_end", endOfExercise);
    	values.put("_status", 2);
    	String selection = DatabaseHandler.WORKSESSION_NAME + " = " + "'"+ workSession.getNameOfWorkSession()+"'" + " AND " + DatabaseHandler.WORKSESSION_NUMBER + " = " + "'"+ workSession.getSession()+"';";
    	data.update(DatabaseHandler.WORKSESSION_TABLE_NAME, values, selection , null); // set start + end time of work sessions
    	data.close();
    	return true;
    }
	
	/**
	 * called if a 1 Worksession gets in Conflict with Deadline - every Worksession nees to be reseted and Exercise will get Status - 1
	 */
	private void resetWorksession(String workSessionName){// delete start + end time of conflicting worksession - Param. worksession name + set status of exercise -1
		db  = new DatabaseHandler(context);
    	SQLiteDatabase data = db.getWritableDatabase();
		String[] workSessionNameArray = workSessionName.split(" ");
		ContentValues exerciseValues = new ContentValues();
		ContentValues workSessionValues = new ContentValues();
		exerciseValues.put(DatabaseHandler.STATUS, -1);
		workSessionValues.put(DatabaseHandler.WORKSESSION_START, 0);
		workSessionValues.put(DatabaseHandler.WORKSESSION_END, 0);
		workSessionValues.put(DatabaseHandler.WORKSESSION_STATUS, -1);
		db.updateTable(DatabaseHandler.WORKSESSION_TABLE_NAME, workSessionValues, DatabaseHandler.WORKSESSION_NAME + " LIKE '%"+ workSessionNameArray[0] +"%';");
		db.updateTable(DatabaseHandler.TABLE_NAME, exerciseValues, DatabaseHandler.EXERCISE_NAME + " LIKE '%"+ workSessionNameArray[0] +"%';");
	}
	
	/**
	 * checks every day if there is time for worksession
	 * @return - specific time in millis when worksession should be done
	 */
	private long checkTime_new (long DeadLineInMillis, long workTimeInMillis, String name){ // returns the start time of the workSession
		db  = new DatabaseHandler(context);
    	SQLiteDatabase data = db.getWritableDatabase();
		Calendar date = Calendar.getInstance();
		for (long i = currentDateMillis + millisOfDay; i < DeadLineInMillis; i += millisOfDay){		//goes through every day starts with next day 00:00
			long dayWorkBegin = weekStart;
			long dayWorkEnd =  dayWorkBegin + (workinHoursOfDay);
			long startOfDay = i;
			long endOfDay = i + millisOfDay;
			date.setTimeInMillis(i);
			ArrayList<Pair<Pair<Long, Long>, String>> workSessions = db.getWorkSessionOfDate(startOfDay, endOfDay);
			ArrayList<Pair<Pair<Long, Long>, String>> events = calendarData.getEvent(startOfDay, endOfDay); // we need a Pair<Pair<EventName, TimeRange (String)>, StartTime (to sort it later)
			if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){		
				if (!weekndsAvailable) continue;
				dayWorkBegin = weekendStarttime;
				dayWorkEnd = dayWorkBegin + (weekendWorktime);
			}
			if (getSameSessionsOfDay(name, workSessions, sameSessionsDay)) continue;				//returns true if there are alreay more than specified number of same sessions
			for (int w = 0 ; w < workSessions.size(); w++) {
				events.add(workSessions.get(w));	
			}
			Collections.sort(events, new eventComparator());								// sorting events from mobile calendar and worksessions
			if (events.isEmpty()) return i + dayWorkBegin;									//if there area no events set event on the beginning of workday
			if (events.get(0).first.first - (i + dayWorkBegin) >= workTimeInMillis + pause){		//if there is time between first event and dayworkbegin
				return i + dayWorkBegin;
			}
			else {						//else go through events of day and check if there is time between or at the end
				  for (int e = 0 ; e < events.size(); e++){
					   if (e + 1 == events.size()){		// if loop is at the end check if there is time between last event and and of day
						  if ((i + dayWorkEnd) - events.get(e).first.second  >= workTimeInMillis + (pause) ){
								return events.get(e).first.second + (pause);
						}
				  }
					  else {
						  if ((events.get(e + 1).first.first - events.get(e).first.second) >= workTimeInMillis + (pause * 2)){		//check if there is time between event and next event + pause * 2 
							  return events.get(e).first.second + (pause);
						  }
					  }
			  }
		}
	}
		db.close();
		return 0;			// returns the day in milli seconds where the exercise can be done
}

	/**
	 * checks if there already are the number of the same worksessions the user wants to have on a day 
	 * @return - true if there is
	 */
	private boolean getSameSessionsOfDay(String name, ArrayList<Pair<Pair<Long, Long>, String>> workSessions, int number){
		String pureName = name.split(" ")[0];
		int occurrences = 0;
		for (int i = 0; i < workSessions.size(); i++){
			if (workSessions.get(i).second.contains(pureName)){
				occurrences++;
			}
		}
		if (occurrences >= number){
			return true;
		}
		return false;
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
			prefs = sharedPreferences;
			if (key == WORKING_HOURS_KEY || key == WEEKENDS_AVAILABLE_KEY || key == SESSIONS_A_DAY_KEY
					|| key == WEEKEND_STARTTIME || key == WEEKEND_WORKTIME){
				getPreferences();
			}
	}

 }

