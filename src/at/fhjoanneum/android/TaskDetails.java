package at.fhjoanneum.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import at.fhjoanneum.android.data.DatabaseHandler;
import at.fhjoanneum.android.data.EditTextDatePicker;

public class TaskDetails extends Activity {

	private String exerciseName;
	private long deadlineMillis;
	private Integer status;
	private Integer priority;
	private String addInfo;
	private Integer duration;
	private Integer sessions;
	private TextView input_name;
	private TextView input_priority;
	private TextView input_addInfo;
	private TextView input_duration;
	private TextView input_sessions;
	private RadioGroup input_status;
	private EditTextDatePicker deadline;
	private View selectRadio;
	private DatabaseHandler db = new DatabaseHandler(this);
	private SQLiteDatabase database;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent startingIntent = getIntent();
		Exercise exercise = (Exercise) startingIntent
				.getSerializableExtra(ToDoList.EXERCISE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_details);
		database       = db.getWritableDatabase();
		exerciseName   = exercise.getName();
		deadlineMillis = exercise.getDeadline();
		status         = exercise.getStatus();
		priority       = exercise.getPriority();
		addInfo        = exercise.getAddInput();
		duration       = exercise.getDuration();
		sessions       = exercise.getSessions();
		input_name     = (TextView) findViewById(R.id.taskdetails_exercisename);
		deadline       = new EditTextDatePicker(this, R.id.taskdetails_deadline, status);
		deadline.setInitText(deadlineMillis);
		input_priority = (TextView) findViewById(R.id.taskdetails_priority);
		input_addInfo  = (TextView) findViewById(R.id.taskdetails_additional);
		input_status   = (RadioGroup) findViewById(R.id.radiogroup_status);
		input_duration = (TextView) findViewById(R.id.taskdetails_duration);
		input_sessions = (TextView) findViewById(R.id.taskdetails_sessions);
		selectRadio    = findViewById(R.id.rbtn_notYetFinished);
		setRbtn();
		switch (priority) {
		case 1:
			input_priority.setText(priority.toString()
					+ getResources().getText(R.string.priority1));
			break;
		case 2:
			input_priority.setText(priority.toString()
					+ getResources().getText(R.string.priority2));
			break;
		case 3:
			input_priority.setText(priority.toString()
					+ getResources().getText(R.string.priority3));
			break;
		}
		input_name.setText(exerciseName);
		input_sessions.setText(sessions.toString());
		input_duration.setText(duration.toString());
		input_addInfo.setText(addInfo);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // prevents the keyboard from immediately popping up
	}

	/**
	 * Formats the date into the new format
	 * @return - the date in milliseconds
	 */
	private long getDateChange() {
		Date newDate = new Date();
		try {
			newDate = dateFormat.parse(deadline.getText());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return newDate.getTime();
	}

	private void setRbtn() {
		switch (status) {
		case 0:
			input_status.check(R.id.rbtn_notYetFinished);
			break;
		case 1:
			input_status.check(R.id.rbtn_finished);
			break;
		default:
			input_status.check(R.id.rbtn_notYetFinished);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.task_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() != R.id.back) {
			return super.onOptionsItemSelected(item);
		}
		if (status < 1 && deadlineMillis < getDateChange()) { // change of deadline
			updateExercise();
		}
		Intent intent = new Intent(this, ToDoList.class);
		startActivity(intent);
		return true;
	}

	public void onRadioButtonClicked(View view) {
		if (status == 1 || status == -1) {
			Toast.makeText(this,
					getResources().getText(R.string.status_change),
					Toast.LENGTH_SHORT).show();
			setRbtn();
			return;
		}
		selectRadio = view;
		updateStatus();
	}

	/**
	 * Updates the Exercise in the database
	 */
	private void updateExercise() {
		ContentValues exerciseDeadline = new ContentValues();
		ContentValues workSessionDeadline = new ContentValues();
		exerciseDeadline.put(DatabaseHandler.DEADLINE, getDateChange());
		workSessionDeadline.put(DatabaseHandler.WORKSESSION_DEADLINE,
				getDateChange());
		db.updateTable(DatabaseHandler.TABLE_NAME, exerciseDeadline,
				DatabaseHandler.EXERCISE_NAME + " = '" + exerciseName + "';");
		db.updateTable(DatabaseHandler.WORKSESSION_TABLE_NAME,
				workSessionDeadline, DatabaseHandler.WORKSESSION_NAME
						+ " LIKE '%" + exerciseName + "%';");
		db.close();
	}

	public void updateStatus() {
		ContentValues values = new ContentValues();
		switch (selectRadio.getId()) {
		case R.id.rbtn_finished:
			values.put(DatabaseHandler.STATUS, 1);
			db.deleteWorksessions(exerciseName);
			break;
		case R.id.rbtn_notYetFinished:
			values.put(DatabaseHandler.STATUS, 0);
			break;
		}
		db.updateTable(DatabaseHandler.TABLE_NAME, values,
				DatabaseHandler.EXERCISE_NAME + " = '" + exerciseName + "';");
		db.updateTable(DatabaseHandler.WORKSESSION_TABLE_NAME, values,
				DatabaseHandler.WORKSESSION_NAME + " LIKE '%" + exerciseName
						+ "%';");
		db.close();
	}

}
