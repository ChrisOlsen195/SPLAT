/**************************************************
 *        LinReg_Power_VsEffectSizeView           *
 *                 05/18/26                       *
 *                    03:00                       *
 *************************************************/
package power_LinReg;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.layout.AnchorPane;
import superClasses.*;
import genericClasses.*;

public class LinReg_Power_VsEffectSizeView extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int sampleSize;

    double yMin, yMax, nullBeta, seBeta, dbl_daN;

    //  FX 
    Pane theContainingPane;
    Text txtTitle_1, txtTitle_2;

    //  My objects
    LinReg_Power_Model linReg_Power_Model;
   
    public LinReg_Power_VsEffectSizeView(LinReg_Power_Model linReg_Power_Model,
                         LinReg_Power_Dashboard linReg_Power_Dashboard,
                         double placeHoriz, double placeVert,
                         double withThisWidth, double withThisHeight) {    
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff) {
            System.out.println("41 *** OneMean_Power_VsEffectSizeView, Constructing");
        }
        this.linReg_Power_Model = linReg_Power_Model;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm(); 
        linReg_Power_Model.restoreNullValues();
        sampleSize = linReg_Power_Model.getSampleSize();
        dbl_daN = sampleSize;
        nullBeta = 0.0;
        seBeta = linReg_Power_Model.getStErr_Beta();
        fromHere = -3.25 * seBeta; 
        toThere = 3.25 * seBeta; 
        alpha = linReg_Power_Model.getAlpha();   
        makeItHappen();
    }  
    
    public void makeItHappen() {   
        theContainingPane = new Pane();
        graphCanvas = new Canvas(initWidth, initHeight);
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
        initializeGraphParameters();
        setUpUI();       
        setUpGridPane();
        setHandlers();
        doTheGraph();           
        theContainingPane = dragableAnchorPane.getTheContainingPane();     
    }
    
    public void setUpUI() {
        txtTitle_1 = new Text("Power vs. Effect Size");
        txtTitle_2 = new Text (String.format("Sample size = %3d, alpha = %3.2f", sampleSize, alpha));
        txtTitle_1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle_2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15));         
    }
    
    private void setUpGridPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(txtTitle_1, txtTitle_2, 
                                   xAxis, yAxis, graphCanvas);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void initializeGraphParameters() {
        xAxis = new JustAnAxis(fromHere, toThere);
        xAxis.setSide(Side.BOTTOM);       

        prepareTheDensityAxis();
         
        yAxis = new JustAnAxis(yMin, yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.forceLowScaleEndToBe(0.0);  
        yAxis.setBounds(0.0, yMax);
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
        bigDelta = (xGraphRight - xGraphLeft) / NUMBER_OF_DXs;
        delta = bigDelta;
        xRange = xGraphRight - xGraphLeft;        
        yRange = yMax = getInitialYMax();
        // These constants control the rate of axis scale change when dragging
        deltaX = 0.005 * xRange; deltaY = 0.005 * yRange;   
    }
    
    public void setIntervalOfInterest(double startHere, double endHere) {
        fromHere = startHere; toThere = endHere;
        delta = (endHere - startHere) / bigDelta * NUMBER_OF_DXs; 
    }
    
    public double getInitialYMax() { return 1.025; }
   
    public void doTheGraph() {      
        double xx0, yy0, xx1, yy1, power;     
        double text1Width = txtTitle_1.getLayoutBounds().getWidth();
        double text2Width = txtTitle_2.getLayoutBounds().getWidth();
        double dragWidth = dragableAnchorPane.getWidth();
        double dragHeight = dragableAnchorPane.getHeight();
        
        double txt1Edge = (dragWidth - text1Width) / (2 * dragWidth);
        double txt2Edge = (dragWidth - text2Width) / (2 * dragWidth);
        
        AnchorPane.setTopAnchor(txtTitle_1, 0.0 * dragHeight);
        AnchorPane.setLeftAnchor(txtTitle_1, txt1Edge * dragWidth);
        AnchorPane.setRightAnchor(txtTitle_1, txt1Edge * dragWidth);
        AnchorPane.setBottomAnchor(txtTitle_1, 0.1 * dragHeight);
                
        AnchorPane.setTopAnchor(txtTitle_2, 0.1 * dragHeight);
        AnchorPane.setLeftAnchor(txtTitle_2, txt2Edge * dragWidth);
        AnchorPane.setRightAnchor(txtTitle_2, txt2Edge * dragWidth);
        AnchorPane.setBottomAnchor(txtTitle_2, 0.2 * dragHeight);
    
        AnchorPane.setTopAnchor(xAxis, 0.9 * dragHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * dragWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * dragWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * dragHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * dragHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * dragWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * dragWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.1 * dragHeight);
        
        AnchorPane.setTopAnchor(graphCanvas, 0.2 * dragHeight);
        AnchorPane.setLeftAnchor(graphCanvas, 0.1 * dragWidth);
        AnchorPane.setRightAnchor(graphCanvas, 0.0 * dragWidth);
        AnchorPane.setBottomAnchor(graphCanvas, 0.1 * dragHeight);
        
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        gc.setLineWidth(2);
        gc.setStroke(Color.BLACK);  
        
        //  Set initial display interval
        yStart = yAxis.getDisplayPosition(0.00);
        yStop = yAxis.getDisplayPosition(1.00);       
        
        linReg_Power_Model.restoreNullValues();
        
        // Set initial yValue, and get the power there
        xx0 = xGraphLeft; 
        linReg_Power_Model.setAltBeta(xx0 + nullBeta); // xx0 is effect size
        power = linReg_Power_Model.calculatePower();
        yy0 = power;
        // Get needed current values for restoration        
        double forRestorationAltBeta = linReg_Power_Model.getAltBeta();   //  Move out of loop?
        double forRestorationAltStErr = linReg_Power_Model.getStErr_AltBeta();    //  Move out of loop?
        
        for (double x = xGraphLeft; x <= xGraphRight; x += delta) {
            xx1 = x; 
            linReg_Power_Model.setAltBeta(xx1 + nullBeta); // xx1 is effect size
            double tempEffectSize = xx1; 
            double tempAltBeta = tempEffectSize + linReg_Power_Model.getNullBeta();        
            double tempStErrBeta = linReg_Power_Model.getStErr_Beta();
            double tempStErrAlt = tempStErrBeta;
            linReg_Power_Model.setAltBeta(tempStErrBeta);
            linReg_Power_Model.setStErr_AltBeta(tempStErrBeta);     
            linReg_Power_Model.setEffectSize(xx1);   
            power = linReg_Power_Model.calculatePower();
            // restore to prior values

            yy1 = power;            
            xStart = xAxis.getDisplayPosition(xx1); 
            yStart = yAxis.getDisplayPosition(yy0); 
            xStop = xAxis.getDisplayPosition(xx1);
            yStop = yAxis.getDisplayPosition(yy1);
            gc.setLineWidth(2);
            gc.setStroke(Color.BLUE);
            gc.strokeLine(xStart, yStart, xStop, yStop);            
            xx0 = xx1; yy0 = yy1;   //  Next start point for line segment
        }
        linReg_Power_Model.setAltBeta(forRestorationAltBeta);
        linReg_Power_Model.setStErr_AltBeta(forRestorationAltStErr);
    }    
    
   public Pane getTheContainingPane() { return theContainingPane; }
}


