/**************************************************
 *             BinomialDist_Calc_PDFView          *
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
//import javafx.scene.layout.AnchorPane;  //  Need for Static AnchorPane
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import probabilityDistributions.BinomialDistribution;
import utilityClasses.*;

public class BinomialDist_Calc_PDFView extends Distributions_Calc_PDFView {
    
    // POJOs
    boolean printTheStuff = true;
        
    int binomial_N, lowerShadeBound, upperShadeBound, probSelection;
    int intDaLeftChoice, intDaRightChoice;

    //     ********  Left and Right base X positions ********
    double init_LeftX, init_RightX, binomial_P, binompdf,
            maxBinomialProbThisTime, daProb; 
    
    String returnStatus;

    // My classes  
    BinomialDist_Calc_DialogView binomialDist_Calc_DialogView;
    BinomialDistribution binomDistr;

    //  POJOs / FX
    
    public BinomialDist_Calc_PDFView(ProbCalc_Dashboard probCalc_Dashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
    super(probCalc_Dashboard, placeHoriz, placeVert,
                        withThisWidth, withThisHeight);
        if (printTheStuff) {
            System.out.println("\n53 *** BinomialDist_Calc_PDFView, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        initializing = true;

        binomialDist_Calc_DialogView = probCalc_Dashboard.get_Binomial_DialogView();
        binomial_N = binomialDist_Calc_DialogView.getBinomial_n(); 
        binomial_P = binomialDist_Calc_DialogView.getBinomial_p();
        binomDistr = new BinomialDistribution(binomial_N, binomial_P);
        
        // Enough info to get the maxOfYScale
        maxOfYScale = 0.5;
        this.probCalc_Dashboard = probCalc_Dashboard;
        
        if (respondToChanges().equals("OK")) {
            graphCanvas = new Canvas(initWidth, initHeight);
            makeTheCheckBoxes(); // Done in super -- only some code utilized here
            makeItHappen();
        } else {
            //  No op?
        }
    }
    
    @Override
    protected void setUpUI() { 
        okToGraph = binomialDist_Calc_DialogView.getOKToGraph();
        txtTitle1 = new Text(50, 25, " Probability calculations -- Binomial Distribution ");
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
    
    public String respondToChanges() {
        // System.out.println("93 BinomDistCalcPDF, respondToChanges()");
        returnStatus = "OK";
        
        if (initializing) {
            okToGraph = true;
            init_LeftX = -0.5;
            init_RightX =  12.5; 
            binomial_N = 12; binomial_P = 0.5;
            initializeGraphParameters();
            initializing = false;
        } else {
            int maybeNew_N = binomialDist_Calc_DialogView.getBinomial_n();
            double maybeNew_P = binomialDist_Calc_DialogView.getBinomial_p();
            boolean n_Changed = (binomial_N != maybeNew_N);
            boolean p_Changed = (binomial_P != maybeNew_P);
            
            if (n_Changed || p_Changed) {
                binomDistr.setBinomial_n_and_p(maybeNew_N, maybeNew_P);
                binomial_N = maybeNew_N; binomial_P = maybeNew_P;
                reInitializeGraphParameters();
            }

            doTheGraph();
            lowerShadeBound = binomialDist_Calc_DialogView.getLowerShadeBound();
            upperShadeBound = binomialDist_Calc_DialogView.getUpperShadeBound();
            probSelection = binomialDist_Calc_DialogView.getProbSelection();
            switch (probSelection) {
                
            // ************************  Singles  **********************
                case 1:
                    strDaChoice = binomialDist_Calc_DialogView.getThisSTF(2);
                    daProb = binomDistr.getCDF(Integer.parseInt(strDaChoice) - 1);
                    strAnswer = leftProb + LT + strDaChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4) ;
                    break;

                case 2:
                    strDaChoice = binomialDist_Calc_DialogView.getThisSTF(3);
                    daProb = daProb = binomDistr.getCDF(Integer.parseInt(strDaChoice));
                    strAnswer = leftProb + LE + strDaChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4) ;
                    break;

                case 3:
                    strDaChoice = binomialDist_Calc_DialogView.getThisSTF(4);
                    daProb = binomDistr.getPDF(Integer.parseInt(strDaChoice));
                    strAnswer = leftProb + EQ + strDaChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4) ;
                    break;

                case 4:
                    strDaChoice = binomialDist_Calc_DialogView.getThisSTF(5);
                    daProb = 1.0 - binomDistr.getCDF(Integer.parseInt(strDaChoice) - 1);
                    strAnswer = leftProb + GE + strDaChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4) ;
                    break;

                case 5: 
                    strDaChoice = binomialDist_Calc_DialogView.getThisSTF(6);
                    daProb = 1.0 - binomDistr.getCDF(Integer.parseInt(strDaChoice));
                    strAnswer = leftProb + GT + strDaChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4) ; 
                    break;
                    
                    // ************************  Ranges  **********************

                case 6:
                    strDaLeftChoice = binomialDist_Calc_DialogView.getThisSTF(7);
                    strDaRightChoice = binomialDist_Calc_DialogView.getThisSTF(8);
                    intDaLeftChoice = Integer.parseInt(strDaLeftChoice);
                    intDaRightChoice = Integer.parseInt(strDaRightChoice);
                    daProb = binomDistr.getCDF(intDaRightChoice - 1) - binomDistr.getCDF(intDaLeftChoice);
                    strAnswer = str_Range_Prob_LeftParen + intDaLeftChoice + str_Range_Prob_LTLT + intDaRightChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4);
                    break;

                case 7:
                    strDaLeftChoice = binomialDist_Calc_DialogView.getThisSTF(9);
                    strDaRightChoice = binomialDist_Calc_DialogView.getThisSTF(10);
                    intDaLeftChoice = Integer.parseInt(strDaLeftChoice);
                    intDaRightChoice = Integer.parseInt(strDaRightChoice);
                    daProb = binomDistr.getCDF(intDaRightChoice) - binomDistr.getCDF(intDaLeftChoice);
                    strAnswer = str_Range_Prob_LeftParen + intDaLeftChoice + str_Range_Prob_LTLE + intDaRightChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4);
                    break;

                case 8:
                    strDaLeftChoice = binomialDist_Calc_DialogView.getThisSTF(11);
                    strDaRightChoice = binomialDist_Calc_DialogView.getThisSTF(12);
                    intDaLeftChoice = Integer.parseInt(strDaLeftChoice);
                    intDaRightChoice = Integer.parseInt(strDaRightChoice);
                    daProb = binomDistr.getCDF(intDaRightChoice - 1) - binomDistr.getCDF(intDaLeftChoice - 1);
                    strAnswer = str_Range_Prob_LeftParen + intDaLeftChoice + str_Range_Prob_LELT + intDaRightChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4);
                    break;

                case 9:
                    strDaLeftChoice = binomialDist_Calc_DialogView.getThisSTF(13);
                    strDaRightChoice = binomialDist_Calc_DialogView.getThisSTF(14);
                    intDaLeftChoice = Integer.parseInt(strDaLeftChoice);
                    intDaRightChoice = Integer.parseInt(strDaRightChoice);
                    daProb = binomDistr.getCDF(intDaRightChoice) - binomDistr.getCDF(intDaLeftChoice);
                    strAnswer = str_Range_Prob_LeftParen + intDaLeftChoice + str_Range_Prob_LELE + intDaRightChoice + rightProb + StringUtilities.roundDoubleToNDigitString(daProb, 4);
                    break;

                default: 
                    String switchFailure = "Switch failure: BinomialDist_Calc_PDFView 191" + String.valueOf(probSelection);
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);
            }
            txtTitle2.setText(strAnswer);
        }
        return returnStatus;
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
        binomial_N = binomialDist_Calc_DialogView.getBinomial_n();
        binomial_P = binomialDist_Calc_DialogView.getBinomial_p();
        init_LeftX = -0.5;
        init_RightX = binomial_N + 0.5;
        maxOfYScale = 1.2 * binomDistr.getPDF((int)Math.floor(binomial_N * binomial_P));
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
        okToGraph = binomialDist_Calc_DialogView.getOKToGraph();   
        
        dTG_Discrete();
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        boolean probabilitiesDesired = checkBoxSettings[0];
        boolean quartilesDesired = checkBoxSettings[1];
        boolean momentsDesired = checkBoxSettings[2];
        
        //  Start point for graph
        dTG_xx0 = xGraphLeft; 
        int binomStart = (int) Math.floor(xGraphLeft);
        
        if (binomStart < 0) { binomStart = 0;}
        
        int binomStop = (int) Math.floor(xGraphRight + 1.0);
        dTG_yy0 = binomDistr.getPDF(binomStart);
        
        if (!okToGraph) { dTG_yy1 = 0.0; } 
        
        maxBinomialProbThisTime = 0.0;
        
        for (int x = binomStart; x <= binomStop; x++) {
            binompdf = binomDistr.getPDF(x);
            
            if (binompdf > maxBinomialProbThisTime) {
                maxBinomialProbThisTime = binompdf;
            }

            dTG_xAsDouble = x;

            if (!okToGraph) { binompdf = 0.0; }

            if ((lowerShadeBound <= x) && (x <= upperShadeBound)) {
                
                for (double xToShade = dTG_xAsDouble - 0.5; xToShade < dTG_xAsDouble + 0.5; xToShade = xToShade + deltaX / 10.0) {
                    double qxStart = xAxis.getDisplayPosition(xToShade); 
                    double qyStart = yAxis.getDisplayPosition(0.0); 
                    double qxStop = xAxis.getDisplayPosition(xToShade);
                    double qyStop = yAxis.getDisplayPosition(binompdf);          
                    gc.setLineWidth(2);
                    gc.setStroke(Color.AQUAMARINE);
                    gc.strokeLine(qxStart, qyStart, qxStop, qyStop);                
                }
            }

            daPDF = binompdf;
            //                 Lower left ,            upper right
            makeARectangle(dTG_xAsDouble - 0.5, 0.0, dTG_xAsDouble + 0.5, daPDF);
        }   
        
        maxOfYScale = 1.15 * maxBinomialProbThisTime;
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
        
        for (int ithBar = 0; ithBar <= binomial_N; ithBar++) {
            prob_ithBar = binomDistr.getPDF(ithBar);
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
        q1 = binomDistr.getPercentile(0.25);
        q2 = binomDistr.getPercentile(0.50);
        q3 = binomDistr.getPercentile(0.75); 
        xPositionQ1 = xAxis.getDisplayPosition(q1) - 16;
        xPositionQ2 = xAxis.getDisplayPosition(q2) - 16;
        xPositionQ3 = xAxis.getDisplayPosition(q3) - 16;
        
        yPositionQ1 = yAxis.getDisplayPosition(binomDistr.getPDF(q1)) - 32;
        yPositionQ2 = yAxis.getDisplayPosition(binomDistr.getPDF(q2)) - 32;
        yPositionQ3 = yAxis.getDisplayPosition(binomDistr.getPDF(q3)) - 32;
        
        gc.setFill(Color.RED);
        prtQ1 = "Q1 = " +  Integer.toString(q1);
        prtQ2 = "Q2 = " +  Integer.toString(q2);
        prtQ3 = "Q3 = " +  Integer.toString(q3);
        
        gc.fillText(prtQ1, xPositionQ1 - 6, yPositionQ1);
        gc.fillText(prtQ2, xPositionQ2 - 6, yPositionQ2);
        gc.fillText(prtQ3, xPositionQ3 - 6, yPositionQ3);        
    }
    
    private void printTheMoments() {
        //System.out.println("361 BinomCalcPDF, printTheMoments()");
        double xMomentBelow, xMomentAbove;
        xMomentBelow = 0.0; xMomentAbove = binomial_N;
        double belowTheMaxTrigger = maxBinomialProbThisTime - .05;
        
        double muValue = binomial_N * binomial_P;
        double sigmaValue = Math.sqrt(binomial_N * binomial_P * (1.0 - binomial_P));
        
        // Find the last value less than trigger        
        for (int ithLow = 0; ithLow < binomial_N; ithLow++) {
            if ((binomDistr.getPDF(ithLow) < belowTheMaxTrigger) && (binomDistr.getPDF(ithLow) < binomDistr.getPDF(ithLow + 1))) {
                xMomentBelow = ithLow;
            }
        }

        // Find the first value greater than trigger
        for (int ithHigh = binomial_N - 1; ithHigh > 0; ithHigh--) {
            if ((binomDistr.getPDF(ithHigh) < belowTheMaxTrigger) && (binomDistr.getPDF(ithHigh) > binomDistr.getPDF(ithHigh + 1))) {
                xMomentAbove = ithHigh;
            }
        }

        if (!binomialDist_Calc_DialogView.getThisSTF(0).isEmpty()) {   // i.e. not initializing  
            
            double xMomentPosition, xNStart, yNStart,
                                    xPStart, yPStart, 
                                    xMuStart, yMuStart, 
                                    xSigmaStart, ySigmaStart;
            gc.setFill(Color.BLACK);
            
            if (binomial_P < 0.5) {
                xMomentPosition = muValue + 2.0 * sigmaValue;
            }
            else {
                xMomentPosition = muValue - 6.0 * sigmaValue;
            }
            
            xNStart = xAxis.getDisplayPosition(xMomentPosition);
            yNStart = yAxis.getDisplayPosition(0.80 * maxOfYScale);
            String nString = "n = " + String.valueOf(binomial_N);
            gc.fillText(nString, xNStart, yNStart);
            
            xPStart = xAxis.getDisplayPosition(xMomentPosition);
            yPStart = yAxis.getDisplayPosition(0.75 * maxOfYScale);
            String pString = "p = " + StringUtilities.roundDoubleToNDigitString(binomial_P, 4);
            gc.fillText(pString, xPStart, yPStart);           
            
            xMuStart = xAxis.getDisplayPosition(xMomentPosition);
            yMuStart = yAxis.getDisplayPosition(0.70 * maxOfYScale); 

            String muString = "\u03BC = " + StringUtilities.roundDoubleToNDigitString(muValue, 4) ;
            gc.fillText(muString, xMuStart, yMuStart);
            
            xSigmaStart = xMuStart;
            ySigmaStart = yAxis.getDisplayPosition(0.65 * maxOfYScale);

            String sigmaString = "\u03C3 = " + StringUtilities.roundDoubleToNDigitString(sigmaValue, 4);
            gc.fillText(muString, xMuStart, yMuStart);
            gc.fillText(sigmaString, xSigmaStart, ySigmaStart); 
        }
    }   
    
    public void setInitializing(boolean toThis) { initializing = toThis; }
}

