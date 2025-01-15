/**************************************************
 *             GeometricDist_Calc_PDFView         *
 *                    12/31/24                    *
 *                     12:00                      *
 *************************************************/
package probabilityCalculators;

import genericClasses.JustAnAxis;
import java.util.ArrayList;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;  //  Need for Static AnchorPane
import javafx.scene.paint.Color;
//import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;import probabilityDistributions.GeometricDistribution;
import smarttextfield.*;
import utilityClasses.MyAlerts;
import utilityClasses.StringUtilities;

public class GeometricDist_Calc_PDFView extends Distributions_Calc_PDFView {
    
    // POJOs
    boolean printTheStuff = true;
    
    int geometric_nToDisplay, lowerShadeBound, 
        upperShadeBound, probSelection, /*intDaChoice,*/ intDaLeftChoice, 
        intDaRightChoice, geomStart, geomStop;

    //     ********  Left and Right base X positions ********
    double init_LeftX, init_RightX, geometric_P, geompdf,  geomcdf,
           maxGeometricProbThisTime;

    double daProb;

    // My classes  
    GeometricDistribution geomDistr;
    GeometricDist_Calc_DialogView geometricDist_Calc_DialogView;
    ArrayList<SmartTextField> allTheSTFs;

    //  POJOs / FX
    //Line line;    
    //Text txtAnswer;
    
    public GeometricDist_Calc_PDFView(ProbCalc_Dashboard probCalc_Dashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(probCalc_Dashboard, placeHoriz, placeVert,
                        withThisWidth, withThisHeight); 
        if (printTheStuff) {
            System.out.println("\n60 *** GeometricDist_Calc_PDFView, Constructing");
        }               
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        initializing = true;

        allTheSTFs = new ArrayList();

        geometricDist_Calc_DialogView = probCalc_Dashboard.get_Geometric_DialogView();
        geometric_nToDisplay = geometricDist_Calc_DialogView.getGeometric_nToDisplay(); 
        geometric_P = geometricDist_Calc_DialogView.getGeometric_p();
        geomDistr = new GeometricDistribution(geometric_P);

        // Enough info to get the maxOfYScale
        maxOfYScale = 0.5;
        this.probCalc_Dashboard = probCalc_Dashboard;
        
        respondToChanges();

        graphCanvas = new Canvas(initWidth, initHeight);
        makeTheCheckBoxes(); // Done in super -- only some code utilized here
        makeItHappen();
    }
    
    @Override
    protected void setUpUI() { 
        String title2String;
        okToGraph = geometricDist_Calc_DialogView.getOKToGraph();
        txtTitle1 = new Text(50, 25, " Probability calculations -- Geometric Distribution ");
        title2String = "";
        txtTitle2 = new Text (60, 50, title2String);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    public void prepareTheSupportAxis() {
        xGraphLeft = fromHere;   
        xGraphRight = toThere;
        bigDelta = (xGraphRight - xGraphLeft) / NUMBER_OF_DXs;
        delta = bigDelta;
        xDataMin = xDataMax = xGraphLeft;
        xRange = xGraphRight - xGraphLeft;        
        yRange = yDataMax = maxOfYScale;
        // These constants control the rate of axis scale change when dragging
        deltaX = 0.005 * xRange; deltaY = 0.005 * yRange;   
    }
    
    public void respondToChanges() {
        if (initializing) {
            okToGraph = true;
            init_LeftX = 0.5;
            init_RightX =  12.5; 
            geometric_nToDisplay = 12; geometric_P = 0.5;
            initializeGraphParameters();
            initializing = false;
        } else {
            int maybeNew_N = geometricDist_Calc_DialogView.getGeometric_nToDisplay();
            double maybeNew_P = geometricDist_Calc_DialogView.getGeometric_p();
            boolean n_Changed = (geometric_nToDisplay != maybeNew_N);
            boolean p_Changed = (geometric_P != maybeNew_P);
            
            if (n_Changed || p_Changed) {
                geomDistr.setPSuccess(maybeNew_P);
                geometric_nToDisplay = maybeNew_N; geometric_P = maybeNew_P;
                reInitializeGraphParameters();
            }

            doTheGraph();
            lowerShadeBound = geometricDist_Calc_DialogView.getLowerShadeBound();
            upperShadeBound = geometricDist_Calc_DialogView.getUpperShadeBound();
            probSelection = geometricDist_Calc_DialogView.getProbSelection();
            
            switch (probSelection) {
                
            // ************************  Singles  **********************
                case 1:
                    strDaChoice = geometricDist_Calc_DialogView.getThisSTF(2);
                    daProb = geomDistr.getCDF(Integer.parseInt(strDaChoice) - 1);
                    strAnswer = leftProb + LT + strDaChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4) ;
                    break;

                case 2:
                    strDaChoice = geometricDist_Calc_DialogView.getThisSTF(3);
                    daProb = daProb = geomDistr.getCDF(Integer.parseInt(strDaChoice));
                    strAnswer = leftProb + LE + strDaChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4) ;
                    break;

                case 3:
                    strDaChoice = geometricDist_Calc_DialogView.getThisSTF(4);
                    daProb = geomDistr.getPDF(Integer.parseInt(strDaChoice));
                    strAnswer = leftProb + EQ + strDaChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4) ;
                    break;

                case 4:
                    strDaChoice = geometricDist_Calc_DialogView.getThisSTF(5);
                    daProb = 1.0 - geomDistr.getCDF(Integer.parseInt(strDaChoice) - 1);
                    strAnswer = leftProb + GE + strDaChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4) ;
                    break;

