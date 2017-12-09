// BV Ue4 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-16

package bv_ws1718;

import java.io.File;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

public class ImageAnalysisAppController {
	
	private static final String initialFileName = "mountains.png";
	private static File fileOpenPath = new File(".");
	
	public enum StatsProperty {
		Minimum, Maximum, Mean, Median, Variance, Entropy;
		
	    private final SimpleStringProperty name;
	    private final SimpleStringProperty value;
	    
	    private StatsProperty() {
	        this.name = new SimpleStringProperty(name());
	        this.value = new SimpleStringProperty("n/a");
	    }
	    
	    public String getName() { return name.get(); }
	    public String getValue() { return value.get(); }
	    public void setValue(double value) { this.value.set(String.format("%.2f", value)); }
	    public void setValue(int value) { this.value.set(String.format("%d", value)); }
	}
	
	final ObservableList<StatsProperty> statsData = FXCollections.observableArrayList(StatsProperty.values());
	
	private int brightness;
	private double gamma;
	
	private RasterImage originalImage;
	private ToneCurve toneCurve;
	private Histogram histogram;
	
    private GraphicsContext selectionGC;
    private GraphicsContext histogramGC;
    private GraphicsContext toneCurveGC;
	
    private Rectangle selectionRect;
	private Point2D selectionStartPoint;

    @FXML
    private Slider brightnessSlider;

    @FXML
    private Label brightnessLabel;

    @FXML
    private Slider contrastSlider;

    @FXML
    private Label contrastLabel;

    @FXML
    private Slider gammaSlider;

    @FXML
    private Label gammaLabel;

    @FXML
    private ImageView imageView;

    @FXML
    private Canvas selectionCanvas;

    @FXML
    private Label selectionLabel;

    @FXML
    private Canvas histogramCanvas;

    @FXML
    private Canvas toneCurveCanvas;

    @FXML
    private TableView<StatsProperty> statsTableView;
    
    @FXML
    private TableColumn<StatsProperty, String> statsNamesColoumn;

    @FXML
    private TableColumn<StatsProperty, String> statsValuesColoumn;

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
			originalImage = new RasterImage(selectedFile);
			initImage();
			resetToneCurve();
			messageLabel.getScene().getWindow().sizeToScene();;
		}
    }
	
    @FXML
    void brightnessChanged() {
    	brightness = (int)brightnessSlider.getValue();
    	brightnessLabel.setText("" + brightness);
    	processImage();
    }
    
    @FXML
    void gammaChanged() {
    	gamma = gammaSlider.getValue();
    	gammaLabel.setText(String.format("%.1f", gamma));
    	processImage();
    }
    
    @FXML
    void resetToneCurve() {
    	brightnessSlider.setValue(0);
    	brightnessChanged();
    	gammaSlider.setValue(1);
    	gammaChanged();
    }
        
    @FXML
    void selectionBegan(MouseEvent event) {
    	selectionStartPoint = new Point2D(event.getX(), event.getY());
    	selectionRect.setX(selectionStartPoint.getX());
    	selectionRect.setY(selectionStartPoint.getY());
    	selectionRect.setWidth(0);
    	selectionRect.setHeight(0);
    	processImage();
    }
    
    @FXML
    void selectionResized(MouseEvent event) {
    	double ex = event.getX();
    	double ey = event.getY();
    	if(ex < 0) ex = 0;
    	if(ey < 0) ey = 0;
    	if(ex >= originalImage.width) ex = originalImage.width - 1;
    	if(ey >= originalImage.height) ey = originalImage.height - 1;
    	double width = ex - selectionStartPoint.getX();
    	double height = ey - selectionStartPoint.getY();
    	// set normalized rectangle
    	if(width >= 0) {
    		selectionRect.setX(selectionStartPoint.getX());
    		selectionRect.setWidth(width);
    	} else {
    		selectionRect.setX(ex);
    		selectionRect.setWidth(-width);
    	}
    	if(height >= 0) {
    		selectionRect.setY(selectionStartPoint.getY());
    		selectionRect.setHeight(height);
    	} else {
    		selectionRect.setY(ey);
    		selectionRect.setHeight(-height);
    	}
    	processImage();
    }
    
    @FXML
    void selectionEnded(MouseEvent event) {
    	double width = event.getX() - selectionStartPoint.getX();
    	double height = event.getY() - selectionStartPoint.getY();
    	if(Math.abs(width) <= 1 && Math.abs(height) <= 1) {
    		resetSelection();
    	} else {
    		selectionResized(event);
    	}
    }
    
	@FXML
	public void initialize() {
		// initialize table view
		statsNamesColoumn.setCellValueFactory(new PropertyValueFactory<StatsProperty, String>("name"));
		statsValuesColoumn.setCellValueFactory(new PropertyValueFactory<StatsProperty, String>("value"));
		statsTableView.setItems(statsData);
		statsTableView.setSelectionModel(null);
		
		// initialize parameters
		brightnessChanged();
		gammaChanged();
		
		// load and process default image
		originalImage = new RasterImage(new File(initialFileName));
		initImage();
	}
	
	private void initImage() {
		originalImage.convertToGray();
		originalImage.setToView(imageView);
        selectionCanvas.setWidth(originalImage.width);
        selectionCanvas.setHeight(originalImage.height);
        resetSelection();
	}
	
    void resetSelection() {
        selectionRect = new Rectangle(0, 0, originalImage.width, originalImage.height);
		processImage();
    }
    
	private void drawSelection() {
        selectionGC.clearRect(0, 0, originalImage.width, originalImage.height);
        
        // round to integer positions
        int x = (int)(selectionRect.getX() + 0.5);
        int y = (int)(selectionRect.getY() + 0.5);
        int w = (int)(selectionRect.getWidth() + 0.5);
        int h = (int)(selectionRect.getHeight() + 0.5);
        selectionRect.setX(x);
        selectionRect.setY(y);
        selectionRect.setWidth(w);
        selectionRect.setHeight(h);
        
        selectionGC.setStroke(Color.RED);
        selectionGC.setLineWidth(2);
        selectionGC.strokeRect(x, y, w, h);
		
        selectionLabel.setText(String.format("Selection (x=%d, y=%d, w=%d, h=%d)", x, y, w, h));
	}
	
	private void processImage() {
		if(imageView.getImage() == null)
			return; // no image: nothing to do
		
		long startTime = System.currentTimeMillis();
		
        if (selectionGC == null) {
        	selectionGC = selectionCanvas.getGraphicsContext2D();
        }
        if (histogramGC == null) {
        	histogramGC = histogramCanvas.getGraphicsContext2D();
        	histogram = new Histogram(histogramGC, (int)histogramCanvas.getHeight());
        }
        if (toneCurveGC == null) {
        	toneCurveGC = toneCurveCanvas.getGraphicsContext2D();
        	toneCurve = new ToneCurve(toneCurveGC);
        }
        
		toneCurve.setBrightness(brightness);
		toneCurve.setGamma(gamma);
       
		RasterImage img = new RasterImage(originalImage);
		img.applyToneCurve(toneCurve);
		img.setToView(imageView);
		
		histogram.update(img, selectionRect, statsData);
		
		drawSelection();
		toneCurve.draw();
		histogram.draw();
		statsTableView.refresh();
		
	   	messageLabel.setText("Processing time: " + (System.currentTimeMillis() - startTime) + " ms");
	}
	

	



}
