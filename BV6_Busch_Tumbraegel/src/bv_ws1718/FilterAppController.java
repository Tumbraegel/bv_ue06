// BV Ue1 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-11

package bv_ws1718;

import java.io.File;

//import bv_ws1718.Filter.BorderProcessing;
import bv_ws1718.Filter.PredicationType;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class FilterAppController {
	
	private static final String initialFileName = "test1.jpg";
	private static File fileOpenPath = new File(".");
	
	private static final Filter filter = new Filter();
	private double noiseQuantity;
	private int noiseStrength;
	private int kernelSize;
	private RasterImage img;

    @FXML
    private ComboBox<PredicationType> predicationSelection;

//    @FXML
//    private ComboBox<BorderProcessing> borderProcessingSelection;

    @FXML
    private ImageView originalImageView;

    @FXML
    private ImageView predicationImageView;

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
			RasterImage img = new RasterImage(selectedFile);
			img.convertToGray();
    		img.setToView(originalImageView);
	    	processImages();
	    	messageLabel.getScene().getWindow().sizeToScene();;
		}
    }

    @FXML
    void predicationChanged() {
    	processImages();
    }

    
	@FXML
	public void initialize() {
		// set combo boxes items
		predicationSelection.getItems().addAll(PredicationType.values());
		predicationSelection.setValue(PredicationType.COPY);
		
		// initialize parameters
		predicationChanged();
		
		// load and process default image
		img = new RasterImage(new File(initialFileName));
		img.convertToGray();
		img.setToView(originalImageView);
		processImages();
	}
	
	private void processImages() {
		if(originalImageView.getImage() == null)
			return; // no image: nothing to do
		
		long startTime = System.currentTimeMillis();
		
		RasterImage origImg = new RasterImage(originalImageView); 
		RasterImage predicationImg = new RasterImage(origImg.width, origImg.height); 
		RasterImage filteredImg = new RasterImage(origImg.width, origImg.height); 
		
		filter.copy(origImg, filteredImg);		
		
		switch(predicationSelection.getValue()) {
		case COPY:
			filter.copy(origImg, predicationImg);
			break;
		case A:
			filter.methodA(origImg, predicationImg);
			break;
		case B:
//			filter.box(noisyImg, filteredImg, kernelSize, borderProcessingSelection.getValue());
			break;
		case C:
//			filter.median(noisyImg, filteredImg, kernelSize, borderProcessingSelection.getValue());
			break;
		case AANDBMINUSC:
			break;
		case AANDBDIVIDEDBY2:
			break;
		case ADAPTIV:
			break;
		default:
			break;
		}
		
		predicationImg.setToView(predicationImageView);
		filteredImg.setToView(filteredImageView);
		
	   	messageLabel.setText("Processing time: " + (System.currentTimeMillis() - startTime) + " ms");
	}
	

	



}
