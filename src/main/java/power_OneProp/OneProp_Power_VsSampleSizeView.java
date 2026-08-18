/**************************************************
 *         OneProp_Power_VsSampleSizeView         *
 *                  05/24/26                      *
 *                    09:00                       *
 *************************************************/
package power_OneProp;

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

public class OneProp_Power_VsSampleSizeView extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int sampleSize;

    double yMin, yMax, effectSize, power, dbl_SampleSize, dbl_daN;

    //  FX 
    Pane theContainingPane;
    Text title1Text, title2Text;    
   
    // My classes
    OneProp_Power_Model oneProp_Power_Model;

    public OneProp_Power_VsSampleSizeView(OneProp_Power_Model oneProp_PowerModel,
                         OneProp_Power_Dashboard oneProp_Power_Dashboard,
                         double placeHoriz, double placeVert,
                         double withThisWidth, double withThisHeight) {
        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff) {
            System.out.println("43 *** OneProp_Power_VsSampleSizeView, Constructing");
        }
        this.oneProp_Power_Model = oneProp_PowerModel;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        sampleSize = oneProp_PowerModel.getSampleSize();
        if (printTheStuff) {
            System.out.println("50 --- OneProp_Power_VsSampleSizeView, sampleSize = " + sampleSize);
        }
        dbl_SampleSize = sampleSize;
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm(); 
        alpha = oneProp_PowerModel.getAlpha();
        effectSize = oneProp_PowerModel.getEffectSize();
        fromHere = 1.0; toThere = 1.25 * sampleSize;
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
        title1Text = new Text("Power vs. Sample size");
        title2Text = new Text (String.format("Effect size = %4.2f, Alpha = %3.2f", effectSize, alpha));
        title1Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        title2Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15));         
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
        //xMin = xMax = xGraphLeft;
        xRange = xGraphRight - xGraphLeft;        
        yMin = 0.0; yMax = 1.025;
        yRange = yMax;
        // These constants control the rate of axis scale change when dragging
        deltaX = 0.005 * xRange; deltaY = 0.005 * yRange;   
    }
    
    public double getInitialYMax() { return 1.025; }
   
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
        // Get needed current values for restoration 
        // In the case of means, the StErrs are equal
        double forRestorationNullStErr = oneProp_Power_Model.getStErr_NullParam();  // Not sure these are needed
        double forRestorationAltStErr = oneProp_Power_Model.getStErr_AltParam(); 
        for (int daN = 2; daN < sampleSize; daN++) {
            dbl_daN = daN;
            oneProp_Power_Model.setSampleSize(daN);
            // Get needed current values for restoration
            double tempNullProp = oneProp_Power_Model.getNullParam();
            double tempAltProp = oneProp_Power_Model.getAltParam();
            // Calculate changing stErrors
            double tempStErrNull = Math.sqrt(tempNullProp * (1.0 - tempNullProp) / dbl_daN);
            double tempStErrAlt = Math.sqrt(tempAltProp * (1.0 - tempAltProp) / dbl_daN); 
            oneProp_Power_Model.setStErr_NullParam(tempStErrNull);
            oneProp_Power_Model.setStErr_AltParam(tempStErrAlt);
            // Calculate power with the volative stErrs
            power = oneProp_Power_Model.calculatePower();
            if (printTheStuff) {
                System.out.println("/n192 ... OneProp_Power_VsSampleSizeView, daN = " + daN);
                System.out.println("193 tempNull/AltStErr = " + tempStErrNull + " / " + tempStErrAlt);
                System.out.println("194 power = " + power);
            }        
            xStop = xAxis.getDisplayPosition(dbl_daN);
            yStop = yAxis.getDisplayPosition(power);            
            gc.setLineWidth(2);
            gc.setStroke(Color.BLUE); 
            gc.strokeOval(xStop - 1., yStop + 1., 2, 2);
        }   
        oneProp_Power_Model.setStErr_NullParam(forRestorationNullStErr);    // // Not sure these are needed
        oneProp_Power_Model.setStErr_AltParam(forRestorationAltStErr);
    }   //  end doTheGraph    
    
   public Pane getTheContainingPane() { return theContainingPane; }   
}
