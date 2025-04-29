/****************************************************************************
 *                      X2GOF_ResidualsView                                 * 
 *                           01/15/25                                       *
 *                            12:00                                         *
 ***************************************************************************/
package chiSquare.GOF;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.collections.FXCollections;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;

public class X2GOF_ResidualsView {
    // POJOs
    boolean dragging;
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int nCategories;
    
    double initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
           residScaleBoundary, sig05Line, yPix_InitialPress, 
           yPix_MostRecentDragPoint, newY_Lower, initVert, initWidth,
           newY_Upper, deltaY, dispLowerBound, initHoriz, initHeight;
    
    double[] standResids;
    
    ObservableList<String> allTheLabels; 
    ObservableList<String> categoryLabels;
    
    // My classes
    AnchorPane anchorPane;
    DragableAnchorPane dragableAnchorPane;   
    JustAnAxis yAxis;    
    
    // POJOs / FX
    Canvas canvasResiduals;
    CategoryAxis xAxis;
    GraphicsContext gc; // Required for drawing on the Canvas
    Pane containingPane;
    Text txtTitle1, txtTitle2;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;

    public X2GOF_ResidualsView(X2GOF_Model x2GOF_Model, X2GOF_Dashboard x2GOF_Dashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("69 *** X2GOF_ResidualsView, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        allTheLabels = FXCollections.observableArrayList();
        categoryLabels = FXCollections.observableArrayList();
        allTheLabels = x2GOF_Model.getCategoryLabels();

        nCategories = x2GOF_Model.getNCategories(); 
      
        standResids = x2GOF_Model.getStandResids(); 
        
        containingPane = new Pane();
        canvasResiduals = new Canvas(initWidth, initHeight);

        gc = canvasResiduals.getGraphicsContext2D(); 
        canvasResiduals.heightProperty().addListener(ov-> {doTheGraph();});
        canvasResiduals.widthProperty().addListener(ov-> {doTheGraph();});

        residScaleBoundary = 3.00;
        sig05Line = 1.96;
    }
    
    public void completeTheDeal() {
        if (printTheStuff == true) {
            System.out.println("95 --- X2GOF_ResidualsView, completeTheDeal()");
        }
        initializeGraphParameters();
        setUpUI();       
        setUpGridPane();        
        setHandlers();    
        containingPane = dragableAnchorPane.getTheContainingPane(); 
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);
    }
    
