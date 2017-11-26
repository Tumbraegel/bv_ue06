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
		//calculate histogram[] out of the gray values found in the images selectionRect		
		Arrays.fill(histogram, 0);
		
		int rectY = (int)selectionRect.getY();
		int rectX = (int)selectionRect.getX();
		int rectWidth = (int)selectionRect.getWidth();
		int rectHeight = (int)selectionRect.getHeight();
		
		for(int picY = rectY; picY < rectY+rectHeight; picY++) {
			for(int picX = rectX; picX < rectX+rectWidth; picX++) {
				int grayLevel = (image.argb[picY*rectWidth+picX] >>16) & 0xff;
				histogram[grayLevel]++;
			}
		}
		// Remark: Please ignore statsData in Exercise 4. It will be used in Exercise 5.	
	}
	    
	public void draw() {
		//TO DO: draw histogram[] into the gc graphic context
		gc.clearRect(0, 0, grayLevels, maxHeight);
		gc.setStroke(Color.DARKRED);
		gc.setLineWidth(1);	
		
		//get max value of histogram
		int max = histogram[0];
		for(int i=0;i<histogram.length;i++){
			int current = histogram[i];
			if(current>max){
				max = current;
			}
		}
		
		double shift = 0.5;
		double ratio = (double)maxHeight/max;
		
		for(int index=1; index<grayLevels; index++){
			double adaptedValue = (double)(histogram[index]+shift)*ratio;
			gc.strokeLine(index+shift,maxHeight+shift,index+shift,maxHeight-adaptedValue+shift);
		}
		// Remark: This is some dummy code to give you an idea for graphics drawing		
		// note that we need to add 0.5 to all coordinates to get a one pixel thin line 
//		gc.setStroke(Color.GREEN);
//		gc.strokeLine(128 + shift, shift, 128 + shift, maxHeight + shift);
//		gc.strokeLine(shift, maxHeight/2 + shift, grayLevels + shift, maxHeight/2 + shift);
//		gc.setStroke(Color.ORANGE);
//		gc.strokeLine(shift, shift, grayLevels + shift, maxHeight + shift);
//		gc.strokeLine(grayLevels + shift, shift, shift, maxHeight + shift);
	}
}
