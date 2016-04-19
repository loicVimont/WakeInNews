package com.mycompany.wakeinnews;

import android.content.*;
import android.database.*;
import android.net.*;
import java.text.*;
import java.util.*;

public class CalendarUtility {

	public static String event;
	public static long startDate;
	
	public static String getNextCalendarEvent(Context context) {
		
		Cursor cursor = context.getContentResolver()
            .query(
			Uri.parse("content://com.android.calendar/events"),
			new String[] { "calendar_id", "title", "description",
				"dtstart", "dtend", "eventLocation" }, null,
			null, null);
		cursor.moveToFirst();
		
		Calendar c = Calendar.getInstance(Locale.FRANCE);
		Long now = c.getTimeInMillis();
		while(!cursor.isLast()){
			startDate = Long.parseLong(cursor.getString(3));
			//if event after now
			if(now <= startDate){
				// if event come into 20 hours
				//if((startDate - now) < 100000000){
				event = "Prochain événement : " +cursor.getString(1);
				event += ", le " + getDate(startDate) + " à " + getHour(startDate);
				return event;
				//}
			}
			cursor.moveToNext();
		}
		return null;
	}
	
	public static String getDate(long milliSeconds) {
		
		SimpleDateFormat formatter = new SimpleDateFormat(
            "EEEE dd MMMM yyyy", Locale.FRANCE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
	
	public static String getHour(long milliSeconds) {

		SimpleDateFormat formatter = new SimpleDateFormat(
            "kk:mm", Locale.FRANCE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
}
