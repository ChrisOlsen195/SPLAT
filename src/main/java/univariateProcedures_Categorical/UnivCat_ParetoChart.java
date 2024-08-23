/****************************************************************************
 *                       UnivCat_ParetoChart                                * 
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

public class UnivCat_ParetoChart {
    // POJOs
    boolean dragging;
    
    int nCategories, nValues;
    int[] model_ObservedCounts, sortIndex, my_ObservedCounts, cumFrequencies;

    double initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
           yPix_InitialPress, yPix_MostRecentDragPoint, newY_Lower, newY_Upper,
           deltaY, dispLowerBound, dispUpperBound, initHoriz, initVert, 
           initWidth, initHeight, daXCatStartPosition, daYCatStartPosition, 
           startXPosition, startYPosition, daXCatEndPosition, daYCatEndPosition,
           endXPosition, endYPosition;

    String[] strMyCategoryLabels;
    
    String waldoFile = "UnivCat_ParetoChart";
    //String waldoFile = "";  
    
    ObservableList<String> model_CategoryLabels, my_CategoryLabels; 

    // My classes
    Data_Manager dm;
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis yAxis;
    UnivCat_Model univCat_Model;    
    
    // POJOs / FX
    AnchorPane anchorPane;    
    Canvas freqDistrCanvas;
    CategoryAxis xAxis;
    GraphicsContext gc; 
    Pane theContainingPane;  
    Text txtTitle1, txtTitle2;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;
    
    public UnivCat_ParetoChart(UnivCat_Model univCat_Model, UnivCat_Dashboard univCat_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        dm = univCat_Model.getDataManager();
        dm.whereIsWaldo(72, waldoFile, "Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        model_CategoryLabels = FXCollections.observableArrayList();
        my_CategoryLabels = FXCollections.observableArrayList();
        model_CategoryLabels = univCat_Model.getCategoryLabels();
        this.univCat_Model = univCat_Model;
        
        nCategories = univCat_Model.getNCategories();
        nValues = (int)(univCat_Model.getObservedTotal());
        model_ObservedCounts = univCat_Model.getObservedCounts();
        
        sortIndex = new int[nCategories];
        my_ObservedCounts = new int[nCategories];
        strMyCategoryLabels = new String[nCategories]; 
        cumFrequencies = new int[nCategories];
        
        cumFrequencies[0] = my_ObservedCounts[0];
        
        for (int ithFreq = 1; ithFreq < nCategories; ithFreq++) {
            cumFrequencies[ithFreq] = cumFrequencies[ithFreq - 1] + my_ObservedCounts[ithFreq];
        }
        
    //       ***************Simple bubble sort  ***********************
        for (int ithFreq = 0; ithFreq < nCategories; ithFreq++) {
            sortIndex[ithFreq] = ithFreq;
            my_ObservedCounts[ithFreq] = model_ObservedCounts[ithFreq];
            strMyCategoryLabels[ithFreq] = model_CategoryLabels.get(ithFreq);
        }
        
        int tempInt;
        String tempStr;
        
        for (int kth = 1; kth < nCategories; kth++) {            
            for (int ith = 0; ith < nCategories - kth ; ith++) {
                if (my_ObservedCounts[ith] < my_ObservedCounts[ith + 1]) {                    
                   tempInt = my_ObservedCounts[ith];
                   my_ObservedCounts[ith] = my_ObservedCounts[ith + 1];
                   my_ObservedCounts[ith + 1] = tempInt;
                   
                   tempInt = sortIndex[ith];
                   sortIndex[ith] = sortIndex[ith + 1];
                   sortIndex[ith + 1] = tempInt;

                   tempStr = strMyCategoryLabels[ith];
                   strMyCategoryLabels[ith] = strMyCategoryLabels[ith + 1];
                   strMyCategoryLabels[ith + 1] = tempStr;
                   
                   tempInt = cumFrequencies[ith];
                   cumFrequencies[ith] = cumFrequencies[ith + 1];
                   cumFrequencies[ith + 1] = tempInt;
                }
            }
        }
    
        // Must be sorted first...
        cumFrequencies[0] = my_ObservedCounts[0];
        for (int ithFreq = 1; ithFreq < nCategories; ithFreq++) {
            cumFrequencies[ithFreq] = cumFrequencies[ithFreq - 1] + my_ObservedCounts[ithFreq];
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
        txtTitle1 = new Text("Pareto Chart"); 
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
        //  Find max observed and expected values for the vertical axis
        
        initial_yMax = 0.0;
        for (int i = 0; i < nCategories; i++) {
            if (model_ObservedCounts[i] > initial_yMax)
                initial_yMax = model_ObservedCounts[i];           
        }
        
        initial_yMax = nValues;        
        anchorPane = new AnchorPane();        
        initial_yMax *= 1.025; //  create room for title.          
        initial_yRange = initial_yMax - initial_yMin;
        yMin = initial_yMin;
        yMax = initial_yMax;
        yRange = initial_yRange;
        
        for (int iCats = 0; iCats < nCategories; iCats++) {
            my_CategoryLabels.add(strMyCategoryLabels[iCats]);
        }
    
        // This constant controls the rate of scale change when dragging
        deltaY = 0.005 * yRange;       
        xAxis = new CategoryAxis(my_CategoryLabels);
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
            //  daXCatPosition is generated by CategoryAxis when AutoRanging == true
            double daXCatPosition = xAxis.getDisplayPosition(my_CategoryLabels.get(theCategory));
            //  Plot the observed rectangles
            double daYCatPosition = yAxis.getDisplayPosition(model_ObservedCounts[sortIndex[theCategory]]);   
            double observedBarHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(my_ObservedCounts[theCategory]); 
            gc.setStroke(Color.BLUE);   // For observed counts
            gc.setFill(Color.BLUE);
            gc.fillRect(daXCatPosition - 4 * spaceFraction + 20, daYCatPosition, 4 * spaceFraction, observedBarHeight);                
        }   
        
        gc.setStroke(Color.RED);        
        for (int zzz = 0; zzz < nCategories - 1; zzz++) {
            daXCatStartPosition = xAxis.getDisplayPosition(my_CategoryLabels.get(zzz));
            daYCatStartPosition = yAxis.getDisplayPosition(cumFrequencies[zzz]);
            startXPosition = daXCatStartPosition - 4 * spaceFraction + 60;
            startYPosition = daYCatStartPosition;
            daXCatEndPosition = xAxis.getDisplayPosition(my_CategoryLabels.get(zzz+1));
            daYCatEndPosition = yAxis.getDisplayPosition(cumFrequencies[zzz+1]);
            endXPosition = daXCatEndPosition - 4 * spaceFraction + 60;
            endYPosition = daYCatEndPosition;
            gc.strokeLine(startXPosition, startYPosition, endXPosition, endYPosition);
            gc.strokeRect(startXPosition - 2, startYPosition - 2, 4, 4);
            gc.strokeRect(endXPosition - 2, endYPosition - 2, 4, 4);
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
        public void handle(MouseEvent mouseEvent)  {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)  { 
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
                    if (frac < 0.5)   {
                        if (!yAxis.getHasForcedHighScaleEnd())
                            newY_Upper = yAxis.getUpperBound() + deltaY;
                    }
                    else {
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
     
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
        public void handle(MouseEvent mouseEvent)  {  }
    }; 

    public Pane getTheContainingPane() { return theContainingPane; }
}


