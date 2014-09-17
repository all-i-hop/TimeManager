package at.fhjoanneum.android.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import at.fhjoanneum.android.Exercise;
import at.fhjoanneum.android.R;
import at.fhjoanneum.android.ToDoList;
import at.fhjoanneum.android.WorkSession;

@SuppressLint("NewApi")
public class CreateTask extends Activity {

	private SimpleDateFormat 				mFormat = new SimpleDateFormat("dd.MM.yyyy");
	private EditText 						input_taskName;
	private EditTextDatePicker 				input_deadLine;
	private RadioGroup						input_priority;
	private EditText 						input_sessions;
	private EditText 						input_duration;
	private EditText 						input_additional;
	private String 							taskName;
	private String 							addInput;
	private int 							priority;
	private int 							sessions;
	private int 							duration;
	private Date 							deadLine;
	private Exercise 						exercise;
	private Date 							today = new Date();
	private View 							selectRadio;
	public DatabaseHandler 					db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_task);
		db = new DatabaseHandler(this);
		db.getWritableDatabase();
		input_taskName   = (EditText) findViewById(R.id.input_task);
		input_additional = (EditText) findViewById(R.id.input_additional);
		input_priority   = (RadioGroup) findViewById(R.id.radiogroup);
		input_priority.check(R.id.rbtn_priority1);			//Default button checked!
		selectRadio      = findViewById(R.id.rbtn_priority1);
		input_deadLine   = new EditTextDatePicker(this, R.id.input_deadline);
		input_deadLine.setInitText(new Date().getTime());
		input_sessions   = (EditText) findViewById(R.id.input_sessions);
		input_duration   = (EditText) findViewById(R.id.input_duration);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // prevents the keyboard from immediately popping up
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_task, menu);
		return true;
	}
	
	public void onRadioButtonClicked(View view){
		selectRadio = view;
	}
	
	/**
	 * This method checks the input of a created exercise.
	 * If one of the inputs is invalid, the exercise won't either be created nor saved into the database.
	 */
	@SuppressWarnings("deprecation")
	public void onCreateTask(){
		this.deadLine = new Date();
		try {
			this.deadLine = mFormat.parse(input_deadLine.getText());
		} catch (ParseException e){
			e.printStackTrace();
		}
		if (this.deadLine.before(today)){
			Toast.makeText(this, getResources().getText(R.string.datePast), Toast.LENGTH_SHORT).show();
			return;
		}
		if (this.input_taskName.length() < 1){
			Toast.makeText(this, getResources().getText(R.string.noTaskName), Toast.LENGTH_SHORT).show();
			return;
		}
		if (db.getNamesOfExercises().contains(this.input_taskName.getText().toString())) {
			Toast.makeText(this, getResources().getText(R.string.existingTaskName), Toast.LENGTH_SHORT).show();
			return;
		}
			switch (selectRadio.getId()){
				case R.id.rbtn_priority1:
					this.priority = 1;
					break;
				case R.id.rbtn_priority2:
					this.priority = 2;
					break;
				case R.id.rbtn_priority3:
					this.priority = 3;
					break;
				}
			try{
				this.taskName     = input_taskName.getText().toString();
				this.addInput     = input_additional.getText().toString();
				this.sessions     = Integer.parseInt(input_sessions.getText().toString());
				this.duration     = Integer.parseInt(input_duration.getText().toString());
				Exercise exercise = new Exercise(this.taskName, this.priority, this.deadLine.getTime(), this.sessions, this.duration, this.addInput);
				addExercise(exercise);
				addWorkSessions(exercise);
				Intent intent     = new Intent(this, ToDoList.class);
				startActivity(intent);
			} catch(Exception e){
				Toast.makeText(this, getResources().getText(R.string.invalidInput), Toast.LENGTH_SHORT).show();
				return;
			}
	}

	public void onCancel(View view) {
		Intent intent = new Intent(this, ToDoList.class);
		startActivity(intent);
	}

	/**
	 * Adds an exercise to the database
	 * 
	 * @param exercise - The exercise to be added to the database
	 */
	public void addExercise(Exercise exercise){
		db.insertRecord(exercise);
	}
	
	/**
	 * Creates and inserts the number of corresponding worksessions of an exercise to the database
	 * 
	 * @param exercise - The name of the exercise of which worksessions should be created and added to the database
	 */
	public void addWorkSessions(Exercise exercise){
		long deadLineMillis;
		int sessionNumber = 1;
		for (int i = 0; i < exercise.getSessions(); i++) {
			String nameOfSession = exercise.getName() + getResources().getText(R.string.numberWorkSession) + String.valueOf(sessionNumber++);
			try {
				deadLineMillis = exercise.getDeadline();
			} catch(Exception e){
				Toast.makeText(this, R.string.wrongDate, Toast.LENGTH_SHORT).show();
				return;
			}
			WorkSession session = new WorkSession(nameOfSession, deadLineMillis, exercise.getPriority(), exercise.getAddInput(), sessionNumber, exercise.getDuration() / exercise.getSessions());
			db.insertWorkSession(session);
		}
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() != R.id.add_exercise && item.getItemId() != R.id.back){
			return super.onOptionsItemSelected(item);
		}
		
		Intent intent = new Intent();
		
		switch (item.getItemId()) {
			case R.id.add_exercise:
				onCreateTask();
				break;
			case R.id.back:
				intent = new Intent(this, ToDoList.class);
				startActivity(intent);
				break;
		}
		return true;
	}


}