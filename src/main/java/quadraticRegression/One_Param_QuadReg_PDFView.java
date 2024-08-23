/**************************************************
 *           OneParam_QuadReg_PDFView             *
 *                  02/19/24                      *
 *                    15:00                       *
 *************************************************/
package quadraticRegression;

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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import probabilityDistributions.*;
import splat.Data_Manager;
import superClasses.BivariateScale_W_CheckBoxes_View;
import utilityClasses.*;

public class One_Param_QuadReg_PDFView extends BivariateScale_W_CheckBoxes_View {
    // POJOs

    int df;

    double tStat, absValTStat;
    double daMode = 0.4;    //  Let's see how this works.
    
    final double MIDDLE_T = 0.9999;
    final double[] alphas = {0.10, 0.05, 0.025, 0.01};
    double[] initialInterval;
    
    String hypotheses;
    
    String waldoFile = "One_Param_QuadReg_PDFView";
    // String waldoFile = "";

    // My classes  
    Data_Manager dm;
    TDistribution tDistr;

    //  POJOs / FX
    // AnchorPane anchorPane; 
    Pane theContainingPane;
    
    public One_Param_QuadReg_PDFView(OneParam_QuadReg_Model oneParam_QuadReg_Model, OneParam_QuadReg_Dashboard oneParam_QuadReg_Dashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        dm = oneParam_QuadReg_Model.getDataManager();
        dm.whereIsWaldo(56, waldoFile, "Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.noInt_QuadReg_Model = oneParam_QuadReg_Model;
        this.noInt_QuadReg_Dashboard = oneParam_QuadReg_Dashboard;
        
        nCheckBoxes = 2;
        scatterPlotCheckBoxDescr = new String[nCheckBoxes];
        scatterPlotCheckBoxDescr[0] = " Identify P-value ";
        scatterPlotCheckBoxDescr[1] = " Identify alphas  ";
        
        txtTitle1 = new Text(50, 25, " Scatterplot ");
        hypotheses = "NotEqual";
        alpha = 0.05;
        
        df = 2;
        tStat = oneParam_QuadReg_Model.getTStat();
        absValTStat = Math.abs(tStat);
        pValue = oneParam_QuadReg_Model.getPValue();
        tDistr = new TDistribution(df);
        initialInterval = new double[2];
        middle_ForGraph = MIDDLE_T; 
        initialInterval = tDistr.getInverseMiddleArea(middle_ForGraph);
        
        switch (hypotheses) {
            case "NotEqual":
                shadeLeftTail = true;
                shadeRightTail = true;
                leftTailCutPoint = -absValTStat;
                rightTailCutPoint = absValTStat;
                break;

            case "LessThan":
                shadeLeftTail = true;
                leftTailCutPoint = tStat;
                break;

            case "GreaterThan":
                shadeRightTail = true;
                rightTailCutPoint = tStat;
                break;

            default: 
                String switchFailure = "Switch failure: OneParam_QuadReg_PDFView 99 " + hypotheses;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }
        
        checkBoxHeight = 350.0;
        graphCanvas = new Canvas(initWidth, initHeight);
        makeTheCheckBoxes();
        makeItHappen();
    }  
  
    private void makeItHappen() {
        theContainingPane = new Pane();        
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
        
    public void completeTheDeal() {
        initializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();
        setHandlers();
        doTheGraph();     
        theContainingPane = dragableAnchorPane.getTheContainingPane();     
    }
        
    @Override
    protected void setUpUI() { 
        String title2String;
        txtTitle1 = new Text(50, 25, " Model Utility Test");
        
        if (df > 1) {
            title2String = String.valueOf(df) + " degrees of freedom";
        }
        else {
            title2String = String.valueOf(df) + " degree of freedom";
        }
        
        txtTitle2 = new Text (60, 45, title2String);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    public void initializeGraphParameters() {
        initialInterval = tDistr.getInverseMiddleArea(MIDDLE_T);
        fromHere = 1.05 * Math.min(-absValTStat, initialInterval[0]);
        toThere = 1.05 * Math.max(absValTStat, initialInterval[1]);
        xAxis = new JustAnAxis(fromHere, toThere);
        xAxis.setSide(Side.BOTTOM);       
        prepareTheDensityAxis();
        yAxis = new JustAnAxis(yDataMin, yDataMax);
        yAxis.setSide(Side.LEFT);
        yAxis.forceLowScaleEndToBe(0.0);  
        yAxis.setBounds(0.0, yDataMax);
        newX_Lower = fromHere; newX_Upper = toThere;
        newY_Lower = yDataMin; newY_Upper = yDataMax;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );  
    }

    private void prepareTheDensityAxis() {
        xGraphLeft = fromHere;   
        xGraphRight = toThere;
        bigDelta = (xGraphRight - xGraphLeft) / NUMBER_OF_DXs;
        delta = bigDelta;
        xDataMin = xDataMax = xGraphLeft;
        xRange = xGraphRight - xGraphLeft;        
        yRange = yDataMax = getInitialYMax();
        // These constants control the rate of axis scale change when dragging
        deltaX = 0.005 * xRange; deltaY = 0.005 * yRange;   
    }
    
    public void setIntervalOfInterest(double startHere, double endHere)  {
        fromHere = startHere; toThere = endHere;
        delta = (endHere - startHere) / bigDelta * NUMBER_OF_DXs; 
    }
    
    public double getInitialYMax() {
        yDataMax = 1.0;
        if (df == 2)
            yDataMax = 0.40;                
        if (df > 2)
            yDataMax = 0.45;
        if (df > 5)
            yDataMax = 0.50;                
        return yDataMax;
    }
    
    //  Note to self: would Bezier curves be worth and added code?
    public void doTheGraph() {   
        double xx0, yy0, xx1, yy1;
        String tempString;
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
            AnchorPane.setLeftAnchor(scatterPlotCheckBoxes[chex], (chex) * tempWidth / 5.0);
        }
        
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        identifyPValueIsDesired = checkBoxSettings[0];
        assumptionCheckIsDesired = checkBoxSettings[1];

        //  Start point for graph
        xx0 = xGraphLeft; yy0 = tDistr.getDensity(xx0);
        
        for (double x = xGraphLeft; x <= xGraphRight; x += delta) {
            xx1 = x;
            yy1 = tDistr.getDensity(xx1);
            xStart = xAxis.getDisplayPosition(xx0); 
            yStart = yAxis.getDisplayPosition(yy0); 
            xStop = xAxis.getDisplayPosition(xx1);
            yStop = yAxis.getDisplayPosition(yy1);          
            gc.setLineWidth(2);
            gc.setStroke(Color.BLACK);
            gc.strokeLine(xStart, yStart, xStop, yStop);

            if ((shadeLeftTail) && (x < leftTailCutPoint)) {
                yStart = yAxis.getDisplayPosition(0.0);                
                gc.strokeLine(xStart, yStart, xStop, yStop);
            }
            
            if ((shadeRightTail) && (x > rightTailCutPoint)) {
                yStart = yAxis.getDisplayPosition(0.0);                
                gc.strokeLine(xStart, yStart, xStop, yStop);
            }     

            xx0 = xx1; yy0 = yy1;   //  Next start point for line segment
        }   

        double elFactoro = 7.0; //  Fudge factor to lessen clutter
        xStart = xStop = xAxis.getDisplayPosition(tStat);
        yStart = yAxis.getDisplayPosition(0.0);

        double heightAtPoint_10 = yAxis.getDisplayPosition(tDistr.getDensity(tDistr.getInvRightTailArea(0.10))) - elFactoro * 0.1 * yStart;
        double heightAtPoint_05 = yAxis.getDisplayPosition(tDistr.getDensity(tDistr.getInvRightTailArea(0.05))) - elFactoro * 0.05 * yStart;            

        yStop = yAxis.getDisplayPosition(daMode);        

        gc.setLineWidth(2);
        gc.setStroke(Color.RED);
        gc.strokeLine(xStart, yStart, xStop, yStop);

        if (identifyPValueIsDesired) {
            tempString = String.format("t = %6.3f,  pValue = %4.3f", tStat, pValue);
        }
        else {
            tempString = String.format("t = %6.3f", tStat);
        }
        
        gc.setFill(Color.RED);
        gc.fillText(tempString, xStop + 5, yStop - 5);

        if (assumptionCheckIsDesired == true) {
            daMode = 0.4;    //  Let's see how this works.
            double otherXStart, otherXStop;
            
            for (int ithAlpha = 0; ithAlpha < 4; ithAlpha++) {
                double thisAlpha = alphas[ithAlpha];  
                
                switch (hypotheses) {
                    case "NotEqual":
                        theCriticalValue = tDistr.getInvRightTailArea(thisAlpha/2.0); 
                        xStart = xStop = xAxis.getDisplayPosition(theCriticalValue);
                        yStart = yAxis.getDisplayPosition(0.0);
                        yStop = yAxis.getDisplayPosition(daMode * (-0.2 * ithAlpha + 0.8)); 
                        gc.setLineWidth(2);
                        gc.setStroke(Color.BLUE);
                        gc.strokeLine(xStart, yStart, xStop, yStop);
                        gc.setFill(Color.BLUE);
                        tempString = String.format("Critical Value (\u03B1 = %4.3f) =%7.3f", thisAlpha, theCriticalValue);
                        gc.fillText(tempString, xStop - 00, yStop - 5); 
                        otherXStart = otherXStop = xAxis.getDisplayPosition(-theCriticalValue);
                        gc.strokeLine(otherXStart, yStart, otherXStop, yStop);
                        break;

                    case "LessThan":
                        theCriticalValue = tDistr.getInvRightTailArea(thisAlpha); 
                        xStart = xStop = xAxis.getDisplayPosition(theCriticalValue);
                        double leftXStart = -xStart;
                        double leftXStop = - xStop;
                        yStart = yAxis.getDisplayPosition(0.0);
                        yStop = yAxis.getDisplayPosition(daMode * (-0.2 * ithAlpha + 0.8)); 
                        gc.setLineWidth(2);
                        gc.setStroke(Color.BLUE);
                        gc.strokeLine(leftXStart, yStart, leftXStop, yStop);
                        gc.setFill(Color.BLUE);
                        tempString = String.format("Critical Value (\u03B1 = %4.3f) =%7.3f", thisAlpha, theCriticalValue);
                        gc.fillText(tempString, xStop - 00, yStop - 5); 
                        otherXStart = otherXStop = xAxis.getDisplayPosition(-theCriticalValue);
                        gc.strokeLine(otherXStart, yStart, otherXStop, yStop); 
                        ///???
                        gc.strokeLine(otherXStart, yStop, xStart, yStop);                        
                        break;

                    case "GreaterThan":
                        theCriticalValue = tDistr.getInvRightTailArea(thisAlpha); 
                        xStart = xStop = xAxis.getDisplayPosition(theCriticalValue);
                        yStart = yAxis.getDisplayPosition(0.0);
                        yStop = yAxis.getDisplayPosition(daMode * (-0.2 * ithAlpha + 0.8)); 
                        gc.setLineWidth(2);
                        gc.setStroke(Color.BLUE);
                        gc.strokeLine(xStart, yStart, xStop, yStop);
                        gc.setFill(Color.BLUE);
                        tempString = String.format("Critical Value (\u03B1 = %4.3f) =%7.3f", thisAlpha, theCriticalValue);
                        gc.fillText(tempString, xStop - 00, yStop - 5); 
                        break;

                    default: 
                        String switchFailure = "Switch failure: OneParam_QuadReg_PDFView 348 " + hypotheses;
                        MyAlerts.showUnexpectedErrorAlert(switchFailure);
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

    public Pane getTheContainingPane() { return theContainingPane; }
}





