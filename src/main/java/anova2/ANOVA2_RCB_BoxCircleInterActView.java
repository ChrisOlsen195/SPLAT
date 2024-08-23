/**************************************************
 *           RCB_BoxCircleInterActView            *
 *                  02/19/24                      *
 *                    12:00                       *
 *************************************************/
package anova2;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import javafx.collections.FXCollections;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import splat.Data_Manager;
import utilityClasses.*;

public class ANOVA2_RCB_BoxCircleInterActView extends ANOVA2_Views_Super  { 
    // POJOs
    
    //String waldoFile = "";
    String waldoFile = "RCB_BoxCircleInterActView";
    
    // My Classes
    ANOVA2_RCB_Model anova2_RCB_Model;

    ANOVA2_RCB_BoxCircleInterActView(ANOVA2_RCB_Model anova2_RCB_Model, 
            ANOVA2_RCB_wReplicates_Dashboard rcb_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        super();
        this.anova2_RCB_Model = anova2_RCB_Model;
        dm = anova2_RCB_Model.getDataManager();
        dm.whereIsWaldo(46, waldoFile, "Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        theContainingPane = new Pane();
        
        nFactorA_Levels = anova2_RCB_Model.getNFactorA_Levels();
        nFactorB_Levels = anova2_RCB_Model.getNFactorB_Levels();
        
        categoryLabels = FXCollections.observableArrayList();        
        factorA_Levels = FXCollections.observableArrayList();
        factorB_Levels = FXCollections.observableArrayList();  
        strTopLabels = FXCollections.observableArrayList();
        strLeftLabels = FXCollections.observableArrayList();
        
        preStrTopLabels = anova2_RCB_Model.getFactorALevels();
        preStrLeftLabels = anova2_RCB_Model.getFactorBLevels();
        int nTopLabels = preStrTopLabels.size() - 1;
        int nLeftLabels = preStrLeftLabels.size() - 1;
       
        for (int ithTopLabel = 0; ithTopLabel < nTopLabels; ithTopLabel++) {
            strTopLabels.add(preStrTopLabels.get(ithTopLabel + 1)); 
        }
        
        for (int ithLeftLabel = 1; ithLeftLabel < nLeftLabels; ithLeftLabel++) {
            strLeftLabels.add(preStrLeftLabels.get(ithLeftLabel + 1));
        }
        
        categoryLabels.addAll(strTopLabels);
        factorA_Levels.addAll(anova2_RCB_Model.getFactorALevels());
        factorB_Levels.addAll(anova2_RCB_Model.getFactorBLevels()); 

        allData_UCDO = anova2_RCB_Model.getAllDataUCDO();
        
        strResponseVar = anova2_RCB_Model.getResponseLabel();
        strFactorA = anova2_RCB_Model.getBlockLabel();
        strFactorB = anova2_RCB_Model.getTreatmentLabel();
        
        strTitle1 = strResponseVar + " vs " + strFactorA + " & " + strFactorB;
        strTitle2 = " ";
        
        txtTitle1 = new Text(strTitle1);        
        txtTitle1.getStyleClass().add("titleLabel");              
        txtTitle2 = new Text(strTitle2);        
        txtTitle2.getStyleClass().add("titleLabel");   
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();  
        nFactorA_Levels = this.anova2_RCB_Model.getNFactorA_Levels();
        nFactorB_Levels = this.anova2_RCB_Model.getNFactorB_Levels();
    }
     
    public void completeTheDeal() {
        initializeGraphParams();
        setUpUI();
        initialize();
        setUpAnchorPane();
        dragableAnchorPane.heightProperty().addListener(ov-> {doThePlot();});
        dragableAnchorPane.widthProperty().addListener(ov-> {doThePlot();});
        theContainingPane = dragableAnchorPane.getTheContainingPane(); 
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);
    }

