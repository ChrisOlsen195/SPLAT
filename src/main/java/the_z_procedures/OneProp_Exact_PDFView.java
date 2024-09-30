/**************************************************
 *           OneProp_Exact_PDFView                *
 *                  09/21/24                      *
 *                    21:00                       *
 *************************************************/
package the_z_procedures;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import probabilityDistributions.BetaDistribution;
import probabilityDistributions.BinomialDistribution;
import probabilityDistributions.StandardNormal;
import superClasses.BivariateScale_W_CheckBoxes_View;
import utilityClasses.MyAlerts;

public class OneProp_Exact_PDFView extends BivariateScale_W_CheckBoxes_View {
    
    // POJOs
    int nSuccesses, nFailures, nTrials;

    double zStatistic, meanProp, absVal_zStatistic, sdProp,
           pHat, pHypoth, hypothSuccesses, hypothFailures; 
    
    double daModalValue, dbl_NTrials, daMode;    //  Let's see how this works.
    final double MIDDLE_Z = 0.9999;
    double[] /*initialInterval,*/ binomialProbs;
    
    String hypotheses, title1String, title2String;

    // My classes  
    OneProp_Inf_Model oneProp_Inf_Model;
    StandardNormal zDistribution;
    BinomialDistribution binomDist;
    BetaDistribution betaDist_CI, betaDist_HT;

    //  POJOs / FX
    AnchorPane anchorPane;  
    Pane theContainingPane;
    
