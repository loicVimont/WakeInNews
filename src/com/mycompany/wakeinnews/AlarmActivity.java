package com.mycompany.wakeinnews;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.*;
import android.os.*;
import android.widget.*;
import android.content.*;
import android.content.pm.*;
import android.view.*;
import android.graphics.*;

public class AlarmActivity extends Activity
{
	
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private ToggleButton toggle;
	private TextView alarmText;
	private Intent myIntent; 
	private Button close;
	private long time;
	private String activee;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
		alarmTimePicker.setIs24HourView(true);
		alarmText = (TextView) findViewById(R.id.alarmText);
		toggle = (ToggleButton) findViewById(R.id.alarmToggle);
		toggle.setBackgroundColor(Color.BLUE);
		close = (Button) findViewById(R.id.close);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		//if(savedInstanceState.getString("alarmTime","pas d'alarme active") != null)
			//activee = savedInstanceState.getString("alarmTime","pas d'alarme active");
    	if(activee != null){
			alarmText.setText(activee);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		
		super.onSaveInstanceState(outState);
		outState.putString("alarmTime",String.valueOf(time));
	}
	
	

    public void onToggleClicked(View view)
    {
		if (((ToggleButton) view).isChecked())
		{
	   	 	Log.i("WakeInNews", "Alarm On");
			toggle.setBackgroundColor(Color.CYAN);
	    	Calendar now = Calendar.getInstance();
			Calendar calendar = (Calendar) Calendar.getInstance().clone();
			
	    	calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
	    	calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
	  		
			if(calendar.compareTo(now) < 0){
			// time is passed, set for tomorrow
				calendar.add(Calendar.DATE, 1);
			}
			time = calendar.getTimeInMillis();
			activee = calendar.getTime().toString();
			alarmText.setText(activee);
			myIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);
	    	pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 123, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	    	alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
			//alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(time, pendingIntent), pendingIntent);
			
//			Intent alarmChanged = new Intent("android.intent.action.ALARM_CHANGED");
//			alarmChanged.putExtra("alarmSet",true);
//			sendBroadcast(alarmChanged);
		}
		else
		{
	    	alarmManager.cancel(pendingIntent);
			toggle.setBackgroundColor(Color.BLUE);
			//alarmManager.getNextAlarmClock();
			//PendingIntent.getBroadcast(AlarmActivity.this, 123, myIntent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
	    	
//			Intent alarmCancelled = new Intent("android.intent.action.ALARM_CHANGED");
//			alarmCancelled.putExtra("alarmSet",false);
//			sendBroadcast(alarmCancelled);
		}
    }
	
	public void close(View v){
		finish();
	}
}
