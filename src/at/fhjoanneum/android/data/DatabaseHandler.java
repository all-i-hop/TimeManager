package at.fhjoanneum.android.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;
import at.fhjoanneum.android.Exercise;
import at.fhjoanneum.android.WorkSession;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME ="exercise.db";
	private static final int DATABSE_VERSION = 1;
	
	public static final String TABLE_NAME    = "exercise_table";
	public static final String EXERCISE_NAME = "_name";
	public static final String ADD_INFO      = "__addInfo";
	public static final String PRIORITY      = "_priority";
	public static final String DEADLINE      = "_deadline";
	public static final String DURATION      = "_duration";
	public static final String LENGTH        = "_length";
	public static final String STATUS        = "_status";
	public static final String SESSION       = "_session";
	public static final String OBJECT        = "_object";
	
	public static final String WORKSESSION_TABLE_NAME 	= "workSession_table";
	public static final String WORKSESSION_NAME       	= "_name";
	public static final String WORKSESSION_PRIORITY     = "_deadline";
	public static final String WORKSESSION_DEADLINE   	= "_priority";
	public static final String WORKSESSION_ADDINPUT   	= "_addInput";
	public static final String WORKSESSION_NUMBER     	= "_session";
	public static final String WORKSESSION_DURATION   	= "_duration";
	public static final String WORKSESSION_START  		= "_start";
	public static final String WORKSESSION_END		  	= "_end";
	public static final String WORKSESSION_STATUS		= "_status";
	
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABSE_VERSION);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHandler(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createExerciseTable = "CREATE TABLE " + TABLE_NAME + " ( " +
							EXERCISE_NAME +" TEXT PRIMARY KEY,"+
							DEADLINE +" INTEGER, "+
							PRIORITY +" INTEGER," +
							SESSION + " INTEGER," +
							DURATION + " INTEGER, " +
							ADD_INFO +" TEXT,"+
							LENGTH + " INTEGER," +
							STATUS + " INTEGER," +
							OBJECT + " TEXT);"; 
							db.execSQL(createExerciseTable);
		
		String createWorkSessionTable = "CREATE TABLE " + WORKSESSION_TABLE_NAME + " ( " +
										WORKSESSION_NAME + " TEXT PRIMARY KEY," +
										WORKSESSION_DEADLINE + " INTEGER, " +
										WORKSESSION_PRIORITY + " INTEGER, " +
										WORKSESSION_NUMBER + " INTEGER, " + 
										WORKSESSION_DURATION + " TEXT, " +
										WORKSESSION_START + " INTEGER, " +
										WORKSESSION_END + " INTEGER, " +
										WORKSESSION_ADDINPUT + " TEXT, "+
										WORKSESSION_STATUS + " INTEGER);";
										db.execSQL(createWorkSessionTable);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
		onCreate(db);

	}
	/**
	 * This method gets called if an exercise should be added to the database
	 * 
	 * @param exercise - The name of the exercise to be added to the database
	 */
	public void insertRecord(Exercise exercise) {
		  SQLiteDatabase database = this.getWritableDatabase();
		  ContentValues values = new ContentValues();
		  values.put(EXERCISE_NAME, exercise.getName() );
		  values.put(DEADLINE, exercise.getDeadline());
		  values.put(PRIORITY, exercise.getPriority());
		  values.put(SESSION, exercise.getSessions());
		  values.put(DURATION, exercise.getDuration());
		  values.put(ADD_INFO, exercise.getAddInput());
		  values.put(STATUS, exercise.getStatus());
		  database.insert(TABLE_NAME, null, values);
		  database.close();
		}
	
	/**
	 * This method gets called if an WorkSession should be added to the database
	 * 
	 * @param session - Name of the WorkSession that should be added to the db
	 */
	public void insertWorkSession(WorkSession session) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(WORKSESSION_NAME, session.getNameOfWorkSession());
		values.put(WORKSESSION_DEADLINE, session.getDeadLineOfWorkSession());
		values.put(WORKSESSION_NUMBER, session.getSession());
		values.put(WORKSESSION_START, session.getStart());
		values.put(WORKSESSION_END, session.getEnd());
		values.put(WORKSESSION_DURATION, session.getDuration());
		values.put(WORKSESSION_ADDINPUT, session.getAddInput());
		values.put(WORKSESSION_STATUS, session.getStatus());
		database.insert(WORKSESSION_TABLE_NAME, null, values);
		database.close();
	}
	
	/**
	 * Does an update of a table in the database
	 * 
	 * @param tableName - Name of the table that sould be updated
	 * @param values -  map from column names to new column values
	 * @param where - SQL condition
	 */
	public void updateTable(String tableName, ContentValues values, String where){
		SQLiteDatabase db = this.getWritableDatabase();
		db.update(tableName, values, where, null);
		db.close();
	}
	
	/**
	 * Gets called if an exercise should be read out of the database
	 * 
	 * @param name - name of the exercise that should be read out of the database
	 * @return - an Object of type Exercise of the given name
	 */
	public Exercise getExercise(String name){			//get specific Exercise
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE _name = '" + name +"'" + ";";
		Cursor cursor = db.rawQuery(selectQuery, null);
		int indexPriority = cursor.getColumnIndex(PRIORITY);
		int indexDeadline = cursor.getColumnIndex(DEADLINE);
		int indexStatus   = cursor.getColumnIndex(STATUS);
		int indexSessions = cursor.getColumnIndex(SESSION);
		int indexDuration = cursor.getColumnIndex(DURATION);
		int indexAddInput = cursor.getColumnIndex(ADD_INFO);
		cursor.moveToFirst();
		Exercise exercise =  new Exercise(name, cursor.getInt(indexPriority), cursor.getLong(indexDeadline), cursor.getInt(indexSessions), cursor.getInt(indexDuration), cursor.getString(indexAddInput));
		exercise.setStatus(cursor.getInt(indexStatus));
		cursor.close();
		db.close();
		return exercise;
	}
	
	/**
	 * Gets called if a WorkSession should be read out of the database
	 * 
	 * @param name - The name of the WorkSession needed
	 * @return - an Object of type WorkSession of the given name
	 */
	public WorkSession getWorkSession(String name){			//get specific Worksession 
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + WORKSESSION_TABLE_NAME + " WHERE _name LIKE '%" + name + "%';";
		Cursor cursor = db.rawQuery(selectQuery, null);
		int indexPriority = cursor.getColumnIndex(WORKSESSION_PRIORITY);
		int indexDeadline = cursor.getColumnIndex(WORKSESSION_DEADLINE);
		int indexStatus   = cursor.getColumnIndex(WORKSESSION_STATUS);
		int indexSession =  cursor.getColumnIndex(WORKSESSION_NUMBER);
		int indexDuration = cursor.getColumnIndex(WORKSESSION_DURATION);
		int indexAddInput = cursor.getColumnIndex(WORKSESSION_ADDINPUT);
		int indexEnd	 = cursor.getColumnIndex(WORKSESSION_END);
		int indexStart	 = cursor.getColumnIndex(WORKSESSION_START);
		cursor.moveToFirst();
		WorkSession worksession =  new WorkSession(name,cursor.getLong(indexDeadline), cursor.getInt(indexPriority),cursor.getString(indexAddInput), cursor.getInt(indexSession), cursor.getInt(indexDuration));
		worksession.setStatus(cursor.getInt(indexStatus));
		worksession.setStart(cursor.getLong(indexStart));
		worksession.setEnd(cursor.getLong(indexEnd));
		cursor.close();
		db.close();
		return worksession;
	}
	
	/**
	 * Gets called if not set WorkSessions are needed
	 * @return - an ArrayList containing all WorkSessions that are not date set yet
	 */
	public ArrayList<WorkSession> getNotSetWorkSessions(){		//returns all worksessions with do not have start + endtime
		ArrayList<WorkSession> worksessions = new ArrayList<WorkSession>();
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + WORKSESSION_TABLE_NAME +" WHERE _start = 0 and _status < 1 ORDER BY  _deadline, _priority ASC;";
		Cursor cursor =  db.rawQuery(selectQuery, null);
		int indexName     = cursor.getColumnIndex(WORKSESSION_NAME);
		int indexStart    = cursor.getColumnIndex(WORKSESSION_START);
		int indexEnd      = cursor.getColumnIndex(WORKSESSION_END);
		int indexDeadline = cursor.getColumnIndex(WORKSESSION_DEADLINE);
		int indexPriority = cursor.getColumnIndex(WORKSESSION_PRIORITY);
		int indexNumber   = cursor.getColumnIndex(WORKSESSION_NUMBER);
		int indexDuration = cursor.getColumnIndex(WORKSESSION_DURATION);
		int indexAddInput = cursor.getColumnIndex(WORKSESSION_ADDINPUT);
		while (cursor.moveToNext()){
			WorkSession worksession = new WorkSession(cursor.getString(indexName), cursor.getLong(indexDeadline), 
					cursor.getInt(indexPriority), cursor.getString(indexAddInput), cursor.getInt(indexNumber), cursor.getInt(indexDuration));
			worksession.setStart(cursor.getLong(indexStart));
			worksession.setEnd(cursor.getLong(indexEnd));
			worksessions.add(worksession);
		}
		cursor.close();
		db.close();
		return worksessions;
	}
	
	/**
	 * Gets called if an ArrayList with all exercise names is needed
	 * 
	 * @return - an ArrayList that contains all existing exercise names in the database
	 */
	public ArrayList<String> getNamesOfExercises() {
		ArrayList<String> exerciseNames = new ArrayList<String>();
		SQLiteDatabase db  = this.getReadableDatabase();
		String selectQuery = "SELECT _name from " + TABLE_NAME;
		Cursor cursor      = db.rawQuery(selectQuery, null);
		int indexName      = cursor.getColumnIndex(EXERCISE_NAME);
		if (cursor.moveToFirst()) {
			exerciseNames.add(cursor.getString(indexName));
			while (cursor.moveToNext()) {
				exerciseNames.add(cursor.getString(indexName));
			}
			cursor.close();
			db.close();
		}
		return exerciseNames;
	}
	
	/**
	 * Gets called if a WorkSession of a specific period of time is needed
	 * 
	 * @param date - The start time of the period
	 * @param nextDay - The end time of the period
	 * @return - an ArrayList that contains a Pair with another Pair in it with start and end time and a String with the name of the WorkSession
	 */
	public ArrayList<Pair<Pair<Long, Long>, String>> getWorkSessionOfDate (long date, long nextDay) { 
		ArrayList<Pair<Pair<Long, Long>, String>> workSessions = new ArrayList<Pair<Pair<Long,Long>,String>>();	
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<WorkSession> workSessionsOfDate = new ArrayList<WorkSession>();
		String selectQuery = "SELECT * FROM " + WORKSESSION_TABLE_NAME + " WHERE _start > '" + date + "'" + " AND "
				+ " _end < '" + nextDay +"' ORDER BY _start;";
		Cursor cursor = db.rawQuery(selectQuery, null);
		int indexName = cursor.getColumnIndex(WORKSESSION_NAME);	
		int indexStart = cursor.getColumnIndex(WORKSESSION_START);
		int indexEnd = cursor.getColumnIndex(WORKSESSION_END);
		while (cursor.moveToNext()) {
			Pair <Long, Long> startEndTime = new Pair<Long, Long>(cursor.getLong(indexStart), cursor.getLong(indexEnd));
			Pair <Pair<Long, Long>, String> workSessionPair = new Pair<Pair<Long,Long>, String>(startEndTime, cursor.getString(indexName));
			workSessions.add(workSessionPair);
		}
		cursor.close();
		db.close();
		return workSessions;
	}
	
	/**
	 * Deletes an exercise from the database
	 * 
	 * @param name - name of the exercise to be deleted
	 */
	public void deleteExercise(String name) {
		SQLiteDatabase db = this.getReadableDatabase();
		String deleteQuery = "DELETE FROM " + TABLE_NAME + " WHERE _name = '" + name + "'" + ";";
		Cursor cursor = db.rawQuery(deleteQuery, null);
		cursor.moveToFirst();
		cursor.close();
		db.close();
	}
	
	/**
	 * Deletes all WorkSessions of a given name
	 * 
	 * @param name - The name of the WorkSessions to be deleted
	 */
	public void deleteWorksessions(String name){		 // delets all worksessions that contains 'name'
		String[] workSessionName = name.split(" "); 	// it is important to get the name of the worksession without 'WokrSession name Nr.'
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(WORKSESSION_TABLE_NAME, WORKSESSION_NAME + " LIKE '" + workSessionName[0] +"%';", null);
		db.close();
	}
	

}