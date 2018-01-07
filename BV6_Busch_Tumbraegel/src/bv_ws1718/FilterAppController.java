// BV Ue1 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-11

package bv_ws1718;

import java.io.File;

//import bv_ws1718.Filter.BorderProcessing;
import bv_ws1718.Filter.PredictionType;
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
    private ComboBox<PredictionType> predictionSelection;

//    @FXML
//    private ComboBox<BorderProcessing> borderProcessingSelection;

    @FXML
    private ImageView originalImageView;

    @FXML
    private ImageView predictionImageView;

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
    void predictionChanged() {
    	processImages();
    }

    
	@FXML
	public void initialize() {
		// set combo boxes items
		predictionSelection.getItems().addAll(PredictionType.values());
		predictionSelection.setValue(PredictionType.COPY);
		
		// initialize parameters
		predictionChanged();
		
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
		RasterImage predictionImg = new RasterImage(origImg.width, origImg.height); 
		RasterImage filteredImg = new RasterImage(origImg.width, origImg.height); 
		
		filter.copy(origImg, filteredImg);		
		
		switch(predictionSelection.getValue()) {
		case COPY:
			filter.copy(origImg, predictionImg);
			filter.copy(predictionImg, filteredImg);
			break;
		case A:
			filter.methodA(origImg, predictionImg);
			filter.reconstructA(predictionImg, filteredImg);
			break;
		case B:
			filter.methodB(origImg, predictionImg);
			filter.reconstructB(predictionImg, filteredImg);
			break;
		case C:
			filter.methodC(origImg, predictionImg);
			filter.reconstructC(predictionImg, filteredImg);
			break;
		case AANDBMINUSC:
			filter.methodAAndBMinusC(origImg, predictionImg);
			filter.reconstructAAndBMinusC(predictionImg, filteredImg);
			break;
		case AANDBDIVIDEDBY2:
			filter.methodAAndBDividedBy2(origImg, predictionImg);
			filter.reconstructAAndBDividedBy2(predictionImg, filteredImg);
			break;
		case ADAPTIV:
			filter.methodAdaptive(origImg, predictionImg);
			filter.reconstructAdaptive(predictionImg, filteredImg);
			break;
		default:
			break;
		}
		
		predictionImg.setToView(predictionImageView);
		filteredImg.setToView(filteredImageView);
		
	   	messageLabel.setText("Processing time: " + (System.currentTimeMillis() - startTime) + " ms");
	}
	

	



}
