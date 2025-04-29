/**************************************************
 *       TwoMeans_Power_VsEffectSizeView          *
 *                  01/15/25                      *
 *                    21:00                       *
 *************************************************/
package power_twomeans;

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
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;

public class IndepMeans_Power_VsEffectSizeView extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int sampleSize_1, sampleSize_2;

    double /*xMin, xMax,*/ yMin, yMax, nullMu, /*altMu,*/ nullDiffMeans, /*altDiffMeans,*/ 
           /*nullSigma_1, nullSigma_2, effectSize, alpha,*/ standErrDiffMeans;

    //  FX 
    Pane theContainingPane;
    Text title1Text, title2Text;

    //  My objects
    IndepMeans_Power_Model indepMeans_Power_Model;   

    public IndepMeans_Power_VsEffectSizeView(IndepMeans_Power_Model indepMeans_Power_Model,
                         IndepMeans_Power_Dashboard indepMeans_Power_Dashboard,
                         double placeHoriz, double placeVert,
                         double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff == true) {
            System.out.println("50 *** IndepMeans_Power_VsEffectSizeView, Constructing");
        }
        this.indepMeans_Power_Model = indepMeans_Power_Model;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        indepMeans_Power_Model.restoreNullValues();
        sampleSize_1 = indepMeans_Power_Model.getSampleSize_1();
        sampleSize_2 = indepMeans_Power_Model.getSampleSize_2();
        nullDiffMeans = indepMeans_Power_Model.getNullDiffMeans();
        //altDiffMeans = indepMeans_Power_Model.getAltDiffMeans();
        //nullSigma_1 = indepMeans_Power_Model.getNullSigma_1();
        //nullSigma_2 = indepMeans_Power_Model.getNullSigma_2();
        //effectSize = indepMeans_Power_Model.getEffectSize();
        alpha = indepMeans_Power_Model.getAlpha();
        //effectSize = indepMeans_Power_Model.getEffectSize();
        standErrDiffMeans = indepMeans_Power_Model.getStandErrDiffMeans();
        fromHere = nullDiffMeans -3.25 * standErrDiffMeans; 
        toThere = nullDiffMeans + 3.25 * standErrDiffMeans; 
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
        title1Text = new Text("Power vs. Effect Size");
        title2Text = new Text(String.format("     N1 = %3d,  N2 = %3d, alpha = %4.2f", sampleSize_1, sampleSize_2, alpha));
        title1Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,24));
        title2Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,18));         
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
                           .addAll(title1Text, title2Text, 
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
        //xMin = xMax = xGraphLeft;
        xRange = xGraphRight - xGraphLeft;        
        yRange = yMax = getInitialYMax();
        // These constants control the rate of axis scale change when dragging
        deltaX = 0.005 * xRange; deltaY = 0.005 * yRange;   
    }
    
    public void setIntervalOfInterest(double startHere, double endHere)  {
        fromHere = startHere; toThere = endHere;
        delta = (endHere - startHere) / bigDelta * NUMBER_OF_DXs; 
    }
    
    public double getInitialYMax() { return 1.025; }
   
    public void doTheGraph() {      
        double xx0, yy0, xx1, yy1, power;     
        double text1Width = title1Text.getLayoutBounds().getWidth();
        double text2Width = title2Text.getLayoutBounds().getWidth();
        double dragWidth = dragableAnchorPane.getWidth();
        double dragHeight = dragableAnchorPane.getHeight();
        
        double txt1Edge = (dragWidth - text1Width) / (2 * dragWidth);
        double txt2Edge = (dragWidth - text2Width) / (2 * dragWidth);
        
        AnchorPane.setTopAnchor(title1Text, 0.0 * dragHeight);
        AnchorPane.setLeftAnchor(title1Text, txt1Edge * dragWidth);
        AnchorPane.setRightAnchor(title1Text, txt1Edge * dragWidth);
        AnchorPane.setBottomAnchor(title1Text, 0.1 * dragHeight);
                
        AnchorPane.setTopAnchor(title2Text, 0.1 * dragHeight);
        AnchorPane.setLeftAnchor(title2Text, txt2Edge * dragWidth);
        AnchorPane.setRightAnchor(title2Text, txt2Edge * dragWidth);
        AnchorPane.setBottomAnchor(title2Text, 0.2 * dragHeight);
    
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
        
        // Set initial yValue, and get the power there
        xx0 = xGraphLeft; 
        indepMeans_Power_Model.setAltMuDiff(xx0 + nullMu); // xx0 is effect size
        power = indepMeans_Power_Model.calculatePower();
        yy0 = power;
        
        for (double x = xGraphLeft; x <= xGraphRight; x += delta) {
            xx1 = x; 
            indepMeans_Power_Model.setAltMuDiff(xx1 + nullMu); // xx1 is effect size
            power = indepMeans_Power_Model.calculatePower();
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
    
   public Pane getTheContainingPane() {  return theContainingPane; }
}


