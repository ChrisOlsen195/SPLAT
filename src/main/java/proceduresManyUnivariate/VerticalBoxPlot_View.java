/**************************************************
 *               VerticalBoxPlotView              *
 *                    09/03/24                    *
 *                     00:00                      *
 *************************************************/
package proceduresManyUnivariate;

import anova1.categorical.ANOVA1_Cat_Dashboard;
import anova1.quantitative.ANOVA1_Quant_Dashboard;
import anova2.ANOVA2_RM_Dashboard;
import the_t_procedures.Indep_t_Dashboard;
import the_t_procedures.Matched_t_Dashboard;
import the_t_procedures.Single_t_Dashboard;
import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;
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
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import proceduresTwoUnivariate.Explore_2Ind_Dashboard;
import genericClasses.ZoomieThing;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import proceduresOneUnivariate.Exploration_Dashboard;
import utilityClasses.*;

public class VerticalBoxPlot_View extends Region { 
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    private boolean areDragging;
    private boolean[] vBoxCheckBoxSettings;    
    
    private int nVariables, nCheckBoxes, nDataPoints, n_Uniques;
    private int[] whiskerEndRanks;
    int[] countsOfUniqueValues;
    
    private double initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
           yPix_InitialPress, yPix_MostRecentDragPoint, newY_Lower, newY_Upper, 
           deltaY, dispLowerBound, bottomOfLowWhisker, topOfHighWhisker, 
           initHoriz, initVert, initWidth, initHeight, jitFraction;
    
    double lowExtremeCutoff, highExtremeCutoff;
    double[] uniqueValues;
    private double[] fiveNumberSummary, means, stDevs;
    private String strSubtitle, catLabels, graphsCSS;
    private String[] vBoxCheckBoxDescr;
  
    private ObservableList<String> varLabels;
    
    // My classes
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis yAxis;   
    QuantitativeDataVariable tempQDV;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    UnivariateContinDataObj tempUCDO;  
    VerticalBoxPlot_Model vBoxModel;
    
    // POJOs / FX
    AnchorPane checkBoxRow, anchorPane;
    Canvas graphCanvas;
    CheckBox[] vBoxCheckBoxes;
    GraphicsContext gcVBox; // Required for drawing on the Canvas

