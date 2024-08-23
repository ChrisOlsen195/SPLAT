/****************************************************************************
 *                       UnivCat_FreqDistr                                  * 
 *                           02/19/24                                       *
 *                            15:00                                         *
 ***************************************************************************/
package univariateProcedures_Categorical;

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
import javafx.scene.text.*;
import javafx.scene.layout.AnchorPane;
import javafx.collections.FXCollections;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import splat.Data_Manager;

public class UnivCat_RelFreqDistr {
    // POJOs
    boolean dragging;
    
    int nCategories;
    int[] observedValues;
    double[] relFreqs;

    double initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
           yPix_InitialPress, yPix_MostRecentDragPoint, newY_Lower, newY_Upper,
           deltaY, dispLowerBound, dispUpperBound, initHoriz, initVert, 
           initWidth, initHeight, nTotal;

    //String waldoFile = "UnivCat_PrintStats";
    String waldoFile = ""; 
    
    ObservableList<String> allTheLabels, categoryLabels; 

    // My classes
    Data_Manager dm;
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis yAxis;
    UnivCat_Model univCat_Model;    
    
    // POJOs / FX
    AnchorPane anchorPane;    
    Canvas freqDistrCanvas;
    CategoryAxis xAxis;
    GraphicsContext gc; // Required for drawing on the Canvas
    Pane theContainingPane;  
    Text txtTitle1, txtTitle2;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;
    
    public UnivCat_RelFreqDistr(UnivCat_Model univCat_Model, UnivCat_Dashboard univCat_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        dm = univCat_Model.getDataManager();
        dm.whereIsWaldo(69, waldoFile, "Constructing"); 
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.univCat_Model = univCat_Model;
        allTheLabels = FXCollections.observableArrayList();
        categoryLabels = FXCollections.observableArrayList();
        allTheLabels = univCat_Model.getCategoryLabels();
        
        nCategories = univCat_Model.getNCategories();
        observedValues = univCat_Model.getObservedCounts();
        nTotal = univCat_Model.getObservedTotal();
        relFreqs = new double[nCategories];
        
        initial_yMax = 0.0; 
        for (int ithCat = 0; ithCat < nCategories; ithCat++) {
            relFreqs[ithCat] = observedValues[ithCat] / nTotal;
            initial_yMax = Math.max(initial_yMax, relFreqs[ithCat]);
        }

        theContainingPane = new Pane();
        freqDistrCanvas = new Canvas(initWidth, initHeight);
        gc = freqDistrCanvas.getGraphicsContext2D();
        freqDistrCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        freqDistrCanvas.widthProperty().addListener(ov-> {doTheGraph();}); 
    }
    
    public void completeTheDeal() {
        initializeGraphParams();
        setUpUI();       
        setUpGridPane();        
        setHandlers();   
        theContainingPane = dragableAnchorPane.getTheContainingPane();  
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);        
    }
  
    private void setUpUI() {         
        txtTitle1 = new Text("Relative Frequency Distribution");
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));  
        txtTitle2 = new Text (univCat_Model.getDescriptionOfVariable());
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,16)); 
    }
    
    private void setUpGridPane() {
        dragableAnchorPane = new DragableAnchorPane();
        freqDistrCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        freqDistrCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);  
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(txtTitle1, txtTitle2, freqDistrCanvas, xAxis, yAxis);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
            
    private void initializeGraphParams() {
        initial_yMin = 0.0;       
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
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);      
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
         
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(txtTitle1, 0.06 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.10 * tempHeight);
      
        AnchorPane.setTopAnchor(txtTitle2, 0.11 * tempHeight);
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
        
        AnchorPane.setTopAnchor(freqDistrCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(freqDistrCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(freqDistrCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(freqDistrCanvas, 0.1 * tempHeight);
        
        gc.clearRect(0, 0 , freqDistrCanvas.getWidth(), freqDistrCanvas.getHeight());   
        double spacing = 100.0;
        double spaceFraction = 0.10 * spacing;  //  Controls widths and separations?
        gc.setLineWidth(2);

        for (int theCategory = 0; theCategory < nCategories; theCategory++) {
            //  daXPosition is generated by CategoryAxis when AutoRanging == true
            double daXPosition = xAxis.getDisplayPosition(categoryLabels.get(theCategory));
            //  Plot the observed rectangles
            double observedBarTop = yAxis.getDisplayPosition(relFreqs[theCategory]);
            double observedBarHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(relFreqs[theCategory]);   
            gc.setStroke(Color.BLUE);   // For observed counts
            gc.setFill(Color.BLUE);
            gc.fillRect(daXPosition - 4 * spaceFraction + 20, observedBarTop, 4 * spaceFraction, observedBarHeight);                     
        } 
        
        theContainingPane.requestFocus();
        theContainingPane.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                WritableImage writableImage = theContainingPane.snapshot(new SnapshotParameters(), null);
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
                dispUpperBound = yAxis.getDisplayPosition(yAxis.getUpperBound());

                double frac = mouseEvent.getY() / dispLowerBound;

                if((yPix_Dragging > yPix_InitialPress) && (yPix_Dragging > yPix_MostRecentDragPoint)) {    
                    if (frac < 0.5) {
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
                    if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint)) {   
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
                dispUpperBound = yAxis.getDisplayPosition(yAxis.getUpperBound());
                
                yPix_MostRecentDragPoint = mouseEvent.getY();
                doTheGraph();
            } 
        } 
    };   
     
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) { }
    }; 

    public Pane getTheContainingPane() { return theContainingPane; }
}


