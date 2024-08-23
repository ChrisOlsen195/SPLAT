
/**************************************************
 *           OneParam_QuadReg_BestFit_View        *
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
import splat.Data_Manager;

public class OneParam_QuadReg_BestFit_View extends BivariateScale_W_CheckBoxes_View
{
    // POJOs
    double slope_NoInterceptModel, outlierCircleRadius, sqrRootOfTwoOverTwo,
            slope_InterceptModel, intercept_NoInterceptModel;     
    double fqr_Intercept, fqr_LinTerm, fqr_QuadTerm;
    
    String waldoFile = "OneParam_QuadReg_BestFit_View";
    // String waldoFile = "";

    // My classes  
    Data_Manager dm;

    //  POJO / FX
    Pane theContainingPane;

    public OneParam_QuadReg_BestFit_View(OneParam_QuadReg_Model oneParam_QuadReg_Model, OneParam_QuadReg_Dashboard oneParam_QuadReg_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        System.out.println("43 OneParam_QuadReg_Best_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        dm = oneParam_QuadReg_Model.getDataManager();
        dm.whereIsWaldo(47, waldoFile, "Constructing");
        // X, Y are matrixes -- must stay so for later plotting
        X = oneParam_QuadReg_Model.getXVar();
        Y = oneParam_QuadReg_Model.getY();
        
        this.noInt_QuadReg_Model = oneParam_QuadReg_Model ;
        this.noInt_QuadReg_Dashboard = oneParam_QuadReg_Dashboard;
        sqrRootOfTwoOverTwo = Math.sqrt(2.0) / 2.0;
        nCheckBoxes = 2;
        scatterPlotCheckBoxDescr = new String[nCheckBoxes];
        scatterPlotCheckBoxDescr[0] = " Fit One-parameter ";
        scatterPlotCheckBoxDescr[1] = " Fit Full quadratic ";
        
        fqr_Intercept = oneParam_QuadReg_Model.getFQR_Intercept();
        fqr_LinTerm = oneParam_QuadReg_Model.getFQR_LinTerm();
        fqr_QuadTerm = oneParam_QuadReg_Model.getFQR_QuadTerm();
        
        txtTitle1 = new Text(50, 25, " Scatterplot ");
        txtTitle2 = new Text (60, 45, oneParam_QuadReg_Model.getRespVsExplSubtitle());
        
        radius = 4.0; diameter = 2.0 * radius;  //  For the dots
        outlierCircleRadius = 1.75;  // drawing factor 
        
        checkBoxHeight = 350.0;
        intercept_NoInterceptModel = oneParam_QuadReg_Model.get_Intercept_InterceptModel();
        slope_NoInterceptModel = oneParam_QuadReg_Model.get_SlopeNoInterceptModel();
        slope_InterceptModel = oneParam_QuadReg_Model.get_SlopeInterceptModel();
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
    
    public void doTheGraph() {      
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
            AnchorPane.setLeftAnchor(scatterPlotCheckBoxes[chex], (chex - 1) * tempWidth / 250.0 - 50);
        }

        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        boolean bestFitQuadDesired = checkBoxSettings[0];
        boolean interceptPlotDesired = checkBoxSettings[1];
        
        for (int i = 0; i < nDataPoints; i++) {
            double xx = xAxis.getDisplayPosition(dataArray[i][0]);
            double yy = yAxis.getDisplayPosition(dataArray[i][1]);
            
            //  radius, diameter for centering the dots on point
            gc.setFill(Color.BLACK);
            gc.fillOval(xx - sqrRootOfTwoOverTwo * radius , yy - sqrRootOfTwoOverTwo * radius , diameter, diameter); 
        
        }   //  End nDataPoints   
      
        if (bestFitQuadDesired) {
            double tempLB = xAxis.getLB();  // Lower bound
            double tempUB = xAxis.getUB();  // Upper bound
            double dx = (tempUB - tempLB) / 600.0;

            for (double xyWexy = tempLB; xyWexy < tempUB; xyWexy = xyWexy + dx) {
                double xyWexyPlus_dx = xyWexy + dx;
                double xxAxis1 = xAxis.getDisplayPosition(xyWexy);
                double quadYAtxxAxis1 = yAxis.getDisplayPosition(noInt_QuadReg_Model.getQuadCoefficient() * xyWexy * xyWexy);
                double xxAxis2 = xAxis.getDisplayPosition(xyWexyPlus_dx);
                double quadYAtxxAxis2 = yAxis.getDisplayPosition(noInt_QuadReg_Model.getQuadCoefficient() * xyWexyPlus_dx * xyWexyPlus_dx);
                gc.setLineWidth(2);
                
                if (nDataPoints > 50) { gc.setLineWidth(4); }
                
                gc.setStroke(Color.TOMATO);
                gc.strokeLine(xxAxis1, quadYAtxxAxis1, xxAxis2, quadYAtxxAxis2);  
            } 

        }

        if (interceptPlotDesired == true) {
            double tempLB = xAxis.getLB();  // Lower bound
            double tempUB = xAxis.getUB();  // Upper bound
            double dx = (tempUB - tempLB) / 600.0;

            for (double xyWexy = tempLB; xyWexy < tempUB; xyWexy = xyWexy + dx) {
                double xyWexyPlus_dx = xyWexy + dx;
                double xxAxis1 = xAxis.getDisplayPosition(xyWexy);
                double quadYAtxxAxis1 = yAxis.getDisplayPosition(calculateFullQuadReg_YValue(xyWexy));
                double xxAxis2 = xAxis.getDisplayPosition(xyWexyPlus_dx);
                double quadYAtxxAxis2 = yAxis.getDisplayPosition(calculateFullQuadReg_YValue(xyWexyPlus_dx));
                gc.setLineWidth(2);
                
                if (nDataPoints > 50) { gc.setLineWidth(4); }
                
                gc.setStroke(Color.RED);
                gc.strokeLine(xxAxis1, quadYAtxxAxis1, xxAxis2, quadYAtxxAxis2);  
            }   //  End Scale

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
        
    }   // end doTheGraph
    
    private double calculateFullQuadReg_YValue(double xValue) {
        double yValue = fqr_Intercept;
               yValue += fqr_LinTerm * xValue;
               yValue += fqr_QuadTerm * xValue * xValue;
        return yValue;
    }
    
   public Pane getTheContainingPane() { return theContainingPane; }
}


