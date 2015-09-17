package com.example.outfitsifter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class crop extends Activity {

	public void onLoad(File location, String type){
		int xLow = 0;
		int xHigh = 0;
		int yLow = 0;
		int yHigh = 0;
		Bitmap newBitmap = null;
		if (type == "shirt"){
			yLow = 75;
			yHigh = 485;
			xLow = 35;
			xHigh = 445;
			newBitmap = Bitmap.createBitmap(410,410, Bitmap.Config.RGB_565);
		}
		
		if (type == "jewelry"){
			yLow = 75;
			yHigh = 485;
			xLow = 35;
			xHigh = 445;
			newBitmap = Bitmap.createBitmap(410,410, Bitmap.Config.RGB_565);
		}
		
		if (type == "onePiece"){
			yLow = 1;
			yHigh = 640;
			xLow = 65;
			xHigh = 415;
			newBitmap = Bitmap.createBitmap(350,640, Bitmap.Config.RGB_565);
		}
		
		if (type.equals("shoes")){
			yLow = 50;
			yHigh = 490;
			xLow = 20;
			xHigh = 460;
			newBitmap = Bitmap.createBitmap(440,440, Bitmap.Config.RGB_565);
		}
		
		if (type.equals("pants")){
			
			yLow = 25;
			yHigh = 525;
			xLow = 65;
			xHigh = 415;
			newBitmap = Bitmap.createBitmap(350,500, Bitmap.Config.RGB_565);
			
		}
		
		try{
		Bitmap bitmap = BitmapFactory.decodeFile(location.getPath());
		
		for(int i = yLow; i < yHigh; i++){
			
			for(int j = xLow; j < xHigh; j++){
				
				int color1 = bitmap.getPixel(j, i);
				int r = Color.red(color1);
				int g = Color.green(color1);
				int b = Color.blue(color1);
				newBitmap.setPixel((j - xLow), (i-yLow), Color.rgb(r, g, b));
			}
			
		}
		bitmap.recycle();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		newBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
		location.delete();
		location.createNewFile();
		//write the bytes in file
		FileOutputStream fo = new FileOutputStream(location);
		fo.write(bytes.toByteArray());
		
		fo.close();
		newBitmap.recycle();
		}catch(Exception e){
			
		}
	}
}