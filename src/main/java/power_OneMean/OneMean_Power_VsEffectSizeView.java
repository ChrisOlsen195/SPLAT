/**************************************************
 *       OneMean_Power_VsEffectSizeView           *
 *                  05/29/24                      *
 *                    15:00                       *
 *************************************************/
package power_OneMean;

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

public class OneMean_Power_VsEffectSizeView extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    int sampleSize;

    double yMin, yMax, nullMean, nullSigma;

    //  FX 
    Pane theContainingPane;
    Text txtTitle_1, txtTitle_2;

    //  My objects
    OneMean_Power_Model oneMean_Power_Model;
   
    public OneMean_Power_VsEffectSizeView(OneMean_Power_Model oneMean_Power_Model,
                         OneMean_Power_Dashboard oneMean_Power_Dashboard,
                         double placeHoriz, double placeVert,
                         double withThisWidth, double withThisHeight) {    
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        //System.out.println("\n45 OneMean_Power_VsEffectSizeView, Constructing");
        this.oneMean_Power_Model = oneMean_Power_Model;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm(); 
        oneMean_Power_Model.restoreNullValues();
        sampleSize = oneMean_Power_Model.getSampleSize();
        nullMean = oneMean_Power_Model.getNullMu();
        nullSigma = oneMean_Power_Model.getNullSigma();
        fromHere = -3.25 * nullSigma; 
        toThere = 3.25 * nullSigma; 
        alpha = oneMean_Power_Model.getAlpha();   
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
        
        oneMean_Power_Model.restoreNullValues();
        
        // Set initial yValue, and get the power there
        xx0 = xGraphLeft; 
        oneMean_Power_Model.setAltMu(xx0 + nullMean); // xx0 is effect size
        power = oneMean_Power_Model.calculatePower();
        yy0 = power;
        
        for (double x = xGraphLeft; x <= xGraphRight; x += delta) {
            xx1 = x; 
            oneMean_Power_Model.setAltMu(xx1 + nullMean); // xx1 is effect size
            power = oneMean_Power_Model.calculatePower();
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
    
   public Pane getTheContainingPane() { return theContainingPane; }
}


