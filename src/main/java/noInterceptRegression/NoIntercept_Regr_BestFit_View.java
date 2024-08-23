 /*************************************************
 *            NoIntercept_Regr_BestFit_View       *
 *                    02/19/24                    *
 *                      12:00                     *
 *************************************************/
package noInterceptRegression;

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

public class NoIntercept_Regr_BestFit_View extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    double slope_NoInterceptModel, outlierCircleRadius, sqrRootOfTwoOverTwo,
           slope_InterceptModel, intercept_InterceptModel;  

    //String waldoFile = "NoIntercept_Regr_BestFit_View";
    String waldoFile = "";

    // My classes    
    Data_Manager dm;

    //  POJO / FX
    Pane theContainingPane;

    public NoIntercept_Regr_BestFit_View(NoIntercept_Regr_Model noInt_Regr_Model, NoIntercept_Regr_Dashboard noInt_Regr_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        dm = noInt_Regr_Model.getDataManager(); 
        //dm.whereIsWaldo(41, waldoFile, "Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 

        // X, Y are matrixes -- must stay so for later plotting
        X = noInt_Regr_Model.getXVar();
        Y = noInt_Regr_Model.getY();        
        sqrRootOfTwoOverTwo = Math.sqrt(2.0) / 2.0;
        nCheckBoxes = 2;
        scatterPlotCheckBoxDescr = new String[nCheckBoxes];
        scatterPlotCheckBoxDescr[0] = " No-intercept Model ";
        scatterPlotCheckBoxDescr[1] = " Intercept Model ";
        
        txtTitle1 = new Text(50, 25, " Scatterplot ");
        txtTitle2 = new Text (60, 45, noInt_Regr_Model.getRespVsExplSubtitle());
        
        radius = 4.0; diameter = 2.0 * radius;  //  For the dots
        outlierCircleRadius = 1.75;  // drawing factor 
        
        checkBoxHeight = 350.0;
        X = noInt_Regr_Model.getXVar();
        Y = noInt_Regr_Model.getY();
        intercept_InterceptModel = noInt_Regr_Model.get_Intercept_InterceptModel();
        slope_NoInterceptModel = noInt_Regr_Model.get_SlopeNoInterceptModel();
        slope_InterceptModel = noInt_Regr_Model.get_SlopeInterceptModel();
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
            AnchorPane.setLeftAnchor(scatterPlotCheckBoxes[chex], (chex - 1) * tempWidth / 250.0 - 150);
        }

        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        boolean bestFitLineDesired = checkBoxSettings[0];
        boolean interceptPlotDesired = checkBoxSettings[1];
        
        for (int i = 0; i < nDataPoints; i++) {
            double xx = xAxis.getDisplayPosition(dataArray[i][0]);
            double yy = yAxis.getDisplayPosition(dataArray[i][1]);
            
            //  radius, diameter for centering the dots on point
            gc.setFill(Color.BLACK);
            gc.fillOval(xx - sqrRootOfTwoOverTwo * radius , yy - sqrRootOfTwoOverTwo * radius , diameter, diameter);         
        }   //  End nDataPoints   
        
        if (bestFitLineDesired) {
            double x1 = xAxis.getDisplayPosition(xDataMin);
            double y1 = yAxis.getDisplayPosition(slope_NoInterceptModel * xDataMin);
            double x2 = xAxis.getDisplayPosition(xDataMax);
            double y2 = yAxis.getDisplayPosition(slope_NoInterceptModel * xDataMax);
            gc.setLineWidth(2);
            gc.setStroke(Color.RED);
            gc.strokeLine(x1, y1, x2, y2);  
        }

        if (interceptPlotDesired) {
            double x1 = xAxis.getDisplayPosition(xDataMin);
            double temp1 = intercept_InterceptModel + slope_InterceptModel * xDataMin;
            double y1 = yAxis.getDisplayPosition(temp1);
            double x2 = xAxis.getDisplayPosition(xDataMax);
            double temp2 = intercept_InterceptModel + slope_InterceptModel * xDataMax;
            double y2 = yAxis.getDisplayPosition(temp2);
            gc.setLineWidth(2);
            
            if (nDataPoints > 50) { gc.setLineWidth(4); }
            
            gc.setStroke(Color.GREEN);
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