    CategoryAxis xAxis;
    Pane theContainingPane;    
    Text txtTitle1, txtTitle2;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;

    
    public VerticalBoxPlot_View(VerticalBoxPlot_Model vBoxModel, Exploration_Dashboard explore_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff) {
            System.out.println("102 *** VerticalBoxPlot_View, constructing");
        }
        this.vBoxModel = vBoxModel;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel;
        catLabels = vBoxModel.getSubTitle();
        initStuff();
    }
    
    public VerticalBoxPlot_View(VerticalBoxPlot_Model vBoxModel, Explore_2Ind_Dashboard explore_2Ind_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff) {
            System.out.println("116 *** VerticalBoxPlot_View, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel;
        initStuff();
    }
    
    public VerticalBoxPlot_View(VerticalBoxPlot_Model vBoxModel, Indep_t_Dashboard independent_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff) {
            System.out.println("128 *** VerticalBoxPlot_View, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel;      
        initStuff();
    }
    
    public VerticalBoxPlot_View(VerticalBoxPlot_Model vBoxModel, Single_t_Dashboard single_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff) {
            System.out.println("140 *** VerticalBoxPlot_View, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel; 
        catLabels = vBoxModel.getSubTitle();
        initStuff();
    }
    
    public VerticalBoxPlot_View(VerticalBoxPlot_Model vBoxModel, Matched_t_Dashboard matched_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff) {
            System.out.println("153 *** VerticalBoxPlot_View, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel; 
        catLabels = vBoxModel.getSubTitle();
        initStuff();
    }
    
    public VerticalBoxPlot_View(VerticalBoxPlot_Model vBoxModel, ANOVA1_Cat_Dashboard anova1_Cat_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff) {
            System.out.println("166 *** VerticalBoxPlot_View, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel;
        catLabels = vBoxModel.getSubTitle();
        initStuff();
    }
    
    public VerticalBoxPlot_View(VerticalBoxPlot_Model vBoxModel, ANOVA1_Quant_Dashboard anova1_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff) {
            System.out.println("179 *** VerticalBoxPlot_View, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel;
        catLabels = vBoxModel.getSubTitle();
        initStuff();
    }
    
    public VerticalBoxPlot_View(VerticalBoxPlot_Model vBoxModel, ANOVA2_RM_Dashboard anova2_RM_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff) {
            System.out.println("191 *** VerticalBoxPlot_View, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel;  
        initStuff();
    }
    
    public VerticalBoxPlot_View(VerticalBoxPlot_Model vBoxModel, MultUni_Dashboard multUni_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff) {
            System.out.println("203 *** VerticalBoxPlot_View, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel;
        initStuff();
    }
    
    private void initStuff() {
        strSubtitle = vBoxModel.getSubTitle();
        varLabels = FXCollections.observableArrayList();
        varLabels = vBoxModel.getVarLabels();
        allTheQDVs = vBoxModel.getAllTheQDVs();
        nCheckBoxes = 3;
        vBoxCheckBoxDescr = new String[nCheckBoxes];
        vBoxCheckBoxDescr[0] = " Means diamond ";
        vBoxCheckBoxDescr[1] = " Extreme Outliers "; 
        vBoxCheckBoxDescr[2] = " Jitter ";
        
        nVariables = allTheQDVs.size();
        means = new double[nVariables];
        stDevs = new double[nVariables];

        for (int iVars = 0; iVars < nVariables; iVars++) {            
            means[iVars] = allTheQDVs.get(iVars).getTheMean();
            stDevs[iVars] = allTheQDVs.get(iVars).getTheStandDev();
        }

        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        graphCanvas = new Canvas(600, 600);
        gcVBox = graphCanvas.getGraphicsContext2D();

        makeTheCheckBoxes();    
        makeItHappen();        
    }
    
    private void makeItHappen() {       
        theContainingPane = new Pane();
        gcVBox = graphCanvas.getGraphicsContext2D();
        gcVBox.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
    
    public void completeTheDeal() { 
        if (printTheStuff) {
            System.out.println("249 *** VerticalBoxPlot_View, completeTheDeal()");
        }
        initializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();
        setHandlers();
        doTheGraph();           
        theContainingPane = dragableAnchorPane.getTheContainingPane();       
    }
    
    private void makeTheCheckBoxes() {  
        vBoxCheckBoxSettings = new boolean[nCheckBoxes];
        
        for (int ithSetting = 0; ithSetting < nCheckBoxes; ithSetting++) {
            vBoxCheckBoxSettings[ithSetting] =  false;
        }   
        
        checkBoxRow = new AnchorPane();
        checkBoxRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        vBoxCheckBoxes = new CheckBox[nCheckBoxes];

        for (int i = 0; i < nCheckBoxes; i++) {
            vBoxCheckBoxes[i] = new CheckBox(vBoxCheckBoxDescr[i]);            
            vBoxCheckBoxes[i].setMaxWidth(Double.MAX_VALUE);
            vBoxCheckBoxes[i].setId(vBoxCheckBoxDescr[i]);
            vBoxCheckBoxes[i].setSelected(vBoxCheckBoxSettings[i]);
            vBoxCheckBoxes[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            
            if (vBoxCheckBoxes[i].isSelected() == true) {
                vBoxCheckBoxes[i].setTextFill(Color.GREEN);
            }
            else {
                vBoxCheckBoxes[i].setTextFill(Color.RED);
            }
            
            vBoxCheckBoxes[i].setOnAction(e->{
                CheckBox tb = ((CheckBox) e.getTarget());                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                
                // Reset selected color                
                if (checkValue) { tb.setTextFill(Color.GREEN); }
                else { tb.setTextFill(Color.RED); }
                
                switch (daID) {    
                    case " Means diamond ":
                        vBoxCheckBoxSettings[0] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    case " Extreme Outliers ":  
                        vBoxCheckBoxSettings[1] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    case " Jitter ":
                        vBoxCheckBoxSettings[2] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    default:
                        String switchFailure = "Switch failure: VerticalBoxPlot_View 316 " + daID;
                        MyAlerts.showUnexpectedErrorAlert(switchFailure);
                    break;
                }
            }); //  end setOnAction
        }  
        
        checkBoxRow.getChildren().addAll(vBoxCheckBoxes);
    }
    
    public void setUpUI() {
        txtTitle1 = new Text(50, 25, " Vertical Box Plot ");
        txtTitle2 = new Text (60, 45, strSubtitle);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
  
        for (int iChex = 0; iChex < nCheckBoxes; iChex++) { 
            vBoxCheckBoxes[iChex].translateXProperty()
                                 .bind(graphCanvas.widthProperty()
                                 .divide(250.0)
                                 .multiply(5 * iChex)
                                 .subtract(50.0));
        }
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(checkBoxRow, txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void initializeGraphParameters() {  
        initial_yMin = Math.min(allTheQDVs.get(0).getMinValue(), allTheQDVs.get(0).getMinValue());
        initial_yMax = Math.max(allTheQDVs.get(0).getMaxValue(), allTheQDVs.get(0).getMaxValue());

        if (nVariables > 1) {
            for (int ithVariable = 1; ithVariable < nVariables; ithVariable++) {
                initial_yMin = Math.min(initial_yMin, allTheQDVs.get(ithVariable).getMinValue());
                initial_yMax = Math.max(initial_yMax, allTheQDVs.get(ithVariable).getMaxValue());
            }
        }
        initial_yRange = initial_yMax - initial_yMin; 

        yMin = initial_yMin;
        yMax = initial_yMax;
        yRange = initial_yRange;

        yAxis = new JustAnAxis(yMin, yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setLabel("   ");
        // This constant controls the rate of scale change when areDragging
        deltaY = 0.005 * yRange;
        
        xAxis = new CategoryAxis(varLabels);
        xAxis.setSide(Side.BOTTOM);  
        xAxis.setAutoRanging(true);
        
        xAxis.setMinWidth(40);  //  Controls the Min Y Axis width (for labels)
        xAxis.setPrefWidth(40);              
    }
    
    public void doTheGraph() {
        yAxis.setForcedAxisEndsFalse(); // Just in case

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
       
        for (int chex = 0; chex < nCheckBoxes; chex++) {
            AnchorPane.setLeftAnchor(vBoxCheckBoxes[chex], (chex) * tempWidth / 5.0);
        }
        
        gcVBox.clearRect(0, 0 , graphCanvas.getWidth(), graphCanvas.getHeight());
        for (int theBatch = 0; theBatch < nVariables; theBatch++) {
            tempQDV = allTheQDVs.get(theBatch);
            tempUCDO = new UnivariateContinDataObj("VerticalBoxPlot_View", tempQDV);
            double daXPosition = xAxis.getDisplayPosition(varLabels.get(theBatch));
            nDataPoints = tempUCDO.getLegalN();
            fiveNumberSummary = new double[5];
            fiveNumberSummary = tempUCDO.get_5NumberSummary();
            whiskerEndRanks = tempUCDO.getWhiskerEndRanks();
            double theMean = tempUCDO.getTheMean();
            double theStDev = tempUCDO.getTheStandDev();
            double theIQR = tempUCDO.getTheIQR();
            
            // *****   For Jittering *****
            n_Uniques = tempUCDO.getNUniques();
            uniqueValues = new double[n_Uniques];
            uniqueValues = tempUCDO.getUniqueValues();
            countsOfUniqueValues = new int[n_Uniques];
            countsOfUniqueValues = tempUCDO.getCountsOfUniqueValues();
            
            // *****   For Jittering *****
            bottomOfLowWhisker = yAxis.getDisplayPosition(fiveNumberSummary[0]);            
            if (whiskerEndRanks[0] != -1) {
                bottomOfLowWhisker = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[0]));
            }
            
            topOfHighWhisker = yAxis.getDisplayPosition(fiveNumberSummary[4]);            
            if (whiskerEndRanks[1] != -1) {
                topOfHighWhisker = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[1]));
            }
            
            lowExtremeCutoff = fiveNumberSummary[1] - 3.0 * theIQR;
            highExtremeCutoff = fiveNumberSummary[3] + 3.0 * theIQR; 

            double q1_display = yAxis.getDisplayPosition(fiveNumberSummary[1]);
            double q2_display = yAxis.getDisplayPosition(fiveNumberSummary[2]);
            double q3_display = yAxis.getDisplayPosition(fiveNumberSummary[3]);
            
            double mean_display = yAxis.getDisplayPosition(theMean);
            double upperStDev_display = yAxis.getDisplayPosition(theMean + theStDev);
            double lowerStDev_display = yAxis.getDisplayPosition(theMean - theStDev);
            double iqr_display = q3_display - q1_display;
            
            double spacing = 100.;

            gcVBox.setLineWidth(2);
            gcVBox.setStroke(Color.BLACK);
            double spaceFraction = 0.25 * spacing;

            // x, y, w, h
            gcVBox.strokeRect(daXPosition - spaceFraction, q3_display, 2 * spaceFraction, -iqr_display);    //  box
            gcVBox.strokeLine(daXPosition - spaceFraction, q2_display, daXPosition + spaceFraction, q2_display);    //  Median

            gcVBox.strokeLine(daXPosition, bottomOfLowWhisker, daXPosition, q1_display);  //  Low whisker
            gcVBox.strokeLine(daXPosition, q3_display, daXPosition, topOfHighWhisker);  //  High whisker
            
            // Low, High
            gcVBox.strokeLine(daXPosition - 0.10 * spacing, bottomOfLowWhisker, 
                              daXPosition + 0.10 * spacing, bottomOfLowWhisker);
            gcVBox.strokeLine(daXPosition - 0.10 * spacing, topOfHighWhisker, 
                              daXPosition + 0.10 * spacing, topOfHighWhisker);
            
            // means & stDev diamond
            if (vBoxCheckBoxes[0].isSelected() == true){
                gcVBox.setLineWidth(1);
                gcVBox.setStroke(Color.RED);
                gcVBox.strokeLine(daXPosition - spaceFraction, mean_display, daXPosition, upperStDev_display);  
                gcVBox.strokeLine(daXPosition - spaceFraction, mean_display, daXPosition, lowerStDev_display);
                gcVBox.strokeLine(daXPosition + spaceFraction, mean_display, daXPosition, upperStDev_display);  
                gcVBox.strokeLine(daXPosition + spaceFraction, mean_display, daXPosition, lowerStDev_display);
                gcVBox.setLineWidth(2);
                gcVBox.setStroke(Color.BLACK);
            }
            
            if (vBoxCheckBoxes[2].isSelected() == false) {
                // Low outliers
                if (whiskerEndRanks[0] != -1) {   //  Are there low outliers?
                    int dataPoint = 0;
                    
                    while (dataPoint < whiskerEndRanks[0]) {
                        double xx = daXPosition;
                        double tempY = tempUCDO.getIthSortedValue(dataPoint);
                        double yy = yAxis.getDisplayPosition(tempY);

                        if ((tempY < lowExtremeCutoff) && (vBoxCheckBoxes[1].isSelected() == true)) {
                            gcVBox.strokeOval(xx - 6, yy - 6, 12, 12);
                        }
                        else {
                            gcVBox.fillOval(xx - 3, yy - 3, 6, 6);
                        }
                        dataPoint++;
                    }
                }

                // High outliers
                if (whiskerEndRanks[1] != -1) { //  Are there high outliers?                    
                    for (int dataPoint = whiskerEndRanks[1] + 1; dataPoint < nDataPoints; dataPoint++) {
                        double xx = daXPosition;
                        double tempY = tempUCDO.getIthSortedValue(dataPoint);
                        double yy = yAxis.getDisplayPosition(tempY);
                        if ((tempY > highExtremeCutoff) && (vBoxCheckBoxes[1].isSelected() == true)) {
                            gcVBox.strokeOval(xx - 6, yy - 6, 12, 12);
                        }
                        else {
                            gcVBox.fillOval(xx - 3, yy - 3, 6, 6);   
                        }
                    }
                }
            }   //  end not jittering
            else { //  jittering                
                for (int ith_Unique = 0; ith_Unique < n_Uniques; ith_Unique++) {
                    int uniquesThisValue = countsOfUniqueValues[ith_Unique];
                    double thisValue = uniqueValues[ith_Unique];
                    jitFraction = 1.0 / (uniquesThisValue + 1.);
                    double yPoint = yAxis.getDisplayPosition(thisValue);
                    
                    for (int ithJit = 0; ithJit < uniquesThisValue; ithJit++) {
                        double deviation = 0.5 - (ithJit +  1) * jitFraction;
                        double xPoint = xAxis.getDisplayPosition(varLabels.get(theBatch)) + 70. * deviation;

                        if ((thisValue < lowExtremeCutoff) && (vBoxCheckBoxes[1].isSelected() == true)) {
                        // if (tempY < tempLowBall) {
                            gcVBox.strokeOval(xPoint - 6, yPoint - 6, 12, 12);
                        }
                        else {
                            gcVBox.fillOval(xPoint - 3, yPoint - 3, 6, 6);
                        }

                        if ((thisValue > highExtremeCutoff) && (vBoxCheckBoxes[1].isSelected() == true)) {
                            gcVBox.strokeOval(xPoint - 6, yPoint - 6, 12, 12);
                        }
                        else {
                            gcVBox.fillOval(xPoint - 3, yPoint - 3, 6, 6);   
                        }                    
                    }
                }  //  *****  end jittering  *****
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
    
    public void setHandlers() {
        yAxis.setOnMouseDragged(yAxisMouseHandler);  
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler);         
        dragableAnchorPane.setOnMouseReleased(scatterplotMouseHandler);
    }
    
     EventHandler<MouseEvent> yAxisMouseHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent mouseEvent) {
            
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) { 
                yPix_InitialPress = mouseEvent.getY(); 
                yPix_MostRecentDragPoint = mouseEvent.getY();
                areDragging = false;
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (areDragging) {
                    yAxis.setLowerBound(newY_Lower ); 
                    yAxis.setUpperBound(newY_Upper );
                    areDragging = false;
                }
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                areDragging = true;
                double yPix_Dragging = mouseEvent.getY();  

                newY_Lower = yAxis.getLowerBound();
                newY_Upper = yAxis.getUpperBound(); 
 
                dispLowerBound = yAxis.getDisplayPosition(yAxis.getLowerBound());
                yAxis.getDisplayPosition(yAxis.getUpperBound());

                double frac = mouseEvent.getY() / dispLowerBound;
                
                if((yPix_Dragging > yPix_InitialPress) && (yPix_Dragging > yPix_MostRecentDragPoint)) {    
                    if (frac < 0.5) {
                        newY_Upper = yAxis.getUpperBound() + deltaY;
                    }
                    else { newY_Lower = yAxis.getLowerBound() + deltaY; }
                }
                else 
                    if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint)) {   
                        if (frac < 0.5) {
                            newY_Upper = yAxis.getUpperBound() - deltaY;
                        }
                        else { newY_Lower = yAxis.getLowerBound() - deltaY; }
                    }    

                yAxis.setLowerBound(newY_Lower ); 
                yAxis.setUpperBound(newY_Upper ); 
                dispLowerBound = yAxis.getDisplayPosition(yAxis.getLowerBound());
                yAxis.getDisplayPosition(yAxis.getUpperBound());                
                yPix_MostRecentDragPoint = mouseEvent.getY();
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