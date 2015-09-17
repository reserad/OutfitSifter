package com.example.outfitsifter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Toast;

public class settings extends PreferenceActivity {
	Context context = this;
	String zip = "";
	String UserName = "";
	@SuppressWarnings("deprecation")
	@Override

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setTitle("Settings");
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		UserName = sharedPrefs.getString(UserName+"CurrentUser", null);
		
		PreferenceScreen prefScreen = getPreferenceManager().createPreferenceScreen(this);
		
		PreferenceCategory personal = new PreferenceCategory(this);
		personal.setTitle("Personal Settings");
		prefScreen.addPreference(personal);
		CharSequence[] genderValues = new String[] { "Male", "Female"};
		    ListPreference gender = new ListPreference(this);
		    gender.setTitle("Gender");
		    gender.setKey(UserName+"gender");
		    gender.setEntries(genderValues);
		    gender.setEntryValues(genderValues);
		    
		    EditTextPreference profile = new EditTextPreference(this);
		    profile.setTitle(UserName + "'s Profile Picture");
		    profile.setKey(UserName+"pic");		    
		    personal.addPreference(gender);
		    personal.addPreference(profile);
		    
		    profile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

                public boolean onPreferenceClick(Preference preference) 
                {
                	Intent intent = new Intent(context, cam_profile_pic.class);
        			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        			startActivity(intent);
                    return false;
                }

            });
		    
		PreferenceCategory weather = new PreferenceCategory(this);
		weather.setTitle("Weather Settings");
		prefScreen.addPreference(weather);
			    EditTextPreference Zip = new EditTextPreference(this);
			    Zip.setTitle("Zip Code");
			    Zip.setKey(UserName+"Zip");
			    weather.addPreference(Zip);
			    
			    CheckBoxPreference units = new CheckBoxPreference(this);
			    units.setTitle("Temperature Units");
			    units.setKey(UserName+"Units");
			    units.setSummary("Specifies (Celsius)");
			    units.setDefaultValue(false);
			    weather.addPreference(units);
			    
		PreferenceCategory Suggestions = new PreferenceCategory(this);
		Suggestions.setTitle("Suggestion Settings");
		prefScreen.addPreference(Suggestions);
			CharSequence[] temps = new String[] { "55 F (12.8 C)", "60 F (15.6 C)", "65 F (18.3 C)", "70 F (21.1 C)" };
			CharSequence[] tempValues = new String[] { "55", "60", "65", "70" };
			 	ListPreference shorts = new ListPreference(this);
			 	shorts.setTitle("Minimum temperature for shorts");
			 	shorts.setKey(UserName+"tempShorts");
			 	shorts.setEntries(temps);
			 	shorts.setEntryValues(tempValues);
			 	Suggestions.addPreference(shorts);
			    
			 	ListPreference shirts = new ListPreference(this);
			 	shirts.setTitle("Minimum temperature for t-shirts");
			 	shirts.setKey(UserName+"tempTShirt");
			 	shirts.setEntries(temps);
			 	shirts.setEntryValues(tempValues);
			 	Suggestions.addPreference(shirts);
		PreferenceCategory Work = new PreferenceCategory(this);
		Work.setTitle("Work Schedule");
		prefScreen.addPreference(Work);	    
				CheckBoxPreference monday = new CheckBoxPreference(this);
				monday.setTitle("Monday");
				monday.setKey(UserName+"Monday");
				monday.setDefaultValue(false);
				Work.addPreference(monday);
				
				CheckBoxPreference tuesday = new CheckBoxPreference(this);
				tuesday.setTitle("Tuesday");
				tuesday.setKey(UserName+"Tuesday");
				tuesday.setDefaultValue(false);
				Work.addPreference(tuesday);
				
				CheckBoxPreference Wednesday = new CheckBoxPreference(this);
				Wednesday.setTitle("Wednesday");
				Wednesday.setKey(UserName+"Wednesday");
				Wednesday.setDefaultValue(false);
				Work.addPreference(Wednesday);				
				
				CheckBoxPreference thursday = new CheckBoxPreference(this);
				thursday.setTitle("Thursday");
				thursday.setKey(UserName+"Thursday");
				thursday.setDefaultValue(false);
				Work.addPreference(thursday);				
				
				CheckBoxPreference friday = new CheckBoxPreference(this);
				friday.setTitle("Friday");
				friday.setKey(UserName+"Friday");
				friday.setDefaultValue(false);
				Work.addPreference(friday);					

				CheckBoxPreference saturday = new CheckBoxPreference(this);
				saturday.setTitle("Saturday");
				saturday.setKey(UserName+"Saturday");
				saturday.setDefaultValue(false);
				Work.addPreference(saturday);
				
				CheckBoxPreference sunday = new CheckBoxPreference(this);
				sunday.setTitle("Sunday");
				sunday.setKey(UserName+"Sunday");
				sunday.setDefaultValue(false);
				Work.addPreference(sunday);
				
			    setPreferenceScreen(prefScreen);
	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			if(sharedPrefs.getString(UserName+"Zip", "").length() != 5 || !sharedPrefs.contains(UserName + "tempShorts") || !sharedPrefs.contains(UserName + "tempTShirt") || !sharedPrefs.contains(UserName + "gender"))
			{
				Toast.makeText(context, "Please supply your gender, zip code, weather preferences for shorts & T-shirts.", Toast.LENGTH_LONG).show();
				return false;
			}
			else
			{
				Intent intent = new Intent(context, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}