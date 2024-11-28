/**************************************************
 *             Regression_JointCI_View            *
 *                    11/27/24                    *
 *                      12:00                     *
 *************************************************/
/**************************************************
 *  JointCI method is from Montgomery, Peck,      *
 *  Vining, Intro to Linear Regression Analysis,  *
 *  5th ed, p101ff.                               *
 *************************************************/
package simpleRegression;

import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import superClasses.BivariateScale_W_CheckBoxes_View;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import probabilityDistributions.*;
import genericClasses.Point_2D;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import splat.Data_Manager;

public class Regression_JointCI_View extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    double b0_Hat, b1_Hat, sumX, sumX2,twiceMSResid, start_b0_Axis, 
           stop_b0_Axis, start_b1_Axis, stop_b1_Axis, fToCompare,
           dotRadius = 0.5, dotDiameter = 1.0;

    // Make empty if no-print
    String waldoFile = "Regression_JointCI_View";
    //String waldoFile = "";

    // My classes  
    Data_Manager dm;
    FDistribution fDist_JointCI;
    Point_2D init_ScaleLimits_0, init_ScaleLimits_1;
    
    //  POJO / FX
    Pane theContainingPane;

    public Regression_JointCI_View(Inf_Regression_Model inf_Regression_Model, Regression_Dashboard regression_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        dm = inf_Regression_Model.getDataManager();
        dm.whereIsWaldo(53, waldoFile, "Constructing"); 
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        
        this.regrModel = inf_Regression_Model;
        this.regrDashboard = regression_Dashboard;
        nDataPoints = regrModel.getNRows(); 
        
        txtTitle1 = new Text(40, 25, " 95% Joint Confidence Interval");
        txtTitle2 = new Text (60, 45, inf_Regression_Model.getRespVsExplSubtitle());
        checkBoxHeight = 350.0;
        
        fDist_JointCI = new FDistribution(2, nDataPoints - 2);
        fToCompare = fDist_JointCI.getInvRightTailArea(0.05);
        b0_Hat = inf_Regression_Model.getIntercept();
        b1_Hat = inf_Regression_Model.getSlope();
        init_ScaleLimits_0 = inf_Regression_Model.get_InitialScaleLimits_b0();
        init_ScaleLimits_1 = inf_Regression_Model.get_InitialScaleLimits_b1();
        
        start_b0_Axis = init_ScaleLimits_0.getFirstValue(); 
        stop_b0_Axis = init_ScaleLimits_0.getSecondValue();
        start_b1_Axis = init_ScaleLimits_1.getFirstValue(); 
        stop_b1_Axis = init_ScaleLimits_1.getSecondValue();
        
        X = inf_Regression_Model.getX();
        sumX = 0.0;
        sumX2 = 0.0;
        
        for (int ithX = 0; ithX < nDataPoints; ithX++) {
            double temp_x = X.get(ithX, 1);
            sumX += temp_x;
            sumX2 += (temp_x * temp_x);   
        }

        twiceMSResid = 2.0 * inf_Regression_Model.getMSRes();        
        graphCanvas = new Canvas(initWidth, initHeight);     
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

        xAxis.setLabel("Intercept");
        yAxis.setLabel("Slope");
        
        doTheGraph();     
        theContainingPane = dragableAnchorPane.getTheContainingPane();       
    }
    
    public void initializeGraphParameters() {  
        xAxis = new genericClasses.JustAnAxis(start_b0_Axis, stop_b0_Axis);
        xAxis.setSide(Side.BOTTOM); 
        yAxis = new genericClasses.JustAnAxis(start_b1_Axis, stop_b1_Axis);
        yAxis.setSide(Side.LEFT);
        newX_Lower = start_b0_Axis; newX_Upper = stop_b0_Axis;
        newY_Lower = start_b1_Axis; newY_Upper = stop_b1_Axis;

        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );            
    }
    
    public void doTheGraph() {      
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
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
        
        xRange = newX_Upper - newX_Lower;
        deltaX = 0.001 * xRange;
        yRange = newY_Upper - newY_Lower;
        deltaY = 0.001 * yRange;   
        
        for (double b1_YCoord = newY_Lower; b1_YCoord < newY_Upper; b1_YCoord += deltaY ) {            
            for (double b0_XCoord = newX_Lower; b0_XCoord < newX_Upper; b0_XCoord += deltaX) {
                double bin_0 = b0_Hat - b0_XCoord;
                double bin_1 = b1_Hat - b1_YCoord;
                double xDot = b0_XCoord;
                double yDot = b1_YCoord;
                
                double temp_1 = nDataPoints * bin_0 * bin_0;
                double temp_2 = 2.0 * sumX * bin_0 * bin_1;
                double temp_3 = sumX2 * bin_1 * bin_1;
                
                double ratio = (temp_1 + temp_2 + temp_3) / twiceMSResid;                
                if (ratio < fToCompare) {   //  Point inside the ellipse?
                    double x1 = xAxis.getDisplayPosition(xDot);
                    double y1 = yAxis.getDisplayPosition(yDot);
                    gc.setFill(Color.GREEN);
                    gc.fillOval(x1 - dotRadius , y1 - dotRadius , dotDiameter, dotDiameter); 
                }
            }
        }
        
        theContainingPane.requestFocus();
        theContainingPane.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                //System.out.println("Doing it -- Best Fit");
                WritableImage writableImage = theContainingPane.snapshot(new SnapshotParameters(), null);
                ImageView iv = new ImageView(writableImage);
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.put(DataFormat.IMAGE, writableImage);
                clipboard.setContent(content);
            }
        }));
        
    }
    
   public Pane getTheContainingPane() { return theContainingPane; }
}

