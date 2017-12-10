// BV Ue4 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-16

package bv_ws1718;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ToneCurve {
	
	private static final int grayLevels = 256;
	
    private GraphicsContext gc;
    
    private int brightness;
    private double gamma;
    private double contrast;
    
    private int[] grayTable = new int[grayLevels];

	public ToneCurve(GraphicsContext gc) {
		this.gc = gc;
	}
	
	public void setBrightness(int brightness) {
		this.brightness = brightness;
		updateTable();
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
		updateTable();
	}
	
	public void setContrast(double contrast) {
		this.contrast = contrast;
		updateTable();
	}

	private void updateTable() {
		// Fill the grayTable[] array to map gay input values to gray output values.
		// It will be used as follows: grayOut = grayTable[grayIn].
		//
		// Use brightness and gamma values.
		// See "Gammakorrektur" at slide no. 18 of 
		// http://home.htw-berlin.de/~barthel/veranstaltungen/GLDM/vorlesungen/04_GLDM_Bildmanipulation1_Bildpunktoperatoren.pdf
		//
		// First apply the brightness change, afterwards the gamma correction.
		for(int grayScale = 0; grayScale < grayTable.length; grayScale++) {
//			grayTable[grayScale] = grayScale;
			 int brightnessChangedGray = (int) (contrast * ((grayScale  + brightness) - 128) + 128);
			 int gammaChangedGray = (int) Math.round(((255*(Math.pow(brightnessChangedGray, (1/gamma))))/(Math.pow(255, (1/gamma)))));
			 
			 if(gammaChangedGray <= 0) {
				 grayTable[grayScale] = 0;
			 } else if(gammaChangedGray >= 255) {
				 grayTable[grayScale] = 255;
			 }else {
				 grayTable[grayScale] = gammaChangedGray;
			 }
			 
		}		
	}
	
	public int mappedGray(int inputGray) {
		return grayTable[inputGray];
	}
	
	public void draw() {
		updateTable();
		gc.clearRect(0, 0, grayLevels, grayLevels);
		gc.setStroke(Color.DARKGREEN);
		gc.setLineWidth(3);

		// draw the tone curve into the gc graphic context
		gc.beginPath();
		gc.moveTo(0, 255 - grayTable[0]);
		for(int index = 1; index < grayTable.length; index++) {
			gc.lineTo(index, 255- grayTable[index]);
		}
		gc.stroke();
		
		// Remark: This is some dummy code to give you an idea for graphics drawing with pathes		
//		gc.beginPath();
//		gc.moveTo(0, 128);
//		gc.lineTo(64, brightness);
//		gc.lineTo(128, brightness * gamma);
//		gc.stroke();
	}

	
}