    private void initializeGraphParams() { 
        canvas_ANOVA2 = new Canvas(625, 625);
        gc = canvas_ANOVA2.getGraphicsContext2D(); 
        
        initial_yMin = anova2_RCB_Model.getMinVertical();
        initial_yMax = anova2_RCB_Model.getMaxVertical();
        initial_yRange = initial_yMax - initial_yMin;
        
        yAxis = new JustAnAxis(initial_yMin, initial_yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setBounds(initial_yMin, initial_yMax);
        yMin = initial_yMin; yMax = initial_yMax; yRange = initial_yRange;

        // This constant controls the rate of scale change when dragging
        deltaY = 0.005 * yRange;
        
        categoryAxis_X = new CategoryAxis(categoryLabels);
        categoryAxis_X.setSide(Side.BOTTOM); 
        categoryAxis_X.setAutoRanging(true);
        categoryAxis_X.setLabel(anova2_RCB_Model.getBlockLabel());
        categoryAxis_X.setMinWidth(40);  //  Controls the Min X Axis width (for labels)
        categoryAxis_X.setPrefWidth(40);   
        categoryAxis_X.setLayoutX(500); categoryAxis_X.setLayoutY(25); 
        yAxis.setLayoutX(500); yAxis.setLayoutY(50);
        yAxis.setLabel(anova2_RCB_Model.getResponseLabel());
        
        setHandlers();
    }
    
    private void setUpUI() {        
        canvas_ANOVA2 = new Canvas(0.95 * initWidth, 0.8 * initHeight);
        gc = canvas_ANOVA2.getGraphicsContext2D();
        anova2CategoryBoxes = new HBox(nLittleSquares);
        anova2CategoryBoxes.setAlignment(Pos.CENTER);
        anova2CategoryBoxes.setStyle("-fx-padding: 2;"+
                                     "-fx-border-style: solid inside;" +
                                     "-fx-border-width: 0;" +
                                     "-fx-border-insets: 5;"+
                                     "-border-radius: 5;");
        
        //  Set up little Squares and text
        nLittleSquares = graphColors.length;
        littleSquares = new Rectangle[nLittleSquares];
        littleSquaresText = new Text[nLittleSquares];
        squaresNText = new HBox[nLittleSquares];
        
        for (int i = 0; i < nRowsCat; i++) {
            littleSquares[i] = new Rectangle(10, 10, 10, 10);
            littleSquares[i].setStroke(graphColors[i]);
            littleSquares[i].setFill(graphColors[i]);
            littleSquaresText[i] = new Text(0, 0, strLeftLabels.get(i));
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
            anova2CategoryBoxes.getChildren().add(squaresNText[i]);
        }       
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        canvas_ANOVA2.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        canvas_ANOVA2.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane_BoxPlot = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    

        dragableAnchorPane.getTheAP()
                                 .getChildren()
                                 .addAll(anchorPane_TitleInfo, anova2CategoryBoxes, canvas_ANOVA2, categoryAxis_X, yAxis);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    private void initialize() { 
        // These values are for positioning the colored squares
        nSquaresRow1 = Math.min(4, nFactorB_Levels);
        nSquaresRow2 = nFactorB_Levels - nSquaresRow1;
        startSquareFactor_1 = 1.0 / (2. * (nSquaresRow1 + 1.));
        jumpSquareFactor_1 = 1.0 / (nSquaresRow1 + 1.);
        startSquareFactor_2 = 1.0 / (2. * (nSquaresRow2 + 1.));
        jumpSquareFactor_2 = 1.0 / (nSquaresRow2 + 1.);
        
        anchorPane_TitleInfo = new AnchorPane();
        anchorPane_TitleInfo.getChildren().addAll(txtTitle1, txtTitle2);
        
        //  Set up little Squares and text
        nLittleSquares = graphColors.length;
        littleSquares = new Rectangle[nLittleSquares];
        textForSquares = new Text[nLittleSquares];
        
        for (int i = 0; i < nFactorB_Levels; i++) {
            littleSquares[i] = new Rectangle(10, 10, 10, 10);
            littleSquares[i].setStroke(graphColors[i]);
            littleSquares[i].setFill(graphColors[i]);

            // Formula for placement?  Odd/Even?
            //  The zeroth textForSquares is "All"
            textForSquares[i] = new Text(0, 0, factorB_Levels.get(i + 1));
            textForSquares[i].setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR,14));
            textForSquares[i].setFill(graphColors[i]);
            
            anchorPane_TitleInfo.getChildren().addAll(littleSquares[i]);
            anchorPane_TitleInfo.getChildren().addAll(textForSquares[i]);
        }
    }
    
