/**************************************************
 *            QuadReg_Residuals_View              *
 *                    02/19/24                    *
 *                      15:00                     *
 *************************************************/
package quadraticRegression;

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
import probabilityDistributions.FDistribution;
import splat.Data_Manager;

public class QuadReg_Residuals_View extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    double slope, intercept, outlierCircleRadius, influenceCircleRadius,
           influenceTrigger, leverageTrigger, sqrRootOfTwoOverTwo;
    
    double[] leverage;
    
    //String waldoFile = "QuadReg_Residuals_View";
    String waldoFile = "";
    
    // My classes    
    Data_Manager dm;
    Matrix r_Student, cooks_D;
    FDistribution fInfluence, fDist_Leverage;
    
    //  POJO / FX
    Pane theContainingPane;
    
    public QuadReg_Residuals_View(QuadReg_Model quadReg_Model, QuadReg_Dashboard quadReg_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);   
        dm = quadReg_Model.getDataManager();
        dm.whereIsWaldo(49, waldoFile, "Constructing");        
        X = quadReg_Model.getXVar(); 
        Y = quadReg_Model.getResids();
        slope = 0.0;
        intercept = 0.0;
        
        nCheckBoxes = 4;
        scatterPlotCheckBoxDescr = new String[nCheckBoxes];        
        scatterPlotCheckBoxDescr[0] = " Horizontal Line ";
        scatterPlotCheckBoxDescr[1] = " Outliers ";
        scatterPlotCheckBoxDescr[2] = " Influence ";
        scatterPlotCheckBoxDescr[3] = " Leverage ";
        
        txtTitle1 = new Text(50, 25, " Residual plot ");
        String strTxtTitle2 = "Residuals vs. " + quadReg_Model.getExplanatoryVariable();
        txtTitle2 = new Text (60, 45, strTxtTitle2);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 

        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.quadReg_Model = quadReg_Model;
        this.quadReg_Dashboard = quadReg_Dashboard;
        sqrRootOfTwoOverTwo = Math.sqrt(2.0) / 2.0;        
        radius = 4.0; diameter = 2.0 * radius;  //  For the dots
        outlierCircleRadius = 1.75;  // drawing factor 
        influenceCircleRadius = 3.0; // drawing factor

        r_Student = new Matrix(nDataPoints, 1);
        r_Student = quadReg_Model.getR_StudentizedResids();
        
        cooks_D = new Matrix(nDataPoints, 1);
        cooks_D = quadReg_Model.getCooksD();
        
        leverage = new double[nDataPoints];
        leverage = quadReg_Model.getLeverage();
        
        fInfluence = new FDistribution(1, nDataPoints);
        fDist_Leverage = new FDistribution(1, nDataPoints - 2);
        influenceTrigger = fInfluence.getInvLeftTailArea(0.5);        
        leverageTrigger = 4.0 / nDataPoints;      
        
        checkBoxHeight = 350.0;
        X = quadReg_Model.getXVar();
        Y = quadReg_Model.getResids();
        slope = 0.0;        //  This slope and intercept define the 
        intercept = 0.0;    //  horizontal line in the residual plot
        graphCanvas = new Canvas(initWidth, initHeight);        
        makeTheCheckBoxes();    
        makeItHappen();
    }  
  
    public void makeItHappen() {       
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
        
        for (int chex = 0; chex < nCheckBoxes; chex++) {
            AnchorPane.setLeftAnchor(scatterPlotCheckBoxes[chex], (chex - 1) * tempWidth / 250.0 - 25);
        }

        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        boolean bestFitLineIsDesired = checkBoxSettings[0];
        boolean outlierPlotIsDesired = checkBoxSettings[1];
        boolean influencePlotIsDesired = checkBoxSettings[2];
        boolean leveragePlotDesired = checkBoxSettings[3];
        
        for (int i = 0; i < nDataPoints; i++) {
            double xx = xAxis.getDisplayPosition(dataArray[i][0]);
            double yy = yAxis.getDisplayPosition(dataArray[i][1]);
            
            //  radius, diameter for centering the dots on point
            gc.setFill(Color.GREEN);
            gc.fillOval(xx - sqrRootOfTwoOverTwo * radius, yy - sqrRootOfTwoOverTwo * radius, diameter, diameter); 
            
            if (outlierPlotIsDesired) {
                gc.setFill(Color.RED);
                double outRadius = outlierCircleRadius * Math.abs(r_Student.get(i, 0));
                double outDiameter = 2.5 * outRadius;
                if (outRadius > 3.0) {  //  Arbitrary!
                    gc.fillOval(xx - sqrRootOfTwoOverTwo * outRadius, yy - sqrRootOfTwoOverTwo * outRadius, outDiameter, outDiameter);
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
                gc.setStroke(Color.BLUE);
                gc.setLineWidth(2);
                double cooksD = cooks_D.get(i, 0);
                double influenceCrossLength = 12.5 * cooksD + .5;
                
                if (cooksD > influenceTrigger) {
                    gc.strokeLine(xx, yy - influenceCrossLength, xx, yy + influenceCrossLength);
                    gc.strokeLine(xx - influenceCrossLength, yy, xx + influenceCrossLength, yy);
                }
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
            } 

        /*********************************************************************
         *  Evaluation of leverage follows Belsley, D., et al.  (2004).      *
         *  Regression Diagnostics: Identifying Influential Data and Sources *
         *  of Collinearity.  Wiley Interscience: New York.  p68             *
         ********************************************************************/
            if (leveragePlotDesired) {
                gc.setStroke(Color.GREEN);
                gc.setLineWidth(2);
                double fStatFactor = nDataPoints - 2.0;
                double fStatNumerator = leverage[i] - 1.0 / nDataPoints;
                double fStatDenominator = 1 - leverage[i];
                double fStatLeverage = fStatFactor * fStatNumerator / fStatDenominator;

                if (leverage[i] > leverageTrigger) {
                    double cdfLeverage = fDist_Leverage.getLeftTailArea(fStatLeverage);                   
                    double leverageCrossLength = 7.5 * cdfLeverage;                    
                    gc.strokeLine(xx - leverageCrossLength, yy - leverageCrossLength, xx + leverageCrossLength, yy + leverageCrossLength);
                    gc.strokeLine(xx - leverageCrossLength, yy + leverageCrossLength, xx + leverageCrossLength, yy - leverageCrossLength);
                }                
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
            }             
            
        } 
        
        if (bestFitLineIsDesired) {
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

