/**************************************************
 *              Single_t_PDFView                  *
 *                  02/19/24                      *
 *                    15:00                       *
 *************************************************/
package the_t_procedures;

import genericClasses.DragableAnchorPane;
import genericClasses.JustAnAxis;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
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
import theRProbDists.*;
import superClasses.BivariateScale_W_CheckBoxes_View;
import utilityClasses.MyAlerts;

public class Single_t_PDFView extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    double tStat, absValTStat, df; 
    
    final double daMode = 0.4;    //  Let's see how this works.
    final double MIDDLE_T = 0.9999;
    final double[] alphas = {0.10, 0.05, 0.025, 0.01};
    double[] initialInterval;
    
    String hypotheses;
    String[] hBoxCheckBoxDescr;
    
    //String waldoFile = "Single_t_PDFView";
    String waldoFile = "";

    // My classes  
    Data_Manager dm;    
    T_double_df tDistr;
    
    //  POJOs / FX
    AnchorPane checkBoxRow;   
    Pane theContainingPane;
    
    boolean[] hBoxCheckBoxSettings;
    CheckBox[] hBoxCheckBoxes;
    
    public Single_t_PDFView(Single_t_Model single_t_Model, Single_t_Dashboard single_t_Dashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        dm = single_t_Model.getDataManager();
        dm.whereIsWaldo(59, waldoFile, "Constructing");        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        hypotheses = single_t_Model.getHypotheses();
        alpha = single_t_Model.getAlpha();
        df = single_t_Model.getDF();
        tStat = single_t_Model.getTStat();
        pValue = single_t_Model.getPValue();   
    }
    
    public Single_t_PDFView(Single_t_SumStats_Model single_t_SumStats_Model, Single_t_SumStats_Dashboard single_t_SumStats_Dashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        System.out.println("74 Single_t_PDFView, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        hypotheses = single_t_SumStats_Model.getHypotheses();
        alpha = single_t_SumStats_Model.getAlpha();
        df = single_t_SumStats_Model.getDF();
        tStat = single_t_SumStats_Model.getTStat();
        pValue = single_t_SumStats_Model.getPValue();   
    }
    
    public void doTheRest() {        
        nCheckBoxes = 2;
        hBoxCheckBoxDescr = new String[nCheckBoxes];
        hBoxCheckBoxDescr[0] = " Identify P-value ";
        hBoxCheckBoxDescr[1] = " Identify alphas  ";
        
        txtTitle1 = new Text(50, 25, " Scatterplot ");
        absValTStat = Math.abs(tStat);

        tDistr = new T_double_df(df);
        initialInterval = new double[2];
        middle_ForGraph = MIDDLE_T; 
        
        initialInterval[0] = tDistr.quantile((1.0 - middle_ForGraph) / 2.0);
        initialInterval[1] = - initialInterval[0];
        
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
                String switchFailure = "Switch failure: Single_t_PDFView  120 " + hypotheses;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }
        
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
        hBoxCheckBoxes = new CheckBox[nCheckBoxes];

        for (int i = 0; i < nCheckBoxes; i++) {
            hBoxCheckBoxes[i] = new CheckBox(hBoxCheckBoxDescr[i]);
            
            hBoxCheckBoxes[i].setMaxWidth(Double.MAX_VALUE);
            hBoxCheckBoxes[i].setId(hBoxCheckBoxDescr[i]);
            hBoxCheckBoxes[i].setSelected(hBoxCheckBoxSettings[i]);

            hBoxCheckBoxes[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );
            
            if (hBoxCheckBoxes[i].isSelected() == true) {
                hBoxCheckBoxes[i].setTextFill(Color.GREEN);
            }
            else { hBoxCheckBoxes[i].setTextFill(Color.RED); }
            
            hBoxCheckBoxes[i].setOnAction(e->{
                CheckBox tb = ((CheckBox) e.getTarget());                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                
                // Reset selected color
                if (checkValue == true) { tb.setTextFill(Color.GREEN); }
                else { tb.setTextFill(Color.RED); }
                
                switch (daID) {    
                    case " Identify P-value ":
                        hBoxCheckBoxSettings[0] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    case " Identify alphas  ":  
                        hBoxCheckBoxSettings[1] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    default:
                        String switchFailure = "Switch failure: Single_t_PDFView  180 " + hypotheses;
                        MyAlerts.showUnexpectedErrorAlert(switchFailure);
                }
            }); 
        }          
        checkBoxRow.getChildren().addAll(hBoxCheckBoxes);
    }
  
    private void makeItHappen() {
        theContainingPane = new Pane();        
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
        
    public void completeTheDeal() {
        doTheRest();
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
        
        if (df > 1) {
            title2String = String.format("%5.2f", df) + " degrees of freedom";
        }
        else {
            title2String = String.valueOf(df) + " degree of freedom";
        }
        
        txtTitle2 = new Text (60, 45, title2String);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    public void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
      
        for (int iChex = 0; iChex < nCheckBoxes; iChex++) {            
            hBoxCheckBoxes[iChex].translateXProperty()
                                 .bind(graphCanvas.widthProperty()
                                 .divide(250.0)
                                 .multiply(5 * iChex)
                                 .subtract(50.0));
        }
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(checkBoxRow, txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    @Override
    public void initializeGraphParameters() {
        initialInterval[0] = tDistr.quantile((1.0 - MIDDLE_T) / 2.0);
        initialInterval[1] = - initialInterval[0];         
         
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
            AnchorPane.setLeftAnchor(hBoxCheckBoxes[chex], (chex) * tempWidth / 5.0);
        }
        
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        identifyPValueIsDesired = hBoxCheckBoxSettings[0];
        assumptionCheckIsDesired = hBoxCheckBoxSettings[1];
        
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

        xStart = xStop = xAxis.getDisplayPosition(tStat);
        yStart = yAxis.getDisplayPosition(0.0);        

        yStop = yAxis.getDisplayPosition(daMode);        

        gc.setLineWidth(2);
        gc.setStroke(Color.RED);
        gc.strokeLine(xStart, yStart, xStop, yStop);

        if (identifyPValueIsDesired) {
            tempString = String.format("t = %6.3f,  pValue = %4.3f", tStat, pValue);
        }
        else { tempString = String.format("t = %6.3f", tStat); }
        gc.setFill(Color.RED);
        gc.fillText(tempString, xStop + 5, yStop - 5);
       
        if (assumptionCheckIsDesired) {
            double otherXStart, otherXStop;
            
            for (int ithAlpha = 0; ithAlpha < 4; ithAlpha++) {
                double thisAlpha = alphas[ithAlpha]; 
                
                switch (hypotheses) {
                    case "NotEqual": 
                        theCriticalValue = tDistr.quantile(1. - thisAlpha/2.0); 
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
                        theCriticalValue = tDistr.quantile(1. - thisAlpha); 
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
                        theCriticalValue = tDistr.quantile(1. - thisAlpha); 
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
                        String switchFailure = "Switch failure: Single_t_PDFView  446 " + hypotheses;
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




