/**************************************************
 *        OneMean_Power_VsSampleSizeView          *
 *                  01/15/25                      *
 *                    21:00                       *
 *************************************************/
package power_OneMean;

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
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;

public class OneMean_Power_VsSampleSizeView extends BivariateScale_W_CheckBoxes_View
{
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    double yMin, yMax, effectSize;

    //  FX 
    Pane theContainingPane;
    Text txtTitle_1, txtTitle_2;    
   
    // My classes
    OneMean_Power_Model oneMean_Power_Model;    // parent is NOT the Dashboard!!

    public OneMean_Power_VsSampleSizeView(OneMean_Power_Model oneMean_Power_Model,
                         OneMean_Power_Dashboard oneMean_Power_Dashboard,
                         double placeHoriz, double placeVert,
                         double withThisWidth, double withThisHeight) {        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff == true) {
            System.out.println("48 *** OneMean_Power_VsSampleSizeView, Constructing");
        }
        this.oneMean_Power_Model = oneMean_Power_Model;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();                
        alpha = oneMean_Power_Model.getAlpha();
        effectSize = oneMean_Power_Model.getEffectSize();
        fromHere = 1.0; toThere = 50.0;
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
        txtTitle_1 = new Text("Power vs. Sample size");
        txtTitle_2 = new Text (String.format("Effect size = %4.2f, Alpha = %3.2f", effectSize, alpha));
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
        yAxis.setBounds(-0.2, yMax);
        newX_Lower = fromHere; newX_Upper = toThere;
        newY_Lower = yMin; newY_Upper = yMax;
        
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );            
    }

    private void prepareTheDensityAxis() {     
        xGraphLeft = fromHere;
        xAxis.forceLowScaleEndToBe(0.0);  
        xGraphRight = toThere;
        bigDelta = (xGraphRight - xGraphLeft) / NUMBER_OF_DXs;
        delta = bigDelta;
        xRange = xGraphRight - xGraphLeft;        
        yMin = 0.0; yMax = 1.025;
        yRange = yMax;
        // These constants control the rate of axis scale change when dragging
        deltaX = 0.005 * xRange; deltaY = 0.005 * yRange;   
    }
    
    public void setIntervalOfInterest(double startHere, double endHere)  {
        fromHere = startHere; toThere = endHere;
    }
    
    public double getInitialYMax() { return 1.025; }
   
    @Override
    public void doTheGraph() {      
        double power, dbl_daN;
        
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
        oneMean_Power_Model.restoreNullValues();
        //  Set initial display interval
        yStart = yAxis.getDisplayPosition(0.0);
        yStop = yAxis.getDisplayPosition(1.0);       
        
        for (int daN = 2; daN < 201; daN++) {
            dbl_daN = daN;
            oneMean_Power_Model.setSampleSize(daN);
            power = oneMean_Power_Model.calculatePower();         
            xStop = xAxis.getDisplayPosition(dbl_daN);
            yStop = yAxis.getDisplayPosition(power);            
            gc.setLineWidth(2);
            gc.setStroke(Color.BLUE); 
            gc.strokeOval(xStop - 1., yStop + 1., 2, 2);
        }     
        oneMean_Power_Model.restoreNullValues();
        
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
