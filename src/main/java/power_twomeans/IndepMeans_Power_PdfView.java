/**************************************************
 *            IndepMeans_Power_PdfView            *
 *                   05/29/24                     *
 *                    21:00                       *
 *************************************************/
package power_twomeans;

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
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import utilityClasses.*;

public class IndepMeans_Power_PdfView extends BivariateScale_W_CheckBoxes_View
{ 
    boolean[] boolHBoxCheckBoxSettings;
    int n_1, n_2;

    double yMin, yMax, scaleDelta, nullDiffMeans, altMu,
            rawCritValLT, rawCritValGT, yStart_Null,  yStop_Null, yStart_Alt, 
            yStop_Alt, standErrDiffMeans, effectSize, lowerSliver, upperSliver, 
            densityFactor, altDiffMeans, yNullDistDescr,
            
           rejectionArrowStart, rejectionArrowStop, rejectionFailureArrowStart, 
           rejectionFailureArrowStop, nullArrowHeight, altScaleHeight,
           nullTextHeight, altScaleStart, altScaleStop,
           rejectionTextStart, nonRejectionTextStart,
    
           leftRejectionArrowStart, leftRejectionArrowStop,
           rightRejectionArrowStart, rightRejectionArrowStop,
    
           xNotEqualNoRejectRegionStart, yNotEqualNoRejectRegionStart,
    
           xCritValDescrStart, yCritValDescrStart, xAltDistDescr, yAltDistDescr,
           leftRejectionTextStart, rightRejectionTextStart;
    
    final double MIDDLE_Z = 0.999;
    final double SQRT2_OVER2 = Math.sqrt(2.0) / 2.0;
    
    String rejectionCriterion;
    
    String strSingle_CritValueDescr = "Critical value = ";
    String strTwo_CritValuesDescr = "Critical values = ";
    
    final String strRejectRegion = "Reject";
    final String strNoRejectRegion = "Fail to \nreject";   
    final String strGoodCall = "  Good  \n  Call!";
    final String strPrTypeII = "Oops! Type \n II error";
    final String strAltSampDistDescr = "The alternate\ndistribution";
    final String strNullSampDistDescr = "  The null\ndistribution";
    String[] strHBoxCheckBoxDescr;

    // My classes
    IndepMeans_Power_Model indepMeans_Power_Model;  
    
    //  FX
    Point_2D nonRejectionRegion;
    Pane theContainingPane;
    AnchorPane checkBoxRow;
    CheckBox[] chBoxCheckBoxes;
    
