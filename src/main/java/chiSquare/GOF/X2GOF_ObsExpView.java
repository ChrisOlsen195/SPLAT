/****************************************************************************
 *                       X2GOF_ObsExpView                                   * 
 *                           09/07/24                                       *
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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;

public class X2GOF_ObsExpView {
    // POJOs
    boolean dragging;
    
    int nCategories;
    int[] observedValues;

    double initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
           yPix_InitialPress, yPix_MostRecentDragPoint, newY_Lower, newY_Upper, 
           deltaY, dispLowerBound, initHoriz, initVert, initWidth, initHeight;
    
    double[] expectedValues;
    
    ObservableList<String> allTheLabels, categoryLabels; 

    // My classes
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis yAxis; 
    
    // POJOs / FX
    AnchorPane anchorPane;    
    Canvas canvas_X2GOF_ObjExpView;
    CategoryAxis xAxis;
    GraphicsContext gc; // Required for drawing on the Canvas
    HBox hBoxLabel, hBoxExpLabel, hBoxObsExt; 
    Pane containingPane;  
    Rectangle blueSquare, orangeSquare; 
    Text txtTitle, txtBlue, txtOrange;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;
    
    public X2GOF_ObsExpView(X2GOF_Model x2GOF_Model, X2GOF_Dashboard x2GOF_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        System.out.println("\n70 X2GPF_ObsExpView, Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        allTheLabels = FXCollections.observableArrayList();
        categoryLabels = FXCollections.observableArrayList();
        allTheLabels = x2GOF_Model.getCategoryLabels();
        
        nCategories = x2GOF_Model.getNCategories();
        observedValues = x2GOF_Model.getObservedValues();
        expectedValues = x2GOF_Model.getExpectedValues();

        containingPane = new Pane();
        canvas_X2GOF_ObjExpView = new Canvas(initWidth, initHeight);
        gc = canvas_X2GOF_ObjExpView.getGraphicsContext2D();
        canvas_X2GOF_ObjExpView.heightProperty().addListener(ov-> {doTheGraph();});
        canvas_X2GOF_ObjExpView.widthProperty().addListener(ov-> {doTheGraph();}); 
    }
    
    public void completeTheDeal() {
        System.out.println("90  *** X2GPF_ObsExpView, completeTheDeal()");
        initializeGraphParams();
        setUpUI();       
        setUpGridPane();        
        setHandlers();   
        containingPane = dragableAnchorPane.getTheContainingPane();  
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);        
    }
  
    private void setUpUI() {  
        System.out.println("100  *** X2GPF_ObsExpView, setUpUI()");
        blueSquare = new Rectangle(10, 10, 15, 15);
        blueSquare.setStroke(Color.BLUE);
        blueSquare.setFill(Color.BLUE);
        orangeSquare = new Rectangle(10, 10, 15, 15);
        orangeSquare.setStroke(Color.ORANGE);
        orangeSquare.setFill(Color.ORANGE);   
        
        txtTitle = new Text(250, 25, "Observed & Expected Counts");
        txtTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));  
        txtBlue = new Text(250, 50, "Observed");
        txtBlue.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,18));        
        txtOrange = new Text(250, 50, "Expected");
        txtOrange.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,18));        

        hBoxLabel = new HBox(15.0, blueSquare, txtBlue);
        hBoxExpLabel = new HBox(15.0, orangeSquare, txtOrange);
        hBoxObsExt = new HBox(40.0, hBoxLabel, hBoxExpLabel); 
        hBoxObsExt.setAlignment(Pos.CENTER);     
    }
    
    private void setUpGridPane() {
        System.out.println("122  *** X2GPF_ObsExpView, setUpGridPane()");
        dragableAnchorPane = new DragableAnchorPane();
        canvas_X2GOF_ObjExpView.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        canvas_X2GOF_ObjExpView.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);  
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(hBoxObsExt, txtTitle, canvas_X2GOF_ObjExpView, xAxis, yAxis);
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
            
    private void initializeGraphParams() {
        System.out.println("137  *** X2GPF_ObsExpView, initializeGraphParams()");
        initial_yMin = 0.0;
        //  Find max observed and expected values for the vertical axis
        initial_yMax = 0.0;
        
        for (int i = 0; i < nCategories; i++) {
            if (observedValues[i] > initial_yMax)
                initial_yMax = observedValues[i];
            if (expectedValues[i] > initial_yMax)
                initial_yMax = expectedValues[i];            
        }
        
        anchorPane = new AnchorPane();
        
        initial_yMax *= 1.025; //  create room for title.  
        
        initial_yRange = initial_yMax - initial_yMin;
        yMin = initial_yMin;
        yMax = initial_yMax;
        yRange = initial_yRange;
        
        for (int iCats = 0; iCats < nCategories; iCats++) {
            categoryLabels.add(allTheLabels.get(iCats));
        }
    
        // This constant controls the rate of scale change when dragging
        deltaY = 0.005 * yRange;       
        xAxis = new CategoryAxis(categoryLabels);
        xAxis.setSide(Side.BOTTOM);
        xAxis.setAutoRanging(true);
        // xAxis.setLabel(/*   */);
        xAxis.setMinWidth(40);
        xAxis.setPrefWidth(40);
        
        xAxis.setPrefSize(anchorPane.getWidth() - 50, 40);
        xAxis.setLayoutX(500); xAxis.setLayoutY(50);
        
        yAxis = new JustAnAxis(yMin, yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setPrefSize(20, anchorPane.getHeight() - 50);
        yAxis.setLayoutX(500); yAxis.setLayoutY(50);
        yAxis.forceLowScaleEndToBe(0.0);           
    }

    public void doTheGraph() {
        //System.out.println("182  *** X2GOF_ObsExpView, doTheGraph()");
        double text1Width = txtTitle.getLayoutBounds().getWidth();
        double hBoxWidth = hBoxObsExt.getWidth();
        double paneWidth = dragableAnchorPane.getWidth();

        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double hBoxEdge = (paneWidth - hBoxWidth) / (2 * paneWidth);       
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(txtTitle, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle, 0.1 * tempHeight);
                
        AnchorPane.setTopAnchor(hBoxObsExt, 0.1 * tempHeight);
        AnchorPane.setLeftAnchor(hBoxObsExt, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(hBoxObsExt, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(hBoxObsExt, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.1 * tempHeight);
        
        AnchorPane.setTopAnchor(canvas_X2GOF_ObjExpView, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(canvas_X2GOF_ObjExpView, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(canvas_X2GOF_ObjExpView, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(canvas_X2GOF_ObjExpView, 0.1 * tempHeight);
        
        gc.clearRect(0, 0 , canvas_X2GOF_ObjExpView.getWidth(), canvas_X2GOF_ObjExpView.getHeight());   
        double spacing = 100.0;
        double spaceFraction = 0.10 * spacing;  //  Controls widths and separations?
        gc.setLineWidth(2);
        
        for (int theCategory = 0; theCategory < nCategories; theCategory++) {
            //  daXPosition is generated by CategoryAxis when AutoRanging == true
            double daXPosition = xAxis.getDisplayPosition(categoryLabels.get(theCategory));

            //  Plot the observed rectangles
            double observedBarTop = yAxis.getDisplayPosition(observedValues[theCategory]);
            double observedBarHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(observedValues[theCategory]);        

            gc.setStroke(Color.BLUE);   // For observed counts
            gc.setFill(Color.BLUE);
            gc.fillRect(daXPosition - 4 * spaceFraction, observedBarTop, 4 * spaceFraction, observedBarHeight);
            
            //  Plot the expected rectangles
            double expectedBarTop = yAxis.getDisplayPosition(expectedValues[theCategory]);
            double expectedBarHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(expectedValues[theCategory]);           

            gc.setStroke(Color.ORANGE);   // For expected counts
            gc.setFill(Color.ORANGE);
            gc.fillRect(daXPosition, expectedBarTop, 4 * spaceFraction, expectedBarHeight);            
        }   //  Loop through batches 
        
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
                //dispUpperBound = yAxis.getDisplayPosition(yAxis.getUpperBound());

                double frac = mouseEvent.getY() / dispLowerBound;

                if((yPix_Dragging > yPix_InitialPress) && (yPix_Dragging > yPix_MostRecentDragPoint)) {   
                    
                    if (frac < 0.5)  {
                        if (!yAxis.getHasForcedHighScaleEnd())
                            newY_Upper = yAxis.getUpperBound() + deltaY;
                    }
                    else  
                    {
                        if (!yAxis.getHasForcedLowScaleEnd())
                            newY_Lower = yAxis.getLowerBound() + deltaY;
                    }
                }
                else 
                if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint))  {   
                    if (frac < 0.5) {
                        if (!yAxis.getHasForcedHighScaleEnd())
                            newY_Upper = yAxis.getUpperBound() - deltaY;
                    }
                    else
                    {
                        if (!yAxis.getHasForcedLowScaleEnd())
                            newY_Lower = yAxis.getLowerBound() - deltaY;
                    }
                }    

                if (yAxis.getHasForcedLowScaleEnd()) {
                    newY_Lower = yAxis.getForcedLowScaleEnd();
                }
                
                if (yAxis.getHasForcedHighScaleEnd()) {
                    newY_Upper = yAxis.getForcedHighScaleEnd();
                }
                
                yAxis.setLowerBound(newY_Lower ); 
                yAxis.setUpperBound(newY_Upper ); 

                dispLowerBound = yAxis.getDisplayPosition(yAxis.getLowerBound());
                //dispUpperBound = yAxis.getDisplayPosition(yAxis.getUpperBound());
                
                yPix_MostRecentDragPoint = mouseEvent.getY();
                doTheGraph();
            }   // end if mouse dragged
        }   //  end handle
    };   
     
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) { }
    }; 

    public Pane getTheContainingPane() { return containingPane; }
}

