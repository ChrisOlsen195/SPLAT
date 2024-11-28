/**************************************************
 *             HorizontalBoxPlot_View             *
 *                   11/27/24                     *
 *                     12:00                      *
 *************************************************/
/**************************************************
 *  Checked against PSO / 6th ed, p171, 08/13/23  *
 *************************************************/
package proceduresManyUnivariate;

import anova2.ANCOVA_Dashboard;
import the_t_procedures.Indep_t_Dashboard;
import the_t_procedures.Matched_t_Dashboard;
import the_t_procedures.Single_t_Dashboard;
import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;
import genericClasses.ZoomieThing;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import java.util.ArrayList;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import proceduresOneUnivariate.Exploration_Dashboard;
import simpleRegression.Regr_Compare_Dashboard;

import proceduresTwoUnivariate.*;
import utilityClasses.MyAlerts;

public class HorizontalBoxPlot_View extends Region { 
    // POJOs
    boolean dragging;
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    boolean[] hBoxCheckBoxSettings;
    
    int nVariables, nCheckBoxes, nDataPoints, n_Uniques;
    int[] whiskerEndRanks, countsOfUniqueValues;
    
    double initial_xMin, initial_xMax, initial_xRange, xMin, xMax, xRange,
           xPix_InitialPress, xPix_MostRecentDragPoint, newX_Lower, newX_Upper, 
           deltaX, dispLowerBound, dispUpperBound,  bottomOfLowWhisker, 
           topOfHighWhisker, initHoriz, initVert, initWidth, initHeight,
           jitFraction;
    
    double lowExtremeCutoff, highExtremeCutoff;
    double[] uniqueValues;
    
    double[] fiveNumberSummary, means, stDevs;
    
    private String strSubTitle, graphsCSS;
    String explanatoryVariable, responseVariable;
    
    String[] hBoxCheckBoxDescr;
    ObservableList<String> categoryLabels;  
    
    // My classes
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis xAxis;    
    QuantitativeDataVariable tempQDV;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    HorizontalBoxPlot_Model hBoxModel;
    UnivariateContinDataObj tempUCDO; 
    
    // POJOs / FX
    AnchorPane anchorPane, checkBoxRow;
    Canvas graphCanvas;
    CategoryAxis yAxis;
    CheckBox[] hBoxCheckBoxes;
    GraphicsContext gcHBox; // Required for drawing on the Canvas
    Pane theContainingPane;    
    Text txtTitle1, txtTitle2;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;