                case 5: 
                    strDaChoice = geometricDist_Calc_DialogView.getThisSTF(6);
                    daProb = 1.0 - geomDistr.getCDF(Integer.parseInt(strDaChoice));
                    strAnswer = leftProb + GT + strDaChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4) ; 
                    break;
                    
                    // ************************  Ranges  **********************

                case 6:
                    strDaLeftChoice = geometricDist_Calc_DialogView.getThisSTF(7);
                    strDaRightChoice = geometricDist_Calc_DialogView.getThisSTF(8);
                    intDaLeftChoice = Integer.parseInt(strDaLeftChoice);
                    intDaRightChoice = Integer.parseInt(strDaRightChoice);
                    daProb = geomDistr.getCDF(intDaRightChoice - 1) - geomDistr.getCDF(intDaLeftChoice);
                    strAnswer = str_Range_Prob_LeftParen + intDaLeftChoice + str_Range_Prob_LTLT + intDaRightChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4);
                    break;

                case 7:
                    strDaLeftChoice = geometricDist_Calc_DialogView.getThisSTF(9);
                    strDaRightChoice = geometricDist_Calc_DialogView.getThisSTF(10);
                    intDaLeftChoice = Integer.parseInt(strDaLeftChoice);
                    intDaRightChoice = Integer.parseInt(strDaRightChoice);
                    daProb = geomDistr.getCDF(intDaRightChoice) - geomDistr.getCDF(intDaLeftChoice);
                    strAnswer = str_Range_Prob_LeftParen + intDaLeftChoice + str_Range_Prob_LTLE + intDaRightChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4);
                    break;

                case 8:
                    strDaLeftChoice = geometricDist_Calc_DialogView.getThisSTF(11);
                    strDaRightChoice = geometricDist_Calc_DialogView.getThisSTF(12);
                    intDaLeftChoice = Integer.parseInt(strDaLeftChoice);
                    intDaRightChoice = Integer.parseInt(strDaRightChoice);
                    daProb = geomDistr.getCDF(intDaRightChoice) - geomDistr.getCDF(intDaLeftChoice);
                    strAnswer = str_Range_Prob_LeftParen + intDaLeftChoice + str_Range_Prob_LELT + intDaRightChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4);
                    break;

                case 9:
                    strDaLeftChoice = geometricDist_Calc_DialogView.getThisSTF(13);
                    strDaRightChoice = geometricDist_Calc_DialogView.getThisSTF(14);
                    intDaLeftChoice = Integer.parseInt(strDaLeftChoice);
                    intDaRightChoice = Integer.parseInt(strDaRightChoice);
                    daProb = geomDistr.getCDF(intDaRightChoice) - geomDistr.getCDF(intDaLeftChoice - 1);
                    strAnswer = str_Range_Prob_LeftParen + intDaLeftChoice + str_Range_Prob_LELE + intDaRightChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4);
                    break;

                default: 
                    String switchFailure = "Switch failure: GeometricDist_Calc_PDFView 197 " + probSelection;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure); 
            }
            txtTitle2.setText(strAnswer);
        }
    }
    
   @Override
    public void initializeGraphParameters() { 
        fromHere = init_LeftX;
        toThere = init_RightX;
        xAxis = new JustAnAxis(fromHere, toThere);
        xAxis.setSide(Side.BOTTOM);       
        prepareTheSupportAxis();
        yAxis = new JustAnAxis(yDataMin, maxOfYScale);
        yAxis.setSide(Side.LEFT);
        yAxis.forceLowScaleEndToBe(0.0);  
        yAxis.setBounds(0.0, maxOfYScale);
        newX_Lower = fromHere; newX_Upper = toThere;
        newY_Lower = yDataMin; newY_Upper = maxOfYScale;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper ); 
    }

    private void reInitializeGraphParameters() { 
        geometric_nToDisplay = geometricDist_Calc_DialogView.getGeometric_nToDisplay();
        geometric_P = geometricDist_Calc_DialogView.getGeometric_p();
        init_LeftX = 0.5;
        init_RightX = geometric_nToDisplay + 0.5;
        maxOfYScale = 1.15 * geomDistr.getPDF(1);
        yAxis.setBounds(0.0, maxOfYScale);
        fromHere = init_LeftX;
        toThere = init_RightX;      
        prepareTheSupportAxis(); 
        yAxis.setBounds(0.0, maxOfYScale);
        newX_Lower = fromHere; newX_Upper = toThere;
        newY_Lower = yDataMin; newY_Upper = maxOfYScale;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper ); 
        doTheGraph();
    }    
    
    @Override
    public void doTheGraph() {   
        okToGraph = geometricDist_Calc_DialogView.getOKToGraph();
        
        dTG_Discrete ();
        
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        boolean probabilitiesDesired = checkBoxSettings[0];
        boolean quartilesDesired = checkBoxSettings[1];
        boolean momentsDesired = checkBoxSettings[2];
        
        //  Start point for graph
        dTG_xx0 = xGraphLeft; 
        geomStart = (int) Math.floor(xGraphLeft);
        
        if (geomStart < 1) { geomStart = 1;}
        
        geomStop = (int) Math.floor(xGraphRight + 1.0);
        dTG_yy0 = geomDistr.getPDF(geomStart);
        
        if (!okToGraph) { dTG_yy1 = 0.0; } 
        
        maxGeometricProbThisTime = 0.0;
        
        for (int x = geomStart; x <= geomStop; x++) {

            geompdf = geomDistr.getPDF(x);
            geomcdf = geomDistr.getCDF(x);
            
            if (geompdf > maxGeometricProbThisTime) {
                maxGeometricProbThisTime = geompdf;
            }

            dTG_xAsDouble = x;

            if (!okToGraph) { geompdf = 0.0; }

            if ((lowerShadeBound <= x) && (x <= upperShadeBound)) {
                
                for (double xToShade = dTG_xAsDouble - 0.5; xToShade < dTG_xAsDouble + 0.5; xToShade = xToShade + deltaX / 10.0) {
                    double qxStart = xAxis.getDisplayPosition(xToShade); 
                    double qyStart = yAxis.getDisplayPosition(0.0); 
                    double qxStop = xAxis.getDisplayPosition(xToShade);
                    double qyStop = yAxis.getDisplayPosition(geompdf);          
                    gc.setLineWidth(2);
                    gc.setStroke(Color.AQUAMARINE);
                    gc.strokeLine(qxStart, qyStart, qxStop, qyStop);                
                }
            }
            //                 Lower left ,            upper right
            daPDF = geompdf;
            makeARectangle(dTG_xAsDouble - 0.5, 0.0, dTG_xAsDouble + 0.5, geompdf);
        }   
        
        maxOfYScale = 1.1 * maxGeometricProbThisTime;
        prepareTheSupportAxis();
        
        if (probabilitiesDesired) { printTheProbs(); }
        if (quartilesDesired) { printTheQuartiles(); }
        if (momentsDesired) {printTheMoments(); }
        
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
    
    private void printTheProbs() {
        double xPosition_ithBar, yPosition_ithBar, prob_ithBar;
        String strProb;
        
        gc.setFill(Color.BLUE);
        
        for (int ithBar = geomStart; ithBar <= geomStop; ithBar++) {
            prob_ithBar = geomDistr.getPDF(ithBar);
            xPosition_ithBar = xAxis.getDisplayPosition(ithBar) - 12;
            yPosition_ithBar = yAxis.getDisplayPosition(prob_ithBar) - 14;   
            strProb = StringUtilities.roundDoubleToNDigitString(prob_ithBar, 3);
            
            if (prob_ithBar > .000005) {
                gc.fillText(strProb, xPosition_ithBar - 6, yPosition_ithBar);
            }
        }
    }
    
    private void printTheQuartiles() {
        double xPositionQ1, xPositionQ2, xPositionQ3;
        double yPositionQ1, yPositionQ2, yPositionQ3;
        String prtQ1, prtQ2, prtQ3;
        q1 = geomDistr.getPercentile(0.25);
        q2 = geomDistr.getPercentile(0.50);
        q3 = geomDistr.getPercentile(0.75);         
        xPositionQ1 = xAxis.getDisplayPosition(q1) - 16;
        xPositionQ2 = xAxis.getDisplayPosition(q2) - 16;
        xPositionQ3 = xAxis.getDisplayPosition(q3) - 16;
        
        yPositionQ1 = yAxis.getDisplayPosition(geomDistr.getPDF(q1)) - 64;
        yPositionQ2 = yAxis.getDisplayPosition(geomDistr.getPDF(q2)) - 48;
        yPositionQ3 = yAxis.getDisplayPosition(geomDistr.getPDF(q3)) - 32;
        
        gc.setFill(Color.RED);
        prtQ1 = "Q1 = " +  Integer.toString(q1);
        prtQ2 = "Q2 = " +  Integer.toString(q2);
        prtQ3 = "Q3 = " +  Integer.toString(q3);
        
        gc.fillText(prtQ1, xPositionQ1 - 6, yPositionQ1);
        gc.fillText(prtQ2, xPositionQ2 - 6, yPositionQ2);
        gc.fillText(prtQ3, xPositionQ3 - 6, yPositionQ3);
    }
    
private void printTheMoments() {
        double xMomentAbove;
        xMomentAbove = 0;
        double belowTheMaxTrigger = geomDistr.getPDF(0) - .05;

        // Find the first value greater than trigger
        
        for (int ithHigh = 0; ithHigh < geomStop; ithHigh++) {
            
            if (geomDistr.getPDF(ithHigh) > belowTheMaxTrigger) {
                xMomentAbove = ithHigh + 4;
            }
        }

        if (!geometricDist_Calc_DialogView.getThisSTF(0).isEmpty()) {   // i.e. not initializing  
            
            double xMomentPosition, xPStart, yPStart,
                                    xMuStart, yMuStart, 
                                    xSigmaStart, ySigmaStart;
            gc.setFill(Color.BLACK);
            xMomentPosition = xMomentAbove;
            
            xPStart = xAxis.getDisplayPosition(xMomentPosition);
            yPStart = yAxis.getDisplayPosition(0.75 * maxOfYScale);
            String pString = "p = " + StringUtilities.roundDoubleToNDigitString(geometric_P, 4);
            gc.fillText(pString, xPStart, yPStart);   

            xMuStart = xAxis.getDisplayPosition(xMomentPosition);
            yMuStart = yAxis.getDisplayPosition(0.70 * maxOfYScale); 
            double daMeanGeom = 1.0 / geometric_P;
            String muString = "\u03BC = " + StringUtilities.roundDoubleToNDigitString(daMeanGeom, 4) ;
            gc.fillText(muString, xMuStart, yMuStart);
            
            xSigmaStart = xMuStart;
            ySigmaStart = yAxis.getDisplayPosition(0.65 * maxOfYScale);
            double daSigmaGeom = Math.sqrt(1.0 - geometric_P) / geometric_P;
            String sigmaString = "\u03C3 = " + StringUtilities.roundDoubleToNDigitString(daSigmaGeom, 4);
            gc.fillText(muString, xMuStart, yMuStart);
            gc.fillText(sigmaString, xSigmaStart, ySigmaStart); 
        }
    }   

    public void setInitializing(boolean toThis) { initializing = toThis; }
}


