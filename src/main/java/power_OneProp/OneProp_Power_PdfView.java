/**************************************************
 *            OneProp_Power_PdfView               *
 *                   01/15/25                     *
 *                    21:00                       *
 *************************************************/
package power_OneProp;

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

public class OneProp_Power_PdfView extends BivariateScale_W_CheckBoxes_View { 
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    boolean[] boolHBoxChBoxSettings;
    
    double yMin, yMax, scaleDelta, stErr_PNull, nullProp, altProp,
           rawCritValLT, rawCritValGT, nonRejStart, yStart_Null, yStop_Null,
           yStart_Alt, yStop_Alt, xNENoRejRegionTxtStart, stErr_PAlt, 
           effectSize, lowerSliver, upperSliver, densityFactor;
    
    double rejArrStart, rejArrStop, rejFailureArrStart, rejTxtStart,
           rejFailureArrStop, nullArrHeight, altScaleHeight, nonRejTxtStart,
            nullTextHeight, altScaleStart, altScaleStop,
            leftRejArrStart, leftRejArrStop, rightRejArrStart, rightRejArrStop;
    
    double /*xNERejRegionStart,*/ yNENoRejRegionStart, xCritValDescrStart, 
            yCritValDescrStart, xAltDistDescr, yAltDistDescr, yNullDistDescr,
            leftRejTxtStart, rightRejTxtStart;
    
    final double MIDDLE_Z = 0.999;
    final double SQRT2_OVER2 = Math.sqrt(2.0) / 2.0;
    
    String strRejectionCriterion;
    
    String strSingleCritValueDescr = "Critical value = ";
    String strTwoCritValuesDescr = "Critical values = ";
    
    final String strRejectRegion = "Reject";
    final String strNonRejectRegion = "Fail to \nreject";   
    final String strGoodCall = "  Good  \n  Call!";
    final String strPrTypeII = "Oops! Type \n II error";
    final String str_AltSampDistDescr = "The alternate\ndistribution";
    final String str_NullSampDistDescr = "  The null\ndistribution";
    
    String[] strHBoxCheckBoxDescr;
        
    AnchorPane checkBoxRow;
    CheckBox[] arCheckBoxes;
    
    // My classes
    OneProp_Power_Model oneProp_Power_Model;
 
    //  FX
    Pane theContainingPane;
    Point_2D nonRejectionRegion;


    public OneProp_Power_PdfView(OneProp_Power_Model oneProp_Power_Model, 
                             OneProp_Power_Dashboard oneProp_Power_Dashboard,
                             double placeHoriz, double placeVert,
                             double withThisWidth, double withThisHeight) {

        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff == true) {
            System.out.println("86 *** OneProp_Power_PdfView, Constructing");
        }
        this.oneProp_Power_Model = oneProp_Power_Model;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.oneProp_Power_Model.restoreNullValues();
        //sampleSize = this.oneProp_Power_Model.getSampleSize();
        nullProp = this.oneProp_Power_Model.getNullProp();
        stErr_PNull = this.oneProp_Power_Model.getStErr_PNull();
        stErr_PAlt = this.oneProp_Power_Model.getStandErr_PAlt();
        // Control the height of the normal curves
        densityFactor = 0.35 / Normal.density(nullProp, nullProp, stErr_PNull, false);
        
        stErr_PAlt = this.oneProp_Power_Model.getStandErr_PAlt();
        alpha = this.oneProp_Power_Model.getAlpha();
        effectSize = this.oneProp_Power_Model.getEffectSize();
        lowerSliver =  (1.0 - MIDDLE_Z) / 2.0;
        upperSliver = 1 - lowerSliver;     
        alpha = this.oneProp_Power_Model.getAlpha();
        nullProp = this.oneProp_Power_Model.getNullProp();
        strRejectionCriterion = this.oneProp_Power_Model.getRejectionCriterion();
        
