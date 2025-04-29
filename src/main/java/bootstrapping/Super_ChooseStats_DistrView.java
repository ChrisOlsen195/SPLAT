/**************************************************
 *           Super_ChooseStats_DistrView          *
 *                   02/24/25                     *
 *                     09:00                      *
 *************************************************/
package bootstrapping;

import dialogs.Change_Bins_Dialog;
import genericClasses.JustAnAxis;
import javafx.geometry.Side;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import smarttextfield.SmartTextFieldDoublyLinkedSTF;
import splat.Data_Manager;
import superClasses.BivariateScale_W_CheckBoxes_View;
import utilityClasses.DataUtilities;

public class Super_ChooseStats_DistrView extends BivariateScale_W_CheckBoxes_View {
    
    boolean shadeLeft, shadeRight;
    
    boolean bILT,   //  binInLeftTail
            bIRT,   //  binInRightTail
            bITM,   //  binIsInTheMiddle
            bSLP,   //  binSurroundsLeftPercentile
            bSRP;   //  binSurroundsRightPercentile    
    
    int nLegalDataPoints, nBinsToLeft, nBinsToRight, nBinsTotal;
    
    double xMin, xMax, minScale, maximumFreq, yMin, yMax, initial_xMin, 
       initial_xMax, initial_xRange,  ithBinLow, ithBinHigh,
       minDataRange, maxDataRange;  
    
    double binWidth, displayBinWidth; 
    
    double[] dbl_AllTheSTFs, sortedData, leftBinEnd,  rightBinEnd, frequencies;
    
    String returnStatus, descrOfVar;
    
    Data_Manager dm;
    SmartTextFieldDoublyLinkedSTF al_ProbCalcs_STF;
    ChooseStats_Controller chooseStats_Controller;
    ChooseStats_Dashboard chooseStats_Dashboard;
    ChooseStats_DistrModel chooseStats_DistrModel;
    ChooseStats_DialogView chooseStats_DialogView;
    Change_Bins_Dialog change_Bins_Dialog; 

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

    //                 ***********  Printing positions   ***********************
    //double leftArea_XPosition, middleArea_XPosition, rightArea_XPosition;
 
    //                 *********  Do-The-Graph variables   *********************
    public double dTG_xx0, dTG_yy0, dTG_xAsDouble, dTG_xx1, dTG_yy1;
    
    double tempWidth, tempHeight;
    
    double text1Width, text2Width, paneWidth, txt1Edge, txt2Edge;
    
    // Make empty if no-print
    //String waldoFile = "Super_ChooseStats_DistrView";
    String waldoFile = "";

    public Label lbl_LeftProb, lbl_MidProb, lbl_RightProb, lbl_N_Equals, lbl_P_Equals,
          lbl_LeftParenProbX_LT, lbl_LeftParenProbX_LE, lbl_LeftParen_ProbX_EQ,
          lbl_LeftProbX_LT_RightParen, lbl_LeftProbX_LE_RightParen,
          lbl_LeftProbX_EQ_RightParen, lbl_LeftParenProbX_GE,
          lbl_LeftProbX_GT_RightParen, lbl_LeftProbX_GE_RightParen,
          lbl_LeftParenProbX_GT, lbl_Range_Prob_LTLT_Left_Paren,
          lbl_Range_Prob_LTLE_Left_Paren, lbl_Range_Prob_LELE_Left_Paren,
          lbl_Range_Prob_LELT_Left_Paren, lbl_RangeIs_LTLT, lbl_RangeIs_LTLE, 
          lbl_RangeIs_LELE, lbl_RangeIs_LELT, lbl_RangeIs_LTLT_RightParen, 
          lbl_RangeIs_LTLE_RightParen, lbl_RangeIs_LELE_RightParen,  
          lbl_RangeIs_LELT_RightParen;      
    
    public String LT, EQ, GT, LE, GE, leftProb, rightProb, LT_AND_LT, LT_AND_LE, 
           LE_AND_LE, LE_AND_LT, str_LeftParenProb_Is_LT, 
           str_LeftParenProbX_Is_LE, str_LeftParenProbX_Is_EQ,
           str_LeftParenProbX_Is_GE, str_LeftParenProbX_Is_GT,
           str_Range_Prob_LeftParen, str_Range_Prob_LTLT, str_Range_Prob_LTLE, 
           str_Range_Prob_LELT, str_Range_Prob_LELE,
           strDaChoice, strDaLeftChoice, strDaRightChoice, strAnswer;    
    
    public String theModelName, tailChoice, prtLeftArea, prtMidArea, prtRightArea; 
    
    public Pane theContainingPane;
    
    String title2String;

    public Super_ChooseStats_DistrView(ChooseStats_DistrModel boot_ChooseStats_DistrModel, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
    super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        dm = boot_ChooseStats_DistrModel.getDataManager();
        dm.whereIsWaldo(115, waldoFile, "Constructing"); 
        descrOfVar = boot_ChooseStats_DistrModel.getDescriptionOfVariable();
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
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
        
        LT = " < "; EQ = " = "; GT = " > "; LE = " \u2266 "; GE = " \u2267 ";

        leftProb = "P(x"; 
        rightProb = ") = ";

        LT_AND_LT = LT + " x " + LT; LT_AND_LE = LT + " x " + LE;
        LE_AND_LE = LE + " x " + LE; LE_AND_LT = LE + " x " + LT;  
        
        str_Range_Prob_LeftParen = "P(";
        str_Range_Prob_LTLT = " < x < ";
        str_Range_Prob_LTLE = " < x " + LE;
        str_Range_Prob_LELT = LE + " x <";
        str_Range_Prob_LELE = LE + " x <" + LE;                       
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
        chooseStats_DialogView.constructGraphStatus();
        okToGraph = true;
        txtTitle1 = new Text(50, 25, " Bootstrap -- " + descrOfVar);
        title2String = "";
        txtTitle2 = new Text (60, 45, title2String);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
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
        dm.whereIsWaldo(195, waldoFile, "respondToChanges()");
        chooseStats_DialogView.constructGraphStatus();
        // Check for shading
        leftTailChecked = chooseStats_DistrModel.get_LeftTail_IsChecked();
        midTailChecked = chooseStats_DistrModel.get_TwoTail_IsChecked();
        rightTailChecked = chooseStats_DistrModel.get_RightTail_IsChecked();
        shadeLeft = chooseStats_DistrModel.get_ShadeLeft();
        shadeRight = chooseStats_DistrModel.get_ShadeRight();
        al_ProbCalcs_STF = chooseStats_DialogView.getAllTheSTFs(); 
        int nSTFs = al_ProbCalcs_STF.getSize();

        /***************************************************************
         *     STFs and dbl_STFs are:                                  *
         *     [0] mu                                                  *
         *     [1] sigma                                               *
               [2] left probability                                    *
         *     [3] middle probability                                  *
         *     [4] right probability                                   *
         *     [5] left-mid boundary                                   *
         *     [6] mid-right boundary                                  *
         *                                                             *
         ***************************************************************/
        dbl_AllTheSTFs = new double[nSTFs];

        for (int ithSTF = 0; ithSTF < nSTFs; ithSTF++) {
            String ithString = al_ProbCalcs_STF.get(ithSTF).getText();
            if (DataUtilities.strIsADouble(ithString)) {
                dbl_AllTheSTFs[ithSTF] = Double.parseDouble(ithString);
            } else {
                dbl_AllTheSTFs[ithSTF] = Double.NaN;
            }   
        }

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
