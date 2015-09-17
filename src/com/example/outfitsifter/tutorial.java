package com.example.outfitsifter;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class tutorial extends Activity {
	Context context = this;
	String zip = "";
	int pageCount = 1;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	String result = "";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tut1_page);
		LinearLayout page = (LinearLayout)findViewById(R.id.page);

		SpannableString text = new SpannableString("- - -");
		TextView tv = (TextView)findViewById(R.id.progressDots);
		text.setSpan(new ForegroundColorSpan(Color.parseColor("#5cb85c")), 0, 1, 0);
		tv.setText(text, BufferType.SPANNABLE);


		page.setOnTouchListener(new LinearLayout.OnTouchListener(){
			@SuppressLint("ClickableViewAccessibility")
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gestureDetector = new GestureDetector( new SwipeDetector() );
				return false;
			}
		});

	}

	private class SwipeDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY ) {

			if( Math.abs( e1.getY() - e2.getY() ) > SWIPE_MAX_OFF_PATH )
				return false;

			if( e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs( velocityX ) > SWIPE_THRESHOLD_VELOCITY ) {

				next();
				return true;
			}

			if( e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs( velocityX ) > SWIPE_THRESHOLD_VELOCITY ) {
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

	public void next()
	{
		if(pageCount < 3)
		{
			pageCount++;

		}
		TextView tv;
		SpannableString text = new SpannableString("- - -");
		switch(pageCount)
		{
		case 1:
			setContentView(R.layout.tut1_page);
			tv = (TextView)findViewById(R.id.progressDots);
			text.setSpan(new ForegroundColorSpan(Color.parseColor("#5cb85c")), 0, 1, 0);
			tv.setText(text, BufferType.SPANNABLE);
			break;
		case 2:
			setContentView(R.layout.tut2_page);
			tv = (TextView)findViewById(R.id.progressDots);
			text.setSpan(new ForegroundColorSpan(Color.parseColor("#5cb85c")), 2, 3, 0);
			tv.setText(text, BufferType.SPANNABLE);
			break;
		case 3:
			setContentView(R.layout.tutorial_page);
			tv = (TextView)findViewById(R.id.progressDots);
			text.setSpan(new ForegroundColorSpan(Color.parseColor("#5cb85c")),  4, 5, 0);
			tv.setText(text, BufferType.SPANNABLE);

			Button ok = (Button)findViewById(R.id.btnOK);

			ok.setOnClickListener(new Button.OnClickListener(){
				public void onClick(View arg0) {
					
					LayoutInflater li = LayoutInflater.from(context);
					View promptsView = li.inflate(R.layout.add_user_dialog, null);

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							context);
					
					// set prompts.xml to alertdialog builder
					alertDialogBuilder.setView(promptsView);

					final EditText userInput = (EditText) promptsView.findViewById(R.id.txtUser);
					alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("Add",
						  new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog,int id) {
							result = userInput.getText().toString();
							
							TinyDB tinydb = new TinyDB(context);
							ArrayList<String> currentUsers = tinydb.getList("users");
						    currentUsers.add(result);
						    tinydb.putList("users", currentUsers);
						    
						    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
							SharedPreferences.Editor editor = sharedPrefs.edit();
							editor.putBoolean("Tutorial",true);
							editor.putString("CurrentUser", result);
							editor.apply();
							
							Intent intent = new Intent(context, MainActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						    
						    }
						  })
						.setNegativeButton("Cancel",
						  new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
						    }
						  });

					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();
					
					
				}});
			break;
		}

	}

	public void previous()
	{		
		if(pageCount > 1)
		{
			pageCount--;

		}
		
		TextView tv;
		SpannableString text = new SpannableString("- - -");
		switch(pageCount)
		{
		case 1:
			setContentView(R.layout.tut1_page);
			tv = (TextView)findViewById(R.id.progressDots);
			text.setSpan(new ForegroundColorSpan(Color.parseColor("#5cb85c")),  0, 1, 0);
			tv.setText(text, BufferType.SPANNABLE);

			break;
		case 2:
			setContentView(R.layout.tut2_page);
			tv = (TextView)findViewById(R.id.progressDots);
			text.setSpan(new ForegroundColorSpan(Color.parseColor("#5cb85c")),  2, 3, 0);
			tv.setText(text, BufferType.SPANNABLE);
			break;
		case 3:
			setContentView(R.layout.tutorial_page);
			tv = (TextView)findViewById(R.id.progressDots); 
			text.setSpan(new ForegroundColorSpan(Color.parseColor("#5cb85c")),  4, 5, 0);
			tv.setText(text, BufferType.SPANNABLE);
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{

			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}