/**************************************************
 *             LinReg_Power_PdfView               *
 *                   06/01/26                     *
 *                    12:00                       *
 *************************************************/
package power_LinReg;

import genericClasses.Point_2D;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import noncentrals.JDistr_Noncentrals.*;
import superClasses.*;
import genericClasses.*;
import javafx.scene.control.CheckBox;
import utilityClasses.*;

public class LinReg_Power_PdfView extends BivariateScale_W_CheckBoxes_View { 
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    boolean[] hBoxCheckBoxSettings;

    double yMin, yMax, scaleDelta, nullBeta, altBeta,
           rawCritValLT, rawCritValGT, yStart_Null, yStop_Null, yStart_Alt, 
           yStop_Alt, stErrNullBeta, stErrAltBeta, effectSize, lowerSliver, 
           upperSliver, densityFactor;
    
    double rejArrStart, rejArrStop, rejFailureArrStart, 
           rejFailureArrStop, nullArrowHeight, altScaleHeight,
            nullTextHeight, altScaleStart, altScaleStop, yNullDistDescr,
           rejectionTextStart, nonRejectionTextStart, df;
    
    double leftRejArrStart, leftRejArrStop,
           rightRejArrStart, rightRejArrStop;
    
    double xNotEqualNoRejectRegionStart, yNotEqualNoRejectRegionStart;
    
    double xCritValDescrStart, yCritValDescrStart, xAltDistDescr, yAltDistDescr;
    double leftRejectionTextStart, rightRejectionTextStart;
    
    double tempT_Null, tempT_Alt;
    
    final double MIDDLE_T = 0.999;
    final double SQRT2_Over2 = Math.sqrt(2.0) / 2.0;
    
    String rejectionCriterion;
    
    String strSingleCritValueDescr = "Critical value = ";
    String strTwoCritValuesDescr = "Critical values = ";
    
    final String strRejectRegion = "Reject";
    final String strNoRejectRegion = "Fail to \nreject";   
    final String strGoodCall = "  Good  \n  Call!";
    final String strPrTypeII = "Oops! Type \n II error";
    final String strAltSampDistDescr = "The alternate\ndistribution";
    final String strNullSampDistDescr = "  The null\ndistribution"; 
    String[] strHBoxCheckBoxDescrs;

    // My classes
    LinReg_Power_Model linReg_Power_Model;
    Point_2D nonRejectionRegion;
    TDistribution tDistNull, tDistAlt;
    
    //  FX
    Pane theContainingPane;
    AnchorPane checkBoxRow;
    CheckBox[] chBoxHBox;

    public LinReg_Power_PdfView(LinReg_Power_Model linReg_Power_Model, 
                             LinReg_Power_Dashboard single_Z_Dash,
                             double placeHoriz, double placeVert,
                             double withThisWidth, double withThisHeight) {

        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff) {
            System.out.println("83 *** LinReg_Power_PdfView, Constructing");
        }
        this.linReg_Power_Model = linReg_Power_Model;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        
        df = linReg_Power_Model.get_df();
        tDistNull = new TDistribution(df);
        tDistAlt = new TDistribution(df);
        
        linReg_Power_Model.restoreNullValues();
        nullBeta = linReg_Power_Model.getNullBeta();
        stErrNullBeta = linReg_Power_Model.getStErr_Beta();   
        stErrAltBeta = stErrNullBeta;
        // Control the height of the normal curves
        densityFactor = 0.75;
        alpha = linReg_Power_Model.getAlpha();
        effectSize = linReg_Power_Model.getEffectSize();
        lowerSliver =  (1.0 - MIDDLE_T) / 2.0;
        upperSliver = 1 - lowerSliver;     
        nullBeta = linReg_Power_Model.getNullBeta();
        rejectionCriterion = linReg_Power_Model.getRejectionCriterion();
        
        switch (rejectionCriterion) {
            case "LessThan":
                altBeta = nullBeta - effectSize;
                break;
                
            case "NotEqual":
            case "GreaterThan":
                altBeta = nullBeta + effectSize;
                break;
                
            default:
                String switchFailure = "Switch failure: LinReg_Power_PdfView 119 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }        
  
        nonRejectionRegion = new Point_2D(linReg_Power_Model.getNonRejectionRegion().getFirstValue(),
                                                linReg_Power_Model.getNonRejectionRegion().getSecondValue());


        // These checkboxes control what is printed in the alternate density
        nCheckBoxes = 3;
        strHBoxCheckBoxDescrs = new String[nCheckBoxes];
        strHBoxCheckBoxDescrs[0] = " Power ";
        strHBoxCheckBoxDescrs[1] = " Type II ";        
        strHBoxCheckBoxDescrs[2] = " Annotation ";      
        checkBoxHeight = 350.0;
        graphCanvas = new Canvas(initWidth, initHeight);
        makeTheCheckBoxes();
        makeItHappen(); 
    }  
    