    private void setUpUI() {
        txtTitle1 = new Text(50, 25, "Standardized residuals");
        txtTitle2 = new Text (60, 45, "Bars indicate significance at the .05 level");
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15));      
    }

    private void setUpGridPane() {
        dragableAnchorPane = new DragableAnchorPane();
        canvasResiduals.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        canvasResiduals.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                                 .getChildren()
                                 .addAll(txtTitle1, txtTitle2, xAxis, yAxis, canvasResiduals);
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    private void initializeGraphParameters() {
        //System.out.println("121 *** X2GOF_ResidualsView, initializeGraphParameters()");
        initial_yMin = 0.0; initial_yMax = 0.0;

        for (int i = 0; i < nCategories; i++) {
            if ( Math.abs(standResids[i]) > residScaleBoundary)
                residScaleBoundary = Math.abs(standResids[i]);
        }
        
        anchorPane = new AnchorPane();
        residScaleBoundary = residScaleBoundary + 0.25; //  Keep bars from title       
        initial_yRange = initial_yMax - initial_yMin;
        yMin = -residScaleBoundary; yMax = residScaleBoundary;
        yRange = initial_yRange;
        
        for (int iCats = 0; iCats < nCategories; iCats++) {
            categoryLabels.add(allTheLabels.get(iCats));
        }

        xAxis = new CategoryAxis(categoryLabels);
        xAxis.setSide(Side.BOTTOM);
        xAxis.setAutoRanging(true);
        // xAxis.setLabel(/*   */);
        xAxis.setMinWidth(40);
        xAxis.setPrefWidth(40);
        
        xAxis.setPrefSize(anchorPane.getWidth() - 50, 40);
        xAxis.setLayoutX(500); xAxis.setLayoutY(50);
        
        // This constant controls the rate of scale change when dragging
        deltaY = 0.005 * yRange;
        yAxis = new JustAnAxis(yMin, yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setPrefSize(20, anchorPane.getHeight() - 50);
        yAxis.setLayoutX(500); yAxis.setLayoutY(50);
        // yAxis.setLabel( /*   */);
    }

    public void doTheGraph() {
        //System.out.println("159 *** X2GOF_ResidualsView, doTheGraph()");
        double standResidBarTop, standResidBarHeight, xStart, xStop, yStart, yStop;
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = containingPane.getWidth();

        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);        
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        AnchorPane.setTopAnchor(txtTitle1, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.1 * tempHeight);
                
        AnchorPane.setTopAnchor(txtTitle2, 0.1 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle2, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.1 * tempHeight);
        
        AnchorPane.setTopAnchor(canvasResiduals, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(canvasResiduals, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(canvasResiduals, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(canvasResiduals, 0.1 * tempHeight);
        
        gc.clearRect(0, 0 , canvasResiduals.getWidth(), canvasResiduals.getHeight());        
        double spacing = 100.0;
        double spaceFraction = 0.10 * spacing;  //  Controls widths and separations?
        
        xStart = xAxis.getDisplayPosition(categoryLabels.get(0)) - 0.5 * spacing;
        xStop = xAxis.getDisplayPosition(categoryLabels.get(nCategories - 1)) + 0.5 * spacing;
        
        yStart = yAxis.getDisplayPosition(0.0);
        yStop = yAxis.getDisplayPosition(0.0);            
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.fillRect(xStart - 2, yStart - 2, xStop - xStart, 2);        
        gc.setLineWidth(2);
        
        for (int theCategory = 0; theCategory < nCategories; theCategory++) {
            //  daXPosition is generated by CategoryAxis when AutoRanging == true
            double daXPosition = xAxis.getDisplayPosition(categoryLabels.get(theCategory));

            //  Plot the observed rectangles
            if (standResids[theCategory] > 0) {
                standResidBarTop = yAxis.getDisplayPosition(standResids[theCategory]);
                standResidBarHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(standResids[theCategory]);  
            } else
            {
                standResidBarTop = yAxis.getDisplayPosition(0.0);
                standResidBarHeight = yAxis.getDisplayPosition(standResids[theCategory]) - yAxis.getDisplayPosition(0.0);
            }
            
            gc.setStroke(Color.BLUEVIOLET); 
            gc.setFill(Color.BLUEVIOLET);
            gc.fillRect(daXPosition - 2 * spaceFraction, standResidBarTop, 4 * spaceFraction, standResidBarHeight);           
        }   //  Loop through batches   
        
        gc.setStroke(Color.RED);
        gc.setFill(Color.RED);
        
        yStart = yAxis.getDisplayPosition(sig05Line);
        yStop = yAxis.getDisplayPosition(sig05Line);            
        gc.fillRect(xStart - 1, yStart - 1, xStop - xStart, 2);         
        
        yStart = yAxis.getDisplayPosition(-sig05Line);
        yStop = yAxis.getDisplayPosition(-sig05Line);            
        gc.fillRect(xStart - 1, yStart - 1, xStop - xStart, 2); 
        
        anchorPane.requestFocus();
        anchorPane.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                WritableImage writableImage = anchorPane.snapshot(new SnapshotParameters(), null);
                ImageView iv = new ImageView(writableImage);
                clipboard = Clipboard.getSystemClipboard();
                content = new ClipboardContent();
                content.put(DataFormat.IMAGE, writableImage);
                clipboard.setContent(content);
            }
        }));        
    }
    
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) { }
    }; 

    private void setHandlers() {       
        yAxis.setOnMouseDragged(yAxisMouseHandler); 
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler); 
    }
  
     EventHandler<MouseEvent> yAxisMouseHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) { 
                yPix_InitialPress = mouseEvent.getY(); 
                yPix_MostRecentDragPoint = mouseEvent.getY();
                dragging = false;
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (dragging) {
                    yAxis.setLowerBound(newY_Lower ); 
                    yAxis.setUpperBound(newY_Upper );
                    dragging = false;
                }
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                dragging = true;
                double yPix_Dragging = mouseEvent.getY();  

                newY_Lower = yAxis.getLowerBound();
                newY_Upper = yAxis.getUpperBound(); 
 
                dispLowerBound = yAxis.getDisplayPosition(yAxis.getLowerBound());
                
                double frac = mouseEvent.getY() / dispLowerBound;
                
                if((yPix_Dragging > yPix_InitialPress) && (yPix_Dragging > yPix_MostRecentDragPoint)) {    
                    if (frac < 0.5) {
                        newY_Upper = yAxis.getUpperBound() + deltaY;
                    }
                    else {
                        newY_Lower = yAxis.getLowerBound() + deltaY;
                    }
                }
                else 
                if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint)) {   
                    if (frac < 0.5) {
                        newY_Upper = yAxis.getUpperBound() - deltaY;
                    }
                    else {
                        newY_Lower = yAxis.getLowerBound() - deltaY;
                    }
                }    

                //yAxis.setLowerBound(newY_Lower ); 
                yAxis.setUpperBound(newY_Upper ); 

                dispLowerBound = yAxis.getDisplayPosition(yAxis.getLowerBound());
                yPix_MostRecentDragPoint = mouseEvent.getY();
                doTheGraph();
            }
        }
    };   
     
    EventHandler<MouseEvent> scatterplotMouseHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)  {   
                yPix_InitialPress = mouseEvent.getY();  
            }
        }
    };      
    
    public Pane getTheContainingPane() { return containingPane; }
}

