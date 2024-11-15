/**************************************************
 *         BivCat_SideBySideSegBarChartView       *
 *                    10/15/24                    *
 *                     18:00                      *
 *************************************************/
package bivariateProcedures_Categorical;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import utilityClasses.*;
import utilityClasses.HorizontalPositioner;

public class BivCat_SideBySideSegBarChartView  {
    // POJOs
    
    boolean dragging;
    
    int nLittleSquares, nColsCat, nRowsCat;

    double width, halfWidth, initHoriz, initVert, initWidth,
           initHeight, text1Width, text2Width, maxHeight;
    
    double[] columnProps;
    double[][] cumRowProps;
    
    double  initial_yMin, initial_yMax, initial_yRange, //yMin, yMax, yRange,
            yPix_InitialPress, yPix_MostRecentDragPoint, newY_Lower, 
            newY_Upper, deltaY, dispLowerBound, dispUpperBound, 
            tempPos0, tempPos1, bigTickInterval, yAxisEquals0; 

    String strTopVariable, strLeftVariable, graphsCSS;
    String[] strLeftLabels, strTopLabels;
    
    String  strTitle1, strTitle2, strResponseVar;
    
    ObservableList<String> categoryLabels;
    
    //  My classes
    BivCat_Model bivCat_Model;
    DragableAnchorPane dragableAnchorPane;
    CategoryAxis categoryAxis_X;
    HorizontalPositioner horizontalPositioner;
    JustAnAxis yAxis;

    Point2D horizPosition;
    
    //  POJOs / FX
    AnchorPane anchorPane_BoxPlot;
    Canvas segBarCanvas;
    Color[] graphColors;
    GraphicsContext gc;
    GridPane gridPane_SegBar;
    //HBox anova2CategoryBoxes;
    HBox[] squaresNText;
    Pane theContainingPane;
    Rectangle[] littleSquares;
    Text txtTitle1, txtTitle2;   
    Text[] textForSquares, littleSquaresText;
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;

    public BivCat_SideBySideSegBarChartView(BivCat_Model bivCat_Model, 
            BivCat_Dashboard association_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) 
    {
        //System.out.println("\n102 BivCat_SideBySideSegBarChartView, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;         
        this.bivCat_Model = bivCat_Model;
        graphColors = Colors_and_CSS_Strings.getGraphColors_02();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        theContainingPane = new Pane();
        categoryLabels = FXCollections.observableArrayList(); 
    }
    
