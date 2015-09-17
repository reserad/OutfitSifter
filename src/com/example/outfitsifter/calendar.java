package com.example.outfitsifter;

import java.io.File;
import java.util.Scanner;

import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class calendar extends Activity{
Context context = this;
	CalendarView calendar;
	String gender = "";
	String UserName = "";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_layout);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setTitle("Outfit History");
		
		 SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		 UserName = sharedPrefs.getString("CurrentUser", null);
		if(sharedPrefs.getString(UserName+"gender", "").equals("Female")){gender = "Female";}
		if(sharedPrefs.getString(UserName+"gender", "").equals("Male")){gender = "Male";}
		
		calendar = (CalendarView)findViewById(R.id.calendar);
		calendar.setOnDateChangeListener(new OnDateChangeListener(){

		@Override
		public void onSelectedDayChange(CalendarView view,
		int year, int month, int dayOfMonth) {
			String monthReplacement = "";
			month++;
		if (month < 10)
			
			monthReplacement = "0" + month;
			
		else
			monthReplacement = month +"";
		
		File data = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/Calendar/" + monthReplacement + "-" + dayOfMonth + "-" + year + ".txt");
		//Toast.makeText(context, Environment.getExternalStorageDirectory() + "/OutfitSifter/Calendar/" + monthReplacement + "-" + dayOfMonth + "-" + year + ".txt" ,Toast.LENGTH_LONG).show();
		String filepath = monthReplacement + "-" + dayOfMonth + "-" + year;
		
		if(data.exists()){
			//Toast.makeText(context, "Here's the outfit you wore that day" ,Toast.LENGTH_SHORT).show();
			int count = 0;
			try{
			
				String results = "";
				Scanner scan = new Scanner(data);
				while (scan.hasNextLine()) {
					results = (scan.next().toString());
					File path = new File(results);
					if(path.exists() == false){
						
						break;
						
					}else{
					count++;}
				}
				scan.close();

			}catch(Exception e){}
			
			if(count > 0){
				
				Intent intent = new Intent(context, showOutfit.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("data", filepath);
				startActivity(intent);
				
			}else{
				data.delete();
				Toast.makeText(context, "A saved outfit worn that day could not be found" ,Toast.LENGTH_SHORT).show();
				
			}
			
			
			
		}else
			Toast.makeText(context, "A saved outfit worn that day could not be found" ,Toast.LENGTH_SHORT).show();
		
		}});
		}
		}