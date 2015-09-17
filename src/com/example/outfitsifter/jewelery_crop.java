package com.example.outfitsifter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;

public class jewelery_crop  extends Activity {

	
	public Bitmap selection(Bitmap bmp)
	{
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bmp2 = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
		int red = 0;
		int green = 0;
		int blue = 0;
		int count = 0;
		int pixel;
		//bottom right
		for(int i = (width-50); i < width; i++)
		{
			for(int j = (height-50); j < height; j++)
			{
				pixel = bmp.getPixel(i,j);
				
				red+= Color.red(pixel);
				green+= Color.green(pixel);
				blue+= Color.blue(pixel);
				count++;
			}
		}
		//top left
		for(int i = 1; i < 50; i++)
		{
			for(int j = 1; j < 50; j++)
			{
				pixel = bmp.getPixel(i,j);
				
				red+= Color.red(pixel);
				green+= Color.green(pixel);
				blue+= Color.blue(pixel);
				count++;
			}
		}
		//top right
		for(int i = (width-50); i < width; i++)
		{
			for(int j = 1; j < 50; j++)
			{
				pixel = bmp.getPixel(i,j);
				
				red+= Color.red(pixel);
				green+= Color.green(pixel);
				blue+= Color.blue(pixel);
				count++;
			}
		}
		
		//bottom left
				for(int i = 1; i < 50; i++)
				{
					for(int j = (height-50); j < height; j++)
					{
						pixel = bmp.getPixel(i,j);
						
						red+= Color.red(pixel);
						green+= Color.green(pixel);
						blue+= Color.blue(pixel);
						count++;
					}
				}
		
		double avgRed = red/count;
		double avgGreen = green/count;
		double avgBlue = blue/count;
		
		for(int x = 0; x < width; x++)
		{
		    for(int y = 0; y < height; y++)
		    {
		    	pixel = bmp.getPixel(x, y);
		    	
		    	int red2 = Color.red(pixel);
				int green2 = Color.green(pixel);
				int blue2 = Color.blue(pixel);
				
				double ratioRed = Math.abs(1- (red2 / avgRed));
				double ratioGreen = Math.abs(1- (green2 / avgGreen));
				double ratioBlue = Math.abs(1- (blue2 / avgBlue));
				
		        if(ratioRed < 0.3 && ratioGreen < 0.3 && ratioBlue < 0.3)
		        {
		        	bmp2.setPixel(x, y, Color.TRANSPARENT);
		        }
		        else
		        {
		        	bmp2.setPixel(x, y, bmp.getPixel(x, y));
		        }
		    }
		}
		return bmp2;
	}
	
}