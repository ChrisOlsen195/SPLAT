/**************************************************
 *          IndepProps_Power_VsAlphaView          *
 *                  01/16/25                      *
 *                    00:00                       *
 *************************************************/
package power_twoprops;

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

public class IndepProps_Power_VsAlphaView extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int n_1, n_2;

    double yMin, yMax, effectSize;

    //  FX 
    Pane theContainingPane;
    Text txtTitle_1, txtTitle_2;    
   
    // My classes
    IndepProps_Power_Model indepProops_Power_Model;

    public IndepProps_Power_VsAlphaView(IndepProps_Power_Model indepProps_Power_Model,
                         IndepProps_Power_Dashboard indepMeans_Power_Dashboard,
                         double placeHoriz, double placeVert,
                         double withThisWidth, double withThisHeight) {
        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff == true) {
            System.out.println("50 *** IndepProps_Power_VsAlphaView, Constructing");
        }
        this.indepProops_Power_Model = indepProps_Power_Model;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        indepProps_Power_Model.restoreNullValues();
        n_1 = indepProps_Power_Model.getSampleSize_1();
        n_2 = indepProps_Power_Model.getSampleSize_2();
        effectSize = indepProps_Power_Model.getEffectSize();
        alpha = indepProps_Power_Model.getAlpha();
        effectSize = indepProps_Power_Model.getEffectSize();
        fromHere = 0.000; toThere = 0.200;
        makeItHappen();
    }  
    
    private void makeItHappen() {     
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
        txtTitle_1 = new Text("Power vs. Alpha");
        txtTitle_2 = new Text (String.format("Sample size = %3d, Effect Size = %4.2f", n_1, effectSize));
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
        xAxis.forceLowScaleEndToBe(0.0); 
        xAxis.forceHighScaleEndToBe(0.200);
        xGraphRight = toThere;
        bigDelta = (xGraphRight - xGraphLeft) / NUMBER_OF_DXs;
        delta = bigDelta;
        //xMin = xMax = xGraphLeft;
        xRange = xGraphRight - xGraphLeft; 
        yMin = 0.0; yMax = 1.025;
        yRange = yMax;
        // These constants control the rate of axis scale change when dragging
        deltaX = 0.005 * xRange; deltaY = 0.005 * yRange;   
    }
    
    public void setIntervalOfInterest(double startHere, double endHere)  {
        fromHere = startHere; toThere = endHere;
    }
   
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

        // Set initial yValue, and get the power there
        xx0 = 0.200; 
        indepProops_Power_Model.setAlpha(xx0);
        power = indepProops_Power_Model.calculatePower();
        yy0 = power;

        for (double alphaIndex = 0.200; alphaIndex > 0.000; alphaIndex = alphaIndex - .001) {
            xx1 = alphaIndex;
            indepProops_Power_Model.setAlpha(alphaIndex); 
            power = indepProops_Power_Model.calculatePower();
            yy1 = power;            
            xStart = xAxis.getDisplayPosition(xx0); 
            yStart = yAxis.getDisplayPosition(yy0); 
            xStop = xAxis.getDisplayPosition(xx1);
            yStop = yAxis.getDisplayPosition(yy1);
            
            gc.setLineWidth(2);
            gc.setStroke(Color.BLUE);
            gc.strokeLine(xStart, yStart, xStop, yStop);            

            xx0 = xx1; yy0 = yy1;   //  Next start point for line segment
        }     

        indepProops_Power_Model.restoreNullValues();
        
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
    
    public Pane getTheContainingPane() {  return theContainingPane; }
}