    public void makeTheCheckBoxes() {
        hBoxCheckBoxSettings = new boolean[nCheckBoxes];
        
        for (int ithSetting = 0; ithSetting < nCheckBoxes; ithSetting++) {
            hBoxCheckBoxSettings[ithSetting] =  false;
        } 
        
        checkBoxRow = new AnchorPane();
        checkBoxRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        chBoxHBox = new CheckBox[nCheckBoxes];

        for (int i = 0; i < nCheckBoxes; i++) {
            chBoxHBox[i] = new CheckBox(strHBoxCheckBoxDescrs[i]);            
            chBoxHBox[i].setMaxWidth(Double.MAX_VALUE);
            chBoxHBox[i].setId(strHBoxCheckBoxDescrs[i]);
            chBoxHBox[i].setSelected(hBoxCheckBoxSettings[i]);

            chBoxHBox[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            
            if (chBoxHBox[i].isSelected()) 
                chBoxHBox[i].setTextFill(Color.GREEN);
            else
                chBoxHBox[i].setTextFill(Color.RED);
            
            chBoxHBox[i].setOnAction(e->{
                CheckBox tb = ((CheckBox) e.getTarget());
                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                // Reset selected color
                if (checkValue == true)
                    tb.setTextFill(Color.GREEN);
                else 
                    tb.setTextFill(Color.RED);
                
                switch (daID) {    
                    case " Power ":
                        hBoxCheckBoxSettings[0] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    case " Type II ":  
                        hBoxCheckBoxSettings[1] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    case " Annotation ":  
                        hBoxCheckBoxSettings[2] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    default:
                        String switchFailure = "Switch failure: LinReg_Power_PdfView 197 " + daID;
                        MyAlerts.showUnexpectedErrorAlert(switchFailure);
                }

            }); //  end setOnAction
        }  
        checkBoxRow.getChildren().addAll(chBoxHBox);
    }
    
    public void makeItHappen() { 
        theContainingPane = new Pane();
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Courier New", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
        setUpDecisionRegions();
        initializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();
        setHandlers();
        doTheGraph();           
        theContainingPane = dragableAnchorPane.getTheContainingPane();     
    }
    
    public void setUpDecisionRegions() {
        // Do I need this if I'm really just getting these from the controller?
        switch(rejectionCriterion) {
            case "LessThan":    // Alternative is less than
                rawCritValLT =  nonRejectionRegion.getFirstValue();
                strSingleCritValueDescr = "Critical value = " + String.format("%6.3f", rawCritValLT);
                break;
            
            case "NotEqual":    // Alternative is not equal to  
                rawCritValLT =  nonRejectionRegion.getFirstValue();
                rawCritValGT =  nonRejectionRegion.getSecondValue(); 
                strTwoCritValuesDescr = "Critical values = " + String.format("%6.3f and %6.3f", rawCritValLT, rawCritValGT);
                break;                
            
            case "GreaterThan":    // Alternative is greater than
                rawCritValGT =  nonRejectionRegion.getSecondValue(); 
                strSingleCritValueDescr = "Critical value = " + String.format("%6.3f", rawCritValGT);
                break;  
                
            default:
                String switchFailure = "Switch failure: LinReg_Power_PdfView 241 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);    
        } 
    } 

    
    @Override
    protected void setUpUI() { 
        String title1String = " Power, Simple Regression";
        String title2String = linReg_Power_Model.getPrintedNullHypothesis() + " vs. " + linReg_Power_Model.getPrintedAltHypothesis();
        txtTitle1 = new Text(50, 25, title1String);
        
        txtTitle2 = new Text (60, 45, title2String);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    public void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
      
        for (int iChex = 0; iChex < nCheckBoxes; iChex++) {            
            chBoxHBox[iChex].translateXProperty()
                                 .bind(graphCanvas.widthProperty()
                                 .divide(250.0)
                                 .multiply(5 * iChex)
                                 .subtract(50.0));
        }
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(checkBoxRow, txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
  
      public void initializeGraphParameters() {
        //  Get ranges of  t
        double lowTNull = nullBeta - 3.5 * stErrNullBeta;
        double hiTNull = nullBeta + 3.5 * stErrNullBeta; 
        double lowTAlt = altBeta - 3.5 * stErrNullBeta;
        double hiTAlt = altBeta + 3.5 * stErrNullBeta;
        fromHere = Math.min(lowTNull, lowTAlt);
        toThere = Math.max(hiTNull, hiTAlt);

        xAxis = new JustAnAxis(fromHere, toThere);
        xAxis.setSide(Side.BOTTOM);       

        prepareTheDensityAxis();
         
        yAxis = new JustAnAxis(yMin, yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.forceLowScaleEndToBe(0.0);  
        yAxis.setBounds(0.0, yMax);
        yAxis.setVisible(false);
        newX_Lower = fromHere; newX_Upper = toThere;
        newY_Lower = yMin; newY_Upper = yMax;
        
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );            
    }

    private void prepareTheDensityAxis() {
        xGraphLeft = fromHere;  
        xGraphRight = toThere;
        scaleDelta = (xGraphRight - xGraphLeft) / NUMBER_OF_DXs;
        xRange = xGraphRight - xGraphLeft;     
        
        yMax = 1.0;
        yRange = yMax;
        // These constants control the rate of axis scale change when dragging
        deltaX = 0.005 * xRange; deltaY = 0.005 * yRange;   
    }
    
    public void doTheGraph() {      
        double xx0_Null, yy0_Null, xx1_Null, density_Null;
        double xx0_Alt, yy0_Alt, xx1_Alt, density_Alt; 

        //String tempString;
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
            AnchorPane.setLeftAnchor(chBoxHBox[chex], (chex) * tempWidth / 5.0);
        }
        
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        boolean power_Desired = hBoxCheckBoxSettings[0];
        boolean typeII_Desired = hBoxCheckBoxSettings[1];
        boolean annotation_Desired = hBoxCheckBoxSettings[2];
        
        // Vertical line height for critical value
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);  
        yStart = yAxis.getDisplayPosition(0.0);
        double tentative_yStop = 0.925 * yMax;
        
        if (tentative_yStop > .925) { tentative_yStop = .925; }
        
        yStop = yAxis.getDisplayPosition(tentative_yStop);

        switch(rejectionCriterion) {
            
            case "LessThan":    // Alternative is less than
                xStart = xAxis.getDisplayPosition(rawCritValLT); 
                xStop = xAxis.getDisplayPosition(rawCritValLT);               
                gc.strokeLine(xStart, yStart, xStop, yStop);
                break;
            
            case "NotEqual":    // Alternative is not equal to
                yStop = yAxis.getDisplayPosition(0.5);
                xStart = xAxis.getDisplayPosition(rawCritValLT);   
                xStop = xAxis.getDisplayPosition(rawCritValLT);
                gc.strokeLine(xStart, yStart, xStop, yStop);
                xStart = xAxis.getDisplayPosition(rawCritValGT); 
                xStop = xAxis.getDisplayPosition(rawCritValGT);
                gc.strokeLine(xStart, yStart, xStop, yStop);
                break;                
            
            case "GreaterThan":    // Alternative is greater than
                xStart = xAxis.getDisplayPosition(rawCritValGT); 
                xStop = xAxis.getDisplayPosition(rawCritValGT);
                gc.strokeLine(xStart, yStart, xStop, yStop);

                break;  
                
            default:
                String switchFailure = "Switch failure: LinReg_Power_PdfView 411 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }   //  end switch

        /************************************************************
        *****                Do the Null graph         **************
        ************************************************************/
        xx0_Null = xGraphLeft; 
        //yy0_Null = Normal.density(xx0_Null, nullBeta, stErrNullBeta, false);
        tempT_Null = (xx0_Null - nullBeta) / stErrNullBeta;
        yy0_Null = densityFactor * tDistNull.density(xx0_Null, false); 
        for (double x = xGraphLeft; x <= xGraphRight; x += scaleDelta) {
            xx1_Null = x;
            // density_Null = densityFactor * Normal.density(xx1_Null, nullParam, stErrNullBeta, false); 
            tempT_Null = (xx1_Null - nullBeta) / stErrNullBeta;
            density_Null = densityFactor * tDistNull.density(tempT_Null, false); 
            xStart = xAxis.getDisplayPosition(xx0_Null); 
            yStart = yAxis.getDisplayPosition(yy0_Null); 
            xStop = xAxis.getDisplayPosition(xx1_Null);
            yStop = yAxis.getDisplayPosition(density_Null);
            
            gc.setLineWidth(1);
            gc.setStroke(Color.BLACK);
            gc.strokeLine(xStart, yStart, xStop, yStop);            

            xx0_Null = xx1_Null; yy0_Null = density_Null;   //  Next start point for line segment
        }   
         
        /************************************************************
        *          Do the Alt graph is less or greater than         *
        ************************************************************/       
        if (!rejectionCriterion.equals("NotEqual")) {
            xx0_Alt = xGraphLeft; 
            //yy0_Alt = Normal.density(xx0_Alt, altBeta, stErrNullBeta, false); 
            tempT_Alt = (xx0_Alt - altBeta) / stErrNullBeta;
            yy0_Alt = densityFactor * tDistNull.density(tempT_Alt, false); 
            altScaleHeight = yAxis.getDisplayPosition(0.5);
            altScaleStart = xAxis.getDisplayPosition(nullBeta - 3.5 * stErrNullBeta);
            altScaleStop = xAxis.getDisplayPosition(altBeta + 3.5 * stErrNullBeta);
            gc.strokeLine(altScaleStart, altScaleHeight, altScaleStop, altScaleHeight); 

            for (double x = xGraphLeft; x <= xGraphRight; x += scaleDelta) {
                xx1_Alt = x + altBeta;
                //density_Alt = densityFactor * Normal.density(xx1_Alt, altParam, stErrNullBeta, false);
                tempT_Alt = (xx1_Alt - altBeta) / stErrAltBeta;
                density_Alt = densityFactor * tDistNull.density(tempT_Alt, false); 
                xStart = xAxis.getDisplayPosition(xx0_Alt); 
                yStart = yAxis.getDisplayPosition(yy0_Alt + .5); 
                xStop = xAxis.getDisplayPosition(xx1_Alt);
                yStop = yAxis.getDisplayPosition(density_Alt + .5);

                gc.setLineWidth(1);
                gc.setStroke(Color.BLACK);
                //gc.strokeLine(xStart, yStart, xStop, yStop);

                xx0_Alt = xx1_Alt; yy0_Alt = density_Alt;   //  Next start point for line segment
            } 
        }
        
        /************************************************************
        *****               Do the Shading             **************
        ************************************************************/
        gc.setLineWidth(1);
        xx0_Null = xGraphLeft; 
       
        for (double x = xGraphLeft; x <= xGraphRight; x += scaleDelta) {
            xx1_Null = x;
            tempT_Null = (x - nullBeta) / stErrNullBeta;
            density_Null = densityFactor * tDistNull.density(tempT_Null, false);
            tempT_Alt = (x - altBeta) / stErrAltBeta;
            density_Alt = densityFactor * tDistNull.density(tempT_Alt, false);
            xStart = xAxis.getDisplayPosition(xx1_Null); 
            xStop = xAxis.getDisplayPosition(xx1_Null); 
            yStart_Null = yAxis.getDisplayPosition(0.0); 
            yStop_Null = yAxis.getDisplayPosition(density_Null);            
            yStart_Alt = yAxis.getDisplayPosition(0.0 + 0.5); 
            yStop_Alt = yAxis.getDisplayPosition(density_Alt + 0.5);              
            
            switch(rejectionCriterion) {
                case "LessThan":    // Alternative is less than
                    if (x < rawCritValLT) { 
  
                        if (power_Desired) {
                            gc.setStroke(Color.RED);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null);  
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);
                        }   else {
                            gc.setStroke(Color.AQUAMARINE);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null); 
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);
                        }                        
                    } else {

                        if (typeII_Desired == true) {
                            gc.setStroke(Color.BLUE);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null); 
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);
                        } else {
                            gc.setStroke(Color.AQUAMARINE);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null);
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);
                        }
                    }
                    break;

                case "NotEqual":    // Alternative is not equal to
                    if ((rawCritValLT < x) && (x < rawCritValGT)) { 
                        if (typeII_Desired == true) {
                            gc.setStroke(Color.BLUE);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null);
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);                            
                        } else {
                            gc.setStroke(Color.AQUAMARINE);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null);  
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);                            
                        }                      
                    }
                    else
                    {
                        if (power_Desired) {
                            gc.setStroke(Color.RED);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null);
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);
                        } else {
                            gc.setStroke(Color.AQUAMARINE);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null);  
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);
                        }
                    }
                    break;                

                case "GreaterThan":    // Alternative is greater than
                    if (x < rawCritValGT) { 
                        if (typeII_Desired) {
                            gc.setStroke(Color.BLUE);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null); 
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);
                        } else {
                            gc.setStroke(Color.AQUAMARINE);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null);
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);
                        } 
                    }  else {
                        if (power_Desired) {
                            gc.setStroke(Color.RED);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null);
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);
                        } else {
                            gc.setStroke(Color.AQUAMARINE);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null);
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);
                        }
                    }
                    break;  

                default:
                    String switchFailure = "Switch failure: LinReg_Power_PdfView 567 " + rejectionCriterion;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure); 
                    break;     
            }   //  end switch
        }  //   end loop   
        
        /***********************************************************************
         *                     Arrows and Scribbles                            *                    
         **********************************************************************/
        switch (rejectionCriterion) {
            case "LessThan":
                xCritValDescrStart = xAxis.getDisplayPosition(rawCritValLT - 0.75  * stErrNullBeta);
                break;
                
            case "NotEqual":
                xCritValDescrStart = xAxis.getDisplayPosition(nullBeta - 1.75  * stErrNullBeta);
                break;
                
            case "GreaterThan":
                xCritValDescrStart = xAxis.getDisplayPosition(rawCritValGT - 0.75  * stErrNullBeta);
                break;
                
            default:
                String switchFailure = "Switch failure: LinReg_Power_PdfView 590 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
                break; 
        } 

        if (!rejectionCriterion.equals("NotEqual")) {
            yCritValDescrStart = yAxis.getDisplayPosition(0.90);
            gc.fillText(strSingleCritValueDescr, xCritValDescrStart, yCritValDescrStart);
        } else {
            yCritValDescrStart = yAxis.getDisplayPosition(0.90);
            gc.fillText(strTwoCritValuesDescr, xCritValDescrStart, yCritValDescrStart); 
        }
        
        switch (rejectionCriterion) {
            case "LessThan": 
                xAltDistDescr = xAxis.getDisplayPosition(nullBeta + 1.75  * stErrNullBeta);
                break;
                
            case "NotEqual":
                break;
            
            case "GreaterThan": 
                xAltDistDescr = xAxis.getDisplayPosition(nullBeta - 3.0  * stErrNullBeta);
                break;
                
            default:
                String switchFailure = "Switch failure: LinReg_Power_PdfView 616 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
                break; 
        }
        
        // No alternative sampling distribution is drawn if NotEqual
        if (!rejectionCriterion.equals("NotEqual")) {
            yAltDistDescr = yAxis.getDisplayPosition(0.65);
            gc.fillText(strAltSampDistDescr, xAltDistDescr, yAltDistDescr);
            yNullDistDescr = yAxis.getDisplayPosition(0.15);
            gc.fillText(strNullSampDistDescr, xAltDistDescr, yNullDistDescr);
        }

        switch(rejectionCriterion) {
            case "LessThan":    // Alternative is less than        
                if (annotation_Desired) {
                    gc.setStroke(Color.RED);   
                    nullArrowHeight = yAxis.getDisplayPosition(0.40); 
                    nullTextHeight = yAxis.getDisplayPosition(0.80);
                    rejArrStart = xAxis.getDisplayPosition(rawCritValLT);
                    rejArrStop = xAxis.getDisplayPosition(rawCritValLT - 1.5 * stErrNullBeta);

                    rejFailureArrStart = rejArrStart;
                    rejFailureArrStop = xAxis.getDisplayPosition(rawCritValLT + 1.5 * stErrNullBeta);
                    nonRejectionTextStart = xAxis.getDisplayPosition(stErrNullBeta + 1.5 * stErrNullBeta);
                    rejectionTextStart = xAxis.getDisplayPosition(rawCritValLT - 1.7 * stErrNullBeta);
                    gc.strokeLine(rejArrStart, nullArrowHeight, rejArrStop, nullArrowHeight); 
                    gc.strokeLine(rejFailureArrStart, nullArrowHeight, rejFailureArrStop, nullArrowHeight);

                    nonRejectionTextStart = xAxis.getDisplayPosition(rawCritValLT + 2.0 * stErrNullBeta);
                    
                    drawAnArrow(rawCritValLT, rawCritValLT - 1.5 * stErrNullBeta, 0.40);
                    drawAnArrow(rawCritValLT, rawCritValLT + 1.5 * stErrNullBeta, 0.40);

                    gc.fillText(strRejectRegion, rejectionTextStart, nullArrowHeight);
                    gc.fillText(strNoRejectRegion, nonRejectionTextStart, nullArrowHeight);

                    gc.fillText(strGoodCall, rejectionTextStart, nullTextHeight);
                    gc.fillText(strPrTypeII, nonRejectionTextStart, nullTextHeight);
                }
                break;
                
            case "NotEqual":    // Alternative is not equal to
                if (annotation_Desired ) {
                    gc.setStroke(Color.RED);   
                    nullArrowHeight = yAxis.getDisplayPosition(0.40); 
                    leftRejArrStart = xAxis.getDisplayPosition(rawCritValLT);
                    leftRejArrStop = xAxis.getDisplayPosition(rawCritValLT - 1.5 * stErrNullBeta);
                    rightRejArrStart = xAxis.getDisplayPosition(rawCritValGT);
                    rightRejArrStop = xAxis.getDisplayPosition(rawCritValGT + 1.5 * stErrNullBeta);
                    
                    nonRejectionTextStart = xAxis.getDisplayPosition(nullBeta - 1.7 * stErrNullBeta);
                    gc.strokeLine(leftRejArrStart, nullArrowHeight, leftRejArrStop, nullArrowHeight); 
                    gc.strokeLine(rightRejArrStart, nullArrowHeight, rightRejArrStop, nullArrowHeight);

                    leftRejectionTextStart = xAxis.getDisplayPosition(rawCritValLT - 1.25 * stErrNullBeta);
                    rightRejectionTextStart = xAxis.getDisplayPosition(rawCritValGT + 1.00 * stErrNullBeta);
                    
                    gc.strokeLine(leftRejArrStart, nullArrowHeight, leftRejArrStop, nullArrowHeight); 
                    gc.strokeLine(rejFailureArrStart, nullArrowHeight, rejFailureArrStop, nullArrowHeight);
                    
                    xNotEqualNoRejectRegionStart = xAxis.getDisplayPosition(nullBeta - 0.25 * stErrNullBeta); 
                    yNotEqualNoRejectRegionStart = yAxis.getDisplayPosition(0.45);
                   
                    gc.fillText(strRejectRegion, leftRejectionTextStart, yAxis.getDisplayPosition(0.43));
                    gc.fillText(strRejectRegion, rightRejectionTextStart, yAxis.getDisplayPosition(0.43));                    
                    
                    gc.fillText(strNoRejectRegion, xNotEqualNoRejectRegionStart, yNotEqualNoRejectRegionStart);
                    
                    drawAnArrow(rawCritValLT, rawCritValLT - 1.5 * stErrNullBeta, 0.40);                    
                    drawAnArrow(rawCritValGT, rawCritValGT + 1.5 * stErrNullBeta, 0.40);
                    
                }                
                break;
        
            case "GreaterThan":    // Alternative is greater than
                if (annotation_Desired) {
                    gc.setStroke(Color.RED);   
                    nullArrowHeight = yAxis.getDisplayPosition(0.40); 
                    nullTextHeight = yAxis.getDisplayPosition(0.80);
                    rejArrStart = xAxis.getDisplayPosition(rawCritValGT);
                    rejArrStop = xAxis.getDisplayPosition(rawCritValGT + 1.5 * stErrNullBeta);

                    rejFailureArrStart = rejArrStart;
                    rejFailureArrStop = xAxis.getDisplayPosition(rawCritValGT - 1.5 * stErrNullBeta);
                    nonRejectionTextStart = xAxis.getDisplayPosition(nullBeta - 2.7 * stErrNullBeta);
                    rejectionTextStart = xAxis.getDisplayPosition(rawCritValGT + 1.0 * stErrNullBeta);
                    gc.strokeLine(rejArrStart, nullArrowHeight, rejArrStop, nullArrowHeight); 
                    gc.strokeLine(rejFailureArrStart, nullArrowHeight, rejFailureArrStop, nullArrowHeight);

                    drawAnArrow(rawCritValGT, rawCritValGT + 1.5 * stErrNullBeta, 0.40);
                    drawAnArrow(rawCritValGT, rawCritValGT - 1.5 * stErrNullBeta, 0.40);

                    gc.fillText(strRejectRegion, rejectionTextStart, nullArrowHeight);
                    gc.fillText(strNoRejectRegion, nonRejectionTextStart, nullArrowHeight);

                    gc.fillText(strGoodCall, rejectionTextStart, nullTextHeight);
                    gc.fillText(strPrTypeII, nonRejectionTextStart, nullTextHeight);
                }
                break;
                
            default:
                String switchFailure = "Switch failure: LinReg_Power_PdfView 718 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
                break; 
        }  
    }
    
    private void drawAnArrow (double fromHere, double toThere, double atThisHeight) {
        double verticalArrowPointLength, horizontalArrowPointLength,
               xUpperPointEnd, yUpperPointEnd,xLowerPointEnd,yLowerPointEnd,
               xUPDisplay, yUPDisplay, xLPDisplay, yLPDisplay;
        
        rejArrStart = xAxis.getDisplayPosition(fromHere);
        rejArrStop = xAxis.getDisplayPosition(toThere);
        nullArrowHeight = yAxis.getDisplayPosition(atThisHeight);
        
        verticalArrowPointLength = SQRT2_Over2 * 0.035;
        horizontalArrowPointLength = 0.15 * stErrNullBeta;
        yUpperPointEnd = atThisHeight + verticalArrowPointLength;
        yLowerPointEnd = atThisHeight - verticalArrowPointLength;        

        if (fromHere < toThere) {   //  Pointing to right
            xUpperPointEnd = toThere - horizontalArrowPointLength;
            xLowerPointEnd = toThere - horizontalArrowPointLength;

            xUPDisplay = xAxis.getDisplayPosition(xUpperPointEnd);
            yUPDisplay = yAxis.getDisplayPosition(yUpperPointEnd);
            xLPDisplay = xAxis.getDisplayPosition(xLowerPointEnd);
            yLPDisplay = yAxis.getDisplayPosition(yLowerPointEnd);
            
            gc.strokeLine(rejArrStart, nullArrowHeight, rejArrStop, nullArrowHeight);
            gc.strokeLine(rejArrStop, nullArrowHeight, xUPDisplay, yUPDisplay);
            gc.strokeLine(rejArrStop, nullArrowHeight, xLPDisplay, yLPDisplay);
        }
        else {  //  Pointing to left
            xUpperPointEnd = toThere + horizontalArrowPointLength;
            xLowerPointEnd = toThere + horizontalArrowPointLength;

            xUPDisplay = xAxis.getDisplayPosition(xUpperPointEnd);
            yUPDisplay = yAxis.getDisplayPosition(yUpperPointEnd);
            xLPDisplay = xAxis.getDisplayPosition(xLowerPointEnd);
            yLPDisplay = yAxis.getDisplayPosition(yLowerPointEnd);
            
            gc.strokeLine(rejArrStart, nullArrowHeight, rejArrStop, nullArrowHeight);
            gc.strokeLine(rejArrStop, nullArrowHeight, xUPDisplay, yUPDisplay);
            gc.strokeLine(rejArrStop, nullArrowHeight, xLPDisplay, yLPDisplay);           
        }
    }

   /*******************************************************************
   *   The following functions control whether a test statistic will  *
   *    be plotted and a tail area therefore drawn                    *
   ********************************************************************/
    
    public double[] getInverseMiddle_Z_Area(double middleArea)  {
        double[] middleInterval;
        middleInterval = new double[2];
        middleInterval[0] = Normal.quantile(lowerSliver, nullBeta, stErrNullBeta, true, false);
        middleInterval[1] = Normal.quantile(upperSliver, nullBeta, stErrNullBeta, true, false);
        return middleInterval;
    }
    
    public double[] getInverseMiddle_NonC_Z_Area(double middleArea)  {
        double[] middleInterval;
        middleInterval = new double[2];
        middleInterval[0] = Normal.quantile(lowerSliver, nullBeta, stErrNullBeta, true, false);
        middleInterval[1] = Normal.quantile(upperSliver, nullBeta, stErrNullBeta, true, false);
        return middleInterval;
    }
   
   public Pane getTheContainingPane() {  return theContainingPane; }
}