        switch (strRejectionCriterion) {
            case "LessThan":
                altProp = nullProp - effectSize;
                break;
                
            case "NotEqual":
            case "GreaterThan":
                altProp = nullProp + effectSize;
                break;
                
            default:
                String switchFailure = "Switch failure: OneProp_Power_PdfView 115 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }        
        stErr_PNull = this.oneProp_Power_Model.getStErr_PNull();   
        nonRejectionRegion = new Point_2D(this.oneProp_Power_Model.getNonRejectionRegion().getFirstValue(),
                                                this.oneProp_Power_Model.getNonRejectionRegion().getSecondValue());


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
        boolHBoxChBoxSettings = new boolean[nCheckBoxes];
        
        for (int ithSetting = 0; ithSetting < nCheckBoxes; ithSetting++) {
            boolHBoxChBoxSettings[ithSetting] =  false;
        } 
        
        checkBoxRow = new AnchorPane();
        checkBoxRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        arCheckBoxes = new CheckBox[nCheckBoxes];

        for (int i = 0; i < nCheckBoxes; i++) {
            arCheckBoxes[i] = new CheckBox(strHBoxCheckBoxDescr[i]);
            
            arCheckBoxes[i].setMaxWidth(Double.MAX_VALUE);
            arCheckBoxes[i].setId(strHBoxCheckBoxDescr[i]);
            arCheckBoxes[i].setSelected(boolHBoxChBoxSettings[i]);

            arCheckBoxes[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            
            if (arCheckBoxes[i].isSelected() == true) {
                arCheckBoxes[i].setTextFill(Color.GREEN);
            }
            else { arCheckBoxes[i].setTextFill(Color.RED);  }
            
            arCheckBoxes[i].setOnAction(e->{
                CheckBox tb = ((CheckBox) e.getTarget());                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                // Reset selected color
                if (checkValue == true) { tb.setTextFill(Color.GREEN); }
                else { tb.setTextFill(Color.RED); }
                
                switch (daID) {    
                    case " Power ":
                        boolHBoxChBoxSettings[0] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    case " Type II ":  
                        boolHBoxChBoxSettings[1] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    case " Annotation ":  
                        boolHBoxChBoxSettings[2] = (checkValue == true);
                        doTheGraph();
                        break;

                    default:
                        String switchFailure = "Switch failure: OneProp_Power_PdfView 191 " + daID;
                        MyAlerts.showUnexpectedErrorAlert(switchFailure);
                }
            }); 
        }          
        checkBoxRow.getChildren().addAll(arCheckBoxes);
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
        // Do I need this if I'm really just getting these from the controller?
        switch(strRejectionCriterion) {
            
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
                String switchFailure = "Switch failure: OneProp_Power_PdfView 244 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);    
        }   //  end switch
    }   //  end setUpDecisionRegions

    
    @Override
    protected void setUpUI() { 
        String title1String = " Power, single Proportion Z";
        String title2String = oneProp_Power_Model.getPrintedNullHypothesis() + " vs. " + oneProp_Power_Model.getPrintedAltHypothesis();
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
            
            arCheckBoxes[iChex].translateXProperty()
                                 .bind(graphCanvas.widthProperty()
                                 .divide(250.0)
                                 .multiply(5 * iChex)
                                 .subtract(50.0));
        }
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(checkBoxRow, txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
  
    public void initializeGraphParameters() {
        //  Get ranges of Central and NonCentral t
        double lowZNull = nullProp - 4.5 * stErr_PAlt;
        double hiZNull = nullProp + 4.5 * stErr_PAlt; 
        double lowZAlt = altProp - 4.5 * stErr_PAlt;
        double hiZAlt = altProp + 4.5 * stErr_PAlt;
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
            AnchorPane.setLeftAnchor(arCheckBoxes[chex], (chex) * tempWidth / 5.0);
        }
        
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        boolean power_Desired = boolHBoxChBoxSettings[0];
        boolean typeII_Desired = boolHBoxChBoxSettings[1];
        boolean annotation_Desired = boolHBoxChBoxSettings[2];
        
        // Vertical line height for critical value
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);  
        yStart = yAxis.getDisplayPosition(0.0);
        double tentative_yStop = 0.925 * yMax;
        
        if (tentative_yStop > .925) { tentative_yStop = .925; }
        
        yStop = yAxis.getDisplayPosition(tentative_yStop);

        switch(strRejectionCriterion) {
            
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
                String switchFailure = "Switch failure: OneProp_Power_PdfView 415 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);  
        }   //  end switch

        /************************************************************
        *****                Do the Null graph         **************
        ************************************************************/
        xx0_Null = xGraphLeft; 
        yy0_Null = Normal.density(xx0_Null, nullProp, stErr_PAlt, false); 
        
        for (double x = xGraphLeft; x <= xGraphRight; x += scaleDelta) {
            xx1_Null = x;
            density_Null = densityFactor * Normal.density(xx1_Null, nullProp, stErr_PAlt, false); 
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
        
        if (!strRejectionCriterion.equals("NotEqual")) {
            xx0_Alt = xGraphLeft; 
            yy0_Alt = Normal.density(xx0_Alt, altProp, stErr_PAlt, false); 

            altScaleHeight = yAxis.getDisplayPosition(0.5);
            altScaleStart = xAxis.getDisplayPosition(nullProp - 3.5 * stErr_PAlt);
            altScaleStop = xAxis.getDisplayPosition(altProp + 3.5 * stErr_PAlt);
            gc.strokeLine(altScaleStart, altScaleHeight, altScaleStop, altScaleHeight); 

            for (double x = xGraphLeft; x <= xGraphRight; x += scaleDelta) {
                xx1_Alt = x;
                density_Alt = densityFactor * Normal.density(xx1_Alt, altProp, stErr_PAlt, false);
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
        // Convert raw scale to normal for density calculation
        yy0_Null = densityFactor * Normal.density(xx0_Null, nullProp, stErr_PAlt, false);        
        yy0_Alt = densityFactor * Normal.density(xx0_Null, nullProp, stErr_PAlt, false); 
       
        for (double x = xGraphLeft; x <= xGraphRight; x += scaleDelta) {
            xx1_Null = x;
            density_Null = densityFactor * Normal.density(xx1_Null, nullProp, stErr_PAlt, false);        
            density_Alt = densityFactor * Normal.density(xx1_Null, altProp, stErr_PAlt, false);

            xStart = xAxis.getDisplayPosition(xx1_Null); 
            xStop = xAxis.getDisplayPosition(xx1_Null); 
            yStart_Null = yAxis.getDisplayPosition(0.0); 
            yStop_Null = yAxis.getDisplayPosition(density_Null);            
            yStart_Alt = yAxis.getDisplayPosition(0.0 + 0.5); 
            yStop_Alt = yAxis.getDisplayPosition(density_Alt + 0.5);              
            
            switch(strRejectionCriterion) {
                case "LessThan":
                    if (x < rawCritValLT) { 
  
                        if (power_Desired == true) {
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

                        if (typeII_Desired == true) {
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

                case "NotEqual":
                    
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

                case "GreaterThan":
                    
                    if (x < rawCritValGT) {      
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
                    else
                    {
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
                    break;  

                    default:
                        String switchFailure = "Switch failure: OneProp_Power_PdfView 579 " + strRejectionCriterion;
                        MyAlerts.showUnexpectedErrorAlert(switchFailure);    
            }   //  end switch
        }  //   end loop   
        
        /***********************************************************************
        *                     Arrows and Scribbles                             *                    
        *  l/r = left/right; rejArr = rejectionArrow; rejTxt = rejectionText   *
        *  LT = LessThan, EQ = Equal to; GT = Greater Than                     *
        ***********************************************************************/
        
        switch (strRejectionCriterion) {
            case "LessThan":
                xCritValDescrStart = xAxis.getDisplayPosition(rawCritValLT - 0.75  * stErr_PNull);
                break;
                
            case "NotEqual":
                xCritValDescrStart = xAxis.getDisplayPosition(nullProp - 1.75  * stErr_PNull);
                break;
                
            case "GreaterThan":
                xCritValDescrStart = xAxis.getDisplayPosition(rawCritValGT - 0.75  * stErr_PNull);
                break;
                
            default:
                String switchFailure = "Switch failure: OneProp_Power_PdfView 604 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);                  
        } 

        if (!strRejectionCriterion.equals("NotEqual")) {
            yCritValDescrStart = yAxis.getDisplayPosition(0.975);
            gc.fillText(strSingleCritValueDescr, xCritValDescrStart, yCritValDescrStart);
        }
        else {
            yCritValDescrStart = yAxis.getDisplayPosition(0.975);
            gc.fillText(strTwoCritValuesDescr, xCritValDescrStart, yCritValDescrStart); 
        }
        
        switch (strRejectionCriterion) {
            case "LessThan": 
                xAltDistDescr = xAxis.getDisplayPosition(nullProp + 1.0  * stErr_PAlt);
                break;
                
            case "NotEqual":
                break;
            
            case "GreaterThan": 
                xAltDistDescr = xAxis.getDisplayPosition(nullProp - 3.0  * stErr_PAlt);
                break;
                
            default:
                String switchFailure = "Switch failure: OneProp_Power_PdfView 630 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);                  
        }
        
        // No alternative sampling distribution is drawn if NotEqual
        if (!strRejectionCriterion.equals("NotEqual")) {
            yAltDistDescr = yAxis.getDisplayPosition(0.65);
            gc.fillText(str_AltSampDistDescr, xAltDistDescr, yAltDistDescr);
            yNullDistDescr = yAxis.getDisplayPosition(0.15);
            gc.fillText(str_NullSampDistDescr, xAltDistDescr, yNullDistDescr);
        }

        switch(strRejectionCriterion) {
            case "LessThan": 
                if (annotation_Desired) {
                    gc.setStroke(Color.RED);   
                    nullArrHeight = yAxis.getDisplayPosition(0.40); 
                    nullTextHeight = yAxis.getDisplayPosition(0.80);
                    rejArrStart = xAxis.getDisplayPosition(rawCritValLT);
                    rejArrStop = xAxis.getDisplayPosition(rawCritValLT - 1.5 * stErr_PAlt);

                    rejFailureArrStart = rejArrStart;
                    rejFailureArrStop = xAxis.getDisplayPosition(rawCritValLT + 1.5 * stErr_PAlt);
                    nonRejStart = xAxis.getDisplayPosition(nullProp + 0.5 * stErr_PAlt);
                    nonRejTxtStart = xAxis.getDisplayPosition(nullProp + 2.0 * stErr_PAlt);
                    rejTxtStart = xAxis.getDisplayPosition(rawCritValLT - 2.7 * stErr_PAlt);
                    gc.strokeLine(rejArrStart, nullArrHeight, rejArrStop, nullArrHeight); 
                    gc.strokeLine(rejFailureArrStart, nullArrHeight, rejFailureArrStop, nullArrHeight);
                    
                    drawAnArrow(rawCritValLT, rawCritValLT - 1.5 * stErr_PAlt, 0.40);
                    drawAnArrow(rawCritValLT, rawCritValLT + 1.5 * stErr_PAlt, 0.40);

                    gc.fillText(strRejectRegion, rejTxtStart, nullArrHeight);
                    gc.fillText(strNonRejectRegion, nonRejStart, nullArrHeight);

                    gc.fillText(strGoodCall, rejTxtStart, nullTextHeight);
                    gc.fillText(strPrTypeII, nonRejTxtStart, nullTextHeight);
                }
                break;
                
            case "NotEqual": 
                if (annotation_Desired) {
                    gc.setStroke(Color.RED);   
                    nullArrHeight = yAxis.getDisplayPosition(0.40); 
                    
                    leftRejArrStart = xAxis.getDisplayPosition(rawCritValLT);
                    leftRejArrStop = xAxis.getDisplayPosition(rawCritValLT - 1.5 * stErr_PAlt);
                    rightRejArrStart = xAxis.getDisplayPosition(rawCritValGT);
                    rightRejArrStop = xAxis.getDisplayPosition(rawCritValGT + 1.5 * stErr_PAlt);

                   
                    nonRejTxtStart = xAxis.getDisplayPosition(nullProp - 0.15 * stErr_PAlt);
                    gc.strokeLine(leftRejArrStart, nullArrHeight, leftRejArrStop, nullArrHeight); 
                    gc.strokeLine(rightRejArrStart, nullArrHeight, rightRejArrStop, nullArrHeight);

                    leftRejTxtStart = xAxis.getDisplayPosition(rawCritValLT - 2.5 * stErr_PAlt);
                    rightRejTxtStart = xAxis.getDisplayPosition(rawCritValGT + 2.25 * stErr_PAlt);
                    
                    gc.strokeLine(leftRejArrStart, nullArrHeight, leftRejArrStop, nullArrHeight); 
                    gc.strokeLine(rejFailureArrStart, nullArrHeight, rejFailureArrStop, nullArrHeight);
                    
                    //xNERejRegionStart = xAxis.getDisplayPosition(nullProp - 2.25 * stErr_PAlt);
                    xNENoRejRegionTxtStart = xAxis.getDisplayPosition(nullProp - 0.125 * stErr_PAlt);
                    yNENoRejRegionStart = yAxis.getDisplayPosition(0.45);                 
                   
                    gc.fillText(strRejectRegion, leftRejTxtStart, nullArrHeight);
                    gc.fillText(strRejectRegion, rightRejTxtStart, nullArrHeight);
                    
                    gc.fillText(strNonRejectRegion, xNENoRejRegionTxtStart, yNENoRejRegionStart);
                    drawAnArrow(rawCritValLT, rawCritValLT - 1.5 * stErr_PAlt, 0.40);                    
                    drawAnArrow(rawCritValGT, rawCritValGT + 1.5 * stErr_PAlt, 0.40);
                    
                }                
                break;
        
            case "GreaterThan": 
                if (annotation_Desired) {
                    gc.setStroke(Color.RED);   
                    nullArrHeight = yAxis.getDisplayPosition(0.40); 
                    nullTextHeight = yAxis.getDisplayPosition(0.80);
                    rejArrStart = xAxis.getDisplayPosition(rawCritValGT);
                    rejArrStop = xAxis.getDisplayPosition(rawCritValGT + 1.5 * stErr_PAlt);

                    rejFailureArrStart = rejArrStart;
                    rejFailureArrStop = xAxis.getDisplayPosition(rawCritValGT - 1.5 * stErr_PAlt);
                    nonRejStart = xAxis.getDisplayPosition(nullProp - 3.5 * stErr_PAlt);
                    nonRejTxtStart = xAxis.getDisplayPosition(nullProp - 2.5 * stErr_PAlt);
                    rejTxtStart = xAxis.getDisplayPosition(rawCritValGT + 1.75 * stErr_PAlt);
                    gc.strokeLine(rejArrStart, nullArrHeight, rejArrStop, nullArrHeight); 
                    gc.strokeLine(rejFailureArrStart, nullArrHeight, rejFailureArrStop, nullArrHeight);

                    nonRejTxtStart = xAxis.getDisplayPosition(rawCritValGT - 3.0 * stErr_PAlt);
                    
                    drawAnArrow(rawCritValGT, rawCritValGT + 1.5 * stErr_PAlt, 0.40);
                    drawAnArrow(rawCritValGT, rawCritValGT - 1.5 * stErr_PAlt, 0.40);

                    gc.fillText(strRejectRegion, rejTxtStart, nullArrHeight);
                    gc.fillText(strNonRejectRegion, nonRejTxtStart, nullArrHeight);

                    gc.fillText(strGoodCall, rejTxtStart, nullTextHeight);
                    gc.fillText(strPrTypeII, nonRejStart, nullTextHeight);
                }
                break;
                
            default:
                String switchFailure = "Switch failure: OneProp_Power_PdfView 735 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);  
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
        
    }   //  end doTheGraph
    
    private void drawAnArrow (double fromHere, double toThere, double atThisHeight) {
        double verticalArrowPointLength, horizontalArrowPointLength,
               xUpperPointEnd, yUpperPointEnd,xLowerPointEnd,yLowerPointEnd,
               xUPDisplay, yUPDisplay, xLPDisplay, yLPDisplay;
        
        rejArrStart = xAxis.getDisplayPosition(fromHere);
        rejArrStop = xAxis.getDisplayPosition(toThere);
        nullArrHeight = yAxis.getDisplayPosition(atThisHeight);
        
        verticalArrowPointLength = SQRT2_OVER2 * 0.035;
        horizontalArrowPointLength = 0.15 * stErr_PAlt;
        yUpperPointEnd = atThisHeight + verticalArrowPointLength;
        yLowerPointEnd = atThisHeight - verticalArrowPointLength;        

        if (fromHere < toThere) {   //  Pointing to right
            xUpperPointEnd = toThere - horizontalArrowPointLength;
            xLowerPointEnd = toThere - horizontalArrowPointLength;

            xUPDisplay = xAxis.getDisplayPosition(xUpperPointEnd);
            yUPDisplay = yAxis.getDisplayPosition(yUpperPointEnd);
            xLPDisplay = xAxis.getDisplayPosition(xLowerPointEnd);
            yLPDisplay = yAxis.getDisplayPosition(yLowerPointEnd);
            
            gc.strokeLine(rejArrStart, nullArrHeight, rejArrStop, nullArrHeight);
            gc.strokeLine(rejArrStop, nullArrHeight, xUPDisplay, yUPDisplay);
            gc.strokeLine(rejArrStop, nullArrHeight, xLPDisplay, yLPDisplay);
        }
        else {  //  Pointing to left
            xUpperPointEnd = toThere + horizontalArrowPointLength;
            xLowerPointEnd = toThere + horizontalArrowPointLength;

            xUPDisplay = xAxis.getDisplayPosition(xUpperPointEnd);
            yUPDisplay = yAxis.getDisplayPosition(yUpperPointEnd);
            xLPDisplay = xAxis.getDisplayPosition(xLowerPointEnd);
            yLPDisplay = yAxis.getDisplayPosition(yLowerPointEnd);
            
            gc.strokeLine(rejArrStart, nullArrHeight, rejArrStop, nullArrHeight);
            gc.strokeLine(rejArrStop, nullArrHeight, xUPDisplay, yUPDisplay);
            gc.strokeLine(rejArrStop, nullArrHeight, xLPDisplay, yLPDisplay);           
        }
    }

   /*******************************************************************
   *   The following functions control whether a test statistic will  *
   *    be plotted and a tail area therefore drawn                    *
   ********************************************************************/
    
    public double[] getInverseMiddle_Z_Area(double middleArea)  {
        double[] middleInterval;
        middleInterval = new double[2];
        middleInterval[0] = Normal.quantile(lowerSliver, nullProp, stErr_PAlt, true, false);
        middleInterval[1] = Normal.quantile(upperSliver, nullProp, stErr_PAlt, true, false);
        return middleInterval;
    }
    
    public double[] getInverseMiddle_NonC_Z_Area(double middleArea)  {
        double[] middleInterval;
        middleInterval = new double[2];
        middleInterval[0] = Normal.quantile(lowerSliver, nullProp, stErr_PAlt, true, false);
        middleInterval[1] = Normal.quantile(upperSliver, nullProp, stErr_PAlt, true, false);
        return middleInterval;
    }
   
   public Pane getTheContainingPane() {  return theContainingPane; }
}



