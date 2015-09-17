package com.example.outfitsifter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class closet extends Activity {
	Context context = this;
	ArrayList<String> topData = new ArrayList<String>();
	ArrayList<String> bottomData = new ArrayList<String>();
	ArrayList<String> shoeData = new ArrayList<String>();
	ArrayList<String> fileLocations = new ArrayList<String>();
	int itemCount = 0;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	String gender = "";
	String UserName = "";
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(context, calendar.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		return true;  }

	@SuppressLint({ "ShowToast", "CutPasteId" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getActionBar().setDisplayShowHomeEnabled(false);


		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			// Ignore
		}

		 SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		 UserName = sharedPrefs.getString(UserName+"CurrentUser", null);
		if(sharedPrefs.getString(UserName+"gender", "").equals("Female")){gender = "Female";}
		if(sharedPrefs.getString(UserName+"gender", "").equals("Male")){gender = "Male";}

		setContentView(R.layout.closet_layout);
		Button btnWear = (Button)findViewById(R.id.btnWear);
		ImageView tops = (ImageView)findViewById(R.id.imgtops);
		ImageView bottoms = (ImageView)findViewById(R.id.imgbottoms);
		ImageView shoes = (ImageView)findViewById(R.id.imgshoes);
		LinearLayout l1  = (LinearLayout)findViewById(R.id.swipe);
		retrievePhotos();
		Bitmap b1 = BitmapFactory.decodeFile(topData.get(0));
		Bitmap b2 = BitmapFactory.decodeFile(bottomData.get(0));
		Bitmap b3 = BitmapFactory.decodeFile(shoeData.get(0));
		tops.setImageBitmap(b1);
		bottoms.setImageBitmap(b2);
		shoes.setImageBitmap(b3);

		try
        {
			File lastWeather = new File (Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/lastWeather.txt");
			String[] textArray = new String[2];

			Scanner weather = new Scanner(lastWeather);

			int a = 0;
			while (weather.hasNextLine()) {
				textArray[a] = weather.nextLine();
				a++;
			}
			weather.close();

			if(textArray[1].contains("Rain") || textArray[1].contains("Storm"))
            {
				Toast.makeText(context, "There's a forcasted rain shower/storm, here's a few jackets to choose from!", Toast.LENGTH_SHORT).show();
			}

		}catch(InputMismatchException e){} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int item = itemCount+1;
		getActionBar().setTitle("Closet ("+ item +"/" + topData.size()+")");

		btnWear.setOnClickListener(new Button.OnClickListener(){
			@SuppressLint("SimpleDateFormat") public void onClick(View arg0) {
				File directory = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/Calendar");
				if(!directory.exists())
                {
					directory.mkdir();
				}

				SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
				String currentDateandTime = sdf.format(new Date());
				final File date = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/Calendar/" + currentDateandTime + ".txt");
				if(date.exists() == false)
                {
					try
                    {
						BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(date), "US-ASCII"));
						out.write(topData.get(itemCount) + "");
						out.newLine();
						if(topData.get(itemCount).contains("onePiece"))
						{
							out.write("null");
						}
						else
						{
							out.write(bottomData.get(itemCount) + "");
						}
						out.newLine();
						out.write(shoeData.get(itemCount) + "");
						out.flush();
						out.close();
						Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
					}
                    catch (Exception e) {}
				}
				else
                {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
					// set title
					alertDialogBuilder.setTitle("Update your previously worn outfit today?");

					// set dialog message
					alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
						@SuppressLint("InlinedApi")
						public void onClick(DialogInterface dialog,int id) {
							date.delete();
							try
                            {
								BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(date), "US-ASCII"));
								out.write(topData.get(itemCount) + "");
								out.newLine();
								if(topData.get(itemCount).contains("onePiece"))
								{
									out.write("null");
								}
								else
								{
									out.write(bottomData.get(itemCount) + "");
								}
								out.newLine();
								out.write(shoeData.get(itemCount) + "");
								out.flush();
								out.close();
							}
                            catch (Exception e) {}
							Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
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
				}
			}});

		l1.setOnTouchListener(new LinearLayout.OnTouchListener(){

			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
				gestureDetector = new GestureDetector( new SwipeDetector() );
				//Toast.makeText(context, "thump", Toast.LENGTH_SHORT).show();
				return false;
			}});

		tops.setOnTouchListener(new ImageView.OnTouchListener(){
			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
				gestureDetector = new GestureDetector( new SwipeDetector() );
				return false;
			}});

		bottoms.setOnTouchListener(new ImageView.OnTouchListener(){
			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
				gestureDetector = new GestureDetector( new SwipeDetector() );
				return false;
			}});

		shoes.setOnTouchListener(new ImageView.OnTouchListener(){
			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
				gestureDetector = new GestureDetector( new SwipeDetector() );
				return false;
			}});

		tops.setOnLongClickListener(new ImageView.OnLongClickListener() {

			public boolean onLongClick(View v) {
				longClick();
				return false;

			}});
		bottoms.setOnLongClickListener(new ImageView.OnLongClickListener() {

			public boolean onLongClick(View v) {
				longClick();
				return false;
			}});
		shoes.setOnLongClickListener(new ImageView.OnLongClickListener() {

			public boolean onLongClick(View v) {
				longClick();
				return false;

			}});

		setLayoutColor(topData, bottomData, shoeData, 0, 0, 0);
	}

	private class SwipeDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY ) {

			if( e1.getY() - e2.getY() > SWIPE_MAX_OFF_PATH ){

				return false;
			}

			if( e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs( velocityX ) > SWIPE_THRESHOLD_VELOCITY ) {

				next();
				return false;
			}

			if( e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs( velocityX ) > SWIPE_THRESHOLD_VELOCITY ) {

				previous();


				return false;
			}

			return false;
		}
	}

	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		return gestureDetector.onTouchEvent( event );
	}



	protected void previous() {

		final Animation translate_to_left_prev = AnimationUtils.loadAnimation(this, R.anim.prev_translate_left);
		final Animation translate_from_right_prev = AnimationUtils.loadAnimation(this, R.anim.prev_translate_right);
		getActionBar().setTitle("Closet ("+ (itemCount) +"/" + topData.size()+")");
		itemCount--;
		if(itemCount == topData.size()-1){
			int item = itemCount+1;
			testLastWornOutfit();
			getActionBar().setTitle("Closet ("+ item +"/" + topData.size()+")");
			final ImageView tops = (ImageView)findViewById(R.id.imgtops);
			final ImageView bottoms = (ImageView)findViewById(R.id.imgbottoms);
			final ImageView shoes = (ImageView)findViewById(R.id.imgshoes);

			final Bitmap b1 = BitmapFactory.decodeFile(topData.get(itemCount));
			final Bitmap b2 = BitmapFactory.decodeFile(bottomData.get(itemCount));
			final Bitmap b3 = BitmapFactory.decodeFile(shoeData.get(itemCount));

			tops.startAnimation(translate_to_left_prev);
			bottoms.startAnimation(translate_to_left_prev);
			shoes.startAnimation(translate_to_left_prev);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					tops.setImageBitmap(b1);
					bottoms.setImageBitmap(b2);
					shoes.setImageBitmap(b3);
				}
			}, 150);


		}
		if(itemCount == -1)
        {
			itemCount = topData.size() -1;
			testLastWornOutfit();
			int item = itemCount+1;
			getActionBar().setTitle("Closet ("+ item +"/" + topData.size()+")");

			final ImageView tops = (ImageView)findViewById(R.id.imgtops);
			final ImageView bottoms = (ImageView)findViewById(R.id.imgbottoms);
			final ImageView shoes = (ImageView)findViewById(R.id.imgshoes);

			final Bitmap b1 = BitmapFactory.decodeFile(topData.get(itemCount));
			final Bitmap b2 = BitmapFactory.decodeFile(bottomData.get(itemCount));
			final Bitmap b3 = BitmapFactory.decodeFile(shoeData.get(itemCount));

			tops.startAnimation(translate_to_left_prev);
			bottoms.startAnimation(translate_to_left_prev);
			shoes.startAnimation(translate_to_left_prev);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					tops.setImageBitmap(b1);
					bottoms.setImageBitmap(b2);
					shoes.setImageBitmap(b3);
					tops.startAnimation(translate_from_right_prev);
					bottoms.startAnimation(translate_from_right_prev);
					shoes.startAnimation(translate_from_right_prev);
				}
			}, 150);
		}
        else
        {
			int item = itemCount+1;
			testLastWornOutfit();
			getActionBar().setTitle("Closet ("+ item +"/" + topData.size()+")");
			final ImageView tops = (ImageView)findViewById(R.id.imgtops);
			final ImageView bottoms = (ImageView)findViewById(R.id.imgbottoms);
			final ImageView shoes = (ImageView)findViewById(R.id.imgshoes);

			final Bitmap b1 = BitmapFactory.decodeFile(topData.get(itemCount));
			final Bitmap b2 = BitmapFactory.decodeFile(bottomData.get(itemCount));
			final Bitmap b3 = BitmapFactory.decodeFile(shoeData.get(itemCount));

			tops.startAnimation(translate_to_left_prev);
			bottoms.startAnimation(translate_to_left_prev);
			shoes.startAnimation(translate_to_left_prev);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					tops.setImageBitmap(b1);
					bottoms.setImageBitmap(b2);
					shoes.setImageBitmap(b3);
					tops.startAnimation(translate_from_right_prev);
					bottoms.startAnimation(translate_from_right_prev);
					shoes.startAnimation(translate_from_right_prev);
				}
			}, 150);
		}

		if(topData.get(itemCount).contains("onePiece"))
		{
			LinearLayout l3 = (LinearLayout)findViewById(R.id.L3);
			ImageView shoe = (ImageView)findViewById(R.id.imgshoes);
			ImageView bottoms = (ImageView)findViewById(R.id.imgbottoms);

			LinearLayout.LayoutParams l3Params = new LinearLayout.LayoutParams(0,LayoutParams.FILL_PARENT);
			l3Params.weight = 8.0f;

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.weight = 25.0f;

			shoe.setLayoutParams(params);
			l3.setLayoutParams(l3Params);

			bottoms.setVisibility(ImageView.GONE);
		}
		else
		{
			LinearLayout l3 = (LinearLayout)findViewById(R.id.L3);
			ImageView shoe = (ImageView)findViewById(R.id.imgshoes);
			ImageView bottoms = (ImageView)findViewById(R.id.imgbottoms);

			LinearLayout.LayoutParams l3Params = new LinearLayout.LayoutParams(0,LayoutParams.FILL_PARENT);
			l3Params.weight = 5.0f;

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.weight = 13.0f;

			l3.setLayoutParams(l3Params);
			shoe.setLayoutParams(params);

			bottoms.setVisibility(ImageView.VISIBLE);
		}

		setLayoutColor(topData, bottomData, shoeData, itemCount, itemCount, itemCount);

	}

	@SuppressLint("CutPasteId")
	protected void next() {

		final Animation translate_to_left = AnimationUtils.loadAnimation(this, R.anim.translate_left);
		final Animation translate_from_right = AnimationUtils.loadAnimation(this, R.anim.translate_right);
		itemCount++;


		if(itemCount == topData.size()-1){
			testLastWornOutfit();
			int item = itemCount+1;
			getActionBar().setTitle("Closet ("+ item +"/" + topData.size()+")");
			final ImageView tops = (ImageView)findViewById(R.id.imgtops);
			final ImageView bottoms = (ImageView)findViewById(R.id.imgbottoms);
			final ImageView shoes = (ImageView)findViewById(R.id.imgshoes);

			final Bitmap b1 = BitmapFactory.decodeFile(topData.get(itemCount));
			final Bitmap b2 = BitmapFactory.decodeFile(bottomData.get(itemCount));
			final Bitmap b3 = BitmapFactory.decodeFile(shoeData.get(itemCount));

			tops.startAnimation(translate_to_left);
			bottoms.startAnimation(translate_to_left);
			shoes.startAnimation(translate_to_left);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					tops.setImageBitmap(b1);
					bottoms.setImageBitmap(b2);
					shoes.setImageBitmap(b3);

					tops.startAnimation(translate_from_right);
					bottoms.startAnimation(translate_from_right);
					shoes.startAnimation(translate_from_right);

				}
			}, 150);

		}else if(itemCount == topData.size()){
			itemCount = 0;
			testLastWornOutfit();
			int item = itemCount+1;
			getActionBar().setTitle("Closet ("+ item +"/" + topData.size()+")");
			final ImageView tops = (ImageView)findViewById(R.id.imgtops);
			final ImageView bottoms = (ImageView)findViewById(R.id.imgbottoms);
			final ImageView shoes = (ImageView)findViewById(R.id.imgshoes);

			final Bitmap b1 = BitmapFactory.decodeFile(topData.get(itemCount));
			final Bitmap b2 = BitmapFactory.decodeFile(bottomData.get(itemCount));
			final Bitmap b3 = BitmapFactory.decodeFile(shoeData.get(itemCount));

			tops.startAnimation(translate_to_left);
			bottoms.startAnimation(translate_to_left);
			shoes.startAnimation(translate_to_left);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					tops.setImageBitmap(b1);
					bottoms.setImageBitmap(b2);
					shoes.setImageBitmap(b3);

					tops.startAnimation(translate_from_right);
					bottoms.startAnimation(translate_from_right);
					shoes.startAnimation(translate_from_right);
				}
			}, 150);

		}else {
			int item = itemCount+1;
			getActionBar().setTitle("Closet ("+ item +"/" + topData.size()+")");
			testLastWornOutfit();
			final ImageView tops = (ImageView)findViewById(R.id.imgtops);
			final ImageView bottoms = (ImageView)findViewById(R.id.imgbottoms);
			final ImageView shoes = (ImageView)findViewById(R.id.imgshoes);

			final Bitmap b1 = BitmapFactory.decodeFile(topData.get(itemCount));
			final Bitmap b2 = BitmapFactory.decodeFile(bottomData.get(itemCount));
			final Bitmap b3 = BitmapFactory.decodeFile(shoeData.get(itemCount));

			tops.startAnimation(translate_to_left);
			bottoms.startAnimation(translate_to_left);
			shoes.startAnimation(translate_to_left);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					tops.setImageBitmap(b1);
					bottoms.setImageBitmap(b2);
					shoes.setImageBitmap(b3);

					tops.startAnimation(translate_from_right);
					bottoms.startAnimation(translate_from_right);
					shoes.startAnimation(translate_from_right);
				}
			}, 150);
		}

		if(topData.get(itemCount).contains("onePiece"))
		{
			LinearLayout l3 = (LinearLayout)findViewById(R.id.L3);
			ImageView shoe = (ImageView)findViewById(R.id.imgshoes);
			ImageView bottoms = (ImageView)findViewById(R.id.imgbottoms);

			LinearLayout.LayoutParams l3Params = new LinearLayout.LayoutParams(0,LayoutParams.FILL_PARENT);
			l3Params.weight = 8.0f;

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.weight = 25.0f;

			shoe.setLayoutParams(params);
			l3.setLayoutParams(l3Params);

			bottoms.setVisibility(ImageView.GONE);
		}
		else
		{
			LinearLayout l3 = (LinearLayout)findViewById(R.id.L3);
			ImageView shoe = (ImageView)findViewById(R.id.imgshoes);
			ImageView bottoms = (ImageView)findViewById(R.id.imgbottoms);

			LinearLayout.LayoutParams l3Params = new LinearLayout.LayoutParams(0,LayoutParams.FILL_PARENT);
			l3Params.weight = 5.0f;

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.weight = 13.0f;

			l3.setLayoutParams(l3Params);
			shoe.setLayoutParams(params);

			bottoms.setVisibility(ImageView.VISIBLE);
		}

		setLayoutColor(topData, bottomData, shoeData, itemCount, itemCount, itemCount);
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
	public void retrievePhotos(){
		File closetFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/closet/");
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		File file2[] = closetFile.listFiles();
		if(file2.length > 0){
			String line = "";
			String l1 = "";
			String l2 = "";
			String l3 = "";
			for (int i=0; i < file2.length; i++){
				if(file2[i].getName().indexOf(".txt")!= -1)
                {
					try
                    {
						BufferedReader br = new BufferedReader(new FileReader(closetFile.getAbsolutePath().toString() + "/" + file2[i].getName()));
						fileLocations.add(closetFile.getAbsolutePath().toString() + "/" + file2[i].getName());
                        line = br.readLine();
						topData.add(line);
                        l1 = line;
						line = br.readLine();
						l2 = line;

						bottomData.add(line);

						line = br.readLine();
						l3 = line;
						shoeData.add(line);

						if(l1.contains("onePiece") && sharedPrefs.getString(UserName+"gender", "").equals("Male"))
						{
							topData.remove(l1);
							bottomData.remove(l2);
							bottomData.remove(l3);
						}
						br.close();
					}
					catch(Exception e)
                    {
                        Toast.makeText(context, e.toString() + "", Toast.LENGTH_SHORT).show();
                    }
				}
			}
		}
        else
        {
			Intent intent = new Intent(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}
	public void longClick(){
		final Animation translate_to_left = AnimationUtils.loadAnimation(this, R.anim.translate_left);
		final Animation translate_from_right = AnimationUtils.loadAnimation(this, R.anim.translate_right);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle("Delete this outfit?");

		// set dialog message
		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			@SuppressLint("InlinedApi")
			public void onClick(DialogInterface dialog,int id) {

				try{
					File file = new File(fileLocations.get(itemCount));
					file.delete();
					fileLocations.clear();
					bottomData.clear();
					topData.clear();
					shoeData.clear();
					retrievePhotos();
					Toast.makeText(context, "Outfit deleted!", Toast.LENGTH_SHORT).show();
					if(bottomData.size() == 0){

						Intent intent = new Intent(context, MainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						Toast.makeText(context, "Insufficient number of outfits found!", Toast.LENGTH_SHORT).show();

					}

					if (itemCount > bottomData.size()-1){
						itemCount = topData.size()-1;
						final ImageView tops = (ImageView)findViewById(R.id.imgtops);
						final ImageView bottoms = (ImageView)findViewById(R.id.imgbottoms);
						final ImageView shoes = (ImageView)findViewById(R.id.imgshoes);

						final Bitmap b1 = BitmapFactory.decodeFile(topData.get(itemCount));
						final Bitmap b2 = BitmapFactory.decodeFile(bottomData.get(itemCount));
						final Bitmap b3 = BitmapFactory.decodeFile(shoeData.get(itemCount));

						tops.startAnimation(translate_from_right);
						bottoms.startAnimation(translate_from_right);
						shoes.startAnimation(translate_from_right);
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							public void run() {
								tops.setImageBitmap(b1);
								bottoms.setImageBitmap(b2);
								shoes.setImageBitmap(b3);

								tops.startAnimation(translate_to_left);
								bottoms.startAnimation(translate_to_left);
								shoes.startAnimation(translate_to_left);
							}
						}, 150);
					}else
					{
						final ImageView tops = (ImageView)findViewById(R.id.imgtops);
						final ImageView bottoms = (ImageView)findViewById(R.id.imgbottoms);
						final ImageView shoes = (ImageView)findViewById(R.id.imgshoes);

						final Bitmap b1 = BitmapFactory.decodeFile(topData.get(itemCount));
						final Bitmap b2 = BitmapFactory.decodeFile(bottomData.get(itemCount));
						final Bitmap b3 = BitmapFactory.decodeFile(shoeData.get(itemCount));

						tops.startAnimation(translate_from_right);
						bottoms.startAnimation(translate_from_right);
						shoes.startAnimation(translate_from_right);
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							public void run() {
								tops.setImageBitmap(b1);
								bottoms.setImageBitmap(b2);
								shoes.setImageBitmap(b3);

								tops.startAnimation(translate_to_left);
								bottoms.startAnimation(translate_to_left);
								shoes.startAnimation(translate_to_left);
							}
						}, 150);

					}
					int item = itemCount+1;
					getActionBar().setTitle("Closet ("+ item +"/" + topData.size()+")");
				}catch(Exception e){
					//Toast.makeText(context, e.toString() + "", Toast.LENGTH_SHORT).show();
				}

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

	}

	public void setLayoutColor(ArrayList<String> list1, ArrayList<String> list2, ArrayList<String> list3, int count, int count2, int count3)
	{
		String filePath1 = list1.get(count);
		String filePath2 = list2.get(count2);

		String filePath3 = list3.get(count3);

		Bitmap bmp1 = BitmapFactory.decodeFile(filePath1);

		Bitmap bmp3 = BitmapFactory.decodeFile(filePath3);
		LinearLayout l1 = (LinearLayout)findViewById(R.id.swipe);
		String color = "";

		if(filePath2.length() > 1)
		{
			Bitmap bmp2 = BitmapFactory.decodeFile(filePath2);
			color = getLayoutColor(bmp1, bmp2, bmp3);
		}
		else
		{
			color = getLayoutColorNoBottoms(bmp1, bmp3);
		}

		l1.setBackgroundColor(Color.parseColor(color));

	}

	public String getLayoutColor(Bitmap image1, Bitmap image2, Bitmap image3)
	{
		String color = "";
		int red = 0;
		int blue = 0;
		int green = 0;
		int count = 0;
		for(int i = 145; i < 155; i++)
		{
			for(int k = 250; k < 280; k++)
			{
				int pixel = image1.getPixel(i,k);
				red += Color.red(pixel);
				blue += Color.blue(pixel);
				green += Color.green(pixel);
				count++;
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
				count++;
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
				count++;
			}
		}
		red = (red/count);
		green = (green/count);
		blue = (blue/count);

		color = String.format("#%02x%02x%02x", red, green, blue);
		return color;
	}


	public String getLayoutColorNoBottoms(Bitmap image1, Bitmap image3)
	{
		String color = "";
		int red = 0;
		int blue = 0;
		int green = 0;
		int count = 0;
		for(int i = 145; i < 155; i++)
		{
			for(int k = 250; k < 280; k++)
			{
				int pixel = image1.getPixel(i,k);
				red += Color.red(pixel);
				blue += Color.blue(pixel);
				green += Color.green(pixel);
				count++;
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
				count++;
			}
		}
		red = (red/count);
		green = (green/count);
		blue = (blue/count);

		color = String.format("#%02x%02x%02x", red, green, blue);
		return color;
	}

	public void testLastWornOutfit() {
        File calendar = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/Calendar/");
        ArrayList<File> calOutfits = new ArrayList<File>();
        if (calendar.exists())
        {
            for (File f : calendar.listFiles())
            {
                calOutfits.add(f);
            }
            String top = topData.get(itemCount);
            String bottom = bottomData.get(itemCount);
            String shoe = shoeData.get(itemCount);

            if (calOutfits.size() > 0)
            {
                int verifyOutfit = 0;
                String date = "";
                for (int i = 0; i < calOutfits.size(); i++)
                {
                    try
                    {
                        if (calOutfits.get(i).getName().indexOf(".txt") != -1)
                        {
                            File outfit = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/Calendar/" + calOutfits.get(i).getName());
                            Scanner in = new Scanner(outfit);
                            while (in.hasNextLine())
                            {
                                String line = in.nextLine();
                                if (line.equals(top))
                                {
                                    line = in.nextLine();
                                    if (line.equals(bottom))
                                    {
                                        line = in.nextLine();
                                        if (line.equals(shoe))
                                        {
                                            verifyOutfit++;
                                            date = calOutfits.get(i).getName();
                                            date = date.substring(0, date.length() - 4);
                                        }
                                    }
                                }
                            }
                            in.close();
                        }
                    }
                    catch (Exception e) { }
                }

                if (verifyOutfit > 0)
                {
                    Toast.makeText(context, "Last worn " + date, Toast.LENGTH_SHORT).show();
                }

            }

        }
    }
}