/**************************************************
 *               X2Dist_Calc_PDFView              *
 *                    01/16/25                    *
 *                     09:00                      *
 *************************************************/
package probabilityCalculators;

import genericClasses.JustAnAxis;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import probabilityDistributions.ChiSquareDistribution;
import smarttextfield.*;
import utilityClasses.DataUtilities;
import utilityClasses.StringUtilities;

    /****************************************
     *  STF's in Dist_Calc_PDFView          *
    *   stf_Mu              [0]             *
    *   stf_Sigma           [1]             *
    *   stf_df              [2]             *
    *   stf_Left_Prob       [3]             *
    *   stf_Mid_Prob        [4]             *
    *   stf_Right_Prob      [5]             *
    *   stf_Left_Stat       [6]             *
    *   stf_Right_Stat      [7]             *
    ****************************************/

public class X2Dist_Calc_PDFView extends Distributions_Calc_PDFView {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int df;

    double sigma, dbl_df; 
    double[] dbl_AllTheSTFs;
    
    // My classes  
    ChiSquareDistribution x2Distr;
    SmartTextFieldDoublyLinkedSTF al_ProbCalcs_STF;
    X2Dist_Calc_DialogView x2Dist_Calc_DialogView;

    //  POJOs / FX 
    
    public X2Dist_Calc_PDFView(ProbCalc_Dashboard probCalc_Dashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
    super(probCalc_Dashboard, placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff == true) {
            System.out.println("63 *** X2Dist_Calc_PDFView, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        xPrintPosLeft = 0.05;
        xPrintPosCenter = 0.3;
        xPrintPosRight = 0.5;
        yPrintPosLeftRight = 0.9;
        yPrintPosCenter = 0.95;
        
        //  There are no check boxes; This is here b/c superclass constructs
        //  a CheckBoxRow
        nCheckBoxes = 0;
        maxOfYScale = 0.20;        
        initializing = true;

        x2Dist_Calc_DialogView = probCalc_Dashboard.get_X2_DialogView();
        x2Distr = x2Dist_Calc_DialogView.getTheX2Distribution();
        df = x2Dist_Calc_DialogView.getDegreesOfFreedom();
        dbl_df = df; 
        this.probCalc_Dashboard = probCalc_Dashboard;
        respondToChanges();      

        graphCanvas = new Canvas(initWidth, initHeight);
        makeTheCheckBoxes(); // Done in super -- only some code utilized here
        makeItHappen();
    }
    
   
    public void completeTheDeal() {
        initializeGraphParameters();
        setUpUI();  
        setUpAnchorPane();  //  Done in super
        setHandlers();      // Done in super
        doTheGraph();     
        theContainingPane = dragableAnchorPane.getTheContainingPane();    
    }
        
    @Override
    protected void setUpUI() {
        okToGraph = x2Dist_Calc_DialogView.getOKToGraph();
        txtTitle1 = new Text(50, 25, " Probability calculations -- Chi square Distribution  ");
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2 = new Text (60, 45, title2String);
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    public void respondToChanges() {
        x2Dist_Calc_DialogView.constructGraphStatus();
        // Check for shading
        leftTailChecked = x2Dist_Calc_DialogView.getLeftTailChecked();
        midTailChecked = x2Dist_Calc_DialogView.getMidTailChecked();
        rightTailChecked = x2Dist_Calc_DialogView.getRightTailChecked();
        df = x2Dist_Calc_DialogView.getDegreesOfFreedom();
        dbl_df = df; sigma = dbl_df / (dbl_df - 2.0);
        
        if (initializing) {
            okToGraph = false;
            newX_Lower = 0.0001;
            newX_Upper =  x2Distr.getInvRightTailArea(0.005); 
            initializeGraphParameters();
        } else {
            al_ProbCalcs_STF = x2Dist_Calc_DialogView.getAllTheSTFs(); 
            int nSTFs = al_ProbCalcs_STF.getSize();
            
            /***************************************************************
             *     STFs and dbl_STFs are:                                  *
             *     [0] degrees of freedom                                  *
             *     [1] left probability                                    *
             *     [2] middle probability                                  *
             *     [3] right probability                                   *
             *     [4] left-mid boundary                                   *
             *     [5] mid-right boundary                                  *
             *                                                             *
             ***************************************************************/
            dbl_AllTheSTFs = new double[nSTFs];
            
            for (int ithSTF = 0; ithSTF < nSTFs; ithSTF++) {
                String ithString = al_ProbCalcs_STF.get(ithSTF).getText();
                
                if (DataUtilities.strIsADouble(ithString) ==  true) {
                    dbl_AllTheSTFs[ithSTF] = Double.parseDouble(ithString);
                } else {
                    dbl_AllTheSTFs[ithSTF] = Double.NaN;
                }   
            }

            if (!okToGraph) {
                df = 2;
                sigma = 1.0; //  Dummy value so "null graph" can proceed
            } else {
                df = x2Dist_Calc_DialogView.getDegreesOfFreedom();
                // Calculation duplicated here rather than getting an STF sigma
                dbl_df = df;
                sigma = 1.0;
                title2String = Integer.toString(df) + " degrees of freedom";
                txtTitle2.setText(title2String);
                txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15));
            }

            leftArea = dbl_AllTheSTFs[3];
            midArea = dbl_AllTheSTFs[4];
            rightArea = dbl_AllTheSTFs[5];
            left_Middle_Boundary = dbl_AllTheSTFs[6];
            middle_Right_Boundary = dbl_AllTheSTFs[7];
            newX_Lower = 0.0001;
            newX_Upper = x2Distr.getInvRightTailArea(0.005);
            
            if (newX_Upper < dbl_AllTheSTFs[5]) {
                newX_Upper = 1.05 * dbl_AllTheSTFs[5];
            }
            
            xAxis.setLowerBound(newX_Lower); 
            xAxis.setUpperBound(newX_Upper);
        }
        initializing = false;
    }
    
    @Override
    public void initializeGraphParameters() {
        maxOfYScale = 0.20;
        
        if (df == 1) { maxOfYScale = 2.1; }
        if (df == 2) { maxOfYScale = 0.50; }
        if (df == 3) { maxOfYScale = 0.25; }        
        if (df > 3) { maxOfYScale = 0.17; }   
        if (df > 15) { maxOfYScale = 0.10; }
        
        xAxis = new JustAnAxis(newX_Lower, newX_Upper);
        xAxis.setSide(Side.BOTTOM);       
        prepareTheSupportAxis();
        yAxis = new JustAnAxis(yDataMin, maxOfYScale);
        yAxis.setSide(Side.LEFT);
        yAxis.forceLowScaleEndToBe(0.0);  
        yAxis.setBounds(0.0, maxOfYScale);
        newY_Lower = 0.0; newY_Upper = maxOfYScale;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );
    }
    
