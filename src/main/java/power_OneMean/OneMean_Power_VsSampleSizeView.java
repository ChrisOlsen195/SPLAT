/**************************************************
 *        OneMean_Power_VsSampleSizeView          *
 *                  05/24/26                      *
 *                    06:00                       *
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

public class OneMean_Power_VsSampleSizeView extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int sampleSize;
    
    double yMin, yMax, effectSize, dbl_SampleSize, dbl_daN, power;

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
        if (printTheStuff) {
            System.out.println("42 *** OneMean_Power_VsSampleSizeView, Constructing");
        }
        this.oneMean_Power_Model = oneMean_Power_Model;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        sampleSize = oneMean_Power_Model.getSampleSize();
        if (printTheStuff) {
            System.out.println("49 --- OneMean_Power_VsSampleSizeView, sampleSize = " + sampleSize);
        }
        dbl_SampleSize = sampleSize;        
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();                
        alpha = oneMean_Power_Model.getAlpha();
        effectSize = oneMean_Power_Model.getEffectSize();
        fromHere = 1.0; toThere = dbl_SampleSize;
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
    
    //public double getInitialYMax() { return 1.025; }
   
    @Override
    public void doTheGraph() {      
        
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
        // Get needed current values for restoration 
        // In the case of means, the StErrs are equal
        double forRestorationNullStErr = oneMean_Power_Model.getStErr_NullParam();
        double forRestorationAltStErr = oneMean_Power_Model.getStErr_AltParam(); 
        System.out.println("179 OneMean_PowerVsN, sampleSize = " + sampleSize);
        for (int daN = 2; daN < sampleSize; daN++) {
            dbl_daN = daN;
            oneMean_Power_Model.setSampleSize(daN);
            // Calculate changing stErrors
            double tempStErrNull = oneMean_Power_Model.getNullSigma()/ Math.sqrt(dbl_daN);
            double tempStErrAlt = tempStErrNull;
            oneMean_Power_Model.setStErr_NullParam(tempStErrNull);
            oneMean_Power_Model.setStErr_AltParam(tempStErrAlt);
            power = oneMean_Power_Model.calculatePower();       
        if (printTheStuff) {
            System.out.println("/n190 ... OneMean_Power_VsSampleSizeView, daN = " + daN);
            System.out.println("191 tempNull/AltStErr = " + tempStErrNull + " / " + tempStErrAlt);
            System.out.println("192 power = " + power);
        }
            xStop = xAxis.getDisplayPosition(dbl_daN);
            yStop = yAxis.getDisplayPosition(power);     
            gc.setLineWidth(2);
            gc.setStroke(Color.BLUE); 
            gc.strokeOval(xStop - 1., yStop + 1., 2, 2);
        }  
        oneMean_Power_Model.setStErr_NullParam(forRestorationNullStErr);
        oneMean_Power_Model.setStErr_AltParam(forRestorationAltStErr);
    }   //  end doTheGraph    
    
   public Pane getTheContainingPane() { return theContainingPane; }  
}
