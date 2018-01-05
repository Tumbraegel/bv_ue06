// BV Ue1 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws1718;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class RasterImage {
	
	private static final int gray  = 0xffa0a0a0;

	public int[] argb;	// pixels represented as ARGB values in scanline order
	public int width;	// image width in pixels
	public int height;	// image height in pixels
//	public boolean isGray = false;
	
	public RasterImage(int width, int height) {
		// creates an empty RasterImage of given size
		this.width = width;
		this.height = height;
		argb = new int[width * height];
		Arrays.fill(argb, gray);
	}
	
	public RasterImage(File file) {
		// creates an RasterImage by reading the given file
		Image image = null;
		if(file != null && file.exists()) {
			image = new Image(file.toURI().toString());
		}
		if(image != null && image.getPixelReader() != null) {
			width = (int)image.getWidth();
			height = (int)image.getHeight();
			argb = new int[width * height];
			image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
		} else {
			// file reading failed: create an empty RasterImage
			this.width = 256;
			this.height = 256;
			argb = new int[width * height];
			Arrays.fill(argb, gray);
		}
	}
	
	public RasterImage(ImageView imageView) {
		// creates a RasterImage from that what is shown in the given ImageView
		Image image = imageView.getImage();
		width = (int)image.getWidth();
		height = (int)image.getHeight();
		argb = new int[width * height];
		image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
	}
	
	public void setToView(ImageView imageView) {
		// sets the current argb pixels to be shown in the given ImageView
		if(argb != null) {
			WritableImage wr = new WritableImage(width, height);
			PixelWriter pw = wr.getPixelWriter();
			pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
			imageView.setImage(wr);
		}
	}
	
	
	// image point operations to be added here
	
	public void convertToGray() {
		// TODO: convert the image to grayscale
		for(int i = 0; i < argb.length; i++) {
			int r = (argb[i] >> 16) & 0xff;
			int g = (argb[i] >> 8) & 0xff;
			int b = (argb[i]) & 0xff;
			
			int gray = (r+g+b)/3;
			
			argb[i] = 0xff000000 | (gray << 16) | (gray << 8) | (gray); 
		}
	}
	
	/**
	 * @param quantity The fraction of pixels that need to be modified
	 * @param strength The brightness to be added or subtracted from a pixel's gray level
	 */
	public void addNoise(double quantity, int strength) {
		// TODO: add noise with the given quantity and strength
		int amountOfPixels = argb.length;
		int amountOfNoisePixels = (int) (quantity * amountOfPixels);
		Random randomizer = new Random();
		
		for(int i = 0; i < amountOfNoisePixels; i++) {		

			int randomPixel = randomizer.nextInt(amountOfPixels);
			int r = (argb[randomPixel] >> 16) & 0xff;
			int g = (argb[randomPixel] >> 8) & 0xff;
			int b = (argb[randomPixel]) & 0xff;
			
			if(randomizer.nextBoolean()){
				r += strength;
				g += strength;
				b += strength;
				if(r>255) {
					r=g=b=255;
				}
				argb[randomPixel] = 0xff000000 | (r << 16) | (g << 8) | (b); 
			}else {
				r -= strength;
				g -= strength;
				b -= strength;
				if(r<0){
					r=g=b=0;
				}
				argb[randomPixel] = 0xff000000 | (r << 16) | (g << 8) | (b); 
			}
			
			
		}
		
		
		
	}
	

}
