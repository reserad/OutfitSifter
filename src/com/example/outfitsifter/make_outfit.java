package com.example.outfitsifter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class make_outfit extends Activity {
	Context context = this;
	ArrayList<Bitmap> topData = new ArrayList<Bitmap>();
	ArrayList<Bitmap> bottomData = new ArrayList<Bitmap>();
	ArrayList<Bitmap> shortData = new ArrayList<Bitmap>();
	ArrayList<Bitmap> shoeData = new ArrayList<Bitmap>();
	int countShirts = 0;
	int countPants = 0;
	int countShoes = 0;

	int selection = 1;
	ArrayList<String> topPointer = new ArrayList<String>();

	ArrayList<String> bottomPointer = new ArrayList<String>();
	ArrayList<String> shortsPointer = new ArrayList<String>();
	ArrayList<String> shoePointer = new ArrayList<String>();

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	String gender = "";
	String UserName = "";
	
	@SuppressLint({ "ShowToast", "CutPasteId" })
	@Override
	public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);

		//	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setTitle("Outfit Designer");
		setContentView(R.layout.make_outfit);

		final Button save = (Button)findViewById(R.id.btnSave);

		LinearLayout l1 = (LinearLayout)findViewById(R.id.l1);
		LinearLayout l2 = (LinearLayout)findViewById(R.id.l2);
		LinearLayout l3 = (LinearLayout)findViewById(R.id.l3);
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		UserName = sharedPrefs.getString(UserName+"CurrentUser", null);
		if(sharedPrefs.getString(UserName+"gender", "").equals("Female")){gender = "Female";}
		if(sharedPrefs.getString(UserName+"gender", "").equals("Male")){gender = "Male";}
		
		obtainTopData();
		obtainBottomData();
		obtainShoeData();

		ImageView top = (ImageView)findViewById(R.id.imgTop);
		ImageView bottom = (ImageView)findViewById(R.id.imgBottoms);
		ImageView shoe = (ImageView)findViewById(R.id.imgShoes);

		top.setImageBitmap(topData.get(0));
		bottom.setImageBitmap(bottomData.get(0));
		shoe.setImageBitmap(shoeData.get(0));

		setLayoutColor(topPointer, 0, 1);
		setLayoutColor(bottomPointer, 0, 2);
		setLayoutColor(shoePointer, 0, 3);

		save.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View arg0) {
				try
                {
					Toast.makeText(context, "Outfit saved to closet", Toast.LENGTH_SHORT).show();
					final String[] outfitData = new String[3];
					outfitData[0] = topPointer.get(countShirts);

					outfitData[2] = shoePointer.get(countShoes);
					if(topPointer.get(countShirts).contains("onePiece"))
					{
						outfitData[1] = "";
					}
					else
					{
						outfitData[1] = bottomPointer.get(countPants);
					}

					File closet = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/closet/");
					if (!closet.exists())
                    {
						closet.mkdir();
					}
					int count = 1;
					File outfit=new File(closet, count + ".txt");
					while(outfit.exists())
                    {
						count++;
						outfit=new File(closet, count + ".txt");
					}

					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfit), "US-ASCII"));
					out.write(outfitData[0] + "");
					out.newLine();
					out.write(outfitData[1] + "");
					out.newLine();
					out.write(outfitData[2] + "");
					out.flush();
					out.close();
				}
                catch (Exception e)
                {
                    Toast.makeText(context, e.toString() + "", Toast.LENGTH_SHORT).show();
                }

			}});


		top.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View v) {
				selection = 1;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

				// set title
				alertDialogBuilder.setTitle("Delete this clothing item?");

				// set dialog message
				alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					@SuppressLint("InlinedApi")
					public void onClick(DialogInterface dialog,int id) {
						//delete
						String filePath = topPointer.get(countShirts);
						//Toast.makeText(context, filePath, Toast.LENGTH_SHORT).show();
						File file = new File(filePath);
						file.delete();

						File closetFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/closet/");
						//
						if (closetFile.exists())
                        {
							File items[] = closetFile.listFiles();

							for(int i= 0; i < items.length; i++)
                            {
								File tempFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/closet/" + items[i].getName());
								try
                                {
									Scanner inFile = new Scanner(tempFile);
									String results = "";
									while(inFile.hasNext())
                                    {
										results = inFile.next();
										if(filePath.equals(results))
                                        {
											File deleteNow = new File(items[i] + "");
											deleteNow.delete();
										}
									}
									inFile.close();
								}
                                catch(Exception e){}
							}
						}
						topData.clear();
						obtainTopData();

						if (countShirts > topData.size()-1)
                        {
							countShirts = topData.size()-1;
							ImageView top = (ImageView)findViewById(R.id.imgTop);
							top.setImageBitmap(topData.get(countShirts));
						}
                        else
						{
							ImageView top = (ImageView)findViewById(R.id.imgTop);
							top.setImageBitmap(topData.get(countShirts));
						}
						//setLayoutColor(topPointer, countShirts, 1);
						Toast.makeText(context, "Item deleted (and any outfits containing that item)", Toast.LENGTH_SHORT).show();

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
		});


		bottom.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View v) {
				selection = 2;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				// set title
				alertDialogBuilder.setTitle("Delete this clothing item?");
				// set dialog message
				alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					@SuppressLint("InlinedApi")
					public void onClick(DialogInterface dialog,int id) {

						//delete
						String filePath = bottomPointer.get(countPants);
						//Toast.makeText(context, filePath, Toast.LENGTH_SHORT).show();
						File file = new File(filePath);
						file.delete();

						File closetFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/closet/");
						//
						if (closetFile.exists())
                        {
							File items[] = closetFile.listFiles();

							for(int i= 0; i < items.length; i++)
                            {
								File tempFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/closet/" + items[i].getName());
								try
                                {
									Scanner inFile = new Scanner(tempFile);
									String results = "";
									while(inFile.hasNext())
                                    {
										results = inFile.next();

										if(filePath.equals(results))
                                        {
											File deleteNow = new File(items[i] + "");
											deleteNow.delete();
										}
									}
									inFile.close();
								}
                                catch(Exception e){}
							}
						}

						bottomData.clear();
						obtainBottomData();

						if (countPants > bottomData.size()-1)
                        {
							countPants = bottomData.size()-1;
							ImageView bottom = (ImageView)findViewById(R.id.imgBottoms);
							bottom.setImageBitmap(bottomData.get(countPants));
						}
                        else
						{
							ImageView bottom = (ImageView)findViewById(R.id.imgBottoms);
							bottom.setImageBitmap(bottomData.get(countPants));
						}
						//setLayoutColor(bottomPointer, countPants, 2);
						Toast.makeText(context, "Item deleted (and any outfits containing that item)", Toast.LENGTH_SHORT).show();
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
		});


		shoe.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View v) {
				selection = 3;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

				// set title
				alertDialogBuilder.setTitle("Delete this clothing item?");

				// set dialog message
				alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					@SuppressLint("InlinedApi")
					public void onClick(DialogInterface dialog,int id) {
						//delete
						String filePath = shoePointer.get(countShoes);
						//Toast.makeText(context, filePath, Toast.LENGTH_SHORT).show();
						File file = new File(filePath);
						file.delete();

						File closetFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/closet/");
						//
						if (closetFile.exists())
                        {
							File items[] = closetFile.listFiles();
							for(int i= 0; i < items.length; i++)
                            {
								File tempFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/closet/" + items[i].getName());
								try
                                {
									Scanner inFile = new Scanner(tempFile);

									String results = "";
									while(inFile.hasNext()){
										results = inFile.next();
										if(filePath.equals(results))
                                        {
											File deleteNow = new File(items[i] + "");
											deleteNow.delete();
										}
									}
									inFile.close();
								}
                                catch(Exception e){}
							}
						}

						shoeData.clear();
						obtainShoeData();

						if (countShoes > shoeData.size()-1)
                        {
							countShoes = shoeData.size()-1;
							ImageView shoe = (ImageView)findViewById(R.id.imgShoes);
							shoe.setImageBitmap(shoeData.get(countShoes));
						}
                        else
						{
							ImageView shoe = (ImageView)findViewById(R.id.imgShoes);
							shoe.setImageBitmap(shoeData.get(countShoes));
						}
						//setLayoutColor(shoePointer, countShoes, 3);
						Toast.makeText(context, "Item deleted (and any outfits containing that item)", Toast.LENGTH_SHORT).show();

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
		});


		shoe.setOnTouchListener(new ImageView.OnTouchListener(){
			@SuppressLint("ClickableViewAccessibility")
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				selection = 3;
				gestureDetector = new GestureDetector(new SwipeDetector());
				return false;
			}


		});


		l1.setOnTouchListener(new LinearLayout.OnTouchListener(){
			@SuppressLint("ClickableViewAccessibility")
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				selection = 1;
				gestureDetector = new GestureDetector( new SwipeDetector() );
				return false;
			}


		});

		top.setOnTouchListener(new ImageView.OnTouchListener(){
			@SuppressLint("ClickableViewAccessibility")
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				selection = 1;
				gestureDetector = new GestureDetector( new SwipeDetector() );
				return false;
			}


		});

		bottom.setOnTouchListener(new ImageView.OnTouchListener(){

			@SuppressLint("ClickableViewAccessibility")
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				selection = 2;
				gestureDetector = new GestureDetector( new SwipeDetector() );
				return false;
			}


		});


		l2.setOnTouchListener(new LinearLayout.OnTouchListener(){
			@SuppressLint("ClickableViewAccessibility")
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				selection = 2;
				gestureDetector = new GestureDetector( new SwipeDetector() );
				return false;
			}});

		l3.setOnTouchListener(new LinearLayout.OnTouchListener(){
			@SuppressLint("ClickableViewAccessibility")
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				selection = 3;
				gestureDetector = new GestureDetector( new SwipeDetector() );
				return false;
			}});
	}


	private class SwipeDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY )
        {
			// Check movement along the Y-axis. If it exceeds SWIPE_MAX_OFF_PATH,
			// then dismiss the swipe.
			if( Math.abs( e1.getY() - e2.getY() ) > SWIPE_MAX_OFF_PATH )
				return false;

			// Swipe from right to left.
			// The swipe needs to exceed a certain distance (SWIPE_MIN_DISTANCE)
			// and a certain velocity (SWIPE_THRESHOLD_VELOCITY).
			if( e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs( velocityX ) > SWIPE_THRESHOLD_VELOCITY )
            {
				next();
				return true;
			}

			// Swipe from left to right.
			// The swipe needs to exceed a certain distance (SWIPE_MIN_DISTANCE)
			// and a certain velocity (SWIPE_THRESHOLD_VELOCITY).
			if( e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs( velocityX ) > SWIPE_THRESHOLD_VELOCITY )
            {
				previous();
				return false;
			}
			return true;
		}
	}


	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		return gestureDetector.onTouchEvent( event );
	}


	protected void previous()
    {
		final Animation animation_prev_right = AnimationUtils.loadAnimation(this, R.anim.prev_translate_right);
		final Animation animation_prev_left = AnimationUtils.loadAnimation(this, R.anim.prev_translate_left);

		if (selection == 1)
        {
			countShirts--;
			if(countShirts == topData.size()-1)
            {
				ImageView top = (ImageView)findViewById(R.id.imgTop);
				top.setImageBitmap(topData.get(countShirts));
				top.startAnimation(animation_prev_left);
			}
            else if(countShirts == -1)
            {
				countShirts = topData.size()-1;
				final ImageView top = (ImageView)findViewById(R.id.imgTop);
				top.startAnimation(animation_prev_left);
				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
						top.setImageBitmap(topData.get(countShirts));
						top.startAnimation(animation_prev_right);
					} 
				}, 150);
			}
            else
            {
				final ImageView top = (ImageView)findViewById(R.id.imgTop);
				top.startAnimation(animation_prev_left);
				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
						top.setImageBitmap(topData.get(countShirts));
						top.startAnimation(animation_prev_right);
					} 
				}, 150);
			}
			if(topPointer.get(countShirts).contains("onePiece"))
			{
				LinearLayout l3 = (LinearLayout)findViewById(R.id.l3);
				
				LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
					    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				imgParams.weight = 8.0f;
				
				ImageView top = (ImageView)findViewById(R.id.imgTop);
				ImageView shoe = (ImageView)findViewById(R.id.imgShoes);
				Button save = (Button)findViewById(R.id.btnSave);
				
				top.setLayoutParams(imgParams);
				shoe.setLayoutParams(imgParams);
				save.setLayoutParams(imgParams);
				
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
					params.weight = 25.0f;
					l3.setLayoutParams(params);
				LinearLayout l2 = (LinearLayout)findViewById(R.id.l2);
				l2.setVisibility(LinearLayout.GONE);
			}
			else
			{
				LinearLayout l3 = (LinearLayout)findViewById(R.id.l3);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				
				LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
					    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				imgParams.weight = 9.0f;
				
				ImageView top = (ImageView)findViewById(R.id.imgTop);
				ImageView shoe = (ImageView)findViewById(R.id.imgShoes);
				Button save = (Button)findViewById(R.id.btnSave);
				
				top.setLayoutParams(imgParams);
				shoe.setLayoutParams(imgParams);
				save.setLayoutParams(imgParams);
				
					params.weight = 13.0f;
					l3.setLayoutParams(params);
				LinearLayout l2 = (LinearLayout)findViewById(R.id.l2);
				l2.setVisibility(LinearLayout.VISIBLE);
			}
			setLayoutColor(topPointer, countShirts, 1);
		}
        else if(selection ==2)
        {
			countPants--;
			if(countPants == bottomData.size()-1)
            {
				final ImageView bottom = (ImageView)findViewById(R.id.imgBottoms);
				bottom.setImageBitmap(bottomData.get(countPants));
				bottom.startAnimation(animation_prev_left);
				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
						bottom.setImageBitmap(bottomData.get(countPants));
						bottom.startAnimation(animation_prev_right);
					} 
				}, 150); 
			}
            else if(countPants == -1)
            {
				countPants = bottomData.size()-1;
				final ImageView bottom = (ImageView)findViewById(R.id.imgBottoms);
				bottom.startAnimation(animation_prev_left);
				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
						bottom.setImageBitmap(bottomData.get(countPants));
						bottom.startAnimation(animation_prev_right);
					} 
				}, 150); 

			}else
            {
				final ImageView bottom = (ImageView)findViewById(R.id.imgBottoms);
				bottom.startAnimation(animation_prev_left);

				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
						bottom.setImageBitmap(bottomData.get(countPants));
						bottom.startAnimation(animation_prev_right);
					} 
				}, 150); 
			}
			setLayoutColor(bottomPointer, countPants, 2);
		}
        else if(selection == 3)
        {
			countShoes--;
			if(countShoes == shoeData.size()-1)
            {
				final ImageView shoes = (ImageView)findViewById(R.id.imgShoes);
				shoes.startAnimation(animation_prev_left);

				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
						shoes.setImageBitmap(shoeData.get(countShoes));
						shoes.startAnimation(animation_prev_right);
					} 
				}, 150); 

			}
            else if(countShoes == -1)
            {
				countShoes = shoeData.size()-1;
				final ImageView shoes = (ImageView)findViewById(R.id.imgShoes);
				shoes.startAnimation(animation_prev_left);

				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
						shoes.setImageBitmap(shoeData.get(countShoes));
						shoes.startAnimation(animation_prev_right);
					} 
				}, 150); 
			}
            else
            {

				final ImageView shoes = (ImageView)findViewById(R.id.imgShoes);
				shoes.startAnimation(animation_prev_left);

				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
						shoes.setImageBitmap(shoeData.get(countShoes));
						shoes.startAnimation(animation_prev_right);
					} 
				}, 150); 
			}

			setLayoutColor(shoePointer, countShoes, 3);
		}
	}

	protected void next()
    {
		final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.translate_left);
		final Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.translate_right);
		if (selection == 1)
        {
			countShirts++;
			if(countShirts == topData.size()-1)
            {
				final ImageView top = (ImageView)findViewById(R.id.imgTop);

				top.startAnimation(animationFadeIn);

				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
						top.setImageBitmap(topData.get(countShirts));
						top.startAnimation(animationFadeOut);
					} 
				}, 150); 

			}
            else if(countShirts == topData.size())
            {
				countShirts = 0;
				final ImageView top = (ImageView)findViewById(R.id.imgTop);
				top.startAnimation(animationFadeIn);

				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
						top.setImageBitmap(topData.get(countShirts));
						top.startAnimation(animationFadeOut);
					} 
				}, 150); 
			}
            else
            {
				final ImageView top = (ImageView)findViewById(R.id.imgTop);
				top.startAnimation(animationFadeIn);
				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
						top.setImageBitmap(topData.get(countShirts));
						top.startAnimation(animationFadeOut);
					} 
				}, 150); 
			}
			
			if(topPointer.get(countShirts).contains("onePiece"))
			{
				LinearLayout l3 = (LinearLayout)findViewById(R.id.l3);
				
				LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				imgParams.weight = 8.0f;
				ImageView top = (ImageView)findViewById(R.id.imgTop);
				ImageView shoe = (ImageView)findViewById(R.id.imgShoes);
				Button save = (Button)findViewById(R.id.btnSave);
				
				top.setLayoutParams(imgParams);
				shoe.setLayoutParams(imgParams);
				save.setLayoutParams(imgParams);
				
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				params.weight = 25.0f;
				l3.setLayoutParams(params);
				LinearLayout l2 = (LinearLayout)findViewById(R.id.l2);
				l2.setVisibility(LinearLayout.GONE);
			}
			else
			{
				LinearLayout l3 = (LinearLayout)findViewById(R.id.l3);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				
				LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				imgParams.weight = 9.0f;
				
				ImageView top = (ImageView)findViewById(R.id.imgTop);
				ImageView shoe = (ImageView)findViewById(R.id.imgShoes);
				Button save = (Button)findViewById(R.id.btnSave);
				
				top.setLayoutParams(imgParams);
				shoe.setLayoutParams(imgParams);
				save.setLayoutParams(imgParams);
				
                params.weight = 13.0f;
                l3.setLayoutParams(params);
				LinearLayout l2 = (LinearLayout)findViewById(R.id.l2);
				l2.setVisibility(LinearLayout.VISIBLE);
			}
			setLayoutColor(topPointer, countShirts, 1);

		}
        else if(selection ==2)
        {
			countPants++;
			if(countPants == bottomData.size()-1)
            {
				final ImageView bottom = (ImageView)findViewById(R.id.imgBottoms);
				bottom.startAnimation(animationFadeIn);

				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
                    bottom.setImageBitmap(bottomData.get(countPants));
                    bottom.startAnimation(animationFadeOut);
					} 
				}, 150);
			}
            else if(countPants == bottomData.size())
            {
				countPants = 0;
				final ImageView bottom = (ImageView)findViewById(R.id.imgBottoms);
				bottom.startAnimation(animationFadeIn);
				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
                    bottom.setImageBitmap(bottomData.get(countPants));
                    bottom.startAnimation(animationFadeOut);
					} 
				}, 150); 
			}
            else
            {
				final ImageView bottom = (ImageView)findViewById(R.id.imgBottoms);
				bottom.startAnimation(animationFadeIn);
				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
                    bottom.setImageBitmap(bottomData.get(countPants));
                    bottom.startAnimation(animationFadeOut);
					} 
				}, 150); 
			}
			setLayoutColor(bottomPointer, countPants, 2);

		}
        else if(selection == 3)
        {
			countShoes++;
			if(countShoes == shoeData.size()-1)
            {
				final ImageView shoes = (ImageView)findViewById(R.id.imgShoes);

				shoes.startAnimation(animationFadeIn);

				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
                    shoes.setImageBitmap(shoeData.get(countShoes));
                    shoes.startAnimation(animationFadeOut);
					} 
				}, 150);
			}
            else if(countShoes == shoeData.size())
            {
				countShoes = 0;
				final ImageView shoes = (ImageView)findViewById(R.id.imgShoes);
				shoes.startAnimation(animationFadeIn);

				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
						shoes.setImageBitmap(shoeData.get(countShoes));
						shoes.startAnimation(animationFadeOut);
					} 
				}, 150); 
			}
            else
            {
				final ImageView shoes = (ImageView)findViewById(R.id.imgShoes);
				shoes.startAnimation(animationFadeIn);
				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
                    shoes.setImageBitmap(shoeData.get(countShoes));
                    shoes.startAnimation(animationFadeOut);
					} 
				}, 150);
			}
			setLayoutColor(shoePointer, countShoes, 3);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
        {
			topData.clear();
			bottomData.clear();
			shoeData.clear();
			topPointer.clear();
			bottomPointer.clear();
			shoePointer.clear();
			Intent intent = new Intent(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void setLayoutColor(ArrayList<String> list,int count, int type)
	{
		String filePath = list.get(count);
		Bitmap bmp = BitmapFactory.decodeFile(filePath);
		LinearLayout l1 = (LinearLayout)findViewById(R.id.l1);
		LinearLayout l2 = (LinearLayout)findViewById(R.id.l2);
		LinearLayout l3 = (LinearLayout)findViewById(R.id.page);
		String color = getLayoutColor(bmp, type);

		switch (type)
        {
            case 1:
                l1.setBackgroundColor(Color.parseColor(color));
                break;
            case 2:
                l2.setBackgroundColor(Color.parseColor(color));
                break;
            case 3:
                l3.setBackgroundColor(Color.parseColor(color));
                break;
		}
	}

	public String getLayoutColor(Bitmap image, int type)
	{
		String color = "";
		int red = 0;
		int blue = 0;
		int green = 0;
		switch (type){
		case 1:

			for(int i = 145; i < 155; i++)
			{
				for(int k = 250; k < 280; k++)
				{
					int pixel = image.getPixel(i,k);
					red += Color.red(pixel);
					blue += Color.blue(pixel);
					green += Color.green(pixel);
				}
			}
			red = (red/300);
			green = (green/300);
			blue = (blue/300);

			break;
		case 2:

			for(int i = 145; i < 155; i++)
			{
				for(int k = 200; k < 230; k++)
				{
					int pixel = image.getPixel(i,k);
					red += Color.red(pixel);
					blue += Color.blue(pixel);
					green += Color.green(pixel);
				}
			}
			red = (red/300);
			green = (green/300);
			blue = (blue/300);

			break;
		case 3:

			for(int i = 110; i < 125; i++)
			{
				for(int k = 250; k < 265; k++)
				{
					int pixel = image.getPixel(i,k);
					red += Color.red(pixel);
					blue += Color.blue(pixel);
					green += Color.green(pixel);
				}
			}
			red = (red/225);
			green = (green/225);
			blue = (blue/225);

			break;
		}
		color = String.format("#%02x%02x%02x", red, green, blue);
		return color;
	}

	public void obtainTopData(){
		File topFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/shirts/");
		File file1[] = topFile.listFiles();
		if(file1.length > 0)
        {
			for (int i=0; i < file1.length; i++)
            {
				String tempData = topFile.getAbsolutePath().toString() + "/" + file1[i].getName();
				topPointer.add(tempData);
				Bitmap bmp = BitmapFactory.decodeFile(tempData);
				topData.add(bmp);
			}
		}
		
		File workTopFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/work/shirts/");
		File workTopLocation[] = workTopFile.listFiles();
		if(workTopLocation.length > 0)
        {
			for (int i=0; i < workTopLocation.length; i++)
            {
				String tempData = workTopFile.getAbsolutePath().toString() + "/" + workTopLocation[i].getName();
				topPointer.add(tempData);
				Bitmap bmp = BitmapFactory.decodeFile(tempData);
				topData.add(bmp);
			}
		}
		
		File workLongTopFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/work/Lshirts/");
		File workLongTopLocation[] = workLongTopFile.listFiles();
		if(workLongTopLocation.length > 0)
        {
			for (int i=0; i < workLongTopLocation.length; i++)
            {
				String tempData = workLongTopFile.getAbsolutePath().toString() + "/" + workLongTopLocation[i].getName();
				topPointer.add(tempData);
				Bitmap bmp = BitmapFactory.decodeFile(tempData);
				topData.add(bmp);
			}
		}

		File LongSleeveFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/Lshirts/");
		File file4[] = LongSleeveFile.listFiles();
		if(file4.length > 0)
        {
			for (int i=0; i < file4.length; i++)
            {
				String tempData = LongSleeveFile.getAbsolutePath().toString() + "/" + file4[i].getName();
				topPointer.add(tempData);
				Bitmap bmp = BitmapFactory.decodeFile(tempData);
				topData.add(bmp);
			}
		}

		if(file1.length+file4.length == 0)
        {
			Intent intent = new Intent(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			Toast.makeText(context, "Insufficient number of clothing items found!", Toast.LENGTH_SHORT).show();
		}
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		if(sharedPrefs.getString(UserName+"gender", "").equals("Female"))
		{
			File onePieceLocation = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/Female/onePiece/");
			File onePiece[] = onePieceLocation.listFiles();
			if(onePiece.length > 0)
            {
				for (int i=0; i < onePiece.length; i++)
                {
					String tempData = onePieceLocation.getAbsolutePath().toString() + "/" + onePiece[i].getName();
					topPointer.add(tempData);
					Bitmap bmp = BitmapFactory.decodeFile(tempData);
					topData.add(bmp);
				}
			}
		}
	}

	public void obtainBottomData(){
		File shortsFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/shorts/");
		File bottomFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/pants/");
		File workShortsFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/work/shorts/");
		File workPantsFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/work/pants/");
		
		File workShorts[] = workShortsFile.listFiles();
		if(workShorts.length > 0)
        {
			for (int i=0; i < workShorts.length; i++)
            {
				String tempData = workShortsFile.getAbsolutePath().toString() + "/" + workShorts[i].getName();
				bottomPointer.add(tempData);
				Bitmap bmp = BitmapFactory.decodeFile(tempData);
				bottomData.add(bmp);
			}
		}
		
		File workPants[] = workPantsFile.listFiles();
		if(workPants.length > 0)
        {
			for (int i=0; i < workPants.length; i++)
            {
				String tempData = workPantsFile.getAbsolutePath().toString() + "/" + workPants[i].getName();
				bottomPointer.add(tempData);
				Bitmap bmp = BitmapFactory.decodeFile(tempData);
				bottomData.add(bmp);
			}
		}
		
		File file2[] = bottomFile.listFiles();
		if(file2.length > 0)
        {
			for (int i=0; i < file2.length; i++)
            {
				String tempData = bottomFile.getAbsolutePath().toString() + "/" + file2[i].getName();
				bottomPointer.add(tempData);
				Bitmap bmp = BitmapFactory.decodeFile(tempData);
				bottomData.add(bmp);
			}
		}

		File file3[] = shortsFile.listFiles();
		if(file3.length > 0)
        {
			for (int i=0; i < file3.length; i++)
            {
				String tempData = shortsFile.getAbsolutePath().toString() + "/" + file3[i].getName();
				bottomPointer.add(tempData);
				Bitmap bmp = BitmapFactory.decodeFile(tempData);
				bottomData.add(bmp);
				shortData.add(bmp);
			}
		}

		if(file3.length+file2.length == 0)
        {
			Intent intent = new Intent(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			Toast.makeText(context, "Insufficient number of clothing items found!", Toast.LENGTH_SHORT).show();
		}

	}
	public void obtainShoeData(){
		File shoesFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/shoes/");
		File file5[] = shoesFile.listFiles();
		if(file5.length > 0)
        {
			for (int i=0; i < file5.length; i++)
            {
				String tempData = shoesFile.getAbsolutePath().toString() + "/" + file5[i].getName();
				shoePointer.add(tempData);
				Bitmap bmp = BitmapFactory.decodeFile(tempData);
				shoeData.add(bmp);
			}
		}
		
		File workShoesFile = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName +"/work/shoes/");
		File workShoes[] = workShoesFile.listFiles();
		if(workShoes.length > 0)
        {
			for (int i=0; i < workShoes.length; i++)
            {
				String tempData = workShoesFile.getAbsolutePath().toString() + "/" + workShoes[i].getName();
				shoePointer.add(tempData);
				Bitmap bmp = BitmapFactory.decodeFile(tempData);
				shoeData.add(bmp);
			}
		}

		if(file5.length == 0)
        {
			Intent intent = new Intent(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			Toast.makeText(context, "Insufficient number of clothing items found!", Toast.LENGTH_SHORT).show();
		}
	}
}