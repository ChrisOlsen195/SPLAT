/**************************************************
 *               Indep_t_PDFView                  *
 *                  03/08/25                      *
 *                    12:00                       *
 *************************************************/
package the_t_procedures;

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
import splat.Data_Manager;
import superClasses.BivariateScale_W_CheckBoxes_View;
import utilityClasses.MyAlerts;
import theRProbDists.*;

public class Indep_t_PDFView extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    double tStat, absValTStat, dbl_df, xStart_tPVal; 
    
    final double daMode = 0.4;    //  Let's see how this works.
    final double MIDDLE_T = 0.99999;
    final double[] alphas = {0.10, 0.05, 0.025, 0.01};
    double[] initialInterval;
    
    String hypotheses;
    //String waldoFile = "Indep_t_PDFView";
    String waldoFile = "";

    // My classes  
    Data_Manager dm;
    Indep_t_Model indep_t_Model;
    T_double_df tDistr;

    //  POJOs / FX
    AnchorPane anchorPane;
  
    Pane theContainingPane;
    
    public Indep_t_PDFView(Indep_t_Model indep_t_Model, Indep_t_Dashboard indep_t_Dashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        this.indep_t_Model = indep_t_Model;
        dm = indep_t_Model.getDataManager();
        dm.whereIsWaldo(59, waldoFile, "Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        //this.indep_t_Model = indep_t_Model;
        nCheckBoxes = 2;
        scatterPlotCheckBoxDescr = new String[nCheckBoxes];
        scatterPlotCheckBoxDescr[0] = " Identify P-value ";
        scatterPlotCheckBoxDescr[1] = " Identify alphas  ";
        
        txtTitle1 = new Text(50, 25, " Scatterplot ");

        hypotheses = indep_t_Model.getHypotheses();
        alpha = indep_t_Model.getAlpha();
        
        dbl_df = indep_t_Model.getDF();
        tStat = indep_t_Model.getTStat();
        absValTStat = Math.abs(tStat);
        pValue = indep_t_Model.getPValue();
        tDistr = new T_double_df(dbl_df);
        initialInterval = new double[2];
        middle_ForGraph = MIDDLE_T; 
        initialInterval[0] = tDistr.quantile((1. - middle_ForGraph) / 2.0);
        initialInterval[1] = -initialInterval[0];

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
                String switchFailure = "Switch failure: Indep_t_PDFView 102 " + hypotheses;
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
        txtTitle1 = new Text(50, 25, " t test");
        
        if (dbl_df > 1) {
            title2String = String.format("%5.2f", dbl_df) + " degrees of freedom";
        }
        else {
            title2String = String.valueOf(dbl_df) + " degree of freedom";
        }
        
        txtTitle2 = new Text (60, 45, title2String);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    @Override
    public void initializeGraphParameters() {
        initialInterval[0] = tDistr.quantile((1. - MIDDLE_T) / 2.0);
        initialInterval[1] = -initialInterval[0];
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
        if (dbl_df == 2)
            yDataMax = 0.40;                
        if (dbl_df > 2)
            yDataMax = 0.45;
        if (dbl_df > 5)
            yDataMax = 0.50;                
        return yDataMax;
    }
    
    @Override
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
        xx0 = xGraphLeft; yy0 = tDistr.density(xx0, false);
        
        for (double x = xGraphLeft; x <= xGraphRight; x += delta) {
            xx1 = x;
            yy1 = tDistr.density(xx1, false);
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

        xStart = xStop = xStart_tPVal = xAxis.getDisplayPosition(tStat);
        yStart = yAxis.getDisplayPosition(0.0);
        yStop = yAxis.getDisplayPosition(daMode);        

        gc.setLineWidth(2);
        gc.setStroke(Color.RED);
        gc.strokeLine(xStart, yStart, xStop, yStop);
        double rightEndPad = 225.;
        double temp  = dbl_df / (dbl_df - 2.0);
        
        if (identifyPValueIsDesired) {
            if (paneWidth - xStart_tPVal < rightEndPad) { xStart_tPVal = xAxis.getDisplayPosition(temp) - 100.; }
            tempString = String.format("t = %6.3f, pValue = %4.3f", tStat, pValue);
            gc.setFill(Color.RED);
            gc.fillText(tempString, xStart_tPVal + 5, yStop - 5);
        } else {
            if (paneWidth - xStart_tPVal < rightEndPad) { xStart_tPVal = xAxis.getDisplayPosition(temp) - 100.; }
            tempString = String.format("t = %6.3f", tStat);
            gc.setFill(Color.RED);
            gc.fillText(tempString, xStart_tPVal + 5, yStop - 5);            
        }

        if (assumptionCheckIsDesired) {
            double daMode = 0.4;    //  Let's see how this works.
            double otherXStart, otherXStop;           
            for (int ithAlpha = 0; ithAlpha < 4; ithAlpha++) {
                double thisAlpha = alphas[ithAlpha];   
                
                switch (hypotheses) {
                    case "NotEqual":
                        theCriticalValue = tDistr.quantile(1.0 - thisAlpha/2.0); 
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
                        theCriticalValue = tDistr.quantile(1.0 - thisAlpha); 
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
                        theCriticalValue = tDistr.quantile(1.0 - thisAlpha); 
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
                        String switchFailure = "Switch failure: Indep_t_PDFView 347 " + hypotheses;
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


