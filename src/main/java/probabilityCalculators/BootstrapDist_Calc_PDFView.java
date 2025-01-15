/**************************************************
 *             NormalDist_Calc_PDFView            *
 *                    12/31/24                    *
 *                     12:00                      *
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
//import javafx.scene.layout.Pane;
// import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import probabilityDistributions.StandardNormal;
import smarttextfield.*;
import utilityClasses.DataUtilities;
import utilityClasses.StringUtilities;

public class BootstrapDist_Calc_PDFView extends Distributions_Calc_PDFView {

    boolean printTheStuff = true;
    double mu, sigma;
    double[] dbl_AllTheSTFs;    //  doubles of the al_ProbCalcs
    // My classes  
    DoublyLinkedSTF al_ProbCalcs_STF;
    NormalDist_Calc_DialogView normalDist_Calc_DialogView;
    StandardNormal zDistr;

    //  POJOs / FX   

    public BootstrapDist_Calc_PDFView(ProbCalc_Dashboard probCalc_Dashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(probCalc_Dashboard, placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff) {
            System.out.println("\n47 *** BootstrapDist_Calc_PDFView, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        xPrintPosLeft = 0.05;
        xPrintPosCenter = 0.325;
        xPrintPosRight = 0.5;
        yPrintPosLeftRight = 0.9;
        yPrintPosCenter = 0.95;   
        maxOfYScale = 0.45; 
    
        //  There are no check boxes, but superclass constructs a CheckBoxRow
        nCheckBoxes = 0;
        zDistr = new StandardNormal();
        initializing = true;
        mu = 0.0; sigma = 1.0;
        al_ProbCalcs_STF = new DoublyLinkedSTF();
        //System.out.println("58 BootstrapDist_Calc_PDFView, constructing");
        normalDist_Calc_DialogView = probCalc_Dashboard.get_Normal_DialogView();
        al_ProbCalcs_STF = normalDist_Calc_DialogView.getAllTheSTFs();
        this.probCalc_Dashboard = probCalc_Dashboard;
        tailChoice = "NotEqual";
        //System.out.println("63 BootstrapDist_Calc_PDFView, constructing");
        respondToChanges();
        graphCanvas = new Canvas(initWidth, initHeight);
        //System.out.println("66 BootstrapDist_Calc_PDFView, constructing");
        makeTheCheckBoxes();
        //System.out.println("68 BootstrapDist_Calc_PDFView, constructing");
        makeItHappen();
        //System.out.println("70 BootstrapDist_Calc_PDFView, constructing");
    }
    
    @Override
    protected void setUpUI() { 
        //System.out.println("75 BootstrapDist_Calc_PDFView, setUpUI()");
        String title2String;
        normalDist_Calc_DialogView.constructGraphStatus();
        okToGraph = normalDist_Calc_DialogView.getOKToGraph();
        txtTitle1 = new Text(50, 25, " Probability calculations -- Normal Distribution ");
        title2String = "";
        txtTitle2 = new Text (60, 45, title2String);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
 
    public void respondToChanges() {
        //System.out.println("87 BootstrapDist_Calc_PDFView, respondToChanges()");
        normalDist_Calc_DialogView.constructGraphStatus();
        // Check for shading
        leftTailChecked = normalDist_Calc_DialogView.getLeftTailChecked();
        midTailChecked = normalDist_Calc_DialogView.getMidTailChecked();
        rightTailChecked = normalDist_Calc_DialogView.getRightTailChecked();
        
        if (initializing) {
            okToGraph = false;
            mu = normalDist_Calc_DialogView.getMu();
            sigma = normalDist_Calc_DialogView.getSigma();
            newX_Lower = mu - 3.5 * sigma;
            newX_Upper =  mu + 3.5 * sigma; 
            initializeGraphParameters();
        } else {
            al_ProbCalcs_STF = normalDist_Calc_DialogView.getAllTheSTFs(); 
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

            leftArea = dbl_AllTheSTFs[3];
            midArea = dbl_AllTheSTFs[4];
            rightArea = dbl_AllTheSTFs[5];
            left_Middle_Boundary = dbl_AllTheSTFs[6];
            middle_Right_Boundary = dbl_AllTheSTFs[7];            
            
            mu = normalDist_Calc_DialogView.getMu();
            sigma = normalDist_Calc_DialogView.getSigma();
            newX_Lower = mu - 3.5 * sigma;
            newX_Upper =  mu + 3.5 * sigma; 

            xAxis.setLowerBound(newX_Lower); 
            xAxis.setUpperBound(newX_Upper);
        }
        initializing = false;
    }
    
   @Override
    public void initializeGraphParameters() {
        //System.out.println("146 BootstrapDist_Calc_PDFView, initializeGraphParameters()");
        xAxis = new JustAnAxis(newX_Lower, newX_Upper);
        xAxis.setSide(Side.BOTTOM);       
        prepareTheSupportAxis();
        yAxis = new JustAnAxis(yDataMin, maxOfYScale);
        yAxis.setSide(Side.LEFT);
        yAxis.forceLowScaleEndToBe(0.0);  
        yAxis.setBounds(0.0, maxOfYScale);
        newY_Lower = yDataMin; newY_Upper = maxOfYScale;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper ); 
    }
    
    @Override
    public void doTheGraph() {  
        //System.out.println("163 BootstrapDist_Calc_PDFView, doTheGraph()");
        normalDist_Calc_DialogView.constructGraphStatus();
        okToGraph = normalDist_Calc_DialogView.getOKToGraph();
        dTG_Continuous();
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        //  Start point for graph
        dTG_xx0 = newX_Lower; 
        dTG_yy0 = zDistr.getDensity(calculateZOf(dTG_xx0));
        
        for (double x = newX_Lower; x <= newX_Upper; x += delta) {
            dTG_xx1 = x;
            dTG_yy1 = zDistr.getDensity(calculateZOf(dTG_xx1));
            if (!okToGraph) { dTG_yy1 = 0.0; }
            xStart = xAxis.getDisplayPosition(dTG_xx0); 
            yStart = yAxis.getDisplayPosition(dTG_yy0); 
            xStop = xAxis.getDisplayPosition(dTG_xx1);
            yStop = yAxis.getDisplayPosition(dTG_yy1);          
            gc.setLineWidth(2);
            gc.setStroke(Color.BLACK);
            gc.strokeLine(xStart, yStart, xStop, yStop);

            if ((leftTailChecked || midTailChecked) && (x < left_Middle_Boundary)) {
                yStart = yAxis.getDisplayPosition(0.0);                
                gc.strokeLine(xStart, yStart, xStop, yStop);
            }
            
            if ((rightTailChecked || midTailChecked) && (x > middle_Right_Boundary)) {
                yStart = yAxis.getDisplayPosition(0.0);                
                gc.strokeLine(xStart, yStart, xStop, yStop);
            }     

            dTG_xx0 = dTG_xx1; dTG_yy0 = dTG_yy1;   //  Next start point for line segment
        } 
        
        if (okToGraph) {
            printTheProbabilities(); 
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
    
    private void printTheProbabilities() {
        //System.out.println("210 BootstrapDist_Calc_PDFView, printTheProbabilities()");
        double xPrtPosition, yPrtPosition;

        // if (normalDist_Calc_DialogView.getLeftTailChecked()) {
        if (leftTailChecked) {
            leftArea_XPosition = calculateScaleScoreOf(zDistr.getInvLeftTailArea(.01));
            xPrtPosition = xAxis.getDisplayPosition(leftArea_XPosition);
            yPrtPosition = yAxis.getDisplayPosition(yPrintPosLeftRight * yAxis.getUB());
            gc.setFill(Color.BLUE);
            prtLeftArea = "LeftArea = " +  StringUtilities.roundDoubleToNDigitString(leftArea, 4);
            if (leftArea != Double.NaN) { gc.fillText(prtLeftArea, xPrtPosition, yPrtPosition);  }
        }
        
        //if (normalDist_Calc_DialogView.getRightTailChecked() == true) {
        if (rightTailChecked) {
            rightArea_XPosition = calculateScaleScoreOf(zDistr.getInvRightTailArea(.05));
            xPrtPosition = xAxis.getDisplayPosition(rightArea_XPosition);
            yPrtPosition = yAxis.getDisplayPosition(yPrintPosLeftRight * yAxis.getUB());
            gc.setFill(Color.BLUE);
            prtRightArea = "RightArea = " +  StringUtilities.roundDoubleToNDigitString(rightArea, 4);
            if (rightArea != Double.NaN) { gc.fillText(prtRightArea, xPrtPosition, yPrtPosition); }
        }
        // On the other hand...
        // if (normalDist_Calc_DialogView.getMidTailChecked() == true) {
        
        if (midTailChecked) {
            leftArea_XPosition = calculateScaleScoreOf(zDistr.getInvLeftTailArea(.01));
            xPrtPosition = xAxis.getDisplayPosition(leftArea_XPosition);
            yPrtPosition = yAxis.getDisplayPosition(yPrintPosLeftRight * yAxis.getUB());
            gc.setFill(Color.BLUE);
            prtLeftArea = "LeftArea = " +  StringUtilities.roundDoubleToNDigitString(leftArea, 4);
            if (leftArea != Double.NaN) { gc.fillText(prtLeftArea, xPrtPosition, yPrtPosition); }

            middleArea_XPosition = calculateScaleScoreOf(zDistr.getInvLeftTailArea(.30));
            xPrtPosition = xAxis.getDisplayPosition(middleArea_XPosition);
            yPrtPosition = yAxis.getDisplayPosition(yPrintPosCenter * yAxis.getUB());
            prtMidArea = "MidArea = " +  StringUtilities.roundDoubleToNDigitString(midArea, 4);
            if (midArea != Double.NaN) { gc.fillText(prtMidArea, xPrtPosition, yPrtPosition); }                 

            rightArea_XPosition = calculateScaleScoreOf(zDistr.getInvRightTailArea(.05));
            xPrtPosition = xAxis.getDisplayPosition(rightArea_XPosition);
            yPrtPosition = yAxis.getDisplayPosition(yPrintPosLeftRight * yAxis.getUB());
            prtRightArea = "RightArea = " +  StringUtilities.roundDoubleToNDigitString(rightArea, 4);
            if (rightArea != Double.NaN) {gc.fillText(prtRightArea, xPrtPosition, yPrtPosition); }  
        }      
    }

    private double calculateZOf(double x) { return (x - mu)/sigma; }
    private double calculateScaleScoreOf(double z) { return z * sigma + mu; }
    public void setInitializingToTrue() { initializing = true; }
    //public Pane getTheContainingPane() { return theContainingPane; }
}