    @Override
    public void doTheGraph() {  
        x2Dist_Calc_DialogView.constructGraphStatus();
        okToGraph = x2Dist_Calc_DialogView.getOKToGraph();      
        dTG_Continuous();
                
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        //  Start point for graph
        
        if (newX_Lower < .0001) {
            newX_Lower = .0001; 
            xAxis.setLowerBound(.0001);
        }
        dTG_xx0 = newX_Lower; 
        dTG_yy0 = x2Dist_Calc_DialogView.getDensity(dTG_xx0);       

        for (double x = newX_Lower; x <= newX_Upper; x += delta) {
            dTG_xx1 = x;
            dTG_yy1 = x2Dist_Calc_DialogView.getDensity(dTG_xx1);
            if (!okToGraph) { dTG_yy1 = 0.0; }
            xStart = xAxis.getDisplayPosition(dTG_xx0); 
            yStart = yAxis.getDisplayPosition(dTG_yy0); 
            xStop = xAxis.getDisplayPosition(dTG_xx1);
            yStop = yAxis.getDisplayPosition(dTG_yy1);          
            gc.setLineWidth(2);
            gc.setStroke(Color.BLACK);
            gc.strokeLine(xStart, yStart, xStop, yStop); //  Piece of curve

            if ((leftTailChecked || midTailChecked) && (x < left_Middle_Boundary)) {    
                yStart = yAxis.getDisplayPosition(0.0);
                gc.strokeLine(xStart, yStart, xStop, yStop);
            } 
            
            if ((rightTailChecked || midTailChecked) && (x > middle_Right_Boundary)) {
                yStart = yAxis.getDisplayPosition(0.0);
                gc.strokeLine(xStart, yStart, xStop, yStop);
            }     

            dTG_xx0 = dTG_xx1; dTG_yy0 = dTG_yy1;   //  Next start point for line segment
        }   // End graph left to graph right   
        
        if (okToGraph) { printTheProbabilities(); }
        
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
    
    private void printTheProbabilities() {
        double xPrtPosition, yPrtPosition;

        if (leftTailChecked) {
            leftArea_XPosition = x2Distr.getInvLeftTailArea(.01);
            xPrtPosition = xAxis.getDisplayPosition(leftArea_XPosition);
            yPrtPosition = yAxis.getDisplayPosition(yPrintPosLeftRight * yAxis.getUB());
            gc.setFill(Color.BLUE);
            prtLeftArea = "LeftArea = " +  StringUtilities.roundDoubleToNDigitString(leftArea, 4);
            gc.fillText(prtLeftArea, xPrtPosition, yPrtPosition); 
        }

        if (rightTailChecked) {
            rightArea_XPosition = x2Distr.getInvRightTailArea(.05);
            xPrtPosition = xAxis.getDisplayPosition(rightArea_XPosition);
            yPrtPosition = yAxis.getDisplayPosition(yPrintPosLeftRight * yAxis.getUB());
            gc.setFill(Color.BLUE);
            prtRightArea = "RightArea = " +  StringUtilities.roundDoubleToNDigitString(rightArea, 4);
            gc.fillText(prtRightArea, xPrtPosition, yPrtPosition); 
        }  

        // On the other hand...
        if (midTailChecked) {
            leftArea_XPosition = x2Distr.getInvLeftTailArea(.01);
            xPrtPosition = xAxis.getDisplayPosition(leftArea_XPosition);
            yPrtPosition = yAxis.getDisplayPosition(yPrintPosLeftRight * yAxis.getUB());
            gc.setFill(Color.BLUE);
            prtLeftArea = "LeftArea = " +  StringUtilities.roundDoubleToNDigitString(leftArea, 4);
            gc.fillText(prtLeftArea, xPrtPosition, yPrtPosition); 

            middleArea_XPosition = x2Distr.getInvLeftTailArea(.30);
            xPrtPosition = xAxis.getDisplayPosition(middleArea_XPosition);
            yPrtPosition = yAxis.getDisplayPosition(yPrintPosCenter * yAxis.getUB());
            prtMidArea = "MidArea = " +  StringUtilities.roundDoubleToNDigitString(midArea, 4);
            gc.fillText(prtMidArea, xPrtPosition, yPrtPosition);                  

            rightArea_XPosition = x2Distr.getInvRightTailArea(.05);
            xPrtPosition = xAxis.getDisplayPosition(rightArea_XPosition);
            yPrtPosition = yAxis.getDisplayPosition(yPrintPosLeftRight * yAxis.getUB());
            prtRightArea = "RightArea = " +  StringUtilities.roundDoubleToNDigitString(rightArea, 4);
            gc.fillText(prtRightArea, xPrtPosition, yPrtPosition);
        }        
    }
    
    public void checkForShading() {
        leftTailChecked = x2Dist_Calc_DialogView.getLeftTailChecked();
        midTailChecked = x2Dist_Calc_DialogView.getMidTailChecked();
        rightTailChecked = x2Dist_Calc_DialogView.getRightTailChecked();
        if (midTailChecked) {
            leftTailChecked = true;
            rightTailChecked = true;
        }
    }
    
    public Pane getTheContainingPane() { return theContainingPane; }
}
