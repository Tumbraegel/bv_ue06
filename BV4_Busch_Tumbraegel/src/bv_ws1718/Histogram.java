// BV Ue4 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-16

package bv_ws1718;

import bv_ws1718.ImageAnalysisAppController.StatsProperty;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.Arrays;

public class Histogram {

	private static final int grayLevels = 256;

	private GraphicsContext gc;
	private int maxHeight;

	private int[] histogram = new int[grayLevels];

	public Histogram(GraphicsContext gc, int maxHeight) {
		this.gc = gc;
		this.maxHeight = maxHeight;
	}

	public void update(RasterImage image, Rectangle selectionRect, ObservableList<StatsProperty> statsData) {
		// calculate histogram[] out of the gray values found in the images
		// selectionRect
		Arrays.fill(histogram, 0);

		int rectY = (int) selectionRect.getY();
		int rectX = (int) selectionRect.getX();
		int rectWidth = (int) selectionRect.getWidth();
		int rectHeight = (int) selectionRect.getHeight();


		for (int picY = rectY; picY < rectY + rectHeight; picY++) {
			for (int picX = rectX; picX < rectX + rectWidth; picX++) {
				int grayLevel = (image.argb[picY * image.width + picX] >> 16) & 0xff;
				histogram[grayLevel]++;
			}

		}
		// Remark: Please ignore statsData in Exercise 4. It will be used in
		// Exercise 5.
		StatsProperty min = statsData.get(0);
		min.setValue(getMinGrey());

		StatsProperty max = statsData.get(1);
		max.setValue(getMaxGrey());

		StatsProperty mean = statsData.get(2);
		mean.setValue(getMeanGrey());
		
		if(rectWidth != 0 && rectHeight != 0) {
			StatsProperty median = statsData.get(3);
			median.setValue(getMedian());

			StatsProperty varianz = statsData.get(4);
			varianz.setValue(getVarianz());
			
			StatsProperty entropy = statsData.get(5);
			entropy.setValue(getEntropy(image));
		}
	}

	
	private double getEntropy(RasterImage image){

		double entropy = 0;
		int amount = (image.width*image.height);
		
		//p is the probability of seeing this particular value
		for (int i = 0; i < histogram.length; i++){
			int count = histogram[i]++;
			double p = (double)count/amount;
			
			if (p > 0){
				entropy = (entropy - p*Math.log(p));
			}
		}	
		return entropy;
	}

	private double getVarianz() {
		int[] allValues = getAllValueArray();
		int n = allValues.length;

		double temp = 0;

		for (int i : allValues) {
			temp += Math.pow((double) i,2);
		}

		double P = (1.0 / (double) n) * temp;
		//varianz = P - mü^2
		double varianz = P - Math.pow(getMeanGrey(),2);
		return varianz;
	}

	private double getMedian() {
		int median = 0;

		int[] allValues = getAllValueArray();

		// finde den Median
		int middle = allValues.length / 2;
		median = allValues[middle];

		return median;
	}

	private int[] getAllValueArray() {
		int numberOfValues = 0;
		// Wie viele Indizes muss das neue Array haben?
		for (int i : histogram) {
			numberOfValues += i;
		}

		// erstelle neues Array
		int[] allValues = new int[numberOfValues];

		int counterForAllValues = 0;
		// diese Schleife zählt bis 255 (das ende des histogram arrays)
		for (int grayValue = 0; grayValue < histogram.length; grayValue++) {
			// diese Schleife zählt hoch bis der jew. Grauwert aus der oberen
			// Schleife mit der korrekten Anzahl im neuen Array vorhanden ist
			for (int j = 0; j < histogram[grayValue]; j++) {
				allValues[counterForAllValues] = grayValue;
				counterForAllValues++;
			}
		}
		return allValues;
	}

	private double getMeanGrey() {
		// mean == mü
		double mean = 0;
		double amount = 0;

		for (int i = 0; i < histogram.length; i++) {
			amount += histogram[i];
			mean += (histogram[i] * i);
		}

		return mean / amount;
	}

	public void draw() {
		// TO DO: draw histogram[] into the gc graphic context
		gc.clearRect(0, 0, grayLevels, maxHeight);
		gc.setStroke(Color.DARKRED);
		gc.setLineWidth(1);

		// get max value of histogram
		int max = calculateMax();

		double shift = 0.5;
		double ratio = (double) maxHeight / max;

		for (int index = 0; index < grayLevels; index++) {
			double adaptedValue = (double) (histogram[index]) * ratio;
			gc.strokeLine(index + shift, maxHeight + shift, index + shift, maxHeight - adaptedValue + shift);
		}
		// Remark: This is some dummy code to give you an idea for graphics
		// drawing
		// note that we need to add 0.5 to all coordinates to get a one pixel
		// thin line
		// gc.setStroke(Color.GREEN);
		// gc.strokeLine(128 + shift, shift, 128 + shift, maxHeight + shift);
		// gc.strokeLine(shift, maxHeight/2 + shift, grayLevels + shift,
		// maxHeight/2 + shift);
		// gc.setStroke(Color.ORANGE);
		// gc.strokeLine(shift, shift, grayLevels + shift, maxHeight + shift);
		// gc.strokeLine(grayLevels + shift, shift, shift, maxHeight + shift);
	}

	private int calculateMax() {
		int max = histogram[0];
		for (int i = 0; i < histogram.length; i++) {
			int current = histogram[i];
			if (current > max) {
				max = current;
			}
		}
		return max;
	}

	private int getMinGrey() {
		int min = 0;
		int counter = 0;
		boolean foundMin = false;
		while (!foundMin && counter < 256) {
			if (histogram[counter] != 0) {
				min = counter;
				foundMin = true;
			}
			counter++;
		}
		return min;
	}

	private int getMaxGrey() {
		int max = 255;
		int counter = 255;
		boolean foundMax = false;
		while (!foundMax && counter >= 0) {
			if (histogram[counter] != 0) {
				max = counter;
				foundMax = true;
			}
			counter--;
		}
		return max;
	}
}
