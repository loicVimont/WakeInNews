package com.mycompany.wakeinnews;
import android.app.*;
import android.os.*;
import java.util.*;
import android.widget.*;
import android.view.*;
import android.content.*;
import android.widget.LinearLayout.*;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.content.pm.*;
import android.media.*;
import org.json.*;
import android.util.*;
import android.net.wifi.*;
import android.provider.*;
import android.net.*;

public class WakeActivity extends Activity 
{

	private static final String FLUX="http://rss.lemonde.fr/c/205/f/3050/index.rss";
	private static final String CITY = "Paris";
	private GetRSSDataTask rssTask;
	private GetOpenWeatherTask weatherTask;
	private OpenWifiTask wifitask;
	private Speaker speaker;
	private MediaPlayer player;
	private PowerManager pm;
	private Window win;
	private PowerManager.WakeLock wl;
	private WifiManager wifiManager;
	
	private String event;

	private String city;
	private int weatherCode;
	private String description;
	private long sunriseTime;
	private long sunsetTime;
	private String temperature;
	private String weather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
//		isFlightModeEnabled = Settings.System.getInt(
//						this.getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 0) == 1;
//		if(isFlightModeEnabled)
//			Settings.System.putInt(
//				getContentResolver(),Settings.System.AIRPLANE_MODE_ON,1);
//		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);	
//		intent.putExtra("state", true);										
//		sendBroadcast(intent);
				
		win = this.getWindow();
		win.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD); 
		win.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        win.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
		win.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.wake);
	
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"wake");
		wl.acquire();
		weather = "";
		speaker = new Speaker(this);
		wifitask = new OpenWifiTask();
		wifitask.execute();
		
		event = CalendarUtility.getNextCalendarEvent(this);
		
		Toast.makeText(this, event, Toast.LENGTH_LONG).show();
	}
	
	public void startConnections(){
		
		weatherTask = new GetOpenWeatherTask();
		weatherTask.execute(CITY);
		rssTask = new GetRSSDataTask();
		rssTask.execute(FLUX);	
	}
	
	public void onTaskCompleted(ArrayList<String> titles){
		
		speaker.setTextes(titles);
		speaker.setWeather(weather);
		speaker.setNextEvent(event);
		player = MediaPlayer.create(this,R.raw.time);
		player.start();
		speaker.read();
			
    }
	
	public void stopReading(View v){
		
		if(speaker!=null)
	    	speaker.stop();
			speaker.destroy();
			player.stop();
			player.release();
			if(wl.isHeld())
				wl.release();
			finish();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if(speaker!=null)
			speaker.resume();
	}  

	@Override
	public void onPause()
	{
		super.onPause();
		if(speaker!=null)
	    	speaker.stop();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(speaker!=null)
			speaker.destroy();
		
		if(player!= null)
			player.release();
		if(wl.isHeld())
			wl.release();
	}
	
	
	private void renderWeather(JSONObject json){
		
		try {
			city = json.getString("name");
			JSONObject main = json.getJSONObject("main");
		
            weatherCode = json.getJSONArray("weather").getJSONObject(0).getInt("id");
			
			if(weatherCode > 199 && weatherCode <300)
					description = "orageux";
			else if(weatherCode > 299 && weatherCode <400)
				description = "bruineux";
			else if(weatherCode > 499 && weatherCode <600)
				description = "pluvieux";
			else if(weatherCode > 599 && weatherCode <700)
				description = "neigeux";
			else if(weatherCode == 800)
				description = "clair";
			else if(weatherCode > 800 && weatherCode <900)
				description = "nuageux";
			
			temperature = String.format("%.0f", main.getDouble("temp"))+ " degrés";
			
			sunriseTime = json.getJSONObject("sys").getLong("sunrise") * 1000;
			sunsetTime = json.getJSONObject("sys").getLong("sunset") * 1000;
			
			weather = "Aujourd'hui, le temps à " + city + " est " + description +
						", et la température est, de " + temperature + ".";
			
			
		}catch(Exception e){
			Log.e("SimpleWeather", "One or more fields not found in the JSON data");
		}
	}
	
	
	private class OpenWifiTask extends AsyncTask<Void,Void,Void>
	{

		@Override
		protected Void doInBackground(Void[] p1)
		{
			wifiManager = (WifiManager) WakeActivity.this.getSystemService(Context.WIFI_SERVICE); 
			wifiManager.setWifiEnabled(true);
			
			while(true){
			ConnectivityManager conM = (ConnectivityManager) WakeActivity.this.getSystemService(
										Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = null;
			if(conM != null){
				netInfo = conM.getActiveNetworkInfo();
			}
			if(netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
				break;
				try
				{
					Thread.sleep(500);
				}
				catch (InterruptedException e)
				{}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			if(wifiManager.isWifiEnabled())
				startConnections();
		}
	}
	
	
	private class GetRSSDataTask extends AsyncTask<String, Void, List<RssItem> >
    {

		@Override
        protected List<RssItem> doInBackground(String... urls) {

            
            try {
                // Create RSS reader
                RssReader rssReader = new RssReader(urls[0]);

                // Parse RSS, get items
                return rssReader.getItems();

            } catch (Exception e) {
				//Toast.makeText(WakeActivity.this, e.getMessage()+" !!!!!!!", Toast.LENGTH_LONG).show();
			}

            return null;
        }

        @Override
        protected void onPostExecute(List<RssItem> result) {


			ArrayList<String> titres = new ArrayList<String>();
	    	for(RssItem item : result){
				titres.add(item.getTitle());
	    	}
			
	    	onTaskCompleted(titres);      
    	}
	}
	
	private class GetOpenWeatherTask extends AsyncTask<String, Void, JSONObject >
    {

		@Override
        protected JSONObject doInBackground(String... city) {
			
			JSONObject json = OpenWeatherConnection.getJSON(WakeActivity.this, city[0]);
			return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {

			//Toast.makeText(WakeActivity.this,"json="+json.toString(),Toast.LENGTH_LONG).show();
			renderWeather(json);
			    
    	}
	}
}
