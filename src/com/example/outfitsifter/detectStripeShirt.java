package com.example.outfitsifter;

import android.graphics.Bitmap;
import android.graphics.Color;

public class detectStripeShirt {

	public Boolean start(Bitmap data){

		int pixel = 0;
		double red = 0;
		double blue =0;
		double green = 0;

		int pixel2 = 0;
		double red2 = 0;
		double blue2 =0;
		double green2 = 0;

		for(int i = 145; i < 155; i++){

			for(int k = 225; k < 275; k++){

				pixel = data.getPixel(i,k);
				red += Color.red(pixel);
				blue += Color.blue(pixel);
				green += Color.green(pixel);

			}

		}

		double avRed = red / 500;
		double avBlue = blue / 500;
		double avGreen = green / 500;


		for(int i = 156; i < 166; i++){

			for(int k = 276; k < 326; k++){

				pixel2 = data.getPixel(i,k);
				red2 += Color.red(pixel2);
				blue2 += Color.blue(pixel2);
				green2 += Color.green(pixel2);

			}

		}

		double avRed2 = red2 / 500;
		double avBlue2 = blue2 / 500;
		double avGreen2 = green2 / 500;


		double redOff = Math.abs(avRed - avRed2);
		double blueOff = Math.abs(avBlue - avBlue2);
		double greenOff = Math.abs(avGreen - avGreen2);

		if(redOff > 50 || blueOff > 50 || greenOff > 50){

			return true;

		}

		return false;}

}