/**************************************************
 *             ANOVA2_RM_LinePlotView             *
 *                   05/24/24                     *
 *                     12:00                      *
 *************************************************/
package anova2;

import dataObjects.CategoricalDataVariable;
import dataObjects.ColumnOfData;
import genericClasses.DragableAnchorPane;
import genericClasses.JustAnAxis;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ANOVA2_RM_LinePlotView extends ANOVA2_Views_Super { 
    // POJOs
    int nTreatments, nSubjects, nOriginalColumns;
    
    double least_value, greatest_value, daLeftDotPosition, daRightDotPosition;
    Double tempDouble;
    double[][] theNonReplicateData;
    
    String strSubjectLabel, strTreatLabel, tempString;
    
    //String waldoFile = "ANOVA2_RM_LinePlotView";
    String waldoFile = "";
    
    ObservableList<String> strTheSubjects, strTheTreatments;
    
    // My Classes
    ANOVA2_RM_Model anova2_RM_Model;
    ColumnOfData[] col_OriginalData;
    CategoricalDataVariable cdv_TreatValues, cdv_SubjectValues;
    
    // FX Classes
    AnchorPane anchorPane_LinePlotView;

    ANOVA2_RM_LinePlotView(ANOVA2_RM_Model anova2_RM_Model, 
            ANOVA2_RM_Dashboard anova2_RM_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        super();
        this.anova2_RM_Model = anova2_RM_Model;
        dm = anova2_RM_Model.getDataManager();
        grabTheOriginalData();

        dm.whereIsWaldo(67, waldoFile, "Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        
        // These are CategoricalDataVariables
        cdv_SubjectValues = anova2_RM_Model.getRM_TreatmentValues();
        cdv_TreatValues = anova2_RM_Model.getRM_SubjectValues();

        theNonReplicateData = new double[nSubjects][nTreatments];
        
        strSubjectLabel = cdv_SubjectValues.getTheDataLabel();
        strTreatLabel = cdv_TreatValues.getTheDataLabel();
        strTheSubjects = FXCollections.observableArrayList();
        
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        theContainingPane = new Pane();
        
        categoryLabels = FXCollections.observableArrayList();        
        factorB_Levels = FXCollections.observableArrayList();  
        strTheSubjects = FXCollections.observableArrayList();
        strTheTreatments = FXCollections.observableArrayList();

        for (int ithSubject = 0; ithSubject < nSubjects; ithSubject++) {
            tempString = col_OriginalData[0].getStringInIthRow(ithSubject);
            strTheSubjects.add(tempString); 
        }
        
        for (int ithTreatment = 0; ithTreatment < nTreatments; ithTreatment++) {
            tempString = col_OriginalData[ithTreatment + 1].getVarLabel();
            strTheTreatments.add(tempString);
        }
        
        categoryLabels.addAll(strTheTreatments); 
        
        least_value = Double.MAX_VALUE;
        greatest_value = -Double.MAX_VALUE;
        
        for (int ithTreatment = 0; ithTreatment < nTreatments; ithTreatment++) {            
            for (int ithSubject = 0; ithSubject < nSubjects; ithSubject++) {  
                tempString = col_OriginalData[ithTreatment + 1].getStringInIthRow(ithSubject);
                tempDouble = Double.valueOf(tempString);
                theNonReplicateData[ithSubject][ithTreatment] = tempDouble;
                least_value = Math.min(least_value, tempDouble);
                greatest_value = Math.max(greatest_value, tempDouble);                     
            }
        }  
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
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        canvas_ANOVA2.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        canvas_ANOVA2.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane_LinePlotView = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);            
        dragableAnchorPane.getTheAP()
                         .getChildren()
                         .addAll(anchorPane_TitleInfo, anova2CategoryBoxes, canvas_ANOVA2, categoryAxis_X, yAxis); // !
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
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

        if (nSubjects <= nLittleSquares) {            
            for (int i = 0; i < nSubjects; i++) {
                littleSquares[i] = new Rectangle(10, 10, 10, 10);
                littleSquares[i].setStroke(graphColors[i]);
                littleSquares[i].setFill(graphColors[i]);
                tempString = col_OriginalData[0].getStringInIthRow(i);
                littleSquaresText[i] = new Text(0, 0, tempString);
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
    }
    
    private void initialize() { 
        // These values are for positioning the colored squares
        nSquaresRow1 = Math.min(4, nTreatments);
        startSquareFactor_1 = 1.0 / (2. * (nSquaresRow1 + 1.));
        jumpSquareFactor_1 = 1.0 / (nSquaresRow1 + 1.);
        nSquaresRow2 = 0;
        
        if (nTreatments > 4) {
            nSquaresRow2 = nTreatments - nSquaresRow1;
            startSquareFactor_2 = 1.0 / (2. * (nSquaresRow2 + 1.));
            jumpSquareFactor_2 = 1.0 / (nSquaresRow2 + 1.);
        }

        //  Set up little Squares and text
        littleSquares = new Rectangle[nLittleSquares];
        textForSquares = new Text[nLittleSquares];
        
        for (int i = 0; i < nTreatments; i++) {
            littleSquares[i] = new Rectangle(10, 10, 10, 10);
            littleSquares[i].setStroke(graphColors[i]);
            littleSquares[i].setFill(graphColors[i]);

            // Formula for placement?  Odd/Even?
            //  The zeroth textForSquares is "All"
            textForSquares[i] = new Text(0, 0, col_OriginalData[i+1].getVarLabel());
            textForSquares[i].setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR,14));
            textForSquares[i].setFill(graphColors[i]);
        }
    }
        
    public void doThePlot() {
        double xxLeft, xxRight, yyLeft, yyRight;
       
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double hBoxWidth = anova2CategoryBoxes.getWidth();
        
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double hBoxEdge = (paneWidth - hBoxWidth) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(txtTitle1, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.85 * tempHeight);

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
        
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        
        tempPos1 = categoryAxis_X.getDisplayPosition(categoryLabels.get(1));      
        tempPos0 = categoryAxis_X.getDisplayPosition(categoryLabels.get(0));      
        bigTickInterval = tempPos1 - tempPos0;
        yAxis.setForcedAxisEndsFalse();
        positionTopInfo();  
              
        gc.clearRect(0, 0 , canvas_ANOVA2.getWidth(), canvas_ANOVA2.getHeight());
        gc.setLineWidth(2);

        for (int jthSubject = 0; jthSubject < nSubjects; jthSubject++) {
            gc.setFill(graphColors[jthSubject % nLittleSquares]);
            gc.setStroke(graphColors[jthSubject % nLittleSquares]);            
            for (int ithTreatment = 0; ithTreatment < nTreatments; ithTreatment++) {                
                if (ithTreatment == 0) {
                    //System.out.println("312 RCB_LinePlotView, ithTreatment = 0");
                    /*
                    daLeftDotPosition = categoryAxis_X.getDisplayPosition(strTheTreatments.get(0));
                    daRightDotPosition = categoryAxis_X.getDisplayPosition(strTheTreatments.get(1));
                    xxLeft = daLeftDotPosition;
                    xxRight = daRightDotPosition;
                    yyLeft = yAxis.getDisplayPosition(theNonReplicateData[0][ithTreatment]); 
                    yyRight = yAxis.getDisplayPosition(theNonReplicateData[1][ithTreatment]);
                    gc.moveTo(xxLeft, yyLeft);
                    gc.setLineWidth(2);
                    gc.strokeLine(xxLeft, yyLeft, xxRight, yyRight); 
                    */
                } else {
                    daLeftDotPosition = categoryAxis_X.getDisplayPosition(strTheTreatments.get(ithTreatment - 1));
                    daRightDotPosition = categoryAxis_X.getDisplayPosition(strTheTreatments.get(ithTreatment));
                    xxLeft = daLeftDotPosition;
                    xxRight = daRightDotPosition;
                    yyLeft = yAxis.getDisplayPosition(theNonReplicateData[jthSubject][ithTreatment - 1]); 
                    yyRight = yAxis.getDisplayPosition(theNonReplicateData[jthSubject][ithTreatment]);
                    gc.fillOval(xxLeft - 4, yyLeft - 4, 8, 8);
                    gc.fillOval(xxRight - 4, yyRight - 4, 8, 8);
                    gc.moveTo(xxLeft, yyLeft);
                    gc.setLineWidth(2);
                    gc.strokeLine(xxLeft, yyLeft, xxRight, yyRight);
                }
            }   //  Loop through the blocks
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
    }   // End Plot
   
    private void initializeGraphParams() { 
        /****************************************************************
        * Find range of means for both levels.  This will determine the *
        * initial vertical scale limits.                                *
        ****************************************************************/
        canvas_ANOVA2 = new Canvas(625, 625);
        gc = canvas_ANOVA2.getGraphicsContext2D(); 

        anchorPane_TitleInfo = new AnchorPane();
        strTitle1 = "Repeated measures: " + strTreatLabel + " x " + strSubjectLabel;
        
        strTitle2 = " ";
        txtTitle1 = new Text(strTitle1);    
        txtTitle2 = new Text(strTitle2);
        txtTitle1.getStyleClass().add("titleLabel");                
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        anchorPane_TitleInfo.getChildren().addAll(txtTitle1, txtTitle2);
        initial_yMin = least_value;
        initial_yMax = greatest_value;
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
        categoryAxis_X.setLabel(anova2_RM_Model.getTreatmentLabels());
        categoryAxis_X.setMinWidth(40);  //  Controls the Min X Axis width (for labels)
        categoryAxis_X.setPrefWidth(40);   
        categoryAxis_X.setLayoutX(500); categoryAxis_X.setLayoutY(25); 
        yAxis.setLayoutX(500); yAxis.setLayoutY(50);
        yAxis.setLabel(anova2_RM_Model.getResponseLabel());
        
        setHandlers();
    }
    
    private void grabTheOriginalData() {
        nOriginalColumns = dm.getNVarsInStruct();
        nTreatments = nOriginalColumns - 1;
        col_OriginalData = new ColumnOfData[nTreatments+1];
        
        for (int ithCol = 0; ithCol < nOriginalColumns; ithCol++) {
            col_OriginalData[ithCol] = dm.getAllTheColumns().get(ithCol);
        }
        
        nSubjects = col_OriginalData[0].getNCasesInColumn();
    }
    
    private void setHandlers() {
        yAxis.setOnMouseDragged(yAxisMouseHandler); 
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler); 
    }
      
    public Pane getTheContainingPane() { return theContainingPane; }
}


