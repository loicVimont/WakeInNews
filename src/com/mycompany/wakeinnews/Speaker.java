package com.mycompany.wakeinnews;

import android.speech.tts.TextToSpeech;
import android.content.*;
import java.util.*;
import android.widget.*;
import android.app.*;
import android.test.*;

public class Speaker implements TextToSpeech.OnInitListener
{
    private static TextToSpeech tts; 
    private Context context;
    private boolean isNight, stopped;

    private ArrayList<String> textes;
	private String weather;
	private String event;
    private int count;
	private Date dt;
    private boolean reading=false;

    public Speaker(Context context){
	this.context = context;
	isNight = false; 
	stopped = false;
	count = 0;
	dt = new Date();
	
		
	tts = new TextToSpeech(context, this);
	String engine = tts.getDefaultEngine();
	tts = new TextToSpeech(context, this, engine);
    }
    
    @Override
    public void onInit(int status) {
		
		if(status != TextToSpeech.ERROR){
	   		if(tts.isLanguageAvailable(Locale.FRANCE) == TextToSpeech.LANG_COUNTRY_AVAILABLE){
				tts.setLanguage(Locale.FRANCE);   
	    	}
	    	else tts.setLanguage(Locale.ENGLISH);
		}
		//if(status == TextToSpeech.SUCCESS){
			//read();
		//}
    }
    
	public void setTextes(ArrayList<String> textes){
		this.textes = textes;
	}
	
	public void setWeather(String weather){
		this.weather = weather;
	}
	
	public void setNextEvent(String event){
		this.event = event;
	}
	
    public void read(){
	
		if(!reading){
			reading = true;
			String  day = getDay();
			String time = getTimeString();
			pause(20000);
			if(!isNight)
	    		speak("Bonjour !!!");
			else speak("Bonsoir !!!");
			speak("Nous sommes le "+ day +", et il est, "+ time+".");
			pause(500);
			
			speak(weather);
			pause(500);
			
			speak(event);
			pause(500);
			
			speak( "Voici maintenant les dernières nouvelles :");
			pause(500);
			readTitles();
			speak("Voilà, c'est tout pour l'instant !!! "+
				"Passez une excellente journée, et n'oubliez pas");
			pause(500);
			speak("si vous le voulez");
			pause(300);
			speak("vous réussirez !!!");
			reading = false;
		}
		else{
	    	stop();
		}
    }
    
    private void readTitles(){
		//Toast.makeText(context,"*****read titles: quantity="+ textes.size(),Toast.LENGTH_SHORT).show();
		for(int i=0;i<16;i++){
	    	speak(textes.get(i));
	    	pause(700);
	    
	    	if(stopped)
				break;
		}
    }
    
	public void speak(String text){
		//Toast.makeText(context,"*****Into Speak !!! ",Toast.LENGTH_SHORT).show();
	tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }
    public void pause(int duration){
	
	tts.playSilence(duration, TextToSpeech.QUEUE_ADD,null);
    }
    
    private String getTimeString(){

	if(dt.getHours() > 16) isNight = true;
	return dt.getHours()+" heures "+dt.getMinutes()+".";

    }
    private String getDay(){
		Calendar c = Calendar.getInstance(Locale.FRANCE);
		return c.get(Calendar.DATE) +" "+ c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRANCE);
	}
    public void resume(){
	
	if(count>0){
	    stopped = false;
	    readTitles();
	}    
    }
    public void stop(){
	
	if(tts !=null){
	    tts.stop();
	    stopped = true;
	    
	}
    }
    
    public void destroy(){
	
	if(tts !=null){
	    tts.stop();
		stopped = true;
        tts.shutdown();
	}
    }
}
