package com.example.outfitsifter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.GestureDetector; 
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem; 
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {
	Context context = this;
	String zip = "";
	ProgressDialog pd;
	String isItCelsius = "";
	GestureDetector detector;
	SharedPreferences sharedPrefs;
	boolean proceed = false;
	String gender = "";
	String result = "";
	String UserName = "";
	File settings = new File (Environment.getExternalStorageDirectory() + "/OutfitSifter/settings.txt");
	public boolean onCreateOptionsMenu(Menu menu) {  
		// Inflate the menu; this adds items to the action bar if it is present.  
		getMenuInflater().inflate(R.menu.home, menu);//Menu Resource, Menu  
		return true;  
	}  

	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int option = item.getItemId();
		if (option == R.id.item1)
		{
			try
			{
				zip = sharedPrefs.getString(UserName+"Zip", "");
				grabURL("http://api.wunderground.com/api/285164b0af259d47/conditions/q/"+ zip + ".json");
			}
			catch(Exception e)
			{

			}
		}
		if(option == R.id.item2)
		{
			Intent intent = new Intent(context, settings.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition( R.layout.slide_up, R.layout.slide_down );
		}

		if(option == R.id.item3)
		{
			Toast.makeText(context, "Outfit Sifter created by Alec Reser. Using Wunderground's free API.", Toast.LENGTH_LONG).show();
		}

		if(option == R.id.item4)
		{
			LayoutInflater li = LayoutInflater.from(context);
			View promptsView = li.inflate(R.layout.add_user_dialog, null);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setView(promptsView);

			final EditText userInput = (EditText) promptsView.findViewById(R.id.txtUser);
			alertDialogBuilder
			.setCancelable(false)
			.setNegativeButton("Add",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) 
				{
					result = userInput.getText().toString();
					TinyDB tinydb = new TinyDB(context);
					ArrayList<String> currentUsers = tinydb.getList("users");
					if(result.length() > 0)
					{
						if(result.charAt(0) != ' ')
						{
						currentUsers.add(result);
						tinydb.putList("users", currentUsers);
						createFolders();
						}
					}
				}
			})
			.setPositiveButton("Cancel",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) 
				{
					dialog.cancel();
				}
			});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}

		if(option == R.id.item5)
		{
			TinyDB tinydb = new TinyDB(context);
			ArrayList<String> currentUsers = tinydb.getList("users");

			LayoutInflater li = LayoutInflater.from(context);
			View promptsView = li.inflate(R.layout.select_user_dialog, null);
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);

			alertDialogBuilder.setView(promptsView);

			final Spinner selected_user = (Spinner)promptsView.findViewById(R.id.SelectUser);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, currentUsers);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			selected_user.setAdapter(adapter);
			selected_user.setSelection(adapter.getPosition(UserName));
			alertDialogBuilder
			.setCancelable(false)
			.setNegativeButton("Select",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) 
				{

					UserName = selected_user.getSelectedItem().toString();
					SharedPreferences prefs = PreferenceManager  
							.getDefaultSharedPreferences(context);  
					Editor edit = prefs.edit();  

					edit.putString("CurrentUser", UserName);
					edit.commit();
					changeActionBarIcon();
					String[] textArray = new String[2];
					File lastWeather = new File (Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/lastWeather.txt");
					if (lastWeather.exists())
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

						}
						catch(Exception e)
						{
							Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
						}
						Boolean units = sharedPrefs.getBoolean("Units", false);
						if(units)
						{
							Double newTemp = (Double.parseDouble(textArray[0]) - 32.0) * (5.0/9.0);
							DecimalFormat df = new DecimalFormat("#.##");
							File picLocation = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/Profile_Picture.png");
							
							if(picLocation.exists())
							{
								getActionBar().setTitle(" " + df.format(newTemp) + " \u00b0 C - " + textArray[1]);
							}
							else
							getActionBar().setTitle(UserName + " - " + df.format(newTemp) + " \u00b0 C - " + textArray[1]);
						}
						else
						{
							File picLocation = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/Profile_Picture.png");
							
							if(picLocation.exists())
							{
								getActionBar().setTitle(" " + textArray[0] + " \u00b0 F - " + textArray[1]);
							}
							else
							getActionBar().setTitle(UserName + " - " + textArray[0] + " \u00b0 F - " + textArray[1]);
						}
					}
					else
					{

						grabURL("http://api.wunderground.com/api/285164b0af259d47/conditions/q/"+ zip + ".json");
					}
				}
			})
			.setPositiveButton("Cancel",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
		return true;  
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		UserName = sharedPrefs.getString(UserName+"CurrentUser", null);
		zip = sharedPrefs.getString(UserName+"Zip", "");
		setContentView(R.layout.activity_main);
		changeActionBarIcon();
		getActionBar().setDisplayShowHomeEnabled(false);
        File f = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/");
        if(!f.exists())
        {
            f.mkdir();
        }
		
		try 
		{
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) 
			{
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) 
		{
			// Ignore
		}

		if(sharedPrefs.getString(UserName+"gender", "").equals("Female"))
		{
			gender = "Female";
		}
		if(sharedPrefs.getString(UserName+"gender", "").equals("Male"))
		{
			gender = "Male";
		}

		if(sharedPrefs.getString(UserName+"Zip", "").length() != 5 || !sharedPrefs.contains(UserName+"tempShorts") || !sharedPrefs.contains(UserName+"tempTShirt"))
		{
			Intent intent = new Intent(context, settings.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

			if(!sharedPrefs.contains("Tutorial"))
			{
				Intent intent2 = new Intent(context, tutorial.class);
				intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent2);
			}

		}
		else
		{
			Boolean units = sharedPrefs.getBoolean(UserName + "Units", false);

			File firstFolder = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/");
			if (!firstFolder.exists())
			{
				firstFolder.mkdir();
			}

			final Button closet = (Button)findViewById(R.id.btnCloset);
			final Button outfit = (Button)findViewById(R.id.btnMake);
			final Button suggestion = (Button)findViewById(R.id.btnSuggestion);
			String[] textArray = new String[2];
			File refreshlocation = new File (Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/lastRefresh.txt");
			File lastWeather = new File (Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/lastWeather.txt");
			createFolders();

			try
			{
				Calendar c2 = Calendar.getInstance();
				long now = c2.getTimeInMillis();
				c2.set(Calendar.HOUR_OF_DAY, 0);
				c2.set(Calendar.MINUTE, 0);
				c2.set(Calendar.SECOND, 0);
				long passed = now - c2.getTimeInMillis();
				long currentTime = passed / 1000;

				float recordedTime = 0;
				Scanner refresh = new Scanner(refreshlocation);
				while (refresh.hasNextLine()) 
				{
					recordedTime = refresh.nextFloat();
				}
				refresh.close();
				float diff = Math.abs(currentTime - recordedTime);

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

				}
				catch(InputMismatchException e)
				{

				}

				if(units)
				{
					Double newTemp = (Double.parseDouble(textArray[0]) - 32.0) * (5.0/9.0);
					DecimalFormat df = new DecimalFormat("#.##");
					File picLocation = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/Profile_Picture.png");
					if(picLocation.exists())
					{
						getActionBar().setTitle(" " + df.format(newTemp) + " \u00b0 C - " + textArray[1]);
					}
					else
					{
					getActionBar().setTitle(UserName + " - " + df.format(newTemp) + " \u00b0 C - " + textArray[1]);
					}
				}
				else
				{
					File picLocation = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/Profile_Picture.png");
				
					if(picLocation.exists())
					{
						getActionBar().setTitle(" " + textArray[0] + " \u00b0 F - " + textArray[1]);
					}
					else
					{
					getActionBar().setTitle(UserName + " - " + textArray[0] + " \u00b0 F - " + textArray[1]);
					}
				}
				if(textArray[1].contains("Clear") || textArray[1].contains("Cloud"))
				{
					LinearLayout layout =(LinearLayout)findViewById(R.id.background);
					layout.setBackgroundResource(R.drawable.clear2);
				}

				if(textArray[1].contains("Rain") || textArray[1].contains("Storm") || textArray[1].contains("Fog"))
				{
					LinearLayout layout =(LinearLayout)findViewById(R.id.background);
					layout.setBackgroundResource(R.drawable.storm);
				}

				if(textArray[1].contains("Overcast"))
				{
					LinearLayout layout =(LinearLayout)findViewById(R.id.background);
					layout.setBackgroundResource(R.drawable.overcast);
				}

				if(diff > 3600)
				{
					grabURL("http://api.wunderground.com/api/285164b0af259d47/conditions/q/"+ zip + ".json");
				}
			}
			catch(Exception e)
			{

			}

			outfit.setOnClickListener(new Button.OnClickListener()
			{
				public void onClick(View arg0) 
				{
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
					alertDialogBuilder.setTitle("Would you like to take pictures of your clothing for later use?");

					alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {

							if(sharedPrefs.getString(UserName+"gender", "").equals("Female"))
							{
								Intent intent = new Intent(context, type_selection_girls.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent); 
							}
							else
							{
								Intent intent = new Intent(context, type_selection.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent); 
							}
						}
					})
					.setNegativeButton("No",new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog,int id) {

                            ArrayList<File> myShirts = new ArrayList<File>();
                            ArrayList<File> myBottoms = new ArrayList<File>();
                            ArrayList<File> myShoes = new ArrayList<File>();
//-----------------------------------------------
                            //SHIRTS
                            for(File item: new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/work/shirts/").listFiles())
                            {
                                myShirts.add(item);
                            }
                            for(File item: new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/shirts/").listFiles())
                            {
                                myShirts.add(item);
                            }
                            for(File item: new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/Lshirts/").listFiles())
                            {
                                myShirts.add(item);
                            }
//-----------------------------------------------
                            //PANTS
                            for(File item: new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/work/pants/").listFiles())
                            {
                                myBottoms.add(item);
                            }
                            for(File item: new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/pants/").listFiles())
                            {
                                myBottoms.add(item);
                            }
                            for(File item: new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/shorts/").listFiles())
                            {
                                myBottoms.add(item);
                            }
//-----------------------------------------------
                            //SHOES
                            for(File item: new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/shoes/").listFiles())
                            {
                                myShoes.add(item);
                            }
                            for(File item: new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/work/shoes/").listFiles())
                            {
                                myShoes.add(item);
                            }
//-----------------------------------------------
							if(myShirts.size() > 0 && myShoes.size() > 0 && myBottoms.size() > 0)
							{
								Intent intent = new Intent(context, make_outfit.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
							}
							else
							{
								Toast.makeText(context, "Insufficient number of clothing items found!", Toast.LENGTH_SHORT).show();
							}
						}
					});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();

				}});

			closet.setOnClickListener(new Button.OnClickListener()
			{
				public void onClick(View arg0) 
				{
					File closetFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/closet/");

					if (closetFile.exists())
					{
						File file[] = closetFile.listFiles();
						if(file.length > 0)
						{
							Intent intent = new Intent(context, closet.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
						else
						{
							Toast.makeText(context, "No outfits found!", Toast.LENGTH_SHORT).show();
						}
					}
					else
					{
						Toast.makeText(context, "No outfits found!", Toast.LENGTH_SHORT).show();
					}

				}});

			suggestion.setOnClickListener(new Button.OnClickListener()
			{
				public void onClick(View arg0) 
				{

					File shortsFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/shorts/");
					File shoesFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/shoes/");
					File topFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/shirts/");
					File LongSleeveFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/Lshirts/");
					File pantsFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/pants/");

					int pantsSummation = shortsFile.listFiles().length + pantsFile.listFiles().length;
					int topsSummation = topFile.listFiles().length + LongSleeveFile.listFiles().length;

					if(pantsSummation < 1 || topsSummation < 1 || shoesFile.listFiles().length < 1)
					{
						Toast.makeText(context, "Not enough clothing items to suggest anything.", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Intent intent = new Intent(context, Suggestion.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				}
			});

		}
	}
	
	public void grabURL(String url) {
		zip = sharedPrefs.getString(UserName+"Zip", "");
		url = "http://api.wunderground.com/api/285164b0af259d47/conditions/q/"+ zip + ".json";
		
		if(zip.length() == 5 && isOnline())
		{
			try{
				new GrabURL().execute(url);
			}
			catch(Exception e)
			{
				//Toast.makeText(context, "Ensure a connection to the internet exists.", Toast.LENGTH_SHORT).show();
			}
		}
		else if(zip.length() < 5 && isOnline())
		{
			Toast.makeText(context, "Enter a correct zip in the settings.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, settings.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition( R.layout.slide_up, R.layout.slide_down );
		}
		else if(!isOnline())
		{
			Toast.makeText(context, "Ensure a connection to the internet exists.", Toast.LENGTH_SHORT).show();
		}
	}

	protected void onDestroy() { if (pd!=null) { pd.dismiss(); } super.onDestroy(); }

	private class GrabURL extends AsyncTask<String, Void, Void> {
		private String Content;
		private String Content2;
		private String Error = null;

		protected void onPreExecute() {

			pd = new ProgressDialog(context);
			pd.setTitle("Collecting Weather Information..."); 
			pd.setMessage("Please wait."); 
			pd.setCancelable(false); 
			pd.setIndeterminate(true);
			pd.show();

		}

		protected Void doInBackground(String... urls) {
			try 
			{
				HttpGet httpget = new HttpGet(urls[0]);
				HttpGet forecast = new HttpGet("http://api.wunderground.com/api/285164b0af259d47/forecast/q/" + zip + ".json");
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				
				HttpParams httpParameters = new BasicHttpParams();
				// Set the timeout in milliseconds until a connection is established.
				// The default value is zero, that means the timeout is not used. 
				int timeoutConnection = 10000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				// Set the default socket timeout (SO_TIMEOUT) 
				// in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 10000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

				DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
				httpClient.setParams(httpParameters);
				
				Content = httpClient.execute(httpget, responseHandler);
				Content2 = httpClient.execute(forecast, responseHandler);
				proceed = true;
			}
			catch (Exception e) 
			{
				//Toast.makeText(getApplicationContext(), "Invalid Zip Code", Toast.LENGTH_SHORT).show();
			}

			return null;
		}

		protected void onPostExecute(Void unused) {

			if(proceed == true)
            {
				String searchForDegrees = "temp_f";
				String searchConditions = "\"weather\":";
				String searchForecast = "\"icon\":\"chancetstorms\"";

				Date dt = new Date();
				@SuppressWarnings("deprecation")
				int day = dt.getDay();
				String[] days = { "Sunday", "Monday", "Tuesday","Wednesday", "Thursday", "Friday", "Saturday" };

				String weekDay = days[day];

				if (Error != null)
                {
					Toast.makeText(MainActivity.this, Error, Toast.LENGTH_LONG).show();
				}
                else
                {
					Scanner in = new Scanner(Content);
					Scanner forecastIn = new Scanner(Content2); 
					String conditions ="";
					String isThereRainToday = "";
					String temperature = "";

					int count = 0;
					String title = "";
					while(forecastIn.hasNext() && count == 0)
                    {
						String data = forecastIn.next();
						if(data.indexOf(searchForecast) != -1 || data.indexOf("\"icon\":\"tstorms\"") != -1)
                        {
							title = forecastIn.next();
							title = forecastIn.next();
							count++;
						}

					}
					forecastIn.close();

					File laterConditions = new File (Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/nextstorm.txt");

					if (title.length() > 0)
                    {
						String temp = title;
						isThereRainToday = title.substring(9, temp.length()-2);
						if(isThereRainToday.contains(weekDay))
                        {
							if(laterConditions.exists())
                            {
								laterConditions.delete();
							}
							try 
							{
								BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(laterConditions), "US-ASCII"));
								out.write(isThereRainToday + "");
								out.flush();
								out.close();
							} catch (Exception e) 
							{

							}
						}
					}

					while(in.hasNextLine())
					{
						String data = in.nextLine();
						if(data.contains(searchForDegrees))
						{
							temperature = data;
						}

						if(data.contains(searchConditions))
						{
							conditions = data;
						}
					}

					in.close();
					conditions = conditions.substring(13, conditions.length()-2);
					temperature = temperature.substring(11, temperature.length() -1);
					double temp = Double.parseDouble(temperature);

					Boolean units = sharedPrefs.getBoolean(UserName + "Units", false);
					if(units)
					{
						double newcelsTemp = (temp - 32.0) * (5.0/9.0);
						DecimalFormat df = new DecimalFormat("#.##");
						File picLocation = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/Profile_Picture.png");
						
						if(picLocation.exists())
						{
							getActionBar().setTitle(" " + df.format(newcelsTemp) + " \u00b0 C - " + conditions);
						}
						else					
						getActionBar().setTitle(UserName + " - " + df.format(newcelsTemp) + " \u00b0 C - " + conditions);
					}
					else
					{
						File picLocation = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/Profile_Picture.png");
						
						if(picLocation.exists())
						{
							getActionBar().setTitle(" " + temp + " \u00b0 F - " + conditions);
						}
						else
						getActionBar().setTitle(UserName + " - " + temp + " \u00b0 F - " + conditions);
					}

					if(conditions.contains("Clear") || conditions.contains("Cloud"))
					{
						LinearLayout layout =(LinearLayout)findViewById(R.id.background);
						layout.setBackgroundResource(R.drawable.clear2);
					}

					if(conditions.contains("Rain") || conditions.contains("Storm") || conditions.contains("Fog"))
					{
						LinearLayout layout =(LinearLayout)findViewById(R.id.background);
						layout.setBackgroundResource(R.drawable.storm);
					}

					if(conditions.contains("Overcast"))
					{
						LinearLayout layout =(LinearLayout)findViewById(R.id.background);
						layout.setBackgroundResource(R.drawable.overcast);
					}

					conditions.trim();
					File lastKnownWeather = new File (Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/lastWeather.txt");


					try 
					{
						BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(lastKnownWeather), "US-ASCII"));
						out.write(temperature + "");
						out.newLine();
						out.write(conditions);
						out.flush();
						out.close();
					} 
					catch (Exception e) 
					{

					}

					File refresh = new File (Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/lastRefresh.txt");

					final FileOutputStream fos;
					Calendar c2 = Calendar.getInstance();

					long now = c2.getTimeInMillis();
					c2.set(Calendar.HOUR_OF_DAY, 0);
					c2.set(Calendar.MINUTE, 0);
					c2.set(Calendar.SECOND, 0);
					long passed = now - c2.getTimeInMillis();
					long time = passed / 1000;

					try 
					{
						fos = new FileOutputStream(refresh, false);
						String sTime = time + "";
						fos.write(sTime.getBytes());
						fos.flush();
						fos.close();
					} 
					catch (Exception e) 
					{

					}
				}
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Invalid Zip Code", Toast.LENGTH_SHORT).show();	
			}	
			pd.dismiss();
		}
	}

	public void createFolders()
	{
        File mainDir = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/");
        if(!mainDir.exists())
        {
            mainDir.mkdir();
        }
        ArrayList<File> dirs = new ArrayList<File>();
        dirs.add(new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/shirts/"));
        dirs.add(new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/pants/"));
        dirs.add(new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/shorts/"));
        dirs.add(new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/Lshirts/"));
        dirs.add(new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/shoes/"));
        dirs.add(new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/work/"));
        dirs.add(new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/work/shirts/"));
        dirs.add(new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/work/pants/"));
        dirs.add(new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/work/shorts/"));
        dirs.add(new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/work/Lshirts/"));
        dirs.add(new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/work/shoes/"));

        for(File item: dirs)
        {
            if(!item.exists())
            {
                item.mkdir();
            }
        }
	}

	public boolean isOnline() {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) 
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
  }
	
	public void changeActionBarIcon()
	{
		ActionBar actionBar = getActionBar();
		
		File picLocation = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/Profile_Picture.png");
		if(picLocation.exists())
		{
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x / 7;
			int height = width;
			ImageView profPic = (ImageView)findViewById(R.id.ProfilePic);
			
			Bitmap profilePic = BitmapFactory.decodeFile(picLocation.getPath());
			profPic.setImageBitmap(profilePic);
			profPic.getLayoutParams().height= (int) Math.round(height * 1.35);
			profPic.getLayoutParams().width= width;
		}
		else
		{
			ImageView profPic = (ImageView)findViewById(R.id.ProfilePic);
			profPic.setImageResource(R.drawable.ic_action_user);
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x / 7;
			int height = width;
			profPic.getLayoutParams().height= height;
			profPic.getLayoutParams().width= width;
		}
	}
	

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);

			// set title
			alertDialogBuilder.setTitle("Are you sure you wish to exit?");

			// set dialog message
			alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				@SuppressLint("InlinedApi")
				public void onClick(DialogInterface dialog,int id) {
					finish(); 
				}
			})
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}