    public void completeTheDeal() {
        System.out.println("120 BivCat_SideBySideSegBarChartView, completeTheDeal()");
        constructSideBySideBarInfo();
        txtTitle1 = new Text("Side by Size Bar Chart"); 
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 18));        
        txtTitle1.getStyleClass().add("titleLabel");              
        txtTitle2 = new Text(strLeftVariable + " vs. " + strTopVariable); 
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 8));        
        txtTitle2.getStyleClass().add("titleLabel");   
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        calculateMaxHeight();
        initializeGraphParams();
        setUpUI();
        setUpAnchorPane();
        dragableAnchorPane.heightProperty().addListener(ov-> {doThePlot();});
        dragableAnchorPane.widthProperty().addListener(ov-> {doThePlot();});
        theContainingPane = dragableAnchorPane.getTheContainingPane(); 
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);
    }

    private void initializeGraphParams() { 
        segBarCanvas = new Canvas(625, 625);
        gc = segBarCanvas.getGraphicsContext2D(); 
        
        initial_yMin = 0.0;
        initial_yMax = 1.125 * maxHeight;
        initial_yRange = initial_yMax - initial_yMin;
        
        yAxis = new JustAnAxis(initial_yMin, initial_yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setBounds(initial_yMin, initial_yMax);
        yAxis.forceLowScaleEndToBe(0.0);
        
        categoryAxis_X = new CategoryAxis(categoryLabels);
        categoryAxis_X.setSide(Side.BOTTOM); 
        categoryAxis_X.setAutoRanging(true);
        categoryAxis_X.setLabel(strTopVariable);
        categoryAxis_X.setMinWidth(40);  //  Controls the Min X Axis width (for labels)
        categoryAxis_X.setPrefWidth(40);   
        categoryAxis_X.setLayoutX(500); categoryAxis_X.setLayoutY(25); 
        yAxis.setLayoutX(500); yAxis.setLayoutY(50);
        yAxis.setLabel(strLeftVariable);
        
        setHandlers();
    }
    
    private void setUpUI() {        
        gridPane_SegBar = new GridPane();
        gridPane_SegBar.setAlignment(Pos.CENTER);
        gridPane_SegBar.setStyle("-fx-padding: 2;"+
                                     "-fx-border-style: solid inside;" +
                                     "-fx-border-width: 0;" +
                                     "-fx-border-insets: 5;"+
                                     "-border-radius: 5;");
   
        //  Set up little Squares and text
        nLittleSquares = graphColors.length;
        littleSquares = new Rectangle[nLittleSquares];
        littleSquaresText = new Text[nLittleSquares];
        squaresNText = new HBox[nLittleSquares];
        
        int nGridRow = 0; 
        int nGridCol = 0;
        
        for (int i = 0; i < nRowsCat; i++) {
            littleSquares[i] = new Rectangle(10, 10, 10, 10);
            littleSquares[i].setStroke(graphColors[i]);
            littleSquares[i].setFill(graphColors[i]);
            littleSquaresText[i] = new Text(0, 0, strLeftLabels[i]);
            littleSquaresText[i].setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR,18));
            littleSquaresText[i].setFill(graphColors[i]);
            squaresNText[i] = new HBox(10);
            squaresNText[i].setFillHeight(false);
            squaresNText[i].setAlignment(Pos.CENTER);
            squaresNText[i].setStyle("-fx-padding: 2;" +
                                     "-fx-border-width: 0;" +
                                     "-fx-border-style: solid inside;" +
                                     "-fx-border-insets: 5;" +
                                     "-fx-border-radius: 5;");
            squaresNText[i].getChildren().addAll(littleSquares[i], littleSquaresText[i]);
            nGridCol = i % 6;
            gridPane_SegBar.add(squaresNText[i], nGridCol, nGridRow);
            
            if (nGridCol == 5) { nGridRow++; } 
        }       
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        segBarCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        segBarCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane_BoxPlot = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);   
        
        dragableAnchorPane.getTheAP()
                                 .getChildren() 
                                 .addAll(txtTitle1, txtTitle2, gridPane_SegBar, segBarCanvas, categoryAxis_X, yAxis); 
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void doThePlot() {  
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double hBoxWidth = gridPane_SegBar.getWidth();
        
        double txt1Edge = (paneWidth - text1Width) / (2. * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2. * paneWidth);
        double hBoxEdge = (paneWidth - hBoxWidth) / (2. * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(txtTitle1, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.90 * tempHeight);
        
        AnchorPane.setTopAnchor(txtTitle2, 0.05 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle2, 0.85 * tempHeight);
        
        AnchorPane.setTopAnchor(gridPane_SegBar, 0.15 * tempHeight);
        AnchorPane.setLeftAnchor(gridPane_SegBar, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(gridPane_SegBar, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(gridPane_SegBar, 0.80 * tempHeight);         
        
        AnchorPane.setTopAnchor(categoryAxis_X, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(categoryAxis_X, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(categoryAxis_X, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(categoryAxis_X, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.1 * tempHeight);
        
        AnchorPane.setTopAnchor(segBarCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(segBarCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(segBarCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(segBarCanvas, 0.1 * tempHeight);
        
        tempPos1 = categoryAxis_X.getDisplayPosition(strTopLabels[1]);
        tempPos0 = categoryAxis_X.getDisplayPosition(strTopLabels[0]);
        bigTickInterval = tempPos1 - tempPos0;
        yAxis.setForcedAxisEndsFalse();

        horizontalPositioner = new HorizontalPositioner(nColsCat, nRowsCat, bigTickInterval);        
        gc.clearRect(0, 0 , segBarCanvas.getWidth(), segBarCanvas.getHeight());
        yAxisEquals0 = yAxis.getDisplayPosition(0.0);
        width = 40.;

        for (int col = 0; col < nColsCat; col++) {  
            double daMiddleXPosition = categoryAxis_X.getDisplayPosition(strTopLabels[col]); 
            for (int row = 0; row < nRowsCat; row++) {
                int theAppropriateLevel = (col) * nRowsCat + row;
                horizPosition = horizontalPositioner.getIthLeftPosition(theAppropriateLevel, daMiddleXPosition);
                gc.setFill(graphColors[row]); 
                double preY1 = cumRowProps[row][col] / columnProps[col];
                double preY2 = cumRowProps[row + 1][col] / columnProps[col];
                //System.out.println("296 SideBySide, preY1/2 = " + preY1 + " / " + preY2);
                double y1 = yAxis.getDisplayPosition(preY1);                
                double y2 = yAxis.getDisplayPosition(preY2);  
                double height = Math.abs(y2 - y1);
                gc.fillRect(horizPosition.getX() - halfWidth, yAxisEquals0 - Math.abs(height), width,  Math.abs(height));
                gc.setStroke(Color.BLACK);
                gc.strokeRect(horizPosition.getX() - halfWidth, yAxisEquals0 - Math.abs(height), width, Math.abs(height));
            } 
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
    
    private void constructSideBySideBarInfo() {
        nRowsCat = bivCat_Model.getNumberOfRows();
        nColsCat = bivCat_Model.getNumberOfColumns();

        cumRowProps = new double[nRowsCat + 1][nColsCat + 1];
        cumRowProps = bivCat_Model.getCellCumProps();
        
        columnProps = new double[nColsCat];
        columnProps = bivCat_Model.getColumnProportions();
        
        strLeftLabels = new String[nRowsCat];
        
        strTopVariable = bivCat_Model.getTopVariable();
        strLeftVariable = bivCat_Model.getLeftVariable();
        strLeftLabels = bivCat_Model.getLeftLabels();
        strTopLabels = bivCat_Model.getTopLabels();
        categoryLabels.addAll(strTopLabels);
    }
    
    private void calculateMaxHeight() {
        double tempHeight, preY1, preY2;
        maxHeight = 0.0;

        for (int col = 0; col < nColsCat; col++) {   
            for (int row = 0; row < nRowsCat; row++) {
                preY1 = cumRowProps[row][col] / columnProps[col];
                preY2 = cumRowProps[row + 1][col] / columnProps[col];  
                tempHeight = Math.abs(preY2 - preY1);    
                maxHeight = Math.max(maxHeight, tempHeight);
            } 
        }       
    }
    
    private void setHandlers() {
        yAxis.setOnMouseDragged(yAxisMouseHandler); 
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler); 
    }
      
    public void setColor( int relPos) { 
        gc.setStroke(graphColors[relPos]);
        gc.setFill(graphColors[relPos]);     
    } 

    public Pane getTheContainingPane() { return theContainingPane; }
    
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
                        if (!yAxis.getHasForcedHighScaleEnd()) {
                            newY_Upper = yAxis.getUpperBound() + deltaY;
                        }
                    }
                    else {
                        if (!yAxis.getHasForcedLowScaleEnd()) {
                            newY_Lower = yAxis.getLowerBound() + deltaY;
                        }
                    }
                }
                else 
                    if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint)) {   
                        if (frac < 0.5) {
                            if (!yAxis.getHasForcedHighScaleEnd()) {
                                newY_Upper = yAxis.getUpperBound() - deltaY;
                            }
                        }
                        else {
                            if (!yAxis.getHasForcedLowScaleEnd()) {
                                newY_Lower = yAxis.getLowerBound() - deltaY;
                            }
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
                
                doThePlot();
            }   // end if mouse dragged
        }   //  end handle
    };   
     
    EventHandler<MouseEvent> scatterplotMouseHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {  
                yPix_InitialPress = mouseEvent.getY();  
            }
        }
    }; 
    
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {  }
    };
}