    public IndepMeans_Power_PdfView(IndepMeans_Power_Model indepMeans_Power_Model, 
                             IndepMeans_Power_Dashboard single_Z_Dash,
                             double placeHoriz, double placeVert,
                             double withThisWidth, double withThisHeight) {

        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        //System.out.println("85 IndepMeans_Power_PdfView, constructing");
        this.indepMeans_Power_Model = indepMeans_Power_Model;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        indepMeans_Power_Model.restoreNullValues();
        
        n_1 = indepMeans_Power_Model.getSampleSize_1();
        n_2 = indepMeans_Power_Model.getSampleSize_2();
        standErrDiffMeans = indepMeans_Power_Model.getStandErrDiffMeans();
        nullDiffMeans = indepMeans_Power_Model.getNullDiffMeans();
        
        // Control the height of the normal curves
        densityFactor = 0.35 / Normal.density(nullDiffMeans, nullDiffMeans, standErrDiffMeans, false);
        
        alpha = indepMeans_Power_Model.getAlpha();
        effectSize = indepMeans_Power_Model.getEffectSize();
        lowerSliver =  (1.0 - MIDDLE_Z) / 2.0;
        upperSliver = 1 - lowerSliver;     
        nullDiffMeans = indepMeans_Power_Model.getNullDiffMeans();
        altDiffMeans = indepMeans_Power_Model.getAltDiffMeans();
        rejectionCriterion = indepMeans_Power_Model.getRejectionCriterion();
        
        switch (rejectionCriterion) {
            case "LessThan":
                altMu = nullDiffMeans - effectSize;
                break;
                
            case "NotEqual":
            case "GreaterThan":
                altMu = nullDiffMeans + effectSize;
                break;

            default:
                String switchFailure = "Switch failure: IndepMeans_Power_PdfView 118 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }        
 
        nonRejectionRegion = new Point_2D(indepMeans_Power_Model.getNonRejectionRegion().getFirstValue(),
                                                indepMeans_Power_Model.getNonRejectionRegion().getSecondValue());

        // These checkboxes control what is printed in the alternate density
        nCheckBoxes = 3;
        strHBoxCheckBoxDescr = new String[nCheckBoxes];
        strHBoxCheckBoxDescr[0] = " Power ";
        strHBoxCheckBoxDescr[1] = " Type II ";        
        strHBoxCheckBoxDescr[2] = " Annotation ";       
        checkBoxHeight = 350.0;
        graphCanvas = new Canvas(initWidth, initHeight);
        makeTheCheckBoxes();
        makeItHappen(); 
    } 
    
    public void makeTheCheckBoxes() {
        boolHBoxCheckBoxSettings = new boolean[nCheckBoxes];
        for (int ithSetting = 0; ithSetting < nCheckBoxes; ithSetting++) {
            boolHBoxCheckBoxSettings[ithSetting] =  false;
        } 
        
        checkBoxRow = new AnchorPane();
        checkBoxRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        chBoxCheckBoxes = new CheckBox[nCheckBoxes];

        for (int i = 0; i < nCheckBoxes; i++) {
            chBoxCheckBoxes[i] = new CheckBox(strHBoxCheckBoxDescr[i]);            
            chBoxCheckBoxes[i].setMaxWidth(Double.MAX_VALUE);
            chBoxCheckBoxes[i].setId(strHBoxCheckBoxDescr[i]);
            chBoxCheckBoxes[i].setSelected(boolHBoxCheckBoxSettings[i]);
            chBoxCheckBoxes[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );
            
            if (chBoxCheckBoxes[i].isSelected() == true) {
                chBoxCheckBoxes[i].setTextFill(Color.GREEN);
            }
            else { chBoxCheckBoxes[i].setTextFill(Color.RED); }
            
            chBoxCheckBoxes[i].setOnAction(e->{
                CheckBox tb = ((CheckBox) e.getTarget());
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                
                // Reset selected color
                if (checkValue == true) { tb.setTextFill(Color.GREEN); }
                else { tb.setTextFill(Color.RED); }
                
                switch (daID) {    
                    case " Power ":
                        boolHBoxCheckBoxSettings[0] = (checkValue == true);
                        doTheGraph();
                        break;

                    case " Type II ":  
                        boolHBoxCheckBoxSettings[1] = (checkValue == true);
                        doTheGraph();
                        break;

                    case " Annotation ":  
                        boolHBoxCheckBoxSettings[2] = (checkValue == true);
                        doTheGraph();
                        break;

                    default:
                        String switchFailure = "Switch failure: IndepMeans_Power_PdfView 190 " + daID;
                        MyAlerts.showUnexpectedErrorAlert(switchFailure); 
                }
            }); //  end setOnAction
        }  
        
        checkBoxRow.getChildren().addAll(chBoxCheckBoxes);
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
    
    public void completeTheDeal() {
        initializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();
        setHandlers();
        doTheGraph();     
        theContainingPane = dragableAnchorPane.getTheContainingPane();     
    }
   
    public void setUpDecisionRegions() {
        
        double leftT, rightT;
        
        // Do I need this if I'm really just getting these from the controller?
        switch(rejectionCriterion) {
            
            case "LessThan":    // Alternative is less than
                rawCritValLT =  nonRejectionRegion.getFirstValue();
                strSingle_CritValueDescr = "Critical value = " + String.format("%6.3f", rawCritValLT);
                break;
            
            case "NotEqual":    // Alternative is not equal to  
                rawCritValLT =  nonRejectionRegion.getFirstValue();
                rawCritValGT =  nonRejectionRegion.getSecondValue(); 
                strTwo_CritValuesDescr = "Critical values = " + String.format("%6.3f and %6.3f", rawCritValLT, rawCritValGT);
                break;                
            
            case "GreaterThan":    // Alternative is greater than
                rawCritValGT =  nonRejectionRegion.getSecondValue(); 
                strSingle_CritValueDescr = "Critical value = " + String.format("%6.3f", rawCritValGT);
                break;  
                
            default:
                String switchFailure = "Switch failure: IndepMeans_Power_PdfView 247 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);     
        } 
    } 
    
    @Override
    protected void setUpUI() { 
        String title1String = " Power, Independent means";
        String title2String = indepMeans_Power_Model.getPrintedNullHypothesis() + " vs. " + indepMeans_Power_Model.getPrintedAltHypothesis();
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
            chBoxCheckBoxes[iChex].translateXProperty()
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
        //  Get ranges of Central and NonCentral t
        double lowZNull = nullDiffMeans - 4.0 * standErrDiffMeans;
        double hiZNull = nullDiffMeans + 4.0 * standErrDiffMeans; 
        double lowZAlt = altMu - 4.0 * standErrDiffMeans;
        double hiZAlt = altMu + 4.0 * standErrDiffMeans;
        fromHere = Math.min(lowZNull, lowZAlt);
        toThere = Math.max(hiZNull, hiZAlt);
        
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
        //xMin = xMax = xGraphLeft;
        xRange = xGraphRight - xGraphLeft;     
        
        yMax = 1.0;
        yRange = yMax;
        // These constants control the rate of axis scale change when dragging
        deltaX = 0.005 * xRange; deltaY = 0.005 * yRange;   
    }
    
    public void doTheGraph() {      
        double xx0_Null, yy0_Null, xx1_Null, density_Null;
        double xx0_Alt, yy0_Alt, xx1_Alt, density_Alt; 
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
            AnchorPane.setLeftAnchor(chBoxCheckBoxes[chex], (chex) * tempWidth / 5.0);
        }
        
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        boolean power_Desired = boolHBoxCheckBoxSettings[0];
        boolean typeII_Desired = boolHBoxCheckBoxSettings[1];
        boolean annotation_Desired = boolHBoxCheckBoxSettings[2];
        
        // Vertical line height for critical value
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);  
        yStart = yAxis.getDisplayPosition(0.0);
        double tentative_yStop = 0.925 * yMax;
        if (tentative_yStop > .925) {
            tentative_yStop = .925;
        }
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
                String switchFailure = "Switch failure: IndepMeans_Power_PdfView 412 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }   //  end switch

        xx0_Null = xGraphLeft; 
        yy0_Null = Normal.density(xx0_Null, nullDiffMeans, standErrDiffMeans, false); 
        
        for (double x = xGraphLeft; x <= xGraphRight; x += scaleDelta) {
            xx1_Null = x;
            density_Null = densityFactor * Normal.density(xx1_Null, nullDiffMeans, standErrDiffMeans, false); 
            xStart = xAxis.getDisplayPosition(xx0_Null); 
            yStart = yAxis.getDisplayPosition(yy0_Null); 
            xStop = xAxis.getDisplayPosition(xx1_Null);
            yStop = yAxis.getDisplayPosition(density_Null);
            
            gc.setLineWidth(1);
            gc.setStroke(Color.BLACK);
            gc.strokeLine(xStart, yStart, xStop, yStop);            

            xx0_Null = xx1_Null; yy0_Null = density_Null;   //  Next start point for line segment
        }   
                
        if (!rejectionCriterion.equals("NotEqual")) {
            xx0_Alt = xGraphLeft; 
            yy0_Alt = Normal.density(xx0_Alt, altDiffMeans, standErrDiffMeans, false); 

            altScaleHeight = yAxis.getDisplayPosition(0.5);
            altScaleStart = xAxis.getDisplayPosition(altDiffMeans - 3.5 * standErrDiffMeans);
            altScaleStop = xAxis.getDisplayPosition(altDiffMeans + 3.5 * standErrDiffMeans);
            gc.strokeLine(altScaleStart, altScaleHeight, altScaleStop, altScaleHeight); 

            for (double x = xGraphLeft; x <= xGraphRight; x += scaleDelta) {
                xx1_Alt = x;
                density_Alt = densityFactor * Normal.density(xx1_Alt, altDiffMeans, standErrDiffMeans, false);
                xStart = xAxis.getDisplayPosition(xx0_Alt); 
                yStart = yAxis.getDisplayPosition(yy0_Alt + .5); 
                xStop = xAxis.getDisplayPosition(xx1_Alt);
                yStop = yAxis.getDisplayPosition(density_Alt + .5);

                gc.setLineWidth(1);
                gc.setStroke(Color.BLACK);
                gc.strokeLine(xStart, yStart, xStop, yStop);

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
            density_Null = densityFactor * Normal.density(xx1_Null, nullDiffMeans, standErrDiffMeans, false);        
            density_Alt = densityFactor * Normal.density(xx1_Null, altDiffMeans, standErrDiffMeans, false);

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
                        }  
                        else {
                            gc.setStroke(Color.AQUAMARINE);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null); 
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);
                        }                        
                    }
                    else
                    {

                        if (typeII_Desired) {
                            gc.setStroke(Color.BLUE);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null); 
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);
                        }
                        else {
                            gc.setStroke(Color.AQUAMARINE);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null);
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);
                        }
                    }
                    break;

                case "NotEqual":    // Alternative is not equal to
                    if ((rawCritValLT < x) && (x < rawCritValGT)) { 
                        if (typeII_Desired) {
                            gc.setStroke(Color.BLUE);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null);
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);                            
                        } else {
                            gc.setStroke(Color.AQUAMARINE);
                            gc.strokeLine(xStart, yStart_Null, xStop, yStop_Null);  
                            gc.strokeLine(xStart, yStart_Alt, xStop, yStop_Alt);                            
                        }                      
                    } else {
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
                    } else {
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
                    String switchFailure = "Switch failure: IndMeans_PowerPDF 556 " + rejectionCriterion;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);     
            }   //  end switch
        }  //   end loop   
        
        /***********************************************************************
         *                     Arrows and Scribbles                            *                    
         **********************************************************************/
        
        switch (rejectionCriterion) {
            case "LessThan":
                xCritValDescrStart = xAxis.getDisplayPosition(rawCritValLT - 0.75  * standErrDiffMeans);
                break;
                
            case "NotEqual":
                xCritValDescrStart = xAxis.getDisplayPosition(nullDiffMeans - 1.75  * standErrDiffMeans);
                break;
                
            case "GreaterThan":
                xCritValDescrStart = xAxis.getDisplayPosition(rawCritValGT - 0.75  * standErrDiffMeans);
                break;
                
            default:
                String switchFailure = "Switch failure: IndepMeans_Power_PdfView 579 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);  
        } 

        if (!rejectionCriterion.equals("NotEqual")) {
            yCritValDescrStart = yAxis.getDisplayPosition(0.95);
            gc.fillText(strSingle_CritValueDescr, xCritValDescrStart, yCritValDescrStart);
        }
        else {
            yCritValDescrStart = yAxis.getDisplayPosition(0.95);
            gc.fillText(strTwo_CritValuesDescr, xCritValDescrStart, yCritValDescrStart); 

        }
        switch (rejectionCriterion) {
            case "LessThan": 
                xAltDistDescr = xAxis.getDisplayPosition(nullDiffMeans + 1.75  * standErrDiffMeans);
                break;
                
            case "NotEqual":
                break;
            
            case "GreaterThan": 
                xAltDistDescr = xAxis.getDisplayPosition(nullDiffMeans - 3.0  * standErrDiffMeans);
                break;
                
            default:
                String switchFailure = "Switch failure: IndepMeans_Power_PdfView 605 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);  
        }
        
        // No alternative sampling distribution is drawn if NotEqual
        if (!rejectionCriterion.equals("NotEqual")) {
            yAltDistDescr = yAxis.getDisplayPosition(0.65);
            gc.fillText(strAltSampDistDescr, xAltDistDescr, yAltDistDescr);
            yNullDistDescr = yAxis.getDisplayPosition(0.15);
            gc.fillText(strNullSampDistDescr, xAltDistDescr, yNullDistDescr);
        }

        switch(rejectionCriterion) {
            case "LessThan":        
                if (annotation_Desired) {
                    gc.setStroke(Color.RED);   
                    nullArrowHeight = yAxis.getDisplayPosition(0.40); 
                    nullTextHeight = yAxis.getDisplayPosition(0.80);
                    rejectionArrowStart = xAxis.getDisplayPosition(rawCritValLT);
                    rejectionArrowStop = xAxis.getDisplayPosition(rawCritValLT - 1.5 * standErrDiffMeans);

                    rejectionFailureArrowStart = rejectionArrowStart;
                    rejectionFailureArrowStop = xAxis.getDisplayPosition(rawCritValLT + 1.5 * standErrDiffMeans);
                    nonRejectionTextStart = xAxis.getDisplayPosition(standErrDiffMeans + 1.5 * standErrDiffMeans);
                    rejectionTextStart = xAxis.getDisplayPosition(rawCritValLT - 2.7 * standErrDiffMeans);
                    gc.strokeLine(rejectionArrowStart, nullArrowHeight, rejectionArrowStop, nullArrowHeight); 
                    gc.strokeLine(rejectionFailureArrowStart, nullArrowHeight, rejectionFailureArrowStop, nullArrowHeight);

                    nonRejectionTextStart = xAxis.getDisplayPosition(rawCritValLT + 2.0 * standErrDiffMeans);
                    
                    drawAnArrow(rawCritValLT, rawCritValLT - 1.5 * standErrDiffMeans, 0.40);
                    drawAnArrow(rawCritValLT, rawCritValLT + 1.5 * standErrDiffMeans, 0.40);

                    gc.fillText(strRejectRegion, rejectionTextStart, nullArrowHeight);
                    gc.fillText(strNoRejectRegion, nonRejectionTextStart, nullArrowHeight);

                    gc.fillText(strGoodCall, rejectionTextStart, nullTextHeight);
                    gc.fillText(strPrTypeII, nonRejectionTextStart, nullTextHeight);
                }
                break;
                
            case "NotEqual": 
                if (annotation_Desired ) {
                    gc.setStroke(Color.RED);   
                    nullArrowHeight = yAxis.getDisplayPosition(0.40); 
                    leftRejectionArrowStart = xAxis.getDisplayPosition(rawCritValLT);
                    leftRejectionArrowStop = xAxis.getDisplayPosition(rawCritValLT - 1.5 * standErrDiffMeans);
                    rightRejectionArrowStart = xAxis.getDisplayPosition(rawCritValGT);
                    rightRejectionArrowStop = xAxis.getDisplayPosition(rawCritValGT + 1.5 * standErrDiffMeans);
                    
                    nonRejectionTextStart = xAxis.getDisplayPosition(nullDiffMeans - 1.0 * standErrDiffMeans);
                    gc.strokeLine(leftRejectionArrowStart, nullArrowHeight, leftRejectionArrowStop, nullArrowHeight); 
                    gc.strokeLine(rightRejectionArrowStart, nullArrowHeight, rightRejectionArrowStop, nullArrowHeight);

                    leftRejectionTextStart = xAxis.getDisplayPosition(rawCritValLT - 1.25 * standErrDiffMeans);
                    rightRejectionTextStart = xAxis.getDisplayPosition(rawCritValGT + 0.50 * standErrDiffMeans);
                    
                    gc.strokeLine(leftRejectionArrowStart, nullArrowHeight, leftRejectionArrowStop, nullArrowHeight); 
                    gc.strokeLine(rejectionFailureArrowStart, nullArrowHeight, rejectionFailureArrowStop, nullArrowHeight);
                    
                    xNotEqualNoRejectRegionStart = xAxis.getDisplayPosition(nullDiffMeans - 0.25 * standErrDiffMeans); 
                    yNotEqualNoRejectRegionStart = yAxis.getDisplayPosition(0.45);
                   
                    gc.fillText(strRejectRegion, leftRejectionTextStart, yAxis.getDisplayPosition(0.43));
                    gc.fillText(strRejectRegion, rightRejectionTextStart, yAxis.getDisplayPosition(0.43));
                    
                    gc.fillText(strNoRejectRegion, xNotEqualNoRejectRegionStart, yNotEqualNoRejectRegionStart);
                    
                    drawAnArrow(rawCritValLT, rawCritValLT - 1.5 * standErrDiffMeans, 0.40);                    
                    drawAnArrow(rawCritValGT, rawCritValGT + 1.5 * standErrDiffMeans, 0.40);
                    
                }                
                break;
        
            case "GreaterThan":
                if (annotation_Desired) {
                    gc.setStroke(Color.RED);   
                    nullArrowHeight = yAxis.getDisplayPosition(0.40); 
                    nullTextHeight = yAxis.getDisplayPosition(0.80);
                    rejectionArrowStart = xAxis.getDisplayPosition(rawCritValGT);
                    rejectionArrowStop = xAxis.getDisplayPosition(rawCritValGT + 1.5 * standErrDiffMeans);

                    rejectionFailureArrowStart = rejectionArrowStart;
                    rejectionFailureArrowStop = xAxis.getDisplayPosition(rawCritValGT - 1.5 * standErrDiffMeans);
                    nonRejectionTextStart = xAxis.getDisplayPosition(standErrDiffMeans - 2.5 * standErrDiffMeans);
                    rejectionTextStart = xAxis.getDisplayPosition(rawCritValGT + 1.7 * standErrDiffMeans);
                    gc.strokeLine(rejectionArrowStart, nullArrowHeight, rejectionArrowStop, nullArrowHeight); 
                    gc.strokeLine(rejectionFailureArrowStart, nullArrowHeight, rejectionFailureArrowStop, nullArrowHeight);

                    nonRejectionTextStart = xAxis.getDisplayPosition(rawCritValGT - 3.0 * standErrDiffMeans);
                    
                    drawAnArrow(rawCritValGT, rawCritValGT + 1.5 * standErrDiffMeans, 0.40);
                    drawAnArrow(rawCritValGT, rawCritValGT - 1.5 * standErrDiffMeans, 0.40);

                    gc.fillText(strRejectRegion, rejectionTextStart, nullArrowHeight);
                    gc.fillText(strNoRejectRegion, nonRejectionTextStart, nullArrowHeight);

                    gc.fillText(strGoodCall, rejectionTextStart, nullTextHeight);
                    gc.fillText(strPrTypeII, nonRejectionTextStart, nullTextHeight);
                }
                break;
                
            default:
                String switchFailure = "Switch failure: IndepMeans_Power_PdfView 708 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);  
        }
        
        indepMeans_Power_Model.restoreNullValues();
        
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
        
    }   //  end doTheGraph
    
    private void drawAnArrow (double fromHere, double toThere, double atThisHeight) {
        double verticalArrowPointLength, horizontalArrowPointLength,
               xUpperPointEnd, yUpperPointEnd,xLowerPointEnd,yLowerPointEnd,
               xUPDisplay, yUPDisplay, xLPDisplay, yLPDisplay;
        
        rejectionArrowStart = xAxis.getDisplayPosition(fromHere);
        rejectionArrowStop = xAxis.getDisplayPosition(toThere);
        nullArrowHeight = yAxis.getDisplayPosition(atThisHeight);
        
        verticalArrowPointLength = SQRT2_OVER2 * 0.035;
        horizontalArrowPointLength = 0.15 * standErrDiffMeans;
        yUpperPointEnd = atThisHeight + verticalArrowPointLength;
        yLowerPointEnd = atThisHeight - verticalArrowPointLength;        

        if (fromHere < toThere) {   //  Pointing to right
            xUpperPointEnd = toThere - horizontalArrowPointLength;
            xLowerPointEnd = toThere - horizontalArrowPointLength;

            xUPDisplay = xAxis.getDisplayPosition(xUpperPointEnd);
            yUPDisplay = yAxis.getDisplayPosition(yUpperPointEnd);
            xLPDisplay = xAxis.getDisplayPosition(xLowerPointEnd);
            yLPDisplay = yAxis.getDisplayPosition(yLowerPointEnd);
            
            gc.strokeLine(rejectionArrowStart, nullArrowHeight, rejectionArrowStop, nullArrowHeight);
            gc.strokeLine(rejectionArrowStop, nullArrowHeight, xUPDisplay, yUPDisplay);
            gc.strokeLine(rejectionArrowStop, nullArrowHeight, xLPDisplay, yLPDisplay);
        } else {  //  Pointing to left
            xUpperPointEnd = toThere + horizontalArrowPointLength;
            xLowerPointEnd = toThere + horizontalArrowPointLength;

            xUPDisplay = xAxis.getDisplayPosition(xUpperPointEnd);
            yUPDisplay = yAxis.getDisplayPosition(yUpperPointEnd);
            xLPDisplay = xAxis.getDisplayPosition(xLowerPointEnd);
            yLPDisplay = yAxis.getDisplayPosition(yLowerPointEnd);
            
            gc.strokeLine(rejectionArrowStart, nullArrowHeight, rejectionArrowStop, nullArrowHeight);
            gc.strokeLine(rejectionArrowStop, nullArrowHeight, xUPDisplay, yUPDisplay);
            gc.strokeLine(rejectionArrowStop, nullArrowHeight, xLPDisplay, yLPDisplay);           
        }
    }

    public double[] getInverseMiddle_Z_Area(double middleArea)  {
        double[] middleInterval;
        middleInterval = new double[2];
        middleInterval[0] = Normal.quantile(lowerSliver, nullDiffMeans, standErrDiffMeans, true, false);
        middleInterval[1] = Normal.quantile(upperSliver, nullDiffMeans, standErrDiffMeans, true, false);
        return middleInterval;
    }
    
    public double[] getInverseMiddle_NonC_Z_Area(double middleArea)  {
        double[] middleInterval;
        middleInterval = new double[2];
        middleInterval[0] = Normal.quantile(lowerSliver, nullDiffMeans, standErrDiffMeans, true, false);
        middleInterval[1] = Normal.quantile(upperSliver, nullDiffMeans, standErrDiffMeans, true, false);
        return middleInterval;
    }
   
   public Pane getTheContainingPane() { return theContainingPane; }
}




