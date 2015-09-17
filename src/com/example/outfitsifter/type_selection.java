package com.example.outfitsifter;

import java.io.File;
import java.util.Scanner;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

	public class type_selection extends Activity {
		Context context = this;
		String UserName = "";
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setTitle("Clothing Type Cameras");
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		UserName = sharedPrefs.getString(UserName+"CurrentUser", null);
		
	    setContentView(R.layout.selection_type);
	    Button tops = (Button)findViewById(R.id.tops);
		Button bottoms = (Button)findViewById(R.id.bottoms);
		Button shoes = (Button)findViewById(R.id.shoes);
		
		String[] textArray = new String[2];
		File lastWeather = new File (Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/lastWeather.txt");
		if(lastWeather.exists())
        {
            try
            {
                Scanner weather = new Scanner(lastWeather);
                int a = 0;
                while (weather.hasNextLine())
                {
                    textArray[a] = weather.nextLine();
                    a++;
                }
                weather.close();

                if(textArray[1].indexOf("Clear") != -1 || textArray[1].indexOf("Cloud") != -1)
                {
                    LinearLayout layout =(LinearLayout)findViewById(R.id.background);
                    layout.setBackgroundResource(R.drawable.clear2);
                }

                if(textArray[1].indexOf("Rain") != -1 || textArray[1].indexOf("Storm") != -1)
                {
                    LinearLayout layout =(LinearLayout)findViewById(R.id.background);
                    layout.setBackgroundResource(R.drawable.storm);
                }

                if(textArray[1].indexOf("Overcast")!= -1)
                {
                    LinearLayout layout =(LinearLayout)findViewById(R.id.background);
                    layout.setBackgroundResource(R.drawable.overcast);
                }

            } catch (Exception e)
            {

            }
        }
		
		tops.setOnClickListener(new Button.OnClickListener(){
			 public void onClick(View arg0) {
				 Intent intent = new Intent(context, cam_shirt.class);
				 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		          startActivity(intent); 
		          finish();
				 
			 }});
		
		bottoms.setOnClickListener(new Button.OnClickListener(){
			 public void onClick(View arg0) {
				 Intent intent = new Intent(context, cam_pants.class);
				 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		          startActivity(intent); 
		          finish();
				 
			 }});
		
		shoes.setOnClickListener(new Button.OnClickListener(){
			 public void onClick(View arg0) {
				 Intent intent = new Intent(context, cam_shoes.class);
				 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		          startActivity(intent); 
				 finish();
			 }});
	}
	
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
		    if (keyCode == KeyEvent.KEYCODE_BACK) {
		    	
		    	Intent intent = new Intent(context, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
							   
		        return true;
		    }
		    return super.onKeyDown(keyCode, event);
	  }
	}