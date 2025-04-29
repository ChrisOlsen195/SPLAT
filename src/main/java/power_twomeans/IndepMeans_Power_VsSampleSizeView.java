/**************************************************
 *       IndepMeans_Power_VsSampleSizeView        *
 *                  01/15/25                      *
 *                    21:00                       *
 *************************************************/
package power_twomeans;

import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import superClasses.*;
import genericClasses.*;
import javafx.event.EventHandler;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

public class IndepMeans_Power_VsSampleSizeView extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int sampleSize_1, sampleSize_2, maxSampleSize;

    double xMax, yMin, yMax, effectSize, dbl_daN1, dbl_daN2, lowN1, lowN2;
    
    float redColor, greenColor, blueColor;
    float[][] power;
    
    //  FX
    Color powerColor;
    Pane theContainingPane;
    Text title1Text, title2Text;    
   
    // My classes
    IndepMeans_Power_Model indepMeans_Power_Model;    // parent is NOT the Dashboard!!

    public IndepMeans_Power_VsSampleSizeView(IndepMeans_Power_Model indepMeans_Power_Model,
                         IndepMeans_Power_Dashboard singleMean_Z_Dashboard,
                         double placeHoriz, double placeVert,
                         double withThisWidth, double withThisHeight) {        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff == true) {
            System.out.println("55 *** IndepMeans_Power_VsSampleSizeView, Constructing");
        }
        this.indepMeans_Power_Model = indepMeans_Power_Model;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        sampleSize_1 = indepMeans_Power_Model.getSampleSize_1();
        sampleSize_2 = indepMeans_Power_Model.getSampleSize_2();

        effectSize = indepMeans_Power_Model.getEffectSize();
        alpha = indepMeans_Power_Model.getAlpha();

        maxSampleSize = Math.max(sampleSize_1, sampleSize_2) + 10;
        fromHere = 2.0; toThere = maxSampleSize;
        power = new float[maxSampleSize + 1][maxSampleSize + 1];
        makeItHappen();
    }  
    
    public void makeItHappen() {     
        theContainingPane = new Pane();
        graphCanvas = new Canvas(initWidth, initHeight);
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});        
        graphCanvas.setOnMousePressed(graphCanvasMouseHandler);        
        graphCanvas.setOnMouseReleased(graphCanvasMouseHandler);
        
        initializeGraphParameters();
        setUpUI();       
        setUpGridPane();
        setHandlers();
        doThePowerCalculations();        
        doTheGraph();   
        theContainingPane = dragableAnchorPane.getTheContainingPane();     
    }
    
    public void setUpUI() {
        title1Text = new Text("Power vs. Sample sizes");
        title2Text = new Text (String.format("      Minimum Effect size = %4.2f, Alpha = %3.2f", effectSize, alpha));
        title1Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,30));
        title2Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,24));         
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
        xAxis.forceLowScaleEndToBe(0.0); 
        xAxis.setBounds(-0.2, xMax);
        xAxis.setLabel("Sample size #1");
         
        yAxis = new JustAnAxis(fromHere, toThere);
        yAxis.setSide(Side.LEFT);
        yAxis.forceLowScaleEndToBe(0.0);  
        yAxis.setBounds(-0.2, yMax);
        yAxis.setLabel("Sample size #2");

        newX_Lower = fromHere; newX_Upper = toThere;
        newY_Lower = fromHere; newY_Upper = toThere;
        
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );     
    }
    
    public void setIntervalOfInterest(double startHere, double endHere)  {
        fromHere = startHere; toThere = endHere;
    }
    
    public void doThePowerCalculations() {
        for (int daN1 = 2; daN1 < maxSampleSize; daN1++)
        {
            for (int daN2 = 2; daN2 < maxSampleSize; daN2++) {
                dbl_daN1 = daN1; dbl_daN2 = daN2;
                indepMeans_Power_Model.setSampleSize_1(daN1);
                indepMeans_Power_Model.setSampleSize_2(daN2);
                power[daN1][daN2] = (float)indepMeans_Power_Model.calculatePower();           
            }
        }
        indepMeans_Power_Model.restoreNullValues();
    }
   
    @Override
    public void doTheGraph() {            
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
        yStart = yAxis.getDisplayPosition(0.0);
        yStop = yAxis.getDisplayPosition(1.0);       
        
        indepMeans_Power_Model.restoreNullValues();
        
        for (int daN1 = 2; daN1 < maxSampleSize; daN1++) {
            for (int daN2 = 2; daN2 < maxSampleSize; daN2++) {
                dbl_daN1 = daN1; dbl_daN2 = daN2;
                float daPower = power[daN1][daN2];  
                xStop = xAxis.getDisplayPosition(dbl_daN1);
                yStop = yAxis.getDisplayPosition(dbl_daN2);   
                gc.setLineWidth(2);
                float fl_power = (float)daPower;
                
                redColor = 1.0f;
                greenColor = 0.f;
                blueColor = 0.0f;
                
                greenColor = (float)Math.sqrt(power[daN1][daN2] - 0.25);
                
                if ((daPower > 0.5) && (daPower <= 0.8)) { 
                    redColor = -10.0f / 3.0f * fl_power + 8.0f / 3.0f;
                } 
                
                if (daPower > 0.8) { redColor = 0.0f; }
                
                if (redColor >= 1.0 ) {redColor = 0.999f; }
                if (blueColor >= 1.0 ) {blueColor = 0.999f; }
                if (greenColor >= 1.0 ) {greenColor = 0.999f; }
                
                powerColor = Color.color(redColor, greenColor, blueColor);
                gc.setStroke(powerColor); 
                gc.setFill(powerColor); 
                gc.strokeOval(xStop - 1., yStop + 1., 5, 5);            
            }
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
    
   public Pane getTheContainingPane() { return theContainingPane; }
   
    EventHandler<MouseEvent> graphCanvasMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent)  {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                title2Text.setText(String.format("      Minimum Effect size = %4.2f, Alpha = %3.2f", effectSize, alpha));
                mouseEvent.consume();
            }
            
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) { 
                double minPythag = 999.999;
                System.out.println(" x = " + (double)mouseEvent.getX());
                System.out.println(" y = " + (double)mouseEvent.getY());
                
                for (int daMouseN1 = 2; daMouseN1 < maxSampleSize; daMouseN1++) {
                    for (int daMouseN2 = 2; daMouseN2 < maxSampleSize; daMouseN2++) {
                        dbl_daN1 = daMouseN1;
                        dbl_daN2 = daMouseN2;
                        xStop = xAxis.getDisplayPosition(dbl_daN1);
                        yStop = yAxis.getDisplayPosition(dbl_daN2);   
                        double distX = Math.abs(xStop - (double)mouseEvent.getX());
                        double distY = Math.abs(yStop - (double)mouseEvent.getY());
                        double pyThag = Math.sqrt(distX * distX + distY * distY);
                        if (pyThag < minPythag) {
                            lowN1 = dbl_daN1;
                            lowN2 = dbl_daN2;
                            minPythag = Math.min(minPythag, pyThag);
                        }
                    }
                }
                title2Text.setText(String.format("          N1 = %3d,  N2 = %3d, Power = %4.3f", (int)lowN1, (int)lowN2, power[(int)lowN1][(int)lowN2]));
                mouseEvent.consume();
            }
        }
    };   
}

