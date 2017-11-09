// BV Ue3 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws1718;

import java.io.File;

import bv_ws1718.MorphologicFilter.FilterType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

public class MorphologicFilterAppController {
	
	private static final String initialFileName = "rhino_part.png";
	private static File fileOpenPath = new File(".");
	
	private static final MorphologicFilter filter = new MorphologicFilter();
	private int threshold;
	private boolean kernel[][]; // first dimension: y (row), second dimension: x (column)
	
	public enum KernelPreset { 
		CUSTOM("Custom", -1),
		R0_0("r = 0", 0),
		R1_0("r = 1", 1),
		R1_4("r = √2", Math.sqrt(2)),
		R2_0("r = 2", 2),
		R2_4("r = √5", Math.sqrt(5)),
		R2_8("r = 2√2", 2 * Math.sqrt(2));
		
		private final String name;       
		private final double radius;       
	    private KernelPreset(String s, double r) { name = s; radius = r; }
	    public String toString() { return this.name; }
	    public double getRadius() { return radius; }
	};
	
    @FXML
    private Slider thresholdSlider;

    @FXML
    private Label thresholdLabel;

    @FXML
    private ComboBox<FilterType> filterSelection;

    @FXML
    private ComboBox<KernelPreset> kernelPresetSelection;

    @FXML
    private GridPane kernelGrid;
    
    @FXML
    private ImageView originalImageView;

    @FXML
    private ImageView binaryImageView;

    @FXML
    private ImageView filteredImageView;

    @FXML
    private Label messageLabel;

    @FXML
    void openImage() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(fileOpenPath); 
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images (*.jpg, *.png, *.gif)", "*.jpeg", "*.jpg", "*.png", "*.gif"));
		File selectedFile = fileChooser.showOpenDialog(null);
		if(selectedFile != null) {
			fileOpenPath = selectedFile.getParentFile();
			new RasterImage(selectedFile).setToView(originalImageView);
	    	processImages();
	    	messageLabel.getScene().getWindow().sizeToScene();;
		}
    }
	
    @FXML
    void thresholdChanged() {
    	threshold = (int)thresholdSlider.getValue();
    	thresholdLabel.setText("" + threshold);
    	processImages();
    }
    
    @FXML
    void filterChanged() {
    	processImages();
    }
    
    @FXML
    void kernelChanged() {
    	kernelPresetSelection.setValue(KernelPreset.CUSTOM);
    	int numRows = kernelGrid.getRowConstraints().size();
    	int numColumns = kernelGrid.getColumnConstraints().size();
    	kernel = new boolean[numRows][numColumns];
    	for(Node child : kernelGrid.getChildren()) {
    	    Integer column = GridPane.getColumnIndex(child);
    	    if(column == null) column = 0;
    	    Integer row = GridPane.getRowIndex(child);
    	    if(row == null) row = 0;
    	    if(column < numColumns && row < numRows) {
    	        kernel[row][column] = ((CheckBox)child).isSelected();
    	    }
    	}    	
    	processImages();
    }
    
    @FXML
    void kernelPresetChanged() {
    	double radius = kernelPresetSelection.getValue().getRadius();
    	if(radius < 0) return;
    	double radiusSquared = radius * radius;
       	int numRows = kernelGrid.getRowConstraints().size();
    	int numColumns = kernelGrid.getColumnConstraints().size();
    	kernel = new boolean[numRows][numColumns];
    	for(Node child : kernelGrid.getChildren()) {
    	    Integer column = GridPane.getColumnIndex(child);
    	    if(column == null) column = 0;
    	    Integer row = GridPane.getRowIndex(child);
    	    if(row == null) row = 0;
    	    if(column < numColumns && row < numRows) {
    	    	double distSquared = Math.pow(row - numRows/2, 2) + Math.pow(column - numColumns/2, 2);
    	    	kernel[row][column] = distSquared <= radiusSquared;
    	    	((CheckBox)child).setSelected(kernel[row][column]);
    	    }
    	}    	
    	processImages();
    }
    
	@FXML
	public void initialize() {
		// set combo boxes items
		filterSelection.getItems().addAll(FilterType.values());
		filterSelection.setValue(FilterType.DILATION);
		kernelPresetSelection.getItems().addAll(KernelPreset.values());
		kernelPresetSelection.setValue(KernelPreset.R1_0);
		
		// initialize parameters
		thresholdChanged();
		kernelPresetChanged();
		
		// load and process default image
		new RasterImage(new File(initialFileName)).setToView(originalImageView);
		processImages();
	}
	
	private void processImages() {
		if(originalImageView.getImage() == null)
			return; // no image: nothing to do
		
		long startTime = System.currentTimeMillis();
		
		RasterImage origImg = new RasterImage(originalImageView); 
		RasterImage binaryImg = new RasterImage(origImg.width, origImg.height); 
		RasterImage filteredImg = new RasterImage(origImg.width, origImg.height); 
		
		filter.copy(origImg, binaryImg);
		binaryImg.binarize(threshold);
		
		switch(filterSelection.getValue()) {
		case DILATION:
			filter.dilation(binaryImg, filteredImg, kernel);
			break;
		case EROSION:
			filter.erosion(binaryImg, filteredImg, kernel);
			break;
		default:
			break;
		}
		
		binaryImg.setToView(binaryImageView);
		filteredImg.setToView(filteredImageView);
		
	   	messageLabel.setText("Processing time: " + (System.currentTimeMillis() - startTime) + " ms");
	}
	

	



}
