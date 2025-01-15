/**************************************************
 *            Super_OneStat_DistrView             *
 *                   01/08/25                     *
 *                     15:00                      *
 *************************************************/
package bootstrapping;

import dialogs.Change_Bins_Dialog;
import genericClasses.JustAnAxis;
import javafx.geometry.Side;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import superClasses.BivariateScale_W_CheckBoxes_View;

public class Super_OneStat_DistrView extends BivariateScale_W_CheckBoxes_View {
   
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    boolean shadeLeft, shadeRight;
    
    boolean bILT,   //  binInLeftTail
            bIRT,   //  binInRightTail
            bITM,   //  binIsInTheMiddle
            bSLP,   //  binSurroundsLeftPercentile
            bSRP;   //  binSurroundsRightPercentile    
    
    int nLegalDataPoints, nBinsToLeft, nBinsToRight, nBinsTotal;
    
    double xMin, xMax, minScale, maximumFreq, yMin, yMax, initial_xMin, 
       initial_xMax, initial_xRange,  ithBinLow, ithBinHigh,
       minDataRange, maxDataRange, binWidth, displayBinWidth; 
    
    double[] sortedData, leftBinEnd,  rightBinEnd, frequencies;
    
    String returnStatus;
    
    // POJOs
    public boolean dragging, initializing, okToGraph,
            leftTailChecked, midTailChecked, rightTailChecked;
    
    public int spacesNeeded, maxSpaces;
    
    public int q1, q2, q3;

    //     ********  Left and Right base X positions ********
    //double left_Middle_Boundary, middle_Right_Boundary, leftArea, midArea, rightArea;
    
    public double xPrintPosLeft, xPrintPosCenter,  xPrintPosRight, 
           yPrintPosLeftRight,  yPrintPosCenter, maxOfYScale;
    
    public double daPDF;

    //                 *********  Do-The-Graph variables   *********************
    public double dTG_xx0, dTG_yy0, dTG_xAsDouble, dTG_xx1, dTG_yy1;
    
    double tempWidth, tempHeight;
    
    double text1Width, text2Width, paneWidth, txt1Edge, txt2Edge;
    
    public String theModelName, tailChoice, prtLeftArea, prtMidArea, prtRightArea; 
    
    // My classes   
    OneStat_Controller oneStat_Controller;
    Bootstrap_Dashboard bootstrap_Dashboard;
    Boot_DistrModel boot_DistrModel;
    NonGenericBootstrap_Info nonGen;
    BootedStat_DialogView oneStat_DialogView;
    Change_Bins_Dialog chBins_Dialog;   
    
    // POJOs
    public Pane theContainingPane;
    
    String title1String, title2String;

    public Super_OneStat_DistrView(NonGenericBootstrap_Info nonGen, Boot_DistrModel boot_DistrModel, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
    super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff) {
            System.out.println("\n84 *** Super_OneStat_DistrView, Constructing");
        } 
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.nonGen = nonGen;
        this.boot_DistrModel = boot_DistrModel;
        xPrintPosLeft = 0.05;
        xPrintPosCenter = 0.325;
        xPrintPosRight = 0.5;
        yPrintPosLeftRight = 0.9;
        yPrintPosCenter = 0.95;  
        
        nCheckBoxes = 3;
        scatterPlotCheckBoxDescr = new String[nCheckBoxes];
        scatterPlotCheckBoxDescr[0] = " Probabilities ";
        scatterPlotCheckBoxDescr[1] = " Quartiles ";
        scatterPlotCheckBoxDescr[2] = " Mean & StDev ";      
        checkBoxHeight = 350.0;        
    }
    
    protected void makeItHappen() {
        theContainingPane = new Pane();
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
    
    protected void completeTheDeal() {
        initializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();  //  Done in super
        setHandlers();      // Done in super
        doTheGraph();     
        theContainingPane = dragableAnchorPane.getTheContainingPane();     
    }
    
    protected void setUpUI() { 
        oneStat_DialogView.constructGraphStatus();
        okToGraph = true;
    }
    
    public void setUpTheAxes() {
        xAxis = new JustAnAxis(xMin, xMax);
        xAxis.setSide(Side.BOTTOM);
        binWidth = leftBinEnd[1] - leftBinEnd[0];
        displayBinWidth = xAxis.getDisplayPosition(leftBinEnd[1]) - xAxis.getDisplayPosition(leftBinEnd[0]);
        newX_Lower = xMin; 
        newX_Upper = xMax;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yMin = 0;
        yMax = 1.05 * maximumFreq;
        yAxis = new JustAnAxis(yMin, yMax); 
        yAxis.forceLowScaleEndToBe(0.0);
        yAxis.setVisible(true);

        yRange = yMax - yMin;      
        deltaY = 0.005 * yRange;
        yAxis.setSide(Side.LEFT);      
    }
    
    public void respondToChanges() {
        if (printTheStuff) {
            System.out.println("148 --- Super_OneStat_DistrView, respondToChanges()");
        } 
        oneStat_DialogView.constructGraphStatus();
        // Check for shading
        leftTailChecked = boot_DistrModel.get_LeftTail_IsChecked();
        midTailChecked = boot_DistrModel.get_TwoTail_IsChecked();
        rightTailChecked = boot_DistrModel.get_RightTail_IsChecked();
        shadeLeft = boot_DistrModel.get_ShadeLeft();
        shadeRight = boot_DistrModel.get_ShadeRight();

        xAxis.setLowerBound(newX_Lower); 
        xAxis.setUpperBound(newX_Upper);
        initializing = false;
    }
    
    public void prepareTheSupportAxis() {
        bigDelta = (newX_Upper - newX_Lower) / NUMBER_OF_DXs;
        delta = bigDelta;
        xDataMin = xDataMax = newX_Lower;
        xRange = newX_Upper - newX_Lower;        
        yRange = yDataMax = maxOfYScale;
        // These constants control the rate of axis scale change when dragging
        deltaX = 0.005 * xRange; deltaY = 0.005 * yRange;
    }
    
    public void dTG_Continuous() {        
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        paneWidth = dragableAnchorPane.getWidth();
        txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        tempHeight = dragableAnchorPane.getHeight();
        tempWidth = dragableAnchorPane.getWidth();
       
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
    }
    
    public void makeARectangle(double xStart, double yStart, double xStop, double yStop) {
            xStart = xAxis.getDisplayPosition(xStart); 
            yStart = yAxis.getDisplayPosition(0.0); 
            xStop = xAxis.getDisplayPosition(xStop);
            yStop = yAxis.getDisplayPosition(daPDF);          
            gc.setLineWidth(2);
            gc.setStroke(Color.BLACK);
            gc.strokeLine(xStart,yStart, xStart, yStop);   
            gc.strokeLine(xStop, yStart, xStop, yStop); 
            gc.strokeLine(xStart, yStart, xStop, yStart); 
            gc.strokeLine(xStart, yStop, xStop, yStop);         
    }
    
    public Pane getTheContainingPane() { return theContainingPane; }
}
