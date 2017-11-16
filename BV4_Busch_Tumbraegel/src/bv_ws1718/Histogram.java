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
		// TODO: calculate histogram[] out of the gray values found in the image's selectionRect
		
		histogram[0] = 0; // remove this line when implementing the proper calculations
		
		// Remark: Please ignore statsData in Exercise 4. It will be used in Exercise 5.
		
	}
	    
	public void draw() {
		gc.clearRect(0, 0, grayLevels, maxHeight);
		gc.setLineWidth(1);

		// TODO: draw histogram[] into the gc graphic context
		
		// Remark: This is some dummy code to give you an idea for graphics drawing		
		double shift = 0.5;
		// note that we need to add 0.5 to all coordinates to get a one pixel thin line 
		gc.setStroke(Color.GREEN);
		gc.strokeLine(128 + shift, shift, 128 + shift, maxHeight + shift);
		gc.strokeLine(shift, maxHeight/2 + shift, grayLevels + shift, maxHeight/2 + shift);
		gc.setStroke(Color.ORANGE);
		gc.strokeLine(shift, shift, grayLevels + shift, maxHeight + shift);
		gc.strokeLine(grayLevels + shift, shift, shift, maxHeight + shift);

	}
    
}
