// BV Ue1 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-11

package bv_ws1718;

import java.io.File;

import bv_ws1718.Filter.BorderProcessing;
import bv_ws1718.Filter.FilterType;
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
    private Slider noiseQuantitySlider;

    @FXML
    private Label noiseQuantityLabel;

    @FXML
    private Slider noiseStrengthSlider;

    @FXML
    private Label noiseStrengthLabel;

    @FXML
    private Slider kernelSizeSlider;

    @FXML
    private Label kernelSizeLabel;

    @FXML
    private Label kernelTitleLabel;

    @FXML
    private ComboBox<FilterType> filterSelection;

    @FXML
    private ComboBox<BorderProcessing> borderProcessingSelection;

    @FXML
    private ImageView originalImageView;

    @FXML
    private ImageView noisyImageView;

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
    
//    @FXML
//    void convertToGray() {
//    	
//    	if(img.isGray) {
//    		img.isGray = false;
//    		img = new RasterImage(new File(initialFileName));
//    		img.setToView(originalImageView);
//    		processImages();
//    	}else {
//    		img.isGray = true;
//    		img.convertToGray();
//    		img.setToView(originalImageView);
//    		processImages();
//    	}
//    }
	
    @FXML
    void borderProcessingChanged() {
    	processImages();
    }

    @FXML
    void filterChanged() {
    	boolean noKernel = filterSelection.getValue() == FilterType.COPY;
    	kernelSizeSlider.setDisable(noKernel);
    	kernelSizeLabel.setDisable(noKernel);
    	kernelTitleLabel.setDisable(noKernel);
    	processImages();
    }

    @FXML
    void noiseQuantityChanged() {
    	noiseQuantity = noiseQuantitySlider.getValue();
    	noiseQuantityLabel.setText(String.format("%.0f %%", noiseQuantity * 100));
    	processImages();
    }
    
    @FXML
    void noiseStrengthChanged() {
    	noiseStrength = (int)noiseStrengthSlider.getValue();
    	noiseStrengthLabel.setText("" + noiseStrength);
    	processImages();
    }
    
    @FXML
    void kernelSizeChanged() {
    	kernelSize = (int)kernelSizeSlider.getValue() | 1; // ensure odd integer value
    	kernelSizeLabel.setText(kernelSize + " x " + kernelSize);
    	processImages();
    }
    
	@FXML
	public void initialize() {
		// set combo boxes items
		filterSelection.getItems().addAll(FilterType.values());
		filterSelection.setValue(FilterType.COPY);
		borderProcessingSelection.getItems().addAll(BorderProcessing.values());
		borderProcessingSelection.setValue(BorderProcessing.CONTINUE);
		
		// initialize parameters
		noiseQuantityChanged();
		noiseStrengthChanged();
		kernelSizeChanged();
		filterChanged();
		
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
		RasterImage noisyImg = new RasterImage(origImg.width, origImg.height); 
		RasterImage filteredImg = new RasterImage(origImg.width, origImg.height); 
		
		filter.copy(origImg, noisyImg);
		noisyImg.addNoise(noiseQuantity, noiseStrength);
		
		
		switch(filterSelection.getValue()) {
		case COPY:
			filter.copy(noisyImg, filteredImg);
			break;
		case BOX:
			filter.box(noisyImg, filteredImg, kernelSize, borderProcessingSelection.getValue());
			break;
		case MEDIAN:
			filter.median(noisyImg, filteredImg, kernelSize, borderProcessingSelection.getValue());
			break;
		default:
			break;
		}
		
		noisyImg.setToView(noisyImageView);
		filteredImg.setToView(filteredImageView);
		
	   	messageLabel.setText("Processing time: " + (System.currentTimeMillis() - startTime) + " ms");
	}
	

	



}
