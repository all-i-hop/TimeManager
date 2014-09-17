package at.fhjoanneum.android.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import at.fhjoanneum.android.R;

public class EditTextDatePicker implements OnClickListener, OnDateSetListener {
	EditText editText;
	private int day;
	private int month;
	private int year;
	private int status;
	private Activity context;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	public EditTextDatePicker(Context context, int editTextViewID) {
		this.context = (Activity) context;
		this.editText = (EditText) this.context.findViewById(editTextViewID);
		this.editText.setOnClickListener(this);
	}

	public EditTextDatePicker(Context context, int editTextViewID, int status) {
		this.context  = (Activity) context;
		this.editText = (EditText) this.context.findViewById(editTextViewID);
		this.editText.setOnClickListener(this);
		this.status = status;
	}

	public void setInitText(long deadLine) {       // set the textfield to the date that is handed over - only in case of task detail
		String[] date = dateFormat.format(deadLine).split("\\.");
		editText.setText(new StringBuilder().append(date[0]).append(".")
				.append(date[1]).append(".").append(date[2]));
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		this.year = year;
		month = monthOfYear;
		day = dayOfMonth;
		updateDisplay();
	}

	@Override
	public void onClick(View v) {
		Calendar calendar = Calendar.getInstance();
		DatePickerDialog dialog = new DatePickerDialog(context, this,
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	// updates the date in the date EditText
	private void updateDisplay() {
		if (status >  0 ){
			Toast.makeText(context, context.getResources().getText(R.string.updateAssignedExercsie), Toast.LENGTH_SHORT).show();
			return;
		}
		editText.setText(new StringBuilder().append(day).append(".")
				.append(month + 1).append(".").append(year));
	}

	public String getText() {
		return editText.getText().toString();
	}

}