    public OneProp_Exact_PDFView(OneProp_Inf_Model oneProp_Inf_Model, OneProp_Inf_Dashboard oneProp_Inf_Dashboard,
            double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        //System.out.println("55 OneProp_Exact_PDFView, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.oneProp_Inf_Model = oneProp_Inf_Model;
        
        nSuccesses = oneProp_Inf_Model.getNSuccesses();
        nTrials = oneProp_Inf_Model.getSampeSize();
        nFailures = nTrials - nSuccesses;
        pHat = oneProp_Inf_Model.getPHat();
        pHypoth = oneProp_Inf_Model.getHypothProp();
        dbl_NTrials = nTrials;
        
        title1String = ""; title2String = "";
        
        hypothSuccesses = dbl_NTrials * pHypoth;
        hypothFailures = dbl_NTrials - hypothSuccesses;
        
        betaDist_CI = new BetaDistribution(nSuccesses + 0.5, nFailures + 0.5);
        betaDist_HT = new BetaDistribution(hypothSuccesses, hypothFailures);
        
        meanProp = oneProp_Inf_Model.getHypothProp();
        sdProp = Math.sqrt(meanProp * (1.0 - meanProp) / dbl_NTrials);
        binomDist = new BinomialDistribution(nTrials, meanProp);
        binomialProbs = new double[nTrials + 1];
        
        for (int ithX = 0; ithX <= nTrials; ithX++) {
            binomialProbs[ithX] = binomDist.getPDF(ithX);
        }
        
        nCheckBoxes = 2;

        scatterPlotCheckBoxDescr = new String[nCheckBoxes];
        scatterPlotCheckBoxDescr[0] = " Check assumptions ";
        scatterPlotCheckBoxDescr[1] = " Identify P-value ";
        
        txtTitle1 = new Text(50, 25, " Exact Sampling distribution ");
        hypotheses = oneProp_Inf_Model.getHypotheses();
        alpha = this.oneProp_Inf_Model.getAlpha();
        
        zStatistic = this.oneProp_Inf_Model.getZStat();
        absVal_zStatistic = Math.abs(zStatistic);
        pValue = this.oneProp_Inf_Model.getPValue();
        zDistribution = new StandardNormal();
        //initialInterval = new double[2];
        middle_ForGraph = MIDDLE_Z; 
        //initialInterval = zDistribution.getInverseMiddleArea(middle_ForGraph);
        
        switch (hypotheses) {
            case "NotEqual":
                shadeLeftTail = true;
                shadeRightTail = true;
                leftTailCutPoint = -absVal_zStatistic;
                rightTailCutPoint = absVal_zStatistic;
                break;

            case "LessThan":
                shadeLeftTail = true;
                leftTailCutPoint = zStatistic;
                break;

            case "GreaterThan":
                shadeRightTail = true;
                rightTailCutPoint = zStatistic;
                break;

            default:
                String switchFailure = "Switch failure: OneProp_Exact_PDFView 121 " + hypotheses;
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
        txtTitle1 = new Text(50, 25, " Exact Inference for a single proportion  ");
        
        switch(hypotheses) {
            case "NotEqual":
                title2String = String.format("%10s %5.3f  %10s %5.3f", " Null: p\u2080 =", pHypoth,
                                                                       "  Alt:  p\u2080 \u2260", pHypoth);                
                break;

            case "LessThan":
                title2String = String.format("%10s %5.3f  %10s %5.3f", " Null: p\u2080 =", pHypoth,
                                                                       "  Alt:  p\u2080 <", pHypoth);
                break;

            case "GreaterThan":
                title2String = String.format("%10s %5.3f  %10s %5.3f", " Null: p\u2080 =", pHypoth,
                                                                       "  Alt:  p\u2080 >", pHypoth);                
                break;

            default:
                String switchFailure = "Switch failure: OneProp_Exact_PDFView 169 " + "hypotheses";
                MyAlerts.showUnexpectedErrorAlert(switchFailure);           
        }

        txtTitle2 = new Text (60, 45, title2String);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    @Override
    public void initializeGraphParameters() {
        //initialInterval = zDistribution.getInverseMiddleArea(MIDDLE_Z);
        fromHere = -0.5/dbl_NTrials;
        toThere = 1.0 + 0.5/dbl_NTrials;
        xAxis = new JustAnAxis(fromHere, toThere);
        xAxis.setSide(Side.BOTTOM);       
        prepareTheDensityAxis();
        daMode = Math.floor(nTrials * meanProp);
        daModalValue = binomialProbs[(int)Math.floor(nTrials * meanProp + 0.025)] + .03;
        yAxis = new JustAnAxis(0.0, daModalValue);
        yAxis.setSide(Side.LEFT);
        yAxis.forceLowScaleEndToBe(0.0);  
        yAxis.setBounds(0.0, daModalValue);
        newX_Lower = fromHere; newX_Upper = toThere;
        newY_Lower = yDataMin; newY_Upper = daModalValue;
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
    
    public double getInitialYMax() { return daModalValue; }
    
    @Override
    public void doTheGraph() {   
        double xx0, yy0, xx1, yy1;
        String tempString;
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double paneHeight = dragableAnchorPane.getHeight();
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
        
        assumptionCheckIsDesired = checkBoxSettings[0];
        identifyPValueIsDesired = checkBoxSettings[1];
        
        for (int ithX = 0; ithX <= nTrials; ithX++) {
            xx0 = (ithX - 0.5)/dbl_NTrials;
            xx1 = (ithX + 0.5)/dbl_NTrials;
            yy1 = binomialProbs[ithX];
            xStart = xAxis.getDisplayPosition(xx0); 
            yStart = yAxis.getDisplayPosition(0.0); 
            xStop = xAxis.getDisplayPosition(xx1);
            yStop = yAxis.getDisplayPosition(yy1);        
            //double height = yStop - yStart;
            //double width = xStop - xStart; 
            gc.setLineWidth(2);
            gc.setStroke(Color.BLACK);
            gc.strokeLine(xStart, yStart, xStart, yStop);
            gc.strokeLine(xStop, yStart, xStop, yStop);
            gc.strokeLine(xStart, yStart, xStop, yStart);
            gc.strokeLine(xStart, yStop, xStop, yStop);
        }

/***************************************************************************
 *  Interval for exact P-values is the Jeffreys Interval.                  *
 *  Brown, L. D., Cai, T. T, & DasGupta, A. (2001).Interval Estimation for *
 *  a Binomial proportion.  Statistical Science 16(2): 101-133, p108       *
 **************************************************************************/
        xx0 = xGraphLeft; yy0 = nonStandardNormalDensity(xx0);
        for (double x = xGraphLeft; x <= xGraphRight; x += delta) {
            xx1 = x;
            yy1 = nonStandardNormalDensity(xx1);
            xStart = xAxis.getDisplayPosition(xx0); 
            yStart = yAxis.getDisplayPosition(yy0); 
            xStop = xAxis.getDisplayPosition(xx1);
            yStop = yAxis.getDisplayPosition(yy1);          
            gc.setLineWidth(2);
            gc.setStroke(Color.BLACK);
            gc.strokeLine(xStart, yStart, xStop, yStop);            
            xx0 = xx1; yy0 = yy1;   //  Next start point for line segment
        }   

        xStart = xStop = xAxis.getDisplayPosition(pHat);
        yStart = yAxis.getDisplayPosition(0.0);          
        yStop = yAxis.getDisplayPosition(daModalValue);        

        gc.setLineWidth(2);
        gc.setStroke(Color.BLUE);
        gc.strokeLine(xStart, yStart, xStop, yStop + 12);
  
        switch (hypotheses) {
            case "NotEqual":
                if (pHat >= pHypoth) {
                    pValue = 2.0 * (1.0 - betaDist_HT.getLeftTailArea(pHat));
                }
                else { pValue = 2.0 * (betaDist_HT.getLeftTailArea(pHat)); }
                break;

            case "LessThan":
                pValue = betaDist_HT.getLeftTailArea(pHat);
                break;

            case "GreaterThan":
                pValue = 1.0 - betaDist_HT.getLeftTailArea(pHat);
                break;

            default:
                String switchFailure = "Switch failure: OneProp_Exact_PDFView 338 " + hypotheses;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }         
        
        tempString = String.format("pHat = %6.3f", pHat);
        gc.setFill(Color.BLUE);
        gc.fillText(tempString, xStop + 5, yStop + 12);
        
        if (identifyPValueIsDesired) {
            gc.setFill(Color.BLUE);
            tempString = String.format("pValue = %4.3f", pValue);
            gc.fillText(tempString, 0.65 * paneWidth, 0.10 * paneHeight);
        }
        
        if (assumptionCheckIsDesired) {
            gc.setFill(Color.GREEN);
            if (nSuccesses < 10) {
                gc.setFill(Color.RED);  
                tempString = String.format("np =%2d < 10", nSuccesses);
                gc.fillText(tempString, 0.025 * paneWidth, 0.10 * paneHeight);
            }
            else {
                tempString = String.format("np =%4d", nSuccesses);
                gc.fillText(tempString, 0.025 * paneWidth, 0.10 * paneHeight);                
            }
            gc.setFill(Color.GREEN);
            if (nFailures < 10) {
                gc.setFill(Color.RED);  
                tempString = String.format("n(1 - p) =%2d < 10", nFailures);
                gc.fillText(tempString, 0.025 * paneWidth, 0.15 * paneHeight);
            }
            else {
                tempString = String.format("n(1 - p) =%4d", nFailures);
                gc.fillText(tempString, 0.025 * paneWidth, 0.15 * paneHeight);                
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
    
    private double nonStandardNormalDensity(double x) {
        double expNum = -(x - meanProp) * (x - meanProp);
        double expDen = 2.0 * sdProp * sdProp;
        double theExponent = expNum / expDen;
        double pdfNumerator = Math.exp(theExponent);
        double pdfDenominator = sdProp * 2.5066283; //   sqrt(2*pi)
        double thePDF = pdfNumerator / pdfDenominator;
        return thePDF / dbl_NTrials; 
    }
    
    public Pane getTheContainingPane() { return theContainingPane; }   
}
