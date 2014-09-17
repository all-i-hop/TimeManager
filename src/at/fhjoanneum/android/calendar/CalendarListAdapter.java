package at.fhjoanneum.android.calendar;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import at.fhjoanneum.android.ListViewAdapter;
import at.fhjoanneum.android.R;
import at.fhjoanneum.android.WorkSession;
import at.fhjoanneum.android.data.DatabaseHandler;

public class CalendarListAdapter extends ListViewAdapter {
	
	private Context 					context;
	private List<Map<String, String>>	eventList;
	private final int					layoutId;
	private DatabaseHandler db;
	
	public CalendarListAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
		this.layoutId = resource;
		this.eventList	  = (List<Map<String, String>>) data;
		db = new DatabaseHandler(context);
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View events = inflater.inflate(layoutId, parent, false);

		TextView eventNameField  = (TextView) events.findViewById(R.id.event_name);
		TextView eventStartField = (TextView) events.findViewById(R.id.event_start);
		
		String eventName = eventList.get(position).get("event_name");
		eventNameField.setText(eventName);
		
		String eventStart = eventList.get(position).get("event_start");
		eventStartField.setText(eventStart);
		
		
		
		
		return events;
	}
	
}
