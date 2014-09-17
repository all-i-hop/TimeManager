package at.fhjoanneum.android;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import at.fhjoanneum.android.data.DatabaseHandler;

public class ListViewAdapter extends SimpleAdapter {

	private final List<Map<String, String>> data;
	private final Context context;
	private final int layoutID;
	private DatabaseHandler db;
	
	
	/**
	 * 
	 * @param context	The context where the View associated with this SimpleAdapter is running
	 * @param data 		A List of Maps. Each entry in the List corresponds to one row in the list. 
	 * 			The Maps contain the data for each row, and should include all the entries specified in "from"
	 * @param resource	Resource identifier of a view layout that defines the views for this list item. The layout file 
	 * 			should include at least those named views defined in "to"
	 * @param from		A list of column names that will be added to the Map associated with each item
	 * @param to		The views that should display column in the "from" parameter. These should all be TextViews. 
	 * 			The first N views in this list are given the values of the first N columns in the from parameter. 
	 */
	public ListViewAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
	    super(context, data, resource, from, to);
	 
	    this.data = (List<Map<String, String>>) data;
	    this.context = context;
	    this.layoutID = resource;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View taskEntry = inflater.inflate(layoutID, parent, false);
		
		ImageView icon        = (ImageView) taskEntry.findViewById(R.id.actorPic);
		TextView deadline     = (TextView) taskEntry.findViewById(R.id.deadLine);
		TextView exerciseName = (TextView) taskEntry.findViewById(R.id.exercise_name);
		
		//set exercise name
		String fullExerciseName = data.get(position).get("input_task");
		exerciseName.setText(fullExerciseName);
		
		//set deadline
		String mydeadline = data.get(position).get("input_deadline");
		deadline.setText(mydeadline);

		int status = 0;
		try {
			db = new DatabaseHandler(context);
			Exercise exercise = db.getExercise(fullExerciseName);
			status = exercise.getStatus();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (status == 1) icon.setImageResource(R.drawable.check);
		if (status == -1) icon.setImageResource(R.drawable.conflict);
		return taskEntry;
	
	}
	
	
}
