package com.example.outfitsifter;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class showOutfit extends Activity{
Context context = this;
String gender = "";
String UserName = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayShowHomeEnabled(false);
		 SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		 UserName = sharedPrefs.getString(UserName+"CurrentUser", null);
		if(sharedPrefs.getString(UserName+"gender", "").equals("Female")){gender = "Female";}
		if(sharedPrefs.getString(UserName+"gender", "").equals("Male")){gender = "Male";}
		setContentView(R.layout.show_outfit_layout);
		String data = getIntent().getExtras().getString(UserName+"data");
		getActionBar().setTitle("Outfit worn on: " + data);
		File dataPath = new File(Environment.getExternalStorageDirectory() + "/OutfitSifter/" + UserName + "/Calendar/" + data + ".txt");
		
		
		ImageView tops = (ImageView)findViewById(R.id.imgtops);
		ImageView bottoms = (ImageView)findViewById(R.id.imgbottoms);
		ImageView shoes = (ImageView)findViewById(R.id.imgshoes);

		ArrayList<String> l1 = new ArrayList<String>();
		try{
		Scanner scan = new Scanner(dataPath);
		while (scan.hasNextLine()) {
			//Toast.makeText(context, scan.next().toString(), Toast.LENGTH_SHORT).show();
			l1.add(scan.next().toString());
		}
		scan.close();
		Bitmap b1 = BitmapFactory.decodeFile(l1.get(0));
		Bitmap b3 = BitmapFactory.decodeFile(l1.get(2));

		if(l1.get(0).contains("onePiece"))
		{
			tops.setImageBitmap(b1);
			shoes.setImageBitmap(b3);
			
			LinearLayout l3 = (LinearLayout)findViewById(R.id.L3);

			LinearLayout.LayoutParams l3Params = new LinearLayout.LayoutParams(0,LayoutParams.FILL_PARENT);
			l3Params.weight = 8.0f;

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.weight = 4.0f;
			
			LinearLayout.LayoutParams shoeParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			shoeParams.weight = 9.0f;

			tops.setLayoutParams(params);
			shoes.setLayoutParams(shoeParams);
			l3.setLayoutParams(l3Params);

			bottoms.setVisibility(ImageView.GONE);
			

		}
		else
		{
			Bitmap b2 = BitmapFactory.decodeFile(l1.get(1));
			tops.setImageBitmap(b1);
			bottoms.setImageBitmap(b2);
			shoes.setImageBitmap(b3);
		}

		
		
		}catch(Exception e){Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();}
	}
}