    public void doThePlot() {
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double hBoxWidth = anova2CategoryBoxes.getWidth();
        
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double hBoxEdge = (paneWidth - hBoxWidth) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();

        AnchorPane.setTopAnchor(anchorPane_TitleInfo, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(anchorPane_TitleInfo, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(anchorPane_TitleInfo, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(anchorPane_TitleInfo, 0.85 * tempHeight);       
        
        AnchorPane.setTopAnchor(anova2CategoryBoxes, 0.15 * tempHeight);
        AnchorPane.setLeftAnchor(anova2CategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(anova2CategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(anova2CategoryBoxes, 0.70 * tempHeight);        
        
        AnchorPane.setTopAnchor(categoryAxis_X, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(categoryAxis_X, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(categoryAxis_X, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(categoryAxis_X, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(canvas_ANOVA2, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(canvas_ANOVA2, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(canvas_ANOVA2, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(canvas_ANOVA2, 0.2 * tempHeight);
        
        tempPos1 = categoryAxis_X.getDisplayPosition(categoryLabels.get(1));
        tempPos0 = categoryAxis_X.getDisplayPosition(categoryLabels.get(0));
        bigTickInterval = tempPos1 - tempPos0;
        yAxis.setForcedAxisEndsFalse();
        positionTopInfo();
        
        horizontalPositioner = new HorizontalPositioner(nFactorA_Levels, nFactorB_Levels, bigTickInterval);        
        gc.clearRect(0, 0 , canvas_ANOVA2.getWidth(), canvas_ANOVA2.getHeight());

        for (int theBetweenBatch = 1; theBetweenBatch <= nFactorA_Levels; theBetweenBatch++) {
            double daMiddleXPosition = categoryAxis_X.getDisplayPosition(categoryLabels.get(theBetweenBatch - 1));            
            for (int theWithinBatch = 1; theWithinBatch <= nFactorB_Levels; theWithinBatch++) {
                int theAppropriateLevel = (theBetweenBatch - 1) * nFactorB_Levels + theWithinBatch;
                tempUCDO = anova2_RCB_Model.getPrelimAB().getIthUCDO(theAppropriateLevel);

                nDataPoints = tempUCDO.getLegalN();
                fiveNumberSummary = new double[5];
                fiveNumberSummary = tempUCDO.get_5NumberSummary();
                whiskerEndRanks = tempUCDO.getWhiskerEndRanks();
                bottomOfLowWhisker = yAxis.getDisplayPosition(fiveNumberSummary[0]);

                if (whiskerEndRanks[0] != -1) {
                    bottomOfLowWhisker = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[0]));
                }
                
                topOfHighWhisker = yAxis.getDisplayPosition(fiveNumberSummary[4]);

                if (whiskerEndRanks[1] != -1) {
                    topOfHighWhisker = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[1]));
                }
                
                double q1_display = yAxis.getDisplayPosition(fiveNumberSummary[1]);
                double q2_display = yAxis.getDisplayPosition(fiveNumberSummary[2]);
                double q3_display = yAxis.getDisplayPosition(fiveNumberSummary[3]);
                double iqr_display = q3_display - q1_display;

                gc.setLineWidth(2);
                gc.setStroke(Color.BLACK);

                setColor(theWithinBatch - 1);              
                horizPosition = horizontalPositioner.getIthLeftPosition(theAppropriateLevel, daMiddleXPosition);
                gc.strokeRect(horizPosition.getX(), q3_display, horizPosition.getY(), -iqr_display);    //  box
                gc.strokeLine(horizPosition.getX(), q2_display, horizPosition.getX() + horizPosition.getY(), q2_display);    //  Median

                gc.strokeLine(horizPosition.getX() + 0.5 * horizPosition.getY(), bottomOfLowWhisker, horizPosition.getX() + 0.5 * horizPosition.getY(), q1_display);  //  Low whisker
                gc.strokeLine(horizPosition.getX() + 0.5 * horizPosition.getY(), q3_display, horizPosition.getX() + 0.5 * horizPosition.getY(), topOfHighWhisker);  //  High whisker

                //  Top & bottom of whisker
                double topBottomLength = horizontalPositioner.getCIEndWidthFrac();
                double midBar = horizontalPositioner.getMidBarPosition(theAppropriateLevel, daMiddleXPosition);
                gc.strokeLine(midBar - topBottomLength, bottomOfLowWhisker, midBar + topBottomLength, bottomOfLowWhisker);  
                gc.strokeLine(midBar - topBottomLength, topOfHighWhisker, midBar + topBottomLength, topOfHighWhisker);  

                if (whiskerEndRanks[0] != -1) {   //  Are there low outliers?
                    int dataPoint = 0;
                    while (dataPoint < whiskerEndRanks[0]) {
                        double xx = horizPosition.getX() + 0.5 * horizPosition.getY();
                        double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                        gc.fillOval(xx - 3, yy - 3, 6, 6);
                        dataPoint++;
                    }
                }

                if (whiskerEndRanks[1] != -1) { //  Are there high outliers?
                    
                    for (int dataPoint = whiskerEndRanks[1] + 1; dataPoint < nDataPoints; dataPoint++) {
                        double xx = horizPosition.getX() + 0.5 * horizPosition.getY();
                        double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                        gc.fillOval(xx - 3, yy - 3, 6, 6);
                    }
                }
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
    
    private void setHandlers() {
        yAxis.setOnMouseDragged(yAxisMouseHandler); 
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler); 
    }    

    public void setColor( int relPos) {           
        gc.setStroke(graphColors[relPos]);
        gc.setFill(graphColors[relPos]);     
    }
    
    public Data_Manager getDataManager() { return dm; }
    public Pane getTheContainingPane() { return theContainingPane; }
}