    public HorizontalBoxPlot_View(HorizontalBoxPlot_Model hBoxModel, Exploration_Dashboard explore_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("103 *** HorizontalBoxPlot_View, constructing");
        }
        this.hBoxModel = hBoxModel;
        strSubTitle = hBoxModel.getSubTitle();
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        categoryLabels = hBoxModel.getCategoryLabels();
        initStuff();
    }
    
    // In the case of more than one QDV, allTheUDMs(0) is all, 
    // individual QDVs go from 1 to n    
    public HorizontalBoxPlot_View(HorizontalBoxPlot_Model hBoxModel, Explore_2Ind_Dashboard explore_2Ind_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("119 *** HorizontalBoxPlot_View, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.hBoxModel = hBoxModel;
        categoryLabels = hBoxModel.getCategoryLabels();
        explanatoryVariable = hBoxModel.getFirstVarDescription();
        responseVariable = hBoxModel.getSecondVarDescription();
        initStuff();
    }
    
    public HorizontalBoxPlot_View(HorizontalBoxPlot_Model hBoxModel, Indep_t_Dashboard independent_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("134 *** HorizontalBoxPlot_View, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.hBoxModel = hBoxModel;   
        explanatoryVariable = hBoxModel.getFirstVarDescription();
        responseVariable = hBoxModel.getSecondVarDescription();   
        categoryLabels = hBoxModel.getCategoryLabels();
        initStuff();
    }
    
    public HorizontalBoxPlot_View(HorizontalBoxPlot_Model hBoxModel, Single_t_Dashboard single_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("149 *** HorizontalBoxPlot_View, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.hBoxModel = hBoxModel; 
        strSubTitle = hBoxModel.getSubTitle();
        categoryLabels = hBoxModel.getCategoryLabels();
        initStuff();
    }
    
    public HorizontalBoxPlot_View(HorizontalBoxPlot_Model hBoxModel, Matched_t_Dashboard matched_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("163 *** HorizontalBoxPlot_View, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.hBoxModel = hBoxModel; 
        strSubTitle = hBoxModel.getSubTitle();
        categoryLabels = hBoxModel.getCategoryLabels();
        initStuff();
    }
    
        public HorizontalBoxPlot_View(HorizontalBoxPlot_Model hBoxModel, MultUni_Dashboard multUni_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("177 *** HorizontalBoxPlot_View, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.hBoxModel = hBoxModel;
        categoryLabels = hBoxModel.getCategoryLabels();
        initStuff();
    }
        
    public HorizontalBoxPlot_View(HorizontalBoxPlot_Model hBoxModel, Regr_Compare_Dashboard ancova_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("190 *** HorizontalBoxPlot_View, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.hBoxModel = hBoxModel; 
        categoryLabels = hBoxModel.getCategoryLabels();
        initStuff();
    }
    
    public HorizontalBoxPlot_View(HorizontalBoxPlot_Model hBoxModel, ANCOVA_Dashboard ancova_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("190 *** HorizontalBoxPlot_View, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.hBoxModel = hBoxModel; 
        categoryLabels = hBoxModel.getCategoryLabels();
        initStuff();
    }
    
    private void initStuff() {
        if (printTheStuff == true) {
            System.out.println("201 *** HorizontalBoxPlot_View, initStuff()");
        }
        strSubTitle = hBoxModel.getSubTitle();       
        nCheckBoxes = 3;
        hBoxCheckBoxDescr = new String[nCheckBoxes];
        hBoxCheckBoxDescr[0] = " Means diamond ";
        hBoxCheckBoxDescr[1] = " Extreme Outliers "; 
        hBoxCheckBoxDescr[2] = " Jitter ";
        allTheQDVs = new ArrayList<>();
        allTheQDVs = hBoxModel.getAllTheQDVs();
        nVariables = allTheQDVs.size();
        means = new double[nVariables];
        stDevs = new double[nVariables];

        for (int ithVar = 0; ithVar < nVariables; ithVar++) {            
            if (nVariables == 1) {  // This is surely vestigial!
                //categoryLabels.add(catLabels);
            }
            else {
                //categoryLabels.add(allTheQDVs.get(ithVar + 1).getTheVarLabel());
                means[ithVar] = allTheQDVs.get(ithVar).getTheMean();
                stDevs[ithVar] = allTheQDVs.get(ithVar).getTheStandDev();
            }
        }       
        
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        graphCanvas = new Canvas(600, 600);
        gcHBox = graphCanvas.getGraphicsContext2D();

        makeTheCheckBoxes();    
        makeItHappen();        
    }
    
    private void makeItHappen() {  
        if (printTheStuff == true) {
            System.out.println("236 *** HorizontalBoxPlot_View, makeItHappen()");
        }
        theContainingPane = new Pane();
        gcHBox = graphCanvas.getGraphicsContext2D();
        gcHBox.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
    
    public void completeTheDeal() { 
        if (printTheStuff == true) {
            System.out.println("247 *** HorizontalBoxPlot_View, completeTheDeal()");
        }
        initializeGraphParameters();
        xAxis.setTickLabelFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        yAxis.setTickLabelRotation(270);
        setUpUI();       
        setUpAnchorPane();
        setHandlers();
        doTheGraph();           
        theContainingPane = dragableAnchorPane.getTheContainingPane();       
    }
        
    public void initializeGraphParameters() { 
        if (printTheStuff == true) {
            System.out.println("261 *** HorizontalBoxPlot_View, initializeGraphParameters()");
        }
        initial_xMin = Math.min(allTheQDVs.get(0).getMinValue(), allTheQDVs.get(0).getMinValue());
        initial_xMax = Math.max(allTheQDVs.get(0).getMaxValue(), allTheQDVs.get(0).getMaxValue());

        if (nVariables > 1) {
            for (int ithVariable = 1; ithVariable < nVariables; ithVariable++) {
                initial_xMin = Math.min(initial_xMin, allTheQDVs.get(ithVariable).getMinValue());
                initial_xMax = Math.max(initial_xMax, allTheQDVs.get(ithVariable).getMaxValue());
            }
        }
        
        initial_xRange = initial_xMax - initial_xMin;   
        
        xMin = initial_xMin;
        xMax = initial_xMax;
        xRange = initial_xRange;
        
        xAxis = new JustAnAxis(xMin, xMax);
        xAxis.setSide(Side.BOTTOM);
        xAxis.setLabel("     ");        
        deltaX = 0.005 * xRange;  

        yAxis = new CategoryAxis(categoryLabels);
        yAxis.setSide(Side.LEFT);  
        yAxis.setAutoRanging(true);
        yAxis.setMinWidth(40);  //  Controls the Min Y Axis width (for labels)
        yAxis.setPrefWidth(40);         
    }
    
    public void setUpUI() {
        txtTitle1 = new Text(50, 25, " Horizontal Box Plot ");
        txtTitle2 = new Text (60, 45, strSubTitle);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    private void makeTheCheckBoxes() {
        if (printTheStuff == true) {
            System.out.println("300 *** HorizontalBoxPlot_View, makeTheCheckBoxes()");
        }
        hBoxCheckBoxSettings = new boolean[nCheckBoxes];        
        for (int ithSetting = 0; ithSetting < nCheckBoxes; ithSetting++) {
            hBoxCheckBoxSettings[ithSetting] =  false;
        } 
        
        checkBoxRow = new AnchorPane();
        checkBoxRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        hBoxCheckBoxes = new CheckBox[nCheckBoxes];
        for (int ithCB = 0; ithCB < nCheckBoxes; ithCB++) {
            hBoxCheckBoxes[ithCB] = new CheckBox(hBoxCheckBoxDescr[ithCB]);
            hBoxCheckBoxes[ithCB].setMaxWidth(Double.MAX_VALUE);
            hBoxCheckBoxes[ithCB].setId(hBoxCheckBoxDescr[ithCB]);
            hBoxCheckBoxes[ithCB].setSelected(hBoxCheckBoxSettings[ithCB]);

            hBoxCheckBoxes[ithCB].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            
            if (hBoxCheckBoxes[ithCB].isSelected()) {
                hBoxCheckBoxes[ithCB].setTextFill(Color.GREEN);
            }
            else {
                hBoxCheckBoxes[ithCB].setTextFill(Color.RED);
            }
            
            hBoxCheckBoxes[ithCB].setOnAction(e->{
                CheckBox tb = ((CheckBox) e.getTarget());                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                
                // Reset selected color
                if (checkValue) { tb.setTextFill(Color.GREEN); }
                else { tb.setTextFill(Color.RED); }
                
                switch (daID) {    
                    case " Means diamond ":
                        hBoxCheckBoxSettings[0] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    case " Extreme Outliers ":  
                        hBoxCheckBoxSettings[1] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    case " Jitter ":
                        hBoxCheckBoxSettings[2] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    default:
                        String switchFailure = "Switch failure: HorizontalBoxPlot_View 331 " + daID;
                        MyAlerts.showUnexpectedErrorAlert(switchFailure);
                    break;
                }
            }); //  end setOnAction
        }  
        
        checkBoxRow.getChildren().addAll(hBoxCheckBoxes);
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
      
        for (int ithCB = 0; ithCB < nCheckBoxes; ithCB++) {
            hBoxCheckBoxes[ithCB].translateXProperty()
                                 .bind(graphCanvas.widthProperty()
                                 .divide(250.0)
                                 .multiply(5 * ithCB)
                                 .subtract(50.0));
        }
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(checkBoxRow, txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void doTheGraph() {
        if (printTheStuff == true) {
            System.out.println("393 *** HorizontalBoxPlot_View, doTheGraph()");
        }
        xAxis.setForcedAxisEndsFalse(); // Just in case
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(checkBoxRow, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(checkBoxRow, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(checkBoxRow, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(checkBoxRow, 0.95 * tempHeight);
       
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
        
        AnchorPane.setTopAnchor(graphCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(graphCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(graphCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(graphCanvas, 0.1 * tempHeight);
        
        for (int ithCB = 0; ithCB < nCheckBoxes; ithCB++) {
            AnchorPane.setLeftAnchor(hBoxCheckBoxes[ithCB], (ithCB) * tempWidth / 5.0);
        }
        
        gcHBox.clearRect(0, 0 , graphCanvas.getWidth(), graphCanvas.getHeight());

        for (int ithBatch = 0; ithBatch < nVariables; ithBatch++) {
            tempQDV = allTheQDVs.get(ithBatch);
            tempUCDO = new UnivariateContinDataObj("HorizontalBoxPlot_View", tempQDV);
            double daYPosition = yAxis.getDisplayPosition(categoryLabels.get(ithBatch));
            nDataPoints = tempUCDO.getLegalN();
            fiveNumberSummary = new double[5];
            fiveNumberSummary = tempUCDO.get_5NumberSummary();
            whiskerEndRanks = tempUCDO.getWhiskerEndRanks();
            double theMean = tempUCDO.getTheMean();
            double theStDev = tempUCDO.getTheStandDev();
            double theIQR = tempUCDO.getTheIQR();

            n_Uniques = tempUCDO.getNUniques();
            uniqueValues = new double[n_Uniques];
            uniqueValues = tempUCDO.getUniqueValues();
            countsOfUniqueValues = new int[n_Uniques];
            countsOfUniqueValues = tempUCDO.getCountsOfUniqueValues();            

            bottomOfLowWhisker = xAxis.getDisplayPosition(fiveNumberSummary[0]); 
            if (whiskerEndRanks[0] != -1) {
                bottomOfLowWhisker = xAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[0]));
            }
            
            topOfHighWhisker = xAxis.getDisplayPosition(fiveNumberSummary[4]);
            if (whiskerEndRanks[1] != -1) {
                topOfHighWhisker = xAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[1]));
            }
            
            // Extreme outlier cutoffs
            lowExtremeCutoff = fiveNumberSummary[1] - 3.0 * theIQR;
            highExtremeCutoff = fiveNumberSummary[3] + 3.0 * theIQR; 

            double q1_display = xAxis.getDisplayPosition(fiveNumberSummary[1]);
            double q2_display = xAxis.getDisplayPosition(fiveNumberSummary[2]);
            double q3_display = xAxis.getDisplayPosition(fiveNumberSummary[3]);
  
            double mean_display = xAxis.getDisplayPosition(theMean);
            double upperStDev_display = xAxis.getDisplayPosition(theMean + theStDev);
            double lowerStDev_display = xAxis.getDisplayPosition(theMean - theStDev);
            double iqr_display = q3_display - q1_display;
 
            double spacing = 100.;

            gcHBox.setLineWidth(2);
            gcHBox.setStroke(Color.BLACK);
            double spaceFraction = 0.25 * spacing;

            // x, y, w, h
            gcHBox.strokeRect(q1_display, daYPosition - spaceFraction, iqr_display, 2 * spaceFraction);    //  box
            gcHBox.strokeLine(q2_display, daYPosition - spaceFraction, q2_display, daYPosition + spaceFraction);    //  Median
            gcHBox.strokeLine(bottomOfLowWhisker, daYPosition, q1_display, daYPosition);  //  Low whisker
            gcHBox.strokeLine(q3_display, daYPosition, topOfHighWhisker, daYPosition);  //  High whisker
            
            // means & stDev diamond
            if (hBoxCheckBoxes[0].isSelected()){
                gcHBox.setLineWidth(1);
                gcHBox.setStroke(Color.RED);
                gcHBox.strokeLine(mean_display, daYPosition - spaceFraction, upperStDev_display, daYPosition);  
                gcHBox.strokeLine(mean_display, daYPosition - spaceFraction, lowerStDev_display, daYPosition);
                gcHBox.strokeLine(mean_display, daYPosition + spaceFraction, upperStDev_display, daYPosition);  
                gcHBox.strokeLine(mean_display, daYPosition + spaceFraction, lowerStDev_display, daYPosition);
                gcHBox.setLineWidth(2);
                gcHBox.setStroke(Color.BLACK);
            }
            
            // Hi / Low 
            gcHBox.strokeLine(bottomOfLowWhisker, daYPosition - 0.10 * spacing, 
                              bottomOfLowWhisker, daYPosition + 0.10 * spacing);
            gcHBox.strokeLine(topOfHighWhisker,  daYPosition - 0.10 * spacing, 
                              topOfHighWhisker,  daYPosition + 0.10 * spacing);  

            // Low outliers
            if (!hBoxCheckBoxes[2].isSelected()) {
                if (whiskerEndRanks[0] != -1) {   //  Are there low outliers?
                    int dataPoint = 0;
                    while (dataPoint < whiskerEndRanks[0]) {
                        double yy = daYPosition;
                        double tempX = tempUCDO.getIthSortedValue(dataPoint);
                        double xx = xAxis.getDisplayPosition(tempX);

                        if ((tempX < lowExtremeCutoff) && (hBoxCheckBoxes[1].isSelected() == true)) {
                            gcHBox.strokeOval(xx - 6, yy - 6, 12, 12);
                        }
                        else { gcHBox.fillOval(xx - 3, yy - 3, 6, 6); }

                        dataPoint++;
                    }
                }

                // High outliers
                if (whiskerEndRanks[1] != -1) {    //  Are there high outliers?
                    for (int dataPoint = whiskerEndRanks[1] + 1; dataPoint < nDataPoints; dataPoint++) {
                        double yy = daYPosition;
                        double tempX = tempUCDO.getIthSortedValue(dataPoint);
                        double xx = xAxis.getDisplayPosition(tempX);

                        if ((tempX > highExtremeCutoff) && (hBoxCheckBoxes[1].isSelected() == true)) {
                            gcHBox.strokeOval(xx - 6, yy - 6, 12, 12);
                        }
                        else { gcHBox.fillOval(xx - 3, yy - 3, 6, 6); }
                    }
                }
            }   else {  // jittering
                for (int ithUnique = 0; ithUnique < n_Uniques; ithUnique++) {
                    int uniquesThisValue = countsOfUniqueValues[ithUnique];
                    double thisValue = uniqueValues[ithUnique];
                    jitFraction = 1.0 / (uniquesThisValue + 1.);
                    double xPoint = xAxis.getDisplayPosition(thisValue);
                    
                    for (int ithJit = 0; ithJit < uniquesThisValue; ithJit++) {
                        double deviation = 0.5 - (ithJit +  1) * jitFraction;
                        double yPoint = yAxis.getDisplayPosition(categoryLabels.get(ithBatch)) + 70. * deviation;

                        if ((thisValue < lowExtremeCutoff) && (hBoxCheckBoxes[1].isSelected())) {
                        // if (tempX < tempLowBall) {
                            gcHBox.strokeOval(xPoint - 6, yPoint - 6, 12, 12);
                        }
                        else {
                            gcHBox.fillOval( xPoint - 3, yPoint - 3, 6, 6);
                        }

                        if ((thisValue > highExtremeCutoff) && (hBoxCheckBoxes[1].isSelected())) {
                            gcHBox.strokeOval(xPoint - 6, yPoint - 6, 12, 12);
                        }
                        else {
                            gcHBox.fillOval(xPoint - 3, yPoint - 3, 6, 6);   
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
    }
    
    public void setHandlers() {
        xAxis.setOnMouseDragged(xAxisMouseHandler); 
        xAxis.setOnMousePressed(xAxisMouseHandler); 
        xAxis.setOnMouseReleased(xAxisMouseHandler);         
        dragableAnchorPane.setOnMouseReleased(scatterplotMouseHandler);        
    }
    
    EventHandler<MouseEvent> xAxisMouseHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent mouseEvent) {
            
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) { 
                xPix_InitialPress = mouseEvent.getX();  
                xPix_MostRecentDragPoint = mouseEvent.getX();
                dragging = false;   
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                
                if (dragging) {
                    xAxis.setLowerBound(newX_Lower ); 
                    xAxis.setUpperBound(newX_Upper );
                    dragging = false;
                }
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                dragging = true;
                
                double xPix_Dragging = mouseEvent.getX();
                newX_Lower = xAxis.getLowerBound();
                newX_Upper = xAxis.getUpperBound(); 

                dispLowerBound = xAxis.getDisplayPosition(xAxis.getLowerBound());
                dispUpperBound = xAxis.getDisplayPosition(xAxis.getUpperBound());

                double frac = mouseEvent.getX() / dispUpperBound;
                
                // Still dragging right
                if((xPix_Dragging > xPix_InitialPress) && (xPix_Dragging > xPix_MostRecentDragPoint)) {    
                    // Which half of scale?
                    if (frac > 0.5) {  //  Right of center -- OK
                        newX_Upper = xAxis.getUpperBound() - deltaX;
                    }
                    else { // Left of Center
                        newX_Lower = xAxis.getLowerBound() - deltaX;
                    }
                }
                else 
                if ((xPix_Dragging < xPix_InitialPress) && (xPix_Dragging < xPix_MostRecentDragPoint)) {   
                    
                    if (frac < 0.5) { // left of center
                        newX_Lower = xAxis.getLowerBound() + deltaX;
                    }
                    else {   // Right of center -- OK
                        newX_Upper = xAxis.getUpperBound() + deltaX;
                    }
                }    

                xAxis.setLowerBound(newX_Lower ); 
                xAxis.setUpperBound(newX_Upper );

                dispLowerBound = xAxis.getDisplayPosition(xAxis.getLowerBound());
                dispUpperBound = xAxis.getDisplayPosition(xAxis.getUpperBound());
                xPix_MostRecentDragPoint = mouseEvent.getX();
                
                doTheGraph();
            }
        }
    };   
     
    EventHandler<MouseEvent> scatterplotMouseHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent mouseEvent) {
            
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                ZoomieThing zoomieThing = new ZoomieThing(dragableAnchorPane);
            }
        }
    };  
    
   public Pane getTheContainingPane() { return theContainingPane; }
}
