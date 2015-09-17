package com.example.outfitsifter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Suggestion extends Activity {
	Context context = this;
	String tempData = "";
	double temperatureOutside = 0.0;

	ArrayList<Bitmap> lpantsData = new ArrayList<Bitmap>();
	ArrayList<String> lpantsPointer = new ArrayList<String>();

	ArrayList<Bitmap> shortsData = new ArrayList<Bitmap>();
	ArrayList<String> shortsPointer = new ArrayList<String>();

	ArrayList<Bitmap> topData = new ArrayList<Bitmap>();
	ArrayList<String> topPointer = new ArrayList<String>();

	ArrayList<Bitmap> lsleeveData = new ArrayList<Bitmap>();
	ArrayList<String> lsleevePointer = new ArrayList<String>();

	ArrayList<Bitmap> shoeData = new ArrayList<Bitmap>();
	ArrayList<String> shoePointer = new ArrayList<String>();

	ArrayList<Bitmap> newTopData = new ArrayList<Bitmap>();
	ArrayList<String> newTopPointer = new ArrayList<String>();

	ArrayList<Bitmap> newPantsData = new ArrayList<Bitmap>();
	ArrayList<String> newPantsPointer = new ArrayList<String>();

	boolean[] workDays = new boolean[7];
	int day;

	int  tRand = 0;
	int  bRand = 0;
	int  sRand = 0;
	String gender = "";
	String UserName = "";
	
	public boolean onCreateOptionsMenu(Menu menu) {  
		// Inflate the menu; this adds items to the action bar if it is present.  
		getMenuInflater().inflate(R.menu.suggestion, menu);//Menu Resource, Menu  
		return true;  
	}  

	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int option = item.getItemId();
		if (option == R.id.item1)
		{
			getRand();
		}
		return true;  
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggestion_layout);
		Calendar calendar = Calendar.getInstance();
		day = (calendar.get(Calendar.DAY_OF_WEEK)) - 1;

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		UserName = sharedPrefs.getString(UserName+"CurrentUser", null);
		if(sharedPrefs.getString(UserName+"gender", "").equals("Female")){gender = "Female";}
		if(sharedPrefs.getString(UserName+"gender", "").equals("Male")){gender = "Male";}
		
		File shortsFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/shorts/");
		File shoesFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/shoes/");
		File topFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/shirts/");
		File LongSleeveFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/Lshirts/");
		File pantsFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/pants/");
		File lastWeather = new File (Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/lastWeather.txt");
		
		if(sharedPrefs.getString(UserName+"tempShorts", "") == null || sharedPrefs.getString(UserName+"tempTShirt", "") == null)
		{
			Intent intent = new Intent(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			Toast.makeText(context, "Verify your defined temperature settings for t-shirts and shorts!", Toast.LENGTH_SHORT).show();
		}
		int shortsWeather = Integer.parseInt(sharedPrefs.getString(UserName+"tempShorts", ""));
		int tShirts = Integer.parseInt(sharedPrefs.getString(UserName+"tempTShirt", ""));


		workDays[0] = sharedPrefs.getBoolean(UserName+"Sunday", false);
		workDays[1] = sharedPrefs.getBoolean(UserName+"Monday", false);
		workDays[2] = sharedPrefs.getBoolean(UserName+"Tuesday", false);
		workDays[3] = sharedPrefs.getBoolean(UserName+"Wednesday", false);
		workDays[4] = sharedPrefs.getBoolean(UserName+"Thursday", false);
		workDays[5] = sharedPrefs.getBoolean(UserName+"Friday", false);
		workDays[6] = sharedPrefs.getBoolean(UserName+"Saturday", false);


		getActionBar().setDisplayShowHomeEnabled(false);

		final Button save = (Button)findViewById(R.id.btnWear);
		try
		{
			if (lastWeather.exists())
			{
				if(workDays[day] == true)
				{
					Toast.makeText(context, "Work Day!", Toast.LENGTH_SHORT).show();
					//shortsFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/work/shorts/");
					shoesFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/work/shoes/");
					topFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/work/shirts/");
					LongSleeveFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/work/Lshirts/");
					pantsFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/work/pants/");

					File file2[] = pantsFile.listFiles();
					if(file2.length > 0)
					{
						for (int i=0; i < file2.length; i++)
						{
							String tempData = pantsFile.getAbsolutePath().toString() + "/" + file2[i].getName();
							newPantsPointer.add(tempData);
							Bitmap bmp = BitmapFactory.decodeFile(tempData);
							newPantsData.add(bmp); 
						}
					}

					File file1[] = topFile.listFiles();
					if(file1.length > 0){
						for (int i=0; i < file1.length; i++){
							String tempData = topFile.getAbsolutePath().toString() + "/" + file1[i].getName();
							newTopPointer.add(tempData);
							Bitmap bmp = BitmapFactory.decodeFile(tempData);
							newTopData.add(bmp); 

						}
					}

				}


				String[] textArray = new String[2];

				try{

					Scanner weather = new Scanner(lastWeather);

					int a = 0;
					while (weather.hasNextLine()) {
						textArray[a] = weather.nextLine();
						a++;
					}
					weather.close();

				}catch(InputMismatchException e){} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
				}


				temperatureOutside = Double.parseDouble(textArray[0]);



				File location = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/"+ UserName +"/nextstorm.txt");
				try{

					Scanner isThereRain = new Scanner(location);
					String results = "";
					while(isThereRain.hasNext()){

						results = isThereRain.next();

					}

					isThereRain.close();

					if(results.length() > 0){

						getActionBar().setTitle("Suggestions");
						getActionBar().setSubtitle("Chance for storms " + results);
					}else
						getActionBar().setTitle("Suggestions");
				}catch(Exception e){}

				File file5[] = shoesFile.listFiles();
				if(file5.length > 0){
					for (int i=0; i < file5.length; i++){
						String tempData = shoesFile.getAbsolutePath().toString() + "/" + file5[i].getName();
						shoePointer.add(tempData);
						Bitmap bmp = BitmapFactory.decodeFile(tempData);
						shoeData.add(bmp); 

					}
				}

				if (temperatureOutside < tShirts){

					File file2[] = LongSleeveFile.listFiles();
					if(file2.length > 0){
						for (int i=0; i < file2.length; i++){
							String tempData = LongSleeveFile.getAbsolutePath().toString() + "/" + file2[i].getName();
							newTopPointer.add(tempData);
							Bitmap bmp = BitmapFactory.decodeFile(tempData);
							newTopData.add(bmp); 

						}
					}


					File file3[] = pantsFile.listFiles();
					if(file3.length > 0){
						for (int i=0; i < file3.length; i++){
							String tempData = pantsFile.getAbsolutePath().toString() + "/" + file3[i].getName();
							newPantsPointer.add(tempData);
							Bitmap bmp = BitmapFactory.decodeFile(tempData);
							newPantsData.add(bmp); 

						}
					}


				}



				if (temperatureOutside > tShirts && temperatureOutside < shortsWeather){


					File file1[] = topFile.listFiles();
					if(file1.length > 0){
						for (int i=0; i < file1.length; i++){
							String tempData = topFile.getAbsolutePath().toString() + "/" + file1[i].getName();
							newTopPointer.add(tempData);
							Bitmap bmp = BitmapFactory.decodeFile(tempData);
							newTopData.add(bmp); 

						}
					}

					File file2[] = LongSleeveFile.listFiles();
					if(file2.length > 0){
						for (int i=0; i < file2.length; i++){
							String tempData = LongSleeveFile.getAbsolutePath().toString() + "/" + file2[i].getName();
							newPantsPointer.add(tempData);
							Bitmap bmp = BitmapFactory.decodeFile(tempData);
							newTopData.add(bmp); 

						}
					}


					File file3[] = pantsFile.listFiles();
					if(file3.length > 0){
						for (int i=0; i < file3.length; i++){
							String tempData = pantsFile.getAbsolutePath().toString() + "/" + file3[i].getName();
							newPantsPointer.add(tempData);
							Bitmap bmp = BitmapFactory.decodeFile(tempData);
							newPantsData.add(bmp); 

						}
					}


				}

				if(temperatureOutside > shortsWeather && workDays[day] != true){

					File file1[] = topFile.listFiles();
					if(file1.length > 0){
						for (int i=0; i < file1.length; i++){
							String tempData = topFile.getAbsolutePath().toString() + "/" + file1[i].getName();
							newTopPointer.add(tempData);
							Bitmap bmp = BitmapFactory.decodeFile(tempData);
							newTopData.add(bmp); 

						}
					}

					File file4[] = shortsFile.listFiles();
					if(file4.length > 0){
						for (int i=0; i < file4.length; i++){
							String tempData = shortsFile.getAbsolutePath().toString() + "/" + file4[i].getName();
							newPantsPointer.add(tempData);
							Bitmap bmp = BitmapFactory.decodeFile(tempData);
							newPantsData.add(bmp); 

						}
					}


				}
				getRand();

			}else{

				Intent intent = new Intent(context, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				Toast.makeText(context, "No weather data found!", Toast.LENGTH_SHORT).show();

			}

		}catch(Exception e)
		{
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
		}


		save.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View arg0) {
				try {
					Toast.makeText(context, "Outfit saved to closet", Toast.LENGTH_SHORT).show();
					final String[] outfitData = new String[3];
					outfitData[0] = newTopPointer.get(tRand);
					outfitData[1] = newPantsPointer.get(bRand);
					outfitData[2] = shoePointer.get(sRand);

					File closet = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/closet/");
					if (closet.exists() == false){

						closet.mkdir();
					}
					int count = 1;
					File outfit=new File(closet, count + ".txt");
					while(outfit.exists()){
						count++;
						outfit=new File(closet, count + ".txt");
					}


					BufferedWriter out = new BufferedWriter( 
							new OutputStreamWriter( 
									new FileOutputStream(outfit), "US-ASCII")); 

					out.write(outfitData[0] + "");
					out.newLine();
					out.write(outfitData[1] + "");
					out.newLine();
					out.write(outfitData[2] + "");
					out.flush();
					out.close();
				} catch (Exception e) {Toast.makeText(context, e.toString() + "", Toast.LENGTH_SHORT).show();
				}

			}});
		setLayoutColor(newTopPointer, newPantsPointer, shoePointer, tRand, bRand, sRand);


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

	public void setLayoutColor(ArrayList<String> list1, ArrayList<String> list2, ArrayList<String> list3, int count, int count2, int count3)
	{
		String filePath1 = list1.get(count);
		String filePath2 = list2.get(count2);
		String filePath3 = list3.get(count3);

		Bitmap bmp1 = BitmapFactory.decodeFile(filePath1);
		Bitmap bmp2 = BitmapFactory.decodeFile(filePath2);
		Bitmap bmp3 = BitmapFactory.decodeFile(filePath3);
		LinearLayout l1 = (LinearLayout)findViewById(R.id.suggestion_page);

		String color = getLayoutColor(bmp1, bmp2, bmp3);

		l1.setBackgroundColor(Color.parseColor(color));

	}

	public String getLayoutColor(Bitmap image1, Bitmap image2, Bitmap image3)
	{
		String color = "";
		int red = 0;
		int blue = 0;
		int green = 0;
		for(int i = 145; i < 155; i++)
		{
			for(int k = 250; k < 280; k++)
			{
				int pixel = image1.getPixel(i,k);
				red += Color.red(pixel);
				blue += Color.blue(pixel);
				green += Color.green(pixel);
			}
		}


		for(int i = 145; i < 155; i++)
		{
			for(int k = 200; k < 230; k++)
			{
				int pixel = image2.getPixel(i,k);
				red += Color.red(pixel);
				blue += Color.blue(pixel);
				green += Color.green(pixel);
			}
		}

		for(int i = 110; i < 125; i++)
		{
			for(int k = 250; k < 265; k++)
			{
				int pixel = image3.getPixel(i,k);
				red += Color.red(pixel);
				blue += Color.blue(pixel);
				green += Color.green(pixel);
			}
		}
		red = (red/825);
		green = (green/825);
		blue = (blue/825);

		color = String.format("#%02x%02x%02x", red, green, blue);
		return color;
	}

	public void getRand() {
		try{
			Random rand = new Random();

			tRand = rand.nextInt(newTopData.size());
			bRand = rand.nextInt(newPantsData.size());
			sRand = rand.nextInt(shoeData.size());

			ImageView top = (ImageView)findViewById(R.id.imgtops);
			ImageView bottom = (ImageView)findViewById(R.id.imgbottoms);
			ImageView shoe = (ImageView)findViewById(R.id.imgshoes);
			top.setImageBitmap(newTopData.get(tRand));
			bottom.setImageBitmap(newPantsData.get(bRand));


			setLayoutColor(newTopPointer, newPantsPointer, shoePointer, tRand, bRand, sRand);
			Bitmap bitmap = ((BitmapDrawable)top.getDrawable()).getBitmap();

			detectStripeShirt dss = new detectStripeShirt();
			Boolean isItStripe = dss.start(bitmap);

			if(isItStripe == true)
			{
				Toast.makeText(context, "Stripes!", Toast.LENGTH_SHORT).show();
			}

			int topPixelCount = 0;
			int bottomPixelCount = 0;
			int pixel = 0;
			double bred = 0;
			double bblue =0;
			double bgreen = 0;
			double red = 0;
			double blue =0;
			double green = 0;
			Bitmap bitmapbottom = ((BitmapDrawable)bottom.getDrawable()).getBitmap();
			int bpixel = 0;

			for(int i = 140; i < 290; i++)
			{
				for(int k = 280; k < 320; k++)
				{
					pixel = bitmap.getPixel(i,k);
					red += Color.red(pixel);
					blue += Color.blue(pixel);
					green += Color.green(pixel);
					topPixelCount++;
				}			
			}

			for(int i = 70; i < 150; i++)
			{
				for(int k = 170; k < 205; k++)
				{
					bpixel = bitmapbottom.getPixel(i,k);
					bred+= Color.red(bpixel);
					bblue += Color.blue(bpixel);
					bgreen += Color.green(bpixel);
					bottomPixelCount++;
				}			
			}

			double averageTopRed = red/topPixelCount;
			double averageTopGreen = green/topPixelCount;
			double averageTopBlue = blue/topPixelCount;

			double averageBottomRed = bred/bottomPixelCount;
			double averageBottomGreen = bgreen/bottomPixelCount;
			double averageBottomBlue = bblue/bottomPixelCount;

			double redRatio = Math.abs(1- (averageTopRed/averageBottomRed));
			double blueRatio = Math.abs(1- (averageTopBlue/averageBottomBlue));
			double greenRatio = Math.abs(1- (averageTopGreen/averageBottomGreen));

			if (redRatio < 0.18 && blueRatio < 0.18 && greenRatio < 0.18 && isItStripe == false && workDays[day] != true){

				Toast.makeText(context, "Possible monochromatic shirt and pants combo", Toast.LENGTH_SHORT).show();

			}

			shoe.setImageBitmap(shoeData.get(sRand));

		}catch(Exception e){Toast.makeText(context, e.toString() + "", Toast.LENGTH_LONG).show();}
	}
}