package com.mycompany.wakeinnews;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.content.BroadcastReceiver;
import android.widget.*;
import android.os.*;
import android.app.*;
import android.util.*;
import java.util.*;


public class AlarmReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(final Context context, Intent intent) {
 
		Intent wakeIntent= new Intent(context, WakeActivity.class);
        wakeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(wakeIntent);
    }
        
}
