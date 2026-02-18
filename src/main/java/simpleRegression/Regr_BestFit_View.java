/**************************************************
 *             Regression_BestFit_View            *
 *                    05/29/24                    *
 *                      15:00                     *
 *************************************************/
package simpleRegression;

import javafx.scene.SnapshotParameters;
import superClasses.BivariateScale_W_CheckBoxes_View;
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
import matrixProcedures.Matrix;
import probabilityDistributions.*;
import splat.Data_Manager;

public class Regr_BestFit_View extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    double influenceTrigger, defaultInfluencePlusLength, influencePlusLength,
           leverageTrigger, defaultLeverageCrossLength, leverageCrossLength,
           outlierTrigger, defaultOutlierRadius, outlierRadius, dotRadius, 
           dotDiameter, absRStud, cooksD, slope, intercept;
    
    double[] leverage;
        
    // Make empty if no-print
    //String waldoFile = "Regr_BestFit_View";
    String waldoFile = "";

    String[] axisLabels;

    // My classes    
    Data_Manager dm;
    Matrix r_Student, cooks_D;
    FDistribution fDist_Influence;
    
    
    //  POJO / FX
    Pane theContainingPane;

    public Regr_BestFit_View(Inf_Regr_Model inf_Regression_Model, Regr_Dashboard regression_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        dm = inf_Regression_Model.getDataManager();
        dm.whereIsWaldo(57, waldoFile, "Constructing"); 
        X = inf_Regression_Model.getXVar();
        Y = inf_Regression_Model.getY();
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        
        setMinHeight(250);
        setMinWidth(250);
        
        this.regrModel = inf_Regression_Model;
        this.regrDashboard = regression_Dashboard;
        nDataPoints = regrModel.getNRows();        
        nCheckBoxes = 5;
        scatterPlotCheckBoxDescr = new String[nCheckBoxes];
        scatterPlotCheckBoxDescr[0] = " Best fit line ";
        scatterPlotCheckBoxDescr[1] = " Outliers (o) ";
        scatterPlotCheckBoxDescr[2] = " Influence (+) ";
        scatterPlotCheckBoxDescr[3] = " Leverage (x) ";
        scatterPlotCheckBoxDescr[4] = " Relative O/I/L ";

        influenceTrigger = 0.5; // Cook's D
        defaultInfluencePlusLength = 7.5;
        leverageTrigger = 2.0;
        defaultLeverageCrossLength = 7.5;
        outlierTrigger = 2.0; 
        defaultOutlierRadius = 5.0;   
        dotRadius = 5.0;
        dotDiameter =  2.0 * dotRadius;
        
        txtTitle1 = new Text(50, 25, " Scatterplot ");
        txtTitle2 = new Text (60, 45, inf_Regression_Model.getRespVsExplSubtitle());

        r_Student = new Matrix(nDataPoints, 1);
        r_Student = inf_Regression_Model.getR_StudentizedResids();
        
        cooks_D = new Matrix(nDataPoints, 1);
        cooks_D = inf_Regression_Model.getCooksD();
        
        leverage = new double[nDataPoints];
        leverage = inf_Regression_Model.getLeverage();

        fDist_Influence = new FDistribution(1, nDataPoints);
        influenceTrigger = fDist_Influence.getInvLeftTailArea(0.5);
        leverageTrigger = regrModel.getLeverageWarningTrigger();

        checkBoxHeight = 350.0;
        X = inf_Regression_Model.getXVar();
        Y = inf_Regression_Model.getY();
        slope = inf_Regression_Model.getSimpleRegSlope();
        intercept = inf_Regression_Model.getSimpleRegIntercept();
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
        axisLabels = new String[2];
        axisLabels = regrModel.getAxisLabels();
        xAxis.setLabel(axisLabels[0]);
        yAxis.setLabel(axisLabels[1]);  
        doTheGraph();     
        theContainingPane = dragableAnchorPane.getTheContainingPane();       
    }
    
    public void doTheGraph() {      
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(checkBoxRow, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(checkBoxRow, 0.01 * tempWidth);
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

        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        boolean bestFitLineDesired = checkBoxSettings[0];
        boolean outlierPlotIsDesired = checkBoxSettings[1];
        boolean influencePlotIsDesired = checkBoxSettings[2];
        boolean leveragePlotIsDesired = checkBoxSettings[3];
        boolean relativePlotsAreDesired = checkBoxSettings[4];
        
        for (int i = 0; i < nDataPoints; i++) {
            double xx = xAxis.getDisplayPosition(dataArray[i][0]);
            double yy = yAxis.getDisplayPosition(dataArray[i][1]);
            
            //  radius, diameter for centering the dots on point
            gc.setFill(Color.BLACK);
            gc.fillOval(xx - dotRadius , yy - dotRadius , dotDiameter, dotDiameter); 
            
            if (outlierPlotIsDesired) {
                gc.setFill(Color.RED);
                absRStud = Math.abs(r_Student.get(i, 0));
                if (absRStud > outlierTrigger) {
                    outlierRadius = defaultOutlierRadius;
                    if (relativePlotsAreDesired) {
                        outlierRadius = absRStud * defaultOutlierRadius / outlierTrigger;
                    }
                    double outlierCircleDiameter = 2.0 * outlierRadius;
                    gc.fillOval(xx - outlierRadius, yy - outlierRadius, outlierCircleDiameter, outlierCircleDiameter);
                }
            }

            // ******************************************************************
            // Cook, R.  Detection of Influential Observation in Linear         *
            // Regresion (1977).  Technometrics 19(1): 15-18.                   *
            // Hines, R. J., & Hines, W. G. (1995).  Exploring Cook's Statistic *
            // Graphically.  The American Statistician. 49(4): 1995, 389-394    *
            // Letters to the Editor (Obenchain, Cook). (1977). Technometrics   *
            // 19(1): 348-351.                                                  *
            // ******************************************************************     

            if (influencePlotIsDesired) {                
               gc.setLineWidth(3);
               cooksD = cooks_D.get(i, 0);               
                if (cooksD > influenceTrigger) {
                    influencePlusLength = defaultInfluencePlusLength;
                    if (cooksD > 0.5) { gc.setStroke(Color.LIGHTBLUE); }
                    if (cooksD > 1.0) { gc.setStroke(Color.BLUE); }              

                   if (relativePlotsAreDesired) {
                       influencePlusLength = cooksD  * defaultInfluencePlusLength / influenceTrigger;
                   }
                   gc.strokeLine(xx, yy - influencePlusLength, xx, yy + influencePlusLength);
                   gc.strokeLine(xx - influencePlusLength, yy, xx + influencePlusLength, yy);

                   gc.setStroke(Color.BLACK);
                   gc.setLineWidth(1);
                }
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
            }       

            /*********************************************************************
             *  Evaluation of leverage follows Belsley, D., et al.  (2004).      *
             *  Regression Diagnostics: Identifying Influential Data and Sources *
             *  of Collinearity.  Wiley Interscience: New York.  p68             *
             ********************************************************************/
            if (leveragePlotIsDesired) { 
                gc.setLineWidth(3);
                if (leverage[i] > leverageTrigger) {
                    leverageCrossLength = defaultLeverageCrossLength;
                    gc.setStroke(Color.GREEN);        
                    double leverageRatio = leverage[i] / leverageTrigger;   
                    if (relativePlotsAreDesired) {
                       leverageCrossLength = leverageRatio * defaultLeverageCrossLength;
                    }

                    gc.strokeLine(xx - leverageCrossLength, yy - leverageCrossLength, xx + leverageCrossLength, yy + leverageCrossLength);
                    gc.strokeLine(xx - leverageCrossLength, yy + leverageCrossLength, xx + leverageCrossLength, yy - leverageCrossLength);

               }
               gc.setStroke(Color.BLACK);
               gc.setLineWidth(1);
            }

        }   //  End nDataPoints   
        
        if (bestFitLineDesired) {
            double x1 = xAxis.getDisplayPosition(xDataMin);
            double y1 = yAxis.getDisplayPosition(slope * xDataMin + intercept);
            double x2 = xAxis.getDisplayPosition(xDataMax);
            double y2 = yAxis.getDisplayPosition(slope * xDataMax + intercept);
            gc.setLineWidth(2);
            
            if (nDataPoints > 50) { gc.setLineWidth(4); }
            
            gc.setStroke(Color.TOMATO);
            gc.strokeLine(x1, y1, x2, y2);   
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
